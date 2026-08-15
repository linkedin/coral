/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.iceberg;

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
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class IcebergHiveTableConverter {

  private IcebergHiveTableConverter() {
  }

  /**
   * Converts IcebergTable to a Hive Table object for backward compatibility with function resolution.
   *
   * <p><b>NOTE:</b> This is temporary glue code that will be removed after
   * <a href="https://github.com/linkedin/coral/issues/575">issue #575</a> is resolved.
   *
   * @param icebergCoralTable Iceberg coral table to convert
   * @return Hive Table object with complete metadata and schema
   */
  public static Table toHiveTable(IcebergTable icebergCoralTable) {
    org.apache.iceberg.Table icebergTable = icebergCoralTable.getIcebergTable();

    String fullName = icebergCoralTable.name();
    String dbName;
    String tableName;
    int dotIndex = fullName.indexOf('.');
    if (dotIndex > 0) {
      dbName = fullName.substring(0, dotIndex);
      tableName = fullName.substring(dotIndex + 1);
    } else {
      dbName = "default";
      tableName = fullName;
    }

    StorageDescriptor storageDescriptor = new StorageDescriptor();
    SerDeInfo serDeInfo = new SerDeInfo();

    Map<String, String> hiveParameters = new HashMap<>(icebergCoralTable.properties());

    Map<String, String> serdeParams = new HashMap<>();
    if (hiveParameters.containsKey("avro.schema.literal")) {
      serdeParams.put("avro.schema.literal", hiveParameters.get("avro.schema.literal"));
    }
    serDeInfo.setParameters(serdeParams);
    storageDescriptor.setSerdeInfo(serDeInfo);

    try {
      storageDescriptor.setCols(HiveSchemaUtil.convert(icebergTable.schema()));
    } catch (Exception e) {
      storageDescriptor.setCols(new ArrayList<>());
    }

    Table hiveTable = new Table(tableName, dbName, StringUtils.EMPTY, 0, 0, 0, storageDescriptor, new ArrayList<>(),
        hiveParameters, StringUtils.EMPTY, StringUtils.EMPTY, "MANAGED_TABLE");

    return hiveTable;
  }
}
