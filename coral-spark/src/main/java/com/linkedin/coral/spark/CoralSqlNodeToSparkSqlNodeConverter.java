/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlShuttle;

import com.linkedin.coral.hive.hive2rel.functions.CoralEmptyOperator;
import com.linkedin.coral.hive.hive2rel.functions.CoralSqlUnnestOperator;
import com.linkedin.coral.spark.functions.SqlLateralJoin;
import com.linkedin.coral.spark.functions.SqlLateralViewAsOperator;


/**
 * CoralSqlNodeToSparkSqlNodeConverter rewrites the Coral SqlNode AST. It replaces Coral IR SqlCalls
 * with Spark compatible SqlCalls to subsequently obtain a Spark compatible SqlNode AST representation.
 * This will enable generating a SQL which can be accurately interpreted by the Spark engine.
 *
 * It does so by visiting the Coral SqlNode AST in a pre-order traversal manner and
 * transforming each SqlNode (SqlCall), wherever required.
 * The transformation may involve change in operator, reordering the operands
 * or even re-constructing the SqlCall.
 */
public class CoralSqlNodeToSparkSqlNodeConverter extends SqlShuttle {
  public CoralSqlNodeToSparkSqlNodeConverter() {
  }

  @Override
  public SqlNode visit(final SqlCall sqlCall) {
    SqlCall transformedSqlCall = getTransformedSqlCall(sqlCall);
    return super.visit(transformedSqlCall);
  }

  public static SqlCall getTransformedSqlCall(SqlCall sqlCall) {
    switch (sqlCall.getOperator().kind) {
      case JOIN:
        return getTransformedJoinSqlCall(sqlCall);
      case AS:
        return getTransformedAsSqlCall(sqlCall);
      case LATERAL:
      case COLLECTION_TABLE:
        return getEmptyOperatorSqlCall(sqlCall);
      case UNNEST:
        return getTransformedUnnestSqlCall(sqlCall);
      default:
        return sqlCall;
    }
  }

  private static SqlCall getTransformedUnnestSqlCall(SqlCall sqlCall) {
    final SqlNode potentialIfCall = getOrDefault(sqlCall.getOperandList(), 0, null);

    // For outer unnest sqlCalls, Spark supports the simplified SQL format `LATERAL VIEW OUTER EXPLODE(...)`.
    // Hence, if there's an outer Unnest Coral SqlCall which contains the IF operator, for example:
    //        `EXPLODE(if(table_with_struct_array.array_struct_column IS NOT NULL AND size(table_with_struct_array.array_struct_column) > 0, table_with_struct_array.array_struct_column, ARRAY (NULL))) t0 AS exploded_results a`
    // It is transformed to :
    //        EXPLODE(table_with_struct_array.array_struct_column)
    // by extracting the column to explode and creating a new SqlCall
    if (potentialIfCall instanceof SqlCall
        && ((SqlCall) potentialIfCall).getOperator().getName().equalsIgnoreCase("if")) {
      final SqlNode unnestColumn = ((SqlCall) potentialIfCall).getOperandList().get(1);
      return sqlCall.getOperator().createCall(SqlParserPos.ZERO, unnestColumn);
    }
    return sqlCall;
  }

  private static SqlCall getTransformedAsSqlCall(SqlCall sqlCall) {
    // For a SqlCall with AS operator, Calcite un-parsing generates SQL:
    //        table_alias.column_alias t0 (ccol)
    // However, Spark SQL needs the AS operands to be un-parsed in the following manner:
    //        table_alias.column_alias t0 AS ccol
    // this transformation is enabled by substituting the operator with the SqlLateralViewAsOperator

    if (sqlCall.operandCount() <= 2 || !(sqlCall.operand(0) instanceof SqlBasicCall)
        || (sqlCall.operand(0) instanceof SqlBasicCall && sqlCall.operand(0).getKind() == SqlKind.VALUES)) {
      return sqlCall;
    }

    List<SqlNode> aliasOperands = sqlCall.getOperandList();
    SqlCall childSqlCall = sqlCall.operand(0);
    final SqlOperator childSqlCallOperator = childSqlCall.getOperator();

    List<SqlNode> newAsSqlCallOperands = new ArrayList<>();
    newAsSqlCallOperands.add(childSqlCall);

    // If the sqlCall with AS operator has a child sqlCall with POSEXPLODE operator,
    // we reorder the operands again from (`val, pos`) to (`pos, val`).
    // This will undo the reordering done during creation of CoralRelNode
    // in ParseTreeBuilder#visitLateralViewExplode for calcite validation
    if (childSqlCallOperator instanceof CoralSqlUnnestOperator
        && ((CoralSqlUnnestOperator) childSqlCallOperator).withOrdinality && sqlCall.getOperandList().size() == 4) {
      newAsSqlCallOperands.add(aliasOperands.get(1));
      newAsSqlCallOperands.add(aliasOperands.get(3));
      newAsSqlCallOperands.add(aliasOperands.get(2));
    } else {
      newAsSqlCallOperands.addAll(aliasOperands.subList(1, aliasOperands.size()));
    }

    return SqlLateralViewAsOperator.instance.createCall(SqlParserPos.ZERO, newAsSqlCallOperands);
  }

  private static SqlCall getEmptyOperatorSqlCall(SqlCall sqlCall) {
    // For sqlCalls with SqlOperaotrs, Calcite prints out the operator name during the sqlCall's un-parsing.
    // In Spark, for SqlCalls with LATERAL / COLLECTION_TABLE operator, the operator name is not needed.
    // Hence, the operator is replaced with another operator with empty string as the operator name.
    return CoralEmptyOperator.EMPTY_OPERATOR.createCall(SqlParserPos.ZERO, sqlCall.getOperandList());
  }

  private static SqlCall getTransformedJoinSqlCall(SqlCall sqlCall) {
    // All SQL JOIN clauses are represented as a SqlJoin SqlCall in the CoralSqlNode AST.
    // A transformation is only needed for SqlJoin SqlCalls with LATERAL joins.
    if (((SqlJoin) sqlCall).getJoinType() != JoinType.COMMA
        || ((SqlCall) ((SqlJoin) sqlCall).getRight()).getOperator().kind != SqlKind.LATERAL) {
      return sqlCall;
    }

    boolean isOuter = isCorrelateRightChildOuter(sqlCall);

    // The translated Spark SQL generated by un-parsing the default SqlJoin sqlCall
    // does not have the right syntax. It un-parses as follows:
    //       ... FROM table_alias, EXPLODE(...)
    // On the other hand, SqlLateralJoin supports the special SQL format required for Spark to parse. Sample SQL:
    //       ... FROM table_alias LATERAL VIEW (outer) EXPLODE(..)
    // During un-parsing, SqlLateralJoin appends keyword "LATERAL VIEW" in the required place for all unnest calls.
    // It also adds the keyword "OUTER" if the SqlCall contains the outer explode function.
    return new SqlLateralJoin(SqlParserPos.ZERO, ((SqlJoin) sqlCall).getLeft(),
        SqlLiteral.createBoolean(false, SqlParserPos.ZERO), JoinType.COMMA.symbol(SqlParserPos.ZERO),
        ((SqlJoin) sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO), null, isOuter);
  }

  private static boolean isCorrelateRightChildOuter(SqlCall sqlCall) {
    SqlCall lateralOperatorSqlCall = (SqlCall) ((SqlJoin) sqlCall).getRight();
    SqlCall asOperatorSqlCall = lateralOperatorSqlCall.operand(0);
    SqlCall unnestOperatorSqlCall = asOperatorSqlCall.operand(0);

    if (unnestOperatorSqlCall.getKind() == SqlKind.UNNEST) {
      SqlBasicCall unnestCall = (SqlBasicCall) unnestOperatorSqlCall;

      SqlNode ifNode = getOrDefault(unnestCall.getOperandList(), 0, null);
      if (ifNode instanceof SqlBasicCall && ((SqlBasicCall) ifNode).getOperator().getName().equalsIgnoreCase("if")) {
        return true;
      }
    }
    return false;
  }

  private static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
    return list.size() > index ? list.get(index) : defaultValue;
  }
}
