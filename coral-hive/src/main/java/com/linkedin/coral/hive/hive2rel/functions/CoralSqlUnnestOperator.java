/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlUnnestOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.SqlOperandCountRanges;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * This class is Coral's representation for Hive explode function.
 * Explode enables unboxing an array or map of an operand.
 * The function returns a row set of single column for the array operand, or
 * a row set with two columns corresponding to (key, value) for
 * map operand type. The array operand may be a primitive datatype, such as int, String.
 * Or, it may also be an abstract datatype, such as struct.
 */
public class CoralSqlUnnestOperator extends SqlUnnestOperator {

  // _withOrdinality represents whether output should contain an ORDINALITY column
  final boolean _withOrdinality;
  // _sqlDialect represents the target language's dialect. By default, it is set to spark
  String _sqlDialect = "spark";
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

  public void setSqlDialect(String sqlDialect) {
    _sqlDialect = sqlDialect;
  }

  public RelDataType getRelDataType() {
    return _relDataType;
  }

  @Override
  public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
    RelDataType operandType = callBinding.getOperandType(0);
    if (withOrdinality) {
      return operandType instanceof ArraySqlType;
    }
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

  @Override
  public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
    if (_sqlDialect.equals("trino")) {
      super.unparse(writer, call, leftPrec, rightPrec);
      return;
    }

    // for spark
    if (withOrdinality) {
      writer.keyword("POSEXPLODE");
    } else {
      writer.keyword("EXPLODE");
    }

    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "(", ")");
    for (SqlNode operand : call.getOperandList()) {
      writer.sep(",");
      operand.unparse(writer, 0, 0);
    }
    writer.endList(frame);
  }
}
