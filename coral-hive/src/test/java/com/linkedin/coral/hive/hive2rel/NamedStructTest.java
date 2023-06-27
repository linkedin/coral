/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.ToRelConverterTestUtils;

import static com.linkedin.coral.common.ToRelConverterTestUtils.*;
import static org.testng.Assert.*;


public class NamedStructTest {

  private static HiveConf conf;

  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    ToRelConverterTestUtils.setup(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @Test
  public void testMixedTypes() {
    final String sql = "SELECT named_struct('abc', 123, 'def', 'xyz')";
    RelNode rel = toRel(sql);
    final String generated = relToStr(rel);
    final String expected = "" + "LogicalProject(EXPR$0=[named_struct('abc', 123, 'def', 'xyz')])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testNullFieldValue() {
    final String sql = "SELECT named_struct('abc', cast(NULL as int), 'def', 150)";
    final String generated = sqlToRelStr(sql);
    final String expected = "LogicalProject(EXPR$0=[named_struct('abc', CAST(null:NULL):INTEGER, 'def', 150)])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testAllNullValues() {
    final String sql = "SELECT named_struct('abc', cast(NULL as int), 'def', cast(NULL as double))";
    final String generated = sqlToRelStr(sql);
    final String expected =
        "LogicalProject(EXPR$0=[named_struct('abc', CAST(null:NULL):INTEGER, 'def', CAST(null:NULL):DOUBLE)])\n"
            + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testNestedComplexTypes() {
    final String sql = "SELECT named_struct('arr', array(10, 15), 's', named_struct('f1', 123, 'f2', array(20.5)))";
    final String generated = sqlToRelStr(sql);
    final String expected =
        "LogicalProject(EXPR$0=[named_struct('arr', ARRAY(10, 15), 's', named_struct('f1', 123, 'f2', ARRAY(20.5:DECIMAL(3, 1))))])\n"
            + "  LogicalValues(tuples=[[{ 0 }]])\n";
    // verified by human that expected string is correct and retained here to protect from future changes
    assertEquals(generated, expected);
  }

  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Type 'INTEGER' is not supported")
  public void testBadFieldName() {
    final String sql = "SELECT named_struct(123, 18, 'def', 56)";
    sqlToRelStr(sql);
  }

  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Wrong number of arguments.*")
  public void testBadArgumentList() {
    final String sql = "SELECT named_struct('abc', 123, 'def')";
    sqlToRelStr(sql);
  }

  // This is a valid hive query but we disallow this for now due to
  // various analysis issues
  @Test(expectedExceptions = CalciteContextException.class,
      expectedExceptionsMessageRegExp = ".*Wrong number of arguments.*")
  public void testEmptyArgumentList() {
    final String sql = "SELECT named_struct()";
    sqlToRelStr(sql);
  }
}
