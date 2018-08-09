package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import java.io.IOException;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.TestUtils.*;
import static org.testng.Assert.*;

// TODO: Add tests for lateral views and joins (and many more)
public class ParseTreeBuilderTest {

  private static SqlParser.Config parserConfig;
  private static TestHive hive;
  private static HiveMetastoreClient msc;

  @BeforeClass
  public static void beforeClass() throws HiveException, IOException, MetaException {
    hive = setupDefaultHive();
    msc = new HiveMscAdapter(hive.getMetastoreClient());
    parserConfig = SqlParser.configBuilder()
        .setCaseSensitive(true)
        .setUnquotedCasing(Casing.UNCHANGED)
        .setQuotedCasing(Casing.UNCHANGED)
        .build();
  }

  @Test
  public void testBasicSql() throws SqlParseException {
    String[] sql = {
        "SELECT a, -c, a+c, (c>10), (c > 10 AND a < 25) from test.tableOne where c > 10 AND a < 15 OR b = 'abc'",
        "SELECT a, (32 * 18 / 15) from foo",
        "SELECT ''",
        "SELECT 1",
        "SELECT 1+10 * 30",
        "SELECT a, b from foo where 33 < 35",
        "SELECT a from foo where a between 10 and 30",
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
        "SELECT a from foo where a is null",
        "SELECT a from foo where a IS NOT NULL",

        // [NOT] exists
        "SELECT a,b from foo f where exists (select x from bar)",
        "SELECT a,b from foo AS f where NOT EXISTS (select x from bar)",

        // cast
        "SELECT cast(a as double) from foo",
        "SELECT cast(a as int) from foo",
        "SELECT cast(a as boolean) from foo",
        "SELECT cast(a as date) from foo",
        "SELECT cast(a as timestamp) from foo",
        "SELECT cast(a as float) from foo",
        "SELECT a as acol from foo",

        // limit
        "SELECT * from foo LIMIT 100"
    };

    for (String s : sql) {
      convertAndValidate(s);
    }

    String[][] sqlValidator = new String[][] {
        { "SELECT cast(a as binary) from foo", "SELECT cast(`a` as varbinary) from `foo`" },
        { "SELECT cast(a as string) from foo", "SELECT cast(`a` as varchar) from `foo`" },
        { "SELECT a, c from foo union all select x, y from bar",
            "SELECT * FROM (SELECT `a`, `c` from `foo` union SELECT `x`, `y` from `bar`) as `_u1`" },
        { "SELECT case (a + 10) when 20 then 5 when 30 then 10 else 1 END from foo",
            "SELECT CASE `a` + 10 when 20 then 5 when 30 then 10 else 1 END from `foo`" },
        { "SELECT CASE WHEN a THEN 10 WHEN b THEN 20 ELSE 30 END from foo",
            "SELECT CASE WHEN `a` THEN 10 WHEN `b` THEN 20 ELSE 30 END from `foo`"},
        {
          "SELECT named_struct('abc', 123, 'def', 234.23) FROM foo",
            "SELECT `named_struct`('abc', 123, 'def', 234.23) FROM `foo`"
        },
        {"SELECT 0L from foo", "SELECT 0 from `foo`"}
    };

    for (String[] s : sqlValidator) {
      validateSql(s[0], s[1]);
    }
  }

  private void validateSql(String input, String expected) throws SqlParseException {
    SqlNode sqlNode = convert(input);
    assertEquals(sqlNode.toString().toLowerCase().replaceAll("\n", " "),
        expected.toLowerCase());
  }

  private void convertAndValidate(String sql) throws SqlParseException {
    SqlNode sqlNode = convert(sql);
    validateSql(sqlNode, sql);
  }

  private void validateSql(SqlNode sqlNode, String sql) throws SqlParseException {
    SqlParser parser = SqlParser.create(sql, parserConfig);
    SqlNode parsedNode = parser.parseQuery();
    assertEquals(sqlNode.toString(), parsedNode.toString(),
        String.format("Failed to validate sql: %s", sql));
  }

  private static SqlNode convert(String sql) {
    ParseTreeBuilder builder = new ParseTreeBuilder(msc, new ParseTreeBuilder.Config());
    return builder.processSql(sql);
  }
}
