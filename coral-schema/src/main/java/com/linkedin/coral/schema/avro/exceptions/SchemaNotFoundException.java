/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro.exceptions;

public class SchemaNotFoundException extends RuntimeException {
  public SchemaNotFoundException(Throwable e) {
    super(e);
  }

  public SchemaNotFoundException(String msg, Throwable e) {
    super(msg, e);
  }

  public SchemaNotFoundException(String msg) {
    super(msg);
  }
}
