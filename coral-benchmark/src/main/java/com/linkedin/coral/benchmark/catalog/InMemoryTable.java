/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.catalog;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.common.types.CoralDataType;


/**
 * An in-memory implementation of {@link CoralTable} for benchmark tests.
 *
 * <p>Always represents a base table ({@link TableType#TABLE}), not a view.
 * The schema is provided at construction time via the Coral type system.
 */
public final class InMemoryTable implements CoralTable {

  private final String name;
  private final CoralDataType schema;
  private final Map<String, String> properties;

  /**
   * Creates an in-memory table with the given name, schema, and properties.
   *
   * @param namespace  the namespace (database) name
   * @param tableName  the table name
   * @param schema     the table schema (typically a {@link com.linkedin.coral.common.types.StructType})
   * @param properties table properties (may be empty)
   */
  InMemoryTable(String namespace, String tableName, CoralDataType schema, Map<String, String> properties) {
    this.name = Objects.requireNonNull(namespace) + "." + Objects.requireNonNull(tableName);
    this.schema = Objects.requireNonNull(schema, "Schema cannot be null");
    this.properties = Collections.unmodifiableMap(Objects.requireNonNull(properties));
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Map<String, String> properties() {
    return properties;
  }

  @Override
  public TableType tableType() {
    return TableType.TABLE;
  }

  @Override
  public CoralDataType getSchema() {
    return schema;
  }
}
