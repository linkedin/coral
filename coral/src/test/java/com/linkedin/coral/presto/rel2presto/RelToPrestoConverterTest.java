package com.linkedin.coral.presto.rel2presto;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.tools.FrameworkConfig;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.linkedin.coral.presto.rel2presto.TestTable.*;
import static com.linkedin.coral.presto.rel2presto.TestUtils.*;
import static org.testng.Assert.*;


/**
 * Tests conversion from Calcite RelNode to Presto Sql
 */
// All tests use a starting sql and use calcite parser to generate parse tree.
// This makes it easier to generate RelNodes for testing. The input sql is
// in Calcite sql syntax (not Hive)
// Disabled tests are failing tests
public class RelToPrestoConverterTest {

  static FrameworkConfig config;
  static SqlParser prestoParser = new SqlParser();
  static final String tableOne = TABLE_ONE.getTableName();
  static final String tableTwo = TABLE_TWO.getTableName();

  @BeforeTest
  public static void beforeTest() {
    config = TestUtils.createFrameworkConfig(TABLE_ONE, TABLE_TWO);
  }

  @Test
  public void testSimpleSelect() {
    String sql = String.format("SELECT scol, sum(icol) as s from %s where dcol > 3.0 AND icol < 5 group by scol having sum(icol) > 10" +
            " order by scol ASC",
        tableOne);

    String expectedSql = formatSql("SELECT scol as SCOL, SUM(icol) AS s FROM " + tableOne +
        " where dcol > 3.0 and icol < 5\n"
        + "group by scol\n"
        + "having sum(icol) > 10\n"
        + "order by scol");
    testConversion(sql, expectedSql);
  }

  // different data types
  @Test
  public void testTypes() {
    // Array
    {
      String sql = "select acol[10] from tableOne";
      String expected = "SELECT \"acol\"[10]\nFROM \"tableOne\"";
      testConversion(sql, expected);
    }
    {
      String sql = "select ARRAY[1,2,3]";
      String expected = "SELECT ARRAY[1, 2, 3]\nFROM (VALUES  (0))";
      testConversion(sql, expected);
    }
    // date and timestamp
    {
      String sql = "SELECT date '2017-10-21'";
      String expected = "SELECT DATE '2017-10-21'\nFROM (VALUES  (0))";
      testConversion(sql, expected);
    }
    {
      String sql = "SELECT time '13:45:21.011'";
      String expected = "SELECT TIME '13:45:21.011'\nFROM (VALUES  (0))";
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
  @Test (enabled = false)
  public void testRowSelection() {
    String sql = "SELECT ROW(1, 2.5, 'abc')";
    String expected = "SELECT ROW(1, 2.5, 'abc')\nFROM (VALUES  (0))";
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
    testConversion(sql, expected);
  }

  @Test (enabled = false)
  public void testMapSelection() {
    // TODO: This statement does not parse in calcite Sql. Fix syntax
    String sql = "SELECT MAP(ARRAY['a', 'b'], ARRAY[1, 2])";
    String expected = "SELECT MAP(ARRAY['a', 'b'], ARRAY[1, 2])\nFROM (VALUES  (0))";
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
    testConversion(sql, expected);
  }

  @Test
  public void testConstantExpressions() {
    {
      String sql = "SELECT 1";
      String expected = formatSql("SELECT 1 FROM (VALUES  (0))");
      testConversion(sql, expected);
    }
    {
      String sql = "SELECT 5 + 2 * 10 / 4";
      String expected = formatSql("SELECT 5 + 2 * 10 / 4 FROM (VALUES  (0))");
      testConversion(sql, expected);
    }
  }

  // FIXME: this is disabled because the default tables are created
  // with NOT NULL definition. So the translation is not correct
  @Test (enabled = false)
  public void testIsNull() {
    {
      String sql = "SELECT icol from tableOne where icol is not null";
      String expected = formatSql("select icol from tableOne where icol IS NOT NULL");
      System.out.println(RelOptUtil.toString(toRel(sql, config)));
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
    String expected = quoteColumns("SELECT tableOne.icol AS ICOL\n" +
        "FROM tableOne\n" +
        "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n"+
        "FROM tableTwo\n" +
        "WHERE dfield > 32.00) AS \"t2\" ON TRUE\n" +
        "WHERE \"t2\".\"$f0\" IS NOT NULL");
    testConversion(sql, expected);
  }

  @Test
  public void testNotExists() {
    String sql = "SELECT icol from tableOne where not exists (select ifield from tableTwo where dfield > 32.00)";
    String expected = quoteColumns("SELECT tableOne.icol AS ICOL\n" +
    "FROM tableOne\n" +
    "LEFT JOIN (SELECT MIN(TRUE) AS \"$f0\"\n" +
    "FROM tableTwo\n" +
    "WHERE dfield > 32.00) AS \"t2\" ON TRUE\n" +
    "WHERE NOT \"t2\".\"$f0\" IS NOT NULL");
    testConversion(sql, expected);
  }

  // Sub query types
  @Test
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
    String sql = "SELECT tcol, scol\n" + "FROM " + tableOne + " WHERE icol NOT IN ( " + " SELECT ifield from " + tableTwo
        + "   WHERE ifield < 10)";

    String s = "select tableOne.tcol as tcol, tableOne.scol as scol\n" + "FROM " + tableOne + "\n"
        + "INNER JOIN (select ifield as ifield\n" + "from " + tableTwo + "\n" + "where ifield < 10\n"
        + "group by ifield) as \"t1\" on tableOne.icol != \"t1\".\"IFIELD\"";
    String expectedSql = quoteColumns(upcaseKeywords(s));
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
    testConversion(sql, expectedSql);
  }

  @Test
  public void testExceptClause() {
    String sql = "SELECT icol from " + tableOne + " EXCEPT (select ifield from " + tableTwo + ")";
    String expected = formatSql("select icol as icol from tableOne except select ifield as ifield from tableTwo");
    testConversion(sql, expected);
  }

  @Test (enabled = false)
  public void testScalarSubquery() {
    String sql = "SELECT icol from tableOne where icol > (select sum(ifield) from tableTwo)";
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
    testConversion(sql, "");
  }

  @Test (enabled = false)
  public void testCorrelatedSubquery() {
    String sql = "select dcol from tableOne where dcol > (select sum(dfield) from tableTwo where dfield < tableOne.icol)";
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
    testConversion(sql, "");
  }

  @Test
  public void testMultipleNestedQueries() {
    String sql = "select icol from tableOne where dcol > (select avg(dfield) from tableTwo where dfield > " +
        "   (select sum(ifield) from tableOne) )";
    System.out.println(RelOptUtil.toString(toRel(sql, config)));
  }

  // set queries
  @Test
  public void testUnion() throws Exception {
    testSetQueries("UNION");
  }

  @Test
  public void testIntersect() throws Exception {
    testSetQueries("INTERSECT");
  }

  @Test
  public void testExcept() throws Exception {
    testSetQueries("EXCEPT");
  }

  private void testSetQueries(String operator) throws Exception {
    String sql = "SELECT icol FROM " + tableOne + " " +  operator + "\n" +
        "SELECT ifield FROM " + TABLE_TWO.getTableName() + " WHERE sfield = 'abc'";
    String expectedSql = formatSql("SELECT icol as icol FROM " + tableOne + " " +
        operator +
        " SELECT ifield as ifield from " + tableTwo + " " +
        "where sfield = 'abc'");
    testConversion(sql, expectedSql);
  }

  @Test
  public void testCast() throws Exception {
    String sql = "SELECT cast(dcol as integer) as d, cast(icol as double) as i "
        + "FROM " + TABLE_ONE.getTableName();
    String expectedSql = formatSql("SELECT CAST(dcol as integer) as d, cast(icol as double) as i" +
    " from " + tableOne);
    testConversion(sql, expectedSql);
  }

  private void testConversion(String inputSql, String expectedSql) {
    String prestoSql = toPrestoSql(inputSql);
    validate(prestoSql, expectedSql);
  }

  private void validate(String prestoSql, String expected) {
    try{
      Statement statement = prestoParser.createStatement(prestoSql, new ParsingOptions());
      assertNotNull(statement);
    } catch (Exception e) {
      assertTrue(false, "Failed to parse sql: " + prestoSql);
    }
    assertEquals(prestoSql, expected);
  }

  private String toPrestoSql(String sql) {
    RelToPrestoConverter converter = new RelToPrestoConverter();
    return converter.convert(TestUtils.toRel(sql, config));
  }
}
