/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.incremental.TestUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class RelToIncrementalSqlConverterTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_INCREMENTAL_TEST_DIR)));
  }

  public String getIncrementalModification(String sql) {
    RelToIncrementalSqlConverter converter = new RelToIncrementalSqlConverter();
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    String temp = converter.convert(originalRelNode);
    return converter.convert(originalRelNode);
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.foo";
    String expectedSql = "SELECT *\nFROM test.foo_delta";
    assertEquals(getIncrementalModification(sql), expectedSql);
  }

  @Test
  public void testJoinInput() {
    // Not a test, currently used for debugger runs only
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    getIncrementalModification(sql);
  }

  @Test
  public void testFilterInput() {
    // Not a test, currently used for debugger runs only
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x WHERE test.bar1.x > 10";
    getIncrementalModification(sql);
  }

  @Test
  public void testFilterNestedInput() {
    // Not a test, currently used for debugger runs only
    String sql =
        "WITH tmp AS (SELECT * from test.bar1 WHERE test.bar1.x > 10), tmp2 AS (SELECT * from test.bar2) SELECT * FROM tmp JOIN tmp2 ON tmp.x = tmp2.x";
    getIncrementalModification(sql);
  }

  @Test
  public void testJoinOutput() {
    // Not a test, currently used for debugger runs only
    String sql =
        "SELECT * FROM test.bar1 INNER JOIN test.bar2 ON test.bar1.x = test.bar2.x UNION ALL SELECT * FROM test.bar1 INNER JOIN test.bar2 ON test.bar1.x = test.bar2.x UNION ALL SELECT * FROM test.bar1 INNER JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    getIncrementalModification(sql);
  }
}
