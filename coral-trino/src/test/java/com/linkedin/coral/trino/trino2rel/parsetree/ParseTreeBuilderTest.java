/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel.parsetree;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlNode;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.AssertJUnit.assertEquals;


public class ParseTreeBuilderTest {
  private final ParserVisitorContext context = new ParserVisitorContext();
  private final ParseTreeBuilder builder = new ParseTreeBuilder();

  @DataProvider(name = "support")
  public Iterator<Object[]> getSupportedSql() {
    return ImmutableList.<List<String>> builder().add(ImmutableList.of("select * from foo", "SELECT * FROM `FOO`"))
        .add(ImmutableList.of("select * from foo /* end */", "SELECT * FROM `FOO`"))
        .add(ImmutableList.of("/* start */ select * from foo", "SELECT * FROM `FOO`"))
        .add(ImmutableList.of("/* start */ select * /* middle */ from foo /* end */", "SELECT * FROM `FOO`"))
        .add(ImmutableList.of("-- start \n select * -- junk -- hi\n from foo -- done", "SELECT * FROM `FOO`"))
        .add(ImmutableList.of("select * from foo a (x, y, z)", "SELECT * FROM `FOO` AS `A` (`X`, `Y`, `Z`)"))
        .add(ImmutableList.of("select *, 123, * from foo", "SELECT *, 123, * FROM `FOO`"))
        .add(ImmutableList.of("select show from foo", "SELECT `SHOW` FROM `FOO`"))
        .add(ImmutableList.of("select extract(day from x), extract(dow from x) from y",
            "SELECT EXTRACT(DAY FROM `X`), EXTRACT(DOW FROM `X`) FROM `Y`"))
        .add(ImmutableList.of("select 1 + 13 || '15' from foo", "SELECT `concat`(1 + 13, '15') FROM `FOO`"))
        .add(ImmutableList.of("select x is distinct from y from foo where a is not distinct from b",
            "SELECT `X` IS DISTINCT FROM `Y` FROM `FOO` WHERE NOT `A` IS DISTINCT FROM `B`"))
        .add(ImmutableList.of("select x[1] from my_table", "SELECT `X`[1] FROM `MY_TABLE`"))
        .add(ImmutableList.of("select x[1][2] from my_table", "SELECT `X`[1][2] FROM `MY_TABLE`"))
        .add(ImmutableList.of("select x[cast(10 * sin(x) as bigint)] from my_table",
            "SELECT `X`[CAST(10 * SIN(`X`) AS BIGINT)] FROM `MY_TABLE`"))
        .add(ImmutableList.of("select * from unnest(t.my_array)", "SELECT * FROM UNNEST(`T`.`MY_ARRAY`)"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3])", "SELECT * FROM UNNEST(ARRAY[1, 2, 3])"))
        .add(ImmutableList.of("select x from unnest(array[1, 2, 3]) t(x)",
            "SELECT `X` FROM UNNEST(ARRAY[1, 2, 3]) AS `T` (`X`)"))
        .add(ImmutableList.of("select * from users cross join unnest(friends)",
            "SELECT * FROM `USERS` CROSS JOIN UNNEST(`FRIENDS`)"))
        .add(ImmutableList.of("select id, friend from users cross join unnest(friends) t(friend)",
            "SELECT `ID`, `FRIEND` FROM `USERS` CROSS JOIN UNNEST(`FRIENDS`) AS `T` (`FRIEND`)"))
        .add(ImmutableList.of("select * from unnest(t.my_array) with ordinality",
            "SELECT * FROM UNNEST(`T`.`MY_ARRAY`) WITH ORDINALITY"))
        .add(ImmutableList.of("select * from unnest(array[1, 2, 3]) with ordinality",
            "SELECT * FROM UNNEST(ARRAY[1, 2, 3]) WITH ORDINALITY"))
        .add(ImmutableList.of("select x from unnest(array[1, 2, 3]) with ordinality t(x)",
            "SELECT `X` FROM UNNEST(ARRAY[1, 2, 3]) WITH ORDINALITY AS `T` (`X`)"))
        .add(ImmutableList.of("select * from users cross join unnest(friends) with ordinality",
            "SELECT * FROM `USERS` CROSS JOIN UNNEST(`FRIENDS`) WITH ORDINALITY"))
        .add(ImmutableList.of("select id, friend from users cross join unnest(friends) with ordinality t(friend)",
            "SELECT `ID`, `FRIEND` FROM `USERS` CROSS JOIN UNNEST(`FRIENDS`) WITH ORDINALITY AS `T` (`FRIEND`)"))
        .add(ImmutableList.of("select count(*) x from src group by k, v",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY `K`, `V`"))
        .add(ImmutableList.of("select count(*) x from src group by cube (k, v)",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY CUBE(`K`, `V`)"))
        .add(ImmutableList.of("select count(*) x from src group by rollup (k, v)",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY ROLLUP(`K`, `V`)"))
        .add(ImmutableList.of("select count(*) x from src group by grouping sets ((k, v))",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY GROUPING SETS((`K`, `V`))"))
        .add(ImmutableList.of("select count(*) x from src group by grouping sets ((k, v), (v))",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY GROUPING SETS((`K`, `V`), `V`)"))
        .add(ImmutableList.of("select count(*) x from src group by grouping sets (k, v, k)",
            "SELECT COUNT(*) AS `X` FROM `SRC` GROUP BY GROUPING SETS(`K`, `V`, `K`)"))
        .add(ImmutableList.of(
            "select depname, empno, salary , count(*) over () , avg(salary) over (partition by depname) , rank() over (partition by depname order by salary desc) , sum(salary) over (order by salary rows unbounded preceding) , sum(salary) over (partition by depname order by salary rows between current row and 3 following) , sum(salary) over (partition by depname range unbounded preceding) , sum(salary) over (rows between 2 preceding and unbounded following) , lag(salary, 1) ignore nulls over (partition by depname) , lag(salary, 1) respect nulls over (partition by depname) from emp",
            "SELECT `DEPNAME`, `EMPNO`, `SALARY`, COUNT(*) OVER (), AVG(`SALARY`) OVER (PARTITION BY `DEPNAME`), RANK() OVER (PARTITION BY `DEPNAME` ORDER BY `SALARY` DESC), SUM(`SALARY`) OVER (ORDER BY `SALARY` ROWS UNBOUNDED_PRECEDING), SUM(`SALARY`) OVER (PARTITION BY `DEPNAME` ORDER BY `SALARY` ROWS BETWEEN CURRENT_ROW AND `FOLLOWING`(3)), SUM(`SALARY`) OVER (PARTITION BY `DEPNAME` RANGE UNBOUNDED_PRECEDING), SUM(`SALARY`) OVER (ROWS BETWEEN `PRECEDING`(2) AND UNBOUNDED_FOLLOWING), LAG(`SALARY`, 1) OVER (PARTITION BY `DEPNAME`), LAG(`SALARY`, 1) OVER (PARTITION BY `DEPNAME`) FROM `EMP`"))
        .add(ImmutableList.of(
            "with a (id) as (with x as (select 123 from z) select * from x)    , b (id) as (select 999 from z) select * from a join b using (id)",
            "WITH `A` (`ID`) AS (WITH `X` AS (SELECT 123 FROM `Z`) (SELECT * FROM `X`)), `B` (`ID`) AS (SELECT 999 FROM `Z`) (SELECT * FROM `A` INNER JOIN `B` USING (`ID`))"))
        .add(ImmutableList.of("select * from information_schema.tables", "SELECT * FROM `INFORMATION_SCHEMA`.`TABLES`"))
        .add(ImmutableList.of("select cast('123' as bigint)", "SELECT CAST('123' AS BIGINT)"))
        .add(ImmutableList.of("select * from a.b.c", "SELECT * FROM `A`.`B`.`C`"))
        .add(ImmutableList.of("select * from a.b.c.e.f.g", "SELECT * FROM `A`.`B`.`C`.`E`.`F`.`G`"))
        .add(ImmutableList.of("select TOTALPRICE \"my price\" from \"$MY\" \"ORDERS\"",
            "SELECT `TOTALPRICE` AS `MY PRICE` FROM `$MY` as `ORDERS`"))
        .add(ImmutableList.of(
            "select * from foo tablesample system (10) join bar tablesample bernoulli (30) on a.id = b.id",
            "SELECT * FROM `FOO` TABLESAMPLE SYSTEM(10.000000149011612) INNER JOIN `BAR` TABLESAMPLE BERNOULLI(30.000001192092896) ON `A`.`ID` = `B`.`ID`"))
        .add(ImmutableList.of(
            "select * from foo tablesample system (10) join bar tablesample bernoulli (30) on not(a.id > b.id)",
            "SELECT * FROM `FOO` TABLESAMPLE SYSTEM(10.000000149011612) INNER JOIN `BAR` TABLESAMPLE BERNOULLI(30.000001192092896) ON NOT `A`.`ID` > `B`.`ID`"))
        .add(ImmutableList.of("select * from a limit all", "SELECT * FROM `A`"))
        .add(ImmutableList.of("select * from a order by x limit all", "SELECT * FROM `A` ORDER BY `X`"))
        .add(ImmutableList.of("select * from a union select * from b", "SELECT * FROM `A` UNION SELECT * FROM `B`"))
        .add(ImmutableList.of("call foo()", "CALL `foo`()"))
        .add(ImmutableList.of("call foo(123, a => 1, b => 'go', 456)", "CALL `foo`(123, 1, 'GO', 456)"))
        .add(ImmutableList.of("SELECT * FROM table1 WHERE a >= ALL (VALUES 2, 3, 4)",
            "SELECT * FROM `TABLE1` WHERE `A` >= ALL (VALUES 2, 3, 4)"))
        .add(ImmutableList.of("SELECT * FROM table1 WHERE a <> ANY (SELECT 2, 3, 4)",
            "SELECT * FROM `TABLE1` WHERE `A` <> SOME (SELECT 2, 3, 4)"))
        .add(ImmutableList.of("SELECT * FROM table1 WHERE a = SOME (SELECT id FROM table2)",
            "SELECT * FROM `TABLE1` WHERE `A` = SOME (SELECT `ID` FROM `TABLE2`)"))
        .build().stream().map(x -> new Object[] { x.get(0), x.get(1) }).iterator();
  }

  @Test
  public void testUnSupport() {
    ImmutableList<String> sqls = ImmutableList.<String> builder().add("select count(*) filter (where x > 4) y from t")
        .add("select sum(x) filter (where x > 4) y from t")
        .add("select sum(x) filter (where x > 4) y, sum(x) filter (where x < 2) z from t")
        .add("select sum(distinct x) filter (where x > 4) y, sum(x) filter (where x < 2) z from t")
        .add("select sum(x) filter (where x > 4) over (partition by y) z from t")
        .add("with recursive t as (select * from x) select * from t").add("select * from foo tablesample system (10+1)")
        .add("show catalogs").add("show schemas").add("show schemas from sys").add("show tables")
        .add("show tables from information_schema").add("show tables like '%'")
        .add("show tables from information_schema like '%'").add("show functions").add("explain select * from foo")
        .add("explain (type distributed, format graphviz) select * from foo")
        .add("create table foo as (select * from abc)").add("create table if not exists foo as (select * from abc)")
        .add("create table foo with (a = 'apple', b = 'banana') as select * from abc")
        .add("create table foo comment 'test' with (a = 'apple') as select * from abc")
        .add("create table foo as select * from abc WITH NO DATA")
        .add("create table foo as (with t(x) as (values 1) select x from t)")
        .add("create table if not exists foo as (with t(x) as (values 1) select x from t)")
        .add("create table foo as (with t(x) as (values 1) select x from t) WITH DATA")
        .add("create table if not exists foo as (with t(x) as (values 1) select x from t) WITH DATA")
        .add("create table foo as (with t(x) as (values 1) select x from t) WITH NO DATA")
        .add("create table if not exists foo as (with t(x) as (values 1) select x from t) WITH NO DATA")
        .add("create table foo(a) as (with t(x) as (values 1) select x from t)")
        .add("create table if not exists foo(a) as (with t(x) as (values 1) select x from t)")
        .add("create table foo(a) as (with t(x) as (values 1) select x from t) WITH DATA")
        .add("create table if not exists foo(a) as (with t(x) as (values 1) select x from t) WITH DATA")
        .add("create table foo(a) as (with t(x) as (values 1) select x from t) WITH NO DATA")
        .add("create table if not exists foo(a) as (with t(x) as (values 1) select x from t) WITH NO DATA")
        .add("drop table foo").add("insert into foo select * from abc").add("delete from foo")
        .add("delete from foo where a = b").add("alter table foo rename to bar")
        .add("alter table a.b.c rename to d.e.f").add("alter table a.b.c rename column x to y")
        .add("alter table a.b.c add column x bigint").add("alter table a.b.c add column x bigint comment 'large x'")
        .add("alter table a.b.c add column x bigint with (weight = 2)")
        .add("alter table a.b.c add column x bigint comment 'xtra' with (compression = 'LZ4', special = true)")
        .add("alter table a.b.c drop column x").add("create schema test").add("create schema if not exists test")
        .add("create schema test with (a = 'apple', b = 123)").add("drop schema test").add("drop schema test cascade")
        .add("drop schema if exists test").add("drop schema if exists test restrict")
        .add("alter schema foo rename to bar").add("alter schema foo.bar rename to baz")
        .add("create table test (a boolean, b bigint, c double, d varchar, e timestamp)")
        .add("create table test (a boolean, b bigint comment 'test')")
        .add("create table if not exists baz (a timestamp, b varchar)")
        .add("create table test (a boolean, b bigint) with (a = 'apple', b = 'banana')")
        .add("create table test (a boolean, b bigint) comment 'test' with (a = 'apple')")
        .add(
            "create table test (a boolean with (a = 'apple', b = 'banana'), b bigint comment 'bla' with (c = 'cherry')) comment 'test' with (a = 'apple')")
        .add("drop table test").add("create view foo as with a as (select 123) select * from a")
        .add("create or replace view foo as select 123 from t").add("drop view foo")
        .add("insert into t select * from t").add("insert into t (c1, c2) select * from t").add("start transaction")
        .add("start transaction isolation level read uncommitted")
        .add("start transaction isolation level read committed")
        .add("start transaction isolation level repeatable read").add("start transaction isolation level serializable")
        .add("start transaction read only").add("start transaction read write")
        .add("start transaction isolation level read committed, read only")
        .add("start transaction read only, isolation level read committed")
        .add("start transaction read write, isolation level serializable").add("commit").add("commit work")
        .add("rollback").add("rollback work").add("grant select on foo to alice with grant option")
        .add("grant all privileges on foo to alice").add("grant delete, select on foo to role public")
        .add("revoke grant option for select on foo from alice").add("revoke all privileges on foo from alice")
        .add("revoke insert, delete on foo from role public").add("show grants on table t").add("show grants on t")
        .add("show grants").add("show roles").add("show roles from foo").add("show current roles")
        .add("show current roles from foo").add("show role grants").add("show role grants from foo")
        .add("prepare p from select * from (select * from T) \"A B\"")
        .add("select count(*) filter (where x > 4) y from t").add("select sum(x) filter (where x > 4) y from t")
        .add("select sum(x) filter (where x > 4) y, sum(x) filter (where x < 2) z from t")
        .add("select sum(distinct x) filter (where x > 4) y, sum(x) filter (where x < 2) z from t")
        .add("select sum(x) filter (where x > 4) over (partition by y) z from t")
        .add("select * from foo tablesample system (10+1)").add("with recursive t as (select * from x) select * from t")
        .build();
    int unsupportedExceptionCount = 0;
    for (String sql : sqls) {
      try {
        convert(sql);
      } catch (UnhandledASTNodeException e) {
        unsupportedExceptionCount++;
      }
    }
    assertEquals(sqls.size(), unsupportedExceptionCount);
  }

  @Test(dataProvider = "support")
  public void testSupport(String trinoSql, String expected) {
    SqlNode node = TrinoParserDriver.parse(trinoSql.toUpperCase()).accept(builder, context);
    assertEquals(expected.toLowerCase(), node.toString().toLowerCase().replaceAll("\n", " "));
  }

  @Test
  public void testProductCases() throws Exception {
    File srcFolder = new File("./src/test/resources/product_test_cases");
    String[] files = srcFolder.list();
    for (String file : files) {
      String sql = loadResource(format("product_test_cases/%s", file));
      SqlNode node = convert(sql);
      assertEquals(loadResource(format("product_test_cases_expected/%s_expected.sql", file.split("\\.")[0])),
          node.toString().toLowerCase());
    }
  }

  @Test
  public void testComplexQuery() throws Exception {
    for (int i = 1; i < 23; i++) {
      String sql = loadResource(format("queries/%d.sql", i));
      String expected = loadResource(format("queries/%d_expected.sql", i));
      SqlNode node = convert(sql);
      assertEquals(expected, node.toString().toLowerCase());
    }
  }

  private SqlNode convert(String prestoSql) {
    return TrinoParserDriver.parse(prestoSql.toUpperCase()).accept(builder, context);
  }

  public static String loadResource(String fileName) throws IOException {
    try (InputStream inputStream = ParseTreeBuilderTest.class.getClassLoader().getResourceAsStream(fileName)) {
      return IOUtils.toString(inputStream, UTF_8);
    }
  }
}
