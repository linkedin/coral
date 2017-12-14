package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.core.Window;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


public class RelToPrestoConverter extends RelToSqlConverter {

  public static final PrestoSqlDialect PRESTO_DIALECT = new PrestoSqlDialect();

  /**
   * Creates a RelToSqlConverter.
   */
  public RelToPrestoConverter() {
    super(PRESTO_DIALECT);
  }

  /**
   * Convert relational algebra to Presto SQL
   * @param relNode calcite relational algebra representation of SQL
   * @return SQL string
   */
  public String convert(RelNode relNode) {
    return convertToSqlNode(relNode)
        .toSqlString(PRESTO_DIALECT)
        .toString();
  }

  /**
   * Convert input relational algebra to calcite SqlNode
   * @param relNode relation algebra
   * @return calcite SqlNode representation for input
   */
  public SqlNode convertToSqlNode(RelNode relNode) {
    return visitChild(0, relNode)
        .asStatement();
  }

  /**
   * @see #dispatch(RelNode)
   * @param window Relnode representing window clause
   * @return result of translation to sql
   */
  public Result visit(Window window) {
    return null;
  }

  public Result visit(Uncollect e) {
    if (!isPrestoSupportedUnnest(e)) {
      throw new UnsupportedOperationException("PrestoSQL does not allow unnest a result of a queries");
    }
    // Remove SELECT in  UNNEST(SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) AS <alias>(<columnList>) instead.
    final Result x = visitChild(0, e.getInput());
    final Builder builder = x.builder(e, Clause.SELECT);

    // Buil <unnestColumns>
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
    final Result leftResult = visitChild(0, e.getLeft());
    final SqlNode leftLateral = SqlStdOperatorTable.AS.createCall(POS, leftResult.node,
        new SqlIdentifier(e.getCorrelVariable(), POS));

    final Result rightResult = visitChild(1, e.getRight());
    SqlNode rightLateral = rightResult.node;
    if (rightLateral.getKind() != SqlKind.AS) {
      rightLateral = SqlStdOperatorTable.AS.createCall(POS, rightResult.node,
          new SqlIdentifier(rightResult.neededAlias, POS));
    }

    final SqlNode join =
        new SqlJoin(POS,
            leftLateral,
            SqlLiteral.createBoolean(false, POS),
            JoinType.CROSS.symbol(POS),
            rightLateral,
            JoinConditionType.NONE.symbol(POS),
            null);
    return result(join, leftResult, rightResult);
  }
}
