package com.linkedin.coral.hive.hive2rel.parsetree;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.util.Litmus;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.apache.calcite.sql.parser.SqlParserPos.*;
import static org.testng.Assert.*;

// TODO: Add tests for lateral views and joins (and many more)
public class ParseTreeBuilderTest {

  private static SqlParser.Config parserConfig;

  @BeforeClass
  public static void beforeClass() throws HiveException, IOException {
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
            "SELECT CASE `a` + 10 when 20 then 5 when 30 then 10 else 1 END from `foo`" }
    };

    for (String[] s : sqlValidator) {
      validateSql(s[0], s[1]);
    }
  }

  @Test
  public void validateInList() {
    String sql = "SELECT a from foo where b in (10, 15, 20)";
    SqlIdentifier identifierA = new SqlIdentifier("a", ZERO);
    SqlIdentifier identifierB = new SqlIdentifier("b", ZERO);
    SqlNodeList projects = new SqlNodeList(ImmutableList.of(identifierA), ZERO);
    SqlIdentifier table = new SqlIdentifier("foo", ZERO);
    SqlNode inOperator = SqlStdOperatorTable.IN.createCall(ZERO,
        ImmutableList.of(
            identifierB,
            SqlLiteral.createExactNumeric("10", ZERO),
            SqlLiteral.createExactNumeric("15", ZERO),
            SqlLiteral.createExactNumeric("20", ZERO)
            ));
    SqlSelect expected = new SqlSelect(ZERO, null, projects, table, inOperator, null, null, null, null, null, null);
    SqlNode input = convert(sql);
    assertTrue(input.equalsDeep(expected, Litmus.THROW));

    sql = "SELECT a from foo where b NOT IN (10, 15, 20)";
    // hive turns 'b not in <List>' to 'not (b in <List>)'
    SqlNode notInOperator = SqlStdOperatorTable.NOT.createCall(ZERO, inOperator);
    expected = new SqlSelect(ZERO, null, projects, table, notInOperator, null, null, null, null, null, null);
    input = convert(sql);
    assertTrue(input.equalsDeep(expected, Litmus.THROW));
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
    ParseTreeBuilder builder = new ParseTreeBuilder();
    return builder.process(sql);
  }
}
