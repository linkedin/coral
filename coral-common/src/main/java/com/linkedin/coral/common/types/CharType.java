/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a fixed-length character data type in the Coral type system.
 */
public final class CharType implements CoralDataType {
  private final int length;
  private final boolean nullable;

  /**
   * Creates a new CHAR type.
   * @param length the fixed length of the character string
   * @param nullable whether this type allows null values
   */
  public static CharType of(int length, boolean nullable) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be positive, got: " + length);
    }
    return new CharType(length, nullable);
  }

  private CharType(int length, boolean nullable) {
    this.length = length;
    this.nullable = nullable;
  }

  /**
   * Returns the fixed length of this CHAR type.
   * @return the length
   */
  public int getLength() {
    return length;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.CHAR;
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
    CharType that = (CharType) o;
    return length == that.length && nullable == that.nullable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, nullable);
  }

  @Override
  public String toString() {
    return "CHAR(" + length + ")" + (nullable ? " NULL" : " NOT NULL");
  }
}
