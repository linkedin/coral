/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.suite;

import java.util.Objects;
import java.util.Optional;

import com.linkedin.coral.benchmark.comparison.ComparisonResult;
import com.linkedin.coral.benchmark.data.ExplainResult;
import com.linkedin.coral.benchmark.spi.VerificationLevel;


/**
 * The result of running a single query through the benchmark pipeline.
 *
 * <p>Contains the source SQL, translated SQL (if translation succeeded), and verification
 * outcomes at each level that was executed. Tracks which verification level was requested
 * and where the pipeline succeeded or failed.
 */
public final class QueryTestResult {

  /**
   * Outcome status for a single query test.
   */
  public enum Status {
    /** All requested verification levels passed. */
    PASS,
    /** A verification level failed (translation error, explain failure, result mismatch,
     *  or unexpected exception). */
    FAIL
  }

  /**
   * Category of failure, indicating which stage of the pipeline failed.
   * Only meaningful when {@link #getStatus()} is {@link Status#FAIL}.
   */
  public enum FailureCategory {
    /** The translation from source dialect through IR to target dialect failed. */
    TRANSLATION_ERROR,
    /** Translation succeeded but the target engine could not EXPLAIN the query. */
    EXPLAIN_FAILURE,
    /** Translation and EXPLAIN succeeded but result sets from the two engines differ. */
    RESULT_MISMATCH
  }

  private final String queryName;
  private final String sourceSql;
  private final VerificationLevel requestedLevel;
  private final Status status;
  private final String translatedSql;
  private final ExplainResult explainResult;
  private final ComparisonResult comparisonResult;
  private final FailureCategory failureCategory;
  private final String errorMessage;
  private final Exception exception;

  private QueryTestResult(Builder builder) {
    this.queryName = Objects.requireNonNull(builder.queryName);
    this.sourceSql = Objects.requireNonNull(builder.sourceSql);
    this.requestedLevel = Objects.requireNonNull(builder.requestedLevel);
    this.status = Objects.requireNonNull(builder.status);
    this.translatedSql = builder.translatedSql;
    this.explainResult = builder.explainResult;
    this.comparisonResult = builder.comparisonResult;
    this.failureCategory = builder.failureCategory;
    this.errorMessage = builder.errorMessage;
    this.exception = builder.exception;
  }

  /** Returns the query file name (without path or extension). */
  public String getQueryName() {
    return queryName;
  }

  /** Returns the original SQL in the source dialect. */
  public String getSourceSql() {
    return sourceSql;
  }

  /** Returns the verification level that was requested for this query. */
  public VerificationLevel getRequestedLevel() {
    return requestedLevel;
  }

  /** Returns the overall status of this query test. */
  public Status getStatus() {
    return status;
  }

  /** Returns the SQL translated to the target dialect, if translation succeeded. */
  public Optional<String> getTranslatedSql() {
    return Optional.ofNullable(translatedSql);
  }

  /** Returns the EXPLAIN result, if EXPLAIN verification was performed. */
  public Optional<ExplainResult> getExplainResult() {
    return Optional.ofNullable(explainResult);
  }

  /** Returns the result-set comparison result, if result-set verification was performed. */
  public Optional<ComparisonResult> getComparisonResult() {
    return Optional.ofNullable(comparisonResult);
  }

  /** Returns the failure category, if the test failed. */
  public Optional<FailureCategory> getFailureCategory() {
    return Optional.ofNullable(failureCategory);
  }

  /** Returns the error message, if the test failed. */
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  /** Returns the exception, if one was thrown during testing. */
  public Optional<Exception> getException() {
    return Optional.ofNullable(exception);
  }

  /**
   * Creates a new builder.
   *
   * @param queryName      the query file name
   * @param sourceSql      the original SQL
   * @param requestedLevel the verification level requested
   * @return a new builder
   */
  public static Builder builder(String queryName, String sourceSql, VerificationLevel requestedLevel) {
    return new Builder(queryName, sourceSql, requestedLevel);
  }

  /**
   * Builder for {@link QueryTestResult}.
   */
  public static final class Builder {
    private final String queryName;
    private final String sourceSql;
    private final VerificationLevel requestedLevel;
    private Status status;
    private String translatedSql;
    private ExplainResult explainResult;
    private ComparisonResult comparisonResult;
    private FailureCategory failureCategory;
    private String errorMessage;
    private Exception exception;

    private Builder(String queryName, String sourceSql, VerificationLevel requestedLevel) {
      this.queryName = queryName;
      this.sourceSql = sourceSql;
      this.requestedLevel = requestedLevel;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }

    public Builder translatedSql(String translatedSql) {
      this.translatedSql = translatedSql;
      return this;
    }

    public Builder explainResult(ExplainResult explainResult) {
      this.explainResult = explainResult;
      return this;
    }

    public Builder comparisonResult(ComparisonResult comparisonResult) {
      this.comparisonResult = comparisonResult;
      return this;
    }

    public Builder failureCategory(FailureCategory category) {
      this.failureCategory = category;
      return this;
    }

    public Builder errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public Builder exception(Exception exception) {
      this.exception = exception;
      return this;
    }

    public QueryTestResult build() {
      return new QueryTestResult(this);
    }
  }
}
