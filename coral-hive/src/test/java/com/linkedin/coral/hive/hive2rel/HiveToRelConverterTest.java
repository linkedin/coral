/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RelBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.ToRelConverterTestUtils;
import com.linkedin.coral.common.functions.UnknownSqlFunctionException;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static com.linkedin.coral.common.ToRelConverterTestUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class HiveToRelConverterTest {

  private static HiveConf conf;

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    conf = TestUtils.loadResourceHiveConf();
    ToRelConverterTestUtils.setup(conf);

    // add the following 3 test UDF to StaticHiveFunctionRegistry for testing purpose.
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.0");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.0");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.1");
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  public void testBasicWithSQL(String sql) {
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("a"), relBuilder.field("b"), relBuilder.field("c")),
            ImmutableList.of(), true)
        .build();
    verifyRel(rel, expected);
  }

  @Test
  public void testBasic() {
    testBasicWithSQL("SELECT * from foo");
  }

  @Test
  public void testBasic2() {
    testBasicWithSQL("SELECT * from default.foo");
  }

  @Test
  public void testWith1() {
    // Test if the code can handle the use of the first alias from the WithList
    String sql = "WITH tmp AS (SELECT a, b from foo), tmp2 AS (SELECT b, c from foo) SELECT * FROM tmp";
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("a"), relBuilder.field("b")), ImmutableList.of(), true).build();
    verifyRel(rel, expected);
  }

  @Test
  public void testWith2() {
    // Test if the code can handle the use of the second alias from the WithList
    String sql = "WITH tmp AS (SELECT a, b from foo), tmp2 AS (SELECT b, c from foo) SELECT * FROM tmp2";
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("b"), relBuilder.field("c")), ImmutableList.of(), true).build();
    verifyRel(rel, expected);
  }

  @Test
  public void testWithNested() {
    // Test if the code can handle the use of an alias "tmp" within the definitino of another alias "tmp2"
    String sql = "WITH tmp AS (SELECT a, b from foo), tmp2 AS (SELECT b from tmp) SELECT * FROM tmp2";
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("b")), ImmutableList.of(), true).build();
    verifyRel(rel, expected);
  }

  @Test
  public void testWindowSpec() {
    // Test if the code can handle the use of window functions
    String sql = "SELECT ROW_NUMBER() OVER (PARTITION BY a ORDER BY b) AS rid FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(rid=[ROW_NUMBER() OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithRowsUnboundedPreceding() {
    // Test if the code can handle the use of window functions with rows
    String sql = "SELECT MIN(c) OVER (PARTITION BY a ORDER BY b ROWS UNBOUNDED PRECEDING) AS min_c FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(min_c=[MIN($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithRangeUnboundedPreceding() {
    // Test if the code can handle the use of window functions with range
    String sql = "SELECT VARIANCE(c) OVER (PARTITION BY a ORDER BY b RANGE UNBOUNDED PRECEDING) AS var_c FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(var_c=[/(-(CASE(>(COUNT(*($2, $2)) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 0), CAST($SUM0(*($2, $2)) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)):DOUBLE, null:DOUBLE), /(*(CASE(>(COUNT($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 0), CAST($SUM0($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)):DOUBLE, null:DOUBLE), CASE(>(COUNT($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 0), CAST($SUM0($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)):DOUBLE, null:DOUBLE)), CAST(COUNT($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)):DOUBLE)), CASE(=(COUNT($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 1), null:BIGINT, CAST(-(COUNT($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 1)):BIGINT))])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithRowsPrecedingAndFollowing() {
    // Test if the code can handle the use of window functions with "rows between" and "current row"
    String sql =
        "SELECT FIRST_VALUE(c) OVER (PARTITION BY a ORDER BY b ROWS BETWEEN 1 PRECEDING AND CURRENT ROW) AS first_value_c FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(first_value_c=[FIRST_VALUE($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST ROWS BETWEEN 1 PRECEDING AND CURRENT ROW)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithRowsPrecedingAndFollowingCurrentRow() {
    // Test if the code can handle the use of window functions with "rows between" and "current row"
    String sql =
        "SELECT LAST_VALUE(c) OVER (PARTITION BY a ORDER BY b ROWS BETWEEN CURRENT ROW AND 1 FOLLOWING) FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(EXPR$0=[LAST_VALUE($2) OVER (PARTITION BY $0 ORDER BY $1 NULLS FIRST ROWS BETWEEN CURRENT ROW AND 1 FOLLOWING)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithNoPartition() {
    // Test if the code can handle the use of window functions with no "partition by"
    String sql = "SELECT RANK() OVER (ORDER BY b) AS rank FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(rank=[RANK() OVER (ORDER BY $1 NULLS FIRST RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testWindowWithNoPartitionNoOrder() {
    // Test if the code can handle the use of window functions with no "partition by" or "order by"
    String sql = "SELECT a, MAX(c) OVER () AS max_c FROM foo";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected =
        "LogicalProject(a=[$0], max_c=[MAX($2) OVER (RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)])\n"
            + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testLateralViewArray() {
    // Test if the code can handle lateral view explode with an array
    String sql = "SELECT col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias AS col";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected = "LogicalProject(col=[$1])\n"
        + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
        + "    LogicalProject(a=[ARRAY('a1', 'a2')])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"
        + "    HiveUncollect\n" + "      LogicalProject(col=[$cor0.a])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testLateralViewArrayWithoutColumns() {
    // Test if the code can handle lateral view explode with an array without column aliases
    String sql = "SELECT a_alias.col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected = "LogicalProject(col=[$1])\n"
        + "  LogicalCorrelate(correlation=[$cor1], joinType=[inner], requiredColumns=[{0}])\n"
        + "    LogicalProject(a=[ARRAY('a1', 'a2')])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"
        + "    HiveUncollect\n" + "      LogicalProject(col=[$cor1.a])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testLateralViewMap() {
    // Test if the code can handle lateral view explode with a map
    String sql =
        "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected = "LogicalProject(key=[$1], value=[$2])\n"
        + "  LogicalCorrelate(correlation=[$cor2], joinType=[inner], requiredColumns=[{0}])\n"
        + "    LogicalProject(m=[MAP('key1', 'value1')])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"
        + "    HiveUncollect\n" + "      LogicalProject(col=[$cor2.m])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testLateralViewMapWithoutColumns() {
    // Test if the code can handle lateral view explode with a map without column aliases
    String sql = "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias";
    RelNode rel = converter.convertSql(sql);
    String relString = relToStr(rel);
    String expected = "LogicalProject(key=[$1], value=[$2])\n"
        + "  LogicalCorrelate(correlation=[$cor3], joinType=[inner], requiredColumns=[{0}])\n"
        + "    LogicalProject(m=[MAP('key1', 'value1')])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"
        + "    HiveUncollect\n" + "      LogicalProject(col=[$cor3.m])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relString, expected);
  }

  @Test
  public void testSelectNull() {
    final String sql = "SELECT NULL as f";
    RelNode rel = toRel(sql);
    final String expected = "LogicalProject(f=[null:NULL])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToStr(rel), expected);
    final String expectedSql = "SELECT CAST(NULL AS NULL) AS \"f\"\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testWhen() {
    final String sql = "SELECT CASE WHEN '1.5' = 1 THEN 'abc' ELSE 'def' END";
    final String expected = "LogicalProject(EXPR$0=[CASE(=(CAST('1.5'):INTEGER NOT NULL, 1), 'abc', 'def')])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testCase() {
    final String sql = "SELECT CASE '1.5' WHEN '1.5' THEN 'abc' ELSE 'def' END";
    final String expected =
        "LogicalProject(EXPR$0=[CASE(=('1.5', '1.5'), 'abc', 'def')])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testNullOperand() {
    {
      // reverse returns ARG0 as return type
      final String sql = "SELECT reverse(NULL)";
      RelNode rel = toRel(sql);
      String expectedRel = "LogicalProject(EXPR$0=[reverse(null:NULL)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(rel), expectedRel);
    }
    {
      final String sql = "SELECT isnull(NULL)";
      String expectedRel = "LogicalProject(EXPR$0=[IS NULL(null:NULL)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(toRel(sql)), expectedRel);
    }
    {
      final String sql = "SELECT isnull(reverse(NULL))";
      String expectedRel =
          "LogicalProject(EXPR$0=[IS NULL(reverse(null:NULL))])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(toRel(sql)), expectedRel);
    }
  }

  @Test
  public void testIFUDF() {
    {
      final String sql = "SELECT if( a > 10, null, 15) FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), null:NULL, 15)])\n"
          + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convertSql(sql);
      assertEquals(RelOptUtil.toString(rel), expected);
      assertEquals(rel.getRowType().getFieldCount(), 1);
      assertEquals(rel.getRowType().getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    }
    {
      final String sql = "SELECT if(a > 10, b, 'abc') FROM foo";
      String expected =
          "LogicalProject(EXPR$0=[if(>($0, 10), $1, 'abc')])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(relToString(sql), expected);
    }
    {
      final String sql = "SELECT if(a > 10, null, null) FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), null:NULL, null:NULL)])\n"
          + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(relToString(sql), expected);
    }
  }

  @Test
  public void testRegexpExtractUDF() {
    {
      final String sql = "select regexp_extract(b, 'a(.*)$', 1) FROM foo";
      String expected = "LogicalProject(EXPR$0=[regexp_extract($1, 'a(.*)$', 1)])\n"
          + "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convertSql(sql);
      assertEquals(RelOptUtil.toString(rel), expected);
      assertTrue(rel.getRowType().isStruct());
      assertEquals(rel.getRowType().getFieldCount(), 1);
      assertEquals(rel.getRowType().getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    }
  }

  @Test
  public void testTimestampConversion() {
    final String sql = "SELECT cast(b AS timestamp) FROM complex";
    String expected = String.join("\n", "LogicalProject(EXPR$0=[CAST($1):TIMESTAMP])",
        "  LogicalTableScan(table=[[hive, default, complex]])", "");
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testDaliUDFCall() {
    // TestUtils sets up this view with proper function parameters matching dali setup
    RelNode rel = converter.convertView("test", "tableOneView");
    String expectedPlan = "LogicalProject(EXPR$0=[com.linkedin.coral.hive.hive2rel.CoralTestUDF($0)])\n"
        + "  LogicalTableScan(table=[[hive, test, tableone]])\n";
    assertEquals(RelOptUtil.toString(rel), expectedPlan);
  }

  @Test(expectedExceptions = UnknownSqlFunctionException.class)
  public void testUnresolvedUdfError() {
    final String sql = "SELECT default_foo_IsTestMemberId(a) from foo";
    RelNode rel = converter.convertSql(sql);
  }

  @Test
  public void testViewExpansion() {
    {
      String sql = "SELECT avg(sum_c) from foo_view";
      RelNode rel = converter.convertSql(sql);
      String expectedPlan = "LogicalAggregate(group=[{}], EXPR$0=[AVG($0)])\n" + "  LogicalProject(sum_c=[$1])\n"
          + "    LogicalAggregate(group=[{0}], sum_c=[SUM($1)])\n" + "      LogicalProject(bcol=[$1], c=[$2])\n"
          + "        LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(RelOptUtil.toString(rel), expectedPlan);
    }
  }

  @Test
  public void testArrayType() {
    final String sql = "SELECT array(1,2,3)";
    final String expected = "LogicalProject(EXPR$0=[ARRAY(1, 2, 3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(RelOptUtil.toString(converter.convertSql(sql)), expected);
  }

  @Test
  public void testSelectEmptyArray() {
    final String sql = "SELECT array()";
    final String expected = "LogicalProject(EXPR$0=[ARRAY()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(RelOptUtil.toString(converter.convertSql(sql)), expected);
  }

  @Test
  public void testEmptyArrayInFilter() {
    String sql = "SELECT 1 WHERE array_contains(array(), '1')";
    String expected = "LogicalProject(EXPR$0=[1])\n" + "  LogicalFilter(condition=[array_contains(ARRAY(), '1')])\n"
        + "    LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(RelOptUtil.toString(converter.convertSql(sql)), expected);
  }

  @Test
  public void testSelectEmptyMap() {
    final String sql = "SELECT map()";
    final String expected = "LogicalProject(EXPR$0=[MAP()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(RelOptUtil.toString(converter.convertSql(sql)), expected);
  }

  @Test
  public void testSelectArrayElement() {
    final String sql = "SELECT c[0] from complex";
    final String expectedRel =
        "LogicalProject(EXPR$0=[ITEM($2, 1)])\n" + "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expectedRel);
  }

  @Test
  public void testSelectArrayElemComplex() {
    final String sql = "SELECT split(b, ',')[0] FROM complex";
    final String expected =
        "LogicalProject(EXPR$0=[ITEM(split($1, ','), 1)])\n" + "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testSelectArrayElemWithFunctionArgument() {
    final String sql = "SELECT c[size(c) - 1] FROM complex";
    final String expected = "LogicalProject(EXPR$0=[ITEM($2, +(-(CARDINALITY($2), 1), 1))])\n"
        + "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testMapType() {
    final String sql = "SELECT map('abc', 123, 'def', 567)";
    String generated = relToString(sql);
    final String expected =
        "LogicalProject(EXPR$0=[MAP('abc', 123, 'def', 567)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testMapItem() {
    final String sql = "SELECT m['a'] FROM complex";
    final String expected =
        "LogicalProject(EXPR$0=[ITEM($4, 'a')])\n" + "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testArrayMapItemOperator() {
    final String sql = "SELECT array(map('abc', 123, 'def', 567),map('pqr', 65, 'xyz', 89))[0]['abc']";
    // indexes are 1-based in relnodes
    final String expected =
        "LogicalProject(EXPR$0=[ITEM(ITEM(ARRAY(MAP('abc', 123, 'def', 567), MAP('pqr', 65, 'xyz', 89)), 1), 'abc')])\n"
            + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testStructType() {
    final String sql = "SELECT struct(10, 15, 20.23)";
    String generated = relToString(sql);
    final String expected =
        "LogicalProject(EXPR$0=[ROW(10, 15, 20.23:DECIMAL(4, 2))])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test(enabled = false)
  public void testStructFieldAccess() {
    {
      final String sql = "SELECT s.name from complex";
      final String expectedRel =
          "LogicalProject(name=[$3.name])\n" + "  LogicalTableScan(table=[[hive, default, complex]])\n";
      RelNode rel = toRel(sql);
      assertEquals(relToStr(rel), expectedRel);
      final String expectedSql = "SELECT \"s\".\"name\"\nFROM \"hive\".\"default\".\"complex\"";
      assertEquals(relToSql(rel), expectedSql);
    }
    {
      final String sql = "SELECT complex.s.name FROM complex";

      RelNode rel = toRel(sql);
      System.out.println(relToStr(rel));
      System.out.println(relToSql(rel));
    }
  }

  // Calcite supports PEEK_FIELDS to peek into struct fields
  // That is not suitable for our usecase. This test is to ensure
  // we don't inadvertently introduce that change
  @Test(expectedExceptions = CalciteContextException.class)
  public void testStructPeekDisallowed() {
    final String sql = "SELECT name from complex";
    RelNode rel = toRel(sql);
  }

  @Test
  public void testStructReturnFieldAccess() {
    final String sql = "select named_struct('field_a', 10, 'field_b', 'abc').field_b";
    RelNode rel = toRel(sql);
    final String expectedRel = "LogicalProject(EXPR$0=[CAST(ROW(10, 'abc')):"
        + "RecordType(INTEGER NOT NULL field_a, CHAR(3) NOT NULL field_b) NOT NULL.field_b])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToStr(rel), expectedRel);
    final String expectedSql = "SELECT CAST(ROW(10, 'abc') AS ROW(field_a INTEGER, field_b CHAR(3))).field_b\n"
        + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(relToHql(rel), expectedSql);
  }

  // Should not generate case operator for functions like IS NULL -- the nullability check should be ignored
  @Test
  public void testNonNullFunctionView() {
    final String sql = "SELECT * from null_check_wrapper";
    RelNode rel = toRel(sql);
    final String expectedRel =
        "LogicalProject(a=[$0], b_isnull=[IS NULL($1)])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(relToStr(rel), expectedRel);
    final String expectedSql = "SELECT a, b IS NULL b_isnull\n" + "FROM hive.default.foo";
    assertEquals(relToHql(rel), expectedSql);
  }

  // Should not generate case operator for schema evolution happening in the middle of a struct
  @Test
  public void testSchemaEvolvedInMiddleView() {
    final String sql = "SELECT * from view_schema_evolve_wrapper";
    RelNode rel = toRel(sql);
    final String expectedSql = "SELECT *\n" + "FROM hive.default.schema_evolve";
    assertEquals(relToHql(rel), expectedSql);
  }

  @Test
  public void testConversionWithLocalMetastore() {
    Map<String, Map<String, List<String>>> localMetaStore = ImmutableMap.of("default",
        ImmutableMap.of("table_localstore", ImmutableList.of("name|string", "company|string", "group_name|string")));
    HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(localMetaStore);
    RelNode rel = hiveToRelConverter.convertSql("SELECT * FROM default.table_localstore");

    final String expectedSql = "SELECT *\n" + "FROM hive.default.table_localstore";
    assertEquals(relToHql(rel), expectedSql);
  }

  @Test
  public void testCurrentUser() {
    final String sql = "SELECT current_user() as cu";
    String generated = relToString(sql);
    final String expected = "LogicalProject(cu=[CURRENT_USER])\n  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test
  public void testUnionExtractUDF() {
    final String sql1 = "SELECT extract_union(foo) from union_table";
    String generated1 = relToString(sql1);
    final String expected1 =
        "LogicalProject(EXPR$0=[extract_union($0)])\n" + "  LogicalTableScan(table=[[hive, default, union_table]])\n";
    assertEquals(generated1, expected1);

    final String sql2 = "SELECT extract_union(foo, 1) from union_table";
    String generated2 = relToString(sql2);
    final String expected2 = "LogicalProject(EXPR$0=[extract_union($0, 1)])\n"
        + "  LogicalTableScan(table=[[hive, default, union_table]])\n";
    assertEquals(generated2, expected2);
  }

  @Test
  public void testNestedGroupBy() {
    final String sql = "SELECT a, count(1) count FROM (SELECT a FROM foo GROUP BY a ) t GROUP BY a ";
    RelNode rel = toRel(sql);
    final String expectedSql =
        "SELECT a, COUNT(*) count\nFROM (SELECT a, 1 $f1\nFROM hive.default.foo\nGROUP BY a) t1\nGROUP BY a";
    assertEquals(relToHql(rel), expectedSql);
  }

  @Test
  public void testComment() {
    final String expected =
        "LogicalProject(a=[$0], b=[$1], c=[$2])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n";

    // single-line comments
    final String sql1 = "--comment 0\nSELECT * -- comment1\nFROM foo";
    String generated1 = relToString(sql1);
    assertEquals(generated1, expected);

    // bracketed comments
    final String sql2 =
        "/* comment0 */\n/*comment1*//* comment 2*/ /**/ SELECT /*comm\nent3*/* FROM default./*\ncomment4\n*/foo /* comment5 */";
    String generated2 = relToString(sql2);
    assertEquals(generated2, expected);

    // comments with both styles mixed
    final String sql3 =
        "-- comment 0\n/*comment1*/-- comment 2\nSELECT /*comm\nent3*/* FROM/**/default./*comment4*/foo /* comment5 */--";
    String generated3 = relToString(sql3);
    assertEquals(generated3, expected);
  }

  @Test
  public void testConcat() {
    final String expected = "LogicalProject(EXPR$0=[concat('a', 'b')])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n";
    final String sql = "SELECT 'a' || 'b'";
    String generated = relToString(sql);
    assertEquals(generated, expected);
  }

  @Test
  public void testCastToDecimal() {
    final String expected =
        "LogicalProject(EXPR$0=[CAST($0):DECIMAL(6, 2)])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    final String sql = "SELECT CAST(a AS DECIMAL(6, 2)) FROM foo";
    String generated = relToString(sql);
    assertEquals(generated, expected);
  }

  @Test
  public void testCastToDecimalDefault() {
    final String expected =
        "LogicalProject(EXPR$0=[CAST($0):DECIMAL(10, 0)])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n";
    final String sql = "SELECT CAST(a AS DECIMAL) FROM foo";
    String generated = relToString(sql);
    assertEquals(generated, expected);
  }

  @Test
  public void testNameSakeColumnNamesShouldGetUniqueIdentifiers() {
    String expected = "SELECT \"some_id\"\n"
        + "FROM (SELECT \"duplicate_column_name_a\".\"some_id\", \"t\".\"SOME_ID\" AS \"SOME_ID0\"\n"
        + "FROM \"hive\".\"default\".\"duplicate_column_name_a\"\n"
        + "LEFT JOIN (SELECT TRIM(\"some_id\") AS \"SOME_ID\", CAST(TRIM(\"some_id\") AS VARCHAR(10485760)) AS \"$f1\"\n"
        + "FROM \"hive\".\"default\".\"duplicate_column_name_b\") AS \"t\" ON \"duplicate_column_name_a\".\"some_id\" = \"t\".\"$f1\") AS \"t0\"\n"
        + "WHERE \"t0\".\"some_id\" <> ''";
    SqlNode node = viewToSqlNode("default", "view_namesake_column_names");
    converter.getSqlValidator().validate(node);
    String generated = nodeToStr(node);
    assertEquals(generated, expected);
  }

  private String relToString(String sql) {
    return RelOptUtil.toString(converter.convertSql(sql));
  }
}
