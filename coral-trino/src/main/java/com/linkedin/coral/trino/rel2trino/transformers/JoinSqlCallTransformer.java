/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.JoinConditionType;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
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
  private static final String TRANSFORM_OPERATOR = "transform";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sqlCall.getOperator().kind == SqlKind.JOIN && ((SqlJoin) sqlCall).getJoinType() == JoinType.COMMA;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlJoin joinSqlCall = (SqlJoin) sqlCall;

    // Check if there's an unnest SqlCall present in the nested SqlNodes
    if (isUnnestOperatorPresentInRightSqlNode(joinSqlCall.getRight())) {
      // Check if the nested UNNEST SqlCall is correlated to the outer SQL query
      SqlCall unnestCall = ((SqlCall) joinSqlCall.getRight()).operand(0);
      if (isSqlCallCorrelated(unnestCall)) {
        // Substitute COMMA JOIN with CROSS JOIN
        return createCrossJoinSqlCall(joinSqlCall);
      } else {
        // If the unnest SqlCall is uncorrelated to the outer SQL query, for example,
        // when the unnest operand is an inline defined array, do not substitute JoinType
        return joinSqlCall;
      }
    } else {
      // Substitute COMMA JOIN with CROSS JOIN
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

  /**
   * Given an Unnest SqlCall, UNNEST('x'), the SqlCall is considered correlated when the unnest operand, 'x':
   *      (1) References a column from the base tables:
   *      Sample SqlCalls:
   *          - UNNEST(table1.col)
   *          - UNNEST(if(table1.col IS NOT NULL AND CAST(CARDINALITY(table1.col) AS INTEGER) > 0 table1.col, ARRAY[NULL] WITH ORDINALITY))
   *          - UNNEST(split(table1.stringCol, "delimiter"))
   *      (2) SqlCall with "TRANSFORM" operator to support unnesting array of structs
   *      Sample SqlCalls:
   *          - UNNEST(TRANSFORM(table1.col, x -> ROW(x)))
   * @param unnestSqlCall unnest sqlCall
   * @return true if the sqlCall is correlated to the outer query
   */
  private static boolean isSqlCallCorrelated(SqlCall unnestSqlCall) {
    for (SqlNode operand : unnestSqlCall.getOperandList()) {
      if (operand instanceof SqlIdentifier) {
        return true;
      } else if (operand instanceof SqlCall) {
        /**
         * transform sqlCall: TRANSFORM(table1.col, x -> ROW(x))
         * operator = "TRANSFORM"
         * operand = "table1.col, x -> ROW(x)"
         */
        if (((SqlCall) operand).getOperator().getName().equalsIgnoreCase(TRANSFORM_OPERATOR)) {
          return true;
        }
        if (isSqlCallCorrelated((SqlCall) operand)) {
          return true;
        }
      }
    }
    return false;
  }

  private static SqlCall createCrossJoinSqlCall(SqlJoin sqlCall) {
    return new SqlJoin(POS, (sqlCall).getLeft(), SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
        JoinType.CROSS.symbol(POS), (sqlCall).getRight(), JoinConditionType.NONE.symbol(SqlParserPos.ZERO), null);
  }
}
