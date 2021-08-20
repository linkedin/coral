/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;


public class HiveOperatorsTest {

  @BeforeClass
  public void beforeClass() throws HiveException, IOException, MetaException {
    ToRelConverter.setup();
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

  private void testLikeFamilyOperators(String operator) {
    final String sql = "SELECT a, b FROM foo WHERE b " + operator + " 'abc%'";
    String expectedRel = "LogicalProject(a=[$0], b=[$1])\n" + "  LogicalFilter(condition=[" + operator.toUpperCase()
        + "($1, 'abc%')])\n" + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    RelNode rel = toRel(sql);
    assertEquals(relToStr(rel), expectedRel);
    String expectedSql = "SELECT \"a\", \"b\"\nFROM \"hive\".\"default\".\"foo\"\n" + "WHERE \"b\" "
        + operator.toUpperCase() + " 'abc%'";
    assertEquals(relToSql(rel), expectedSql);
  }
}
