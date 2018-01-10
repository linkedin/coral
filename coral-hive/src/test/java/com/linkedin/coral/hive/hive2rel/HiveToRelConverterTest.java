package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.TestUtils.TestHive;
import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HiveToRelConverterTest {

  private static TestHive hive;
  private static IMetaStoreClient msc;
  private static HiveToRelConverter converter;
  private static RelContextProvider relContextProvider;

  @BeforeClass
  public static void beforeClass() throws IOException, HiveException, MetaException {
    hive = TestUtils.setupDefaultHive();
    msc = hive.getMetastoreClient();
    HiveMscAdapter mscAdapter = new HiveMscAdapter(msc);
    converter = HiveToRelConverter.create(mscAdapter);
    // for validation
    HiveSchema schema = new HiveSchema(mscAdapter);
    relContextProvider = new RelContextProvider(schema);
  }

  @Test
  public void testBasic() {
    {
      String sql = "SELECT * from foo";
      RelNode rel = converter.convert(sql);
      RelBuilder relBuilder = createRelBuilder();
      RelNode expected = relBuilder.scan(ImmutableList.of("hive", "default", "foo"))
          .project(ImmutableList.of(relBuilder.field("a"), relBuilder.field("b"), relBuilder.field("c")),
              ImmutableList.of(), true)
          .build();
      verifyRel(rel, expected);
    }
  }

  @Test
  public void testIFUDF() {
    {
      final String sql = "SELECT if( a > 10, null, 15) FROM foo";
      String expected = "LogicalProject(EXPR$0=[if(>($0, 10), null, 15)])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convert(sql);
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
    }
  }

  @Test
  public void testRegexpExtractUDF() {
    {
      final String sql = "select regexp_extract(b, 'a(.*)$', 1) FROM foo";
      String expected = "LogicalProject(EXPR$0=[regexp_extract($1, 'a(.*)$', 1)])\n" +
          "  LogicalTableScan(table=[[hive, default, foo]])\n";
      RelNode rel = converter.convert(sql);
      assertEquals(RelOptUtil.toString(rel), expected);
      assertTrue(rel.getRowType().isStruct());
      assertEquals(rel.getRowType().getFieldCount(), 1);
      assertEquals(rel.getRowType().getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    }
  }

  @Test
  public void testDaliUDFCall() {
    // TestUtils sets up this view with proper function parameters matching dali setup
    final String sql = "SELECT default_foo_view_IsTestMemberId(15, bcol) from foo_view";
    RelNode rel = converter.convert(sql);
    String expectedPlan = "LogicalProject(EXPR$0=[com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId(15, $0)])\n" +
        "  LogicalProject(bcol=[$0], sum_c=[CAST($1):DOUBLE])\n" +
        "    LogicalAggregate(group=[{0}], sum_c=[SUM($1)])\n" +
        "      LogicalProject(bcol=[$1], c=[$2])\n" +
        "        LogicalTableScan(table=[[hive, default, foo]])\n";
    assertEquals(RelOptUtil.toString(rel), expectedPlan);
  }

  @Test (expectedExceptions = CalciteContextException.class)
  public void testUnresolvedUdfError() {
    final String sql = "SELECT default_foo_IsTestMemberId(a) from foo";
    RelNode rel = converter.convert(sql);
  }

  @Test
  public void testViewExpansion() throws TException {
    {
      String sql = "SELECT avg(sum_c) from foo_view";
      RelNode rel = converter.convert(sql);
      // we don't do rel to rel comparison for this method because of casting operation and expression naming rules
      // it's easier to compare strings
      String expectedPlan = "LogicalAggregate(group=[{}], EXPR$0=[AVG($0)])\n" +
          "  LogicalProject(sum_c=[$1])\n" +
          "    LogicalProject(bcol=[$0], sum_c=[CAST($1):DOUBLE])\n" +
          "      LogicalAggregate(group=[{0}], sum_c=[SUM($1)])\n" +
          "        LogicalProject(bcol=[$1], c=[$2])\n" +
          "          LogicalTableScan(table=[[hive, default, foo]])\n";
      assertEquals(RelOptUtil.toString(rel), expectedPlan);
    }
  }

  private String relToString(String sql) {
    return RelOptUtil.toString(converter.convert(sql));
  }
  private RelBuilder createRelBuilder() {
    return RelBuilder.create(relContextProvider.getConfig());
  }

  private void verifyRel(RelNode input, RelNode expected) {
    assertEquals(input.getInputs().size(), expected.getInputs().size());
    for (int i = 0; i < input.getInputs().size(); i++) {
      verifyRel(input.getInput(i), expected.getInput(i));
    }
    RelOptUtil.areRowTypesEqual(input.getRowType(), expected.getRowType(), true);
  }
}
