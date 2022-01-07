/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

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

import com.linkedin.coral.common.ToRelConverterTestUtils;

import static com.linkedin.coral.common.ToRelConverterTestUtils.*;
import static org.testng.Assert.*;


public class HiveOperatorsTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, IOException, MetaException {
    conf = TestUtils.loadResourceHiveConf();
    ToRelConverterTestUtils.setup(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @Test
  public void testLike() {
    testLikeFamilyOperators("LIKE");
  }

  @Test
  public void testNotLike() {
    final String sql = "SELECT a, b FROM foo WHERE b NOT LIKE 'abc%'";
    RelNode rel = toRel(sql);
    final String expectedRel =
        "LogicalProject(a=[$0], b=[$1])\n" + "  LogicalFilter(condition=[NOT(LIKE($1, 'abc%'))])\n"
            + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    final String expectedSql =
        "SELECT \"a\", \"b\"\n" + "FROM \"hive\".\"default\".\"foo\"\n" + "WHERE \"b\" NOT LIKE 'abc%'";
    assertEquals(relToStr(rel), expectedRel);
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testRLike() {
    testLikeFamilyOperators("RLIKE");
  }

  @Test
  public void testRegexp() {
    testLikeFamilyOperators("REGEXP");
  }

  @Test
  public void testInValues() {
    final String sql = "SELECT a, c FROM foo WHERE b IN ('abc', 'pqr', 'mno')";
    RelNode rel = toRel(sql);
    final String expected =
        "LogicalProject(a=[$0], c=[$2])\n" + "  LogicalFilter(condition=[IN($1, 'abc', 'pqr', 'mno')])\n"
            + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relToStr(rel), expected);
    final String expectedSql =
        "SELECT \"a\", \"c\"\nFROM \"hive\".\"default\".\"foo\"\n" + "WHERE \"b\" IN ('abc', 'pqr', 'mno')";
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testNotInValues() {
    final String sql = "SELECT a, b FROM foo WHERE b NOT IN ('abc', 'xyz')";
    RelNode rel = toRel(sql);
    final String expected =
        "LogicalProject(a=[$0], b=[$1])\n" + "  LogicalFilter(condition=[NOT(IN($1, 'abc', 'xyz'))])\n"
            + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relToStr(rel), expected);
    final String expectedSql =
        "SELECT \"a\", \"b\"\nFROM \"hive\".\"default\".\"foo\"\n" + "WHERE NOT \"b\" IN ('abc', 'xyz')";
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testConcatWs() {
    {
      final String sql = "SELECT concat_ws(',', 'abc', b, 'd') from foo";
      final String expectedRel = "LogicalProject(EXPR$0=[concat_ws(',', 'abc', $1, 'd')])\n"
          + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(sqlToRelStr(sql), expectedRel);
    }
    {
      final String sql = "SELECT concat_ws(',', array('abc', 'def', b)) from foo";
      final String expectedRel = "LogicalProject(EXPR$0=[concat_ws(',', ARRAY('abc', 'def', $1))])\n"
          + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(sqlToRelStr(sql), expectedRel);
    }
  }

  @Test
  public void testCast() {
    {
      final String sql = "SELECT a, CAST(NULL as int) FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT \"a\", CAST(NULL AS INTEGER)\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);
    }
    {
      final String sql = "SELECT cast(a as double) FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT CAST(\"a\" AS DOUBLE PRECISION)\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);
    }
  }

  @Test
  public void testDate() {
    {
      final String sql = "SELECT date('2021-01-02') as a FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT \"date\"('2021-01-02') AS \"a\"\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);
    }
  }

  @Test
  public void testMd5() {
    {
      final String sql = "SELECT md5('ABC') as a FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT \"md5\"('ABC') AS \"a\"\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);
    }
  }

  @Test
  public void testSha() {
    {
      final String sql = "SELECT sha1('ABC') as a FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT \"sha1\"('ABC') AS \"a\"\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);

      final String sql2 = "SELECT sha1('ABC') as a FROM foo";
      RelNode rel2 = toRel(sql2);
      final String expectedSql2 = "SELECT \"sha1\"('ABC') AS \"a\"\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel2), expectedSql2);
    }
  }

  @Test
  public void testCrc32() {
    {
      final String sql = "SELECT crc32('ABC') as a FROM foo";
      RelNode rel = toRel(sql);
      final String expectedSql = "SELECT \"crc32\"('ABC') AS \"a\"\n" + "FROM \"hive\".\"default\".\"foo\"";
      assertEquals(relToSql(rel), expectedSql);
    }
  }

  @Test
  public void testLikeFamilyOperators(String operator) {
    final String sql = "SELECT a, b FROM foo WHERE b " + operator + " 'abc%'";
    String expectedRel = "LogicalProject(a=[$0], b=[$1])\n" + "  LogicalFilter(condition=[" + operator.toUpperCase()
        + "($1, 'abc%')])\n" + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    RelNode rel = toRel(sql);
    assertEquals(relToStr(rel), expectedRel);
    String expectedSql = "SELECT \"a\", \"b\"\nFROM \"hive\".\"default\".\"foo\"\n" + "WHERE \"b\" "
        + operator.toUpperCase() + " 'abc%'";
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testIfFunctionReturnType() {
    final String sql = "SELECT IF(FALSE, 0, c) c FROM test.tableOne";
    RelNode rel = toRel(sql);
    assertEquals(rel.getRowType().toString(), "RecordType(DOUBLE c)");
  }
}
