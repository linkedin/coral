/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools.exceptions;

/**
 * MismatchedSchemaException indicates that there exists diffs between schemas that should be equivalent.
 */
public class MismatchedSchemaException extends RuntimeException {

  private static final String MISMATCHED_SCHEMA_TEMPLATE =
      "Schemas that should be equivalent are mismatched.\nExpected Schema:\n%s\nActual Schema:\n%s";

  public MismatchedSchemaException(String expectedSchema, String actualSchema) {
    super(String.format(MISMATCHED_SCHEMA_TEMPLATE, expectedSchema, actualSchema));
  }
}
