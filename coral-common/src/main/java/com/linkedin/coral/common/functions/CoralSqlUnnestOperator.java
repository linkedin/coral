/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * This Coral operator enables unboxing an array or map of an operand.
 * The function returns a row set of single column for the array operand, or
 * a row set with two columns corresponding to (key, value) for
 * map operand type.
 */
public class CoralSqlUnnestOperator extends SqlUnnestOperator {

  // _withOrdinality represents whether output should contain an additional ORDINALITY column
  final boolean _withOrdinality;
  // _relDataType represents the datatype of the operand to be unboxed.
  RelDataType _relDataType;

  public static final String ARRAY_ELEMENT_COLUMN_NAME = "col";
  public static final String ARRAY_ELEMENT_POS_NAME = "pos";

  public CoralSqlUnnestOperator(boolean withOrdinality, RelDataType relDataType) {
    // keep the same as base class 'UNNEST' operator
    // Hive has a separate 'posexplode' function for ordinality
    super(withOrdinality);

    _withOrdinality = withOrdinality;
    _relDataType = relDataType;
  }

  public RelDataType getRelDataType() {
    return _relDataType;
  }

  @Override
  public SqlOperandCountRange getOperandCountRange() {
    return SqlOperandCountRanges.of(1);
  }

  @Override
  public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    RelDataType operandType = opBinding.getOperandType(0);
    final RelDataTypeFactory.Builder builder = opBinding.getTypeFactory().builder();

    if (withOrdinality) {
      builder.add(ARRAY_ELEMENT_COLUMN_NAME, operandType.getComponentType());
      builder.add(ARRAY_ELEMENT_POS_NAME, SqlTypeName.INTEGER);
    } else {
      if (operandType instanceof ArraySqlType) {
        builder.add(ARRAY_ELEMENT_COLUMN_NAME, operandType.getComponentType());
      } else {
        builder.add(MAP_KEY_COLUMN_NAME, operandType.getKeyType());
        builder.add(MAP_VALUE_COLUMN_NAME, operandType.getValueType());
      }
    }
    return builder.build();
  }
}
