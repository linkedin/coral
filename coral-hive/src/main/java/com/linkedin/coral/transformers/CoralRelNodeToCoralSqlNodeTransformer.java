/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.Util;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;


/**
 * This class converts Coral RelNode to Coral SqlNode.
 *
 * This class currently handles only lateral view transformations
 */
public class CoralRelNodeToCoralSqlNodeTransformer extends RelToSqlConverter {

  public static final SqlDialect INSTANCE = returnInstance();
  private final HiveFunctionResolver functionResolver =
      new HiveFunctionResolver(new StaticHiveFunctionRegistry(), new ConcurrentHashMap<>());

  /**
   * Creates a CoralRelNodeToCoralSqlNodeTransformer.
   */
  CoralRelNodeToCoralSqlNodeTransformer() {
    super(INSTANCE);
  }

  private static SqlDialect returnInstance() {
    SqlDialect.Context context = SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(SqlDialect.DatabaseProduct.HIVE)
        .withNullCollation(NullCollation.HIGH);

    return new SqlDialect(context);
  }

  /**
   * This overridden function makes sure that the basetable names in the output SqlNode
   * will be in the format - "dbname.tablename" instead of "catalogname.dbname.tablename"
   * For Example:
   * hive.default.complex will be converted to default.complex
   *
   * input RelNode format is:
   * LogicalTableScan(table=[[hive, default, complex]])
   * This overriding function creates a SqlIdentifier(default, complex)
   * instead of super's default: SqlIdentifier(hive, default, complex)
   */
  @Override
  public Result visit(TableScan e) {
    //todo: look into removing this transformation during hiveToRelConversion
    checkQualifiedName(e.getTable().getQualifiedName());
    List<String> tableNameWithoutCatalogAndDb = e.getTable().getQualifiedName().subList(1, 3);
    final SqlIdentifier identifier = new SqlIdentifier(tableNameWithoutCatalogAndDb, SqlParserPos.ZERO);
    return result(identifier, ImmutableList.of(Clause.FROM), e, null);
  }

  /**
   * Correlate represents the FROM clause of the final sql query
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

    SqlIdentifier leftNode = (SqlIdentifier) leftResult.node;
    SqlBasicCall lateralRightNode = (SqlBasicCall) rightResult.node;
    SqlBasicCall asSqlNode = (SqlBasicCall) lateralRightNode.getOperands()[0];
    SqlIdentifier asSqlNodeAlias = (SqlIdentifier) asSqlNode.getOperands()[1];

    //The default implementation assumes that the rightLateralNode, for LateralSqlOperator,
    // has a null alias. At the same time, it expects a non-null alias for the same node.
    // At this point, we can either customize the rightLateralNode to pass the alias check
    // or we can bypass the check and work around it. The latter appoach has been chosen.
    final ImmutableMap.Builder<String, RelDataType> builder =
        ImmutableMap.<String, RelDataType> builder().put(Util.last(leftNode.names), leftResult.neededType)
            .put(Util.last(asSqlNodeAlias.names), rightResult.neededType);

    return new Result(join, Expressions.list(Clause.FROM), null, null, builder.build());
  }

  /**
   * CORAL represents explode function as an Uncollect RelNode.
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

    Function unnestOperator = functionResolver.tryResolve("explode", null,
        // The first element of sqlOperands is the operator itself. The actual # of operands is sqlOperands.size() - 1
        unnestOperands.size() - 1);

    final SqlNode unnestNode = unnestOperator.getSqlOperator().createCall(POS, unnestOperands.toArray(new SqlNode[0]));

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
      throw new RuntimeException(
          "CoralRelNodeToCoralSqlNodeTransformer Error: Qualified name has incorrect number of elements");
    }
  }
}
