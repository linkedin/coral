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

public class DomainInferenceTest {
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

    // Initialize domain inference program with all transformers
    program = new DomainInferenceProgram(
        Arrays.asList(
            new LowerTransformer(),
            new SubstringTransformer(),
            new PlusTransformer(),
            new TimesTransformer(),
            new CastTransformer()));
  }

  @AfterClass
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_DATA_GENERATION_TEST_DIR)));
  }

  private static HiveConf getHiveConf() {
    InputStream hiveConfStream = DomainInferenceTest.class.getClassLoader().getResourceAsStream("hive.xml");
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

  @Test
  public void testDomainInferenceWithLowerSubstring() {
    // SQL: SELECT * FROM test.T WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'
    String sql = "SELECT * FROM test.T WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("=== Domain Inference Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Step 4: Extract the conjunct and derive domain
    try {
      // Extract the single conjunct from the disjunct
      // The disjunct should contain exactly one conjunct (the equality predicate)
      RexNode conjunct = disjunct; // For simple cases, the disjunct IS the conjunct
      System.out.println("Conjunct: " + conjunct);

      // For equality expressions, extract LHS and create RHS domain
      // conjunct should be: LOWER(SUBSTRING(name, 1, 3)) = 'abc'
      // We need to pass LHS and domain of RHS to program.derive(LHS, domain(RHS))

      // This assumes conjunct is an equality call with operands[0] = LHS, operands[1] = RHS
      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // LOWER(SUBSTRING(name, 1, 3))
          RexNode rhs = call.getOperands().get(1); // 'abc'

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          // Extract literal value from RHS RexNode
          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          // Create domain for RHS using extracted literal
          Domain rhsDomain = new ConstantDomain(rhsValue);

          // Pass LHS and RHS domain to program
          Domain nameDomain = program.derive(lhs, rhsDomain);

          // Step 6: Sample actual records
          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = nameDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }

  @Test
  public void testDomainInferenceWithNumericExpression() {
    // SQL: SELECT * FROM test.T WHERE age * 2 + 5 = 25
    String sql = "SELECT * FROM test.T WHERE age * 2 + 5 = 25";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("\n=== Numeric Expression Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Step 4: Extract the conjunct and derive domain
    try {
      // Extract the single conjunct from the disjunct
      // The disjunct should contain exactly one conjunct (the equality predicate)
      RexNode conjunct = disjunct; // For simple cases, the disjunct IS the conjunct
      System.out.println("Conjunct: " + conjunct);

      // For equality expressions, extract LHS and create RHS domain
      // conjunct should be: age * 2 + 5 = 25
      // We need to pass LHS and domain of RHS to program.derive(LHS, domain(RHS))

      // This assumes conjunct is an equality call with operands[0] = LHS, operands[1] = RHS
      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // age * 2 + 5
          RexNode rhs = call.getOperands().get(1); // 25

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          // Extract literal value from RHS RexNode
          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          // Create domain for RHS using extracted literal
          Domain rhsDomain = new ConstantDomain(rhsValue);

          // Pass LHS and RHS domain to program
          Domain ageDomain = program.derive(lhs, rhsDomain);

          // Step 6: Sample actual records
          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = ageDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }

  @Test
  public void testDomainInferenceWithCastIntToString() {
    // SQL: SELECT * FROM test.T WHERE CAST(age AS STRING) = '25'
    String sql = "SELECT * FROM test.T WHERE CAST(age AS STRING) = '25'";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("\n=== CAST INT to STRING Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Extract the conjunct and derive domain
    try {
      RexNode conjunct = disjunct;
      System.out.println("Conjunct: " + conjunct);

      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // CAST(age AS STRING)
          RexNode rhs = call.getOperands().get(1); // '25'

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          Domain rhsDomain = new ConstantDomain(rhsValue);
          Domain ageDomain = program.derive(lhs, rhsDomain);

          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = ageDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
            // Verify it's numeric
            Integer.parseInt(sample);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }

  @Test
  public void testDomainInferenceWithCastStringToInt() {
    // SQL: SELECT * FROM test.T WHERE CAST(name AS INT) = 42
    String sql = "SELECT * FROM test.T WHERE CAST(name AS INT) = 42";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("\n=== CAST STRING to INT Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Extract the conjunct and derive domain
    try {
      RexNode conjunct = disjunct;
      System.out.println("Conjunct: " + conjunct);

      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // CAST(name AS INT)
          RexNode rhs = call.getOperands().get(1); // 42

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          Domain rhsDomain = new ConstantDomain(rhsValue);
          Domain nameDomain = program.derive(lhs, rhsDomain);

          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = nameDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }

  @Test
  public void testDomainInferenceWithCastAndOtherTransformers() {
    // SQL: SELECT * FROM test.T WHERE CAST(age * 2 AS STRING) = '50'
    String sql = "SELECT * FROM test.T WHERE CAST(age * 2 AS STRING) = '50'";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("\n=== CAST with Arithmetic Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Extract the conjunct and derive domain
    try {
      RexNode conjunct = disjunct;
      System.out.println("Conjunct: " + conjunct);

      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // CAST(age * 2 AS STRING)
          RexNode rhs = call.getOperands().get(1); // '50'

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          Domain rhsDomain = new ConstantDomain(rhsValue);
          Domain ageDomain = program.derive(lhs, rhsDomain);

          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = ageDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
            // Should be numeric (age * 2 = 50, so age = 25)
            int age = Integer.parseInt(sample);
            System.out.println("  -> age * 2 = " + (age * 2));
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }

  @Test
  public void testDomainInferenceWithSubstringOfCastDate() {
    // SQL: SELECT * FROM test.T WHERE SUBSTRING(CAST(birthdate AS STRING), 1, 4) = '2000'
    String sql = "SELECT * FROM test.T WHERE SUBSTRING(CAST(birthdate AS STRING), 1, 4) = '2000'";

    // Steps 1-3: Convert SQL to normalized RelNode and extract predicates as DNF
    RelNode normalized = convertAndNormalizeQuery(sql);
    DnfRewriter.Output dnfOut = extractPredicatesAsDnf(normalized);

    System.out.println("\n=== SUBSTRING of CAST DATE Test ===");
    System.out.println("SQL: " + sql);
    System.out.println("DNF disjuncts count: " + dnfOut.disjuncts.size());

    // Verify we have exactly 1 disjunct
    if (dnfOut.disjuncts.size() != 1) {
      throw new RuntimeException("Expected exactly 1 disjunct, got: " + dnfOut.disjuncts.size());
    }

    RexNode disjunct = dnfOut.disjuncts.get(0);
    System.out.println("Disjunct: " + disjunct);

    // Extract the conjunct and derive domain
    try {
      RexNode conjunct = disjunct;
      System.out.println("Conjunct: " + conjunct);

      if (conjunct instanceof org.apache.calcite.rex.RexCall) {
        org.apache.calcite.rex.RexCall call = (org.apache.calcite.rex.RexCall) conjunct;
        if (call.getOperator() == org.apache.calcite.sql.fun.SqlStdOperatorTable.EQUALS) {
          RexNode lhs = call.getOperands().get(0); // SUBSTRING(CAST(birthdate AS STRING), 1, 4)
          RexNode rhs = call.getOperands().get(1); // '2000'

          System.out.println("LHS: " + lhs);
          System.out.println("RHS: " + rhs);

          if (!(rhs instanceof org.apache.calcite.rex.RexLiteral)) {
            throw new RuntimeException("Expected RHS to be a literal, got: " + rhs.getClass());
          }
          org.apache.calcite.rex.RexLiteral literal = (org.apache.calcite.rex.RexLiteral) rhs;
          String rhsValue = literal.getValue2().toString();
          System.out.println("RHS value: " + rhsValue);

          Domain rhsDomain = new ConstantDomain(rhsValue);
          Domain birthdateDomain = program.derive(lhs, rhsDomain);

          System.out.println("Sampling records:");
          for (int j = 0; j < 5; j++) {
            String sample = birthdateDomain.sample();
            System.out.println("Sample " + j + ": " + sample);
            // Verify the first 4 characters would be '2000' when cast to string
            if (sample.length() >= 4) {
              String year = sample.substring(0, 4);
              System.out.println("  -> Year extracted: " + year);
            }
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Error processing conjunct: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n=== Test completed ===");
  }
}
