/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.comparison;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * The outcome of comparing two result sets.
 *
 * <p>On equivalence, the result is simply {@link #isEquivalent()} == true.
 * On mismatch, it provides structured details about the differences found:
 * row count mismatch, schema mismatch, or per-row diffs.
 */
public final class ComparisonResult {

  private final boolean equivalent;
  private final String summary;
  private final List<String> diffs;

  private ComparisonResult(boolean equivalent, String summary, List<String> diffs) {
    this.equivalent = equivalent;
    this.summary = summary;
    this.diffs = Collections.unmodifiableList(diffs);
  }

  /**
   * Creates a result indicating the two result sets are equivalent.
   *
   * @return an equivalent result
   */
  public static ComparisonResult equivalent() {
    return new ComparisonResult(true, "Result sets are equivalent", Collections.emptyList());
  }

  /**
   * Creates a result indicating the two result sets differ.
   *
   * @param summary a human-readable summary of the mismatch
   * @param diffs   individual difference descriptions (e.g., per-row or per-column diffs)
   * @return a mismatch result
   */
  public static ComparisonResult mismatch(String summary, List<String> diffs) {
    Objects.requireNonNull(summary, "Summary cannot be null");
    Objects.requireNonNull(diffs, "Diffs list cannot be null");
    return new ComparisonResult(false, summary, diffs);
  }

  /**
   * Returns whether the two result sets are equivalent under the comparison config.
   *
   * @return true if equivalent
   */
  public boolean isEquivalent() {
    return equivalent;
  }

  /**
   * Returns a human-readable summary of the comparison outcome.
   *
   * @return the summary
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Returns individual difference descriptions. Empty if the result sets are equivalent.
   *
   * @return an unmodifiable list of diff descriptions
   */
  public List<String> getDiffs() {
    return diffs;
  }
}
