/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlFunctionalOperator;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.util.Static;


public class HiveJsonTupleOperator extends SqlFunctionalOperator {
  public static final HiveJsonTupleOperator JSON_TUPLE = new HiveJsonTupleOperator();

  public HiveJsonTupleOperator() {
    super("json_tuple", SqlKind.OTHER_FUNCTION, 200, true, null, null, null);
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.from(2);
  }

  @Override
  protected void checkOperandCount(SqlValidator validator, SqlOperandTypeChecker argType, SqlCall call) {
    if (call.operandCount() < 2) {
      throw validator.newValidationError(call, Static.RESOURCE.wrongNumOfArguments());
    }
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    // TODO respect throwOnFailure
    for (int operand = 0; operand < callBinding.getOperandCount(); operand++) {
      RelDataType operandType = callBinding.getOperandType(operand);
      if (!operandType.getSqlTypeName().equals(SqlTypeName.VARCHAR)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    RelDataTypeFactory.Builder builder = opBinding.getTypeFactory().builder();
    for (int i = 0; i < opBinding.getOperandCount() - 1; i++) {
      builder.add("c" + i, SqlTypeName.VARCHAR);
    }
    return builder.build();
  }

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    // TODO, see org.apache.calcite.sql.SqlUnnestOperator#unparse
    super.unparse(writer, call, leftPrec, rightPrec);
  }

  @Override
  public boolean isDeterministic() {
    return true;
  }
}
