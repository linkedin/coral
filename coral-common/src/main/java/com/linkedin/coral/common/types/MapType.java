/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Objects;


/**
 * Represents a map data type in the Coral type system.
 */
public final class MapType implements CoralDataType {
  private final CoralDataType keyType;
  private final CoralDataType valueType;
  private final boolean nullable;

  /**
   * Creates a new map type.
   * @param keyType the type of keys in the map
   * @param valueType the type of values in the map
   * @param nullable whether this type allows null values
   */
  public static MapType of(CoralDataType keyType, CoralDataType valueType, boolean nullable) {
    return new MapType(keyType, valueType, nullable);
  }

  private MapType(CoralDataType keyType, CoralDataType valueType, boolean nullable) {
    this.keyType = Objects.requireNonNull(keyType, "Key type cannot be null");
    this.valueType = Objects.requireNonNull(valueType, "Value type cannot be null");
    this.nullable = nullable;
  }

  /**
   * Returns the type of keys in this map.
   * @return the key type
   */
  public CoralDataType getKeyType() {
    return keyType;
  }

  /**
   * Returns the type of values in this map.
   * @return the value type
   */
  public CoralDataType getValueType() {
    return valueType;
  }

  @Override
  public CoralTypeKind getKind() {
    return CoralTypeKind.MAP;
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
    MapType that = (MapType) o;
    return nullable == that.nullable && Objects.equals(keyType, that.keyType)
        && Objects.equals(valueType, that.valueType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyType, valueType, nullable);
  }

  @Override
  public String toString() {
    return "MAP<" + keyType + "," + valueType + ">" + (nullable ? " NULL" : " NOT NULL");
  }
}
