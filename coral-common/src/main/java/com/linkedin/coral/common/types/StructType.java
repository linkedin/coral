/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Represents a struct data type in the Coral type system.
 */
public final class StructType implements CoralDataType {
  private final List<StructField> fields;
  private final boolean nullable;

  /**
   * Creates a new struct type.
   * @param fields the fields in the struct
   * @param nullable whether this type allows null values
   */
  public static StructType of(List<StructField> fields, boolean nullable) {
    return new StructType(fields, nullable);
  }

  private StructType(List<StructField> fields, boolean nullable) {
    this.fields = Collections.unmodifiableList(Objects.requireNonNull(fields, "Fields list cannot be null"));
    this.nullable = nullable;
  }

  /**
   * Returns the fields in this struct.
   * @return an unmodifiable list of fields
   */
  public List<StructField> getFields() {
    return fields;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.STRUCT;
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
    StructType that = (StructType) o;
    return nullable == that.nullable && Objects.equals(fields, that.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields, nullable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("STRUCT<");
    for (int i = 0; i < fields.size(); i++) {
      if (i > 0)
        sb.append(",");
      sb.append(fields.get(i));
    }
    sb.append(">");
    sb.append(nullable ? " NULL" : " NOT NULL");
    return sb.toString();
  }
}
