/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.data;

import java.util.Objects;
import java.util.Optional;


/**
 * The result of running EXPLAIN on a SQL query through an engine.
 *
 * <p>Captures whether the engine could successfully parse and plan the query.
 * On success, optionally includes the query plan text. On failure, includes
 * the error message.
 */
public final class ExplainResult {

  private final boolean success;
  private final String plan;
  private final String errorMessage;
  private final Exception exception;

  private ExplainResult(boolean success, String plan, String errorMessage, Exception exception) {
    this.success = success;
    this.plan = plan;
    this.errorMessage = errorMessage;
    this.exception = exception;
  }

  /**
   * Creates a successful explain result.
   *
   * @param plan the query plan text produced by the engine (may be null if the engine
   *             does not expose plan text)
   * @return a successful result
   */
  public static ExplainResult success(String plan) {
    return new ExplainResult(true, plan, null, null);
  }

  /**
   * Creates a failed explain result.
   *
   * @param errorMessage a human-readable description of the failure
   * @param exception    the exception thrown by the engine, or null
   * @return a failed result
   */
  public static ExplainResult failure(String errorMessage, Exception exception) {
    Objects.requireNonNull(errorMessage, "Error message cannot be null for a failure");
    return new ExplainResult(false, null, errorMessage, exception);
  }

  /**
   * Returns whether the EXPLAIN succeeded.
   *
   * @return true if the engine could parse and plan the query
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Returns the query plan text, if available.
   *
   * @return the plan text, or empty if not available or explain failed
   */
  public Optional<String> getPlan() {
    return Optional.ofNullable(plan);
  }

  /**
   * Returns the error message, if the explain failed.
   *
   * @return the error message, or empty if explain succeeded
   */
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  /**
   * Returns the exception that caused the failure, if any.
   *
   * @return the exception, or empty if explain succeeded or no exception was captured
   */
  public Optional<Exception> getException() {
    return Optional.ofNullable(exception);
  }
}
