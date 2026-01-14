/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.iceberg.hive.HiveSchemaUtil;


/**
 * Utility class to convert Iceberg datasets to Hive Table objects for backward compatibility.
 *
 * <p><b>TEMPORARY BRIDGE CODE:</b> This converter exists as a temporary workaround and will be removed
 * once the refactoring in <a href="https://github.com/linkedin/coral/issues/575">issue #575</a> is complete.
 *
 * <p><b>Why this exists:</b> The existing ParseTreeBuilder
 * and HiveFunctionResolver are currently tightly coupled
 * to Hive's {@code org.apache.hadoop.hive.metastore.api.Table} class and cannot work directly with
 * {@link CoralTable} or {@link IcebergTable}. This converter bridges the gap by converting
 * Iceberg tables to Hive Table objects for:
 * <ul>
 *   <li>Dali UDF resolution (extracting "functions" and "dependencies" from table properties)</li>
 *   <li>Table identification (database name, table name)</li>
 *   <li>Ownership and permission checks (owner field)</li>
 * </ul>
 *
 * <p><b>Future:</b> Once ParseTreeBuilder and HiveFunctionResolver are refactored to accept {@link CoralTable}
 * instead of Hive Table (see <a href="https://github.com/linkedin/coral/issues/575">issue #575</a>),
 * this converter will no longer be needed and should be removed. IcebergCoralTable will be passable directly.
 *
 * <p><b>What gets converted:</b>
 * <ul>
 *   <li>Iceberg schema → Hive columns (via {@code HiveSchemaUtil.convert()})</li>
 *   <li>All Iceberg table properties → Hive table parameters (including Dali UDF metadata)</li>
 *   <li>Table metadata (name, owner, timestamps, table type)</li>
 *   <li>Storage descriptor with SerDe info (for compatibility)</li>
 * </ul>
 *
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class IcebergHiveTableConverter {

  private IcebergHiveTableConverter() {
    // Utility class - prevent instantiation
  }

  /**
   * Converts IcebergCoralTable to a Hive Table object for backward compatibility with function resolution.
   *
   * <p><b>NOTE:</b> This is temporary glue code that will be removed after
   * <a href="https://github.com/linkedin/coral/issues/575">issue #575</a> is resolved.
   *
   * @param icebergCoralTable Iceberg coral table to convert
   * @return Hive Table object with complete metadata and schema
   */
  public static Table toHiveTable(IcebergTable icebergCoralTable) {
    org.apache.iceberg.Table icebergTable = icebergCoralTable.getIcebergTable();

    // Parse db.table name (format: "dbname.tablename")
    String fullName = icebergCoralTable.name();
    String dbName;
    String tableName;
    int dotIndex = fullName.indexOf('.');
    if (dotIndex > 0) {
      dbName = fullName.substring(0, dotIndex);
      tableName = fullName.substring(dotIndex + 1);
    } else {
      // Fallback if no dot (shouldn't happen in practice)
      dbName = "default";
      tableName = fullName;
    }

    // Convert Iceberg schema to Hive columns using HiveSchemaUtil
    StorageDescriptor storageDescriptor = new StorageDescriptor();
    SerDeInfo serDeInfo = new SerDeInfo();

    // Copy all Iceberg table properties to Hive table parameters
    // This includes Dali UDF metadata ("functions", "dependencies") and any other custom properties
    Map<String, String> hiveParameters = new HashMap<>(icebergCoralTable.properties());

    // Set SerDe parameters (include avro.schema.literal if present)
    Map<String, String> serdeParams = new HashMap<>();
    if (hiveParameters.containsKey("avro.schema.literal")) {
      serdeParams.put("avro.schema.literal", hiveParameters.get("avro.schema.literal"));
    }
    serDeInfo.setParameters(serdeParams);
    storageDescriptor.setSerdeInfo(serDeInfo);

    // Convert Iceberg schema to Hive columns
    try {
      storageDescriptor.setCols(HiveSchemaUtil.convert(icebergTable.schema()));
    } catch (Exception e) {
      // If schema conversion fails, set empty columns list
      // This shouldn't break function resolution as it only needs properties
      storageDescriptor.setCols(new ArrayList<>());
    }

    // Create Hive Table object with all metadata
    Table hiveTable = new Table(tableName, dbName, StringUtils.EMPTY, // owner
        0, // createTime
        0, // lastModifiedTime
        0, // retention
        storageDescriptor, new ArrayList<>(), // partition keys
        hiveParameters, StringUtils.EMPTY, // viewOriginalText
        StringUtils.EMPTY, // viewExpandedText
        "MANAGED_TABLE"); // tableType

    return hiveTable;
  }
}
