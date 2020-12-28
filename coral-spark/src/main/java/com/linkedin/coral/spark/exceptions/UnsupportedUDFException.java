/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.exceptions;

/**
 * [CORAL-75] Creating a new exception type for UDFs which proactively triggers View Analysis failure.
 * Tracking this error can help in prioritizing onboarding unsupported UDFs
 */
public class UnsupportedUDFException extends RuntimeException {

  private final String functionName;

  public UnsupportedUDFException(String functionName) {
    this.functionName = functionName;
  }

  @Override
  public String getMessage() {
    return String.format("Coral Spark does not support following UDF: %s", functionName);
  }
}
