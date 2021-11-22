/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.schema.Schema;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveSchema;
import com.linkedin.coral.hive.hive2rel.TestUtils.TestHive;

import static org.testng.Assert.*;


public class HiveSchemaTest {

  private static TestHive hive;

  @BeforeClass
  public static void beforeClass() {
    hive = TestUtils.setupDefaultHive();
  }

  @Test
  public void testHiveSchema() {
    HiveMetastoreClientProvider mscProvider = new HiveMetastoreClientProvider(hive.getConf());
    HiveSchema schema = new HiveSchema(mscProvider.getMetastoreClient());
    assertEquals(schema.getSubSchemaNames(), ImmutableSet.copyOf(hive.getDbNames()));
    assertNull(schema.getSubSchema("noSuchSchema"));
    assertEquals(schema.getTableNames(), ImmutableSet.of());
    assertNull(schema.getTable("noSuchTable"));
    assertEquals(schema.getFunctionNames(), ImmutableSet.of());
    assertEquals(schema.getFunctions("foo"), ImmutableList.of());
    assertTrue(schema.isMutable());

    Schema defaultDb = schema.getSubSchema("default");
    assertEquals(defaultDb.getTableNames(), ImmutableSet.copyOf(hive.getTables("default")));
    assertNull(defaultDb.getTable("noDbTable"));
    assertNotNull(defaultDb.getTable("foo"));
    assertEquals(defaultDb.getSubSchemaNames(), ImmutableSet.of());
    assertNull(defaultDb.getSubSchema("subSchema"));
    assertTrue(defaultDb.isMutable());
    assertEquals(defaultDb.getFunctionNames(), ImmutableSet.of());
    assertEquals(defaultDb.getFunctions("function"), ImmutableList.of());

    assertNotNull(schema.getSubSchema("test"));
  }
}
