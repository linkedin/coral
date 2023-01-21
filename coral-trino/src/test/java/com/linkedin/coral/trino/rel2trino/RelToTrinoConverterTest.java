/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
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
@Test(
    description = "pending migration to hive tables and corresponding queries to use standardised CoralSqlNode and CoralRelNode representations in the translation path")
public class RelToTrinoConverterTest {

  static FrameworkConfig config;
  private HiveConf conf;
  static final SqlParser trinoParser = new SqlParser();
  static final String tableOne = TABLE_ONE.getTableName();
  static final String tableTwo = TABLE_TWO.getTableName();
  static final String tableThree = TABLE_THREE.getTableName();
  static final String tableFour = TABLE_FOUR.getTableName();

  @BeforeTest
  public void beforeClass() throws IOException, HiveException, MetaException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);
  }

  @BeforeTest
  public static void beforeTest() {
    TestUtils.turnOffRelSimplification();
    config = TestUtils.createFrameworkConfig(TABLE_ONE, TABLE_TWO, TABLE_THREE, TABLE_FOUR);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_TRINO_TEST_DIR)));
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
    RelToTrinoConverter converter = new RelToTrinoConverter();
    return converter.convert(TestUtils.toRel(sql, config));
  }

  @Test(enabled = false)
  public void testSimpleSelect() {
    String sql = String
        .format("SELECT scol, sum(icol) as s from %s where dcol > 3.0 AND icol < 5 group by scol having sum(icol) > 10"
            + " order by scol ASC", tableOne);

    String expectedSql = formatSql("SELECT scol as SCOL, SUM(icol) AS s FROM " + tableOne
        + " where dcol > 3.0 and icol < 5\n" + "group by scol\n" + "having sum(icol) > 10\n" + "order by scol");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  // TODO: investigate the quote issue. Expected: element_at("mcol", "scol"). Actual: "element_at(mcol, scol)"
  public void testMapStructAccess() {
    String sql = String.format(
        "SELECT mcol[scol].IFIELD as mapStructAccess, mcol[scol].SFIELD as sField from %s where icol < 5", tableFour);

    String expectedSql =
        "SELECT element_at(\"mcol\", \"scol\").\"IFIELD\" AS \"MAPSTRUCTACCESS\", element_at(\"mcol\", \"scol\").\"SFIELD\" AS \"SFIELD\"\n"
            + "FROM \"tableFour\"\n" + "WHERE \"icol\" < 5";
    testConversion(sql, expectedSql);
  }

  // different data types
  @Test(enabled = false)
  public void testTypes() {
    // Array
    {
      String sql = "select acol[10] from tableOne";
      String expected = "SELECT element_at(\"acol\", 10)\nFROM \"tableOne\"";
      testConversion(sql, expected);
    }
    {
      String sql = "select ARRAY[1,2,3]";
      String expected = "SELECT ARRAY[1, 2, 3]\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
      testConversion(sql, expected);
    }
    // date and timestamp
    {
      String sql = "SELECT date '2017-10-21'";
      String expected = "SELECT DATE '2017-10-21'\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
      testConversion(sql, expected);
    }
    {
      String sql = "SELECT time '13:45:21.011'";
      String expected = "SELECT TIME '13:45:21.011'\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")";
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
      testHiveToTrinoConversion(sql, expected);
    }
    {
      String sql = "SELECT 5 + 2 * 10 / 4";
      String expected = formatSql("SELECT 5 + 2 * 10 / 4 FROM (VALUES  (0)) AS \"t\" (\"ZERO\")");
      testHiveToTrinoConversion(sql, expected);
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
  @Test(enabled = false)
  public void testWindowClause() {

  }

  @Test(enabled = false)
  public void testExists() {
    String sql = "SELECT icol from tableOne where exists (select ifield from tableTwo where dfield > 32.00)";
    String expected =
        quoteColumns("SELECT tableOne.icol AS ICOL\n" + "FROM tableOne\n" + "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n"
            + "FROM tableTwo\n" + "WHERE dfield > 32.00) AS \"t1\" ON TRUE\n" + "WHERE \"t1\".\"$f0\" IS NOT NULL");
    testConversion(sql, expected);
  }

  @Test(enabled = false)
  public void testNotExists() {
    String sql = "SELECT icol from tableOne where not exists (select ifield from tableTwo where dfield > 32.00)";
    String expected =
        quoteColumns("SELECT tableOne.icol AS ICOL\n" + "FROM tableOne\n" + "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n"
            + "FROM tableTwo\n" + "WHERE dfield > 32.00) AS \"t1\" ON TRUE\n" + "WHERE NOT \"t1\".\"$f0\" IS NOT NULL");
    testConversion(sql, expected);
  }

  // Sub query types
  @Test(enabled = false)
  public void testInClause() {
    String sql = "SELECT tcol, scol\n" + "FROM " + tableOne + " WHERE icol IN ( " + " SELECT ifield from " + tableTwo
        + "   WHERE ifield < 10)";

    String s = "select tableOne.tcol as tcol, tableOne.scol as scol\n" + "FROM " + tableOne + "\n"
        + "INNER JOIN (select ifield as ifield\n" + "from " + tableTwo + "\n" + "where ifield < 10\n"
        + "group by ifield) as \"t1\" on tableOne.icol = \"t1\".\"IFIELD\"";
    String expectedSql = quoteColumns(upcaseKeywords(s));
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testNotIn() {
    String sql = "SELECT tcol, scol\n" + "FROM " + tableOne + " WHERE icol NOT IN ( " + " SELECT ifield from "
        + tableTwo + "   WHERE ifield < 10)";

    String s = "select tableOne.tcol as tcol, tableOne.scol as scol\n" + "FROM " + tableOne + "\n"
        + "INNER JOIN (select ifield as ifield\n" + "from " + tableTwo + "\n" + "where ifield < 10\n"
        + "group by ifield) as \"t1\" on tableOne.icol != \"t1\".\"IFIELD\"";
    String expectedSql = quoteColumns(upcaseKeywords(s));
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testExceptClause() {
    String sql = "SELECT icol from " + tableOne + " EXCEPT (select ifield from " + tableTwo + ")";
    String expected = formatSql("select icol as icol from tableOne except select ifield as ifield from tableTwo");
    testConversion(sql, expected);
  }

  @Test(enabled = false)
  public void testScalarSubquery() {
    String sql = "SELECT icol from tableOne where icol > (select sum(ifield) from tableTwo)";
    testConversion(sql, "");
  }

  @Test(enabled = false)
  public void testCorrelatedSubquery() {
    String sql =
        "select dcol from tableOne where dcol > (select sum(dfield) from tableTwo where dfield < tableOne.icol)";
    testConversion(sql, "");
  }

  @Test(enabled = false)
  public void testLateralView() {
    // we need multiple lateral clauses and projection of columns
    // other than those from lateral view for more robust testing
    final String sql = "" + "select icol, i_plusOne, d_plusTen, tcol, acol " + "from tableOne as t, "
        + "     lateral (select t.icol + 1 as i_plusOne" + "              from (values(true))), "
        + "     lateral (select t.dcol + 10 as d_plusTen" + "               from (values(true)))";

    final String expected = "" + "SELECT \"tableOne\".\"icol\" AS \"ICOL\", \"t0\".\"I_PLUSONE\" AS \"I_PLUSONE\", "
        + "\"t2\".\"D_PLUSTEN\" AS \"D_PLUSTEN\", \"tableOne\".\"tcol\" AS \"TCOL\", \"tableOne\".\"acol\" AS \"ACOL\"\n"
        + "FROM \"tableOne\"\n" + "CROSS JOIN LATERAL (SELECT \"tableOne\".\"icol\" + 1 AS \"I_PLUSONE\"\n"
        + "FROM (VALUES  (TRUE)) AS \"t\" (\"EXPR$0\")) AS \"t0\" (\"I_PLUSONE\")\n"
        + "CROSS JOIN LATERAL (SELECT \"tableOne\".\"dcol\" + 10 AS \"D_PLUSTEN\"\n"
        + "FROM (VALUES  (TRUE)) AS \"t\" (\"EXPR$0\")) AS \"t2\" (\"D_PLUSTEN\")";
    testConversion(sql, expected);
  }

  @Test(enabled = false, description = "legacy unnest behavior no longer supported")
  public void testUnnestConstant() {
    final String sql = "" + "SELECT c1 + 2\n" + "FROM UNNEST(ARRAY[(1, 1),(2, 2), (3, 3)]) as t(c1, c2)";

    final String expected = "" + "SELECT \"t0\".\"col_0\" + 2\n"
        + "FROM UNNEST(ARRAY[ROW(1, 1), ROW(2, 2), ROW(3, 3)]) AS \"t0\" (\"col_0\", \"col_1\")";
    testConversion(sql, expected);
  }

  @Test(enabled = false)
  public void testLateralViewUnnest() {
    /**
     * LogicalProject(ICOL=[$0], ACOL_ELEM=[$5])
     *   LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{4}])
     *     EnumerableTableScan(table=[[tableOne]])
     *     LogicalProject(ACOL_ELEM=[$0])
     *       Uncollect
     *         LogicalProject(acol=[$cor0.acol_4])
     *           LogicalValues(tuples=[[{ 0 }]])
     *
     *  Inner project's right child is uncollect. Needs to be as operator: Uncollect(..) AS t(ccol)
     *  in coralSqlNode1 it shouldnt introduce a sqlSelect -> Project
     *
     *  converted to hive SQL:
     *  "select acol_elem from test.tableOne as t LATERAL VIEW explode(t.acol) t1 AS acol_elem"
     *
     *  and translated using hive->coralIR->trino :
     *
     *  Rel:
     *  LogicalProject(acol_elem=[$1])
     *   LogicalCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{0}])
     *     LogicalTableScan(table=[[hive, test, tableone]])
     *     HiveUncollect
     *       LogicalProject(col=[$cor0.acol])
     *         LogicalValues(tuples=[[{ 0 }]])
     *
     * SELECT "t0"."acol_elem"
     * FROM "test"."tableone"
     * CROSS JOIN UNNEST("tableone"."acol") AS "t0" ("acol_elem")
     *
     */
    String sql = "select icol, acol_elem from tableOne as t cross join unnest(t.acol) as t1(acol_elem)";
    String expectedSql = "" + "SELECT \"$cor0\".\"icol\" AS \"ICOL\", \"t1\".\"ACOL_ELEM\" AS \"ACOL_ELEM\"\n"
        + "FROM \"tableOne\" AS \"$cor0\"\nCROSS JOIN LATERAL (SELECT \"acol\" AS \"ACOL_ELEM\"\n"
        + "FROM UNNEST(\"$cor0\".\"acol\") AS \"t0\" (\"acol\")) AS \"t1\"";
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testMultipleNestedQueries() {
    String sql = "select icol from tableOne where dcol > (select avg(dfield) from tableTwo where dfield > "
        + "   (select sum(ifield) from tableOne) )";
  }

  // set queries
  @Test(enabled = false)
  public void testUnion() {
    testSetQueries("UNION");
  }

  @Test(enabled = false)
  public void testIntersect() {
    testSetQueries("INTERSECT");
  }

  @Test(enabled = false)
  public void testExcept() {
    testSetQueries("EXCEPT");
  }

  private void testSetQueries(String operator) {
    String sql = "SELECT icol FROM " + tableOne + " " + operator + "\n" + "SELECT ifield FROM "
        + TABLE_TWO.getTableName() + " WHERE sfield = 'abc'";
    String expectedSql = formatSql("SELECT icol as icol FROM " + tableOne + " " + operator
        + " SELECT ifield as ifield from " + tableTwo + " " + "where sfield = 'abc'");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testCast() {
    String sql = "SELECT cast(dcol as integer) as d, cast(icol as double) as i " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql =
        formatSql("SELECT CAST(dcol as integer) as d, cast(icol as double) as i" + " from " + tableOne);
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testVarcharCast() {
    final String sql = "SELECT cast(icol as varchar(1000)) FROM " + tableOne;
    testConversion(sql, "SELECT CAST(\"icol\" AS VARCHAR(1000))\nFROM \"" + tableOne + "\"");
  }

  @Test(enabled = false)
  public void testRand() {
    String sql1 = "SELECT icol, rand() " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql1 = formatSql("SELECT icol AS \"ICOL\", \"RANDOM\"()" + " from " + tableOne);
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT icol, rand(1) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql2 = formatSql("SELECT icol AS \"ICOL\", \"RANDOM\"()" + " from " + tableOne);
    testConversion(sql2, expectedSql2);
  }

  @Test(enabled = false)
  public void testRandInteger() {
    String sql1 = "SELECT rand_integer(2, icol) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql1 = formatSql("SELECT \"RANDOM\"(icol)" + " from " + tableOne);
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT rand_integer(icol) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql2 = formatSql("SELECT \"RANDOM\"(icol)" + " from " + tableOne);
    testConversion(sql2, expectedSql2);
    {
      final String sql = "SELECT icol FROM " + TABLE_ONE.getTableName() + " WHERE rand_integer(icol) > 10";
      final String expected = "SELECT \"icol\" AS \"ICOL\"\nFROM \"" + TABLE_ONE.getTableName() + "\""
          + "\nWHERE \"RANDOM\"(\"icol\") > 10";
      testConversion(sql, expected);
    }
  }

  @Test(enabled = false)
  public void testTruncate() {
    String sql1 = "SELECT truncate(dcol) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql1 = formatSql("SELECT TRUNCATE(dcol)" + " from " + tableOne);
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT truncate(dcol, 2) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql2 = formatSql("SELECT TRUNCATE(dcol * POWER(10, 2)) / POWER(10, 2)" + " from " + tableOne);
    testConversion(sql2, expectedSql2);
  }

  @Test(enabled = false)
  public void testSubString2() {
    String sql = "SELECT SUBSTRING(scol FROM 1) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql = formatSql("SELECT \"SUBSTR\"(scol, 1)" + " from " + tableOne);
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testSubString3() {
    String sql = "SELECT SUBSTRING(scol FROM icol FOR 3) " + "FROM " + TABLE_ONE.getTableName();
    String expectedSql = formatSql("SELECT \"SUBSTR\"(scol, icol, 3)" + " from " + tableOne);
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testLimit() {
    String sql = "SELECT icol " + "FROM " + TABLE_ONE.getTableName() + " LIMIT 100";
    String expectedSql = formatSql("SELECT icol AS ICOL" + " from " + tableOne + "\nLIMIT 100");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testDistinct() {
    String sql = "SELECT distinct icol FROM " + TABLE_ONE.getTableName();
    String expectedSql = formatSql("SELECT icol AS ICOL" + " from " + tableOne + " GROUP BY icol");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testGroupDistinct() {
    String sql = "SELECT scol, count(distinct icol) FROM " + TABLE_ONE.getTableName() + " GROUP BY scol";
    String expectedSql = formatSql("SELECT scol AS SCOL, COUNT(DISTINCT icol) FROM " + tableOne + " GROUP BY scol");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testJoin() {
    String sql = "SELECT a.icol, b.dfield  FROM " + tableOne + " a JOIN " + tableTwo + " b ON a.scol = b.sfield";
    String expectedSql = formatSql("SELECT tableOne.icol AS ICOL, tableTwo.dfield as DFIELD\nFROM " + tableOne
        + "\nINNER JOIN " + tableTwo + " ON tableOne.scol = tableTwo.sfield");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testLeftJoin() {
    String sql = "SELECT a.icol, b.dfield  FROM " + tableOne + " a LEFT JOIN " + tableTwo + " b ON a.scol = b.sfield";
    String expectedSql = formatSql("SELECT tableOne.icol AS ICOL, tableTwo.dfield as DFIELD\nFROM " + tableOne
        + "\nLEFT JOIN " + tableTwo + " ON tableOne.scol = tableTwo.sfield");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testRightJoin() {
    String sql = "SELECT a.icol, b.dfield  FROM " + tableOne + " a RIGHT JOIN " + tableTwo + " b ON a.scol = b.sfield";
    String expectedSql = formatSql("SELECT tableOne.icol AS ICOL, tableTwo.dfield as DFIELD\nFROM " + tableOne
        + "\nRIGHT JOIN " + tableTwo + " ON tableOne.scol = tableTwo.sfield");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testOuterJoin() {
    String sql =
        "SELECT a.icol, b.dfield  FROM " + tableOne + " a FULL OUTER JOIN " + tableTwo + " b ON a.scol = b.sfield";
    String expectedSql = formatSql("SELECT tableOne.icol AS ICOL, tableTwo.dfield as DFIELD\nFROM " + tableOne
        + "\nFULL JOIN " + tableTwo + " ON tableOne.scol = tableTwo.sfield");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testTryCastIntTrino() {
    String sql =
        "SELECT CASE WHEN a.scol= 0 THEN TRUE ELSE FALSE END AS testcol FROM " + tableOne + " a WHERE a.scol = 1";
    String expectedSql =
        formatSql("SELECT CASE WHEN TRY_CAST(scol AS INTEGER) = 0 THEN TRUE ELSE FALSE END AS TESTCOL\nFROM " + tableOne
            + "\nWHERE " + "TRY_CAST(scol AS INTEGER) = 1");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testTryCastBooleanTrino() {
    String sql = "SELECT CASE WHEN a.scol= TRUE THEN TRUE ELSE FALSE END AS testcol FROM " + tableOne
        + " a WHERE a.scol = FALSE";
    String expectedSql =
        formatSql("SELECT CASE WHEN TRY_CAST(scol AS BOOLEAN) = TRUE THEN TRUE ELSE FALSE END AS TESTCOL\nFROM "
            + tableOne + "\nWHERE " + "TRY_CAST(scol AS BOOLEAN) = FALSE");
    testConversion(sql, expectedSql);
  }

  @Test(enabled = false)
  public void testCase() {
    String sql = "SELECT case when icol = 0 then scol else 'other' end from " + tableOne;
    String expected = formatSql("SELECT CASE WHEN icol = 0 THEN scol ELSE 'other' END FROM " + tableOne);
    testConversion(sql, expected);

    String sqlNull = "SELECT case when icol = 0 then scol end from " + tableOne;
    String expectedNull =
        formatSql("SELECT CASE WHEN icol = 0 THEN CAST(scol AS VARCHAR) ELSE NULL END FROM " + tableOne);
    testConversion(sqlNull, expectedNull);
  }

  @Test(enabled = false)
  public void testDataTypeSpecRewrite() {
    String sql1 = "SELECT CAST(icol AS FLOAT) FROM " + tableOne;
    String expectedSql1 = formatSql("SELECT CAST(icol AS REAL) FROM " + tableOne);
    testConversion(sql1, expectedSql1);

    String sql2 = "SELECT CAST(binaryfield AS BINARY(123)) FROM " + tableThree;
    String expectedSql2 = formatSql("SELECT CAST(binaryfield AS VARBINARY) FROM " + tableThree);
    testConversion(sql2, expectedSql2);

    String sql3 = "SELECT CAST(varbinaryfield AS VARBINARY(123)) FROM " + tableThree;
    String expectedSql3 = formatSql("SELECT CAST(varbinaryfield AS VARBINARY) FROM " + tableThree);
    testConversion(sql3, expectedSql3);
  }

  @Test
  public void testCurrentUser() {
    String sql = "SELECT current_user";
    String expected = formatSql("SELECT CURRENT_USER AS \"current_user\"\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");

    testHiveToTrinoConversion(sql, expected);
  }

  @Test
  public void testCurrentTimestamp() {
    String sql = "SELECT current_timestamp";
    String expected =
        formatSql("SELECT CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3))\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");

    testHiveToTrinoConversion(sql, expected);
  }

  @Test
  public void testCurrentDate() {
    String sql = "SELECT current_date";
    String expected = formatSql("SELECT CURRENT_DATE\nFROM (VALUES  (0)) AS \"t\" (\"ZERO\")");

    testHiveToTrinoConversion(sql, expected);
  }

  private void testHiveToTrinoConversion(String inputSql, String expectedSql) {
    RelNode relNode = hiveToRelConverter.convertSql(inputSql);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, expectedSql);
  }
}
