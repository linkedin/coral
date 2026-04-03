/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergHiveTableConverter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class IcebergHiveTableConverter {

  private IcebergHiveTableConverter() {
  }

  /**
   * Converts IcebergTable to a Hive Table object for backward compatibility.
   *
   * @param icebergCoralTable Iceberg coral table to convert
   * @return Hive Table object
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergHiveTableConverter#toHiveTable} instead.
   */
  @Deprecated
  public static org.apache.hadoop.hive.metastore.api.Table toHiveTable(
      com.linkedin.coral.catalog.iceberg.IcebergTable icebergCoralTable) {
    return com.linkedin.coral.catalog.iceberg.IcebergHiveTableConverter.toHiveTable(icebergCoralTable);
  }

  /**
   * Converts the deprecated IcebergTable wrapper to a Hive Table object.
   *
   * @param icebergCoralTable Deprecated IcebergTable wrapper
   * @return Hive Table object
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergHiveTableConverter#toHiveTable} instead.
   */
  @Deprecated
  public static org.apache.hadoop.hive.metastore.api.Table toHiveTable(IcebergTable icebergCoralTable) {
    return com.linkedin.coral.catalog.iceberg.IcebergHiveTableConverter.toHiveTable(icebergCoralTable);
  }
}
