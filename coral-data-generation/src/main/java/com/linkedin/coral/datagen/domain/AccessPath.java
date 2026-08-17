/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Path to a value reachable from a top-level column via struct field, map key, and array
 * index accesses.
 *
 * Examples:
 * - Flat column: AccessPath.of(0) represents column $0
 * - Struct field access: AccessPath.ofField(3, "name") represents $3.name
 * - Array element access: AccessPath.ofArrayIndex(2, 1) represents $2[1]
 * - Map element access: AccessPath.ofMapKey(4, "key1") represents $4['key1']
 * - Nested: AccessPath.of(5).append(arrayIndex(1)).append(field("name")) represents $5[1].name
 */
public class AccessPath {
  private final int columnIndex;
  private final List<PathElement> path;

  /**
   * Represents a single step in a nested access path.
   */
  public static class PathElement {
    public enum Kind {
      FIELD,
      ARRAY_INDEX,
      MAP_KEY
    }

    private final Kind kind;
    private final String fieldName;
    private final int arrayIndex;
    private final String mapKey;

    private PathElement(Kind kind, String fieldName, int arrayIndex, String mapKey) {
      this.kind = kind;
      this.fieldName = fieldName;
      this.arrayIndex = arrayIndex;
      this.mapKey = mapKey;
    }

    public static PathElement field(String name) {
      return new PathElement(Kind.FIELD, name, -1, null);
    }

    public static PathElement arrayIndex(int index) {
      return new PathElement(Kind.ARRAY_INDEX, null, index, null);
    }

    public static PathElement mapKey(String key) {
      return new PathElement(Kind.MAP_KEY, null, -1, key);
    }

    public Kind getKind() {
      return kind;
    }

    public String getFieldName() {
      return fieldName;
    }

    public int getArrayIndex() {
      return arrayIndex;
    }

    public String getMapKey() {
      return mapKey;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      PathElement that = (PathElement) o;
      return kind == that.kind && arrayIndex == that.arrayIndex && Objects.equals(fieldName, that.fieldName)
          && Objects.equals(mapKey, that.mapKey);
    }

    @Override
    public int hashCode() {
      return Objects.hash(kind, fieldName, arrayIndex, mapKey);
    }

    @Override
    public String toString() {
      switch (kind) {
        case FIELD:
          return "." + fieldName;
        case ARRAY_INDEX:
          return "[" + arrayIndex + "]";
        case MAP_KEY:
          return "['" + mapKey + "']";
        default:
          return "?";
      }
    }
  }

  private AccessPath(int columnIndex, List<PathElement> path) {
    this.columnIndex = columnIndex;
    this.path = Collections.unmodifiableList(path);
  }

  /**
   * Creates a path for a flat column reference.
   */
  public static AccessPath of(int colIdx) {
    return new AccessPath(colIdx, Collections.emptyList());
  }

  /**
   * Creates a path for a struct field access (e.g., $3.name).
   */
  public static AccessPath ofField(int colIdx, String fieldName) {
    return new AccessPath(colIdx, Collections.singletonList(PathElement.field(fieldName)));
  }

  /**
   * Creates a path for an array element access (e.g., $2[1]).
   */
  public static AccessPath ofArrayIndex(int colIdx, int index) {
    return new AccessPath(colIdx, Collections.singletonList(PathElement.arrayIndex(index)));
  }

  /**
   * Creates a path for a map element access (e.g., $4['key1']).
   */
  public static AccessPath ofMapKey(int colIdx, String key) {
    return new AccessPath(colIdx, Collections.singletonList(PathElement.mapKey(key)));
  }

  /**
   * Creates a new AccessPath by appending a path element to this path.
   */
  public AccessPath append(PathElement element) {
    List<PathElement> newPath = new ArrayList<>(path);
    newPath.add(element);
    return new AccessPath(columnIndex, newPath);
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public List<PathElement> getPath() {
    return path;
  }

  public boolean isFlat() {
    return path.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    AccessPath that = (AccessPath) o;
    return columnIndex == that.columnIndex && path.equals(that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnIndex, path);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("$" + columnIndex);
    for (PathElement elem : path) {
      sb.append(elem);
    }
    return sb.toString();
  }
}
