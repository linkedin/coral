/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.rel2presto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.core.Window;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.util.Util;

import com.linkedin.coral.com.google.common.collect.ImmutableList;

import static com.linkedin.coral.presto.rel2presto.Calcite2PrestoUDFConverter.*;


public class RelToPrestoConverter extends RelToSqlConverter {

  /**
   * Creates a RelToSqlConverter.
   */
  public RelToPrestoConverter() {
    super(PrestoSqlDialect.INSTANCE);
  }

  /**
   * Convert relational algebra to Presto SQL
   * @param relNode calcite relational algebra representation of SQL
   * @return SQL string
   */
  public String convert(RelNode relNode) {
    RelNode rel = convertRel(relNode);
    return convertToSqlNode(rel).accept(new PrestoSqlRewriter()).toSqlString(PrestoSqlDialect.INSTANCE).toString();
  }

  /**
   * Convert input relational algebra to calcite SqlNode
   * @param relNode relation algebra
   * @return calcite SqlNode representation for input
   */
  public SqlNode convertToSqlNode(RelNode relNode) {
    return visitChild(0, relNode).asStatement();
  }

  /**
   * @see #dispatch(RelNode)
   * @param window Relnode representing window clause
   * @return result of translation to sql
   */
  public Result visit(Window window) {
    return null;
  }

  @Override
  public Result visit(Project e) {
    e.getVariablesSet();
    Result x = visitChild(0, e.getInput());
    parseCorrelTable(e, x);

    final Builder builder = x.builder(e, Clause.SELECT);
    final List<SqlNode> selectList = new ArrayList<>();
    for (RexNode ref : e.getChildExps()) {
      SqlNode sqlExpr = builder.context.toSql(null, ref);
      addSelect(selectList, sqlExpr, e.getRowType());
    }

    builder.setSelect(new SqlNodeList(selectList, POS));
    return builder.result();
  }

  @Override
  public void addSelect(List<SqlNode> selectList, SqlNode node, RelDataType rowType) {
    // APA-7366 Override this method from parent class RelToSqlConverter to always add "as"
    // when accessing nested struct.
    // In parent class "as" is skipped for "select a.b as b", here we will keep the "a.b as b"
    SqlNode selectNode = node;
    final String name = rowType.getFieldNames().get(selectList.size());
    final String alias = SqlValidatorUtil.getAlias(selectNode, -1);
    final String lowerName = name.toLowerCase(Locale.ROOT);
    final boolean nestedFieldAccess =
        selectNode instanceof SqlIdentifier && ((SqlIdentifier) selectNode).names.size() > 1;
    if (lowerName.startsWith("expr$")) {
      ordinalMap.put(lowerName, selectNode);
    } else if (alias == null || !alias.equals(name) || nestedFieldAccess) {
      selectNode = as(selectNode, name);
    }
    selectList.add(selectNode);
  }

  private SqlCall as(SqlNode e, String alias) {
    return SqlStdOperatorTable.AS.createCall(POS, e, new SqlIdentifier(alias, POS));
  }

  public Result visit(Uncollect e) {
    if (!isPrestoSupportedUnnest(e)) {
      throw new UnsupportedOperationException("PrestoSQL does not allow unnest a result of a queries");
    }
    // Remove SELECT in  UNNEST(SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) AS <alias>(<columnList>) instead.
    final Result x = visitChild(0, e.getInput());

    // Build <unnestColumns>
    final List<SqlNode> unnestOperands = new ArrayList<>();
    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(x.qualifiedContext().toSql(null, unnestCol));
    }

    // Build UNNEST(<unnestColumns>)
    final SqlNode unnestNode = SqlStdOperatorTable.UNNEST.createCall(POS, unnestOperands);

    // Build UNNEST(<unnestColumns>) AS <alias>(<columnList>)
    final List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), unnestNode, x.neededAlias);
    final SqlNode asNode = SqlStdOperatorTable.AS.createCall(POS, asOperands);

    return result(asNode, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * This overridden function makes sure that the basetable names in the output SQL
   * will be in the form of "dbname.tablename" instead of "catalogname.dbname.tablename"
   *
   * Presto can have configurable catalog names. In that case the HiveToRelConverter's default "hive" catalog will
   * cause failures:  https://github.com/prestosql/presto/issues/5785. If catalogname is not prepended, presto uses
   * the catalog name of the view being translated. If for example a view "hive2.db.view" whose coral-presto
   * translation returns "SELECT * FROM db.table" will be evaluated as "SELECT * FROM hive2.db.table" in presto.
   *
   * Example:
   *  hive.default.foo_bar -&gt; default.foo_bar
   */
  @Override
  public Result visit(TableScan e) {
    List<String> qualifiedName = e.getTable().getQualifiedName();
    if (qualifiedName.size() > 2) {
      qualifiedName = qualifiedName.subList(qualifiedName.size() - 2, qualifiedName.size()); // take last two entries
    }
    final SqlIdentifier identifier = new SqlIdentifier(qualifiedName, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Checks whether we do unnest in Presto. In this case the plan should have this structure:
   *    Uncollect
   *      LogicalProject(<List of projections>)
   *        LogicalValues(tuples=[[{ 0 }]])
   * Then when producing presto SQL, we just generate UNNEST(<List of projections>). For other cases
   * Presto does not support UNNEST a query: UNNEST(SELECT ..).
   *  TODO: verify for HIVE parser if we get a Calcite plan for lateral view explode() in this same structure
   */
  private boolean isPrestoSupportedUnnest(Uncollect uncollect) {
    if (!(uncollect.getInput() instanceof Project)
        || !(((Project) uncollect.getInput()).getInput() instanceof Values)) {
      return false;
    }
    Values values = (Values) ((Project) uncollect.getInput()).getInput();
    // Values should have only single row with a single column with value 0.
    if (values.getTuples().size() == 1 && values.getTuples().get(0).size() == 1) {
      RexLiteral val = values.getTuples().get(0).get(0);
      return val.getValue().equals(new BigDecimal(0));
    }
    return false;
  }

  public Result visit(Correlate e) {
    final Result leftResult = visitChild(0, e.getLeft()).resetAlias(e.getCorrelVariable(), e.getLeft().getRowType());
    parseCorrelTable(e, leftResult);
    final Result rightResult = visitChild(1, e.getRight());
    SqlNode rightLateral = rightResult.node;
    rightLateral = SqlStdOperatorTable.LATERAL.createCall(POS, rightLateral);
    if (rightLateral.getKind() != SqlKind.AS) {
      rightLateral =
          SqlStdOperatorTable.AS.createCall(POS, rightLateral, new SqlIdentifier(rightResult.neededAlias, POS));
    }

    final SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS),
        JoinType.CROSS.symbol(POS), rightLateral, JoinConditionType.NONE.symbol(POS), null);
    return result(join, leftResult, rightResult);
  }

  @Override
  public Context aliasContext(Map<String, RelDataType> aliases, boolean qualified) {
    // easier to keep inner class for accessing 'aliases' and 'qualified' variables as closure
    return new AliasContext(PrestoSqlDialect.INSTANCE, aliases, qualified) {
      @Override
      public SqlNode field(int ordinal) {
        for (Map.Entry<String, RelDataType> alias : aliases.entrySet()) {
          final List<RelDataTypeField> fields = alias.getValue().getFieldList();
          if (ordinal < fields.size()) {
            RelDataTypeField field = fields.get(ordinal);
            final SqlNode mappedSqlNode = ordinalMap.get(field.getName().toLowerCase(Locale.ROOT));
            if (mappedSqlNode != null) {
              return ensureAliasedNode(alias.getKey(), mappedSqlNode);
            }
            return new SqlIdentifier(
                !qualified ? ImmutableList.of(field.getName()) : ImmutableList.of(alias.getKey(), field.getName()),
                POS);
          }
          ordinal -= fields.size();
        }
        throw new AssertionError("field ordinal " + ordinal + " out of range " + aliases);
      }

      protected SqlNode ensureAliasedNode(String alias, SqlNode id) {
        if (!(id instanceof SqlIdentifier)) {
          return id;
        }
        ImmutableList<String> names = ((SqlIdentifier) id).names;
        if (names.size() > 1) {
          return id;
        }
        return new SqlIdentifier(ImmutableList.of(alias, Util.last(names)), POS);
      }

      private RexNode stripCastFromString(RexNode node) {
        // DO NOT strip
        return node;
      }
    };
  }
}
