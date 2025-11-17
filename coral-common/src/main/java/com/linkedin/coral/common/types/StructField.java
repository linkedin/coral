/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a field in a struct data type in the Coral type system.
 */
public final class StructField {
  private final String name;
  private final CoralDataType type;

  /**
   * Creates a new struct field.
   * @param name the name of the field
   * @param type the type of the field
   */
  public static StructField of(String name, CoralDataType type) {
    return new StructField(name, type);
  }

  private StructField(String name, CoralDataType type) {
    this.name = Objects.requireNonNull(name, "Field name cannot be null");
    this.type = Objects.requireNonNull(type, "Field type cannot be null");
  }

  /**
   * Returns the name of this field.
   * @return the field name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the type of this field.
   * @return the field type
   */
  public CoralDataType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    StructField that = (StructField) o;
    return Objects.equals(name, that.name) && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public String toString() {
    return name + ": " + type;
  }
}
