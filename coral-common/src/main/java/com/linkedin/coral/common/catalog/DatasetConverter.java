/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import org.apache.hadoop.hive.metastore.api.Table;


/**
 * Utility class for converting table objects from various formats
 * (Hive, Iceberg, etc.) into unified {@link Dataset} objects.
 *
 * This converter provides factory methods to create Dataset implementations
 * from different underlying table representations.
 */
public class DatasetConverter {

  private DatasetConverter() {
    // Utility class, prevent instantiation
  }

  /**
   * Converts a Hive Table object to a Dataset.
   * This creates a HiveDataset wrapper around the table.
   *
   * Note: This method always assumes the table is a Hive table,
   * even if it's an Iceberg table managed through Hive metastore.
   * Use {@link #fromIcebergTable} for native Iceberg tables.
   *
   * @param table Hive metastore Table object
   * @return Dataset representing the Hive table, or null if input is null
   */
  public static Dataset fromHiveTable(Table table) {
    if (table == null) {
      return null;
    }
    return new HiveDataset(table);
  }

  /**
   * Converts an Iceberg Table object to a Dataset.
   * This creates an IcebergDataset wrapper around the table.
   *
   * @param table Iceberg Table object
   * @return Dataset representing the Iceberg table, or null if input is null
   */
  public static Dataset fromIcebergTable(org.apache.iceberg.Table table) {
    if (table == null) {
      return null;
    }
    return new IcebergDataset(table);
  }

  /**
   * Converts a Hive Table object to a Dataset.
   * This method always treats the table as a Hive table.
   *
   * This is a convenience method equivalent to {@link #fromHiveTable(Table)}.
   *
   * @param table Hive metastore Table object
   * @return Dataset representing the table (HiveDataset)
   */
  public static Dataset autoConvert(Table table) {
    return fromHiveTable(table);
  }
}
