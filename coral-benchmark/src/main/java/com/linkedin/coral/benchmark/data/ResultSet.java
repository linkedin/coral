/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.linkedin.coral.common.types.StructType;


/**
 * The result of executing a SQL query on an engine.
 *
 * <p>Contains the output schema (column names and types) and the rows produced
 * by the query. Used as input to {@link com.linkedin.coral.benchmark.comparison.ResultSetComparator}
 * for cross-engine result comparison.
 */
public final class ResultSet {

  private final StructType schema;
  private final List<Object[]> rows;

  private ResultSet(StructType schema, List<Object[]> rows) {
    this.schema = Objects.requireNonNull(schema, "Schema cannot be null");
    this.rows = Collections.unmodifiableList(rows);
  }

  /**
   * Returns the schema of the result set columns.
   *
   * @return the struct type describing the output columns
   */
  public StructType getSchema() {
    return schema;
  }

  /**
   * Returns the result rows. Each row is an Object array with one element per column,
   * following the same type mapping as {@link RowSet}.
   *
   * @return an unmodifiable list of rows
   */
  public List<Object[]> getRows() {
    return rows;
  }

  /**
   * Returns the number of rows in this result set.
   *
   * @return the row count
   */
  public int size() {
    return rows.size();
  }

  /**
   * Creates a new ResultSet.
   *
   * @param schema the output schema
   * @param rows   the result rows
   * @return a new ResultSet
   */
  public static ResultSet of(StructType schema, List<Object[]> rows) {
    return new ResultSet(schema, new ArrayList<>(rows));
  }
}
