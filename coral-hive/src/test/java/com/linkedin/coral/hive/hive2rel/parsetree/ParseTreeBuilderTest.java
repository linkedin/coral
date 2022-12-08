/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.hive.hive2rel.TestUtils;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;
import static org.testng.Assert.*;


public class ParseTreeBuilderTest {

  private static SqlParser.Config parserConfig;
  private static HiveMetastoreClient msc;
  private static HiveConf conf;

  @BeforeClass
  public static void beforeClass() throws HiveException, IOException, MetaException {
    conf = TestUtils.loadResourceHiveConf();
    TestHive hive = setupDefaultHive(conf);
    msc = new HiveMscAdapter(hive.getMetastoreClient());
    parserConfig = SqlParser.configBuilder().setCaseSensitive(true).setUnquotedCasing(Casing.UNCHANGED)
        .setQuotedCasing(Casing.UNCHANGED).build();
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @DataProvider(name = "convertSQL")
  public Iterator<Object[]> getConvertSql() {
    List<String> convertSql = ImmutableList.of(
        "SELECT a, -c, a+c, (c>10), (c > 10 AND a < 25) from test.tableOne where c > 10 AND a < 15 OR b = 'abc'",
        "SELECT a, (32 * 18 / 15) from foo", "SELECT ''", "SELECT 1", "SELECT 1+10 * 30", "SELECT 1 - 2",
        "SELECT a, b from foo where 33 < 35", "SELECT a from foo where a between 10 and 30",
        "SELECT a from foo where b NOT BETWEEN 20 and 40",

        // interval
        "SELECT a + INTERVAL '7' DAY FROM test.tableOne", "SELECT a + INTERVAL '24' HOUR FROM test.tableOne",
        "SELECT a + INTERVAL '60' MINUTE FROM test.tableOne", "SELECT a + INTERVAL '20' SECOND FROM test.tableOne",
        "SELECT a + INTERVAL '12' MONTH FROM test.tableOne", "SELECT a + INTERVAL '7' YEAR FROM test.tableOne",

        // date and timestamp
        "SELECT CAST('2021-08-30' AS DATE) + INTERVAL '3' DAY FROM test.tableOne",
        "SELECT CAST('2021-08-30' AS TIMESTAMP) + INTERVAL '3' DAY FROM test.tableOne",
        "SELECT CAST('2021-08-31' AS TIMESTAMP) + INTERVAL '7 01:02:03' DAY TO SECOND FROM test.tableOne",
        "SELECT CAST('2021-08-31' AS TIMESTAMP) + INTERVAL '-7 01:02:03' DAY TO SECOND FROM test.tableOne",
        "SELECT CAST('2021-08-31' AS TIMESTAMP) + INTERVAL '1-6' YEAR TO MONTH FROM test.tableOne",
        "SELECT CAST('2021-08-31' AS TIMESTAMP) + INTERVAL '-1-6' YEAR TO MONTH FROM test.tableOne",

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
        "SELECT foo.*, bar.b from foo join bar on foo.a = bar.b",

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
        "SELECT current_timestamp", "SELECT current_date",

        // window
        "SELECT a, RANK() OVER (PARTITION BY b ORDER BY c asc) FROM foo GROUP BY a",
        "SELECT ROW_NUMBER() OVER (ORDER BY b) AS rid FROM foo", "SELECT a, MAX(b) OVER () AS max_b FROM foo",
        "SELECT DENSE_RANK() OVER (PARTITION BY a ORDER BY b DESC) AS rid FROM foo",
        "SELECT CUME_DIST() OVER (PARTITION BY a ORDER BY b DESC) FROM foo",
        "SELECT AVG(c) OVER (PARTITION BY a ORDER BY b ROWS UNBOUNDED PRECEDING) AS min_c FROM foo",
        "SELECT FIRST_VALUE(c) OVER (PARTITION BY a ORDER BY b RANGE UNBOUNDED PRECEDING) AS min_c FROM foo",
        "SELECT LAST_VALUE(c) OVER (PARTITION BY a ORDER BY b ROWS BETWEEN 1 PRECEDING AND CURRENT ROW) AS min_c FROM foo",
        "SELECT STDDEV(c) OVER (PARTITION BY a ORDER BY b RANGE BETWEEN CURRENT ROW AND 1 FOLLOWING) AS min_c FROM foo",
        "SELECT VARIANCE(c) OVER (PARTITION BY a ORDER BY b ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS min_c FROM foo");
    // We wrap the SQL to be tested here rather than wrap each SQL statement in the its own array in the constant
    return convertSql.stream().map(x -> new Object[] { x }).iterator();
  }

  @DataProvider(name = "validateSql")
  public Iterator<Object[]> getValidateSql() {
    List<List<String>> convertAndValidateSql = ImmutableList.of(
        // test lateral view explode with an array
        ImmutableList.of(
            "SELECT col FROM (SELECT ARRAY('v1', 'v2') as arr) tmp LATERAL VIEW EXPLODE(arr) arr_alias AS col",
            "SELECT `col` FROM (SELECT ARRAY['v1', 'v2'] AS `arr`) AS `tmp`, LATERAL(UNNEST(`arr`)) AS `arr_alias` (`col`)"),

        // hive automatically creates column aliases `col` when the type is an array
        ImmutableList.of("SELECT col FROM (SELECT ARRAY('v1', 'v2') as arr) tmp LATERAL VIEW EXPLODE(arr) arr_alias",
            "SELECT `col` FROM (SELECT ARRAY['v1', 'v2'] AS `arr`) AS `tmp`, LATERAL(UNNEST(`arr`)) AS `arr_alias`"),

        // test lateral view posexplode with an array
        ImmutableList.of(
            "SELECT col FROM (SELECT ARRAY('v1', 'v2') as arr) tmp LATERAL VIEW POSEXPLODE(arr) arr_alias AS col, val",
            "SELECT `col` FROM (SELECT ARRAY['v1', 'v2'] as `arr`) as `tmp`, lateral(unnest(`arr`) with ordinality) as `arr_alias` (`val`, `col`)"),

        // hive automatically creates column aliases `col` when the type is an array
        ImmutableList.of("SELECT col FROM (SELECT ARRAY('v1', 'v2') as arr) tmp LATERAL VIEW POSEXPLODE(arr) arr_alias",
            "SELECT `col` FROM (SELECT array['v1', 'v2'] as `arr`) as `tmp`, lateral(unnest(`arr`) with ordinality) as `arr_alias`"),

        // test lateral view explode with a map
        ImmutableList.of(
            "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value",
            "SELECT `key`, `value` FROM (SELECT MAP['key1', 'value1'] AS `m`) AS `tmp`, LATERAL(UNNEST(`m`)) AS `m_alias` (`key`, `value`)"),

        // hive automatically creates column aliases `key` and `value` when the type is a map
        ImmutableList.of(
            "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias",
            "SELECT `key`, `value` FROM (SELECT MAP['key1', 'value1'] AS `m`) AS `tmp`, LATERAL(UNNEST(`m`)) AS `m_alias`"),

        // hive doesn't support casting as varbinary
        ImmutableList.of("SELECT cast(a as binary) FROM foo", "SELECT cast(`a` as binary) FROM `foo`"),
        ImmutableList.of("SELECT cast(a as string) FROM foo", "SELECT cast(`a` as varchar) FROM `foo`"),
        ImmutableList.of("SELECT a, c FROM foo union all SELECT x, y FROM bar",
            "SELECT * FROM (SELECT `a`, `c` FROM `foo` union all SELECT `x`, `y` FROM `bar`) as `_u1`"),
        ImmutableList.of("SELECT case (a + 10) when 20 then 5 when 30 then 10 else 1 END FROM foo",
            "SELECT CASE WHEN `a` + 10 = 20 THEN 5 WHEN `a` + 10 = 30 THEN 10 ELSE 1 END FROM `foo`"),
        ImmutableList.of("SELECT CASE WHEN a THEN 10 WHEN b THEN 20 ELSE 30 END FROM foo",
            "SELECT CASE WHEN `a` THEN 10 WHEN `b` THEN 20 ELSE 30 END FROM `foo`"),
        ImmutableList.of("SELECT named_struct('abc', 123, 'def', 234.23) FROM foo",
            "SELECT `named_struct`('abc', 123, 'def', 234.23) FROM `foo`"),
        ImmutableList.of("SELECT 0L FROM foo", "SELECT 0 FROM `foo`"));

    return convertAndValidateSql.stream().map(x -> new Object[] { x.get(0), x.get(1) }).iterator();
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

  private SqlNode convert(String sql) {
    HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(msc);
    return hiveToRelConverter.toSqlNode(sql);
  }

  /**
   * OUTER EXPLODE without column aliases are not supported yet.
   * See details in {@link ParseTreeBuilder#visitLateralViewExplode(List, List, SqlCall, boolean)}
   */
  @Test(expectedExceptions = { java.lang.IllegalStateException.class })
  public void testUnsupportedOuterExplodeWithoutColumns() {
    String input = "SELECT col FROM (SELECT ARRAY('v1', 'v2') as arr) tmp LATERAL VIEW OUTER EXPLODE(arr) arr_alias";
    String expected = "";
    SqlNode sqlNode = convert(input);
    assertEquals(sqlNode.toString().toLowerCase().replaceAll("\n", " "), expected.toLowerCase());
  }
}
