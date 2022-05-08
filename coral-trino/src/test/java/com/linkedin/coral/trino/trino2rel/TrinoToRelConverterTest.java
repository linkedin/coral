/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;

import static com.linkedin.coral.trino.trino2rel.ToRelTestUtils.*;
import static com.linkedin.coral.trino.trino2rel.Trino2CoralOperatorTransformerMapUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.AssertJUnit.assertEquals;


public class TrinoToRelConverterTest {
  private static HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, IOException, MetaException {
    // Simulating a Coral environment where "foo" exists
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("foo", ReturnTypes.INTEGER,
        or(NILADIC, NUMERIC, NUMERIC_NUMERIC));

    conf = ToRelTestUtils.loadResourceHiveConf();
    ToRelTestUtils.initializeViews(conf);

    Map<String, OperatorTransformer> TRANSFORMER_MAP = Trino2CoralOperatorTransformerMap.TRANSFORMER_MAP;

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
    return ImmutableList.<List<String>> builder()
        .add(ImmutableList.of("select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select * from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("/* start */ select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("/* start */ select * /* middle */ from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("-- start \n select * -- junk -- hi\n from foo -- done",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select * from foo a (v, w, x, y, z)",
            "LogicalProject(V=[$0], W=[$1], X=[$2], Y=[$3], Z=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\" AS \"V\", \"a\" AS \"W\", \"b\" AS \"X\", \"x\" AS \"Y\", \"y\" AS \"Z\"\n"
                + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select *, 123, * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4], EXPR$5=[123], show0=[$0], a0=[$1], b0=[$2], x0=[$3], y0=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\", \"a\", \"b\", \"x\", \"y\", 123, \"show\" AS \"show0\", \"a\" AS \"a0\", \"b\" AS \"b0\", \"x\" AS \"x0\", \"y\" AS \"y0\"\n"
                + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select show from foo",
            "LogicalProject(SHOW=[$0])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"show\" AS \"SHOW\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select extract(day from x), extract(dow from x) from foo",
            "LogicalProject(EXPR$0=[EXTRACT(FLAG(DAY), $3)], EXPR$1=[EXTRACT(FLAG(DOW), $3)])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT EXTRACT(DAY FROM \"x\"), EXTRACT(DOW FROM \"x\")\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select 1 + 13 || '15' from foo",
            "LogicalProject(EXPR$0=[concat(+(1, 13), '15')])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"concat\"(CAST(1 + 13 AS VARCHAR(65535)), '15')\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select x is distinct from y from foo where a is not distinct from b",
            "LogicalProject(EXPR$0=[AND(OR(IS NOT NULL($3), IS NOT NULL($4)), IS NOT TRUE(=($3, $4)))])\n"
                + "  LogicalFilter(condition=[NOT(AND(OR(IS NOT NULL($1), IS NOT NULL($2)), IS NOT TRUE(=($1, $2))))])\n"
                + "    LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT (\"x\" IS NOT NULL OR \"y\" IS NOT NULL) AND \"x\" = \"y\" IS NOT TRUE\n"
                + "FROM \"default\".\"foo\"\n"
                + "WHERE NOT ((\"a\" IS NOT NULL OR \"b\" IS NOT NULL) AND \"a\" = \"b\" IS NOT TRUE)"))
        .add(ImmutableList.of("select x[1] from my_table",
            "LogicalProject(EXPR$0=[ITEM($0, 1)])\n" + "  LogicalTableScan(table=[[hive, default, my_table]])\n",
            "SELECT element_at(\"x\", 1)\n" + "FROM \"default\".\"my_table\""))
        .add(ImmutableList.of("select y[1][2] from my_table",
            "LogicalProject(EXPR$0=[ITEM(ITEM($1, 1), 2)])\n"
                + "  LogicalTableScan(table=[[hive, default, my_table]])\n",
            "SELECT element_at(element_at(\"y\", 1), 2)\n" + "FROM \"default\".\"my_table\""))
        .add(ImmutableList.of("select x[cast(10 * sin(z) as bigint)] from my_table",
            "LogicalProject(EXPR$0=[ITEM($0, CAST(*(10, SIN($2))):BIGINT)])\n"
                + "  LogicalTableScan(table=[[hive, default, my_table]])\n",
            "SELECT element_at(\"x\", CAST(10 * SIN(\"z\") AS BIGINT))\n" + "FROM \"default\".\"my_table\""))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3])",
            "LogicalProject(EXPR$0=[$0])\n" + "  HiveUncollect\n" + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n"
                + "      LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"col\"\n" + "FROM UNNEST(ARRAY[1, 2, 3]) AS \"t0\" (\"col\")"))
        .add(ImmutableList.of("select x from unnest(array[1, 2, 3]) t(x)",
            "LogicalProject(X=[$0])\n" + "  HiveUncollect\n" + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n"
                + "      LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"X\"\n" + "FROM UNNEST(ARRAY[1, 2, 3]) AS \"t0\" (\"X\")"))
        .add(ImmutableList.of("select * from my_table cross join unnest(x)",
            "LogicalProject(x=[$0], y=[$1], z=[$2], EXPR$0=[$3])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n" + "    HiveUncollect\n"
                + "      LogicalProject(col=[$cor0.x])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"$cor0\".\"x\" AS \"x\", \"$cor0\".\"y\" AS \"y\", \"$cor0\".\"z\" AS \"z\", \"t0\".\"col\"\n"
                + "FROM \"default\".\"my_table\" AS \"$cor0\"\n"
                + "CROSS JOIN UNNEST(\"$cor0\".\"x\") AS \"t0\" (\"col\")"))
        .add(ImmutableList.of("select z from my_table cross join unnest(x) t(x_)",
            "LogicalProject(Z=[$2])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n" + "    HiveUncollect\n"
                + "      LogicalProject(col=[$cor0.x])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"$cor0\".\"z\" AS \"Z\"\n" + "FROM \"default\".\"my_table\" AS \"$cor0\"\n"
                + "CROSS JOIN UNNEST(\"$cor0\".\"x\") AS \"t0\" (\"X_\")"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3]) with ordinality",
            "LogicalProject(EXPR$0=[$0], ORDINALITY=[$1])\n" + "  HiveUncollect(withOrdinality=[true])\n"
                + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"col\", \"ORDINALITY\"\n"
                + "FROM UNNEST(ARRAY[1, 2, 3]) WITH ORDINALITY AS \"t0\" (\"col\", \"ORDINALITY\")"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3]) with ordinality t(x, y)",
            "LogicalProject(X=[$0], Y=[$1])\n" + "  HiveUncollect(withOrdinality=[true])\n"
                + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"X\", \"Y\"\n" + "FROM UNNEST(ARRAY[1, 2, 3]) WITH ORDINALITY AS \"t0\" (\"X\", \"Y\")"))
        .add(ImmutableList.of("select * from my_table cross join unnest(x) with ordinality",
            "LogicalProject(x=[$0], y=[$1], z=[$2], EXPR$0=[$3], ORDINALITY=[$4])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n"
                + "    HiveUncollect(withOrdinality=[true])\n" + "      LogicalProject(col=[$cor0.x])\n"
                + "        LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"$cor0\".\"x\" AS \"x\", \"$cor0\".\"y\" AS \"y\", \"$cor0\".\"z\" AS \"z\", \"t0\".\"col\", \"t0\".\"ORDINALITY\" AS \"ORDINALITY\"\n"
                + "FROM \"default\".\"my_table\" AS \"$cor0\"\n"
                + "CROSS JOIN UNNEST(\"$cor0\".\"x\") WITH ORDINALITY AS \"t0\" (\"col\", \"ORDINALITY\")"))
        .add(ImmutableList.of("select z from my_table cross join unnest(x) with ordinality t(a, b)",
            "LogicalProject(Z=[$2])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n"
                + "    HiveUncollect(withOrdinality=[true])\n" + "      LogicalProject(col=[$cor0.x])\n"
                + "        LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"$cor0\".\"z\" AS \"Z\"\n" + "FROM \"default\".\"my_table\" AS \"$cor0\"\n"
                + "CROSS JOIN UNNEST(\"$cor0\".\"x\") WITH ORDINALITY AS \"t0\" (\"A\", \"B\")"))
        .add(ImmutableList.of(
            "with a (id) as (with x as (select 123 from foo) select * from x)    , b (id) as (select 999 from foo) select * from a join b using (id)",
            "LogicalProject(ID=[COALESCE($0, $1)])\n" + "  LogicalJoin(condition=[=($0, $1)], joinType=[inner])\n"
                + "    LogicalProject(EXPR$0=[123])\n" + "      LogicalTableScan(table=[[hive, default, foo]])\n"
                + "    LogicalProject(EXPR$0=[999])\n" + "      LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT COALESCE(999, 999) AS \"ID\"\n" + "FROM (SELECT 123\n" + "FROM \"default\".\"foo\") AS \"t\"\n"
                + "INNER JOIN (SELECT 999\n" + "FROM \"default\".\"foo\") AS \"t0\" ON 999 = 999"))
        .add(ImmutableList.of("select cast('123' as bigint)",
            "LogicalProject(EXPR$0=[CAST('123'):BIGINT])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT CAST('123' AS BIGINT)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(ImmutableList.of("select a \"my price\" from \"foo\" \"ORDERS\"",
            "LogicalProject(MY PRICE=[$1])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n",
            "SELECT \"a\" AS \"MY PRICE\"\n" + "FROM \"default\".\"foo\""))
        .add(ImmutableList.of("select * from a limit all",
            "LogicalProject(b=[$0], id=[$1], x=[$2])\n" + "  LogicalTableScan(table=[[hive, default, a]])\n",
            "SELECT \"b\", \"id\", \"x\"\n" + "FROM \"default\".\"a\""))
        .add(ImmutableList.of("select * from a order by x limit all",
            "LogicalSort(sort0=[$2], dir0=[ASC-nulls-first])\n" + "  LogicalProject(b=[$0], id=[$1], x=[$2])\n"
                + "    LogicalTableScan(table=[[hive, default, a]])\n",
            "SELECT \"b\", \"id\", \"x\"\n" + "FROM \"default\".\"a\"\n" + "ORDER BY \"x\" NULLS FIRST"))
        .add(ImmutableList.of("select * from a union select * from b", "LogicalUnion(all=[false])\n"
            + "  LogicalProject(b=[$0], id=[$1], x=[$2])\n" + "    LogicalTableScan(table=[[hive, default, a]])\n"
            + "  LogicalProject(foobar=[$0], id=[$1], y=[$2])\n" + "    LogicalTableScan(table=[[hive, default, b]])\n",
            "SELECT \"b\", \"id\", \"x\"\n" + "FROM \"default\".\"a\"\n" + "UNION\n"
                + "SELECT \"foobar\", \"id\", \"y\"\n" + "FROM \"default\".\"b\""))
        .add(ImmutableList.of("select strpos('foobar', 'b') as pos",
            "LogicalProject(POS=[instr('FOOBAR', 'B')])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"strpos\"('FOOBAR', 'B') AS \"POS\"\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(ImmutableList.of("select foo(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(3)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(ImmutableList.of("select FOO(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(3)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(
            ImmutableList.of("select foo()", "LogicalProject(EXPR$0=[foo()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
                "SELECT \"foo\"()\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .add(
            ImmutableList.of("call foo(3)", "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
                "SELECT \"foo\"(3)\n" + "FROM (VALUES  (0)) AS \"t\" (\"EXPR$0\")"))
        .add(ImmutableList.of("call foo()", "LogicalProject(EXPR$0=[foo()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"()\n" + "FROM (VALUES  (0)) AS \"t\" (\"EXPR$0\")"))
        .add(ImmutableList.of("select foo(10, 2)",
            "LogicalProject(EXPR$0=[foo(+(*(10, 10), *(10, 2)))])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n",
            "SELECT \"foo\"(10 * 10 + 10 * 2)\n" + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")"))
        .build().stream().map(x -> new Object[] { x.get(0), x.get(1), x.get(2) }).iterator();
  }

  //TODO: Add unsupported SQL tests

  public static String relToStr(RelNode rel) {
    return RelOptUtil.toString(rel);
  }

  @Test(dataProvider = "support")
  public void testSupport(String trinoSql, String expectedRelString, String expectedSql) {
    RelNode relNode = trinoToRelConverter.convertSql(trinoSql);
    assertEquals(relToStr(relNode), expectedRelString);

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    // Convert rel node back to Sql
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, expectedSql);
  }

}
