/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TrinoToRelConverterTest {
  public static TrinoToRelConverter converter;


  @BeforeClass
  public void beforeClass() {
    // TODO: migrate to use HiveMetastoreClient
    Map<String, Map<String, List<String>>> localMetastore = new HashMap<>();
    HashMap<String, List<String>> db = new HashMap<>();
    localMetastore.put("default", db);

    db.put("foo", ImmutableList.of("show|int", "a|int", "b|int", "x|date", "y|date"));
    db.put("my_table", ImmutableList.of("x|array<int>", "y|array<array<int>>", "z|int"));
    db.put("src", ImmutableList.of("x|int", "k|int", "v|string"));
    db.put("emp", ImmutableList.of("depname|string", "empno|int", "salary|int"));

    db.put("a", ImmutableList.of("b|int", "id|int", "x|int"));
    db.put("b", ImmutableList.of("foobar|int", "id|int", "y|int"));

    this.converter = new TrinoToRelConverter(localMetastore);
  }

  @DataProvider(name = "support")
  public Iterator<Object[]> getSupportedSql() {
    return ImmutableList.<List<String>> builder()
        .add(ImmutableList.of("select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select * from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("/* start */ select * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("/* start */ select * /* middle */ from foo /* end */",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("-- start \n select * -- junk -- hi\n from foo -- done",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select * from foo a (v, w, x, y, z)",
            "LogicalProject(V=[$0], W=[$1], X=[$2], Y=[$3], Z=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select *, 123, * from foo",
            "LogicalProject(show=[$0], a=[$1], b=[$2], x=[$3], y=[$4], EXPR$5=[123], show0=[$0], a0=[$1], b0=[$2], x0=[$3], y0=[$4])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select show from foo",
            "LogicalProject(SHOW=[$0])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select extract(day from x), extract(dow from x) from foo",
            "LogicalProject(EXPR$0=[EXTRACT(FLAG(DAY), $3)], EXPR$1=[EXTRACT(FLAG(DOW), $3)])\n"
                + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select 1 + 13 || '15' from foo",
            "LogicalProject(EXPR$0=[concat(+(1, 13), '15')])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select x is distinct from y from foo where a is not distinct from b",
            "LogicalProject(EXPR$0=[AND(OR(IS NOT NULL($3), IS NOT NULL($4)), IS NOT TRUE(=($3, $4)))])\n"
                + "  LogicalFilter(condition=[NOT(AND(OR(IS NOT NULL($1), IS NOT NULL($2)), IS NOT TRUE(=($1, $2))))])\n"
                + "    LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select x[1] from my_table",
            "LogicalProject(EXPR$0=[ITEM($0, 1)])\n" + "  LogicalTableScan(table=[[hive, default, my_table]])\n"))
        .add(ImmutableList.of("select y[1][2] from my_table",
            "LogicalProject(EXPR$0=[ITEM(ITEM($1, 1), 2)])\n"
                + "  LogicalTableScan(table=[[hive, default, my_table]])\n"))
        .add(ImmutableList.of("select x[cast(10 * sin(z) as bigint)] from my_table",
            "LogicalProject(EXPR$0=[ITEM($0, CAST(*(10, SIN($2))):BIGINT)])\n"
                + "  LogicalTableScan(table=[[hive, default, my_table]])\n"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3])",
            "LogicalProject(EXPR$0=[$0])\n" + "  HiveUncollect\n" + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n"
                + "      LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select x from unnest(array[1, 2, 3]) t(x)",
            "LogicalProject(X=[$0])\n" + "  HiveUncollect\n" + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n"
                + "      LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select * from my_table cross join unnest(x)",
            "LogicalProject(x=[$0], y=[$1], z=[$2], EXPR$0=[$3])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n" + "    HiveUncollect\n"
                + "      LogicalProject(col=[$cor0.x])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select z from my_table cross join unnest(x) t(x_)",
            "LogicalProject(Z=[$2])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n" + "    HiveUncollect\n"
                + "      LogicalProject(col=[$cor0.x])\n" + "        LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3]) with ordinality",
            "LogicalProject(EXPR$0=[$0], ORDINALITY=[$1])\n" + "  HiveUncollect(withOrdinality=[true])\n"
                + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3]) with ordinality t(x, y)",
            "LogicalProject(X=[$0], Y=[$1])\n" + "  HiveUncollect(withOrdinality=[true])\n"
                + "    LogicalProject(col=[ARRAY(1, 2, 3)])\n" + "      LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select * from my_table cross join unnest(x) with ordinality",
            "LogicalProject(x=[$0], y=[$1], z=[$2], EXPR$0=[$3], ORDINALITY=[$4])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n"
                + "    HiveUncollect(withOrdinality=[true])\n" + "      LogicalProject(col=[$cor0.x])\n"
                + "        LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select z from my_table cross join unnest(x) with ordinality t(a, b)",
            "LogicalProject(Z=[$2])\n"
                + "  LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])\n"
                + "    LogicalTableScan(table=[[hive, default, my_table]])\n"
                + "    HiveUncollect(withOrdinality=[true])\n" + "      LogicalProject(col=[$cor0.x])\n"
                + "        LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of(
            "with a (id) as (with x as (select 123 from foo) select * from x)    , b (id) as (select 999 from foo) select * from a join b using (id)",
            "LogicalProject(ID=[COALESCE($0, $1)])\n" + "  LogicalJoin(condition=[=($0, $1)], joinType=[inner])\n"
                + "    LogicalProject(EXPR$0=[123])\n" + "      LogicalTableScan(table=[[hive, default, foo]])\n"
                + "    LogicalProject(EXPR$0=[999])\n" + "      LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select cast('123' as bigint)",
            "LogicalProject(EXPR$0=[CAST('123'):BIGINT])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select a \"my price\" from \"foo\" \"ORDERS\"",
            "LogicalProject(MY PRICE=[$1])\n" + "  LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of(
            "select * from a cross join b tablesample system (10) join my_table tablesample bernoulli (30) on a.id = b.id",
            "LogicalProject(b=[$0], id=[$1], x=[$2], foobar=[$3], id0=[$4], y=[$5], x0=[$6], y0=[$7], z=[$8])\n"
                + "  LogicalJoin(condition=[=($1, $4)], joinType=[inner])\n"
                + "    LogicalJoin(condition=[true], joinType=[inner])\n"
                + "      LogicalTableScan(table=[[hive, default, a]])\n"
                + "      Sample(mode=[system], rate=[0.1], repeatableSeed=[-])\n"
                + "        LogicalTableScan(table=[[hive, default, b]])\n"
                + "    Sample(mode=[bernoulli], rate=[0.3], repeatableSeed=[-])\n"
                + "      LogicalTableScan(table=[[hive, default, my_table]])\n"))
        .add(ImmutableList.of(
            "select * from a cross join b tablesample system (10) join my_table tablesample bernoulli (30) on not(a.id > b.id)",
            "LogicalProject(b=[$0], id=[$1], x=[$2], foobar=[$3], id0=[$4], y=[$5], x0=[$6], y0=[$7], z=[$8])\n"
                + "  LogicalJoin(condition=[NOT(>($1, $4))], joinType=[inner])\n"
                + "    LogicalJoin(condition=[true], joinType=[inner])\n"
                + "      LogicalTableScan(table=[[hive, default, a]])\n"
                + "      Sample(mode=[system], rate=[0.1], repeatableSeed=[-])\n"
                + "        LogicalTableScan(table=[[hive, default, b]])\n"
                + "    Sample(mode=[bernoulli], rate=[0.3], repeatableSeed=[-])\n"
                + "      LogicalTableScan(table=[[hive, default, my_table]])\n"))
        .add(ImmutableList.of("select * from a limit all",
            "LogicalProject(b=[$0], id=[$1], x=[$2])\n" + "  LogicalTableScan(table=[[hive, default, a]])\n"))
        .add(ImmutableList.of("select * from a order by x limit all",
            "LogicalSort(sort0=[$2], dir0=[ASC-nulls-first])\n" + "  LogicalProject(b=[$0], id=[$1], x=[$2])\n"
                + "    LogicalTableScan(table=[[hive, default, a]])\n"))
        .add(ImmutableList.of("select * from a union select * from b",
            "LogicalUnion(all=[false])\n" + "  LogicalProject(b=[$0], id=[$1], x=[$2])\n"
                + "    LogicalTableScan(table=[[hive, default, a]])\n"
                + "  LogicalProject(foobar=[$0], id=[$1], y=[$2])\n"
                + "    LogicalTableScan(table=[[hive, default, b]])\n"))
        .add(ImmutableList.of("select strpos('foobar', 'b') as pos",
            "LogicalProject(POS=[instr('FOOBAR', 'B')])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select foo(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select FOO(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select foo()",
            "LogicalProject(EXPR$0=[foo()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("call foo(3)",
            "LogicalProject(EXPR$0=[foo(3)])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("call foo()", "LogicalProject(EXPR$0=[foo()])\n" + "  LogicalValues(tuples=[[{ 0 }]])\n"))
        .add(ImmutableList.of("select count(*) from foo",
            "LogicalAggregate(group=[{}], EXPR$0=[COUNT()])\n" + "  LogicalProject($f0=[0])\n"
                + "    LogicalTableScan(table=[[hive, default, foo]])\n"))
        .add(ImmutableList.of("select count(*) as x from src group by k, v",
            "LogicalProject(X=[$2])\n" + "  LogicalAggregate(group=[{0, 1}], X=[COUNT()])\n"
                + "    LogicalProject(k=[$1], v=[$2])\n" + "      LogicalTableScan(table=[[hive, default, src]])\n"))
        .add(ImmutableList.of("select count(*) as x from src group by cube (k, v)",
            "LogicalProject(X=[$2])\n"
                + "  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], X=[COUNT()])\n"
                + "    LogicalProject(k=[$1], v=[$2])\n" + "      LogicalTableScan(table=[[hive, default, src]])\n"))
        .add(ImmutableList.of("select count(*) x from src group by rollup (k, v)",
            "LogicalProject(X=[$2])\n" + "  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {}]], X=[COUNT()])\n"
                + "    LogicalProject(k=[$1], v=[$2])\n" + "      LogicalTableScan(table=[[hive, default, src]])\n"))
        .build().stream().map(x -> new Object[] { x.get(0), x.get(1) }).iterator();
  }

  //TODO: Add unsupported SQL tests

  public static String relToStr(RelNode rel) {
    return RelOptUtil.toString(rel);
  }

  @Test(dataProvider = "support")
  public void testSupport(String trinoSql, String expected) {
    RelNode rel = converter.convertSql(trinoSql);
    String relString = relToStr(rel);

    assertEquals(relString, expected);
  }

}
