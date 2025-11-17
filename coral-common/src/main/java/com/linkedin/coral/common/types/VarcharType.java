/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a variable-length character data type in the Coral type system.
 */
public final class VarcharType implements CoralDataType {
  private final int length;
  private final boolean nullable;

  /**
   * Creates a new VARCHAR type.
   * @param length the maximum length of the character string
   * @param nullable whether this type allows null values
   */
  public static VarcharType of(int length, boolean nullable) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be positive, got: " + length);
    }
    return new VarcharType(length, nullable);
  }

  private VarcharType(int length, boolean nullable) {
    this.length = length;
    this.nullable = nullable;
  }

  /**
   * Returns the maximum length of this VARCHAR type.
   * @return the length
   */
  public int getLength() {
    return length;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.VARCHAR;
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
    VarcharType that = (VarcharType) o;
    return length == that.length && nullable == that.nullable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, nullable);
  }

  @Override
  public String toString() {
    return "VARCHAR(" + length + ")" + (nullable ? " NULL" : " NOT NULL");
  }
}
