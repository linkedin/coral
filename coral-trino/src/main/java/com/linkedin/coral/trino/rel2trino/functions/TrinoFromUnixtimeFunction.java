/**
 * Copyright 2019-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import com.linkedin.coral.common.functions.FunctionReturnTypes;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlSingleOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.Arrays;


/**
 * TrinoFromUnixtimeFunction represents a SQL operator that maps to Trino's `from_unixtime` function
 * which is namesake, but with different signature, with Hive's `from_unixtime` function.
 */
public class TrinoFromUnixtimeFunction extends SqlOperator {

  public static final TrinoFromUnixtimeFunction INSTANCE = new TrinoFromUnixtimeFunction();

  private TrinoFromUnixtimeFunction() {
    super("from_unixtime", SqlKind.OTHER_FUNCTION, 100, true, FunctionReturnTypes.TIMESTAMP, null, null);
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    SqlUtil.unparseFunctionSyntax(this, writer, call);
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.between(1, 3);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    final SqlNode firstOperand = callBinding.operand(0);
    if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, firstOperand, 0, throwOnFailure)) {
      return false;
    }

    if (callBinding.getOperandCount() == 2){
      final SqlNode secondOperand = callBinding.operand(1);
      if (!OperandTypes.family(SqlTypeFamily.STRING).checkSingleOperandType(callBinding, secondOperand, 0, throwOnFailure)) {
        return false;
      }
    }

    if (callBinding.getOperandCount() == 2){
      final SqlNode secondOperand = callBinding.operand(1);
      final SqlNode thirdOperand = callBinding.operand(2);
      if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, secondOperand, 0, throwOnFailure)) {
        return false;
      }
      if (!OperandTypes.family(SqlTypeFamily.NUMERIC).checkSingleOperandType(callBinding, thirdOperand, 0, throwOnFailure)) {
        return false;
      }
    }

    // unknown function signature
    return false;
  }

  @Override
  public SqlSyntax getSyntax() {
    return SqlSyntax.FUNCTION;
  }
}
