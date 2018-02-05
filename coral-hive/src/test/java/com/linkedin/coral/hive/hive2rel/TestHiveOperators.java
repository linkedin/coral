package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;


public class TestHiveOperators {

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
    final String expectedRel = "LogicalProject(a=[$0], b=[$1])\n"
        + "  LogicalFilter(condition=[NOT(LIKE($1, 'abc%'))])\n"
        + "    LogicalTableScan(table=[[hive, default, foo]])\n";
    final String expectedSql = "SELECT \"a\", \"b\"\n"
        + "FROM \"hive\".\"default\".\"foo\"\n"
        + "WHERE \"b\" NOT LIKE 'abc%'";
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

  public void testLikeFamilyOperators(String operator) {
    final String sql = "SELECT a, b FROM foo WHERE b " + operator + " 'abc%'";
     String expectedRel = "LogicalProject(a=[$0], b=[$1])\n" +
        "  LogicalFilter(condition=[" + operator.toUpperCase()  +"($1, 'abc%')])\n" +
        "    LogicalTableScan(table=[[hive, default, foo]])\n";
    RelNode rel = toRel(sql);
    assertEquals(relToStr(rel), expectedRel);
    String expectedSql = "SELECT \"a\", \"b\"\nFROM \"hive\".\"default\".\"foo\"\n"
        + "WHERE \"b\" " + operator.toUpperCase() + " 'abc%'";
    assertEquals(relToSql(rel), expectedSql);
  }
}
