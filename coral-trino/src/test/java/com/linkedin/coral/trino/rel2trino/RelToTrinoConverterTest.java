/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import coral.shading.io.trino.sql.parser.ParsingOptions;
import coral.shading.io.trino.sql.parser.SqlParser;
import coral.shading.io.trino.sql.tree.Statement;

import static com.linkedin.coral.trino.rel2trino.TestTable.*;
import static com.linkedin.coral.trino.rel2trino.TestUtils.*;
import static org.testng.Assert.*;


/**
 * Tests conversion from Calcite RelNode to Trino's SQL
 */
// All tests use a starting sql and use calcite parser to generate parse tree.
// This makes it easier to generate RelNodes for testing. The input sql is
// in Calcite sql syntax (not Hive)
// Disabled tests are failing tests
public class RelToTrinoConverterTest {

  static FrameworkConfig config;
  static final SqlParser trinoParser = new SqlParser();
  private HiveConf hiveConf;

  @BeforeTest
  public void beforeTest() throws HiveException, MetaException, IOException {
    TestUtils.turnOffRelSimplification();
    config = TestUtils.createFrameworkConfig(TABLE_ONE, TABLE_TWO, TABLE_THREE, TABLE_FOUR);
    hiveConf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeTablesAndViews(hiveConf);
  }

  private void testConversion(String inputSql, String expectedSql) {
    String trinoSql = toTrinoSql(inputSql);
    validate(trinoSql, expectedSql);
  }

  private void validate(String trinoSql, String expected) {
    try {
      Statement statement =
          trinoParser.createStatement(trinoSql, new ParsingOptions(ParsingOptions.DecimalLiteralTreatment.AS_DECIMAL));
      assertNotNull(statement);
    } catch (Exception e) {
      fail("Failed to parse sql: " + trinoSql);
    }
    assertEquals(trinoSql, expected);
  }

  private String toTrinoSql(String sql) {
    RelToTrinoConverter converter = TestUtils.getRelToTrinoConverter();
    RelNode relNode = TestUtils.getHiveToRelConverter().convertSql(sql);
    return converter.convert(relNode);
  }

  @Test
  public void testSimpleSelect() {
    String sql =
        "SELECT scol, sum(icol) as s from test.tableOne where dcol > 3.0 AND icol < 5 group by scol having sum(icol) > 10"
            + " order by scol ASC";

    String expectedSql = "SELECT \"tableOne\".\"scol\" AS \"SCOL\", SUM(\"tableOne\".\"icol\") AS \"S\"\n"
        + "FROM \"tableOne\" AS \"tableOne\"\n" + "WHERE \"tableOne\".\"dcol\" > 3.0 AND \"tableOne\".\"icol\" < 5\n"
        + "GROUP BY \"tableOne\".\"scol\"\n" + "HAVING SUM(\"tableOne\".\"icol\") > 10\n"
        + "ORDER BY \"tableOne\".\"scol\"";
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testMapStructAccess() {
    String sql =
        "SELECT mcol[scol].IFIELD as mapStructAccess, mcol[scol].SFIELD as sField from test.tableFour where icol < 5";

    String expectedSql =
        "SELECT element_at(\"tableFour\".\"mcol\", \"tableFour\".\"scol\").\"IFIELD\" AS \"MAPSTRUCTACCESS\", element_at(\"tableFour\".\"mcol\", \"tableFour\".\"scol\").\"SFIELD\" AS \"SFIELD\"\n"
            + "FROM \"tableFour\" AS \"tableFour\"\n" + "WHERE \"tableFour\".\"icol\" < 5";
    testConversion(sql, expectedSql);
  }

  // different data types
  @Test
  public void testTypes() {
    // Array
    /*
    {
      String sql = "select acol[10] from tableOne";
      String expected = "SELECT element_at(\"tableOne\".\"acol\", 10)\n" + "FROM \"tableOne\" AS \"tableOne\"";
      testConversion(sql, expected);
    }
    {
      String sql = "select ARRAY[1,2,3]";
      String expected = "SELECT ARRAY[1, 2, 3]\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
      testConversion(sql, expected);
    }
    */
    // date and timestamp
    {
      String sql = "SELECT date '2017-10-21'";
      String expected = "SELECT '2017-10-21'\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
      testConversion(sql, expected);
    }
    {
      String sql = "SELECT date_format('13:45:21.011', 'HH:mm:ss.SSS') as formatted_time";
      String expected =
          "SELECT \"date_format\"('13:45:21.011', 'HH:mm:ss.SSS') AS \"formatted_time\"\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
      testConversion(sql, expected);
    }
    // TODO: Test disabled: Calcite parser does not support time with timezone. Check Hive
    /*
    {
      String sql = "SELECT time '13:45:21.011 America/Cupertino'";
      String expected = "SELECT TIME '13:45:21.011 America/Cupertino'\nFROM (VALUES  (0))";
      testConversion(sql, expected);
    }
    */
  }

  // FIXME: This conversion is not correct
  @Test(enabled = false)
  public void testRowSelection() {
    String sql = "SELECT ROW(1, 2.5, 'abc')";
    String expected = "SELECT ROW(1, 2.5, 'abc')\nFROM (VALUES  (0))";
    testConversion(sql, expected);
  }

  @Test(enabled = false)
  public void testMapSelection() {
    // TODO: This statement does not parse in calcite Sql. Fix syntax
    String sql = "SELECT MAP(ARRAY['a', 'b'], ARRAY[1, 2])";
    String expected = "SELECT MAP(ARRAY['a', 'b'], ARRAY[1, 2])\nFROM (VALUES  (0))";
    testConversion(sql, expected);
  }

  @Test
  public void testConstantExpressions() {
    {
      String sql = "SELECT 1";
      String expected = formatSql("SELECT 1 FROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
      testConversion(sql, expected);
    }
    {
      String sql = "SELECT 5 + 2 * 10 / 4";
      String expected = formatSql("SELECT 5 + 2 * 10 / 4 FROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
      testConversion(sql, expected);
    }
  }

  // FIXME: this is disabled because the default tables are created
  // with NOT NULL definition. So the translation is not correct
  @Test(enabled = false)
  public void testIsNull() {
    {
      String sql = "SELECT icol from tableOne where icol is not null";
      String expected = formatSql("select icol from tableOne where icol IS NOT NULL");
      testConversion(sql, expected);
    }
  }

  // window clause tests
  @Test
  public void testWindowClause() {

  }

  @Test
  public void testExists() {
    String sql = "SELECT icol from tableOne where exists (select ifield from tableTwo where dfield > 32.00)";
    String expected = "SELECT \"tableOne\".\"icol\" AS \"ICOL\"\n" + "FROM \"tableOne\" AS \"tableOne\"\n"
        + "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n" + "FROM \"tableTwo\" AS \"tableTwo\"\n"
        + "WHERE \"tableTwo\".\"dfield\" > 32.00) AS \"t1\" ON TRUE\n" + "WHERE \"t1\".\"$f0\" IS NOT NULL";
    testConversion(sql, expected);
  }

  @Test
  public void testNotExists() {
    String sql = "SELECT icol from tableOne where not exists (select ifield from tableTwo where dfield > 32.00)";
    String expected = "SELECT \"tableOne\".\"icol\" AS \"ICOL\"\n" + "FROM \"tableOne\" AS \"tableOne\"\n"
        + "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n" + "FROM \"tableTwo\" AS \"tableTwo\"\n"
        + "WHERE \"tableTwo\".\"dfield\" > 32.00) AS \"t1\" ON TRUE\n" + "WHERE NOT \"t1\".\"$f0\" IS NOT NULL";
    testConversion(sql, expected);
  }

  // Sub query types
  @Test
  public void testInClause() {
    String sql = "SELECT tcol, scol\n" + "FROM test.tableOne" + " WHERE icol IN ( "
        + " SELECT ifield from test.tableTwo" + "   WHERE ifield < 10)";

    String expectedSql = "SELECT \"tableOne\".\"tcol\" AS \"TCOL\", \"tableOne\".\"scol\" AS \"SCOL\"\n"
        + "FROM \"tableOne\" AS \"tableOne\"\n" + "INNER JOIN (SELECT \"tableTwo\".\"ifield\" AS \"IFIELD\"\n"
        + "FROM \"tableTwo\" AS \"tableTwo\"\n" + "WHERE \"tableTwo\".\"ifield\" < 10\n"
        + "GROUP BY \"tableTwo\".\"ifield\") AS \"t1\" ON \"tableOne\".\"icol\" = \"t1\".\"IFIELD\"";
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testNotIn() {
    String sql = "SELECT tcol, scol\n" + "FROM test.tableOne" + " WHERE icol NOT IN ( "
        + " SELECT ifield from test.tableTwo" + "   WHERE ifield < 10)";

    String s = "select tableOne.tcol as tcol, tableOne.scol as scol\n" + "FROM test.tableOne" + "\n"
        + "INNER JOIN (select ifield as ifield\n" + "from test.tableTwo" + "\n" + "where ifield < 10\n"
        + "group by ifield) as \"t1\" on tableOne.icol != \"t1\".\"IFIELD\"";
    String expectedSql = quoteColumns(upcaseKeywords(s));
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testScalarSubquery() {
    String sql = "SELECT icol from test.tableOne where icol > (select sum(ifield) from tableTwo)";
    testConversion(sql, "");
  }

  @Test(enabled = false)
  public void testCorrelatedSubquery() {
    String sql =
        "select dcol from test.tableOne where dcol > (select sum(dfield) from tableTwo where dfield < test.tableOne.icol)";
    testConversion(sql, "");
  }

  @Test
  public void testLateralView() {
    // we need multiple lateral clauses and projection of columns
    // other than those from lateral view for more robust testing
    final String sql = "SELECT t.icol, dt1.i_plusOne, dt2.d_plusTen, t.tcol, t.acol\n" + "FROM test.tableOne t\n"
        + "JOIN (SELECT t.icol + 1 as i_plusOne FROM test.tableOne t) dt1 ON 1=1\n"
        + "JOIN (SELECT t.dcol + 10 as d_plusTen FROM test.tableOne t) dt2 ON 1=1";

    final String expected =
        "SELECT \"tableone\".\"icol\" AS \"icol\", \"t\".\"i_plusOne\" AS \"i_plusOne\", \"t0\".\"d_plusTen\" AS \"d_plusTen\", \"tableone\".\"tcol\" AS \"tcol\", \"tableone\".\"acol\" AS \"acol\"\n"
            + "FROM \"test\".\"tableone\"\n" + "INNER JOIN (SELECT \"icol\" + 1 AS \"i_plusOne\"\n"
            + "FROM \"test\".\"tableone\") AS \"t\" ON 1 = 1\n" + "INNER JOIN (SELECT \"dcol\" + 10 AS \"d_plusTen\"\n"
            + "FROM \"test\".\"tableone\") AS \"t0\" ON 1 = 1";
    testConversion(sql, expected);
  }

  @Test
  public void testUnnestConstant() {
    final String sql = "SELECT c1 + 2\n" + "FROM (SELECT 1 as c1, 1 as c2 UNION ALL"
        + " SELECT 2 as c1, 2 as c2 UNION ALL" + " SELECT 3 as c1, 3 as c2) t";

    final String expected = "SELECT \"c1\" + 2\n" + "FROM (SELECT *\n" + "FROM (SELECT 1 AS \"c1\", 1 AS \"c2\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")\n" + "UNION ALL\n" + "SELECT 2 AS \"c1\", 2 AS \"c2\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\"))\n" + "UNION ALL\n" + "SELECT 3 AS \"c1\", 3 AS \"c2\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"t6\"";
    testConversion(sql, expected);
  }

  @Test
  public void testLateralViewUnnest() {
    String sql = "select icol, acol_elem from test.tableOne LATERAL VIEW explode(acol) t1 AS acol_elem";
    String expectedSql = "SELECT \"$cor0\".\"icol\" AS \"icol\", \"t0\".\"acol_elem\" AS \"acol_elem\"\n"
        + "FROM \"test\".\"tableone\" AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"acol\") AS \"t0\" (\"acol_elem\")";
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testMultipleNestedQueries() {
    String sql = "select icol from tableOne where dcol > (select avg(dfield) from tableTwo where dfield > "
        + "   (select sum(ifield) from tableOne) )";
  }

  // set queries
  @Test
  public void testUnion() {
    testSetQueries("UNION ALL");
  }

  private void testSetQueries(String operator) {
    String sql = "SELECT icol FROM test.tableOne" + " " + operator + "\n" + "SELECT ifield FROM test.tableTwo"
        + " WHERE sfield = 'abc'";
    String expectedSql = "SELECT \"icol\"\n" + "FROM \"test\".\"tableone\"\n" + operator + "\nSELECT \"ifield\"\n"
        + "FROM \"test\".\"tabletwo\"" + "\nWHERE \"sfield\" = 'abc'";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testCast() {
    String sql = "SELECT cast(dcol as int) as d, cast(icol as double) as i FROM test.tableOne";
    String expectedSql =
        "SELECT CAST(\"tableOne\".\"dcol\" AS INTEGER) AS \"D\", CAST(\"tableOne\".\"icol\" AS DOUBLE) AS \"I\"\n"
            + "FROM \"test\".\"tableOne\" AS \"tableOne\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testVarcharCast() {
    final String sql = "SELECT cast(icol as varchar(1000)) FROM test.tableOne";
    testConversion(sql, "SELECT CAST(\"tableOne\".\"icol\" AS VARCHAR(65535))\nFROM \"tableOne\" AS \"tableOne\"");
  }

  @Test
  public void testRand() {
    String sql1 = "SELECT icol, rand() " + "FROM test.tableOne";
    String expectedSql1 = "SELECT \"tableOne\".\"icol\" AS \"ICOL\", \"RANDOM\"()\n" + "FROM \"test\".\"tableOne\" AS \"tableOne\"";
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT icol, rand(1) " + "FROM test.tableOne";
    String expectedSql2 = "SELECT \"icol\", \"RANDOM\"()\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql2, expectedSql2);
  }

  @Test
  public void testRandInteger() {
    String sql1 = "SELECT floor(rand() * (icol - 2 + 1) + 2) FROM test.tableOne";
    String expectedSql1 =
        "SELECT CAST(FLOOR(\"RANDOM\"() * (\"icol\" - 2 + 1) + 2) AS BIGINT)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT floor(rand() * icol) FROM test.tableOne";
    String expectedSql2 = "SELECT CAST(FLOOR(\"RANDOM\"() * \"icol\") AS BIGINT)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql2, expectedSql2);
    {
      final String sql = "SELECT icol FROM test.tableOne" + " WHERE floor(rand() * icol) > 10";
      final String expected = "SELECT \"icol\"\n" + "FROM \"test\".\"tableone\"\n"
          + "WHERE CAST(FLOOR(\"RANDOM\"() * \"icol\") AS BIGINT) > 10";
      testConversion(sql, expected);
    }
  }

  @Test
  public void testTruncate() {
    String sql1 = "SELECT floor(dcol) FROM test.tableOne";
    String expectedSql1 = "SELECT CAST(FLOOR(\"dcol\") AS BIGINT)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT round(dcol, 2 - floor(log10(abs(dcol))) - 1) FROM test.tableOne";
    String expectedSql2 =
        "SELECT ROUND(\"dcol\", 2 - CAST(FLOOR(LOG10(ABS(\"dcol\"))) AS BIGINT) - 1)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql2, expectedSql2);
  }

  @Test
  public void testSubString2() {
    String sql = "SELECT SUBSTR(scol, 1) FROM test.tableOne";
    String expectedSql = "SELECT \"substr\"(\"scol\", 1)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testSubString3() {
    String sql = "SELECT SUBSTR(scol, icol, 3) FROM test.tableOne";
    String expectedSql = "SELECT \"substr\"(\"scol\", \"icol\", 3)\n" + "FROM \"test\".\"tableone\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testLimit() {
    String sql = "SELECT icol " + "FROM test.tableOne" + " LIMIT 100";
    String expectedSql = "SELECT \"icol\"\n" + "FROM \"test\".\"tableone\"" + "\nLIMIT 100";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testDistinct() {
    String sql = "SELECT distinct icol FROM test.tableOne";
    String expectedSql = "SELECT \"icol\"\n" + "FROM \"test\".\"tableone\"\n" + "GROUP BY \"icol\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testGroupDistinct() {
    String sql = "SELECT scol, count(distinct icol) FROM test.tableOne" + " GROUP BY scol";
    String expectedSql =
        "SELECT \"scol\", COUNT(DISTINCT \"icol\")\n" + "FROM \"test\".\"tableone\"\n" + "GROUP BY \"scol\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testJoin() {
    String sql = "SELECT a.icol, b.dfield  FROM test.tableOne" + " a JOIN test.tableTwo" + " b ON a.scol = b.sfield";
    String expectedSql =
        "SELECT \"tableone\".\"icol\" AS \"icol\", \"tabletwo\".\"dfield\" AS \"dfield\"\nFROM \"test\".\"tableone\""
            + "\nINNER JOIN \"test\".\"tabletwo\" ON \"tableone\".\"scol\" = \"tabletwo\".\"sfield\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testLeftJoin() {
    String sql =
        "SELECT a.icol, b.dfield  FROM test.tableOne" + " a LEFT JOIN test.tableTwo" + " b ON a.scol = b.sfield";
    String expectedSql =
        "SELECT \"tableone\".\"icol\" AS \"icol\", \"tabletwo\".\"dfield\" AS \"dfield\"\nFROM \"test\".\"tableone\""
            + "\nLEFT JOIN \"test\".\"tabletwo\"" + " ON \"tableone\".\"scol\" = \"tabletwo\".\"sfield\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testRightJoin() {
    String sql =
        "SELECT a.icol, b.dfield  FROM test.tableOne" + " a RIGHT JOIN test.tableTwo" + " b ON a.scol = b.sfield";
    String expectedSql =
        "SELECT \"tableone\".\"icol\" AS \"icol\", \"tabletwo\".\"dfield\" AS \"dfield\"\nFROM \"test\".\"tableone\""
            + "\nRIGHT JOIN \"test\".\"tabletwo\"" + " ON \"tableone\".\"scol\" = \"tabletwo\".\"sfield\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testOuterJoin() {
    String sql =
        "SELECT a.icol, b.dfield  FROM test.tableOne" + " a FULL OUTER JOIN test.tableTwo" + " b ON a.scol = b.sfield";
    String expectedSql =
        "SELECT \"tableone\".\"icol\" AS \"icol\", \"tabletwo\".\"dfield\" AS \"dfield\"\nFROM \"test\".\"tableone\""
            + "\nFULL JOIN \"test\".\"tabletwo\"" + " ON \"tableone\".\"scol\" = \"tabletwo\".\"sfield\"";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testTryCastIntTrino() {
    String sql = "SELECT CASE WHEN a.scol= 0 THEN TRUE ELSE FALSE END AS testcol FROM test.tableOne a WHERE a.scol = 1";
    String expectedSql = "SELECT CASE WHEN CAST(\"tableone\".\"scol\" AS INTEGER) = 0 THEN TRUE ELSE FALSE END AS \"testcol\"\n"
        + "FROM \"test\".\"tableone\" AS \"tableone\"\n" + "WHERE TRY_CAST(\"tableone\".\"scol\" AS INTEGER) = 1";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testTryCastBooleanTrino() {
    String sql = "SELECT CASE WHEN a.scol= TRUE THEN TRUE ELSE FALSE END AS testcol FROM test.tableOne"
        + " a WHERE a.scol = FALSE";
    String expectedSql =
        "SELECT CASE WHEN CAST(\"tableone\".\"scol\" AS BOOLEAN) = TRUE THEN TRUE ELSE FALSE END AS \"testcol\"\nFROM \"test\".\"tableone\" AS \"tableone\""
            + "\nWHERE " + "CAST(\"tableone\".\"scol\" AS BOOLEAN) = FALSE";
    testConversion(sql, expectedSql);
  }

  @Test
  public void testCase() {
    String sql = "SELECT case when icol = 0 then scol else 'other' end from test.tableOne";
    String expected = formatSql("SELECT CASE WHEN icol = 0 THEN scol ELSE 'other' END FROM \"test\".\"tableone\"");
    testConversion(sql, expected);

    String sqlNull = "SELECT case when icol = 0 then scol end from test.tableOne";
    String expectedNull = formatSql("SELECT CASE WHEN icol = 0 THEN scol ELSE NULL END FROM \"test\".\"tableone\"");
    testConversion(sqlNull, expectedNull);
  }

  @Test
  public void testDataTypeSpecRewrite() {
    String sql1 = "SELECT CAST(icol AS FLOAT) FROM test.tableOne";
    String expectedSql1 = formatSql("SELECT CAST(icol AS REAL) FROM \"test\".\"tableone\"");
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT CAST(binaryfield AS BINARY) FROM test.tableThree";
    String expectedSql2 = formatSql("SELECT CAST(binaryfield AS VARBINARY) FROM \"test\".\"tablethree\"");
    testConversion(sql2, expectedSql2);

    String sql3 = "SELECT CAST(varbinaryfield AS BINARY) FROM test.tableThree";
    String expectedSql3 = formatSql("SELECT CAST(varbinaryfield AS VARBINARY) FROM \"test\".\"tablethree\"");
    testConversion(sql3, expectedSql3);
  }

  @Test
  public void testCurrentUser() {
    String sql = "SELECT current_user";
    String expected = formatSql("SELECT CURRENT_USER AS \"current_user\"\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
    testConversion(sql, expected);
  }

  @Test
  public void testCurrentTimestamp() {
    String sql = "SELECT current_timestamp";
    String expected =
        formatSql("SELECT CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3))\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
    testConversion(sql, expected);
  }

  @Test
  public void testCurrentDate() {
    String sql = "SELECT current_date";
    String expected = formatSql("SELECT CURRENT_DATE\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
    testConversion(sql, expected);
  }
}
