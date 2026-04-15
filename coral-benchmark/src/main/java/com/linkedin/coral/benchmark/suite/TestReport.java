/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.suite;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.linkedin.coral.benchmark.spi.Dialect;
import com.linkedin.coral.benchmark.spi.VerificationLevel;


/**
 * Aggregate results of running a {@link TranslationTestSuite}.
 *
 * <p>Provides per-query results and aggregate statistics: pass rate, failure counts
 * by category (translation error, explain failure, result mismatch), and the
 * dialect pair and verification level that were tested.
 */
public final class TestReport {

  private final Dialect sourceDialect;
  private final Dialect targetDialect;
  private final VerificationLevel verificationLevel;
  private final List<QueryTestResult> queryResults;

  /**
   * Creates a new test report.
   *
   * @param sourceDialect     the source dialect
   * @param targetDialect     the target dialect
   * @param verificationLevel the verification level that was used
   * @param queryResults      the per-query results
   */
  public TestReport(Dialect sourceDialect, Dialect targetDialect, VerificationLevel verificationLevel,
      List<QueryTestResult> queryResults) {
    this.sourceDialect = Objects.requireNonNull(sourceDialect);
    this.targetDialect = Objects.requireNonNull(targetDialect);
    this.verificationLevel = Objects.requireNonNull(verificationLevel);
    this.queryResults = Collections.unmodifiableList(Objects.requireNonNull(queryResults));
  }

  /** Returns the source dialect that was tested. */
  public Dialect getSourceDialect() {
    return sourceDialect;
  }

  /** Returns the target dialect that was tested. */
  public Dialect getTargetDialect() {
    return targetDialect;
  }

  /** Returns the verification level that was used. */
  public VerificationLevel getVerificationLevel() {
    return verificationLevel;
  }

  /** Returns the per-query results. */
  public List<QueryTestResult> getQueryResults() {
    return queryResults;
  }

  /** Returns the total number of queries tested. */
  public int totalCount() {
    return queryResults.size();
  }

  /** Returns the number of queries that passed. */
  public int passCount() {
    return countByStatus(QueryTestResult.Status.PASS);
  }

  /** Returns the number of queries that failed. */
  public int failCount() {
    return countByStatus(QueryTestResult.Status.FAIL);
  }

  /**
   * Returns the pass rate as a value between 0.0 and 1.0.
   *
   * @return the pass rate, or 0.0 if no queries were tested
   */
  public double passRate() {
    return totalCount() > 0 ? (double) passCount() / totalCount() : 0.0;
  }

  /** Returns only the results that failed. */
  public List<QueryTestResult> getFailures() {
    return queryResults.stream().filter(r -> r.getStatus() == QueryTestResult.Status.FAIL).collect(Collectors.toList());
  }

  /**
   * Returns failure counts broken down by category: translation error, explain failure,
   * and result mismatch. Only includes results with status FAIL that have a
   * failure category set.
   *
   * @return a map from failure category to the count of queries in that category
   */
  public Map<QueryTestResult.FailureCategory, Integer> failureCountsByCategory() {
    Map<QueryTestResult.FailureCategory, Integer> counts = new LinkedHashMap<>();
    for (QueryTestResult.FailureCategory category : QueryTestResult.FailureCategory.values()) {
      counts.put(category, 0);
    }
    for (QueryTestResult result : queryResults) {
      result.getFailureCategory().ifPresent(category -> counts.merge(category, 1, Integer::sum));
    }
    return Collections.unmodifiableMap(counts);
  }

  /**
   * Returns the results that failed with a specific failure category.
   *
   * @param category the failure category to filter by
   * @return the matching results
   */
  public List<QueryTestResult> getFailuresByCategory(QueryTestResult.FailureCategory category) {
    Objects.requireNonNull(category);
    return queryResults.stream().filter(r -> r.getFailureCategory().orElse(null) == category)
        .collect(Collectors.toList());
  }

  private int countByStatus(QueryTestResult.Status status) {
    return (int) queryResults.stream().filter(r -> r.getStatus() == status).count();
  }
}
