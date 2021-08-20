/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;
import static org.testng.Assert.*;


public class ParseTreeBuilderTest {

  private static SqlParser.Config parserConfig;
  private static HiveMetastoreClient msc;

  @BeforeClass
  public static void beforeClass() throws HiveException, IOException, MetaException {
    TestHive hive = setupDefaultHive();
    msc = new HiveMscAdapter(hive.getMetastoreClient());
    parserConfig = SqlParser.configBuilder().setCaseSensitive(true).setUnquotedCasing(Casing.UNCHANGED)
        .setQuotedCasing(Casing.UNCHANGED).build();
  }

  @DataProvider(name = "convertSQL")
  public Iterator<Object[]> getConvertSql() {
    List<String> convertSql = ImmutableList.of(
        "SELECT a, -c, a+c, (c>10), (c > 10 AND a < 25) from test.tableOne where c > 10 AND a < 15 OR b = 'abc'",
        "SELECT a, (32 * 18 / 15) from foo", "SELECT ''", "SELECT 1", "SELECT 1+10 * 30", "SELECT 1 - 2",
        "SELECT a, b from foo where 33 < 35", "SELECT a from foo where a between 10 and 30",
        "SELECT a from foo where b NOT BETWEEN 20 and 40",

        // empty string literal
        "SELECT * from test.tableOne where b = ''", "SELECT * from test.tableTwo order by x desc, y asc",
        // Group by, aggregate and having with lowercase 'and' operator
        "SELECT a, sum(c) as sum_c, count(*) AS counter from foo group by b, a having sum_c > 100 and counter < 10",
        // aggregate but no group by with uppercase aggregation
        "SELECT SUM(c), COUNT(*) from foo",
        // ORDER BY
        "SELECT a, b from foo order by a DESC",
        // complex types
        "SELECT c[0], s.name from complex",
        // nested non-correlated subquery
        "SELECT a, b from (select x as a, y as b from bar) f",
        // subquery with IN clause
        "SELECT a, b from foo where a in  (select x from bar where y < 10)",
        // is null, is not null
        "SELECT a from foo where a is null", "SELECT a from foo where a IS NOT NULL",
        // Case with and without null else
        "SELECT case when a = b then c end from foo", "SELECT case when a = b then c else d end from foo",

        // [NOT] exists
        "SELECT a,b from foo f where exists (select x from bar)",
        "SELECT a,b from foo AS f where NOT EXISTS (select x from bar)",

        // cast
        "SELECT cast(a as double) from foo", "SELECT cast(a as int) from foo", "SELECT cast(a as boolean) from foo",
        "SELECT cast(a as date) from foo", "SELECT cast(a as timestamp) from foo", "SELECT cast(a as float) from foo",
        "SELECT a as acol from foo",

        // limit
        "SELECT * from foo LIMIT 100",

        // join
        "SELECT * from foo join bar on foo.a = bar.b", "SELECT * from foo join bar",
        // Comma separated joins are not supported by the included Hive parser, though recent versions of Hive do
        // support this
        // "SELECT * from foo, bar on foo.a = bar.b",
        "SELECT * from foo left outer join bar on foo.a = bar.b",
        "SELECT * from foo right outer join bar on foo.a = bar.b",
        "SELECT * from foo full outer join bar on foo.a = bar.b",

        // subqeury
        "SELECT * from foo where a in (select a from bar)",

        // aggregation
        "SELECT distinct * from foo", "SELECT distinct a from foo", "SELECT a from foo group by a",
        "SELECT a, count(1) from foo group by a", "SELECT a, count(b) from foo group by a",
        "SELECT a, count(distinct b) from foo group by a",
        "SELECT a, count(distinct b), count(distinct c) from foo group by a",

        // order by
        "SELECT * from foo order by a",

        // outer parenthesis
        "( SELECT 1 AS c1 )", " ( SELECT 1 AS c1 ) ", "(( SELECT 1 AS c1 ))", "( ( SELECT 1 AS c1 ) )",
        "(( SELECT 1 AS c1 ) )", "( ( SELECT 1 AS c1 ))",
        "(SELECT a,b from foo AS f where NOT EXISTS (select x from bar))",

        //NiladicParentheses
        "SELECT current_timestamp", "SELECT current_date"

    // window
    // Not yet implemented
    // "SELECT a, rank() over (partition by b order by c asc) from foo group by a",
    );
    // We wrap the SQL to be tested here rather than wrap each SQL statement in the its own array in the constant
    return convertSql.stream().map(x -> new Object[] { x }).iterator();
  }

  @DataProvider(name = "validateSql")
  public Object[][] getValidateSql() {
    return new Object[][] {
        // hive doesn't support casting as varbinary
        { "SELECT cast(a as binary) from foo", "SELECT cast(`a` as binary) from `foo`" }, { "SELECT cast(a as string) from foo", "SELECT cast(`a` as varchar) from `foo`" }, { "SELECT a, c from foo union all select x, y from bar", "SELECT * FROM (SELECT `a`, `c` from `foo` union all SELECT `x`, `y` from `bar`) as `_u1`" }, { "SELECT case (a + 10) when 20 then 5 when 30 then 10 else 1 END from foo", "SELECT CASE WHEN `a` + 10 = 20 THEN 5 WHEN `a` + 10 = 30 THEN 10 ELSE 1 END from `foo`" }, { "SELECT CASE WHEN a THEN 10 WHEN b THEN 20 ELSE 30 END from foo", "SELECT CASE WHEN `a` THEN 10 WHEN `b` THEN 20 ELSE 30 END from `foo`" }, { "SELECT named_struct('abc', 123, 'def', 234.23) FROM foo", "SELECT `named_struct`('abc', 123, 'def', 234.23) FROM `foo`" }, { "SELECT 0L from foo", "SELECT 0 from `foo`" } };
  }

  @Test(dataProvider = "convertSQL")
  public void testConvertAndValidate(String sqlStatement) throws SqlParseException {
    SqlNode sqlNode = convert(sqlStatement);
    validateSql(sqlNode, sqlStatement);
  }

  @Test(dataProvider = "validateSql")
  public void testValidateBasicSQL(String input, String expected) {
    SqlNode sqlNode = convert(input);
    assertEquals(sqlNode.toString().toLowerCase().replaceAll("\n", " "), expected.toLowerCase());
  }

  private void validateSql(SqlNode sqlNode, String sql) throws SqlParseException {
    SqlParser parser = SqlParser.create(sql, parserConfig);
    SqlNode parsedNode = parser.parseQuery();
    assertEquals(sqlNode.toString(), parsedNode.toString(), String.format("Failed to validate sql: %s", sql));
  }

  private static SqlNode convert(String sql) {
    ParseTreeBuilder builder = new ParseTreeBuilder(msc, new ParseTreeBuilder.Config());
    return builder.processSql(sql);
  }
}
