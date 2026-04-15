/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.linkedin.coral.common.types.StructType;


/**
 * Typed tabular data for loading into engine tables during benchmark tests.
 *
 * <p>A RowSet is bound to a schema (a {@link StructType}) and contains zero or more rows.
 * Each row is an array of Java objects whose types correspond to the Coral type mapping:
 * <ul>
 *   <li>BOOLEAN -> {@link Boolean}</li>
 *   <li>TINYINT -> {@link Byte}</li>
 *   <li>SMALLINT -> {@link Short}</li>
 *   <li>INT -> {@link Integer}</li>
 *   <li>BIGINT -> {@link Long}</li>
 *   <li>FLOAT -> {@link Float}</li>
 *   <li>DOUBLE -> {@link Double}</li>
 *   <li>DECIMAL -> {@link java.math.BigDecimal}</li>
 *   <li>STRING/VARCHAR/CHAR -> {@link String}</li>
 *   <li>DATE -> {@link java.sql.Date}</li>
 *   <li>TIMESTAMP -> {@link java.sql.Timestamp}</li>
 *   <li>BINARY -> {@code byte[]}</li>
 *   <li>ARRAY -> {@link List}</li>
 *   <li>MAP -> {@link java.util.Map}</li>
 *   <li>STRUCT -> {@code Object[]}</li>
 * </ul>
 *
 * <p>Null values are represented by Java {@code null} and are only permitted for nullable columns.
 *
 * <p>Usage:
 * <pre>{@code
 * RowSet data = RowSet.builder(tableSchema)
 *     .addRow(1, "alice", Timestamp.valueOf("2024-01-15 10:00:00"), Arrays.asList("admin"))
 *     .addRow(2, "bob",   Timestamp.valueOf("2024-03-20 14:30:00"), Arrays.asList("user"))
 *     .build();
 * }</pre>
 */
public final class RowSet {

  private final StructType schema;
  private final List<Object[]> rows;

  private RowSet(StructType schema, List<Object[]> rows) {
    this.schema = Objects.requireNonNull(schema, "Schema cannot be null");
    this.rows = Collections.unmodifiableList(rows);
  }

  /**
   * Returns the schema that defines the column types for this row set.
   *
   * @return the struct type describing the columns
   */
  public StructType getSchema() {
    return schema;
  }

  /**
   * Returns the rows in this row set. Each row is an Object array with one element per column.
   *
   * @return an unmodifiable list of rows
   */
  public List<Object[]> getRows() {
    return rows;
  }

  /**
   * Returns the number of rows in this row set.
   *
   * @return the row count
   */
  public int size() {
    return rows.size();
  }

  /**
   * Creates a new builder for constructing a RowSet with the given schema.
   *
   * @param schema the table schema (must be a {@link StructType})
   * @return a new builder
   */
  public static Builder builder(StructType schema) {
    return new Builder(schema);
  }

  /**
   * Builder for constructing a {@link RowSet}.
   */
  public static final class Builder {

    private final StructType schema;
    private final int columnCount;
    private final List<Object[]> rows = new ArrayList<>();

    private Builder(StructType schema) {
      this.schema = Objects.requireNonNull(schema, "Schema cannot be null");
      this.columnCount = schema.getFields().size();
    }

    /**
     * Adds a row of values. The number of values must match the number of columns
     * in the schema. Values must be Java objects matching the Coral type mapping.
     *
     * @param values one value per column, in schema order
     * @return this builder
     * @throws IllegalArgumentException if the number of values does not match the schema
     */
    public Builder addRow(Object... values) {
      if (values.length != columnCount) {
        throw new IllegalArgumentException("Expected " + columnCount + " values, got " + values.length);
      }
      rows.add(Arrays.copyOf(values, values.length));
      return this;
    }

    /**
     * Builds the RowSet.
     *
     * @return an immutable RowSet
     */
    public RowSet build() {
      return new RowSet(schema, new ArrayList<>(rows));
    }
  }
}
