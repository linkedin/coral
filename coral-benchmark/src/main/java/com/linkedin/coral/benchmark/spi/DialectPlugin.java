/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

import org.apache.calcite.rel.RelNode;


/**
 * SPI for plugging a SQL dialect into the benchmark framework.
 *
 * <p>A {@code DialectPlugin} is a fully-constructed translator bound to a catalog. Instances
 * are produced by a {@link DialectPluginProvider}, which is the registration entry point
 * discovered via {@link java.util.ServiceLoader}. The factory split keeps plugins immutable
 * (catalog-bound at construction time, no two-phase {@code init}) while still supporting
 * standard SPI discovery, where the provider needs a no-arg constructor.
 *
 * <p>Each implementation wraps the existing Coral converters for a specific dialect:
 * <ul>
 *   <li>Hive: {@code HiveToRelConverter} / {@code CoralRelToSqlNodeConverter}</li>
 *   <li>Trino: {@code TrinoToRelConverter} / {@code RelToTrinoConverter}</li>
 *   <li>Spark: {@code HiveToRelConverter} (Spark SQL parses as Hive) / {@code CoralSpark}</li>
 * </ul>
 */
public interface DialectPlugin {

  /**
   * Returns the dialect this plugin handles.
   *
   * @return the dialect identifier
   */
  Dialect dialect();

  /**
   * Parses a SQL string written in this dialect and converts it to Coral IR.
   *
   * @param sql a SELECT statement in this dialect's syntax
   * @return the Calcite {@link RelNode} representing the Coral intermediate representation
   * @throws IllegalArgumentException if the SQL cannot be parsed in this dialect
   */
  RelNode toRelNode(String sql);

  /**
   * Converts a Coral IR {@link RelNode} into a SQL string in this dialect.
   *
   * @param relNode the Coral intermediate representation
   * @return a SQL string in this dialect's syntax
   * @throws UnsupportedOperationException if the IR contains constructs that cannot
   *         be expressed in this dialect
   */
  String toDialectSql(RelNode relNode);
}
