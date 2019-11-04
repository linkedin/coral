package com.linkedin.coral.pig.rel2pig;

import java.io.IOException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.pig.pigunit.PigTest;
import org.apache.pig.tools.parameters.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class RelToPigLatinConverterTest {

  static final String OUTPUT_RELATION = "view";

  @BeforeTest
  public static void beforeTest() throws HiveException, MetaException {
    TestUtils.turnOffRelSimplification();
    TestUtils.initializeViews();
  }

  /**
   * Tests a projection of a single column
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testTrivialSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.c FROM pig.tableA AS tableA";
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE c AS c;"
    };
    String[] expectedOutput = {
        "(100)",
        "(200)",
        "(300)",
        "(400)",
        "(500)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a projection over all columns
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testAllSelect() throws IOException, ParseException {
    String sql = "SELECT * FROM pig.tableA AS tableA";
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE a AS a, b AS b, c AS c;"
    };
    String[] expectedOutput = {
        "(0,0,100)",
        "(1,1,200)",
        "(2,2,300)",
        "(3,3,400)",
        "(4,4,500)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a projection of multiple columns with aliases that differ from its base table
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testFieldsWithAliasesSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.a as f1, tableA.b as f2, tableA.c as f3 FROM pig.tableA AS tableA";
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE a AS f1, b AS f2, c AS f3;"
    };
    String[] expectedOutput = {
        "(0,0,100)",
        "(1,1,200)",
        "(2,2,300)",
        "(3,3,400)",
        "(4,4,500)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests comparison operators
   */
  @Test
  public static void testComparisonOperators() throws IOException, ParseException {
    String sqlTemplate = "SELECT tableA.a FROM pig.tableA AS tableA WHERE tableA.a %s 1";
    String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FILTER view BY (a %s 1);",
        "view = FOREACH view GENERATE a AS a;"
    );

    String[] sqlOperators = {">", ">=", "<", "<=", "=", "!="};
    String[] pigOperators = {">", ">=", "<", "<=", "==", "!="};

    String[] expectedOutputs = {
        "(2),(3),(4)",
        "(1),(2),(3),(4)",
        "(0)",
        "(0),(1)",
        "(1)",
        "(0),(2),(3),(4)"
    };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      String sql = String.format(sqlTemplate, sqlOperators[i]);
      String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigOperators[i]).split("\n");
      String[] expectedOutput = expectedOutputs[i].split(",");

      String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
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
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FILTER view BY a IN (2, 3, 4);",
        "view = FOREACH view GENERATE a AS a;"
    };
    String[] expectedOutput = {
        "(2)",
        "(3)",
        "(4)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests the NOT Hive IN operator
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testNotHiveInFilterSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.a FROM pig.tableA AS tableA WHERE tableA.a NOT IN (2,3,4)";
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FILTER view BY NOT a IN (2, 3, 4);",
        "view = FOREACH view GENERATE a AS a;"
    };
    String[] expectedOutput = {
        "(0)",
        "(1)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a filter with multiple conditions
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public static void testMultipleFilterSelect() throws IOException, ParseException {
    String sql = "SELECT tableA.b FROM pig.tableA AS tableA WHERE (tableA.a > 2 AND tableA.b > 3) OR tableA.c = 100";
    String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FILTER view BY (((a > 2) AND (b > 3)) OR (c == 100));",
        "view = FOREACH view GENERATE b AS b;"
    };
    String[] expectedOutput = {
        "(0)",
        "(4)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests the following aggregate functions with a single grouping:
   *   COUNT, AVG, SUM, MAX, MIN
   */
  @Test
  public static void testAggregateFunctions() throws IOException, ParseException {
    final String sqlTemplate = "SELECT a AS a, %s(b) AS agg FROM pig.tableB GROUP BY a";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/pig/tableb.json' USING JsonLoader('a:int, b:int');",
        "view = GROUP view BY (a);",
        "view = FOREACH view GENERATE group AS a, %s(view.b) AS agg;"
    );

    final String[] aggregateFunctions = {"COUNT", "AVG", "SUM", "MAX", "MIN"};

    final String[] expectedOutputs = {
        "(0,2);(1,3)",
        "(0,5.0);(1,1.6666666666666667)",
        "(0,10);(1,5)",
        "(0,10);(1,2)",
        "(0,0);(1,1)"
    };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, aggregateFunctions[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, aggregateFunctions[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests aggregate function without grouping
   */
  @Test
  public static void testNoGroupingAggregate() throws IOException, ParseException {
    final String sql = "SELECT COUNT(*) FROM pig.tableA";
    final String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE 0 AS xf0;",
        "view = GROUP view ALL;",
        "view = FOREACH view GENERATE COUNT(view) AS EXPRx0;"
    };
    final String[] expectedOutput = {
        "(5)"
    };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests aggregate functions multiple field grouping
   */
  @Test
  public static void testMultipleFieldGroupingAggregate() throws IOException, ParseException {
    final String sql = "SELECT COUNT(a) AS count, a, b, AVG(a) FROM pig.tableA GROUP BY b, a";
    final String[] expectedPigLatin = {
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = FOREACH view GENERATE b AS b, a AS a;",
        "view = GROUP view BY (b, a);",
        "view = FOREACH view GENERATE group.b AS b, group.a AS a, COUNT(view.a) AS count, AVG(view.a) AS EXPRx3;",
        "view = FOREACH view GENERATE count AS count, a AS a, b AS b, EXPRx3 AS EXPRx3;"
    };
    final String[] expectedOutput = {
        "(1,0,0,0.0)",
        "(1,1,1,1.0)",
        "(1,2,2,2.0)",
        "(1,3,3,3.0)",
        "(1,4,4,4.0)"
    };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a SELF-JOIN on a table with a single condition
   */
  @Test
  public static void testSelfJoinSingleCondition() throws IOException, ParseException {
    final String sql = "SELECT tl.a as a, tl.b as bl, tr.b as br FROM pig.tableLeft tl JOIN pig.tableLeft tr ON tl.a = tr.a";
    final String[] expectedPigLatin = {
        "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');",
        "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = JOIN CORAL_PIG_ALIAS_1 BY (a), CORAL_PIG_ALIAS_2 BY (a);",
        "view = FOREACH view GENERATE CORAL_PIG_ALIAS_1::a AS a, CORAL_PIG_ALIAS_1::b AS b, CORAL_PIG_ALIAS_1::c AS c, CORAL_PIG_ALIAS_2::a AS a0, CORAL_PIG_ALIAS_2::b AS b0, CORAL_PIG_ALIAS_2::c AS c0;",
        "view = FOREACH view GENERATE a AS a, b AS bl, b0 AS br;",
    };
    final String[] expectedOutput = {
        "(0,2,2)",
        "(0,2,1)",
        "(0,1,2)",
        "(0,1,1)",
        "(1,4,4)",
        "(1,4,3)",
        "(1,3,4)",
        "(1,3,3)"
    };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests a SELF-JOIN on a table with multiple conditions
   */
  @Test
  public static void testSelfJoinMultipleConditions() throws IOException, ParseException {
    final String sql = "SELECT * FROM pig.tableLeft tl JOIN pig.tableLeft tr ON tl.a = tr.a AND tl.b = tr.b AND tl.c = tr.c ";
    final String[] expectedPigLatin = {
        "CORAL_PIG_ALIAS_1 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');",
        "CORAL_PIG_ALIAS_2 = LOAD 'src/test/resources/data/pig/tableleft.json' USING JsonLoader('a:int, b:int, c:int');",
        "view = JOIN CORAL_PIG_ALIAS_1 BY (a, b, c), CORAL_PIG_ALIAS_2 BY (a, b, c);",
        "view = FOREACH view GENERATE CORAL_PIG_ALIAS_1::a AS a, CORAL_PIG_ALIAS_1::b AS b, CORAL_PIG_ALIAS_1::c AS c, CORAL_PIG_ALIAS_2::a AS a0, CORAL_PIG_ALIAS_2::b AS b0, CORAL_PIG_ALIAS_2::c AS c0;",
        "view = FOREACH view GENERATE a AS a, b AS b, c AS c, a0 AS a0, b0 AS b0, c0 AS c0;"
    };
    final String[] expectedOutput = {
        "(0,1,10,0,1,10)",
        "(0,2,10,0,2,10)",
        "(1,3,10,1,3,10)",
        "(1,4,10,1,4,10)"
    };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
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
        "view = FOREACH view GENERATE a AS a, b AS b, c AS c, d AS d, e AS e;"
    );

    final String[] sqlJoinTypes = {"", "FULL", "LEFT", "RIGHT"};

    final String[] pigJoinTypes = {"", " FULL OUTER", " LEFT OUTER", " RIGHT OUTER"};

    final String[] expectedOutputs = {
        "(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10)",
        "(0,2,10,,);(0,1,10,,);(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10);(,,,2,40);(,,,2,30)",
        "(0,2,10,,);(0,1,10,,);(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10)",
        "(1,4,10,1,20);(1,4,10,1,10);(1,3,10,1,20);(1,3,10,1,10);(,,,2,40);(,,,2,30)"
    };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlJoinTypes[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigJoinTypes[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutput(OUTPUT_RELATION, expectedOutput);
    }
  }

}
