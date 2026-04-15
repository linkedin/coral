/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.comparison;

/**
 * Configuration for how two {@link com.linkedin.coral.benchmark.data.ResultSet}s are compared.
 *
 * <p>Controls tolerances for the real-world differences between engine outputs:
 * floating-point precision, row ordering, NULL handling, timestamp precision,
 * and type widening.
 *
 * <p>Usage:
 * <pre>{@code
 * ComparisonConfig config = ComparisonConfig.builder()
 *     .floatingPointEpsilon(1e-6)
 *     .orderedComparison(false)
 *     .allowTypeWidening(true)
 *     .build();
 * }</pre>
 */
public final class ComparisonConfig {

  /** Default epsilon for FLOAT/DOUBLE comparisons. */
  public static final double DEFAULT_EPSILON = 1e-9;

  private final double floatingPointEpsilon;
  private final boolean orderedComparison;
  private final boolean allowTypeWidening;

  private ComparisonConfig(double floatingPointEpsilon, boolean orderedComparison, boolean allowTypeWidening) {
    this.floatingPointEpsilon = floatingPointEpsilon;
    this.orderedComparison = orderedComparison;
    this.allowTypeWidening = allowTypeWidening;
  }

  /**
   * Returns the epsilon used for FLOAT/DOUBLE equality comparisons.
   * Two floating-point values are considered equal if their absolute difference
   * is less than this epsilon.
   *
   * @return the epsilon
   */
  public double getFloatingPointEpsilon() {
    return floatingPointEpsilon;
  }

  /**
   * Returns whether rows should be compared in order.
   *
   * <p>When false (the default), result sets are compared as multisets (bags):
   * the same rows must appear the same number of times, regardless of order.
   * When true, row order must also match — use this only for queries with
   * an explicit ORDER BY.
   *
   * @return true if row order matters
   */
  public boolean isOrderedComparison() {
    return orderedComparison;
  }

  /**
   * Returns whether safe type widening is allowed during comparison.
   *
   * <p>When true, values are promoted to the wider type before comparison
   * (e.g., INT vs BIGINT, FLOAT vs DOUBLE). When false, any type mismatch
   * between corresponding columns is treated as a comparison failure.
   *
   * @return true if type widening is allowed
   */
  public boolean isAllowTypeWidening() {
    return allowTypeWidening;
  }

  /**
   * Returns a builder with default settings: unordered comparison, default epsilon,
   * type widening allowed.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns a config with all default settings.
   *
   * @return the default comparison config
   */
  public static ComparisonConfig defaults() {
    return new Builder().build();
  }

  /**
   * Builder for {@link ComparisonConfig}.
   */
  public static final class Builder {
    private double floatingPointEpsilon = DEFAULT_EPSILON;
    private boolean orderedComparison = false;
    private boolean allowTypeWidening = true;

    private Builder() {
    }

    /**
     * Sets the epsilon for floating-point comparisons.
     *
     * @param epsilon a non-negative tolerance value
     * @return this builder
     */
    public Builder floatingPointEpsilon(double epsilon) {
      if (epsilon < 0) {
        throw new IllegalArgumentException("Epsilon must be non-negative, got: " + epsilon);
      }
      this.floatingPointEpsilon = epsilon;
      return this;
    }

    /**
     * Sets whether row order matters.
     *
     * @param ordered true for ordered comparison, false for set comparison
     * @return this builder
     */
    public Builder orderedComparison(boolean ordered) {
      this.orderedComparison = ordered;
      return this;
    }

    /**
     * Sets whether safe type widening is allowed.
     *
     * @param allow true to allow widening (INT vs BIGINT, etc.)
     * @return this builder
     */
    public Builder allowTypeWidening(boolean allow) {
      this.allowTypeWidening = allow;
      return this;
    }

    /**
     * Builds the comparison config.
     *
     * @return an immutable ComparisonConfig
     */
    public ComparisonConfig build() {
      return new ComparisonConfig(floatingPointEpsilon, orderedComparison, allowTypeWidening);
    }
  }
}
