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
 * This Coral operator represents a table function that expands an array/map column into a relation.
 *
 * This operator supports returning a row set of a single column for operand type array[struct]
 * as opposed to super's behavior which returns individual columns for each data type inside the struct.
 * For the map operand, the return type is same as the super's - a row set with two columns corresponding to (key, value).
 */
public class CoralSqlUnnestOperator extends SqlUnnestOperator {

  // _withOrdinality represents whether output should contain an additional ORDINALITY column
  final boolean _withOrdinality;
  // _relDataType represents the data type of the operand to be expanded.
  RelDataType _relDataType;

  public static final String ARRAY_ELEMENT_COLUMN_NAME = "col";
  public static final String ARRAY_ELEMENT_POS_NAME = "pos";

  /**
   * This constructor is used when the datatype of the column to be expanded does not need to be persisted,
   * such as when converting a SQL statement from the source SQL dialect to Coral's SqlNode representation.
   */
  public CoralSqlUnnestOperator(boolean withOrdinality) {
    this(withOrdinality, null);
  }

  /**
   * This constructor is used when the datatype of the column to be expanded needs to be persisted,
   * such as when converting from the Coral RelNode representation to the Coral SqlNode representation.
   */
  public CoralSqlUnnestOperator(boolean withOrdinality, RelDataType relDataType) {
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

    if (operandType instanceof ArraySqlType) {
      builder.add(ARRAY_ELEMENT_COLUMN_NAME, operandType.getComponentType());
    } else {
      return super.inferReturnType(opBinding);
    }

    if (withOrdinality) {
      builder.add(ARRAY_ELEMENT_POS_NAME, SqlTypeName.INTEGER);
    }
    return builder.build();
  }
}
