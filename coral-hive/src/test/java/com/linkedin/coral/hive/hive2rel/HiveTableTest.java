/**
 * Copyright 2017-2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HiveTableTest {
  private static TestUtils.TestHive hive;
  private static HiveSchema schema;

  @BeforeClass
  public static void beforeClass() throws IOException {
    hive = TestUtils.setupDefaultHive();
    schema = getHiveSchema();
  }

  @Test
  public void testTable() throws Exception {
    Table fooTable = getTable("default", "foo");
    assertEquals(fooTable.getJdbcTableType(), Schema.TableType.TABLE);
    RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
    RelDataType rowType = fooTable.getRowType(typeFactory);
    assertNotNull(rowType);
    assertTrue(rowType.isStruct());
    assertEquals(rowType.getFieldCount(), 3);
    assertEquals(rowType.getFieldNames(), ImmutableList.of("a", "b", "c"));
    RelDataTypeField aField = rowType.getField("a", false, false);
    assertEquals(aField.getType().getSqlTypeName(), SqlTypeName.INTEGER);
    assertEquals(aField.getIndex(), 0);

    Table complexTable = getTable("default", "complex");
    // complex(a int, b string, c array<double>, s struct<name:string, age:int>, m map<int, string>)
    rowType = complexTable.getRowType(typeFactory);
    assertNotNull(rowType);
    assertTrue(rowType.isStruct());
    assertEquals(rowType.getFieldCount(), 6);
    RelDataTypeField colC = rowType.getField("c", false, false);
    assertEquals(colC.getType().getSqlTypeName(), SqlTypeName.ARRAY);
    assertEquals(colC.getType().getComponentType().getSqlTypeName(), SqlTypeName.DOUBLE);
  }

  @Test
  public void testGetDaliFunctionParams() throws HiveException, TException {
    {
      // empty params
      HiveTable fooTable = getAsHiveTable("default", "foo");
      Map<String, String> params = fooTable.getDaliFunctionParams();
      assertNotNull(params);
      assertEquals(params.size(), 0);
    }
    {
      // just one entry
      HiveTable fooViewTable = getAsHiveTable("default", "foo_view");
      Map<String, String> params = fooViewTable.getDaliFunctionParams();
      assertNotNull(params);
      assertEquals(params.size(), 1);
      assertTrue(params.containsKey("IsTestMemberId"));
      assertEquals(params.get("IsTestMemberId"), "com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId");
    }
    {
      // multiple functions. Setup table params first
      IMetaStoreClient msc = hive.getMetastoreClient();
      org.apache.hadoop.hive.metastore.api.Table complexHiveTable = msc.getTable("default", "complex");
      complexHiveTable.getParameters().clear();
      TestUtils.setOrUpdateDaliFunction(complexHiveTable, "functionOne", "com.linkedin.functionOne");
      TestUtils.setOrUpdateDaliFunction(complexHiveTable, "functionTwo", "com.linkedin.functionTwo");
      msc.alter_table("default", "complex", complexHiveTable);
      try {
        HiveTable complexTable = getAsHiveTable("default", "complex");

        Map<String, String> params = complexTable.getDaliFunctionParams();
        assertEquals(params.size(), 2);
        assertEquals(params.get("functionOne"), "com.linkedin.functionOne");
        assertEquals(params.get("functionTwo"), "com.linkedin.functionTwo");
      } finally {
        // cleanup state. In finally, to guarantee cleanup
        complexHiveTable.getParameters().clear();
        msc.alter_table("default", "complex", complexHiveTable);
      }
    }
  }

  private HiveTable getAsHiveTable(String db, String table) {
    Table t = getTable(db, table);
    assertTrue(t instanceof HiveTable);
    return ((HiveTable) t);
  }

  /**
   * Provide instance of Table given db and table name. This expects that db exists
   * @param db database name
   * @param table table name
   * @return Instance of schema Table if it exists; null otherwise
   * @throws NullPointerException if db does not exist
   */
  private Table getTable(String db, String table) {
    Schema dbSchema = schema.getSubSchema(db);
    Preconditions.checkNotNull(dbSchema);
    return dbSchema.getTable(table);
  }

  private static HiveSchema getHiveSchema() {
    HiveMetastoreClientProvider mscProvider = new HiveMetastoreClientProvider(hive.getConf());
    return new HiveSchema(mscProvider.getMetastoreClient());
  }
}
