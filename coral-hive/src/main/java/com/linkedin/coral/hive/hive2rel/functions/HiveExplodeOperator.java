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
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.SqlOperandCountRanges;


/**
 * Calcite operator representation for Hive explode function.
 * {@code explode} supports single array or map as argument and
 * returns a row set of single column for array operand, or
 * a row set with two columns corresponding to (key, value) for
 * map operand type.
 */
public class HiveExplodeOperator extends SqlUnnestOperator {

  public static final HiveExplodeOperator EXPLODE = new HiveExplodeOperator();

  public static final String ARRAY_ELEMENT_COLUMN_NAME = "col";

  public HiveExplodeOperator() {
    // keep the same as base class 'UNNEST' operator
    // Hive has a separate 'posexplode' function for ordinality
    super(false);
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    RelDataType operandType = callBinding.getOperandType(0);
    return operandType instanceof ArraySqlType || operandType instanceof MapSqlType;
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(1);
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    RelDataType operandType = opBinding.getOperandType(0);
    final RelDataTypeFactory.Builder builder = opBinding.getTypeFactory().builder();
    if (operandType instanceof ArraySqlType) {
      // array type
      builder.add(ARRAY_ELEMENT_COLUMN_NAME, operandType.getComponentType());
    } else {
      // map type
      builder.add(MAP_KEY_COLUMN_NAME, operandType.getKeyType());
      builder.add(MAP_VALUE_COLUMN_NAME, operandType.getValueType());
    }
    return builder.build();
  }
}
