/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

/**
 * Escalating levels of verification for cross-dialect translation tests.
 * Each level subsumes the ones before it.
 *
 * <ul>
 *   <li>{@link #TRANSLATION} — Verifies that the translation pipeline (source SQL -> IR -> target SQL)
 *       completes without error and produces non-empty output.</li>
 *   <li>{@link #EXPLAIN} — Additionally runs the translated SQL through the target engine's EXPLAIN
 *       to validate syntax and query planning against the declared schema.</li>
 *   <li>{@link #RESULT_SET} — Additionally loads test data into both engines, executes the queries,
 *       and compares result sets for semantic equivalence.</li>
 * </ul>
 */
public enum VerificationLevel {
  /** Verify that translation from source to target dialect completes without error. */
  TRANSLATION,

  /** Verify translation + target engine can EXPLAIN the translated query. */
  EXPLAIN,

  /** Verify translation + EXPLAIN + result sets from both engines are equivalent. */
  RESULT_SET
}
