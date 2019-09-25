package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.core.Window;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexInputRef;
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
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.util.Util;

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
    // base class method will return x if isStar(). That causes problems if any of the views
    // alias column names - returning x will make it pass through to the base table and the aliasing
    // information is lost which causes problems.
    // Removing isStar() check caused regression with lateral views. Removing isStar() will
    // create SELECT x from UNNEST(..). Presto does not like SELECT around UNNEST. Hence, we return
    // 'x' for unnest() clauses without adding select around it.
    if (isUnnestAll(e)) {
      return x;
    }

    final Builder builder =
        x.builder(e, Clause.SELECT);
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
    return SqlStdOperatorTable.AS.createCall(POS, e,
        new SqlIdentifier(alias, POS));
  }

  private boolean isUnnestAll(Project project) {
    final List<RexNode> projExps = project.getChildExps();

    if (projExps.size() != project.getInput().getRowType().getFieldCount()
        || !(project.getInput() instanceof Uncollect)) {
      return false;
    }

    for (int i = 0; i < projExps.size(); i++) {
      if (!(projExps.get(i) instanceof RexInputRef)
        || ((RexInputRef) projExps.get(i)).getIndex() != i) {
        return false;
      }
    }
    return true;
  }

  public Result visit(Uncollect e) {
    if (!isPrestoSupportedUnnest(e)) {
      throw new UnsupportedOperationException("PrestoSQL does not allow unnest a result of a queries");
    }
    // Remove SELECT in  UNNEST(SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) AS <alias>(<columnList>) instead.
    final Result x = visitChild(0, e.getInput());
    final Builder builder = x.builder(e, Clause.SELECT);

    // Build <unnestColumns>
    final List<SqlNode> unnestOperands = new ArrayList<>();
    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(builder.context.toSql(null, unnestCol));
    }

    // Build UNNEST(<unnestColumns>)
    final SqlNode unnestNode = SqlStdOperatorTable.UNNEST.createCall(POS, unnestOperands);

    // Build UNNEST(<unnestColumns>) AS <alias>(<columnList>)
    final List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), unnestNode, x.neededAlias);
    final SqlNode asNode = SqlStdOperatorTable.AS.createCall(POS, asOperands);

    return result(asNode, ImmutableList.of(Clause.FROM), e, null);
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
    if (rightLateral.getKind() != SqlKind.AS) {
      rightLateral =
          SqlStdOperatorTable.AS.createCall(POS, rightLateral, new SqlIdentifier(rightResult.neededAlias, POS));
    }

    final SqlNode join =
        new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS), JoinType.CROSS.symbol(POS),
            rightLateral, JoinConditionType.NONE.symbol(POS), null);
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
