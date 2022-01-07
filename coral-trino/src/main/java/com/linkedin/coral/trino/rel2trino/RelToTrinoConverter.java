/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.*;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.util.Util;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.rel.HiveUncollect;
import com.linkedin.coral.trino.rel2trino.functions.TrinoArrayTransformFunction;

import static com.google.common.base.Preconditions.*;
import static com.linkedin.coral.trino.rel2trino.Calcite2TrinoUDFConverter.convertRel;
import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


public class RelToTrinoConverter extends RelToSqlConverter {

  /**
   * We introduce this configuration for LinkedIn's internal use since our Trino is extending the following legacy/internal supports:
   * (1) Unnest array of struct, refer to https://github.com/linkedin/coral/pull/93#issuecomment-912698600 for more information.
   *     If the value of key {@link CoralTrinoConfigKeys#SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT} is set to true, we don't add extra ROW
   *     wrapping in {@link RelToTrinoConverter#visit(Uncollect)}
   * (2) Some internally registered UDFs which should not be converted, like `to_date`.
   *     If the value of key {@link CoralTrinoConfigKeys#AVOID_TRANSFORM_TO_DATE_UDF} is set to true, we don't transform `to_date` UDF
   *     in {@link com.linkedin.coral.trino.rel2trino.Calcite2TrinoUDFConverter.TrinoRexConverter#visitCall(RexCall)}
   * (3) We need to adjust the return type for some functions using cast, since the converted Trino function's return type is not
   *     aligned with the Hive function's return type. For example, if the value of key {@link CoralTrinoConfigKeys#CAST_DATEADD_TO_STRING}
   *     is set to true, we would cast the converted RexCall to `varchar` type (date_add(xxx) -> cast(date_add(xxx) as varchar))
   * For uses outside LinkedIn, just ignore this configuration.
   */
  private Map<String, Boolean> configs = new HashMap<>();

  /**
   * Creates a RelToTrinoConverter.
   */
  public RelToTrinoConverter() {
    super(TrinoSqlDialect.INSTANCE);
  }

  public RelToTrinoConverter(Map<String, Boolean> configs) {
    super(TrinoSqlDialect.INSTANCE);
    checkNotNull(configs);
    this.configs = configs;
  }

  /**
   * Convert relational algebra to Trino's SQL
   * @param relNode calcite relational algebra representation of SQL
   * @return SQL string
   */
  public String convert(RelNode relNode) {
    RelNode rel = convertRel(relNode, configs);
    return convertToSqlNode(rel).accept(new TrinoSqlRewriter()).toSqlString(TrinoSqlDialect.INSTANCE).toString();
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
    // Override this method from parent class RelToSqlConverter to always add "as"
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
    if (!isTrinoSupportedUnnest(e)) {
      throw new UnsupportedOperationException("Trino does not allow unnest a result of a queries");
    }
    // Remove SELECT in  UNNEST(SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) AS <alias>(<columnList>) instead.
    final Result x = visitChild(0, e.getInput());

    // Build <unnestColumns>
    final List<SqlNode> unnestOperands = new ArrayList<>();
    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      if (!configs.getOrDefault(SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT, false) && e instanceof HiveUncollect
          && unnestCol.getType().getSqlTypeName().equals(SqlTypeName.ARRAY)
          && unnestCol.getType().getComponentType().getSqlTypeName().equals(SqlTypeName.ROW)) {

        // wrapper Record type with single column.
        // It is needed as Trino follows SQL standard when unnesting
        // ARRAY of ROWs, exposing each field in a ROW as separate column. This is not in-line with what
        // Hive's LATERAL VIEW EXPLODE does, exposing whole ROW (struct) as a single column.
        // Adding extra artificial wrapping ROW with single field simulates Hive semantics in Trino.
        //
        // Example transformation:
        //
        // Given table with an array of structs column:
        //   CREATE TABLE example_table(id INTEGER, arr array<struct<sa: int, sb: string>>)
        // We rewrite view defined as:
        //  SELECT id, arr_exp FROM example_table LATERAL VIEW EXPLODE(arr) t AS arr_exp
        // To:
        //  SELECT "$cor0".id AS id, t1.arr_exp AS arr_exp
        //    FROM example_table AS "$cor0"
        //    CROSS JOIN LATERAL (SELECT arr_exp
        //    FROM UNNEST(TRANSFORM("$cor0".arr, x -> ROW(x))) AS t0 (arr_exp)) AS t1
        //
        // The crucial part in above transformation is call to TRANSFORM with lambda which adds extra layer of
        // ROW wrapping.

        RelRecordType transformDataType = new RelRecordType(
            ImmutableList.of(new RelDataTypeFieldImpl("wrapper_field", 0, unnestCol.getType().getComponentType())));

        // wrap unnested field to type defined above using transform(field, x -> ROW(x))
        TrinoArrayTransformFunction tranformFunction = new TrinoArrayTransformFunction(transformDataType);
        SqlNode fieldRef = x.qualifiedContext().toSql(null, unnestCol);
        String fieldRefString = fieldRef.toSqlString(TrinoSqlDialect.INSTANCE).getSql();
        SqlCharStringLiteral transformArgsLiteral =
            SqlLiteral.createCharString(String.format("%s, x -> ROW(x)", fieldRefString), POS);

        unnestOperands.add(tranformFunction.createCall(POS, transformArgsLiteral));
      } else {
        unnestOperands.add(x.qualifiedContext().toSql(null, unnestCol));
      }
    }

    // Build UNNEST(<unnestColumns>) or UNNEST(<unnestColumns>) WITH ORDINALITY
    final SqlNode unnestNode =
        (e.withOrdinality ? SqlStdOperatorTable.UNNEST_WITH_ORDINALITY : SqlStdOperatorTable.UNNEST).createCall(POS,
            unnestOperands);

    // Build UNNEST(<unnestColumns>) (WITH ORDINALITY) AS <alias>(<columnList>)
    final List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), unnestNode, x.neededAlias);
    final SqlNode asNode = SqlStdOperatorTable.AS.createCall(POS, asOperands);

    // Reuse the same x.neededAlias since that's already unique by directly calling "new Result(...)"
    // instead of calling super.result(...), which will generate a new table alias and cause an extra
    // "AS" to be added to the generated SQL statement and make it invalid.
    return new Result(asNode, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(x.neededAlias, e.getRowType()));
  }

  /**
   * This overridden function makes sure that the basetable names in the output SQL
   * will be in the form of "dbname.tablename" instead of "catalogname.dbname.tablename"
   *
   * Trino can have configurable catalog names. In that case the HiveToRelConverter's default "hive" catalog will
   * cause failures:  https://github.com/trinodb/trino/issues/5785. If catalogname is not prepended, Trino uses
   * the catalog name of the view being translated. If for example a view "hive2.db.view" whose coral-trino
   * translation returns "SELECT * FROM db.table" will be evaluated as "SELECT * FROM hive2.db.table" in Trino.
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
   * Checks whether we do unnest in Trino. In this case the plan should have this structure:
   *    Uncollect
   *      LogicalProject(<List of projections>)
   *        LogicalValues(tuples=[[{ 0 }]])
   * Then when producing Trino SQL, we just generate UNNEST(<List of projections>). For other cases
   * Trino does not support UNNEST a query: UNNEST(SELECT ..).
   *  TODO: verify for HIVE parser if we get a Calcite plan for lateral view explode() in this same structure
   */
  private boolean isTrinoSupportedUnnest(Uncollect uncollect) {
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
    if (rightLateral.getKind() != SqlKind.AS) {
      // LATERAL is only needed in Trino if it's not an AS node.
      // For example, "FROM t0 CROSS JOIN UNNEST(yyy) AS t1(col1, col2)" is valid Trino SQL
      // without the need of LATERAL keywords.
      rightLateral = SqlStdOperatorTable.LATERAL.createCall(POS, rightLateral);
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
    return new AliasContext(TrinoSqlDialect.INSTANCE, aliases, qualified) {
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
