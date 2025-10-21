/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

/**
 * Enum representing table types supported by Coral.
 * Simplified to distinguish between physical tables and virtual views.
 */
public enum TableType {
  /**
   * Physical table (managed, external, or Iceberg table)
   */
  TABLE,

  /**
   * Virtual view (query definition without data storage)
   */
  VIEW;

  /**
   * Converts Hive table type string to TableType enum.
   *
   * @param hiveTableType Hive table type string (e.g., "MANAGED_TABLE", "EXTERNAL_TABLE", "VIRTUAL_VIEW")
   * @return Corresponding TableType enum value (TABLE or VIEW)
   */
  public static TableType fromHiveTableType(String hiveTableType) {
    if (hiveTableType == null) {
      return TABLE;
    }

    String upperType = hiveTableType.toUpperCase();
    if (upperType.contains("VIEW")) {
      return VIEW;
    }
    return TABLE;
  }
}
