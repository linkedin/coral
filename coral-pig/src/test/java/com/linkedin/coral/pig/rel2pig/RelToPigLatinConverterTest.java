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
}
