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
