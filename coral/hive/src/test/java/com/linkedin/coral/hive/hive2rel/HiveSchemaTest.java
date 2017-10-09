package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.linkedin.coral.hive.hive2rel.TestUtils.TestHive;
import org.apache.calcite.schema.Schema;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HiveSchemaTest {

  private static TestHive hive;

  @BeforeClass
  public static void beforeClass() {
    hive = TestUtils.setupDefaultHive();
  }

  @Test
  public void testHiveSchema() {
    HiveSchema schema = HiveSchema.create(hive.context.getHive());
    assertEquals(schema.getSubSchemaNames(), ImmutableSet.copyOf(hive.getDbNames()));
    assertEquals(schema.getSubSchema("noSuchSchema"), null);
    assertEquals(schema.getTableNames(), ImmutableSet.of());
    assertEquals(schema.getTable("noSuchTable"), null);
    assertEquals(schema.getFunctionNames(), ImmutableSet.of());
    assertEquals(schema.getFunctions("foo"), ImmutableList.of());
    assertTrue(schema.isMutable());
    assertTrue(schema.contentsHaveChangedSince(300, System.currentTimeMillis()));

    Schema defaultDb = schema.getSubSchema("default");
    assertEquals(defaultDb.getTableNames(), ImmutableSet.copyOf(hive.getTables("default")));
    assertEquals(defaultDb.getTable("noDbTable"), null);
    assertNotNull(defaultDb.getTable("foo"));
    assertEquals(defaultDb.getSubSchemaNames(), ImmutableSet.of());
    assertNull(defaultDb.getSubSchema("subSchema"));
    assertTrue(defaultDb.isMutable());
    assertTrue(defaultDb.contentsHaveChangedSince(300, System.currentTimeMillis()));
    assertEquals(defaultDb.getFunctionNames(), ImmutableSet.of());
    assertEquals(defaultDb.getFunctions("function"), ImmutableList.of());

    assertNotNull(schema.getSubSchema("test"));
  }
}
