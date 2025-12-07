/*
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

import static org.testng.Assert.*;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.datagen.rel.CanonicalPredicateExtractor;
import com.linkedin.coral.datagen.rel.DnfRewriter;
import com.linkedin.coral.datagen.rel.ProjectPullUpController;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

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
    program = new DomainInferenceProgram(
        Arrays.asList(
            new LowerRegexTransformer(),
            new SubstringRegexTransformer(),
            new PlusRegexTransformer(),
            new TimesRegexTransformer(),
            new CastRegexTransformer()));
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
   */
  private void testDomainInference(String testName, String sql, DomainAssertion assertion) {
    System.out.println("\n=== " + testName + " ===");
    System.out.println("SQL: " + sql);

    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    try {
      if (disjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) disjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0);
          RexNode rhs = call.getOperands().get(1);

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);
          System.out.println("RHS type: " + literal.getType().getSqlTypeName());

          // Create appropriate domain based on RHS literal type
          Domain<?, ?> outputDomain;
          if (isNumericType(literal.getType().getSqlTypeName())) {
            long numericValue = Long.parseLong(rhsValue);
            outputDomain = IntegerDomain.of(numericValue);
          } else {
            outputDomain = RegexDomain.literal(rhsValue);
          }
          System.out.println("Output domain: " + outputDomain);

          // Derive input domain constraint
          Domain<?, ?> inputDomain = program.deriveInputDomain(lhs, outputDomain);

          System.out.println("Derived input domain: " + inputDomain);
          System.out.println("Is empty: " + inputDomain.isEmpty());

          // Run custom assertions if provided
          if (assertion != null) {
            assertion.accept(inputDomain);
          }

          // Generate examples
          System.out.println("Generated examples:");
          List<?> examples = inputDomain.sample(5);
          System.out.println("  Examples: " + examples);
        }
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
      if (assertion != null) {
        fail("Test failed with exception: " + e.getMessage());
      }
    }

    System.out.println("=== Test completed ===\n");
  }

  @Test
  public void testSimpleSubstring() {
    testDomainInference(
        "Simple SUBSTRING Test",
        "SELECT * FROM test.T WHERE SUBSTRING(name, 1, 4) = '2000'",
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
    testDomainInference(
        "Simple LOWER Test",
        "SELECT * FROM test.T WHERE LOWER(name) = 'abc'",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(5);
          assertEquals(5, examples.size(), "Should generate 5 examples");
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertEquals("abc", s.toLowerCase(), "Should be case-insensitive 'abc': " + s);
          }
        });
  }

  @Test
  public void testSimpleCastIntToString() {
    testDomainInference(
        "Simple CAST INT to STRING Test",
        "SELECT * FROM test.T WHERE CAST(age AS STRING) = '25'",
        inputDomain -> {
          assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
          IntegerDomain intDomain = (IntegerDomain) inputDomain;
          assertTrue(intDomain.contains(25), "Should contain 25");
          assertTrue(intDomain.isSingleton(), "Should be singleton");
        });
  }

  @Test
  public void testNestedLowerSubstring() {
    testDomainInference(
        "Nested LOWER(SUBSTRING) Test",
        "SELECT * FROM test.T WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'",
        inputDomain -> {
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
    testDomainInference(
        "SUBSTRING(CAST(DATE)) Test",
        "SELECT * FROM test.T WHERE SUBSTRING(CAST(birthdate AS STRING), 1, 4) = '2000'",
        inputDomain -> {
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
    testDomainInference(
        "Substring of Cast Integer Test",
        "SELECT * FROM test.T WHERE SUBSTRING(CAST(age AS STRING), 1, 2) = '25'",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(5);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.startsWith("25"), "Should start with 25: " + s);
            assertTrue(s.matches("\\d+"), "Should be all digits: " + s);
          }
        });
  }

  @Test
  public void testArithmeticExpression() {
    testDomainInference(
        "Arithmetic Expression Test",
        "SELECT * FROM test.T WHERE age * 2 + 5 = 25",
        inputDomain -> {
          assertTrue(inputDomain instanceof IntegerDomain, "Should be IntegerDomain");
          IntegerDomain intDomain = (IntegerDomain) inputDomain;
          assertTrue(intDomain.contains(10), "Should contain 10 (since 10 * 2 + 5 = 25)");
          assertTrue(intDomain.isSingleton(), "Should be singleton");
        });
  }

  @Test
  public void testCastWithArithmetic() {
    // This test demonstrates cross-domain inference: String → Integer → through arithmetic
    testDomainInference(
        "CAST with Arithmetic Test",
        "SELECT * FROM test.T WHERE CAST(age * 2 AS STRING) = '50'",
        inputDomain -> {
          // The result should be IntegerDomain since age is an integer
          System.out.println("Domain type: " + inputDomain.getClass().getSimpleName());
          if (inputDomain instanceof IntegerDomain) {
            IntegerDomain intDomain = (IntegerDomain) inputDomain;
            System.out.println("Contains 25: " + intDomain.contains(25)); // Should be true (25 * 2 = 50)
            System.out.println("Contains 30: " + intDomain.contains(30)); // Should be false
            
            // Verify the constraint is correct
            assertTrue(intDomain.contains(25), "Domain should contain 25 (since 25 * 2 = 50)");
            assertFalse(intDomain.contains(30), "Domain should not contain 30");
          } else {
            fail("Expected IntegerDomain but got " + inputDomain.getClass().getSimpleName());
          }
        });
  }

  @Test
  public void testSimpleCastStringToDate() {
    testDomainInference(
        "Simple Cast String to Date Test",
        "SELECT * FROM test.T WHERE CAST(name AS DATE) = DATE '2024-01-15'",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          RegexDomain regex = (RegexDomain) inputDomain;
          assertTrue(regex.isLiteral(), "Should be literal");
          List<?> examples = inputDomain.sample(1);
          assertEquals(1, examples.size(), "Should have exactly 1 example");
        });
  }

  @Test
  public void testSimpleCastStringToInteger() {
    testDomainInference(
        "Simple Cast String to Integer Test",
        "SELECT * FROM test.T WHERE CAST(name AS INT) = 42",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(1);
          String s = examples.get(0).toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
          assertEquals("42", s, "Should be '42'");
        });
  }

  @Test
  public void testCastStringToIntegerWithArithmetic() {
    testDomainInference(
        "Cast String to Integer with Arithmetic Test",
        "SELECT * FROM test.T WHERE CAST(name AS INT) + 10 = 50",
        inputDomain -> {
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
    testDomainInference(
        "Nested Substring Overlapping Test",
        "SELECT * FROM test.T WHERE SUBSTRING(SUBSTRING(name, 1, 10), 3, 5) = 'hello'",
        inputDomain -> {
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
    testDomainInference(
        "Nested Substring Disjoint Test",
        "SELECT * FROM test.T WHERE SUBSTRING(SUBSTRING(name, 5, 15), 1, 3) = 'xyz'",
        inputDomain -> {
          assertTrue(inputDomain instanceof RegexDomain, "Should be RegexDomain");
          List<?> examples = inputDomain.sample(3);
          for (Object ex : examples) {
            String s = ex.toString().replaceAll("^\\^", "").replaceAll("\\$$", "");
            assertTrue(s.length() >= 7, "Should have at least 7 characters (4 prefix + 3 for xyz)");
            assertTrue(s.substring(4, 7).equals("xyz"), "Chars 5-7 should be 'xyz': " + s);
          }
        });
  }

  /**
   * Helper method to determine if a SQL type is numeric.
   */
  private boolean isNumericType(org.apache.calcite.sql.type.SqlTypeName typeName) {
    switch (typeName) {
      case TINYINT:
      case SMALLINT:
      case INTEGER:
      case BIGINT:
      case DECIMAL:
      case FLOAT:
      case REAL:
      case DOUBLE:
        return true;
      default:
        return false;
    }
  }
}
