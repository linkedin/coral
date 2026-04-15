/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.spi;

import com.linkedin.coral.benchmark.data.ExplainResult;
import com.linkedin.coral.benchmark.data.ResultSet;
import com.linkedin.coral.benchmark.data.RowSet;
import com.linkedin.coral.common.types.CoralDataType;


/**
 * SPI for plugging a query execution engine into the benchmark framework.
 *
 * <p>Engine plugins are only required for {@link VerificationLevel#EXPLAIN} and
 * {@link VerificationLevel#RESULT_SET}. Tests at {@link VerificationLevel#TRANSLATION}
 * do not use engines at all.
 *
 * <p>Expected implementations:
 * <ul>
 *   <li>Spark: embedded {@code SparkSession}</li>
 *   <li>Trino: Trino test harness with in-memory connector</li>
 *   <li>Hive: embedded HiveServer2 or Tez local mode</li>
 * </ul>
 *
 * <p>Lifecycle: {@link #start()} is called once before any queries are executed,
 * and {@link #stop()} is called once after all queries complete. Between those calls,
 * {@link #createTable}, {@link #loadData}, {@link #explain}, and {@link #execute}
 * may be called in any order and any number of times.
 */
public interface EnginePlugin {

  /**
   * Returns the dialect this engine natively executes.
   *
   * @return the dialect identifier
   */
  Dialect dialect();

  /**
   * Starts the engine. Called once before any queries are executed.
   * May be a no-op for engines that are always available (e.g., remote services).
   */
  void start();

  /**
   * Creates a table in the engine's catalog with the given schema.
   * The table must be queryable after this call returns (though it may be empty
   * until {@link #loadData} is called).
   *
   * @param namespace the namespace (database) name
   * @param tableName the table name
   * @param schema    the table schema as a Coral {@link CoralDataType} (typically a
   *                  {@link com.linkedin.coral.common.types.StructType})
   */
  void createTable(String namespace, String tableName, CoralDataType schema);

  /**
   * Loads row data into a previously created table.
   *
   * @param namespace the namespace (database) name
   * @param tableName the table name
   * @param data      the rows to load
   * @throws IllegalStateException if the table has not been created via {@link #createTable}
   */
  void loadData(String namespace, String tableName, RowSet data);

  /**
   * Runs EXPLAIN on the given SQL and returns the result.
   * This validates that the engine can parse and plan the query without executing it.
   *
   * @param sql a SQL string in this engine's native dialect
   * @return the explain result indicating success or failure with details
   */
  ExplainResult explain(String sql);

  /**
   * Executes the given SQL query and returns the result set.
   *
   * @param sql a SQL string in this engine's native dialect
   * @return the query result set
   */
  ResultSet execute(String sql);

  /**
   * Stops and tears down the engine. Called once after all queries complete.
   * Releases any resources acquired in {@link #start()}.
   */
  void stop();
}
