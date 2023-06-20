/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.*;


public class JoinSqlCallTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    if (sqlCall.getOperator().kind == SqlKind.JOIN && ((SqlJoin) sqlCall).getJoinType() == JoinType.COMMA) {
      return true;
    }
    return false;
  }

  // Update unnest operand for trino engine to expand the unnest operand to a single column
  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlJoin joinSqlCall = (SqlJoin) sqlCall;

    /**
     * check if there's an unnest SqlCall present in the nested SqlNodes:
     * false -> substitute COMMA JOIN with CROSS JOIN
     * true -> check if unnest operand is an inline independent array (not referring to columns in the SQL)
     *                  true -> return
     *                  false -> substitute COMMA JOIN with CROSS JOIN
     */
    if (isUnnestOperatorPresentInChildNodeNew(joinSqlCall.getRight())) {
      if (shouldSwapForCrossJoinNew(joinSqlCall.getRight())) {
        return createCrossJoinSqlCall(joinSqlCall);
      } else {
        return joinSqlCall;
      }
    } else {
      return createCrossJoinSqlCall(joinSqlCall);
    }
  }

  private static boolean isUnnestOperatorPresentInChildNodeNew(SqlNode sqlNode) {
    if (sqlNode instanceof SqlCall && sqlNode.getKind() == SqlKind.AS
        && ((SqlCall) sqlNode).operand(0) instanceof SqlCall
        && ((SqlCall) sqlNode).operand(0).getKind() == SqlKind.UNNEST) {
      return true;
    }
    return false;
  }

  private static boolean isUnnestOperatorPresentInChildNode(SqlNode sqlNode) {
    if (sqlNode instanceof SqlCall && sqlNode.getKind() == SqlKind.AS
        && ((SqlCall) sqlNode).operand(0) instanceof SqlCall
        && ((SqlCall) sqlNode).operand(0).getKind() == SqlKind.LATERAL
        && ((SqlCall) ((SqlCall) sqlNode).operand(0)).operand(0) instanceof SqlCall
        && ((SqlCall) ((SqlCall) sqlNode).operand(0)).operand(0).getKind() == SqlKind.UNNEST) {
      return true;
    }
    return false;
  }

  private static boolean shouldSwapForCrossJoinNew(SqlNode sqlNode) {
    SqlNode aliasOperand = ((SqlCall) sqlNode).operand(0); //  unnest(x)
    SqlNode lateralOperand = ((SqlCall) aliasOperand).operand(0); //  unnest(x)
    SqlNode unnestOperand = ((SqlCall) aliasOperand).operand(0);

    // Field to unnest can be:
    // (1) a SqlIdentifier referring to a column, ex: table1.col1
    // (2) a SqlCall with "if" operator for outer unnest
    // (3) a SqlSelect SqlCall
    // For the above scenarios, return true
    if (unnestOperand.getKind() == SqlKind.IDENTIFIER
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("transform"))
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("if"))) {
      // should go to cross join
      return true;
    }
    // If the unnest operand is an inline defined array, return false
    return false;
  }

  private static boolean shouldSwapForCrossJoin(SqlNode sqlNode) {
    SqlNode aliasOperand = ((SqlCall) sqlNode).operand(0); // LATERAL unnest(x)
    SqlNode lateralOperand = ((SqlCall) aliasOperand).operand(0); //  unnest(x)
    SqlNode unnestOperand = ((SqlCall) lateralOperand).operand(0);

    // Field to unnest can be:
    // (1) a SqlIdentifier referring to a column, ex: table1.col1
    // (2) a SqlCall with "if" operator for outer unnest
    // (3) a SqlSelect SqlCall
    // For the above scenarios, return true
    if (unnestOperand.getKind() == SqlKind.IDENTIFIER
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("if"))
        || (lateralOperand.getKind() == SqlKind.SELECT)) { // should go to cross join
      return true;
    }
    // If the unnest operand is an inline defined array, return false
    return false;
  }

  private static SqlCall createCrossJoinSqlCall(SqlCall sqlCall) {
    return new SqlJoin(POS, ((SqlJoin) sqlCall).getLeft(), SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
        JoinType.CROSS.symbol(POS), ((SqlJoin) sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
        null);
  }
}
