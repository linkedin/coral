/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.HashMap;
import java.util.Map;

import org.apache.iceberg.Table;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link Dataset} interface for Apache Iceberg tables.
 * This class wraps an Iceberg Table object and provides a unified
 * Dataset API for accessing table metadata.
 *
 * Used by Calcite integration to dispatch to IcebergTable.
 */
public class IcebergDataset implements Dataset {

  private final Table table;

  /**
   * Creates a new IcebergDataset wrapping the given Iceberg table.
   *
   * @param table Iceberg Table object (must not be null)
   */
  public IcebergDataset(Table table) {
    this.table = checkNotNull(table, "Iceberg table cannot be null");
  }

  /**
   * Returns the fully qualified table name from Iceberg table.
   * Uses table.name() which returns the full table identifier.
   *
   * @return Fully qualified table name
   */
  @Override
  public String name() {
    return table.name();
  }

  /**
   * Returns the table properties from Iceberg table metadata.
   * This includes properties set on the Iceberg table.
   *
   * @return Map of table properties
   */
  @Override
  public Map<String, String> properties() {
    if (table.properties() != null) {
      return new HashMap<>(table.properties());
    }
    return new HashMap<>();
  }

  /**
   * Returns the table type.
   * Iceberg tables are always considered physical tables (TABLE type).
   *
   * @return TableType.TABLE
   */
  @Override
  public TableType tableType() {
    return TableType.TABLE;
  }

  /**
   * Returns the underlying Iceberg Table object.
   * Used by Calcite integration layer (IcebergTable).
   *
   * @return Iceberg Table object
   */
  public org.apache.iceberg.Table getIcebergTable() {
    return table;
  }
}
