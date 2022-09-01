/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.BiRel;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.logical.LogicalTableFunctionScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexCorrelVariable;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLateralOperator;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.functions.CoralSqlUnnestOperator;


/**
 * This class converts a Coral intermediate representation, RelNode, to
 * the original query's semantically equivalent but alternate representation: Coral SqlNode,
 * supplemented with additional details, such as aliases required and clause association information.
 */
public class CoralRelToSqlNodeConverter extends RelToSqlConverter {

  public static final SqlDialect INSTANCE = returnInstance();

  /**
   * Creates a CoralRelToSqlNodeConverter.
   */
  public CoralRelToSqlNodeConverter() {
    super(INSTANCE);
  }

  private static SqlDialect returnInstance() {
    SqlDialect.Context context = SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(SqlDialect.DatabaseProduct.HIVE)
        .withNullCollation(NullCollation.HIGH);

    return new SqlDialect(context);
  }

  /**
   * TableScan RelNode represents a relational operator that returns the contents of a table.
   * This overridden implementation removes the catalog name from the table namespace, if present
   *
   * @param e Input TableScan RelNode is. An example:
   *         <pre>
   *            LogicalTableScan(table=[[hive, default, complex]])
   *         </pre>
   * @return This override returns a Result whose sqlNode representation would be:
   *         <pre>
   *            SqlIdentifier(default, complex)
   *         </pre>
   *         supplemented with additional clause association details.
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
   * Correlate relNode represents a Node with two correlated child queries,
   * which upon conversion will be joined by non-traditional Join type.
   * The transformation strategy for a correlate type RelNode involves independently evaluating
   * the two sub-expressions, joining them with COMMA joinType and associating the resulting expression
   * with the FROM clause of the SQL.
   * When the right sub-expression of a Correlate node is an Uncollect / TableFunction type RelNode, the expression
   * obtained from evaluating this right child node will be padded with a LATERAL and AS SqlOperator sequentially.
   *
   * @param e RelNode of type Correlate with two child nodes as input. Example:
   *        <pre>
   *           LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])
   *                          /                                                  \
   *    LogicalTableScan(table=[[hive, default, complex]])                   HiveUncollect
   *                                                                               \
   *           			          			             			          		  	LogicalProject(col=[$cor0.c])
   *                                                                                 \
   *           			          			            			         			 	LogicalValues(tuples=[[{ 0 }]])
   *        </pre>
   *
   * @return Result of transforming the RelNode into a SqlNode with additional supplemental details.
   *         A sample generated Result would have the following SqlNode representation:
   *        <pre>
   *          default.complex , LATERAL UNNEST(`complex`.`c`) AS `t0` (`ccol`)
   *        </pre>
   *        supplemented with additional clause association and optional alias details.
   */
  @Override
  public Result visit(Correlate e) {
    final Result leftResult = visitChild(0, e.getLeft());

    // Add context specifying correlationId has same context as its left child
    correlTableMap.put(e.getCorrelationId(), leftResult.qualifiedContext());

    final Result rightResult = visitChild(1, e.getRight());

    SqlNode rightSqlNode = rightResult.asFrom();

    if (e.getRight() instanceof LogicalTableFunctionScan || e.getRight() instanceof Uncollect) {
      rightSqlNode = generateRightChildForSqlJoinWithLateralViews(e, rightResult);
    }

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS),
        JoinType.COMMA.symbol(POS), rightSqlNode, JoinConditionType.NONE.symbol(POS), null);

    return result(join, leftResult, rightResult);
  }

  /**
   * Coral represents custom table-valued functions as LogicalTableFunctionScan type relational expression.
   * Current version of Calcite used in Coral does not support traversing
   * a LogicalTableFunctionScan type RelNode. Hence, this implementation is added.
   *
   * @param e RelNode of type LogicalTableFunctionScan as input. Example:
   *           <pre>
   *             LogicalTableFunctionScan(invocation=[default_foo_lateral_udtf_CountOfRow($cor0.a)])
   *           </pre>
   *
   * @return Result of transforming the RelNode into a SqlNode with additional supplemental details.
   *         This output has similar semantics as the output of transforming an
   *         Uncollect type RelNode. (Why? Since both have similar tree representations and hence, similar parent nodes.)
   *         A sample generated Result would have the following SqlNode representation:
   *            <pre>
   *              COLLECTION_TABLE(`default_foo_lateral_udtf_CountOfRow`(`complex`.`a`))
   *            </pre>
   *         supplemented with additional clause association and alias details.
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

    SqlCall functionSqlCall = functionOperator.createCall(POS, functionOperands.toArray(new SqlNode[0]));

    SqlNode tableCall = new SqlLateralOperator(SqlKind.COLLECTION_TABLE).createCall(POS, functionSqlCall);

    Result tableCallResultWithAlias = result(tableCall, ImmutableList.of(Clause.FROM), e, null);

    return new Result(tableCall, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(tableCallResultWithAlias.neededAlias, e.getRowType()));
  }

  /**
   * Join relNode represents a Node with two child relational expressions linked by different join type conditions.
   *
   * @param e RelNode of type Join with two child nodes as input. Example:
   *        <pre>
   *                             LogicalJoin(condition=[true], joinType=[inner])
   *                                /                                      \
   *    LogicalTableScan(table=[[hive, default, complex]])              HiveUncollect
   *                                                                         \
   *           			          			                     		  	LogicalProject(col=[ARRAY('a', 'b')])
   *                                                                           \
   *           			          			         			         			 	LogicalValues(tuples=[[{ 0 }]])
   *        </pre>
   *
   * @return Result of transforming the RelNode into a SqlNode with additional supplemental details.
   *         A sample generated Result would have the following SqlNode representation:
   *        <pre>
   *          default.complex , LATERAL UNNEST(ARRAY ('a', 'b')) AS `t0` (`ccol`)
   *        </pre>
   *        supplemented with additional clause association and optional alias details.
   */
  @Override
  public Result visit(Join e) {
    Result leftResult = this.visitChild(0, e.getLeft()).resetAlias();
    Result rightResult = this.visitChild(1, e.getRight()).resetAlias();
    Context leftContext = leftResult.qualifiedContext();
    Context rightContext = rightResult.qualifiedContext();
    SqlNode sqlCondition = null;
    SqlLiteral condType = JoinConditionType.ON.symbol(POS);
    JoinType joinType = joinType(e.getJoinType());

    if (e.getJoinType() == JoinRelType.INNER && e.getCondition().isAlwaysTrue()) {
      joinType = dialect.emulateJoinTypeForCrossJoin();
      condType = JoinConditionType.NONE.symbol(POS);
    } else {
      sqlCondition = convertConditionToSqlNode(e.getCondition(), leftContext, rightContext,
          e.getLeft().getRowType().getFieldCount());
    }

    SqlNode rightSqlNode = rightResult.asFrom();

    if (e.getRight() instanceof LogicalTableFunctionScan || e.getRight() instanceof Uncollect) {
      rightSqlNode = generateRightChildForSqlJoinWithLateralViews(e, rightResult);
    }

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS), joinType.symbol(POS),
        rightSqlNode, condType, sqlCondition);

    return result(join, leftResult, rightResult);
  }

  /**
   * Coral represents relational expression that unnests its input's column(s)
   * using explode() function as an Uncollect type RelNode.
   *
   * @param e RelNode of type Uncollect as input. Example:
   *          <pre>
   *             HiveUncollect
   *               LogicalProject(col=[$cor0.c])
   *                 LogicalValues(tuples=[[{ 0 }]])
   *          </pre>
   *
   * @return Result with simplified custom transformations.
   *         The default super's implementation traverses this tree in post order traversal.
   *         The additive transformations applied generates a Result with SqlNode like:
   * 				<pre>
   *           UNNEST (SELECT `complex`.`c` AS `col` FROM (VALUES  (0)) AS `t` (`ZERO`)) AS `t0` (`ccol`)
   *        </pre>
   *         and additional unessential alias requirements which is then returned to the parent RelNode.
   *
   *         However, the above Result adds complexity to the overall transformations for the following reasons:
   *         1. Result's SqlNode has some expendable clauses, such as the SELECT clause inside UNNEST operator, with make parsing cumbersome in the parent RelNode's transformations.
   *         2. It also doesn't work well for unnesting array of type struct as the transformation attempts to generate individual columns for each datatype inside the struct.
   *         3. The above Result's SqlNode does not mimic the original SqlNode constructed from an input SQL.
   *
   *         Overriding this transformation outputs a more easily parsable Result which is consistent with the original SqlNode.
   *         The generated Result has the following simplified SqlNode representation:
   *        <pre>
   *           UNNEST(`complex`.`c`)
   *        </pre>
   *         and supplemental information about clause associations and optional aliases as required.
   */
  @Override
  public Result visit(Uncollect e) {

    // projectResult's SqlNode representation: SELECT `complex`.`c` AS `col` FROM (VALUES  (0)) AS `t` (`ZERO`)
    final Result projectResult = visitChild(0, e.getInput());

    // Extract column(s) to unnest from projectResult
    // to generate simpler operand for UNNEST operator
    final List<SqlNode> unnestOperands = new ArrayList<>();

    RelDataType recordType = null;
    boolean withOrdinality = e.withOrdinality;

    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(projectResult.qualifiedContext().toSql(null, unnestCol));
      if (unnestCol.getType().getSqlTypeName().equals(SqlTypeName.ARRAY)
          && unnestCol.getType().getComponentType().getSqlTypeName().equals(SqlTypeName.ROW)) {
        recordType = unnestCol.getType().getComponentType();
      }
    }

    // Generate SqlCall with Coral's UNNEST Operator and the unnestOperands. Also, persist ordinality and operand's datatype
    final SqlNode unnestCall =
        new CoralSqlUnnestOperator(withOrdinality, recordType).createCall(POS, unnestOperands.toArray(new SqlNode[0]));

    // Reuse the same projectResult.neededAlias since that's already unique by directly calling "new Result(...)"
    // instead of calling super.result(...), which will generate a new table alias and cause an extra
    // "AS" to be added to the generated SQL statement and make it invalid.
    return new Result(unnestCall, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(projectResult.neededAlias, e.getRowType()));
  }

  private SqlNode generateRightChildForSqlJoinWithLateralViews(BiRel e, Result rightResult) {
    SqlNode rightSqlNode = rightResult.asFrom();

    final SqlNode rightLateral = SqlStdOperatorTable.LATERAL.createCall(POS, rightSqlNode);

    // Append the alias to unnestCall by generating SqlCall with AS operator
    RelDataType relDataType = e.getRight().getRowType();
    String alias = rightResult.aliases.entrySet().stream().filter(entry -> relDataType.equals(entry.getValue()))
        .findFirst().map(Map.Entry::getKey).orElse("coralDefaultColumnAlias");

    List<SqlNode> asOperands = createAsFullOperands(relDataType, rightLateral, alias);

    return SqlStdOperatorTable.AS.createCall(POS, asOperands);
  }
}
