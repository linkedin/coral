/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

/**
 * Exception indicating failure to resolve user-defined or builtin
 * function name in SQL
 */
public class UnknownSqlFunctionException extends RuntimeException {
  private final String functionName;

  /**
   * Constructs an exception indicating unknown function name
   * @param functionName function name that could not be resolved
   */
  public UnknownSqlFunctionException(String functionName) {
    super(String.format("Unknown function name: %s", functionName));
    this.functionName = functionName;
  }

  public String getFunctionName() {
    return functionName;
  }
}
