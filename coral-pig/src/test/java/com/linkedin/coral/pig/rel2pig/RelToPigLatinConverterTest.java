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

  static final String OUTPUT_VARIABLE = "view";

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
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int') ;",
        "view = FOREACH view GENERATE c ;"
    };
    String[] expectedOutput = {
        "(100)",
        "(200)",
        "(300)",
        "(400)",
        "(500)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_VARIABLE);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_VARIABLE, expectedOutput);
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
        "view = LOAD 'src/test/resources/data/pig/tablea.json' USING JsonLoader('a:int, b:int, c:int') ;",
        "view = FOREACH view GENERATE a , b , c ;"
    };
    String[] expectedOutput = {
        "(0,0,100)",
        "(1,1,200)",
        "(2,2,300)",
        "(3,3,400)",
        "(4,4,500)"
    };

    String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_VARIABLE);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    PigTest pigTest = new PigTest(translatedPigLatin);
    pigTest.assertOutput(OUTPUT_VARIABLE, expectedOutput);
  }

}
