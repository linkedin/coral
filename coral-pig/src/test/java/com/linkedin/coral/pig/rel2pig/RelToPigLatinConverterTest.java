/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.pig.pigunit.PigTest;
import org.apache.pig.tools.parameters.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class RelToPigLatinConverterTest {

  private static final String OUTPUT_RELATION = "view";
  private static HiveConf conf;

  @BeforeTest
  public static void beforeTest() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.turnOffRelSimplification();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_PIG_TEST_DIR)));
  }

  /**
   * Tests a projection of a single column
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testTrivialSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.c FROM pig.tableA AS tableA";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FOREACH view GENERATE c AS c;" };
    String[] expectedOutput = { "(100)", "(200)", "(300)", "(400)", "(500)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a projection over all columns
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testAllSelect() throws IOException, ParseException {
    String sql = "SELECT * FROM pig.tableA AS tableA";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FOREACH view GENERATE a AS a, b AS b, c AS c;" };
    String[] expectedOutput = { "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a projection of multiple columns with aliases that differ from its base table
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testFieldsWithAliasesSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.a as f1, tableA.b as f2, tableA.c as f3 FROM pig.tableA AS tableA";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FOREACH view GENERATE a AS f1, b AS f2, c AS f3;" };
    String[] expectedOutput = { "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests top-level and nested structs
   */
  @Test
  public static void testStructType() throws IOException, ParseException {
    String sql = "SELECT t.a as a, t.b.b0 as b0, t.c.c0.c00 as c00 FROM pig.tablestruct AS t";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablestruct.json' USING JsonLoader('a:int, b:(b0:int), c:(c0:(c00:int))');", "view = FOREACH view GENERATE a AS a, b.b0 AS b0, c.c0.c00 AS c00;" };
    String[] expectedOutput = { "(1,10,100)", "(2,20,200)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests top-level map types
   */
  @Test
  public static void testMapType() throws IOException, ParseException {
    String sql = "SELECT t.m1['a'] AS a, t.m1['b'] AS b FROM pig.tablemap AS t";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablemap.json' USING JsonLoader('m1:map[int]');", "view = FOREACH view GENERATE m1#'a' AS a, m1#'b' AS b;" };
    String[] expectedOutput = { "(10,11)", "(20,21)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests comparison operators
   */
  @Test
  public static void testComparisonOperators() throws IOException, ParseException {
    String sqlTemplate = "SELECT tableA.a FROM pig.tableA AS tableA WHERE tableA.a %s 1";
    String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FILTER view BY (a %s 1);", "view = FOREACH view GENERATE a AS a;");

    String[] sqlOperators = { ">", ">=", "<", "<=", "=", "!=" };
    String[] pigOperators = { ">", ">=", "<", "<=", "==", "!=" };

    String[] expectedOutputs = { "(2),(3),(4)", "(1),(2),(3),(4)", "(0)", "(0),(1)", "(1)", "(0),(2),(3),(4)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      String sql = String.format(sqlTemplate, sqlOperators[i]);
      String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigOperators[i]).split("\n");
      String[] expectedOutput = expectedOutputs[i].split(",");

      String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }

  }

  /**
   * Tests arithmetic operators
   */
  @Test
  public static void testArithmeticOperators() throws IOException, ParseException {
    final String sqlTemplate = "SELECT tableA.a %s 2 AS a FROM pig.tableA AS tableA";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE (a %s 2) AS a;");

    final String[] operators = { "+", "-", "/", "*" };

    final String[] expectedOutputs =
        { "(2),(3),(4),(5),(6)", "(-2),(-1),(0),(1),(2)", "(0),(0),(1),(1),(2)", "(0),(2),(4),(6),(8)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, operators[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, operators[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(",");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }

  }

  /**
   * Tests the Hive IN operator
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testHiveInFilterSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.a FROM pig.tableA AS tableA WHERE tableA.a IN (2,3,4)";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FILTER view BY a IN (2, 3, 4);", "view = FOREACH view GENERATE a AS a;" };
    String[] expectedOutput = { "(2)", "(3)", "(4)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests CASE statements with:
   *   - different comparison operators
   *   - AND and OR predicates in a single condition
   *   - CASE with and without an ELSE(default) clause
   *   - overlapping conditions
   */
  @Test
  public static void testCaseOperator() throws IOException, ParseException {
    final String sqlTemplate = "SELECT (CASE %s END) AS result FROM pig.tableA";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE (CASE %s END) AS result;");

    final String[] sqlCases = {
        // Tests multiple conditions with different comparison operators
        "WHEN a <= 1 THEN 'LTE one' WHEN a = 2 THEN 'two' WHEN a > 3 THEN 'GT three' ELSE 'default'",
        // Tests multiple predicates in a single case condition
        "WHEN (a = 0 AND b = 0) THEN 'zero' WHEN (a = 1 OR a = 2) THEN 'one or two' ELSE 'default'",
        // Tests CASE with and without an ELSE clause
        "WHEN a = 0 THEN 'zero' ELSE 'default'", "WHEN a = 0 THEN 'zero'",
        // Tests overlapping conditions
        "WHEN a < 1 THEN 'LT one' WHEN a < 2 THEN 'LT two' WHEN a < 4 THEN 'LT four' WHEN a < 3 THEN 'LT three' ELSE 'default'" };

    final String[] pigCases = {
        // Tests multiple conditions with different comparison operators
        "WHEN (a <= 1) THEN 'LTE one' WHEN (a == 2) THEN 'two' WHEN (a > 3) THEN 'GT three' ELSE 'default'",
        // Tests multiple predicates in a single case condition
        "WHEN ((a == 0) AND (b == 0)) THEN 'zero' WHEN ((a == 1) OR (a == 2)) THEN 'one or two' ELSE 'default'",
        // Tests CASE with and without an ELSE clause
        "WHEN (a == 0) THEN 'zero' ELSE 'default'", "WHEN (a == 0) THEN 'zero' ELSE null",
        // Tests overlapping conditions
        "WHEN (a < 1) THEN 'LT one' WHEN (a < 2) THEN 'LT two' WHEN (a < 4) THEN 'LT four' WHEN (a < 3) THEN 'LT three' ELSE 'default'" };

    final String[] expectedOutputs =
        { "(LTE one);(LTE one);(two);(default);(GT three)", "(zero);(one or two);(one or two);(default);(default)", "(zero);(default);(default);(default);(default)", "(zero);();();();()", "(LT one);(LT two);(LT four);(LT four);(default)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlCases[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigCases[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests the NOT Hive IN operator
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testNotHiveInFilterSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.a FROM pig.tableA AS tableA WHERE tableA.a NOT IN (2,3,4)";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FILTER view BY NOT a IN (2, 3, 4);", "view = FOREACH view GENERATE a AS a;" };
    String[] expectedOutput = { "(0)", "(1)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests the CAST operator
   */
  @Test
  public static void testCastOperator() throws IOException, ParseException {
    final String sqlTemplate = "SELECT %s FROM pig.tablecast";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablecast.json' USING JsonLoader('i:int, bi:long, fl:float, do:double, str:chararray, boo:boolean');",
        "view = FOREACH view GENERATE %s;");

    final String[] sqlCastFields =
        { "CAST(i AS bigint) as l, CAST(i AS float) as fl, CAST(i AS double) as do, CAST(i AS string) as ca", "CAST(bi AS int) as i, CAST(bi AS float) as fl, CAST(bi AS double) as do, CAST(bi AS string) as ca", "CAST(fl AS int) as i, CAST(fl AS bigint) as l, CAST(fl AS double) as do, CAST(fl AS string) as ca", "CAST(do AS int) as i, CAST(do AS bigint) as l, CAST(do AS float) as fl, CAST(do AS string) as ca", "CAST(str AS int) as i, CAST(str AS bigint) as l, CAST(str AS float) as fl, CAST(str AS double) as do, CAST(str AS boolean) as boo", "CAST(boo AS string) as ca" };

    final String[] pigCastFields =
        { "(long)i AS l, (float)i AS fl, (double)i AS do, (chararray)i AS ca", "(int)bi AS i, (float)bi AS fl, (double)bi AS do, (chararray)bi AS ca", "(int)fl AS i, (long)fl AS l, (double)fl AS do, (chararray)fl AS ca", "(int)do AS i, (long)do AS l, (float)do AS fl, (chararray)do AS ca", "(int)str AS i, (long)str AS l, (float)str AS fl, (double)str AS do, (boolean)str AS boo", "(chararray)boo AS ca", };

    final String[] expectedOutputs =
        { "(1000000000,1.0E9,1.0E9,1000000000);(1,1.0,1.0,1)", "(1410065408,1.0E10,1.0E10,10000000000);(1,1.0,1.0,1)", "(0,0,0.12345679104328156,0.12345679);(1,1,1.0,1.0)", "(0,0,0.12345679,0.12345678901234568);(1,1,1.0,1.0)", "(1,1,1.0,1.0,);(,,,,true)", "(true);(false)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlCastFields[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigCastFields[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests a filter with multiple conditions
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testMultipleFilterSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.b FROM pig.tableA AS tableA WHERE (tableA.a > 2 AND tableA.b > 3) OR tableA.c = 100";
    String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FILTER view BY (((a > 2) AND (b > 3)) OR (c == 100));", "view = FOREACH view GENERATE b AS b;" };
    String[] expectedOutput = { "(0)", "(4)" };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests the following aggregate functions with a single grouping:
   *   COUNT, AVG, SUM, MAX, MIN
   */
  @Test
  public static void testAggregateFunctions() throws IOException, ParseException {
    final String sqlTemplate = "SELECT a AS a, %s(b) AS agg FROM pig.tableB GROUP BY a";
    final String expectedPigLatinTemplate =
        String.join("\n", "view = LOAD 'src/test/resources/data/pig/tableb.json' USING JsonLoader('a:int, b:int');",
            "view = GROUP view BY (a);", "view = FOREACH view GENERATE group AS a, %s(view.b) AS agg;");

    final String[] aggregateFunctions = { "COUNT", "AVG", "SUM", "MAX", "MIN" };

    final String[] expectedOutputs =
        { "(0,2);(1,3)", "(0,5.0);(1,1.6666666666666667)", "(0,10);(1,5)", "(0,10);(1,2)", "(0,0);(1,1)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, aggregateFunctions[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, aggregateFunctions[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests aggregate function without grouping
   */
  @Test
  public static void testNoGroupingAggregate() throws IOException, ParseException {
    final String sql = "SELECT COUNT(*) FROM pig.tableA";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FOREACH view GENERATE 0 AS xf0;", "view = GROUP view ALL;", "view = FOREACH view GENERATE COUNT(view) AS EXPRx0;" };
    final String[] expectedOutput = { "(5)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests aggregate functions multiple field grouping
   */
  @Test
  public static void testMultipleFieldGroupingAggregate() throws IOException, ParseException {
    final String sql = "SELECT COUNT(a) AS count, a, b, AVG(a) FROM pig.tableA GROUP BY b, a";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "view = FOREACH view GENERATE b AS b, a AS a;", "view = GROUP view BY (b, a);", "view = FOREACH view GENERATE group.b AS b, group.a AS a, COUNT(view.a) AS count, AVG(view.a) AS EXPRx3;", "view = FOREACH view GENERATE count AS count, a AS a, b AS b, EXPRx3 AS EXPRx3;" };
    final String[] expectedOutput = { "(1,0,0,0.0)", "(1,1,1,1.0)", "(1,2,2,2.0)", "(1,3,3,3.0)", "(1,4,4,4.0)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a SELF-JOIN on a table with a single condition
   */
  @Test
  public static void testSelfJoinSingleCondition() throws IOException, ParseException {
    final String sql =
        "SELECT tl.a as a, tl.b as bl, tr.b as br FROM pig.tableLeft tl JOIN pig.tableLeft tr ON tl.a = tr.a";
    final String[] expectedPigLatin =
        { "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');", "view = JOIN CORAL_PIG_ALIAS_1 BY (a), CORAL_PIG_ALIAS_2 BY (a);", "view = FOREACH view GENERATE CORAL_PIG_ALIAS_1::a AS a, CORAL_PIG_ALIAS_1::b AS b, CORAL_PIG_ALIAS_1::c AS c, CORAL_PIG_ALIAS_2::a AS a0, CORAL_PIG_ALIAS_2::b AS b0, CORAL_PIG_ALIAS_2::c AS c0;", "view = FOREACH view GENERATE a AS a, b AS bl, b0 AS br;", };
    final String[] expectedOutput =
        { "(0,2,2)", "(0,2,1)", "(0,1,2)", "(0,1,1)", "(1,4,4)", "(1,4,3)", "(1,3,4)", "(1,3,3)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a SELF-JOIN on a table with multiple conditions
   */
  @Test
  public static void testSelfJoinMultipleConditions() throws IOException, ParseException {
    final String sql =
        "SELECT * FROM pig.tableLeft tl JOIN pig.tableLeft tr ON tl.a = tr.a AND tl.b = tr.b AND tl.c = tr.c ";
    final String[] expectedPigLatin =
        { "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');", "view = JOIN CORAL_PIG_ALIAS_1 BY (a, b, c), CORAL_PIG_ALIAS_2 BY (a, b, c);", "view = FOREACH view GENERATE CORAL_PIG_ALIAS_1::a AS a, CORAL_PIG_ALIAS_1::b AS b, CORAL_PIG_ALIAS_1::c AS c, CORAL_PIG_ALIAS_2::a AS a0, CORAL_PIG_ALIAS_2::b AS b0, CORAL_PIG_ALIAS_2::c AS c0;", "view = FOREACH view GENERATE a AS a, b AS b, c AS c, a0 AS a0, b0 AS b0, c0 AS c0;" };
    final String[] expectedOutput = { "(0,1,10,0,1,10)", "(0,2,10,0,2,10)", "(1,3,10,1,3,10)", "(1,4,10,1,4,10)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests the following join types over two different tables:
   *   INNER, FULL OUTER, LEFT OUTER, RIGHT OUTER
   */
  @Test
  public static void testJoinTypes() throws IOException, ParseException {
    final String sqlTemplate = "SELECT * FROM pig.tableLeft tl %s JOIN pig.tableRight tr ON tl.a = tr.d";
    final String expectedPigLatinTemplate = String.join("\n",
        "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');",
        "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableright.json' USING JsonLoader('d:int, e:int');",
        "view = JOIN CORAL_PIG_ALIAS_1 BY (a)%s, CORAL_PIG_ALIAS_2 BY (d);",
        "view = FOREACH view GENERATE CORAL_PIG_ALIAS_1::a AS a, CORAL_PIG_ALIAS_1::b AS b, CORAL_PIG_ALIAS_1::c AS c, CORAL_PIG_ALIAS_2::d AS d, CORAL_PIG_ALIAS_2::e AS e;",
        "view = FOREACH view GENERATE a AS a, b AS b, c AS c, d AS d, e AS e;");

    final String[] sqlJoinTypes = { "", "FULL", "LEFT", "RIGHT" };

    final String[] pigJoinTypes = { "", " FULL OUTER", " LEFT OUTER", " RIGHT OUTER" };

    final String[] expectedOutputs =
        { "(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10)", "(0,2,10,,);(0,1,10,,);(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10);(,,,2,40);(,,,2,30)", "(0,2,10,,);(0,1,10,,);(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10)", "(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10);(,,,2,40);(,,,2,30)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlJoinTypes[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigJoinTypes[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests that IS NOT NULL and IS NULL works
   */
  @Test
  public static void testNullablilityOperators() throws IOException, ParseException {
    final String sqlTemplate = "SELECT * FROM pig.tablenull WHERE nullableField %s";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablenull.json' USING JsonLoader('nullablefield:chararray, field:chararray');",
        "view = FILTER view BY nullablefield %s;",
        "view = FOREACH view GENERATE nullablefield AS nullablefield, field AS field;");

    final String[] nullabilityTypes = { "IS NULL", "IS NOT NULL" };

    final String[] expectedOutputs = { "(,nullField)", "(nonNullField,nonNullField)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, nullabilityTypes[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, nullabilityTypes[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests UNION functionality on the same table a single time
   */
  @Test
  public static void testUnionSameTableSingle() throws IOException, ParseException {
    final String sql = "SELECT * FROM pig.tableA UNION ALL SELECT * FROM pig.tableA";
    final String[] expectedPigLatin =
        { "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_1 = FOREACH CORAL_PIG_ALIAS_1 GENERATE a AS a, b AS b, c AS c;", "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_2 = FOREACH CORAL_PIG_ALIAS_2 GENERATE a AS a, b AS b, c AS c;", "view = UNION CORAL_PIG_ALIAS_1, CORAL_PIG_ALIAS_2;", "view = FOREACH view GENERATE a AS a, b AS b, c AS c;" };
    final String[] expectedOutput =
        { "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)", "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests UNION functionality on different tables with explicit aliasing
   */
  @Test
  public static void testUnionDifferentTable() throws IOException, ParseException {
    final String sql = "SELECT a, c FROM pig.tableA UNION ALL SELECT a, b AS c FROM pig.tableB";
    final String[] expectedPigLatin =
        { "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_1 = FOREACH CORAL_PIG_ALIAS_1 GENERATE a AS a, c AS c;", "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableb.json' USING JsonLoader('a:int, b:int');", "CORAL_PIG_ALIAS_2 = FOREACH CORAL_PIG_ALIAS_2 GENERATE a AS a, b AS c;", "view = UNION CORAL_PIG_ALIAS_1, CORAL_PIG_ALIAS_2;", "view = FOREACH view GENERATE a AS a, c AS c;" };
    final String[] expectedOutput =
        { "(0,0)", "(0,10)", "(1,1)", "(1,2)", "(1,2)", "(0,100)", "(1,200)", "(2,300)", "(3,400)", "(4,500)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests UNION functionality on the same table multiple times
   */
  @Test
  public static void testUnionSameTableMultiple() throws IOException, ParseException {
    final String sql = "SELECT * FROM pig.tableA UNION ALL SELECT * FROM pig.tableA UNION ALL SELECT * FROM pig.tableA";
    final String[] expectedPigLatin =
        { "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_2 = FOREACH CORAL_PIG_ALIAS_2 GENERATE a AS a, b AS b, c AS c;", "CORAL_PIG_ALIAS_3 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_3 = FOREACH CORAL_PIG_ALIAS_3 GENERATE a AS a, b AS b, c AS c;", "CORAL_PIG_ALIAS_1 = UNION CORAL_PIG_ALIAS_2, CORAL_PIG_ALIAS_3;", "CORAL_PIG_ALIAS_4 = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');", "CORAL_PIG_ALIAS_4 = FOREACH CORAL_PIG_ALIAS_4 GENERATE a AS a, b AS b, c AS c;", "view = UNION CORAL_PIG_ALIAS_1, CORAL_PIG_ALIAS_4;", "view = FOREACH view GENERATE a AS a, b AS b, c AS c;" };
    final String[] expectedOutput =
        { "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)", "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)", "(0,0,100)", "(1,1,200)", "(2,2,300)", "(3,3,400)", "(4,4,500)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

}
