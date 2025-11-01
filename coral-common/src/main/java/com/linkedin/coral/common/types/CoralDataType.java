/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

/**
 * Represents a data type in the Coral type system.
 * This interface provides a planner-agnostic abstraction for data types
 * that can be converted to various execution engine specific types.
 */
public interface CoralDataType {
  /**
   * Returns the kind of this data type.
   * @return the type kind
   */
  CoralTypeKind getKind();

  /**
   * Returns whether this data type allows null values.
   * @return true if nullable, false otherwise
   */
  boolean isNullable();
}
