/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;

import static com.linkedin.coral.incremental.TestUtils.*;
import static org.testng.Assert.*;


public class RelNodeGenerationTest {
  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_INCREMENTAL_TEST_DIR)));
  }

  public String convert(RelNode relNode) {
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    SqlNode sqlNode = converter.convert(relNode);
    return sqlNode.toSqlString(converter.INSTANCE).getSql();
  }

  public void checkAllPlans(String sql, List<List<String>> expected) {
    List<List<RelNode>> plans = getAllPlans(sql);
    assertEquals(plans.size(), expected.size());
    for (int i = 0; i < plans.size(); i++) {
      List<RelNode> plan = plans.get(i);
      List<String> expectedPlan = expected.get(i);
      assertEquals(plan.size(), expectedPlan.size());
      for (int j = 0; j < plan.size(); j++) {
        String actual = convert(plan.get(j));
        assertEquals(actual, expectedPlan.get(j));
      }
    }
  }

  public List<List<RelNode>> getAllPlans(String sql) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    RelNodeGenerationTransformer transformer = new RelNodeGenerationTransformer();
    return transformer.generateIncrementalRelNodes(originalRelNode);
  }

  @Test
  public void testSimpleJoinPrev() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    RelNodeGenerationTransformer transformer = new RelNodeGenerationTransformer();
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    RelNode prev = transformer.convertRelPrev(originalRelNode);
    String prevSql = convert(prev);
    String expected = "SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_prev.x = bar2_prev.x";
    assertEquals(prevSql, expected);
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.foo";
    List<String> incremental = Arrays.asList("SELECT *\n" + "FROM test.foo_delta AS foo_delta");
    List<String> batch = Arrays.asList("SELECT *\n" + "FROM test.foo AS foo");
    List<List<String>> expected = Arrays.asList(incremental, batch);
    checkAllPlans(sql, expected);
  }

  @Test
  public void testSimpleJoin() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String incrementalSql = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x";
    List<String> incremental = Arrays.asList(incrementalSql);
    List<String> batch =
        Arrays.asList("SELECT *\n" + "FROM test.bar1 AS bar1\n" + "INNER JOIN test.bar2 AS bar2 ON bar1.x = bar2.x");
    List<List<String>> expected = Arrays.asList(incremental, batch);
    checkAllPlans(sql, expected);
  }

  @Test
  public void testNestedJoin() {
    String nestedJoin = "SELECT a1, a2 FROM test.alpha JOIN test.beta ON test.alpha.a1 = test.beta.b1";
    String sql = "SELECT a2, g1 FROM (" + nestedJoin + ") AS nj JOIN test.gamma ON nj.a2 = test.gamma.g2";
    String Table0_delta =
        "SELECT t0.a1, t0.a2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.alpha_prev AS alpha_prev\n"
            + "INNER JOIN test.beta_delta AS beta_delta ON alpha_prev.a1 = beta_delta.b1\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM test.alpha_delta AS alpha_delta\n"
            + "INNER JOIN test.beta_prev AS beta_prev ON alpha_delta.a1 = beta_prev.b1) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM test.alpha_delta AS alpha_delta0\n"
            + "INNER JOIN test.beta_delta AS beta_delta0 ON alpha_delta0.a1 = beta_delta0.b1) AS t0";
    String Table1_delta =
        "SELECT t0.a2, t0.g1\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#0_prev AS Table#0_prev\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta ON Table#0_prev.a2 = gamma_delta.g2\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta\n"
            + "INNER JOIN test.gamma_prev AS gamma_prev ON Table#0_delta.a2 = gamma_prev.g2) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta0\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta0 ON Table#0_delta0.a2 = gamma_delta0.g2) AS t0";
    List<String> combined = Arrays.asList(Table0_delta,
        "SELECT *\n" + "FROM Table#0 AS Table#0\n" + "INNER JOIN test.gamma AS gamma ON Table#0.a2 = gamma.g2");
    List<String> incremental = Arrays.asList(Table0_delta, Table1_delta);
    List<String> batch = Arrays.asList("SELECT t.a2, gamma.g1\n" + "FROM (SELECT alpha.a1, alpha.a2\n"
        + "FROM test.alpha AS alpha\n" + "INNER JOIN test.beta AS beta ON alpha.a1 = beta.b1) AS t\n"
        + "INNER JOIN test.gamma AS gamma ON t.a2 = gamma.g2");
    List<List<String>> expected = Arrays.asList(combined, incremental, batch);
    checkAllPlans(sql, expected);
  }

  @Test
  public void testThreeTablesJoin() {
    String sql =
        "SELECT a1, a2, g1 FROM test.alpha JOIN test.beta ON test.alpha.a1 = test.beta.b1 JOIN test.gamma ON test.alpha.a2 = test.gamma.g2";
    String Table0_delta = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.alpha_prev AS alpha_prev\n"
        + "INNER JOIN test.beta_delta AS beta_delta ON alpha_prev.a1 = beta_delta.b1\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.alpha_delta AS alpha_delta\n"
        + "INNER JOIN test.beta_prev AS beta_prev ON alpha_delta.a1 = beta_prev.b1) AS t\n" + "UNION ALL\n"
        + "SELECT *\n" + "FROM test.alpha_delta AS alpha_delta0\n"
        + "INNER JOIN test.beta_delta AS beta_delta0 ON alpha_delta0.a1 = beta_delta0.b1";
    String Table1_delta =
        "SELECT t0.a1, t0.a2, t0.g1\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#0_prev AS Table#0_prev\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta ON Table#0_prev.a2 = gamma_delta.g2\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta\n"
            + "INNER JOIN test.gamma_prev AS gamma_prev ON Table#0_delta.a2 = gamma_prev.g2) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta0\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta0 ON Table#0_delta0.a2 = gamma_delta0.g2) AS t0";
    List<String> combined = Arrays.asList(Table0_delta,
        "SELECT *\n" + "FROM Table#0 AS Table#0\n" + "INNER JOIN test.gamma AS gamma ON Table#0.a2 = gamma.g2");
    List<String> incremental = Arrays.asList(Table0_delta, Table1_delta);
    List<String> batch = Arrays.asList("SELECT alpha.a1, alpha.a2, gamma.g1\n" + "FROM test.alpha AS alpha\n"
        + "INNER JOIN test.beta AS beta ON alpha.a1 = beta.b1\n"
        + "INNER JOIN test.gamma AS gamma ON alpha.a2 = gamma.g2");
    List<List<String>> expected = Arrays.asList(combined, incremental, batch);
    checkAllPlans(sql, expected);
  }

  @Test
  public void testFourTablesJoin() {
    String nestedJoin1 = "SELECT a1, a2 FROM test.alpha JOIN test.beta ON test.alpha.a1 = test.beta.b1";
    String nestedJoin2 = "SELECT a2, g1 FROM (" + nestedJoin1 + ") AS nj1 JOIN test.gamma ON nj1.a2 = test.gamma.g2";
    String sql = "SELECT g1, e2 FROM (" + nestedJoin2 + ") AS nj2 JOIN test.epsilon ON nj2.g1 = test.epsilon.e1";
    String Table0_delta =
        "SELECT t0.a1, t0.a2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.alpha_prev AS alpha_prev\n"
            + "INNER JOIN test.beta_delta AS beta_delta ON alpha_prev.a1 = beta_delta.b1\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM test.alpha_delta AS alpha_delta\n"
            + "INNER JOIN test.beta_prev AS beta_prev ON alpha_delta.a1 = beta_prev.b1) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM test.alpha_delta AS alpha_delta0\n"
            + "INNER JOIN test.beta_delta AS beta_delta0 ON alpha_delta0.a1 = beta_delta0.b1) AS t0";
    String Table1_delta =
        "SELECT t0.a2, t0.g1\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#0_prev AS Table#0_prev\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta ON Table#0_prev.a2 = gamma_delta.g2\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta\n"
            + "INNER JOIN test.gamma_prev AS gamma_prev ON Table#0_delta.a2 = gamma_prev.g2) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta0\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta0 ON Table#0_delta0.a2 = gamma_delta0.g2) AS t0";
    String Table2_delta =
        "SELECT t0.g1, t0.e2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#1_prev AS Table#1_prev\n"
            + "INNER JOIN test.epsilon_delta AS epsilon_delta ON Table#1_prev.g1 = epsilon_delta.e1\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#1_delta AS Table#1_delta\n"
            + "INNER JOIN test.epsilon_prev AS epsilon_prev ON Table#1_delta.g1 = epsilon_prev.e1) AS t\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM Table#1_delta AS Table#1_delta0\n"
            + "INNER JOIN test.epsilon_delta AS epsilon_delta0 ON Table#1_delta0.g1 = epsilon_delta0.e1) AS t0";
    List<String> combined1 = Arrays.asList(Table0_delta,
        "SELECT *\n" + "FROM Table#0 AS Table#0\n" + "INNER JOIN test.gamma AS gamma ON Table#0.a2 = gamma.g2",
        "SELECT *\n" + "FROM Table#1 AS Table#1\n" + "INNER JOIN test.epsilon AS epsilon ON Table#1.g1 = epsilon.e1");
    List<String> combined2 = Arrays.asList(Table0_delta, Table1_delta,
        "SELECT *\n" + "FROM Table#1 AS Table#1\n" + "INNER JOIN test.epsilon AS epsilon ON Table#1.g1 = epsilon.e1");
    List<String> incremental = Arrays.asList(Table0_delta, Table1_delta, Table2_delta);
    List<String> batch = Arrays
        .asList("SELECT t0.g1, epsilon.e2\n" + "FROM (SELECT t.a2, gamma.g1\n" + "FROM (SELECT alpha.a1, alpha.a2\n"
            + "FROM test.alpha AS alpha\n" + "INNER JOIN test.beta AS beta ON alpha.a1 = beta.b1) AS t\n"
            + "INNER JOIN test.gamma AS gamma ON t.a2 = gamma.g2) AS t0\n"
            + "INNER JOIN test.epsilon AS epsilon ON t0.g1 = epsilon.e1");
    List<List<String>> expected = Arrays.asList(combined1, combined2, incremental, batch);
    checkAllPlans(sql, expected);
  }
}
