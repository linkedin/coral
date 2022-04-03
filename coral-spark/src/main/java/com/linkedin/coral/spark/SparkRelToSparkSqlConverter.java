/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.logical.LogicalTableFunctionScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlMultisetValueConstructor;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.HiveExplodeOperator;
import com.linkedin.coral.hive.hive2rel.functions.HivePosExplodeOperator;
import com.linkedin.coral.spark.dialect.SparkSqlDialect;
import com.linkedin.coral.spark.functions.SqlLateralJoin;
import com.linkedin.coral.spark.functions.SqlLateralViewAsOperator;


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
   * "org.apache.spark.sql.catalyst.parser.ParseException: mismatched input '.' expecting &lt;EOF&gt;" Error.
   *
   * For Example:
   *  hive.default.foo_bar -&gt; default.foo_bar
   */
  @Override
  public Result visit(TableScan e) {
    checkQualifiedName(e.getTable().getQualifiedName());
    List<String> tableNameWithoutCatalog = e.getTable().getQualifiedName().subList(1, 3);
    final SqlIdentifier identifier = new SqlIdentifier(tableNameWithoutCatalog, SqlParserPos.ZERO);
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

    SqlNode rightNode = rightResult.node;

    // If rightResult is not SqlASOperator, let's create a SqlLateralViewAsOperator to wrap it around
    // This is necessary to ensure that the alias is applied.
    if (rightNode.getKind() != SqlKind.AS) {
      final List<SqlNode> asOperands =
          createAsFullOperands(e.getRight().getRowType(), rightResult.node, rightResult.neededAlias);
      // Same as AS operator but instead of "AS TableRef(ColRef1, ColRef2)" produces "TableRef AS ColRef1, ColRef2"
      rightNode = SqlLateralViewAsOperator.instance.createCall(POS, asOperands);
    }

    // A new type of join is used, because the unparsing of this join is different from already existing join
    SqlLateralJoin join =
        new SqlLateralJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS), JoinType.COMMA.symbol(POS),
            rightNode, JoinConditionType.NONE.symbol(POS), null, isCorrelateRightChildOuter(rightResult.node));
    return result(join, leftResult, rightResult);
  }

  @Override
  public Result visit(Join e) {
    Result result = super.visit(e);

    SqlJoin joinNode = (SqlJoin) (result.node);
    SqlNode rightNode = joinNode.getRight();

    // Is this JOIN actually a "LATERAL VIEW EXPLODE(xxx)"?
    // If so, let's replace the JOIN node with SqlLateralJoin node, which unparses as "LATERAL VIEW".
    if (rightNode instanceof SqlBasicCall
        && ((SqlBasicCall) rightNode).getOperator() instanceof SqlLateralViewAsOperator) {
      SqlLateralJoin join = new SqlLateralJoin(POS, joinNode.getLeft(), SqlLiteral.createBoolean(false, POS),
          JoinType.COMMA.symbol(POS), joinNode.getRight(), JoinConditionType.NONE.symbol(POS), joinNode.getCondition(),
          isCorrelateRightChildOuter(joinNode.getRight()));
      result = new Result(join, Expressions.list(Clause.FROM), null, null, result.aliases);
    }
    return result;
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

    // Convert UNNEST to EXPLODE or POSEXPLODE function
    final SqlNode unnestNode = (e.withOrdinality ? HivePosExplodeOperator.POS_EXPLODE : HiveExplodeOperator.EXPLODE)
        .createCall(POS, unnestOperands.toArray(new SqlNode[0]));

    // Build LATERAL VIEW EXPLODE(<unnestColumns>) <alias> AS (<columnList>)
    // Without the AS node, in cases like the following valid Hive queries:
    //   FROM t1 LATERAL VIEW EXPLODE(t1.c) AS t2
    // this function would return  (SqlBasicCall with Operator HiveExplodeOperator), which will cause the
    // parent function visit(Correlate)'s call of SqlImplementor.wrapSelect's assert to fail since
    // SqlOperator with HiveExplodeOperator (or its parent SqlUnnestOperator) is not allowed (only
    // SqlSetOperator, SqlStdOperatorTable.AS, and SqlStdOperatorTable.VALUES are allowed).
    /** See {@link org.apache.calcite.rel.rel2sql.SqlImplementor#wrapSelect(SqlNode)}*/

    List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), unnestNode, x.neededAlias);

    int asOperandsSize = asOperands.size();

    // For POSEXPLODE function, we need to change the order of 2 alias back to be aligned with Spark syntax since
    // we have changed the order before in ParseTreeBuilder#visitLateralViewExplode for calcite validation
    // Otherwise, `LATERAL VIEW POSEXPLODE(ARRAY('a', 'b')) arr AS pos, alias` will be translated to
    // `LATERAL VIEW POSEXPLODE(ARRAY('a', 'b')) arr AS alias, pos`
    if (e.withOrdinality) {
      asOperands = ImmutableList.<SqlNode> builder().addAll(asOperands.subList(0, asOperandsSize - 2))
          .add(asOperands.get(asOperandsSize - 1)).add(asOperands.get(asOperandsSize - 2)).build();
    }

    final SqlNode asNode = SqlLateralViewAsOperator.instance.createCall(POS, asOperands);

    // Reuse the same x.neededAlias since that's already unique by directly calling "new Result(...)"
    // instead of calling super.result(...), which will generate a new table alias and cause an extra
    // "AS" to be added to the generated SQL statement and make it invalid.
    return new Result(asNode, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(x.neededAlias, e.getRowType()));
  }

  /**
   * CORAL represents table function as an LogicalTableFunctionScan RelNode
   *
   * In this function we override default SQL conversion for LogicalTableFunctionScan and
   * handle correctly converting it to a table function by resetting table name
   *
   * For Example:
   *  default_foo_lateral_udtf_CountOfRow($cor0.a)
   *            is converted to
   *  `default_foo_lateral_udtf_CountOfRow`(`complex`.`a`)
   */
  public Result visit(LogicalTableFunctionScan e) {
    RexCall call = (RexCall) e.getCall();
    SqlOperator functionOperator = call.getOperator();
    final List<SqlNode> functionOperands = new ArrayList<>();
    for (RexNode rexOperand : call.getOperands()) {
      RexFieldAccess rexFieldAccess = (RexFieldAccess) rexOperand;
      RexCorrelVariable rexCorrelVariable = (RexCorrelVariable) rexFieldAccess.getReferenceExpr();
      SqlNode sqlNodeOperand = correlTableMap.get(rexCorrelVariable.id).toSql(null, rexOperand);
      functionOperands.add(sqlNodeOperand);
    }
    SqlCall functionOperatorCall = functionOperator.createCall(POS, functionOperands.toArray(new SqlNode[0]));
    return result(functionOperatorCall, ImmutableList.of(Clause.FROM), e, null);
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
    if (rightChild instanceof SqlBasicCall && rightChild.getKind() == SqlKind.AS) {
      SqlBasicCall rightCall = (SqlBasicCall) rightChild;
      SqlNode unnestNode = getOrDefault(rightCall.getOperandList(), 0, null);
      if (unnestNode instanceof SqlBasicCall && unnestNode.getKind() == SqlKind.UNNEST) {
        SqlBasicCall unnestCall = (SqlBasicCall) unnestNode;
        SqlNode ifNode = getOrDefault(unnestCall.getOperandList(), 0, null);
        if (ifNode instanceof SqlBasicCall) {
          SqlBasicCall ifCall = (SqlBasicCall) ifNode;
          if (ifCall.getOperator().getName().equals("if") && ifCall.operandCount() == 3) {
            SqlNode arrayOrMapNode = getOrDefault(ifCall.getOperandList(), 2, null);
            if (arrayOrMapNode instanceof SqlBasicCall) {
              SqlBasicCall arrayOrMapCall = (SqlBasicCall) arrayOrMapNode;
              if (arrayOrMapCall.getOperator() instanceof SqlMultisetValueConstructor
                  && arrayOrMapCall.getOperandList().get(0) instanceof SqlLiteral) {
                SqlLiteral sqlLiteral = (SqlLiteral) arrayOrMapCall.getOperandList().get(0);
                return sqlLiteral.getTypeName().toString().equals("NULL");
              }
            }
          }
        }
      }
    }
    return false;
  }

  private static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
    return list.size() > index ? list.get(index) : defaultValue;
  }

  private void checkQualifiedName(List<String> qualifiedName) {
    if (qualifiedName.size() != 3) {
      throw new RuntimeException("SparkRelToSparkSqlConverter Error: Qualified name has incorrect number of elements");
    }
  }

}
