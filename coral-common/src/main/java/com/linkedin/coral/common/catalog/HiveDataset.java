/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.Collections;
import java.util.Map;

import org.apache.hadoop.hive.metastore.api.Table;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link Dataset} interface for Hive tables.
 * This class wraps a Hive metastore Table object and provides
 * a unified Dataset API for accessing table metadata.
 *
 * Used by Calcite integration to dispatch to HiveTable.
 */
public class HiveDataset implements Dataset {

  private final Table table;

  /**
   * Creates a new HiveDataset wrapping the given Hive table.
   *
   * @param table Hive metastore Table object (must not be null)
   */
  public HiveDataset(Table table) {
    this.table = checkNotNull(table, "Hive table cannot be null");
  }

  /**
   * Returns the fully qualified table name in the format "database.table".
   *
   * @return Fully qualified table name
   */
  @Override
  public String name() {
    return table.getDbName() + "." + table.getTableName();
  }

  /**
   * Returns the table properties/parameters.
   * This includes Hive table properties, SerDe properties,
   * and any custom properties set on the table.
   *
   * @return Map of table properties
   */
  @Override
  public Map<String, String> properties() {
    return table.getParameters() != null ? table.getParameters() : Collections.emptyMap();
  }

  /**
   * Returns the table type (TABLE or VIEW).
   *
   * @return TableType enum value
   */
  @Override
  public TableType tableType() {
    return TableType.fromHiveTableType(table.getTableType());
  }

  /**
   * Returns the underlying Hive Table object.
   * Used by Calcite integration layer (HiveTable).
   *
   * @return Hive metastore Table object
   */
  public org.apache.hadoop.hive.metastore.api.Table getHiveTable() {
    return table;
  }
}
