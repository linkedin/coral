/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.functions;

import java.util.List;

import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.Util;


public class SqlLateralJoin extends SqlJoin {

  final SqlLateralJoinOperator _operator;

  final SqlNode left;

  final SqlNode right;

  final boolean isOuter;

  public SqlLateralJoin(SqlParserPos pos, SqlNode left, SqlLiteral natural, SqlLiteral joinType, SqlNode right,
      SqlLiteral conditionType, SqlNode condition, boolean isOuter) {
    super(pos, left, natural, joinType, right, conditionType, condition);
    this.left = left;
    this.right = right;
    this.isOuter = isOuter;
    _operator = new SqlLateralJoinOperator(isOuter);
  }

  @Override
  public SqlOperator getOperator() {
    return _operator;
  }

  public static class SqlLateralJoinOperator extends SqlOperator {
    final boolean isOuter;

    public SqlLateralJoinOperator(boolean isOuter) {
      super("JOIN", SqlKind.JOIN, 16, true, null, null, null);
      this.isOuter = isOuter;
    }

    @Override
    public SqlSyntax getSyntax() {
      return SqlSyntax.SPECIAL;
    }

    @Override
    public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
      final List<SqlNode> operandList = call.getOperandList();
      // SqlJoin.getOperandList returns ImmutableNullableList.of(left, natural, joinType, right, conditionType, condition);
      // Therefore, we can get the `left`, `right` and `joinType` using index 0, 3 and 2
      final SqlNode left = operandList.get(0);
      final SqlNode right = operandList.get(3);
      JoinType joinType = (JoinType) ((SqlLiteral) operandList.get(2)).getValue();

      final SqlWriter.Frame joinFrame = writer.startList(SqlWriter.FrameTypeEnum.JOIN);
      left.unparse(writer, leftPrec, getLeftPrec());

      switch (joinType) {
        case COMMA:
          writer.literal("LATERAL VIEW");
          writer.setNeedWhitespace(true);
          if (isOuter) {
            writer.literal("OUTER");
          }
          break;
        default:
          throw Util.unexpected(joinType);
      }
      right.unparse(writer, getRightPrec(), rightPrec);
      writer.endList(joinFrame);
    }
  }
}
