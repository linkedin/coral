/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.common.catalog.CoralCatalog;


/**
 * SPI for plugging a SQL dialect into the benchmark framework.
 *
 * <p>Each implementation wraps the existing Coral converters for a specific dialect:
 * <ul>
 *   <li>Hive: {@code HiveToRelConverter} / {@code CoralRelToSqlNodeConverter}</li>
 *   <li>Trino: {@code TrinoToRelConverter} / {@code RelToTrinoConverter}</li>
 *   <li>Spark: {@code HiveToRelConverter} (Spark SQL parses as Hive) / {@code CoralSpark}</li>
 * </ul>
 *
 * <p>Implementations can be registered explicitly via
 * {@link com.linkedin.coral.benchmark.suite.TranslationTestSuite.Builder} or discovered
 * via {@link java.util.ServiceLoader}.
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
   * @param sql    a SELECT statement in this dialect's syntax
   * @param catalog the catalog providing table metadata for query resolution
   * @return the Calcite {@link RelNode} representing the Coral intermediate representation
   * @throws IllegalArgumentException if the SQL cannot be parsed in this dialect
   */
  RelNode toRelNode(String sql, CoralCatalog catalog);

  /**
   * Converts a Coral IR {@link RelNode} into a SQL string in this dialect.
   *
   * @param relNode the Coral intermediate representation
   * @param catalog the catalog providing table metadata (needed by some converters
   *                for UDF resolution or type mapping)
   * @return a SQL string in this dialect's syntax
   * @throws UnsupportedOperationException if the IR contains constructs that cannot
   *         be expressed in this dialect
   */
  String toDialectSql(RelNode relNode, CoralCatalog catalog);
}
