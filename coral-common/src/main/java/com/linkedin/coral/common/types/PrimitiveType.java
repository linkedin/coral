/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a primitive data type in the Coral type system.
 * This includes basic types like BOOLEAN, INT, DOUBLE, STRING, etc.
 */
public final class PrimitiveType implements CoralDataType {
  private final CoralTypeKind kind;
  private final boolean nullable;

  /**
   * Creates a new primitive type.
   * @param kind the type kind (must be a primitive type)
   * @param nullable whether this type allows null values
   */
  public static PrimitiveType of(CoralTypeKind kind, boolean nullable) {
    return new PrimitiveType(kind, nullable);
  }

  private PrimitiveType(CoralTypeKind kind, boolean nullable) {
    this.kind = Objects.requireNonNull(kind, "Type kind cannot be null");
    this.nullable = nullable;
  }

  @Override
  public CoralTypeKind getKind() {
    return kind;
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
    PrimitiveType that = (PrimitiveType) o;
    return nullable == that.nullable && kind == that.kind;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, nullable);
  }

  @Override
  public String toString() {
    return kind.name() + (nullable ? " NULL" : " NOT NULL");
  }
}
