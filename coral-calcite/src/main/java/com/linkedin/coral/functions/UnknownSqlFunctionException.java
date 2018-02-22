package com.linkedin.coral.functions;

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
