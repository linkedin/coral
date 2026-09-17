/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.datagen.rel.CanonicalPredicateExtractor;
import com.linkedin.coral.datagen.rel.DnfRewriter;
import com.linkedin.coral.datagen.rel.ProjectPullUpController;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static org.testng.Assert.*;


/**
 * Integration tests for RegexDomainInferenceProgram using real Hive/Calcite infrastructure.
 * Tests symbolic regex-based domain inference with various SQL expressions.
 */
public class RegexDomainInferenceProgramTest {
  private static final String CORAL_DATA_GENERATION_TEST_DIR = "coral.datagen.test.dir";
  private HiveConf conf;
  private HiveToRelConverter converter;
  private DomainInferenceProgram program;

  @BeforeClass
  public void setup() {
    conf = getHiveConf();
    String testDir = conf.get(CORAL_DATA_GENERATION_TEST_DIR);
    try {
      FileUtils.deleteDirectory(new File(testDir));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    SessionState.start(conf);
    Driver driver = new Driver(conf);
    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS test"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.T (name STRING, age INT, birthdate DATE)"));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS test.complex (a INT, b STRING, c ARRAY<DOUBLE>, s STRUCT<name:STRING, age:INT>, m MAP<STRING, STRING>, sarr ARRAY<STRUCT<name:STRING, age:INT>>)"));
    run(driver,
        String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.deep (" + "id INT, "
            + "nested_struct STRUCT<inner_val:STRING, inner_count:INT, sub:STRUCT<value:STRING, count:INT>>, "
            + "map_of_structs MAP<STRING, STRUCT<label:STRING, score:INT>>, " + "str_map MAP<STRING, STRING>" + ")"));
    // Table with interleaved complex types: struct containing map, struct containing array,
    // array of maps of structs, etc.
    run(driver,
        String.join("\n", "",
            "CREATE TABLE IF NOT EXISTS test.interleaved (" + "id INT, "
                + "sm STRUCT<tags:MAP<STRING, STRING>, scores:ARRAY<INT>, name:STRING>, "
                + "ams ARRAY<MAP<STRING, STRING>>, " + "msa MAP<STRING, ARRAY<STRING>>, "
                + "amss ARRAY<STRUCT<props:MAP<STRING, STRING>, label:STRING, value:INT>>" + ")"));
    run(driver, String.join("\n", "", "USE test"));
    try {
      java.util.List<String> dbs = Hive.get(conf).getMSC().getAllDatabases();
      if (!dbs.contains("test")) {
        throw new IllegalStateException("Metastore does not contain database 'test'. Databases: " + dbs);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to validate metastore after setup", e);
    }
    converter = new HiveToRelConverter(createMscAdapter(conf));

    // Initialize generic domain inference program with all transformers
    program = DomainInferenceProgram.withDefaultTransformers();
  }

  @AfterClass
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_DATA_GENERATION_TEST_DIR)));
  }

  private static HiveConf getHiveConf() {
    InputStream hiveConfStream = RegexDomainInferenceProgramTest.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_DATA_GENERATION_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/datagen/" + UUID.randomUUID());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
    hiveConf.set("_hive.local.session.path", "/tmp/coral");
    return hiveConf;
  }

  private static HiveMscAdapter createMscAdapter(HiveConf conf) {
    try {
      return new HiveMscAdapter(Hive.get(conf).getMSC());
    } catch (MetaException | HiveException e) {
      throw new RuntimeException("Could not initialize Hive Metastore Client Adapter: " + e);
    }
  }

  private static void run(Driver driver, String sql) {
    while (true) {
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  private static RelNode normalize(RelNode root) {
    return ProjectPullUpController.applyUntilFixedPoint(root, 100);
  }

  /**
   * Converts SQL query to a normalized RelNode.
   */
  private RelNode convertAndNormalizeQuery(String sql) {
    RelNode root = converter.convertSql(sql);
    return normalize(root);
  }

  /**
   * Extracts canonical predicates from a RelNode and converts them to DNF form.
   */
  private DnfRewriter.Output extractPredicatesAsDnf(RelNode normalized) {
    CanonicalPredicateExtractor.Output extracted = CanonicalPredicateExtractor.extract(normalized);
    RexBuilder rexBuilder = normalized.getCluster().getRexBuilder();
    return DnfRewriter.convert(extracted, rexBuilder);
  }

  /**
   * Helper interface for custom assertions on derived domains.
   */
  @FunctionalInterface
  private interface DomainAssertion {
    void accept(Domain<?, ?> inputDomain);
  }

  /**
   * Common test logic for domain inference.
   * Extracts the predicate, derives the input domain, and runs optional assertions.
   * Supports EQUALS, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL.
   */
  private void testDomainInference(String testName, String sql, DomainAssertion assertion) {
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    assertFalse(dnfOut.disjuncts.isEmpty(), testName + ": should have at least one disjunct");

    RexNode disjunct = dnfOut.disjuncts.get(0);

    assertTrue(disjunct instanceof org.apache.calcite.rex.RexCall, testName + ": disjunct should be a RexCall");
    org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) disjunct;

    // Use the program's predicate-based inference which handles all comparison operators
    Domain<?, ?> inputDomain = program.deriveInputDomainFromPredicate(call);

    assertFalse(inputDomain.isEmpty(), testName + ": derived input domain should not be empty");

    // Run custom assertions if provided
    if (assertion != null) {
      assertion.accept(inputDomain);
    }

    // Verify that samples can be generated
    List<?> examples = inputDomain.sample(5);
    assertFalse(examples.isEmpty(), testName + ": should generate at least one sample");
  }

  // ==================== Simple Expression Tests ====================

  @Test
  public void testSimpleSubstring() {
    testDomainInference("Simple SUBSTRING Test", "SELECT * FROM test.T WHERE SUBSTRING(name, 1, 4) = '2000'",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          RegexDomain regex = (RegexDomain) inputDomain;
          List<?> examples = inputDomain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString();
            // Remove regex anchors for validation
            String cleaned = s.replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(cleaned.startsWith("2000"), "Should start with 2000: " + s);
          }
        });
  }

  @Test
  public void testSimpleLower() {
    testDomainInference("Simple LOWER Test", "SELECT * FROM test.T WHERE LOWER(name) = 'abc'", inputDomain -> {
      assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
      List<?> examples = inputDomain.sample(5);
      assertEquals(examples.size(), 5, "Should generate 5 examples");
      for (Object ex : examples) {
        String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
        assertEquals("abc", s.toLowerCase(), "Should be case-insensitive 'abc': " + s);
      }
    });
  }

  @Test
  public void testSimpleCastIntToString() {
    testDomainInference("Simple CAST INT to STRING Test", "SELECT * FROM test.T WHERE CAST(age AS STRING) = '25'",
        inputDomain -> {
          assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
          IntegerDomain intDomain = (IntegerDomain) inputDomain;
          assertTrue(intDomain.contains(25), "Should contain 25");
          assertTrue(intDomain.isSingleton(), "Should be singleton");
        });
  }

  // ==================== Nested Expression Tests ====================

  @Test
  public void testNestedLowerSubstring() {
    testDomainInference("Nested LOWER(SUBSTRING) Test",
        "SELECT * FROM test.T WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(3);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 3, "Should have at least 3 characters");
            assertEquals("abc", s.substring(0, 3).toLowerCase(), "First 3 chars should be 'abc': " + s);
          }
        });
  }

  @Test
  public void testSubstringOfCastDate() {
    testDomainInference("SUBSTRING(CAST(DATE)) Test",
        "SELECT * FROM test.T WHERE SUBSTRING(CAST(birthdate AS STRING), 1, 4) = '2000'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.startsWith("2000"), "Should start with 2000: " + s);
            assertTrue(s.matches("\\d{4}-\\d{2}-\\d{2}"), "Should be valid date format: " + s);
          }
        });
  }

  @Test
  public void testSubstringOfCastInteger() {
    testDomainInference("Substring of Cast Integer Test",
        "SELECT * FROM test.T WHERE SUBSTRING(CAST(age AS STRING), 1, 2) = '25'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.startsWith("25"), "Should start with 25: " + s);
            assertTrue(s.matches("\\d+"), "Should be all digits: " + s);
          }
        });
  }

  // ==================== Arithmetic and Cross-Domain Tests ====================

  @Test
  public void testArithmeticExpression() {
    testDomainInference("Arithmetic Expression Test", "SELECT * FROM test.T WHERE age * 2 + 5 = 25", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(10), "Should contain 10 (since 10 * 2 + 5 = 25)");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testCastWithArithmetic() {
    // This test demonstrates cross-domain inference: String → Integer → through arithmetic
    testDomainInference("CAST with Arithmetic Test", "SELECT * FROM test.T WHERE CAST(age * 2 AS STRING) = '50'",
        inputDomain -> {
          assertTrue(inputDomain instanceof IntegerDomain,
              "Expected IntegerDomain but got " + inputDomain.getClass().getSimpleName());
          IntegerDomain intDomain = (IntegerDomain) inputDomain;
          assertTrue(intDomain.contains(25), "Domain should contain 25 (since 25 * 2 = 50)");
          assertFalse(intDomain.contains(30), "Domain should not contain 30");
        });
  }

  @Test
  public void testSimpleCastStringToDate() {
    testDomainInference("Simple Cast String to Date Test",
        "SELECT * FROM test.T WHERE CAST(name AS DATE) = DATE '2024-01-15'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          RegexDomain regex = (RegexDomain) inputDomain;
          assertTrue(regex.isLiteral(), "Should be literal");
          List<?> examples = inputDomain.sample(1);
          assertEquals(examples.size(), 1, "Should have exactly 1 example");
        });
  }

  @Test
  public void testSimpleCastStringToInteger() {
    testDomainInference("Simple Cast String to Integer Test", "SELECT * FROM test.T WHERE CAST(name AS INT) = 42",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(1);
          String s = examples.get(0).toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
          assertEquals("42", s, "Should be '42'");
        });
  }

  @Test
  public void testCastStringToIntegerWithArithmetic() {
    testDomainInference("Cast String to Integer with Arithmetic Test",
        "SELECT * FROM test.T WHERE CAST(name AS INT) + 10 = 50", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(1);
          String s = examples.get(0).toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
          assertEquals("40", s, "Should be '40' (since 40 + 10 = 50)");
        });
  }

  @Test
  public void testNestedSubstringOverlapping() {
    // Nested substring where ranges overlap
    // SUBSTRING(SUBSTRING(name, 1, 10), 3, 5) extracts positions 3-7 of substring at positions 1-10
    // This is equivalent to extracting positions 3-7 of the original string (overlapping ranges)
    testDomainInference("Nested Substring Overlapping Test",
        "SELECT * FROM test.T WHERE SUBSTRING(SUBSTRING(name, 1, 10), 3, 5) = 'hello'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(3);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 7, "Should have at least 7 characters (2 prefix + 5 for hello)");
            assertTrue(s.substring(2, 7).equals("hello"), "Chars 3-7 should be 'hello': " + s);
          }
        });
  }

  @Test
  public void testNestedSubstringDisjoint() {
    // Nested substring where ranges are disjoint from the beginning
    // SUBSTRING(SUBSTRING(name, 5, 15), 1, 3) extracts positions 1-3 of substring at positions 5-19
    // This is equivalent to extracting positions 5-7 of the original string (disjoint from positions 1-4)
    testDomainInference("Nested Substring Disjoint Test",
        "SELECT * FROM test.T WHERE SUBSTRING(SUBSTRING(name, 5, 15), 1, 3) = 'xyz'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(3);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 7, "Should have at least 7 characters (4 prefix + 3 for xyz)");
            assertTrue(s.substring(4, 7).equals("xyz"), "Chars 5-7 should be 'xyz': " + s);
          }
        });
  }

  @Test
  public void testSimpleUpper() {
    testDomainInference("Simple UPPER Test", "SELECT * FROM test.T WHERE UPPER(name) = 'ABC'", inputDomain -> {
      assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
      List<?> examples = inputDomain.sample(5);
      assertEquals(5, examples.size(), "Should generate 5 examples");
      for (Object ex : examples) {
        String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
        assertEquals("ABC", s.toUpperCase(), "Should be case-insensitive 'ABC': " + s);
      }
    });
  }

  @Test
  public void testSimpleMinus() {
    testDomainInference("Simple MINUS Test", "SELECT * FROM test.T WHERE age - 3 = 7", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(10), "Should contain 10 (since 10 - 3 = 7)");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testMinusWithArithmetic() {
    testDomainInference("MINUS with Arithmetic Test", "SELECT * FROM test.T WHERE age * 2 - 5 = 15", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(10), "Should contain 10 (since 10 * 2 - 5 = 15)");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testNestedUpperSubstring() {
    testDomainInference("Nested UPPER(SUBSTRING) Test",
        "SELECT * FROM test.T WHERE UPPER(SUBSTRING(name, 1, 3)) = 'ABC'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(3);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 3, "Should have at least 3 characters");
            assertEquals("ABC", s.substring(0, 3).toUpperCase(), "First 3 chars should be 'ABC': " + s);
          }
        });
  }

  @Test
  public void testAbsSimple() {
    testDomainInference("Simple ABS Test", "SELECT * FROM test.T WHERE ABS(age) = 5", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(5), "Should contain 5");
      assertTrue(intDomain.contains(-5), "Should contain -5");
      assertFalse(intDomain.contains(0), "Should not contain 0");
    });
  }

  @Test
  public void testAbsZero() {
    testDomainInference("ABS Zero Test", "SELECT * FROM test.T WHERE ABS(age) = 0", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(0), "Should contain 0");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testAbsWithArithmetic() {
    testDomainInference("ABS with Arithmetic Test", "SELECT * FROM test.T WHERE ABS(age + 1) = 5", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(4), "Should contain 4 (since ABS(4 + 1) = 5)");
      assertTrue(intDomain.contains(-6), "Should contain -6 (since ABS(-6 + 1) = 5)");
    });
  }

  @Test
  public void testSimpleNegate() {
    testDomainInference("Simple NEGATE Test", "SELECT * FROM test.T WHERE -age = -5", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(5), "Should contain 5 (since -5 negated = 5)");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testNegateWithArithmetic() {
    testDomainInference("NEGATE with Arithmetic Test", "SELECT * FROM test.T WHERE -(age + 2) = -10", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(8), "Should contain 8 (since -(8 + 2) = -10)");
      assertTrue(intDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testSimpleConcatSuffix() {
    testDomainInference("Simple CONCAT(x, suffix) Test",
        "SELECT * FROM test.T WHERE CONCAT(name, 'World') = 'HelloWorld'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          RegexDomain regex = (RegexDomain) inputDomain;
          assertTrue(regex.isLiteral(), "Should be literal");
          assertEquals(regex.getLiteralValue(), "Hello", "Should strip 'World' suffix");
        });
  }

  @Test
  public void testSimpleConcatPrefix() {
    testDomainInference("Simple CONCAT(prefix, x) Test",
        "SELECT * FROM test.T WHERE CONCAT('Hello', name) = 'HelloWorld'", inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          RegexDomain regex = (RegexDomain) inputDomain;
          assertTrue(regex.isLiteral(), "Should be literal");
          assertEquals(regex.getLiteralValue(), "World", "Should strip 'Hello' prefix");
        });
  }

  @Test
  public void testSimpleTrim() {
    testDomainInference("Simple TRIM Test", "SELECT * FROM test.T WHERE TRIM(name) = 'abc'", inputDomain -> {
      assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
      RegexDomain regex = (RegexDomain) inputDomain;
      // The result should accept 'abc' with optional surrounding spaces.
      List<?> samples = regex.sample(5);
      assertFalse(samples.isEmpty(), "Should generate samples");
      for (Object ex : samples) {
        String s = ex.toString();
        assertEquals(s.trim(), "abc", "Sample should trim to 'abc': '" + s + "'");
      }
    });
  }

  @Test
  public void testGreaterThan() {
    testDomainInference("Greater Than Test", "SELECT * FROM test.T WHERE age > 10", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(11), "Should contain 11");
      assertTrue(intDomain.contains(100), "Should contain 100");
      assertFalse(intDomain.contains(10), "Should not contain 10");
      assertFalse(intDomain.contains(5), "Should not contain 5");
    });
  }

  @Test
  public void testGreaterThanOrEqual() {
    testDomainInference("Greater Than Or Equal Test", "SELECT * FROM test.T WHERE age >= 10", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(10), "Should contain 10");
      assertTrue(intDomain.contains(100), "Should contain 100");
      assertFalse(intDomain.contains(9), "Should not contain 9");
    });
  }

  @Test
  public void testLessThan() {
    testDomainInference("Less Than Test", "SELECT * FROM test.T WHERE age < 10", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(9), "Should contain 9");
      assertTrue(intDomain.contains(0), "Should contain 0");
      assertFalse(intDomain.contains(10), "Should not contain 10");
    });
  }

  @Test
  public void testGreaterThanWithArithmetic() {
    testDomainInference("Greater Than with Arithmetic Test", "SELECT * FROM test.T WHERE age + 3 > 10", inputDomain -> {
      assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
      IntegerDomain intDomain = (IntegerDomain) inputDomain;
      assertTrue(intDomain.contains(8), "Should contain 8 (since 8 + 3 = 11 > 10)");
      assertFalse(intDomain.contains(7), "Should not contain 7 (since 7 + 3 = 10 = 10)");
    });
  }

  // ========== Multi-column resolution helpers ==========

  /**
   * Helper interface for assertions on resolved multi-column domains.
   */
  @FunctionalInterface
  private interface MultiColumnAssertion {
    void accept(Map<AccessPath, Domain<?, ?>> pathDomains);
  }

  /**
   * Resolves domains for all columns using resolveAllPaths and runs assertions.
   * Table test.T has columns: name(0:STRING), age(1:INT), birthdate(2:DATE).
   */
  private void testMultiColumnResolution(String testName, String sql, MultiColumnAssertion assertion) {
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    assertFalse(dnfOut.disjuncts.isEmpty(), testName + ": should have at least one disjunct");

    Map<AccessPath, Domain<?, ?>> pathDomains = program.resolveAllPaths(dnfOut.disjuncts);
    assertFalse(pathDomains.isEmpty(), testName + ": should resolve at least one column");

    assertion.accept(pathDomains);
  }

  // ========== Conjunction (AND) tests ==========

  @Test
  public void testAndTwoColumnsDifferent() {
    // AND of predicates on two different columns: name and age
    testMultiColumnResolution("AND two different columns",
        "SELECT * FROM test.T WHERE LOWER(name) = 'abc' AND age = 25", pathDomains -> {
          // Column 0: name
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          Domain<?, ?> nameDomain = pathDomains.get(AccessPath.of(0));
          assertTrue(nameDomain instanceof RegexDomain, "name should be RegexDomain");
          assertFalse(nameDomain.isEmpty(), "name domain should not be empty");

          // Column 1: age
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          Domain<?, ?> ageDomain = pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain instanceof IntegerDomain, "age should be IntegerDomain");
          IntegerDomain ageInt = (IntegerDomain) ageDomain;
          assertTrue(ageInt.contains(25), "age should contain 25");
          assertTrue(ageInt.isSingleton(), "age should be singleton {25}");
        });
  }

  @Test
  public void testAndSameColumnIntersection() {
    // AND of two predicates on the same column: age > 5 AND age < 10
    testMultiColumnResolution("AND same column intersection", "SELECT * FROM test.T WHERE age > 5 AND age < 10",
        pathDomains -> {
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(6), "Should contain 6");
          assertTrue(ageDomain.contains(9), "Should contain 9");
          assertFalse(ageDomain.contains(5), "Should not contain 5");
          assertFalse(ageDomain.contains(10), "Should not contain 10");
        });
  }

  @Test
  public void testAndSameColumnTightRange() {
    // AND that produces a very narrow range: age >= 10 AND age <= 12
    testMultiColumnResolution("AND same column tight range", "SELECT * FROM test.T WHERE age >= 10 AND age <= 12",
        pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(10), "Should contain 10");
          assertTrue(ageDomain.contains(11), "Should contain 11");
          assertTrue(ageDomain.contains(12), "Should contain 12");
          assertFalse(ageDomain.contains(9), "Should not contain 9");
          assertFalse(ageDomain.contains(13), "Should not contain 13");
        });
  }

  @Test
  public void testAndThreeColumns() {
    // AND of predicates on all three columns
    testMultiColumnResolution("AND three columns",
        "SELECT * FROM test.T WHERE LOWER(name) = 'abc' AND age > 18 AND SUBSTRING(CAST(birthdate AS STRING), 1, 4) = '2000'",
        pathDomains -> {
          // Column 0: name
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          assertTrue(pathDomains.get(AccessPath.of(0)) instanceof RegexDomain, "name should be RegexDomain");

          // Column 1: age
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(19), "age should contain 19");
          assertFalse(ageDomain.contains(18), "age should not contain 18");

          // Column 2: birthdate
          assertTrue(pathDomains.containsKey(AccessPath.of(2)), "Should resolve column 2 (birthdate)");
          assertTrue(pathDomains.get(AccessPath.of(2)) instanceof RegexDomain, "birthdate should be RegexDomain");
        });
  }

  @Test
  public void testAndWithExpressions() {
    // AND with expressions on different columns
    testMultiColumnResolution("AND with expressions",
        "SELECT * FROM test.T WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc' AND age * 2 + 5 = 25", pathDomains -> {
          // Column 0: name via LOWER(SUBSTRING(...))
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(AccessPath.of(0));
          List<?> nameExamples = nameDomain.sample(3);
          for (Object ex : nameExamples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 3, "Should have at least 3 characters");
            assertEquals("abc", s.substring(0, 3).toLowerCase(), "First 3 chars should be 'abc'");
          }

          // Column 1: age via age * 2 + 5 = 25 → age = 10
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(10), "age should contain 10");
          assertTrue(ageDomain.isSingleton(), "age should be singleton {10}");
        });
  }

  // ========== Disjunction (OR) tests ==========

  @Test
  public void testOrSameColumnUnion() {
    // OR of two predicates on the same column: age = 10 OR age = 20
    testMultiColumnResolution("OR same column union", "SELECT * FROM test.T WHERE age = 10 OR age = 20",
        pathDomains -> {
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(10), "Should contain 10");
          assertTrue(ageDomain.contains(20), "Should contain 20");
          assertFalse(ageDomain.contains(15), "Should not contain 15");
        });
  }

  @Test
  public void testOrSameColumnRanges() {
    // OR of two ranges: age < 5 OR age > 95
    testMultiColumnResolution("OR same column ranges", "SELECT * FROM test.T WHERE age < 5 OR age > 95",
        pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(0), "Should contain 0");
          assertTrue(ageDomain.contains(4), "Should contain 4");
          assertTrue(ageDomain.contains(96), "Should contain 96");
          assertTrue(ageDomain.contains(1000), "Should contain 1000");
          assertFalse(ageDomain.contains(5), "Should not contain 5");
          assertFalse(ageDomain.contains(50), "Should not contain 50");
          assertFalse(ageDomain.contains(95), "Should not contain 95");
        });
  }

  @Test
  public void testOrThreeValues() {
    // OR of three discrete values
    testMultiColumnResolution("OR three values", "SELECT * FROM test.T WHERE age = 1 OR age = 2 OR age = 3",
        pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(1), "Should contain 1");
          assertTrue(ageDomain.contains(2), "Should contain 2");
          assertTrue(ageDomain.contains(3), "Should contain 3");
          assertFalse(ageDomain.contains(0), "Should not contain 0");
          assertFalse(ageDomain.contains(4), "Should not contain 4");
        });
  }

  @Test
  public void testOrDifferentColumns() {
    // OR on different columns: name = 'abc' OR age = 25.
    // Both columns should resolve; per-column union semantics give name a regex matching
    // case-insensitive 'abc' (from disjunct 1) and age a domain containing 25 (from disjunct 2).
    testMultiColumnResolution("OR different columns", "SELECT * FROM test.T WHERE LOWER(name) = 'abc' OR age = 25",
        pathDomains -> {
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");

          RegexDomain nameDomain = (RegexDomain) pathDomains.get(AccessPath.of(0));
          for (Object ex : nameDomain.sample(3)) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals(s.toLowerCase(), "abc", "name sample should lower to 'abc': " + s);
          }

          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(25), "age domain should contain 25");
        });
  }

  // ========== Combined AND/OR tests ==========

  @Test
  public void testOrOfConjunctions() {
    // (age > 10 AND age < 20) OR (age > 50 AND age < 60)
    // Each disjunct is a conjunction that produces a range, then union across disjuncts
    testMultiColumnResolution("OR of conjunctions",
        "SELECT * FROM test.T WHERE (age > 10 AND age < 20) OR (age > 50 AND age < 60)", pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(15), "Should contain 15 (in first range)");
          assertTrue(ageDomain.contains(55), "Should contain 55 (in second range)");
          assertFalse(ageDomain.contains(10), "Should not contain 10");
          assertFalse(ageDomain.contains(20), "Should not contain 20");
          assertFalse(ageDomain.contains(30), "Should not contain 30 (between ranges)");
          assertFalse(ageDomain.contains(50), "Should not contain 50");
          assertFalse(ageDomain.contains(60), "Should not contain 60");
        });
  }

  @Test
  public void testOrOfConjunctionsMultiColumn() {
    // (name = 'alice' AND age = 25) OR (name = 'bob' AND age = 30)
    // Column 0 (name): union of 'alice' and 'bob' domains
    // Column 1 (age): union of {25} and {30}
    testMultiColumnResolution("OR of conjunctions multi-column",
        "SELECT * FROM test.T WHERE (LOWER(name) = 'alice' AND age = 25) OR (LOWER(name) = 'bob' AND age = 30)",
        pathDomains -> {
          // Column 1: age should be {25} ∪ {30}
          assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(25), "age should contain 25");
          assertTrue(ageDomain.contains(30), "age should contain 30");
          assertFalse(ageDomain.contains(27), "age should not contain 27");

          // Column 0: name should be union of 'alice' and 'bob' regex domains
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          assertTrue(pathDomains.get(AccessPath.of(0)) instanceof RegexDomain, "name should be RegexDomain");
        });
  }

  @Test
  public void testAndRangeWithEquality() {
    // Combine a range and an equality on the same column: age >= 20 AND age = 25
    // Intersection should produce {25}
    testMultiColumnResolution("AND range with equality", "SELECT * FROM test.T WHERE age >= 20 AND age = 25",
        pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(25), "Should contain 25");
          assertTrue(ageDomain.isSingleton(), "Should be singleton {25}");
        });
  }

  @Test
  public void testOrWithExpressions() {
    // OR with expressions: age + 5 = 15 OR age * 2 = 40
    // First disjunct: age = 10, Second: age = 20, Union: {10, 20}
    testMultiColumnResolution("OR with expressions", "SELECT * FROM test.T WHERE age + 5 = 15 OR age * 2 = 40",
        pathDomains -> {
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(10), "Should contain 10 (since 10 + 5 = 15)");
          assertTrue(ageDomain.contains(20), "Should contain 20 (since 20 * 2 = 40)");
          assertFalse(ageDomain.contains(15), "Should not contain 15");
        });
  }

  @Test
  public void testAndMixedDomainTypes() {
    // String column AND integer column together
    testMultiColumnResolution("AND mixed domain types",
        "SELECT * FROM test.T WHERE SUBSTRING(name, 1, 3) = 'abc' AND age >= 0 AND age <= 100", pathDomains -> {
          // Column 0: name
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(AccessPath.of(0));
          List<?> nameExamples = nameDomain.sample(3);
          for (Object ex : nameExamples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.startsWith("abc"), "Should start with 'abc': " + s);
          }

          // Column 1: age in [0, 100]
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
          assertTrue(ageDomain.contains(0), "Should contain 0");
          assertTrue(ageDomain.contains(50), "Should contain 50");
          assertTrue(ageDomain.contains(100), "Should contain 100");
          assertFalse(ageDomain.contains(-1), "Should not contain -1");
          assertFalse(ageDomain.contains(101), "Should not contain 101");
        });
  }

  @Test
  public void testSinglePredicateViaResolveAll() {
    // Verify that resolveAllPaths works correctly for a single simple predicate
    testMultiColumnResolution("Single predicate via resolveAll", "SELECT * FROM test.T WHERE age = 42", pathDomains -> {
      assertEquals(1, pathDomains.size(), "Should resolve exactly one column");
      assertTrue(pathDomains.containsKey(AccessPath.of(1)), "Should resolve column 1 (age)");
      IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
      assertTrue(ageDomain.contains(42), "Should contain 42");
      assertTrue(ageDomain.isSingleton(), "Should be singleton");
    });
  }

  @Test
  public void testSingleStringPredicateViaResolveAll() {
    // Single string predicate through resolveAllPaths
    testMultiColumnResolution("Single string predicate via resolveAll",
        "SELECT * FROM test.T WHERE LOWER(name) = 'hello'", pathDomains -> {
          assertEquals(1, pathDomains.size(), "Should resolve exactly one column");
          assertTrue(pathDomains.containsKey(AccessPath.of(0)), "Should resolve column 0 (name)");
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(AccessPath.of(0));
          List<?> examples = nameDomain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals("hello", s.toLowerCase(), "Should be case-insensitive 'hello'");
          }
        });
  }

  @Test
  public void testOrOverlappingRanges() {
    // OR of overlapping ranges: age > 5 OR age > 3
    // Union should be age > 3 (i.e., [4, MAX])
    testMultiColumnResolution("OR overlapping ranges", "SELECT * FROM test.T WHERE age > 5 OR age > 3", pathDomains -> {
      IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
      assertTrue(ageDomain.contains(4), "Should contain 4 (from age > 3)");
      assertTrue(ageDomain.contains(6), "Should contain 6 (from both)");
      assertTrue(ageDomain.contains(100), "Should contain 100");
      assertFalse(ageDomain.contains(3), "Should not contain 3");
    });
  }

  @Test
  public void testAndContradictoryRange() {
    // AND that produces empty intersection: age > 10 AND age < 5
    // The intersection should be empty
    RelNode normalized = convertAndNormalizeQuery("SELECT * FROM test.T WHERE age > 10 AND age < 5");
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    Map<AccessPath, Domain<?, ?>> pathDomains = program.resolveAllPaths(dnfOut.disjuncts);

    if (pathDomains.containsKey(AccessPath.of(1))) {
      IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(AccessPath.of(1));
      assertTrue(ageDomain.isEmpty(), "Contradictory range should be empty");
    }
    // It's also acceptable if the column isn't present at all (empty domain omitted)
  }

  // ========== Complex type (struct, array, map) tests ==========

  /**
   * Helper for multi-column resolution on test.complex table.
   * Columns: a(0:INT), b(1:STRING), c(2:ARRAY<DOUBLE>), s(3:STRUCT<name:STRING, age:INT>),
   *          m(4:MAP<STRING,STRING>), sarr(5:ARRAY<STRUCT<name:STRING, age:INT>>)
   */
  private void testComplexTypeResolution(String testName, String sql, MultiColumnAssertion assertion) {
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    assertFalse(dnfOut.disjuncts.isEmpty(), testName + ": should have at least one disjunct");

    Map<AccessPath, Domain<?, ?>> pathDomains = program.resolveAllPaths(dnfOut.disjuncts);
    assertFalse(pathDomains.isEmpty(), testName + ": should resolve at least one column");

    assertion.accept(pathDomains);
  }

  @Test
  public void testStructFieldEquality() {
    // Struct field access: s.name = 'alice'
    testComplexTypeResolution("Struct field equality", "SELECT * FROM test.complex WHERE s.name = 'alice'",
        pathDomains -> {
          AccessPath sName = AccessPath.ofField(3, "name");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name at " + sName);
          RegexDomain domain = (RegexDomain) pathDomains.get(sName);
          assertTrue(domain.isLiteral(), "s.name should be the literal 'alice'");
          assertEquals(domain.getLiteralValue(), "alice");
        });
  }

  @Test
  public void testStructFieldWithFunction() {
    // LOWER(s.name) = 'alice'
    testComplexTypeResolution("Struct field with LOWER", "SELECT * FROM test.complex WHERE LOWER(s.name) = 'alice'",
        pathDomains -> {
          AccessPath sName = AccessPath.ofField(3, "name");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name");
          RegexDomain domain = (RegexDomain) pathDomains.get(sName);
          List<?> examples = domain.sample(5);
          for (Object ex : examples) {
            String str = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals("alice", str.toLowerCase(), "Should be case-insensitive 'alice': " + str);
          }
        });
  }

  @Test
  public void testStructIntField() {
    // s.age > 21
    testComplexTypeResolution("Struct int field", "SELECT * FROM test.complex WHERE s.age > 21", pathDomains -> {
      AccessPath sAge = AccessPath.ofField(3, "age");
      assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
      IntegerDomain domain = (IntegerDomain) pathDomains.get(sAge);
      assertTrue(domain.contains(22), "Should contain 22");
      assertFalse(domain.contains(21), "Should not contain 21");
    });
  }

  @Test
  public void testMapElementEquality() {
    // m['key1'] = 'val'
    testComplexTypeResolution("Map element equality", "SELECT * FROM test.complex WHERE m['key1'] = 'val'",
        pathDomains -> {
          AccessPath mKey1 = AccessPath.ofMapKey(4, "key1");
          assertTrue(pathDomains.containsKey(mKey1), "Should resolve m['key1']");
          RegexDomain domain = (RegexDomain) pathDomains.get(mKey1);
          assertTrue(domain.isLiteral(), "m['key1'] should be the literal 'val'");
          assertEquals(domain.getLiteralValue(), "val");
        });
  }

  @Test
  public void testMapElementWithFunction() {
    // UPPER(m['k']) = 'ABC'
    testComplexTypeResolution("Map element with UPPER", "SELECT * FROM test.complex WHERE UPPER(m['k']) = 'ABC'",
        pathDomains -> {
          AccessPath mK = AccessPath.ofMapKey(4, "k");
          assertTrue(pathDomains.containsKey(mK), "Should resolve m['k']");
          RegexDomain domain = (RegexDomain) pathDomains.get(mK);
          List<?> examples = domain.sample(5);
          for (Object ex : examples) {
            String str = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals("abc", str.toLowerCase(), "Should be case-insensitive 'abc': " + str);
          }
        });
  }

  @Test
  public void testMultiColumnWithStruct() {
    // s.name = 'x' AND s.age > 10: two different struct fields → two AccessPath entries
    testComplexTypeResolution("Multi-column with struct",
        "SELECT * FROM test.complex WHERE s.name = 'x' AND s.age > 10", pathDomains -> {
          AccessPath sName = AccessPath.ofField(3, "name");
          AccessPath sAge = AccessPath.ofField(3, "age");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name");
          assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(sName);
          assertTrue(nameDomain.isLiteral(), "s.name should be the literal 'x'");
          assertEquals(nameDomain.getLiteralValue(), "x");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(sAge);
          assertTrue(ageDomain.contains(11), "s.age should contain 11");
          assertFalse(ageDomain.contains(10), "s.age should not contain 10");
        });
  }

  @Test
  public void testStructAndFlatColumn() {
    // a > 5 AND s.name = 'z': flat column + struct field
    testComplexTypeResolution("Struct and flat column", "SELECT * FROM test.complex WHERE a > 5 AND s.name = 'z'",
        pathDomains -> {
          AccessPath colA = AccessPath.of(0);
          AccessPath sName = AccessPath.ofField(3, "name");
          assertTrue(pathDomains.containsKey(colA), "Should resolve column a");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name");
          IntegerDomain aDomain = (IntegerDomain) pathDomains.get(colA);
          assertTrue(aDomain.contains(6), "a should contain 6");
          assertFalse(aDomain.contains(5), "a should not contain 5");
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(sName);
          assertTrue(nameDomain.isLiteral(), "s.name should be the literal 'z'");
          assertEquals(nameDomain.getLiteralValue(), "z");
        });
  }

  @Test
  public void testNestedArrayOfStructs() {
    // sarr[0].name = 'bob': array element → struct field
    testComplexTypeResolution("Nested array of structs", "SELECT * FROM test.complex WHERE sarr[0].name = 'bob'",
        pathDomains -> {
          // Hive 0-based index is shifted to Calcite 1-based: sarr[0] becomes ITEM($5, 1).
          // The resolved path should end at the "name" struct field and the domain should be
          // the literal 'bob'.
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 5 && !path.isFlat()
                && "name".equals(path.getPath().get(path.getPath().size() - 1).getFieldName())) {
              found = true;
              RegexDomain domain = (RegexDomain) entry.getValue();
              assertTrue(domain.isLiteral(), "sarr[].name should be the literal 'bob'");
              assertEquals(domain.getLiteralValue(), "bob");
            }
          }
          assertTrue(found, "Should resolve sarr[0].name to literal 'bob' (column 5, path ending at 'name')");
        });
  }

  // ========== Deeply nested types (test.deep) ==========

  /**
   * Helper for multi-column resolution on test.deep table.
   * Columns: id(0:INT),
   *          nested_struct(1:STRUCT<inner_val:STRING, inner_count:INT, sub:STRUCT<value:STRING, count:INT>>),
   *          map_of_structs(2:MAP<STRING, STRUCT<label:STRING, score:INT>>),
   *          str_map(3:MAP<STRING, STRING>)
   */
  private void testDeepTypeResolution(String testName, String sql, MultiColumnAssertion assertion) {
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    assertFalse(dnfOut.disjuncts.isEmpty(), testName + ": should have at least one disjunct");

    Map<AccessPath, Domain<?, ?>> pathDomains = program.resolveAllPaths(dnfOut.disjuncts);
    assertFalse(pathDomains.isEmpty(), testName + ": should resolve at least one column");

    assertion.accept(pathDomains);
  }

  // --- Deeply nested struct: struct.inner.value ---

  @Test
  public void testDoubleNestedStructField() {
    // nested_struct.sub.value = 'hello': struct → inner struct → string field
    testDeepTypeResolution("Double-nested struct field",
        "SELECT * FROM test.deep WHERE nested_struct.sub.value = 'hello'", pathDomains -> {
          // Path: $1.sub.value
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && path.getPath().size() == 2) {
              found = true;
              assertEquals("sub", path.getPath().get(0).getFieldName(), "First element should be 'sub'");
              assertEquals("value", path.getPath().get(1).getFieldName(), "Second element should be 'value'");
              RegexDomain domain = (RegexDomain) entry.getValue();
              assertTrue(domain.isLiteral(), "nested_struct.sub.value should be literal 'hello'");
              assertEquals(domain.getLiteralValue(), "hello");
            }
          }
          assertTrue(found, "Should resolve nested_struct.sub.value");
        });
  }

  @Test
  public void testDoubleNestedStructIntField() {
    // nested_struct.sub.count > 100: struct → inner struct → int field with comparison
    testDeepTypeResolution("Double-nested struct int field",
        "SELECT * FROM test.deep WHERE nested_struct.sub.count > 100", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && path.getPath().size() == 2) {
              found = true;
              assertEquals("count", path.getPath().get(1).getFieldName());
              IntegerDomain domain = (IntegerDomain) entry.getValue();
              assertTrue(domain.contains(101), "Should contain 101");
              assertFalse(domain.contains(100), "Should not contain 100");
            }
          }
          assertTrue(found, "Should resolve nested_struct.sub.count");
        });
  }

  // --- Function on deeply nested struct field ---

  @Test
  public void testFunctionOnDoubleNestedStruct() {
    // LOWER(SUBSTRING(nested_struct.sub.value, 1, 3)) = 'abc'
    // Chains: LOWER → SUBSTRING → RexFieldAccess(RexFieldAccess($1, sub), value)
    testDeepTypeResolution("Function on double-nested struct",
        "SELECT * FROM test.deep WHERE LOWER(SUBSTRING(nested_struct.sub.value, 1, 3)) = 'abc'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && path.getPath().size() == 2
                && "value".equals(path.getPath().get(1).getFieldName())) {
              found = true;
              RegexDomain domain = (RegexDomain) entry.getValue();
              List<?> examples = domain.sample(5);
              for (Object ex : examples) {
                String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
                assertTrue(s.length() >= 3, "Should have at least 3 chars");
                assertEquals("abc", s.substring(0, 3).toLowerCase(), "First 3 chars should be 'abc': " + s);
              }
            }
          }
          assertTrue(found, "Should resolve nested_struct.inner.value through LOWER(SUBSTRING(...))");
        });
  }

  // --- Map of structs: map_of_structs['key'].label ---

  @Test
  public void testMapOfStructsFieldAccess() {
    // map_of_structs['key1'].label = 'important'
    testDeepTypeResolution("Map of structs field access",
        "SELECT * FROM test.deep WHERE map_of_structs['key1'].label = 'important'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 2 && !path.isFlat()
                && "label".equals(path.getPath().get(path.getPath().size() - 1).getFieldName())) {
              found = true;
              RegexDomain domain = (RegexDomain) entry.getValue();
              assertTrue(domain.isLiteral(), "map_of_structs['key1'].label should be literal 'important'");
              assertEquals(domain.getLiteralValue(), "important");
            }
          }
          assertTrue(found, "Should resolve map_of_structs['key1'].label to literal 'important'");
        });
  }

  @Test
  public void testMapOfStructsIntFieldComparison() {
    // map_of_structs['key1'].score >= 80 AND map_of_structs['key1'].score <= 100
    testDeepTypeResolution("Map of structs int field range",
        "SELECT * FROM test.deep WHERE map_of_structs['key1'].score >= 80 AND map_of_structs['key1'].score <= 100",
        pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 2 && !path.isFlat()) {
              // Check that the path includes 'score' field
              List<AccessPath.PathElement> elements = path.getPath();
              boolean hasScore = elements.stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "score".equals(e.getFieldName()));
              if (hasScore) {
                found = true;
                IntegerDomain domain = (IntegerDomain) entry.getValue();
                assertTrue(domain.contains(80), "Should contain 80");
                assertTrue(domain.contains(90), "Should contain 90");
                assertTrue(domain.contains(100), "Should contain 100");
                assertFalse(domain.contains(79), "Should not contain 79");
                assertFalse(domain.contains(101), "Should not contain 101");
              }
            }
          }
          assertTrue(found, "Should resolve map_of_structs['key1'].score range");
        });
  }

  // --- Flat struct fields alongside nested struct fields ---

  @Test
  public void testFlatAndNestedStructFieldsTogether() {
    // nested_struct.inner_val = 'hello' AND nested_struct.sub.count > 50
    // Tests flat struct field (depth=1) alongside nested struct field (depth=2) on same column
    testDeepTypeResolution("Flat and nested struct fields together",
        "SELECT * FROM test.deep WHERE nested_struct.inner_val = 'hello' AND nested_struct.sub.count > 50",
        pathDomains -> {
          // Flat field: $1.inner_val
          boolean foundFlat = false;
          boolean foundNested = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1) {
              if (path.getPath().size() == 1 && "inner_val".equals(path.getPath().get(0).getFieldName())) {
                foundFlat = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                assertTrue(domain.isLiteral(), "inner_val should be literal 'hello'");
                assertEquals(domain.getLiteralValue(), "hello");
              }
              if (path.getPath().size() == 2 && "count".equals(path.getPath().get(1).getFieldName())) {
                foundNested = true;
                IntegerDomain domain = (IntegerDomain) entry.getValue();
                assertTrue(domain.contains(51), "sub.count should contain 51");
                assertFalse(domain.contains(50), "sub.count should not contain 50");
              }
            }
          }
          assertTrue(foundFlat, "Should resolve nested_struct.inner_val");
          assertTrue(foundNested, "Should resolve nested_struct.sub.count");
        });
  }

  // ========== Combined multi-aspect tests ==========

  @Test
  public void testFlatStructMapAllColumnsAndOr() {
    // Combines: flat col, struct field, map access, AND within disjuncts, OR across disjuncts
    // (a > 5 AND s.name = 'alice' AND m['role'] = 'admin') OR (a = 1 AND s.age <= 30)
    testComplexTypeResolution("Flat + struct + map with AND/OR", "SELECT * FROM test.complex WHERE "
        + "(a > 5 AND s.name = 'alice' AND m['role'] = 'admin') OR (a = 1 AND s.age <= 30)", pathDomains -> {
          // Column 0 (a): union of [6, MAX] and {1}
          AccessPath colA = AccessPath.of(0);
          assertTrue(pathDomains.containsKey(colA), "Should resolve column a");
          IntegerDomain aDomain = (IntegerDomain) pathDomains.get(colA);
          assertTrue(aDomain.contains(1), "a should contain 1 (from second disjunct)");
          assertTrue(aDomain.contains(6), "a should contain 6 (from first disjunct)");
          assertTrue(aDomain.contains(100), "a should contain 100 (from first disjunct)");
          assertFalse(aDomain.contains(2), "a should not contain 2 (not in either disjunct)");
          assertFalse(aDomain.contains(5), "a should not contain 5 (not in either disjunct)");
        });
  }

  @Test
  public void testStructFieldWithArithmeticAndComparison() {
    // Arithmetic on struct int field + comparison: s.age * 2 + 5 = 25 → s.age = 10
    testComplexTypeResolution("Struct field arithmetic + comparison",
        "SELECT * FROM test.complex WHERE s.age * 2 + 5 = 25", pathDomains -> {
          AccessPath sAge = AccessPath.ofField(3, "age");
          assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
          IntegerDomain domain = (IntegerDomain) pathDomains.get(sAge);
          assertTrue(domain.contains(10), "s.age should contain 10 (since 10*2+5=25)");
          assertTrue(domain.isSingleton(), "s.age should be singleton {10}");
        });
  }

  @Test
  public void testCastStructFieldCrossDomain() {
    // CAST(s.age AS STRING) = '42': cross-domain from RegexDomain → IntegerDomain through struct field
    testComplexTypeResolution("CAST struct field cross-domain",
        "SELECT * FROM test.complex WHERE CAST(s.age AS STRING) = '42'", pathDomains -> {
          AccessPath sAge = AccessPath.ofField(3, "age");
          assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
          IntegerDomain domain = (IntegerDomain) pathDomains.get(sAge);
          assertTrue(domain.contains(42), "s.age should contain 42");
          assertTrue(domain.isSingleton(), "s.age should be singleton {42}");
        });
  }

  @Test
  public void testFunctionOnArrayOfStructsField() {
    // UPPER(sarr[0].name) = 'ALICE': function → ITEM → field access
    testComplexTypeResolution("Function on array-of-structs field",
        "SELECT * FROM test.complex WHERE UPPER(sarr[0].name) = 'ALICE'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 5 && !path.isFlat()) {
              found = true;
              RegexDomain domain = (RegexDomain) entry.getValue();
              List<?> examples = domain.sample(5);
              for (Object ex : examples) {
                String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
                assertEquals("alice", s.toLowerCase(), "Should be case-insensitive 'alice': " + s);
              }
            }
          }
          assertTrue(found, "Should resolve UPPER(sarr[0].name)");
        });
  }

  @Test
  public void testArrayOfStructsArithmeticOnIntField() {
    // sarr[0].age + 10 > 30 → sarr[0].age > 20
    testComplexTypeResolution("Array-of-structs int field arithmetic",
        "SELECT * FROM test.complex WHERE sarr[0].age + 10 > 30", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 5 && !path.isFlat()) {
              List<AccessPath.PathElement> elems = path.getPath();
              boolean hasAge = elems.stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "age".equals(e.getFieldName()));
              if (hasAge) {
                found = true;
                IntegerDomain domain = (IntegerDomain) entry.getValue();
                assertTrue(domain.contains(21), "Should contain 21");
                assertFalse(domain.contains(20), "Should not contain 20");
              }
            }
          }
          assertTrue(found, "Should resolve sarr[0].age through arithmetic");
        });
  }

  @Test
  public void testMultipleMapKeysWithDifferentDomains() {
    // m['x'] = 'hello' AND m['y'] = 'world': two different map keys → two AccessPath entries
    testComplexTypeResolution("Multiple map keys",
        "SELECT * FROM test.complex WHERE m['x'] = 'hello' AND m['y'] = 'world'", pathDomains -> {
          AccessPath mX = AccessPath.ofMapKey(4, "x");
          AccessPath mY = AccessPath.ofMapKey(4, "y");
          assertTrue(pathDomains.containsKey(mX), "Should resolve m['x']");
          assertTrue(pathDomains.containsKey(mY), "Should resolve m['y']");
          RegexDomain xDomain = (RegexDomain) pathDomains.get(mX);
          RegexDomain yDomain = (RegexDomain) pathDomains.get(mY);
          assertTrue(xDomain.isLiteral(), "m['x'] should be literal 'hello'");
          assertEquals(xDomain.getLiteralValue(), "hello");
          assertTrue(yDomain.isLiteral(), "m['y'] should be literal 'world'");
          assertEquals(yDomain.getLiteralValue(), "world");
        });
  }

  @Test
  public void testStructFieldOrDisjunction() {
    // s.name = 'alice' OR s.name = 'bob': same struct field in OR → union of regex domains
    testComplexTypeResolution("Struct field OR disjunction",
        "SELECT * FROM test.complex WHERE s.name = 'alice' OR s.name = 'bob'", pathDomains -> {
          AccessPath sName = AccessPath.ofField(3, "name");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name");
          RegexDomain domain = (RegexDomain) pathDomains.get(sName);
          // The union should accept exactly 'alice' and 'bob' — both must be in, others out.
          assertTrue(domain.getAutomaton().run("alice"), "domain should accept 'alice'");
          assertTrue(domain.getAutomaton().run("bob"), "domain should accept 'bob'");
          assertFalse(domain.getAutomaton().run("carol"), "domain should not accept 'carol'");
          assertFalse(domain.getAutomaton().run(""), "domain should not accept empty string");
        });
  }

  @Test
  public void testMixedTypesAcrossAllColumnKinds() {
    // Combines flat, struct, map, array-of-struct in one query with AND
    // a >= 0 AND a <= 1000 AND LOWER(s.name) = 'test' AND s.age > 0 AND m['env'] = 'prod'
    testComplexTypeResolution("Mixed all column kinds", "SELECT * FROM test.complex WHERE "
        + "a >= 0 AND a <= 1000 AND LOWER(s.name) = 'test' AND s.age > 0 AND m['env'] = 'prod'", pathDomains -> {
          // Flat column: a in [0, 1000]
          AccessPath colA = AccessPath.of(0);
          assertTrue(pathDomains.containsKey(colA), "Should resolve column a");
          IntegerDomain aDomain = (IntegerDomain) pathDomains.get(colA);
          assertTrue(aDomain.contains(0), "a should contain 0");
          assertTrue(aDomain.contains(500), "a should contain 500");
          assertTrue(aDomain.contains(1000), "a should contain 1000");
          assertFalse(aDomain.contains(-1), "a should not contain -1");
          assertFalse(aDomain.contains(1001), "a should not contain 1001");

          // Struct string field: s.name (case-insensitive 'test')
          AccessPath sName = AccessPath.ofField(3, "name");
          assertTrue(pathDomains.containsKey(sName), "Should resolve s.name");
          RegexDomain nameDomain = (RegexDomain) pathDomains.get(sName);
          List<?> nameExamples = nameDomain.sample(3);
          for (Object ex : nameExamples) {
            String str = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals("test", str.toLowerCase(), "Should be case-insensitive 'test'");
          }

          // Struct int field: s.age > 0
          AccessPath sAge = AccessPath.ofField(3, "age");
          assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
          IntegerDomain ageDomain = (IntegerDomain) pathDomains.get(sAge);
          assertTrue(ageDomain.contains(1), "s.age should contain 1");
          assertFalse(ageDomain.contains(0), "s.age should not contain 0");

          // Map access: m['env'] = 'prod'
          AccessPath mEnv = AccessPath.ofMapKey(4, "env");
          assertTrue(pathDomains.containsKey(mEnv), "Should resolve m['env']");
          assertTrue(pathDomains.get(mEnv) instanceof RegexDomain, "m['env'] should be RegexDomain");
        });
  }

  @Test
  public void testDeepNestedStructWithArithmeticAndFunction() {
    // CAST(nested_struct.sub.count * 3 - 10 AS STRING) = '50'
    // Chains: CAST → MINUS → TIMES → RexFieldAccess(RexFieldAccess($1, sub), count)
    // → count * 3 - 10 = 50 → count * 3 = 60 → count = 20
    testDeepTypeResolution("Deep struct with arithmetic + CAST",
        "SELECT * FROM test.deep WHERE CAST(nested_struct.sub.count * 3 - 10 AS STRING) = '50'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && path.getPath().size() == 2
                && "count".equals(path.getPath().get(1).getFieldName())) {
              found = true;
              IntegerDomain domain = (IntegerDomain) entry.getValue();
              assertTrue(domain.contains(20), "count should contain 20 (since 20*3-10=50)");
              assertTrue(domain.isSingleton(), "count should be singleton {20}");
            }
          }
          assertTrue(found, "Should resolve nested_struct.sub.count through CAST + arithmetic");
        });
  }

  @Test
  public void testDeepMixedWithOrConjunctions() {
    // (nested_struct.sub.count > 50 AND id < 100) OR (nested_struct.sub.count = 10 AND id > 200)
    // Tests: double-nested struct field, flat column, OR of conjunctions, union + intersection
    testDeepTypeResolution("Deep struct OR conjunctions",
        "SELECT * FROM test.deep WHERE "
            + "(nested_struct.sub.count > 50 AND id < 100) OR (nested_struct.sub.count = 10 AND id > 200)",
        pathDomains -> {
          // id: union of [MIN, 99] and [201, MAX]
          AccessPath colId = AccessPath.of(0);
          assertTrue(pathDomains.containsKey(colId), "Should resolve id");
          IntegerDomain idDomain = (IntegerDomain) pathDomains.get(colId);
          assertTrue(idDomain.contains(50), "id should contain 50 (from first disjunct)");
          assertTrue(idDomain.contains(99), "id should contain 99 (from first disjunct)");
          assertTrue(idDomain.contains(201), "id should contain 201 (from second disjunct)");
          assertTrue(idDomain.contains(500), "id should contain 500 (from second disjunct)");
          assertFalse(idDomain.contains(100), "id should not contain 100");
          assertFalse(idDomain.contains(150), "id should not contain 150");
          assertFalse(idDomain.contains(200), "id should not contain 200");
        });
  }

  @Test
  public void testFunctionOnMapOfStructsField() {
    // LOWER(map_of_structs['team'].label) = 'engineering'
    testDeepTypeResolution("Function on map-of-structs field",
        "SELECT * FROM test.deep WHERE LOWER(map_of_structs['team'].label) = 'engineering'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 2 && !path.isFlat()) {
              found = true;
              RegexDomain domain = (RegexDomain) entry.getValue();
              List<?> examples = domain.sample(5);
              for (Object ex : examples) {
                String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
                assertEquals("engineering", s.toLowerCase(), "Should be case-insensitive 'engineering': " + s);
              }
            }
          }
          assertTrue(found, "Should resolve LOWER(map_of_structs['team'].label)");
        });
  }

  @Test
  public void testAbsOnStructFieldWithOrDisjunction() {
    // ABS(s.age) = 5 OR s.age > 100: struct field in both disjuncts
    testComplexTypeResolution("ABS on struct field with OR",
        "SELECT * FROM test.complex WHERE ABS(s.age) = 5 OR s.age > 100", pathDomains -> {
          AccessPath sAge = AccessPath.ofField(3, "age");
          assertTrue(pathDomains.containsKey(sAge), "Should resolve s.age");
          IntegerDomain domain = (IntegerDomain) pathDomains.get(sAge);
          // ABS(age) = 5 → age = 5 or age = -5; union with age > 100
          assertTrue(domain.contains(5), "Should contain 5");
          assertTrue(domain.contains(-5), "Should contain -5");
          assertTrue(domain.contains(101), "Should contain 101 (from age > 100)");
          assertFalse(domain.contains(0), "Should not contain 0");
          assertFalse(domain.contains(50), "Should not contain 50");
        });
  }

  @Test
  public void testSubstringOnMapValueWithCast() {
    // SUBSTRING(m['date'], 1, 4) = '2024': function chain on map value
    testComplexTypeResolution("SUBSTRING on map value",
        "SELECT * FROM test.complex WHERE SUBSTRING(m['date'], 1, 4) = '2024'", pathDomains -> {
          AccessPath mDate = AccessPath.ofMapKey(4, "date");
          assertTrue(pathDomains.containsKey(mDate), "Should resolve m['date']");
          RegexDomain domain = (RegexDomain) pathDomains.get(mDate);
          List<?> examples = domain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.startsWith("2024"), "Should start with '2024': " + s);
          }
        });
  }

  // ========== Interleaved complex types (struct↔map↔array) ==========

  /**
   * Helper for test.interleaved table.
   * Columns: id(0:INT),
   *          sm(1:STRUCT<tags:MAP<STRING,STRING>, scores:ARRAY<INT>, name:STRING>),
   *          ams(2:ARRAY<MAP<STRING,STRING>>),
   *          msa(3:MAP<STRING, ARRAY<STRING>>),
   *          amss(4:ARRAY<STRUCT<props:MAP<STRING,STRING>, label:STRING, value:INT>>)
   */
  private void testInterleavedResolution(String testName, String sql, MultiColumnAssertion assertion) {
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    assertFalse(dnfOut.disjuncts.isEmpty(), testName + ": should have at least one disjunct");

    Map<AccessPath, Domain<?, ?>> pathDomains = program.resolveAllPaths(dnfOut.disjuncts);
    assertFalse(pathDomains.isEmpty(), testName + ": should resolve at least one column");

    assertion.accept(pathDomains);
  }

  @Test
  public void testStructContainingMapAccess() {
    // sm.tags['color'] = 'red': struct → map key
    // Path: $1.tags → ITEM(..., 'color')
    testInterleavedResolution("Struct containing map access",
        "SELECT * FROM test.interleaved WHERE sm.tags['color'] = 'red'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && path.getPath().size() >= 2) {
              // Should have FIELD:tags then MAP_KEY:color
              boolean hasTags = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "tags".equals(e.getFieldName()));
              boolean hasColor = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.MAP_KEY && "color".equals(e.getMapKey()));
              if (hasTags && hasColor) {
                found = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                assertTrue(domain.isLiteral(), "sm.tags['color'] should be literal 'red'");
                assertEquals(domain.getLiteralValue(), "red");
              }
            }
          }
          assertTrue(found, "Should resolve sm.tags['color'] (struct → map)");
        });
  }

  @Test
  public void testStructContainingMapWithFunction() {
    // LOWER(sm.tags['city']) = 'seattle': LOWER → struct → map
    testInterleavedResolution("Function on struct→map",
        "SELECT * FROM test.interleaved WHERE LOWER(sm.tags['city']) = 'seattle'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1 && !path.isFlat()) {
              boolean hasTags = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "tags".equals(e.getFieldName()));
              boolean hasCity = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.MAP_KEY && "city".equals(e.getMapKey()));
              if (hasTags && hasCity) {
                found = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                List<?> examples = domain.sample(5);
                for (Object ex : examples) {
                  String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
                  assertEquals("seattle", s.toLowerCase(), "Should be case-insensitive 'seattle': " + s);
                }
              }
            }
          }
          assertTrue(found, "Should resolve LOWER(sm.tags['city']) (LOWER → struct → map)");
        });
  }

  @Test
  public void testArrayOfMapsAccess() {
    // ams[0]['status'] = 'active': array → map key
    // Path: ITEM($2, 1) → ITEM(..., 'status')
    testInterleavedResolution("Array of maps access",
        "SELECT * FROM test.interleaved WHERE ams[0]['status'] = 'active'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 2 && path.getPath().size() >= 2) {
              boolean hasArrayIdx =
                  path.getPath().stream().anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.ARRAY_INDEX);
              boolean hasMapKey = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.MAP_KEY && "status".equals(e.getMapKey()));
              if (hasArrayIdx && hasMapKey) {
                found = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                assertTrue(domain.isLiteral(), "ams[0]['status'] should be literal 'active'");
                assertEquals(domain.getLiteralValue(), "active");
              }
            }
          }
          assertTrue(found, "Should resolve ams[0]['status'] (array → map)");
        });
  }

  @Test
  public void testArrayOfStructsContainingMapAccess() {
    // amss[0].props['env'] = 'prod': array → struct → map
    // Path: ITEM($4, 1).props → ITEM(..., 'env')
    testInterleavedResolution("Array → struct → map",
        "SELECT * FROM test.interleaved WHERE amss[0].props['env'] = 'prod'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 4 && path.getPath().size() >= 3) {
              boolean hasArrayIdx =
                  path.getPath().stream().anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.ARRAY_INDEX);
              boolean hasProps = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "props".equals(e.getFieldName()));
              boolean hasEnv = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.MAP_KEY && "env".equals(e.getMapKey()));
              if (hasArrayIdx && hasProps && hasEnv) {
                found = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                assertTrue(domain.isLiteral(), "amss[0].props['env'] should be literal 'prod'");
                assertEquals(domain.getLiteralValue(), "prod");
              }
            }
          }
          assertTrue(found, "Should resolve amss[0].props['env'] (array → struct → map)");
        });
  }

  @Test
  public void testArrayOfStructsMapWithFunction() {
    // UPPER(amss[0].props['tag']) = 'CRITICAL': UPPER → array → struct → map
    testInterleavedResolution("Function on array → struct → map",
        "SELECT * FROM test.interleaved WHERE UPPER(amss[0].props['tag']) = 'CRITICAL'", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 4 && path.getPath().size() >= 3) {
              boolean hasTag = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.MAP_KEY && "tag".equals(e.getMapKey()));
              if (hasTag) {
                found = true;
                RegexDomain domain = (RegexDomain) entry.getValue();
                List<?> examples = domain.sample(5);
                for (Object ex : examples) {
                  String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
                  assertEquals("critical", s.toLowerCase(), "Should be case-insensitive 'critical': " + s);
                }
              }
            }
          }
          assertTrue(found, "Should resolve UPPER(amss[0].props['tag'])");
        });
  }

  @Test
  public void testArrayOfStructsIntFieldWithArithmetic() {
    // amss[0].value * 2 + 1 = 101: arithmetic → array → struct (int field)
    // → value * 2 = 100 → value = 50
    testInterleavedResolution("Arithmetic on array → struct int field",
        "SELECT * FROM test.interleaved WHERE amss[0].value * 2 + 1 = 101", pathDomains -> {
          boolean found = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 4 && !path.isFlat()) {
              boolean hasValue = path.getPath().stream()
                  .anyMatch(e -> e.getKind() == AccessPath.PathElement.Kind.FIELD && "value".equals(e.getFieldName()));
              if (hasValue) {
                found = true;
                IntegerDomain domain = (IntegerDomain) entry.getValue();
                assertTrue(domain.contains(50), "value should contain 50 (since 50*2+1=101)");
                assertTrue(domain.isSingleton(), "value should be singleton {50}");
              }
            }
          }
          assertTrue(found, "Should resolve amss[0].value through arithmetic");
        });
  }

  @Test
  public void testInterleavedMultiPathAndOr() {
    // Combines struct→map, array→struct→map, and flat column with AND/OR:
    // (sm.tags['env'] = 'prod' AND amss[0].props['tier'] = 'p0' AND id > 100)
    //   OR (sm.name = 'fallback' AND id = 0)
    testInterleavedResolution("Interleaved multi-path AND/OR",
        "SELECT * FROM test.interleaved WHERE "
            + "(sm.tags['env'] = 'prod' AND amss[0].props['tier'] = 'p0' AND id > 100) "
            + "OR (sm.name = 'fallback' AND id = 0)",
        pathDomains -> {
          // id: union of [101, MAX] and {0}
          AccessPath colId = AccessPath.of(0);
          assertTrue(pathDomains.containsKey(colId), "Should resolve id");
          IntegerDomain idDomain = (IntegerDomain) pathDomains.get(colId);
          assertTrue(idDomain.contains(0), "id should contain 0 (from second disjunct)");
          assertTrue(idDomain.contains(101), "id should contain 101 (from first disjunct)");
          assertTrue(idDomain.contains(999), "id should contain 999 (from first disjunct)");
          assertFalse(idDomain.contains(1), "id should not contain 1");
          assertFalse(idDomain.contains(100), "id should not contain 100");
        });
  }

  @Test
  public void testStructFieldAndStructMapSameColumn() {
    // sm.name = 'test' AND sm.tags['role'] = 'admin': both paths on same root column (1),
    // but different AccessPaths (FIELD:name vs FIELD:tags + MAP_KEY:role)
    testInterleavedResolution("Struct field and struct→map on same column",
        "SELECT * FROM test.interleaved WHERE sm.name = 'test' AND sm.tags['role'] = 'admin'", pathDomains -> {
          // Should produce two distinct AccessPath entries both rooted at column 1
          boolean foundName = false;
          boolean foundTagsRole = false;
          for (Map.Entry<AccessPath, Domain<?, ?>> entry : pathDomains.entrySet()) {
            AccessPath path = entry.getKey();
            if (path.getColumnIndex() == 1) {
              if (path.getPath().size() == 1 && "name".equals(path.getPath().get(0).getFieldName())) {
                foundName = true;
                RegexDomain nameDomain = (RegexDomain) entry.getValue();
                assertTrue(nameDomain.isLiteral(), "sm.name should be literal 'test'");
                assertEquals(nameDomain.getLiteralValue(), "test");
              }
              if (path.getPath().size() == 2) {
                boolean hasTags = path.getPath().get(0).getKind() == AccessPath.PathElement.Kind.FIELD
                    && "tags".equals(path.getPath().get(0).getFieldName());
                boolean hasRole = path.getPath().get(1).getKind() == AccessPath.PathElement.Kind.MAP_KEY
                    && "role".equals(path.getPath().get(1).getMapKey());
                if (hasTags && hasRole) {
                  foundTagsRole = true;
                  RegexDomain roleDomain = (RegexDomain) entry.getValue();
                  assertTrue(roleDomain.isLiteral(), "sm.tags['role'] should be literal 'admin'");
                  assertEquals(roleDomain.getLiteralValue(), "admin");
                }
              }
            }
          }
          assertTrue(foundName, "Should resolve sm.name");
          assertTrue(foundTagsRole, "Should resolve sm.tags['role']");
        });
  }

}
