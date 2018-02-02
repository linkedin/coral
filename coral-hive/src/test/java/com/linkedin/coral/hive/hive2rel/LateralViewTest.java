package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;


public class LateralViewTest {
  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    ToRelConverter.setup();
  }

  @Test
  public void testLateralView() {
    final String sql = "SELECT a, ccol from complex lateral view explode(complex.c) t as ccol";
    RelNode relNode = toRel(sql);
    String expected = "LogicalProject(a=[$0], ccol=[$6])\n" +
        "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])\n" +
        "    LogicalTableScan(table=[[hive, default, complex]])\n" +
        "    LogicalProject(EXPR$0=[$0])\n" +
        "      HiveUncollect\n" +
        "        LogicalProject(c=[$cor0.c])\n" +
        "          LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(toRelStr(sql), expected);
  }

  @Test
  public void testLateralViewOuter() {
    final String sql = "SELECT a, t.ccol from complex lateral view outer explode(complex.c) t as ccol";
    RelNode relNode = toRel(sql);
    String expected = "LogicalProject(a=[$0], ccol=[$6])\n" +
        "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])\n" +
        "    LogicalTableScan(table=[[hive, default, complex]])\n" +
        "    LogicalProject(EXPR$0=[$0])\n" +
        "      HiveUncollect\n" +
        "        LogicalProject(EXPR$0=[if(AND(IS NOT NULL($cor0.c), >(CARDINALITY($cor0.c), 0)), $cor0.c, ARRAY(null))])\n"+
        "          LogicalValues(tuples=[[{ 0 }]])\n";

    assertEquals(toRelStr(sql), expected);
  }

  @Test
  public void testMultipleMixedLateralClauses() {
    final String sql = "SELECT a, ccol, r.anotherCCol from complex " +
        " lateral view outer explode(complex.c) t as ccol " +
        " lateral view explode(complex.c) r as anotherCCol";
    String expected = "LogicalProject(a=[$0], ccol=[$6], anotherCCol=[$7])\n" +
        "  LogicalCorrelate(correlation=[$cor3], joinType=[inner], requiredColumns=[{2}])\n" +
        "    LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])\n" +
        "      LogicalTableScan(table=[[hive, default, complex]])\n" +
        "      LogicalProject(EXPR$0=[$0])\n" +
        "        HiveUncollect\n" +
        "          LogicalProject(EXPR$0=[if(AND(IS NOT NULL($cor0.c), >(CARDINALITY($cor0.c), 0)), $cor0.c, ARRAY(null))])\n" +
        "            LogicalValues(tuples=[[{ 0 }]])\n" +
        "    LogicalProject(EXPR$0=[$0])\n" +
        "      HiveUncollect\n" +
        "        LogicalProject(c=[$cor3.c])\n" +
        "          LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(relToString(sql), expected);
  }

  @Test
  public void testUnnestNestedTypes() {
    final String sql = "SELECT a, sarr, flat_s FROM complex\n"
        + "lateral view outer explode(complex.sarr) t as flat_s";
    String expected = "LogicalProject(a=[$0], sarr=[$5], flat_s=[$6])\n" +
        "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{5}])\n" +
        "    LogicalTableScan(table=[[hive, default, complex]])\n" +
        "    LogicalProject(EXPR$0=[$0])\n" +
        "      HiveUncollect\n" +
        "        LogicalProject(EXPR$0=[if(AND(IS NOT NULL($cor0.sarr), >(CARDINALITY($cor0.sarr), 0)), $cor0.sarr, ARRAY(null))])\n" +
        "          LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(toRelStr(sql), expected);
  }

  @Test
  public void testComplexLateralExplodeOperand() {
    final String sql = "SELECT a, ccol from complex lateral view " +
        " explode(if(size(complex.c) > 5, array(10.5), complex.c)) t as ccol";
    final String expected = "LogicalProject(a=[$0], ccol=[$6])\n" +
        "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{2}])\n" +
        "    LogicalTableScan(table=[[hive, default, complex]])\n" +
        "    LogicalProject(EXPR$0=[$0])\n" +
        "      HiveUncollect\n" +
        "        LogicalProject(EXPR$0=[if(>(CARDINALITY($cor0.c), 5), ARRAY(10.5), $cor0.c)])\n" +
        "          LogicalValues(tuples=[[{ 0 }]])\n";
    assertEquals(toRelStr(sql), expected);
  }

  private String toRelStr(String sql) {
    return RelOptUtil.toString(toRel(sql));
  }

  private RelNode toRel(String sql) {
    return getConverter().convertSql(sql);
  }

  private HiveToRelConverter getConverter() {
    return HiveToRelConverter.create(new HiveMscAdapter(ToRelConverter.getMsc()));
  }
}
