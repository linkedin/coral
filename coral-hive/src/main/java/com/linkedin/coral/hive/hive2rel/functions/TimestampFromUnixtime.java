/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;

import static org.apache.calcite.sql.type.ReturnTypes.explicit;


public class TimestampFromUnixtime extends SqlFunction {

  public static final TimestampFromUnixtime TIMESTAMP_FROM_UNIXTIME = new TimestampFromUnixtime();

  private TimestampFromUnixtime() {
    super(new SqlIdentifier("timestamp_from_unixtime", SqlParserPos.ZERO), explicit(SqlTypeName.TIMESTAMP), null, null,
        null, SqlFunctionCategory.TIMEDATE);
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.between(1, 3);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    final SqlNode firstOperand = callBinding.operand(0);
    if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, firstOperand, 0,
        throwOnFailure)) {
      return false;
    }

    if (callBinding.getOperandCount() == 2) {
      final SqlNode secondOperand = callBinding.operand(1);
      if (!OperandTypes.family(SqlTypeFamily.STRING).checkSingleOperandType(callBinding, secondOperand, 0,
          throwOnFailure)) {
        return false;
      }
    }

    if (callBinding.getOperandCount() == 3) {
      final SqlNode secondOperand = callBinding.operand(1);
      final SqlNode thirdOperand = callBinding.operand(2);
      if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, secondOperand, 0,
          throwOnFailure)) {
        return false;
      }
      if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, thirdOperand, 0,
          throwOnFailure)) {
        return false;
      }
    }
    return false;
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    SqlUtil.unparseSqlIdentifierSyntax(writer, new SqlIdentifier("from_unixtime", SqlParserPos.ZERO), true);
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "(", ")");
    final SqlLiteral quantifier = call.getFunctionQuantifier();
    if (quantifier != null) {
      quantifier.unparse(writer, 0, 0);
    }
    for (SqlNode operand : call.getOperandList()) {
      writer.sep(",");
      operand.unparse(writer, 0, 0);
    }
    writer.endList(frame);
  }
}
