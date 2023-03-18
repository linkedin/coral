/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.differential;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static com.linkedin.coral.differential.TestUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class RelToDiffSqlConverterTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);

    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER));
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_DIFFERENTIAL_TEST_DIR)));
  }

  public String getDifferentialModification(String sql) {
    RelToDiffSqlConverter converter = new RelToDiffSqlConverter();
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    return converter.convert(originalRelNode);
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.foo";
    String expectedSql = "SELECT *\nFROM test.foo_delta";
    assertEquals(getDifferentialModification(sql), expectedSql);
  }
}
