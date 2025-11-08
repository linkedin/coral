/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a decimal data type with precision and scale in the Coral type system.
 */
public final class DecimalType implements CoralDataType {
  private final int precision;
  private final int scale;
  private final boolean nullable;

  /**
   * Creates a new decimal type.
   * @param precision the total number of digits
   * @param scale the number of digits after the decimal point
   * @param nullable whether this type allows null values
   */
  public static DecimalType of(int precision, int scale, boolean nullable) {
    if (precision <= 0) {
      throw new IllegalArgumentException("Precision must be positive, got: " + precision);
    }
    if (scale < 0 || scale > precision) {
      throw new IllegalArgumentException(
          "Scale must be non-negative and <= precision, got scale=" + scale + ", precision=" + precision);
    }
    return new DecimalType(precision, scale, nullable);
  }

  private DecimalType(int precision, int scale, boolean nullable) {
    this.precision = precision;
    this.scale = scale;
    this.nullable = nullable;
  }

  /**
   * Returns the precision (total number of digits).
   * @return the precision
   */
  public int getPrecision() {
    return precision;
  }

  /**
   * Returns the scale (number of digits after decimal point).
   * @return the scale
   */
  public int getScale() {
    return scale;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.DECIMAL;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DecimalType that = (DecimalType) o;
    return precision == that.precision && scale == that.scale && nullable == that.nullable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(precision, scale, nullable);
  }

  @Override
  public String toString() {
    return "DECIMAL(" + precision + "," + scale + ")" + (nullable ? " NULL" : " NOT NULL");
  }
}
