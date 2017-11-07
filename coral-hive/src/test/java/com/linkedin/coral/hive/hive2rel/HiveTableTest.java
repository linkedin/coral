package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HiveTableTest {
  private static TestUtils.TestHive hive;

  @BeforeClass
  public static void beforeClass() throws IOException {
    hive = TestUtils.setupDefaultHive();
  }

  @Test
  public void testTable() throws Exception {
    HiveMetastoreClientProvider mscProvider = new HiveMetastoreClientProvider(hive.getConf());
    HiveSchema schema = new HiveSchema(mscProvider.getMetastoreClient());
    Schema defaultSchema = schema.getSubSchema("default");
    Table fooTable = defaultSchema.getTable("foo");
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

    Table complexTable = defaultSchema.getTable("complex");
    // complex(a int, b string, c array<double>, s struct<name:string, age:int>, m map<int, string>)
    rowType = complexTable.getRowType(typeFactory);
    assertNotNull(rowType);
    assertTrue(rowType.isStruct());
    assertEquals(rowType.getFieldCount(), 5);
    RelDataTypeField colC = rowType.getField("c", false, false);
    assertEquals(colC.getType().getSqlTypeName(), SqlTypeName.ARRAY);
    assertEquals(colC.getType().getComponentType().getSqlTypeName(), SqlTypeName.DOUBLE);
  }
}
