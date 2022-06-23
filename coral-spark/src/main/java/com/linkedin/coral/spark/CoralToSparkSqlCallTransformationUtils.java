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

import com.linkedin.coral.hive.hive2rel.functions.HivePosExplodeOperator;
import com.linkedin.coral.spark.functions.SqlEmptyOperator;
import com.linkedin.coral.spark.functions.SqlLateralJoin;
import com.linkedin.coral.spark.functions.SqlLateralViewAsOperator;


/**
 * Util class to obtain a spark compatible transformed SqlCall.
 * The transformation may involve change in operator, reordering the operands
 * or even re-construction of the SqlCall.
 */
public class CoralToSparkSqlCallTransformationUtils {
  private CoralToSparkSqlCallTransformationUtils() {
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

    if (potentialIfCall instanceof SqlCall
        && ((SqlCall) potentialIfCall).getOperator().getName().equalsIgnoreCase("if")) {
      final SqlNode unnestColumn = ((SqlCall) potentialIfCall).getOperandList().get(1);
      return sqlCall.getOperator().createCall(SqlParserPos.ZERO, unnestColumn);
    }
    return sqlCall;
  }

  private static SqlCall getTransformedAsSqlCall(SqlCall sqlCall) {
    if (sqlCall.operandCount() <= 2 || !(sqlCall.operand(0) instanceof SqlBasicCall)
        || (sqlCall.operand(0) instanceof SqlBasicCall && sqlCall.operand(0).getKind() == SqlKind.VALUES)) {
      return sqlCall;
    }

    SqlCall explodeCall = sqlCall.operand(0);
    final SqlOperator operator = explodeCall.getOperator();
    List<SqlNode> aliasOperands = sqlCall.getOperandList();

    List<SqlNode> asOperands = new ArrayList<>();
    asOperands.add(explodeCall);

    // For POSEXPLODE case, we reorder the operands again from `val, pos` to `pos, val` to undo the reordering during creation of CoralRelNode
    if (operator instanceof HivePosExplodeOperator && sqlCall.getOperandList().size() == 4) {
      asOperands.add(aliasOperands.get(1));
      asOperands.add(aliasOperands.get(3));
      asOperands.add(aliasOperands.get(2));
    } else {
      asOperands.addAll(aliasOperands.subList(1, aliasOperands.size()));
    }

    return SqlLateralViewAsOperator.instance.createCall(SqlParserPos.ZERO, asOperands);
  }

  private static SqlCall getEmptyOperatorSqlCall(SqlCall sqlCall) {
    return SqlEmptyOperator.EMPTY_OPERATOR.createCall(SqlParserPos.ZERO, sqlCall.getOperandList());
  }

  private static SqlCall getTransformedJoinSqlCall(SqlCall sqlCall) {

    if (((SqlJoin) sqlCall).getJoinType() != JoinType.COMMA
        || ((SqlCall) ((SqlJoin) sqlCall).getRight()).getOperator().kind != SqlKind.LATERAL) {
      return sqlCall;
    }

    boolean isOuter = isCorrelateRightChildOuter(sqlCall);

    return new SqlLateralJoin(SqlParserPos.ZERO, ((SqlJoin) sqlCall).getLeft(),
        SqlLiteral.createBoolean(false, SqlParserPos.ZERO), JoinType.COMMA.symbol(SqlParserPos.ZERO),
        ((SqlJoin) sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO), null, isOuter);
  }

  private static boolean isCorrelateRightChildOuter(SqlCall sqlCall) {
    SqlCall lateralNode = (SqlCall) ((SqlJoin) sqlCall).getRight();
    SqlCall asNode = lateralNode.operand(0);
    SqlCall unnestNode = asNode.operand(0);

    if (unnestNode.getKind() == SqlKind.UNNEST) {
      SqlBasicCall unnestCall = (SqlBasicCall) unnestNode;
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
