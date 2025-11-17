/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a TIMESTAMP type with fractional second precision in the Coral type system.
 *
 * Precision indicates the number of fractional digits of seconds, e.g.:
 *  - 0: seconds
 *  - 3: milliseconds
 *  - 6: microseconds
 *  - 9: nanoseconds
 */
public final class TimestampType implements CoralDataType {
  private final int precision;
  private final boolean nullable;

  /**
   * Create a TIMESTAMP type with the given precision and nullability.
   * @param precision fractional second precision (0-9)
   * @param nullable whether this type allows null values
   */
  public static TimestampType of(int precision, boolean nullable) {
    if (precision < 0 || precision > 9) {
      throw new IllegalArgumentException("Timestamp precision must be in range [0, 9], got: " + precision);
    }
    return new TimestampType(precision, nullable);
  }

  private TimestampType(int precision, boolean nullable) {
    this.precision = precision;
    this.nullable = nullable;
  }

  /**
   * @return the fractional second precision (0-9)
   */
  public int getPrecision() {
    return precision;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.TIMESTAMP;
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
    TimestampType that = (TimestampType) o;
    return precision == that.precision && nullable == that.nullable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(precision, nullable);
  }

  @Override
  public String toString() {
    return "TIMESTAMP(" + precision + ")" + (nullable ? " NULL" : " NOT NULL");
  }
}
