package com.linkedin.coral.spark;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.HiveExplodeOperator;
import com.linkedin.coral.spark.dialect.SparkSqlDialect;
import com.linkedin.coral.spark.functions.SqlLateralJoin;
import com.linkedin.coral.spark.functions.SqlLateralViewAsOperator;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlMultisetValueConstructor;
import org.apache.calcite.sql.parser.SqlParserPos;

/**
 * This class converts Spark RelNode to Spark SQL
 * and is used by CoralSpark's constructSparkSQL() method.
 *
 * This class handles
 *  - Converting Correlate to Lateral Views
 *  - Converting base tables names to catalogname.dbname.tablename
 *  - Converting Unnest to Explode
 *
 */
public class SparkRelToSparkSqlConverter extends RelToSqlConverter {
  /**
   * Creates a SparkRelToSparkSqlConverter.
   *
   * This class converts a Spark RelNode to Spark understandable Hive SQL
   * and is used by CoralSpark's constructSparkSQL() method.
   *
   * One functionality that is overridden is while reading table and
   * changing basetable names from "catalogname.dbname.tablename" to "dbname.tablename".
   *
   */
  SparkRelToSparkSqlConverter() {
    super(SparkSqlDialect.INSTANCE);
  }


  /**
   * This overridden function makes sure that the basetable names in the output SQL
   * will be in the form of "dbname.tablename" instead of "catalogname.dbname.tablename"
   *
   * This is necessary to handle SparkSession.sql() inability to handle table names in
   * the form of 'catalogname.dbname.tablename'. This is because Spark SQL parser doesn't support it and throws
   * "org.apache.spark.sql.catalyst.parser.ParseException: mismatched input '.' expecting <EOF>" Error.
   *
   * For Example:
   *  hive.default.foo_bar -> default.foo_bar
   */
  @Override
  public Result visit(TableScan e) {
    checkQualifiedName(e.getTable().getQualifiedName());
    List<String> tableNameWithoutCatalog = e.getTable().getQualifiedName().subList(1, 3);
    final SqlIdentifier identifier =
        new SqlIdentifier(tableNameWithoutCatalog, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Correlate is Lateral View in Calcite's RelNode world.
   * Correlate is basically a join where right node is re-executed for every left node.
   *
   * This function overrides the default behavior of Calcite's SqlConverter
   * Example:
   *  SELECT $cor0.a, $cor0.ccol
   *       FROM default.complex $cor0, LATERAL(EXPLODE($cor0.c)) t1
   *                  To
   *  SELECT complex.a, t1.ccol
   *        FROM default.complex LATERAL VIEW EXPLODE(complex.c) t1 AS ccol
   */
  @Override
  public Result visit(Correlate e) {
    final Result leftResult = visitChild(0, e.getLeft());
    // Add context specifying correlationId has same context as its left child
    correlTableMap.put(e.getCorrelationId(), leftResult.qualifiedContext());
    final Result rightResult = visitChild(1, e.getRight());
    final List<SqlNode> asOperands = createAsFullOperands(e.getRight().getRowType(), rightResult.node, rightResult.neededAlias);

    // Same as AS operator but instead of "AS TableRef(ColRef1, ColRef2)" produces "TableRef AS ColRef1, ColRef2"
    SqlNode rightLateral = SqlLateralViewAsOperator.instance.createCall(POS, asOperands);

    // A new type of join is used, because the unparsing of this join is different from already existing join
    SqlLateralJoin join =
        new SqlLateralJoin(POS,
            leftResult.asFrom(),
            SqlLiteral.createBoolean(false, POS),
            JoinType.COMMA.symbol(POS),
            rightLateral,
            JoinConditionType.NONE.symbol(POS),
            null,
            isCorrelateRightChildOuter(rightResult.node));
    return result(join, leftResult, rightResult);
  }

  /**
   * CORAL represents explode function as an Uncollect RelNode,
   * which takes Project RelNode as input which is in the form of
   *        SELECT `complex`.`c` as`ccol` FROM (VALUES  (0))
   *
   * In this function we override default SQL conversion for uncollect and
   * handle correctly converting it to an explode function
   *
   * For Example:
   *  SELECT `complex`.`c` as`ccol` FROM (VALUES  (0))
   *            is converted to
   *  EXPLODE(`complex`.`c`)
   */
  @Override
  public Result visit(Uncollect e) {
    // Remove SELECT in  UNNEST(SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) AS <alias>(<columnList>) instead.
    final Result x = visitChild(0, e.getInput());

    // Build <unnestColumns>
    final List<SqlNode> unnestOperands = new ArrayList<>();
    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(x.qualifiedContext().toSql(null, unnestCol));
    }

    // Convert UNNEST to EXPLODE function
    final SqlNode unnestNode = HiveExplodeOperator.EXPLODE.createCall(POS,
        unnestOperands.toArray(new SqlNode[0]));

    return result(unnestNode, ImmutableList.of(Clause.FROM), e, null);
  }


  /**
   * Calcite's RelNode doesn't support 'OUTER' lateral views, Source: https://calcite.apache.org/docs/reference.html
   * The way Coral deals with this is by creating an IF function which will simulate an outer lateral view.
   *
   * For Example:
   *  LATERAL VIEW OUTER explode(complex.c)
   *          will be translated to
   *  LATERAL VIEW explode(if(complex.c IS NOT NULL AND size(complex.c) > 0, complex.c, ARRAY (NULL)))
   *
   * Spark needs an explicit 'OUTER' keyword for it to consider empty arrays,
   * therefore this function helps in finding out whether OUTER keyword is needed.
   *
   * This functions checks if a SqlNode
   *    - has 'if' child
   *    - has ARRAY(NULL) as the else result
   */
  private boolean isCorrelateRightChildOuter(SqlNode rightChild) {
    if (rightChild instanceof SqlBasicCall) {
      List<SqlNode> operandList = ((SqlBasicCall) rightChild).getOperandList();
      if (operandList.get(0) instanceof SqlBasicCall) {
        SqlBasicCall ifNode = (SqlBasicCall) operandList.get(0);
        if (ifNode.getOperator().getName().equals("if") && ifNode.operandCount() == 3) {
          SqlBasicCall arrayNode = (SqlBasicCall) ifNode.getOperandList().get(2);
          if (arrayNode.getOperator() instanceof SqlMultisetValueConstructor && arrayNode.getOperandList().get(0) instanceof SqlLiteral) {
            SqlLiteral sqlLiteral = (SqlLiteral) arrayNode.getOperandList().get(0);
            return sqlLiteral.getTypeName().toString().equals("NULL");
          }
        }
      }
    }
    return false;
  }

  private void checkQualifiedName(List<String> qualifiedName) {
    if (qualifiedName.size() != 3) {
      throw new RuntimeException("SparkRelToSparkSqlConverter Error: Qualified name has incorrect number of elements");
    }
  }

}
