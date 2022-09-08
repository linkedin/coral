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
 * This class converts a Coral intermediate representation, CoralRelNode, to CoralSqlNode.
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
   * This overridden implementation removes the catalog name from the table namespace, if present.
   *
   * @param e TableScan RelNode. An example:
   *         <pre>
   *            LogicalTableScan(table=[[hive, default, complex]])
   *         </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The conversion result of the above example is:
   *        <pre>
   *               (SqlIdentifier)
   *            names: `default``complex`
   *        </pre>
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
   * The transformation strategy for a correlate type RelNode involves independently evaluating
   * the two sub-expressions, joining them with COMMA joinType and associating the resulting expression
   * with the FROM clause of the SQL.
   * When the right child of a Correlate node is an Uncollect / TableFunction type RelNode,
   * this method introduces LATERAL and AS operators as parents of the conversion result of the right child
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
   * @return Result of converting the RelNode to a SqlNode.
   *        The conversion result of the above example is:
   *        <pre>
   *                                      (SqlCall)
   *        SqlJoin[default.complex , LATERAL UNNEST(`complex`.`c`) AS `t0` (`ccol`)]
   *                                         |
   *                _________________________|_____________________________
   *               |                         |                            |
   *  left: `default`.`complex`         joinType: ,       right: LATERAL UNNEST(`complex`.`c`) AS `t0` (`ccol`)
   *
   *        </pre>
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
   * Custom table-valued functions are represented as LogicalTableFunctionScan type relational expression.
   * Current version of Calcite used in Coral does not implement traversing
   * a LogicalTableFunctionScan type RelNode. Hence, this implementation is added.
   *
   * @param e RelNode of type LogicalTableFunctionScan as input. Example:
   *           <pre>
   *             LogicalTableFunctionScan(invocation=[foo_udtf_CountOfRow($cor0.a)])
   *           </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The conversion result of the above example is:
   *         <pre>
   *                                (SqlBasicCall)
   *          COLLECTION_TABLE(`foo_udtf_CountOfRow`(`complex`.`a`))
   *                                     |
   *            _________________________|__________________________
   *           |                                                   |
   *  Operator: COLLECTION_TABLE               Operand: `foo_udtf_CountOfRow`(`complex`.`a`)
   *                                                              |
   *                                             _________________|______________________
   *                                            |                                       |
   *                             Operator: foo_udtf_CountOfRow               Operand: `complex`.`a`
   *         </pre>
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
   * Join RelNode represents a Node with two child relational expressions linked by different join type conditions.
   * When the right child of a Correlate node is an Uncollect / TableFunction type RelNode,
   * this method introduces LATERAL and AS operators as parents of the conversion result of the right child.
   * @param e RelNode of type Join with two child nodes as input. Example:
   *        <pre>
   *                             LogicalJoin(condition=[true], joinType=[inner])
   *                                /                                      \
   *    LogicalTableScan(table=[[hive, default, complex]])              HiveUncollect
   *                                                                         \
   *           			          			                        		  	LogicalProject(col=[$cor0.c])
   *                                                                           \
   *           			          			         			          			 	LogicalValues(tuples=[[{ 0 }]])
   *        </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *        The conversion result of the above example is:
   *        <pre>
   *                                      (SqlCall)
   *          SqlJoin[`default`.`complex` , LATERAL UNNEST(`complex`.`c`) AS `t0` (`ccol`)]
   *                                         |
   *                _________________________|_____________________________
   *               |                         |                            |
   *  left: `default`.`complex`         joinType: ,       right: LATERAL UNNEST(`complex`.`c`) AS `t0` (`ccol`)
   *
   *        </pre>
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
   * UNNEST function is represented as HiveUncollect type relational expression.
   * The default super's implementation traverses this tree in post order traversal and generates a Result with SqlNode like:
   * <pre>
   *     UNNEST (SELECT `complex`.`c` AS `col` FROM (VALUES  (0)) AS `t` (`ZERO`)) AS `t0` (`ccol`)
   * </pre>
   * and additional unessential alias requirements which is then returned to the parent RelNode.
   *
   * However, the above Result adds complexity to the overall transformations for the following reasons:
   *   1. Super's Result has some expendable clauses, such as the SELECT clause inside UNNEST operator, with make parsing cumbersome in the parent RelNode's transformations.
   *   2. It also doesn't work well for unnesting array of type struct as the conversion attempts to generate individual columns for each datatype inside the struct.
   *   3. Super's Result does not mimic the original SqlNode constructed from an input SQL.
   *
   * Overriding this conversion outputs a more easily parsable Result which is consistent with the original SqlNode.
   *
   * @param e RelNode of type HiveUncollect as input. Example:
   *          <pre>
   *             HiveUncollect
   *                      \
   *               LogicalProject(col=[$cor0.c])
   *                        \
   *                 LogicalValues(tuples=[[{ 0 }]])
   *          </pre>
   *
   * @return Result of converting the RelNode to a SqlNode.
   *         The conversion result of the above example is:
   *        <pre>
   *                               (SqlBasicCall)
   *                           UNNEST(`complex`.`c`)
   *                                     |
   *            _________________________|__________________________
   *           |                                                   |
   *  Operator: CoralSqlUnnestOperator                 Operand: `complex`.`c`
   *
   *        </pre>
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
