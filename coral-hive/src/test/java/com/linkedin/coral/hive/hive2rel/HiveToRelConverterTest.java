/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.functions.UnknownSqlFunctionException;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class HiveToRelConverterTest {

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    ToRelConverter.setup();

    // add the following 3 test UDF to StaticHiveFunctionRegistry for testing purpose.
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.0");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.0");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER), "com.linkedin:udf:1.1");

  }

  @Test
  public void testBasic() {
    String sql = "SELECT * from foo";
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("a"), relBuilder.field("b"), relBuilder.field("c")),
            ImmutableList.of(), true)
        .build();
    verifyRel(rel, expected);
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
  public void testViewExpansion() throws TException {
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
    HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(localMetaStore);
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

  private String relToString(String sql) {
    return RelOptUtil.toString(converter.convertSql(sql));
  }
}
