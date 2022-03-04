/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.type.SqlTypeName;


// Copied from Hive source code
public class HiveTypeSystem extends RelDataTypeSystemImpl {
  // TODO: This should come from type system; Currently there is no definition
  // in type system for this.
  private static final int MAX_DECIMAL_PRECISION = 38;
  private static final int MAX_DECIMAL_SCALE = 38;
  private static final int DEFAULT_DECIMAL_PRECISION = 10;
  // STRING type in Hive is represented as VARCHAR with precision Integer.MAX_VALUE.
  // In turn, the max VARCHAR precision should be 65535. However, the value is not
  // used for validation, but rather only internally by the optimizer to know the max
  // precision supported by the system. Thus, no VARCHAR precision should fall between
  // 65535 and Integer.MAX_VALUE; the check for VARCHAR precision is done in Hive.
  private static final int MAX_CHAR_PRECISION = Integer.MAX_VALUE;
  private static final int DEFAULT_VARCHAR_PRECISION = 65535;
  private static final int DEFAULT_CHAR_PRECISION = 255;
  private static final int MAX_BINARY_PRECISION = Integer.MAX_VALUE;
  private static final int MAX_TIMESTAMP_PRECISION = 9;

  @Override
  public int getMaxScale(SqlTypeName typeName) {
    switch (typeName) {
      case DECIMAL:
        return getMaxNumericScale();
      case INTERVAL_YEAR:
      case INTERVAL_MONTH:
      case INTERVAL_YEAR_MONTH:
      case INTERVAL_DAY:
      case INTERVAL_DAY_HOUR:
      case INTERVAL_DAY_MINUTE:
      case INTERVAL_DAY_SECOND:
      case INTERVAL_HOUR:
      case INTERVAL_HOUR_MINUTE:
      case INTERVAL_HOUR_SECOND:
      case INTERVAL_MINUTE:
      case INTERVAL_MINUTE_SECOND:
      case INTERVAL_SECOND:
        return SqlTypeName.MAX_INTERVAL_FRACTIONAL_SECOND_PRECISION;
      default:
        return -1;
    }
  }

  @Override
  public int getDefaultPrecision(SqlTypeName typeName) {
    switch (typeName) {
      // Hive will always require user to specify exact sizes for char, varchar;
      // Binary doesn't need any sizes; Decimal has the default of 10.
      case BINARY:
      case VARBINARY:
      case TIME:
      case TIMESTAMP:
        return RelDataType.PRECISION_NOT_SPECIFIED;
      case CHAR:
        return DEFAULT_CHAR_PRECISION;
      case VARCHAR:
        return DEFAULT_VARCHAR_PRECISION;
      case DECIMAL:
        return DEFAULT_DECIMAL_PRECISION;
      case INTERVAL_YEAR:
      case INTERVAL_MONTH:
      case INTERVAL_YEAR_MONTH:
      case INTERVAL_DAY:
      case INTERVAL_DAY_HOUR:
      case INTERVAL_DAY_MINUTE:
      case INTERVAL_DAY_SECOND:
      case INTERVAL_HOUR:
      case INTERVAL_HOUR_MINUTE:
      case INTERVAL_HOUR_SECOND:
      case INTERVAL_MINUTE:
      case INTERVAL_MINUTE_SECOND:
      case INTERVAL_SECOND:
        return SqlTypeName.DEFAULT_INTERVAL_START_PRECISION;
      default:
        return -1;
    }
  }

  @Override
  public int getMaxPrecision(SqlTypeName typeName) {
    switch (typeName) {
      case DECIMAL:
        return getMaxNumericPrecision();
      case VARCHAR:
      case CHAR:
        return MAX_CHAR_PRECISION;
      case VARBINARY:
      case BINARY:
        return MAX_BINARY_PRECISION;
      case TIME:
      case TIMESTAMP:
        return MAX_TIMESTAMP_PRECISION;
      case INTERVAL_YEAR:
      case INTERVAL_MONTH:
      case INTERVAL_YEAR_MONTH:
      case INTERVAL_DAY:
      case INTERVAL_DAY_HOUR:
      case INTERVAL_DAY_MINUTE:
      case INTERVAL_DAY_SECOND:
      case INTERVAL_HOUR:
      case INTERVAL_HOUR_MINUTE:
      case INTERVAL_HOUR_SECOND:
      case INTERVAL_MINUTE:
      case INTERVAL_MINUTE_SECOND:
      case INTERVAL_SECOND:
        return SqlTypeName.MAX_INTERVAL_START_PRECISION;
      default:
        return -1;
    }
  }

  @Override
  public int getMaxNumericScale() {
    return MAX_DECIMAL_SCALE;
  }

  @Override
  public int getMaxNumericPrecision() {
    return MAX_DECIMAL_PRECISION;
  }

  @Override
  public RelDataType deriveSumType(RelDataTypeFactory typeFactory, RelDataType argumentType) {
    switch (argumentType.getSqlTypeName()) {
      case TINYINT:
      case SMALLINT:
        return nullableType(typeFactory, SqlTypeName.INTEGER);
      case INTEGER:
      case BIGINT:
        return nullableType(typeFactory, SqlTypeName.BIGINT);
      case REAL:
      case FLOAT:
      case DOUBLE:
        return nullableType(typeFactory, SqlTypeName.DOUBLE);
      case DECIMAL:
        return nullableType(typeFactory, SqlTypeName.DECIMAL);
      default:
        return argumentType;
    }
  }

  @Override
  public boolean shouldConvertRaggedUnionTypesToVarying() {
    return true;
  }

  @Override
  public boolean isSchemaCaseSensitive() {
    return false;
  }

  private RelDataType nullableType(RelDataTypeFactory typeFactory, SqlTypeName typeName) {
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(typeName), true);
  }
}
