/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools.exceptions;

/**
 * InvalidSchemaException indicates that a schema cannot be derived for some query.
 */
public class InvalidSchemaException extends RuntimeException {

  private static final String INVALID_SCHEMA_TEMPLATE = "Schema cannot be derived for query:\n%s";

  public InvalidSchemaException(String query) {
    super(String.format(INVALID_SCHEMA_TEMPLATE, query));
  }

}