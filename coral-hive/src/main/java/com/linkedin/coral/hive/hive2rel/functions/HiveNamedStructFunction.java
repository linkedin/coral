/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.util.Pair;
import org.apache.calcite.util.Static;

import static com.google.common.base.Preconditions.*;


public class HiveNamedStructFunction extends SqlUserDefinedFunction {
  public static final HiveNamedStructFunction NAMED_STRUCT = new HiveNamedStructFunction();

  public HiveNamedStructFunction() {
    super(new SqlIdentifier("named_struct", SqlParserPos.ZERO), null, null, null, null, null);
  }

  @Override
  public RelDataType inferReturnType(final SqlOperatorBinding opBinding) {
    checkState(opBinding instanceof SqlCallBinding);
    final SqlCallBinding callBinding = (SqlCallBinding) opBinding;
    return opBinding.getTypeFactory().createStructType(new AbstractList<Map.Entry<String, RelDataType>>() {
      @Override
      public int size() {
        return opBinding.getOperandCount() / 2;
      }

      @Override
      public Map.Entry<String, RelDataType> get(int index) {
        String fieldName = callBinding.operand(2 * index).toString();
        // strip quotes
        String fieldNameNoQuotes = fieldName.substring(1, fieldName.length() - 1);
        //Comparable colName = opBinding.getOperandLiteralValue(2 * index);

        return Pair.of(fieldNameNoQuotes, opBinding.getOperandType(2 * index + 1));
      }
    });
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.any();
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    List<SqlNode> operands = callBinding.operands();
    // check that every even numbered operand is a string literal and odd numbered operands
    // can be of any type
    for (int i = 0; i < operands.size() - 1; i += 2) {
      SqlNode fieldName = callBinding.operand(i);
      RelDataType colNameType = callBinding.getValidator().getValidatedNodeType(fieldName);
      if (SqlUtil.isNull(fieldName) || !SqlTypeFamily.STRING.contains(colNameType)) {
        if (throwOnFailure) {
          throw callBinding.newError(Static.RESOURCE.typeNotSupported(colNameType.toString()));
        } else {
          return false;
        }
      }
    }
    return true;
  }

  protected void checkOperandCount(SqlValidator validator, SqlOperandTypeChecker argTypeChecker, SqlCall call) {
    // Hive allows 0 arguments to named_struct but that causes issues with type inference.
    // Disallow for now and we will enable if there is a real use case
    if (call.operandCount() > 0 && call.operandCount() % 2 == 0) {
      // valid
      return;
    }
    throw validator.newValidationError(call, Static.RESOURCE.wrongNumOfArguments());
  }
}
