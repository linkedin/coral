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


/**
 * This class implements the transformation of SqlCalls with JOIN operator with COMMA JoinType to
 * their corresponding Trino-compatible versions.
 *
 * For example, an input SqlJoin SqlCall:
 *
 *          SqlJoin[`default`.`complex` , UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)]
 *                                         |
 *                _________________________|_____________________________
 *               |                         |                            |
 *  left: `default`.`complex`         joinType: ,       right: UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)
 *
 * Is transformed to:
 *
 *          SqlJoin[`default`.`complex` CROSS JOIN UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)]
 *                                         |
 *                _________________________|_____________________________
 *               |                         |                            |
 *  left: `default`.`complex`         joinType: CROSS JOIN       right: UNNEST(`complex`.`c`) AS `t_alias` (`col_alias`)
 */
public class JoinSqlCallTransformer extends SqlCallTransformer {
  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().kind == SqlKind.JOIN && ((SqlJoin) sqlCall).getJoinType() == JoinType.COMMA;
  }

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
    // Check if there's an unnest SqlCall present in the nested SqlNodes
    // if not, substitute COMMA JOIN with CROSS JOIN
    // if yes, check if the unnest SqlCall is uncorrelated.
    //

    if (isUnnestOperatorPresentInRightSqlNode(joinSqlCall.getRight())) {
      // Check if the unnest SqlCall is uncorrelated with the SqlJoin
      if (isUnnestSqlCallCorrelated(joinSqlCall.getRight())) {
        return createCrossJoinSqlCall(joinSqlCall);
      } else {
        return joinSqlCall;
      }
    } else {
      return createCrossJoinSqlCall(joinSqlCall);
    }
  }

  /**
   * Check if the input sqlNode has a nested SqlCall with UNNEST operator
   * @param rightSqlNode right child of a SqlJoin SqlCall
   * @return boolean result
   */
  private static boolean isUnnestOperatorPresentInRightSqlNode(SqlNode rightSqlNode) {
    return rightSqlNode instanceof SqlCall && rightSqlNode.getKind() == SqlKind.AS
        && ((SqlCall) rightSqlNode).operand(0) instanceof SqlCall
        && ((SqlCall) rightSqlNode).operand(0).getKind() == SqlKind.UNNEST;
  }

  private static boolean isUnnestSqlCallCorrelated(SqlNode sqlNode) {
    SqlNode aliasOperand = ((SqlCall) sqlNode).operand(0); //  unnest(x) AS y
    SqlNode unnestOperand = ((SqlCall) aliasOperand).operand(0);

    // When the unnest operand is:
    // (1) SqlIdentifier referring to a column, ex: table1.col1
    // (2) SqlCall with "IF" operator for outer unnest
    // (3) SqlCall with "TRANSFORM" operator to support unnesting array of structs
    // Substitute JoinType with CROSS JoinType.
    if (unnestOperand.getKind() == SqlKind.IDENTIFIER
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("transform"))
        || (unnestOperand instanceof SqlCall
            && ((SqlCall) unnestOperand).getOperator().getName().equalsIgnoreCase("if"))) {
      return true;
    }
    // If the unnest SqlCall is uncorrelated with the SqlJoin, for example,
    // when the unnest operand is an inline defined array, do not substitute JoinType
    return false;
  }

  private static SqlCall createCrossJoinSqlCall(SqlJoin sqlCall) {
    return new SqlJoin(POS, (sqlCall).getLeft(), SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
        JoinType.CROSS.symbol(POS), (sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO), null);
  }
}
