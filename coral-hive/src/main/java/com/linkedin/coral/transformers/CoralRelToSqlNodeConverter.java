/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.linq4j.tree.Expressions;
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
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.Util;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.com.google.common.collect.Iterables;
import com.linkedin.coral.hive.hive2rel.functions.HiveExplodeOperator;
import com.linkedin.coral.hive.hive2rel.functions.HivePosExplodeOperator;

import static org.apache.calcite.sql.parser.SqlParserPos.*;


/**
 * This class converts Coral RelNode to Coral SqlNode.
 *
 * This class currently handles only lateral view transformations
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
   * This overridden implementation removes the catalog name from the table path.
   * from the function makes sure that the basetable names in the output SqlNode
   *
   * Example: input TableScan RelNode is:
   * LogicalTableScan(table=[[hive, default, complex]])
   * This override returns a Result whose sqlNode representation is: SqlIdentifier(default, complex)
   */
  @Override
  public Result visit(TableScan e) {
    checkQualifiedName(e.getTable().getQualifiedName());
    List<String> tableNameWithoutCatalog = e.getTable().getQualifiedName().subList(1, 3);
    final SqlIdentifier identifier = new SqlIdentifier(tableNameWithoutCatalog, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Correlate RelNode represents the FROM clause of the final sql query
   * when the FROM clause contains explode() UDF as well
   *
   * Overriding the default implementation is required because
   * the rightChildResult is also overriden and doesn't match super's expectation
   */
  @Override
  public Result visit(Correlate e) {
    //Left Result removes the catalog name "hive" from the table path
    final Result leftResult = visitChild(0, e.getLeft());

    // Add context specifying correlationId has same context as its left child
    correlTableMap.put(e.getCorrelationId(), leftResult.qualifiedContext());

    final Result rightResult = visitChild(1, e.getRight());

    SqlJoin join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS),
        JoinType.COMMA.symbol(POS), rightResult.asFrom(), JoinConditionType.NONE.symbol(POS), null);

    //The default implementation assumes that the rightLateralNode, for LateralSqlOperator,
    // has a null alias. At the same time, it expects a non-null alias for the same node.
    // At this point, we can either customize the rightResult to pass the alias check
    // or we can bypass the check and work around it. The latter approach has been chosen.

    final ImmutableMap.Builder<String, RelDataType> builder = ImmutableMap.<String, RelDataType> builder();
    collectAliases(builder, join,
        Iterables.concat(leftResult.aliases.values(), rightResult.aliases.values()).iterator());

    return new Result(join, Expressions.list(Clause.FROM), null, null, builder.build());
  }

  /**
   * Coral represents table function as an LogicalTableFunctionScan RelNode.
   *
   * Current version of calcite used in Coral does not have a default implementation for
   * visiting a LogicalTableFunctionScan RelNode and throws an assertionError instead. Hence this implementation is required.
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

    SqlNode tableCall = new SqlLateralOperator(SqlKind.COLLECTION_TABLE).createCall(POS, functionOperatorCall);

    Result x = result(tableCall, ImmutableList.of(Clause.FROM), e, null);

    List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), tableCall, x.neededAlias);

    SqlCall aliasCall = SqlStdOperatorTable.AS.createCall(ZERO, asOperands);

    final SqlNode lateralNode = SqlStdOperatorTable.LATERAL.createCall(POS, aliasCall);

    return new Result(lateralNode, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(x.neededAlias, e.getRowType()));
  }

  /**
   * Join has a right child node, Hive uncollect. SqlNode of the Result
   * from this right child node is now of kind LATERAL, as opposed to AS as per the super's implementation.
   * SqlValidatorUtil.getAlias assumes a null alias for LATERAL SqlNodes and hence, Result construction fails for Join.
   *
   * @param e
   * @return
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

    if (isCrossJoin(e)) {
      joinType = dialect.emulateJoinTypeForCrossJoin();
      condType = JoinConditionType.NONE.symbol(POS);
    } else {
      sqlCondition = convertConditionToSqlNode(e.getCondition(), leftContext, rightContext,
          e.getLeft().getRowType().getFieldCount());
    }

    SqlNode join = new SqlJoin(POS, leftResult.asFrom(), SqlLiteral.createBoolean(false, POS), joinType.symbol(POS),
        rightResult.asFrom(), condType, sqlCondition);

    final ImmutableMap.Builder<String, RelDataType> builder = ImmutableMap.<String, RelDataType> builder();
    collectAliases(builder, join,
        Iterables.concat(leftResult.aliases.values(), rightResult.aliases.values()).iterator());

    //Calling new Result(...) explicitly prevents addition of new aliases.
    // The builder provided has the needed aliases for all SqlNode kinds available to the join clause.
    return new Result(join, Expressions.list(Clause.FROM), null, null, builder.build());
  }

  /**
   * Coral represents explode function as an Uncollect RelNode.
   * The default super's implementation takes Project RelNode as input in the form of
   *        SELECT `complex`.`c` as`ccol` FROM (VALUES  (0))
   * and appends the UNNEST and AS operators on top of it.
   *
   * However, the result generated is unmanageable for the following reasons:
   * 1. Result has some extra clauses with make parsing difficult.
   * 2. It also doesn't work well for unboxing array of type struct.
   * It attempts to generate individual columns for each datatype inside the struct.
   * 3. It doesn't append the LATERAL operator
   *
   * Overriding the function helps generate a more easily parsable SqlNode:
   * LATERAL UNNEST(`complex`.`c`) AS t0 (ccol)
   */
  @Override
  public Result visit(Uncollect e) {

    final Result x = visitChild(0, e.getInput());

    // Extract unnestColumns from result x: (SELECT <unnestColumns> FROM (VALUES(0)))
    // and generate UNNEST(<unnestColumns>) instead.
    final List<SqlNode> unnestOperands = new ArrayList<>();
    for (RexNode unnestCol : ((Project) e.getInput()).getChildExps()) {
      unnestOperands.add(x.qualifiedContext().toSql(null, unnestCol));
    }

    // Convert UNNEST to EXPLODE or POSEXPLODE function
    final SqlNode unnestNode = (e.withOrdinality ? HivePosExplodeOperator.POS_EXPLODE : HiveExplodeOperator.EXPLODE)
        .createCall(POS, unnestOperands.toArray(new SqlNode[0]));

    List<SqlNode> asOperands = createAsFullOperands(e.getRowType(), unnestNode, x.neededAlias);
    final SqlNode asNode = SqlStdOperatorTable.AS.createCall(POS, asOperands);

    final SqlNode lateralNode = SqlStdOperatorTable.LATERAL.createCall(POS, asNode);

    // Reuse the same x.neededAlias since that's already unique by directly calling "new Result(...)"
    // instead of calling super.result(...), which will generate a new table alias and cause an extra
    // "AS" to be added to the generated SQL statement and make it invalid.
    return new Result(lateralNode, ImmutableList.of(Clause.FROM), null, e.getRowType(),
        ImmutableMap.of(x.neededAlias, e.getRowType()));
  }

  private void checkQualifiedName(List<String> qualifiedName) {
    if (qualifiedName.size() != 3) {
      throw new RuntimeException("CoralRelToSqlNodeConverter Error: Qualified name has incorrect number of elements");
    }
  }

  private boolean isCrossJoin(Join e) {
    return e.getJoinType() == JoinRelType.INNER && e.getCondition().isAlwaysTrue();
  }

  private void collectAliases(ImmutableMap.Builder<String, RelDataType> builder, SqlNode node,
      Iterator<RelDataType> aliases) {
    if (node instanceof SqlJoin) {
      SqlJoin join = (SqlJoin) node;
      collectAliases(builder, join.getLeft(), aliases);
      collectAliases(builder, join.getRight(), aliases);
    } else {
      String alias = getAlias(node, -1);
      assert alias != null;
      builder.put(alias, aliases.next());
    }
  }

  private String getAlias(SqlNode node, int ordinal) {
    switch (node.getKind()) {
      case AS:
        return ((SqlCall) node).operand(1).toString();
      case OVER:
        return getAlias(((SqlCall) node).operand(0), ordinal);
      case IDENTIFIER:
        return (String) Util.last(((SqlIdentifier) node).names);
      case LATERAL:
        SqlCall asNode = ((SqlCall) node).operand(0);
        return getAlias(asNode, ordinal);
      default:
        return ordinal < 0 ? null : SqlUtil.deriveAliasFromOrdinal(ordinal);
    }
  }
}
