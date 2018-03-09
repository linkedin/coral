package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.functions.UnknownSqlFunctionException;
import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;


public class HiveToRelConverterTest {

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    ToRelConverter.setup();
  }

  @Test
  public void testBasic() {
    String sql = "SELECT * from foo";
    RelNode rel = converter.convertSql(sql);
    RelBuilder relBuilder = createRelBuilder();
    RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
        .project(ImmutableList.of(relBuilder.field("a"),
            relBuilder.field("b"), relBuilder.field("c")),
            ImmutableList.of(), true)
        .build();
    verifyRel(rel, expected);
  }

  @Test
  public void testSelectNull() {
    final String sql = "SELECT NULL as f";
    RelNode rel = toRel(sql);
    final String expected = "LogicalProject(f=[null])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToStr(rel), expected);
    final String expectedSql = "SELECT NULL AS \"f\"\nFROM (VALUES  (0))";
    assertEquals(relToSql(rel), expectedSql);
  }

  @Test
  public void testNullOperand() {
    {
      // reverse returns ARG0 as return type
      final String sql = "SELECT reverse(NULL)";
      RelNode rel = toRel(sql);
      String expectedRel = "LogicalProject(EXPR$0=[reverse(null)])\n"
          + "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(rel), expectedRel);
    }
    {
      final String sql = "SELECT isnull(NULL)";
      String expectedRel = "LogicalProject(EXPR$0=[IS NULL(null)])\n"
          + "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(toRel(sql)), expectedRel);
    }
    {
      final String sql = "SELECT isnull(reverse(NULL))";
      String expectedRel = "LogicalProject(EXPR$0=[IS NULL(reverse(null))])\n" +
          "  LogicalValues(tuples=[[{ 0 }]])\n";
      assertEquals(relToStr(toRel(sql)), expectedRel);
    }
  }

  @Test
  public void testIFUDF() {
    {
      final String sql = "SELECT if( a > 10, null, 15) FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), null, 15)])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convertSql(sql);
      assertEquals(RelOptUtil.toString(rel), expected);
      assertEquals(rel.getRowType().getFieldCount(), 1);
      assertEquals(rel.getRowType().getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    }
    {
      final String sql = "SELECT if(a > 10, b, 'abc') FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), $1, 'abc')])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(relToString(sql), expected);
    }
    {
      final String sql = "SELECT if(a > 10, null, null) FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), null, null)])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(relToString(sql), expected);
    }
  }

  @Test
  public void testRegexpExtractUDF() {
    {
      final String sql = "select regexp_extract(b, 'a(.*)$', 1) FROM foo";
      String expected = "LogicalProject(EXPR$0=[regexp_extract($1, 'a(.*)$', 1)])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convertSql(sql);
      assertEquals(RelOptUtil.toString(rel), expected);
      assertTrue(rel.getRowType().isStruct());
      assertEquals(rel.getRowType().getFieldCount(), 1);
      assertEquals(rel.getRowType().getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    }
  }

  @Test
  public void testDaliUDFCall() {
    // TestUtils sets up this view with proper function parameters matching dali setup
    RelNode rel = converter.convertView("test", "tableOneView");
    String expectedPlan = "LogicalProject(EXPR$0=[com.linkedin.coral.hive.hive2rel.CoralTestUDF($0)])\n" +
        "  LogicalTableScan(table=[[hive, test, tableone]])\n";
    assertEquals(RelOptUtil.toString(rel), expectedPlan);
  }

  @Test (expectedExceptions = UnknownSqlFunctionException.class)
  public void testUnresolvedUdfError() {
    final String sql = "SELECT default_foo_IsTestMemberId(a) from foo";
    RelNode rel = converter.convertSql(sql);
  }

  @Test
  public void testViewExpansion() throws TException {
    {
      String sql = "SELECT avg(sum_c) from foo_view";
      RelNode rel = converter.convertSql(sql);
      String expectedPlan = "LogicalAggregate(group=[{}], EXPR$0=[AVG($0)])\n" +
          "  LogicalProject(sum_c=[$1])\n" +
          "    LogicalAggregate(group=[{0}], sum_c=[SUM($1)])\n" +
          "      LogicalProject(bcol=[$1], c=[$2])\n" +
          "        LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(RelOptUtil.toString(rel), expectedPlan);
    }
  }

  @Test
  public void testArrayType() {
    final String sql = "SELECT array(1,2,3)";
    final String expected = "LogicalProject(EXPR$0=[ARRAY(1, 2, 3)])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(RelOptUtil.toString(converter.convertSql(sql)), expected);
  }

  @Test
  public void testSelectArrayElement() {
    final String sql = "SELECT c[0] from complex";
    final String expectedRel = "LogicalProject(EXPR$0=[ITEM($2, 1)])\n" +
        "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expectedRel);
  }

  @Test
  public void testSelectArrayElemComplex() {
    final String sql = "SELECT split(b, ',')[0] FROM complex";
    final String expected = "LogicalProject(EXPR$0=[ITEM(split($1, ','), 1)])\n" +
        "  LogicalTableScan(table=[[hive, default, complex]])\n";
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
    final String sql = "SELECT m[a] FROM complex";
    final String expected = "LogicalProject(EXPR$0=[ITEM($4, $0)])\n" +
        "  LogicalTableScan(table=[[hive, default, complex]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testArrayMapItemOperator() {
    final String sql = "SELECT array(map('abc', 123, 'def', 567),map('pqr', 65, 'xyz', 89))[0]['abc']";
    // indexes are 1-based in relnodes
    final String expected = "LogicalProject(EXPR$0=[ITEM(ITEM(ARRAY(MAP('abc', 123, 'def', 567), MAP('pqr', 65, 'xyz', 89)), 1), 'abc')])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testStructType() {
    final String sql = "SELECT struct(10, 15, 20.23)";
    String generated = relToString(sql);
    final String expected = "LogicalProject(EXPR$0=[ROW(10, 15, 20.23)])\n" +
        "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(generated, expected);
  }

  @Test (enabled = false)
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
  @Test (expectedExceptions = CalciteContextException.class)
  public void testStructPeekDisallowed() {
    final String sql = "SELECT name from complex";
    RelNode rel = toRel(sql);
  }

  @Test
  public void testStructReturnFieldAccess() {
    final String sql = "select named_struct('field_a', 10, 'field_b', 'abc').field_b";
    RelNode rel = toRel(sql);
    final String expectedRel = "LogicalProject(EXPR$0=[CAST(ROW(10, 'abc')):"
        + "RecordType(INTEGER NOT NULL field_a, CHAR(3) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL field_b) NOT NULL.field_b])\n"
        + "  LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToStr(rel), expectedRel);
    final String expectedSql = "SELECT CAST(ROW(10, 'abc') AS ROW).\"field_b\"\nFROM (VALUES  (0))";
    assertEquals(relToSql(rel), expectedSql);
  }

  private String relToString(String sql) {
    return RelOptUtil.toString(converter.convertSql(sql));
  }
}
