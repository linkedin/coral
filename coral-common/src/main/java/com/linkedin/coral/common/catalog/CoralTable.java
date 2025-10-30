/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.Map;


/**
 * A unified abstraction representing a table or view in Coral.
 * This interface provides a common way to access table metadata regardless
 * of the underlying table format (Hive, Iceberg, etc.).
 *
 * This abstraction is used by Calcite integration layer to dispatch to
 * the appropriate table implementation (HiveTable or IcebergTable).
 */
public interface CoralTable {

  /**
   * Returns the fully qualified table name in the format "database.table".
   *
   * @return Fully qualified table name
   */
  String name();

  /**
   * Returns the properties/parameters associated with this table.
   * Properties may include table format specific metadata, statistics,
   * partitioning information, etc.
   *
   * @return Map of property key-value pairs
   */
  Map<String, String> properties();

  /**
   * Returns the type of this table (TABLE or VIEW).
   *
   * @return TableType enum value
   */
  TableType tableType();
}
