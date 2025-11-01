/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

/**
 * Enumeration of all supported data type kinds in the Coral type system.
 * This provides a comprehensive set of primitive and complex types that
 * can be mapped to various execution engines.
 */
public enum CoralTypeKind {
  // Primitive numeric types
  BOOLEAN,
  TINYINT,
  SMALLINT,
  INT,
  BIGINT,
  FLOAT,
  DOUBLE,
  DECIMAL,

  // String and character types
  CHAR,
  VARCHAR,
  STRING,

  // Date and time types
  DATE,
  TIME,
  TIMESTAMP,

  // Binary types
  BINARY,

  // Complex types
  ARRAY,
  MAP,
  STRUCT
}
