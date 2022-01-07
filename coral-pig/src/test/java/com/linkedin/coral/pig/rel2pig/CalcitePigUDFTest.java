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


public class CalcitePigUDFTest {

  static final String OUTPUT_RELATION = "view";
  static private HiveConf conf;

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
   * Tests that Nested UDFs can be used and resolved
   */
  @Test
  public static void testNestedUDF() throws IOException, ParseException {
    final String sql = "SELECT cos(sin(i1)) FROM functions.tablefields AS t";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');", "view = FOREACH view GENERATE COS(SIN(i1)) AS EXPRx0;" };
    final String[] expectedOutput = { "(0.6663667453928805)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(renameUDFPackage(translatedPigLatin));
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests that the if UDF can be used and resolved
   */
  @Test
  public static void testPigIfUDF() throws IOException, ParseException {
    final String sql = "SELECT if(i1 > 0, 'greater', 'lesser') FROM functions.tablefields AS t";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');", "view = FOREACH view GENERATE (CASE WHEN (i1 > 0) THEN 'greater' ELSE 'lesser' END) AS EXPRx0;" };
    final String[] expectedOutput = { "(greater)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(renameUDFPackage(translatedPigLatin));
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests that the log2 UDF can be used and resolved
   */
  @Test
  public static void testPigLog2UDF() throws IOException, ParseException {
    final String sql = "SELECT log2(i3) FROM functions.tablefields AS t";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');", "view = FOREACH view GENERATE LOG(i3)/LOG(2) AS EXPRx0;" };
    final String[] expectedOutput = { "(1.5849625007211563)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(renameUDFPackage(translatedPigLatin));
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests that the log UDF can be used and resolved
   */
  @Test
  public static void testPigLogUDF() throws IOException, ParseException {
    final String sql = "SELECT log(i2, i3) FROM functions.tablefields AS t";
    final String[] expectedPigLatin =
        { "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');", "view = FOREACH view GENERATE LOG(i3)/LOG(i2) AS EXPRx0;" };
    final String[] expectedOutput = { "(1.5849625007211563)" };

    final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

    Assert.assertEquals(translatedPigLatin, expectedPigLatin);

    final PigTest pigTest = new PigTest(renameUDFPackage(translatedPigLatin));
    pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
  }

  /**
   * Tests that substring UDF can be used and resolved
   */
  @Test
  public static void testPigSubstringUDF() throws IOException, ParseException {
    final String sqlTemplate = "SELECT %s FROM functions.tablefields AS t";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');",
        "view = FOREACH view GENERATE %s AS EXPRx0;");

    final String[] sqlFunctionCalls =
        { "substring(str, i2)", "substring(str, i2, i1)", "substr(str, i2)", "substr(str, i2, i1)" };

    final String[] pigFunctionCalls =
        { "SUBSTRING(str, (int)(i2) - 1, (int)SIZE(str))", "SUBSTRING(str, (int)(i2) - 1, (int)((int)(i2) - 1) + (int)(i1))", "SUBSTRING(str, (int)(i2) - 1, (int)SIZE(str))", "SUBSTRING(str, (int)(i2) - 1, (int)((int)(i2) - 1) + (int)(i1))", };

    final String[] expectedOutputs = { "(BcD)", "(B)", "(BcD)", "(B)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlFunctionCalls[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigFunctionCalls[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests that round UDF can be used and resolved
   */
  @Test
  public static void testPigRoundUDF() throws IOException, ParseException {
    final String sqlTemplate = "SELECT %s FROM functions.tablefields AS t";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');",
        "view = FOREACH view GENERATE %s AS EXPRx0;");

    final String[] sqlFunctionCalls =
        { "round(fl1)", "round(fl2)", "round(fl3)", "round(fl1, 1)", "round(fl2, 1)", "round(fl3, 1)" };

    final String[] pigFunctionCalls =
        { "ROUND(fl1)", "ROUND(fl2)", "ROUND(fl3)", "ROUND_TO(fl1, 1)", "ROUND_TO(fl2, 1)", "ROUND_TO(fl3, 1)" };

    final String[] expectedOutputs = { "(1)", "(1)", "(2)", "(1.0)", "(1.2)", "(1.8)" };

    for (int i = 0; i < expectedOutputs.length; ++i) {
      final String sql = String.format(sqlTemplate, sqlFunctionCalls[i]);
      final String[] expectedPigLatin = String.format(expectedPigLatinTemplate, pigFunctionCalls[i]).split("\n");
      final String[] expectedOutput = expectedOutputs[i].split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(translatedPigLatin);
      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }
  }

  /**
   * Tests that all Pig Builtin Functions work
   */
  @Test
  public static void testAllPigBuiltinFunctions() throws IOException, ParseException {
    final String sqlTemplate = "SELECT %s(%s) FROM functions.tablefields";
    final String expectedPigLatinTemplate = String.join("\n",
        "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');",
        "view = FOREACH view GENERATE %1$s(%2$s) AS EXPRx0;");

    final PigFunctionTest[] testSuite = { PigFunctionTest.create("atan", "ATAN", "i1",
        "(0.7853981633974483)"), PigFunctionTest.create("acos", "ACOS", "i1", "(0.0)"), PigFunctionTest.create("abs",
            "ABS", "i1", "(1)"), PigFunctionTest.create("abs", "ABS", "i_1", "(1)"), PigFunctionTest.create("cbrt",
                "CBRT", "i1",
                "(1.0)"), PigFunctionTest.create("cbrt", "CBRT", "i2", "(1.2599210498948732)"), PigFunctionTest.create(
                    "ceil", "CEIL", "fl1",
                    "(1.0)"), PigFunctionTest.create("ceil", "CEIL", "fl2", "(2.0)"), PigFunctionTest.create("ceil",
                        "CEIL", "fl3",
                        "(2.0)"), PigFunctionTest.create("ceiling", "CEIL", "fl1", "(1.0)"), PigFunctionTest.create(
                            "ceiling", "CEIL", "fl2",
                            "(2.0)"), PigFunctionTest.create("ceiling", "CEIL", "fl3", "(2.0)"), PigFunctionTest
                                .create("cos", "COS", "i1", "(0.5403023058681398)"), PigFunctionTest.create("exp",
                                    "EXP", "fl1", "(2.718281828459045)"), PigFunctionTest.create("floor", "FLOOR",
                                        "fl1", "(1.0)"), PigFunctionTest.create("floor", "FLOOR", "fl2",
                                            "(1.0)"), PigFunctionTest.create("floor", "FLOOR", "fl3",
                                                "(1.0)"), PigFunctionTest.create("log10", "LOG10", "fl3",
                                                    "(0.24303804868629444)"), PigFunctionTest.create("lower", "LOWER",
                                                        "str", "(abcd)"), PigFunctionTest.create("ln", "LOG", "fl3",
                                                            "(0.5596157879354227)"), PigFunctionTest.create(
                                                                "regexp_extract", "REGEX_EXTRACT",
                                                                "str, '.*(B)(.*D)', 1",
                                                                "(B)"), PigFunctionTest.create("regexp_extract",
                                                                    "REGEX_EXTRACT", "str, '.*(B)(.*D)', 2",
                                                                    "(cD)"), PigFunctionTest.create("sin", "SIN", "i1",
                                                                        "(0.8414709848078965)"), PigFunctionTest.create(
                                                                            "tan", "TAN", "i1",
                                                                            "(1.5574077246549023)"), PigFunctionTest
                                                                                .create("trim", "TRIM", "exstr",
                                                                                    "(eFg)"), PigFunctionTest.create(
                                                                                        "upper", "UPPER", "str",
                                                                                        "(ABCD)") };

    runTestSuite(sqlTemplate, expectedPigLatinTemplate, testSuite);
  }

  /**
   * Tests that all Pig UDFs work
   */
  @Test
  public static void testAllPigUDF() throws IOException, ParseException {
    final String sqlTemplate = "SELECT %s(%s) FROM functions.tablefields";
    final String expectedPigLatinTemplate = String.join("\n", "DEFINE PIG_UDF_%1$s dali.data.pig.udf.HiveUDF('%1$s');",
        "view = LOAD 'src/test/resources/data/functions/tablefields.json' USING JsonLoader('"
            + "i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, "
            + "str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray');",
        "view = FOREACH view GENERATE PIG_UDF_%1$s(%2$s) AS EXPRx0;");

    final PigFunctionTest[] testSuite = { PigFunctionTest.create("base64", "bin", "(YmluYXJ5)"), PigFunctionTest.create(
        "concat", "str, exstr",
        "(aBcDeFg )"), PigFunctionTest.create("concat_ws", "'Z', str, exstr", "(aBcDZeFg )"), PigFunctionTest.create(
            "conv", "i3, 4, i2", "(11)"), PigFunctionTest.create("decode", "bin, 'UTF-8'", "(binary)"), PigFunctionTest
                .create("degrees", "fl1", "(57.29577951308232)"), PigFunctionTest.create("factorial", "i3",
                    "(6)"), PigFunctionTest.create("hex", "str", "(61426344)"), PigFunctionTest.create("hex", "i3",
                        "(3)"), PigFunctionTest.create("hex", "bin", "(62696E617279)"), PigFunctionTest.create("instr",
                            "str, substr", "(3)"), PigFunctionTest.create("instr", "str, exstr", "(0)"), PigFunctionTest
                                .create("negative", "i1", "(-1)"), PigFunctionTest.create("negative", "i_1",
                                    "(1)"), PigFunctionTest.create("nvl", "i_1, 10", "(-1)"), PigFunctionTest
                                        .create("positive", "i1", "(1)"), PigFunctionTest.create("positive", "i_1",
                                            "(-1)"), PigFunctionTest.create("pow", "i2, i2", "(4.0)"), PigFunctionTest
                                                .create("pow", "fl2, fl2", "(1.3056984531291909)"), PigFunctionTest
                                                    .create("power", "i2, i2", "(4.0)"), PigFunctionTest.create("power",
                                                        "fl2, fl2",
                                                        "(1.3056984531291909)"), PigFunctionTest.create("radians", "i3",
                                                            "(0.05235987755982988)"), PigFunctionTest.create(
                                                                "regexp_replace", "str, substr, exstr",
                                                                "(aBeFg )"), PigFunctionTest.create("split", "str, 'c'",
                                                                    "({(aB),(D)})"), PigFunctionTest.create("unbase64",
                                                                        "str", "(h\u0017\u0003)") };

    runTestSuite(sqlTemplate, expectedPigLatinTemplate, testSuite);
  }

  /**
   * Runs the set of tests provided in the testSuite
   */
  private static void runTestSuite(String sqlTemplate, String expectedPigLatinTemplate, PigFunctionTest[] testSuite)
      throws IOException, ParseException {

    for (final PigFunctionTest pigFunctionTest : testSuite) {
      final String sql = String.format(sqlTemplate, pigFunctionTest.getSqlName(), pigFunctionTest.getOperands());
      final String[] expectedPigLatin = String
          .format(expectedPigLatinTemplate, pigFunctionTest.getPigName(), pigFunctionTest.getOperands()).split("\n");
      final String[] expectedOutput = pigFunctionTest.getExpectedOutput().split(";");

      final String[] translatedPigLatin = TestUtils.sqlToPigLatin(sql, OUTPUT_RELATION);

      Assert.assertEquals(translatedPigLatin, expectedPigLatin);

      final PigTest pigTest = new PigTest(renameUDFPackage(translatedPigLatin));

      pigTest.assertOutputAnyOrder(OUTPUT_RELATION, expectedOutput);
    }

  }

  /**
   * Renames the package "dali.data.pig.udf" to "org.apache.pig.builtin" so that
   * builtin HiveUDFs can be used in unit tests.
   */
  private static String[] renameUDFPackage(String[] pigLatinStatements) {
    for (int i = 0; i < pigLatinStatements.length; ++i) {
      pigLatinStatements[i] = pigLatinStatements[i].replace("dali.data.pig.udf", "org.apache.pig.builtin");
    }
    return pigLatinStatements;
  }

  /**
   * Represents a PigFunctionTest
   */
  private static class PigFunctionTest {
    private final String pigName;
    private final String sqlName;
    private final String operands;
    private final String expectedOutput;

    private PigFunctionTest(String sqlName, String pigName, String operands, String expectedOutput) {
      this.sqlName = sqlName;
      this.pigName = pigName;
      this.operands = operands;
      this.expectedOutput = expectedOutput;
    }

    public static PigFunctionTest create(String functionName, String operands, String output) {
      return new PigFunctionTest(functionName, functionName, operands, output);
    }

    public static PigFunctionTest create(String sqlName, String pigName, String operands, String output) {
      return new PigFunctionTest(sqlName, pigName, operands, output);
    }

    public String getSqlName() {
      return sqlName;
    }

    public String getPigName() {
      return pigName;
    }

    public String getOperands() {
      return operands;
    }

    public String getExpectedOutput() {
      return expectedOutput;
    }

  }

}
