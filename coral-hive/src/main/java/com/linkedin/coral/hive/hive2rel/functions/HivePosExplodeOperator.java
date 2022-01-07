/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * Calcite operator representation for Hive posexplode function.
 * {@code posexplode} supports single array as argument and
 * behaves like explode for arrays, but includes the position of items in the original array
 */
public class HivePosExplodeOperator extends SqlUnnestOperator {

  public static final HivePosExplodeOperator POS_EXPLODE = new HivePosExplodeOperator();

  public static final String ARRAY_ELEMENT_POS_NAME = "pos";
  public static final String ARRAY_ELEMENT_VAL_NAME = "col";

  public HivePosExplodeOperator() {
    // keep the same as base class 'UNNEST' operator
    super(true);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    RelDataType operandType = callBinding.getOperandType(0);
    return operandType instanceof ArraySqlType;
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(1);
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    RelDataType operandType = opBinding.getOperandType(0);
    final RelDataTypeFactory.Builder builder = opBinding.getTypeFactory().builder();
    builder.add(ARRAY_ELEMENT_VAL_NAME, operandType.getComponentType());
    builder.add(ARRAY_ELEMENT_POS_NAME, SqlTypeName.INTEGER);
    return builder.build();
  }
}
