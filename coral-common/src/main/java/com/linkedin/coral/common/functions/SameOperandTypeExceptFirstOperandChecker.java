/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.type.SameOperandTypeChecker;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;


/**
 * This class checks if the operands to a SQL function are all the same except first operand.
 * This allows NULL literal.
 *
 * This class is modeled after similar classes in calcite and should be moved to calcite but the
 * combinations are aplenty.
 */
public class SameOperandTypeExceptFirstOperandChecker extends SameOperandTypeChecker {

  protected final SqlTypeName firstOperandTypeName;

  public SameOperandTypeExceptFirstOperandChecker(int nOperands, SqlTypeName firstOperandTypeName) {
    super(nOperands);
    this.firstOperandTypeName = firstOperandTypeName;
  }

  protected boolean checkOperandTypesImpl(SqlOperatorBinding opBinding, boolean throwOnFailure,
      SqlCallBinding callBinding) {
    int actualOperands = nOperands;
    if (actualOperands == -1) {
      actualOperands = opBinding.getOperandCount();
    }
    Preconditions.checkState(!(throwOnFailure && callBinding == null));
    RelDataType[] types = new RelDataType[actualOperands];
    final List<Integer> operandList = getOperandList(opBinding.getOperandCount());
    for (Integer i : operandList) {
      types[i] = opBinding.getOperandType(i);
    }

    if (!types[0].getSqlTypeName().equals(firstOperandTypeName)) {
      return handleError(callBinding, throwOnFailure);
    }
    // skip 0th operand from this check, NULL types are also comparable
    for (int i = 2; i < operandList.size(); i++) {
      if (!isComparableWithNullable(types[i - 1], types[i])) {
        handleError(callBinding, throwOnFailure);
      }
    }
    return true;
  }

  public String getAllowedSignatures(SqlOperator op, String opName) {
    final String typeName = getTypeName();
    if (nOperands == -1) {
      return SqlUtil.getAliasedSignature(op, opName, ImmutableList.of("...", typeName, typeName));
    } else {
      @SuppressWarnings("unchecked")
      List<String> types = new ImmutableList.Builder().add(firstOperandTypeName)
          .addAll(Collections.nCopies(nOperands - 1, typeName)).build();
      return SqlUtil.getAliasedSignature(op, opName, types);
    }
  }

  private boolean handleError(SqlCallBinding callBinding, boolean throwOnFailure) {
    if (throwOnFailure) {
      throw callBinding.newValidationSignatureError();
    }
    return false;
  }

  private boolean isComparableWithNullable(RelDataType type1, RelDataType type2) {
    return type1.getSqlTypeName() == SqlTypeName.NULL || type2.getSqlTypeName() == SqlTypeName.NULL
        || SqlTypeUtil.isComparable(type1, type2);
  }
}
