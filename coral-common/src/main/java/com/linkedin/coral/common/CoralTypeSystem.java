/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;


/**
 * Coral's type system configuration for Calcite, defining precision and scale defaults
 * compatible with Hive/Spark SQL semantics.
 *
 * <p>This class was previously named {@code HiveTypeSystem} and has been renamed to better
 * reflect that it is a dialect-agnostic Calcite type system configuration used across
 * all Coral translation targets (Hive, Spark, Trino, etc.).
 */
public class CoralTypeSystem extends RelDataTypeSystemImpl {
  private static final int MAX_DECIMAL_PRECISION = 38;
  private static final int MAX_DECIMAL_SCALE = 38;
  private static final int DEFAULT_DECIMAL_PRECISION = 10;
  private static final int MAX_CHAR_PRECISION = Integer.MAX_VALUE;
  private static final int DEFAULT_VARCHAR_PRECISION = 65535;
  private static final int DEFAULT_CHAR_PRECISION = 255;
  private static final int MAX_BINARY_PRECISION = Integer.MAX_VALUE;
  private static final int MAX_TIMESTAMP_PRECISION = 9;
  private static final int DEFAULT_TINYINT_PRECISION = 3;
  private static final int DEFAULT_SMALLINT_PRECISION = 5;
  private static final int DEFAULT_INTEGER_PRECISION = 10;
  private static final int DEFAULT_BIGINT_PRECISION = 19;

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
      case TINYINT:
        return DEFAULT_TINYINT_PRECISION;
      case SMALLINT:
        return DEFAULT_SMALLINT_PRECISION;
      case INTEGER:
        return DEFAULT_INTEGER_PRECISION;
      case BIGINT:
        return DEFAULT_BIGINT_PRECISION;
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

  @Override
  public RelDataType deriveDecimalDivideType(RelDataTypeFactory typeFactory, RelDataType type1, RelDataType type2) {
    if (SqlTypeUtil.isExactNumeric(type1) && SqlTypeUtil.isExactNumeric(type2)) {
      if (SqlTypeUtil.isDecimal(type1) || SqlTypeUtil.isDecimal(type2)) {
        return super.deriveDecimalDivideType(typeFactory, type1, type2);
      } else {
        return nullableType(typeFactory, SqlTypeName.DOUBLE);
      }
    }
    return null;
  }

  @Override
  public RelDataType deriveDecimalMultiplyType(RelDataTypeFactory typeFactory, RelDataType type1, RelDataType type2) {
    if (SqlTypeUtil.isExactNumeric(type1) && SqlTypeUtil.isExactNumeric(type2)) {
      if (SqlTypeUtil.isDecimal(type1) || SqlTypeUtil.isDecimal(type2)) {
        return super.deriveDecimalMultiplyType(typeFactory, type1, type2);
      } else if (SqlTypeUtil.isBigint(type1) || SqlTypeUtil.isBigint(type2)) {
        return nullableType(typeFactory, SqlTypeName.BIGINT);
      }
    }
    return null;
  }

  private RelDataType nullableType(RelDataTypeFactory typeFactory, SqlTypeName typeName) {
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(typeName), true);
  }
}
