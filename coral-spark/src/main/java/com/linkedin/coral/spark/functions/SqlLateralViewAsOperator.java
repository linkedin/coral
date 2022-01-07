/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.functions;

import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.util.Util;


/**
 * SqlLateralViewAsOperator inherits from SqlAsOperator.
 *
 * The only difference between the two is that the `unparse` method in SqlLateralViewAsOperator
 * produces
 *     "... table_alias AS column_alias1, column_alias2"
 * instead of
 *     "... AS table_alias(column_alias1, column_alias2)"
 *
 * The inheritance is necessary for the code to pass the assert condition in
 * {@link org.apache.calcite.rel.rel2sql.SqlImplementor#wrapSelect(SqlNode)}
 */
public class SqlLateralViewAsOperator extends SqlAsOperator {
  public static final SqlLateralViewAsOperator instance = new SqlLateralViewAsOperator();

  public SqlLateralViewAsOperator() {
    super("LAS", SqlKind.AS, 20, true, ReturnTypes.ARG0, InferTypes.RETURN_TYPE, OperandTypes.ANY_ANY);
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    assert call.operandCount() >= 2;
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.SIMPLE);
    call.operand(0).unparse(writer, leftPrec, getLeftPrec());
    final boolean needsSpace = true;
    writer.setNeedWhitespace(needsSpace);
    call.operand(1).unparse(writer, getRightPrec(), rightPrec);
    writer.sep("AS");
    writer.setNeedWhitespace(needsSpace);
    if (call.operandCount() > 2) {
      final SqlWriter.Frame frame1 = writer.startList(SqlWriter.FrameTypeEnum.SIMPLE);
      for (SqlNode operand : Util.skip(call.getOperandList(), 2)) {
        writer.sep(",", false);
        operand.unparse(writer, 0, 0);
      }
      writer.endList(frame1);
    }
    writer.endList(frame);
  }
}
