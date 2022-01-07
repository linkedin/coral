/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.File;
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
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.common.HiveSchema;
import com.linkedin.coral.common.HiveTable;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


public class HiveTableTest {
  private static TestUtils.TestHive hive;
  private static HiveSchema schema;
  private static HiveConf conf;

  @BeforeClass
  public static void beforeClass() throws IOException {
    conf = TestUtils.loadResourceHiveConf();
    hive = TestUtils.setupDefaultHive(conf);
    schema = getHiveSchema();
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @Test
  public void testTable() {
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
  public void testTableWithUnion() {
    final RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();

    // test handling of union
    Table unionTable = getTable("default", "union_table");
    // union_table:(foo uniontype<int, double, array<string>, struct<a:int,b:string>>)
    // expected outcome schema: struct<tag:int, field0:int, field1:double, field2:array<string>, field3:struct<a:int,b:string>>
    RelDataType rowType = unionTable.getRowType(typeFactory);
    assertNotNull(rowType);

    String expectedTypeString =
        "RecordType(" + "RecordType(" + "INTEGER tag, INTEGER field0, DOUBLE field1, VARCHAR(65536) ARRAY field2, "
            + "RecordType(INTEGER a, VARCHAR(65536) b) field3" + ") " + "foo)";
    assertEquals(rowType.toString(), expectedTypeString);
  }

  @Test
  public void testTableWithUnionComplex() throws Exception {
    // Two complex scenarios for union:
    // 1. nested union: a struct within a union that has another union as one of the member types.
    // 2. when there's existing extract_union UDF (where we replace it with coalesce_struct when
    // the reader returns a trino-compliant schema for a union field)
    final RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
    // Schema: foo uniontype<int, double, struct<a:int, b:uniontype<int, double>>>
    // it should become struct<tag:int, field0:int, field1:double, field2: struct<a:int,b:struct<tag:int, field0:int, field1:double>>>
    Table nestedUnionTable = getTable("default", "nested_union");
    RelDataType rowType = nestedUnionTable.getRowType(typeFactory);
    assertNotNull(rowType);

    String expectedTypeString =
        "RecordType(" + "RecordType(" + "INTEGER tag, INTEGER field0, DOUBLE field1, RecordType("
            + "INTEGER a, RecordType(INTEGER tag, INTEGER field0, DOUBLE field1) b)" + " field2) foo)";
    assertEquals(rowType.toString(), expectedTypeString);

    // Case for with extract_union as part of view definition.
    // Put the alias of foo as bar. The outcome type complies with extract_union's schema recursively

    HiveMscAdapter mscAdapter = new HiveMscAdapter(hive.getMetastoreClient());
    HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
    RelDataType rowType2 = converter.convertSql("SELECT coalesce_struct(foo) AS bar from nested_union").getRowType();
    assertNotNull(rowType2);
    expectedTypeString = "RecordType(" + "RecordType(" + "INTEGER tag_0, DOUBLE tag_1, "
        + "RecordType(INTEGER a, RecordType(INTEGER tag_0, DOUBLE tag_1) b) tag_2) bar)";
    assertEquals(rowType2.toString(), expectedTypeString);
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
