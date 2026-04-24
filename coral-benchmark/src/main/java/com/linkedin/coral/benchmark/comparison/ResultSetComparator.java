/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.comparison;

import java.util.Objects;

import com.linkedin.coral.benchmark.data.ResultSet;


/**
 * Compares two {@link ResultSet}s for equivalence according to a {@link ComparisonConfig}.
 *
 * <p>Handles the real-world differences between engine outputs:
 * <ul>
 *   <li><b>Row ordering:</b> unordered (multiset) comparison by default; ordered only when
 *       {@link ComparisonConfig#isOrderedComparison()} is true.</li>
 *   <li><b>Floating-point tolerance:</b> FLOAT/DOUBLE values are compared using the
 *       configured epsilon.</li>
 *   <li><b>NULL equivalence:</b> two NULLs in the same column position are considered equal.</li>
 *   <li><b>Timestamp precision:</b> timestamps are normalized to the lower precision of
 *       the two result sets before comparison.</li>
 *   <li><b>Type widening:</b> when allowed, values are promoted to the wider type
 *       (e.g., INT vs BIGINT) before comparison.</li>
 * </ul>
 */
public final class ResultSetComparator {

  private final ComparisonConfig config;

  /**
   * Creates a comparator with the given configuration.
   *
   * @param config the comparison configuration
   */
  public ResultSetComparator(ComparisonConfig config) {
    this.config = Objects.requireNonNull(config, "ComparisonConfig cannot be null");
  }

  /**
   * Creates a comparator with default configuration.
   */
  public ResultSetComparator() {
    this(ComparisonConfig.defaults());
  }

  /**
   * Compares two result sets for equivalence.
   *
   * @param source the result set from the source engine
   * @param target the result set from the target engine
   * @return the comparison result indicating equivalence or detailing differences
   */
  public ComparisonResult compare(ResultSet source, ResultSet target) {
    Objects.requireNonNull(source, "Source result set cannot be null");
    Objects.requireNonNull(target, "Target result set cannot be null");

    // Implementation will:
    // 1. Compare schemas (column count, types with optional widening)
    // 2. Compare row counts
    // 3. Compare row contents (ordered or as multisets, per config)
    //    - Apply floating-point epsilon
    //    - Apply NULL equivalence
    //    - Normalize timestamp precision
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Returns the comparison configuration used by this comparator.
   *
   * @return the config
   */
  public ComparisonConfig getConfig() {
    return config;
  }
}
