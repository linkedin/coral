/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a BINARY type with optional length in the Coral type system.
 *
 * Length indicates:
 *  - -1: unbounded/variable-length (LENGTH_UNBOUNDED, default for BINARY/VARBINARY)
 *  -  0: fixed-length (for FIXED types, e.g., Iceberg FIXED(16))
 */
public final class BinaryType implements CoralDataType {
  /** Constant for unbounded/variable-length binary */
  public static final int LENGTH_UNBOUNDED = -1;

  private final int length;
  private final boolean nullable;

  /**
   * Create a BINARY type with the given length and nullability.
   * @param length fixed length (greater than 0) or -1 for unbounded
   * @param nullable whether this type allows null values
   */
  public static BinaryType of(int length, boolean nullable) {
    if (length != LENGTH_UNBOUNDED && length <= 0) {
      throw new IllegalArgumentException("Binary length must be -1 (unbounded) or > 0, got: " + length);
    }
    return new BinaryType(length, nullable);
  }

  private BinaryType(int length, boolean nullable) {
    this.length = length;
    this.nullable = nullable;
  }

  /**
   * @return the length (-1 for unbounded, or 0 for fixed-length)
   */
  public int getLength() {
    return length;
  }

  /**
   * @return true if this is fixed-length binary, false if unbounded
   */
  public boolean isFixedLength() {
    return length > 0;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.BINARY;
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
    BinaryType that = (BinaryType) o;
    return length == that.length && nullable == that.nullable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, nullable);
  }

  @Override
  public String toString() {
    String lengthStr = length == LENGTH_UNBOUNDED ? "" : "(" + length + ")";
    return "BINARY" + lengthStr + (nullable ? " NULL" : " NOT NULL");
  }
}
