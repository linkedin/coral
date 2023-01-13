/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.intellij.lang.annotations.Language;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;

import static com.linkedin.coral.spark.spark2rel.Spark2CoralOperatorTransformerMapUtils.createOperator;
import static com.linkedin.coral.spark.spark2rel.Spark2CoralOperatorTransformerMapUtils.createTransformerMapEntry;
import static com.linkedin.coral.spark.spark2rel.ToRelTestUtils.CORAL_FROM_TRINO_TEST_DIR;
import static com.linkedin.coral.spark.spark2rel.ToRelTestUtils.sparkToRelConverter;
import static org.apache.calcite.sql.type.OperandTypes.NILADIC;
import static org.apache.calcite.sql.type.OperandTypes.NUMERIC;
import static org.apache.calcite.sql.type.OperandTypes.NUMERIC_NUMERIC;
import static org.apache.calcite.sql.type.OperandTypes.or;
import static org.testng.AssertJUnit.assertEquals;


public class SparkToRelConverterTest {
  private static HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, IOException, MetaException {
    // Simulating a Coral environment where "foo" exists
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("foo", ReturnTypes.INTEGER,
        or(NILADIC, NUMERIC, NUMERIC_NUMERIC));

    conf = ToRelTestUtils.loadResourceHiveConf();
    ToRelTestUtils.initializeViews(conf);

    Map<String, OperatorTransformer> TRANSFORMER_MAP = Spark2CoralOperatorTransformerMap.TRANSFORMER_MAP;

    // foo(a) or foo()
    createTransformerMapEntry(TRANSFORMER_MAP, createOperator("foo", ReturnTypes.INTEGER, or(NILADIC, NUMERIC)), 1,
        "foo", null, null);

    // foo(a, b) => foo((10 * a) + (10 * b))
    createTransformerMapEntry(TRANSFORMER_MAP, createOperator("foo", ReturnTypes.INTEGER, NUMERIC_NUMERIC), 2, "foo",
        "[{\"op\":\"+\",\"operands\":[{\"op\":\"*\",\"operands\":[{\"value\":10},{\"input\":1}]},{\"op\":\"*\",\"operands\":[{\"value\":10},{\"input\":2}]}]}]",
        null);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_FROM_TRINO_TEST_DIR)));
  }

  @DataProvider(name = "support")
  public Iterator<Object[]> getSupportedSql() {
    return ImmutableList.<Spark2TrinoDataProvider> builder()
        .add(new Spark2TrinoDataProvider("select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT *\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select * from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT *\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("/* start */ select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT *\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("/* start */ select * /* middle */ from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT *\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("-- start \n select * -- junk -- hi\n from foo -- done",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT *\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select * from foo a (v, w, x, y, z)",
            "LogicalProject(V=[$0], W=[$1], X=[$2], Y=[$3], Z=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"foo\".\"show\" AS \"V\", \"foo\".\"a\" AS \"W\", \"foo\".\"b\" AS \"X\", \"foo\".\"x\" AS \"Y\", \"foo\".\"y\" AS \"Z\"\n"
                + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select *, 123, * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4], EXPR$5=[123], show0=[$0], a0=[$1], b0=[$2], x0=[$3], y0=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"foo\".\"show\" AS \"show\", \"foo\".\"a\" AS \"a\", \"foo\".\"b\" AS \"b\", \"foo\".\"x\" AS \"x\", \"foo\".\"y\" AS \"y\", 123, \"foo\".\"show\" AS \"show0\", \"foo\".\"a\" AS \"a0\", \"foo\".\"b\" AS \"b0\", \"foo\".\"x\" AS \"x0\", \"foo\".\"y\" AS \"y0\"\n"
                + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select show from foo",
            "LogicalProject(SHOW=[$0])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"foo\".\"show\" AS \"SHOW\"\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select 1 + 13 || '15' from foo",
            "LogicalProject(EXPR$0=[||(CAST(+(1, 13)):VARCHAR(65535) NOT NULL, '15')])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT CAST(1 + 13 AS VARCHAR(65535)) || '15'\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select x is distinct from y from foo where a is not distinct from b",
            "LogicalProject(EXPR$0=[AND(OR(IS NOT NULL($3), IS NOT NULL($4)), IS NOT TRUE(=($3, $4)))])\n"
                + "  LogicalFilter(condition=[NOT(AND(OR(IS NOT NULL($1), IS NOT NULL($2)), IS NOT TRUE(=($1, $2))))])\n"
                + "    LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT (\"foo\".\"x\" IS NOT NULL OR \"foo\".\"y\" IS NOT NULL) AND \"foo\".\"x\" = \"foo\".\"y\" IS NOT TRUE\n"
                + "FROM \"default\".\"foo\" AS \"foo\"\n"
                + "WHERE NOT ((\"foo\".\"a\" IS NOT NULL OR \"foo\".\"b\" IS NOT NULL) AND \"foo\".\"a\" = \"foo\".\"b\" IS NOT TRUE)"))
        .add(new Spark2TrinoDataProvider("select cast('123' as bigint)",
            "LogicalProject(EXPR$0=[CAST('123'):BIGINT])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT CAST('123' AS BIGINT)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(new Spark2TrinoDataProvider("select a `my price` from `foo` `ORDERS`",
            "LogicalProject(MY PRICE=[$1])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"foo\".\"a\" AS \"MY PRICE\"\n" + "FROM \"default\".\"foo\" AS \"foo\""))
        .add(new Spark2TrinoDataProvider("select * from a limit all",
            "LogicalProject(b=[$0], id=[$1], x=[$2])\n" + "  LogicalTableScan(table=[[hive, default, a]])\n",
            "SELECT *\n" + "FROM \"default\".\"a\" AS \"a\""))
        .add(new Spark2TrinoDataProvider("select * from a order by x limit all",
            "LogicalSort(sort0=[$2], dir0=[ASC-nulls-first])\n" + "  LogicalProject(b=[$0], id=[$1], x=[$2])\n"
                + "    LogicalTableScan(table=[[hive, default, a]])\n",
            "SELECT *\n" + "FROM \"default\".\"a\" AS \"a\"\n" + "ORDER BY \"a\".\"x\" NULLS FIRST"))
        .add(new Spark2TrinoDataProvider("select foo(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(3)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(new Spark2TrinoDataProvider("select FOO(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(3)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(new Spark2TrinoDataProvider("select foo()",
            "LogicalProject(EXPR$0=[foo()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"()\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(new Spark2TrinoDataProvider("select foo(10, 2)",
            "LogicalProject(EXPR$0=[foo(+(*(10, 10), *(10, 2)))])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(10 * 10 + 10 * 2)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .build().stream().map(x -> new Object[] { x.sparkSql, x.explain, x.trinoSql }).iterator();
  }

  private static class Spark2TrinoDataProvider {
    private final String sparkSql;
    private final String explain;
    private final String trinoSql;

    public Spark2TrinoDataProvider(@Language("SQL") String sparkSql, String explain, @Language("SQL") String trinoSql) {
      this.sparkSql = sparkSql;
      this.explain = explain;
      this.trinoSql = trinoSql;
    }
  }

  //TODO: Add unsupported SQL tests

  @Test(dataProvider = "support")
  public void testSupport(String trinoSql, String expectedRelString, String expectedSql) throws Exception {
    RelNode relNode = sparkToRelConverter.convertSql(trinoSql);
    assertEquals(expectedRelString, RelOptUtil.toString(relNode));

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter(new HiveMscAdapter(Hive.get(conf).getMSC()));
    // Convert rel node back to Sql
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expectedSql, expandedSql);
  }

}
