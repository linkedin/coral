/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

/**
 * Supported SQL dialects in the Coral translation framework.
 *
 * <p>Each value corresponds to a SQL dialect that Coral can parse from or generate to.
 * A {@link DialectPlugin} provides the translation logic for a specific dialect,
 * while an {@link EnginePlugin} provides execution capabilities.
 */
public enum Dialect {
  HIVE,
  SPARK,
  TRINO
}
