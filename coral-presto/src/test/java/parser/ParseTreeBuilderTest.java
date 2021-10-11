/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlNode;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import com.linkedin.coral.presto.parser.CalciteSqlFormatter;
import com.linkedin.coral.presto.parser.ParseTreeBuilder;
import com.linkedin.coral.presto.parser.ParserVisitorContext;
import com.linkedin.coral.presto.parser.PrestoParserDriver;
import com.linkedin.coral.trino.rel2trino.TrinoSqlDialect;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.AssertJUnit.assertEquals;


public class ParseTreeBuilderTest {
  private final ParserVisitorContext context = new ParserVisitorContext();
  private final ParseTreeBuilder builder = new ParseTreeBuilder();

  @Test
  public void testUnSupport() throws Exception {
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
        convertCheck(sql);
      } catch (UnsupportedOperationException e) {
        unsupportedExceptionCount++;
      }
    }
    assertEquals(sqls.size(), unsupportedExceptionCount);
  }

  @Test
  public void testSupport() throws Exception {
    convertCheck("select * from foo");
    convertCheck("select * from foo /* end */");
    convertCheck("/* start */ select * from foo");
    convertCheck("/* start */ select * /* middle */ from foo /* end */");
    convertCheck("-- start\nselect * -- junk\n-- hi\nfrom foo -- done");
    convertCheck("select * from foo a (x, y, z)");
    convertCheck("select *, 123, * from foo");
    convertCheck("select \"show\" from foo");
    convertCheck("select extract(day from x), extract(dow from x) from y");
    convertCheck("select 1 + 13 || '15' from foo");
    convertCheck("select x is distinct from y from foo where a is not distinct from b");
    convertCheck("select x[1] from my_table");
    convertCheck("select x[1][2] from my_table");
    convertCheck("select x[cast(10 * sin(x) as bigint)] from my_table");
    convertCheck("select * from unnest(t.my_array)");
    convertCheck("select * from unnest(array[1, 2, 3])");
    convertCheck("select x from unnest(array[1, 2, 3]) t(x)");
    convertCheck("select * from users cross join unnest(friends)");
    convertCheck("select id, friend from users cross join unnest(friends) t(friend)");
    convertCheck("select * from unnest(t.my_array) with ordinality");
    convertCheck("select * from unnest(array[1, 2, 3]) with ordinality");
    convertCheck("select x from unnest(array[1, 2, 3]) with ordinality t(x)");
    convertCheck("select * from users cross join unnest(friends) with ordinality");
    convertCheck("select id, friend from users cross join unnest(friends) with ordinality t(friend)");

    convertCheck("select count(*) x from src group by k, v");
    convertCheck("select count(*) x from src group by cube (k, v)");
    convertCheck("select count(*) x from src group by rollup (k, v)");
    convertCheck("select count(*) x from src group by grouping sets ((k, v))");
    convertCheck("select count(*) x from src group by grouping sets ((k, v), (v))");
    convertCheck("select count(*) x from src group by grouping sets (k, v, k)");

    convertCheck("" + "select depname, empno, salary\n" + ", count(*) over ()\n"
        + ", avg(salary) over (partition by depname)\n" + ", rank() over (partition by depname order by salary desc)\n"
        + ", sum(salary) over (order by salary rows unbounded preceding)\n"
        + ", sum(salary) over (partition by depname order by salary rows between current row and 3 following)\n"
        + ", sum(salary) over (partition by depname range unbounded preceding)\n"
        + ", sum(salary) over (rows between 2 preceding and unbounded following)\n"
        + ", lag(salary, 1) ignore nulls over (partition by depname)\n"
        + ", lag(salary, 1) respect nulls over (partition by depname)\n" + "from emp");

    convertCheck("" + "with a (id) as (with x as (select 123 from z) select * from x) "
        + "   , b (id) as (select 999 from z) " + "select * from a join b using (id)");
    convertCheck("select * from information_schema.tables");
    convertCheck("select cast('123' as bigint)");
    convertCheck("select * from a.b.c");
    convertCheck("select * from a.b.c.e.f.g");
    convertCheck("select \"TOTALPRICE\" \"my price\" from \"$MY\"\"ORDERS\"");
    convertCheck("select * from foo tablesample system (10) join bar tablesample bernoulli (30) on a.id = b.id");
    convertCheck("select * from foo tablesample system (10) join bar tablesample bernoulli (30) on not(a.id > b.id)");
    convertCheck("select * from a limit all");
    convertCheck("select * from a order by x limit all");
    convertCheck("select * from a union select * from b");
    convertCheck("call foo()");
    convertCheck("call foo(123, a => 1, b => 'go', 456)");
    convertCheck("SELECT * FROM table1 WHERE a >= ALL (VALUES 2, 3, 4)");
    convertCheck("SELECT * FROM table1 WHERE a <> ANY (SELECT 2, 3, 4)");
    convertCheck("SELECT * FROM table1 WHERE a = SOME (SELECT id FROM table2)");

  }

  @Test
  public void testProductCases() throws Exception {
    File srcFolder = new File("./src/test/resources/product_test_cases");
    String[] files = srcFolder.list();
    for (String file : files) {
      String sql = loadResource(format("product_test_cases/%s", file));
      convertCheck(sql);
    }
  }

  @Test
  public void testComplexQuery() throws Exception {
    for (int i = 1; i < 23; i++) {
      String sql = loadResource(format("queries/%d.sql", i));
      convertCheck(sql);
    }
  }

  private void convertCheck(String prestoSql) {
    SqlNode node = PrestoParserDriver.parse(prestoSql.toUpperCase()).accept(builder, context);
    System.out.println(CalciteSqlFormatter.FORMATTER.format(node.toSqlString(TrinoSqlDialect.INSTANCE).getSql()));
  }

  public static String loadResource(String fileName) throws IOException {
    try (InputStream inputStream = ParseTreeBuilderTest.class.getClassLoader().getResourceAsStream(fileName)) {
      return IOUtils.toString(inputStream, UTF_8);
    }
  }
}
