/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.schema.Schema;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveSchema;
import com.linkedin.coral.hive.hive2rel.TestUtils.TestHive;

import static org.testng.Assert.*;


public class HiveSchemaTest {

  private static TestHive hive;
  private static HiveConf conf;

  @BeforeClass
  public void beforeClass() throws IOException {
    conf = TestUtils.loadResourceHiveConf();
    hive = TestUtils.setupDefaultHive(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
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
