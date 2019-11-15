/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools.exceptions;

/**
 * ValidatorFailureException indicates that the validation mechanism failed before validating the query.
 */
public class ValidatorFailureException extends RuntimeException {

  public ValidatorFailureException(Exception e) {
    super(e);
  }

}
