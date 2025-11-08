/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents an array data type in the Coral type system.
 */
public final class ArrayType implements CoralDataType {
  private final CoralDataType elementType;
  private final boolean nullable;

  /**
   * Creates a new array type.
   * @param elementType the type of elements in the array
   * @param nullable whether this type allows null values
   */
  public static ArrayType of(CoralDataType elementType, boolean nullable) {
    return new ArrayType(elementType, nullable);
  }

  private ArrayType(CoralDataType elementType, boolean nullable) {
    this.elementType = Objects.requireNonNull(elementType, "Element type cannot be null");
    this.nullable = nullable;
  }

  /**
   * Returns the type of elements in this array.
   * @return the element type
   */
  public CoralDataType getElementType() {
    return elementType;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.ARRAY;
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
    ArrayType that = (ArrayType) o;
    return nullable == that.nullable && Objects.equals(elementType, that.elementType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementType, nullable);
  }

  @Override
  public String toString() {
    return "ARRAY<" + elementType + ">" + (nullable ? " NULL" : " NOT NULL");
  }
}
