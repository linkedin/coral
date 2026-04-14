/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.Collections;
import java.util.Map;

import org.testng.annotations.Test;

import com.linkedin.coral.common.types.CoralDataType;

import static org.testng.Assert.*;


public class CoralTableIsIcebergBackedTest {

  @Test
  public void testDefaultIsIcebergBackedReturnsFalse() {
    CoralTable table = new CoralTable() {
      @Override
      public String name() {
        return "test.table";
      }

      @Override
      public Map<String, String> properties() {
        return Collections.emptyMap();
      }

      @Override
      public TableType tableType() {
        return TableType.TABLE;
      }

      @Override
      public CoralDataType getSchema() {
        return null;
      }
    };

    assertFalse(table.isIcebergBacked());
  }

  @Test
  public void testHiveTableIsNotIcebergBacked() {
    org.apache.hadoop.hive.metastore.api.Table hiveTable = new org.apache.hadoop.hive.metastore.api.Table();
    hiveTable.setDbName("db");
    hiveTable.setTableName("tbl");
    hiveTable.setTableType("MANAGED_TABLE");
    hiveTable.setSd(new org.apache.hadoop.hive.metastore.api.StorageDescriptor());
    hiveTable.getSd().setCols(Collections.emptyList());
    hiveTable.setPartitionKeys(Collections.emptyList());

    HiveTable table = new HiveTable(hiveTable);
    assertFalse(table.isIcebergBacked());
  }
}
