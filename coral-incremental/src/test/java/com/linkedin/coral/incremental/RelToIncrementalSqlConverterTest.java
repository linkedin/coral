/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class RelToIncrementalSqlConverterTest {

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
    RelNodeIncrementalTransformer transformer = new RelNodeIncrementalTransformer();
    RelNode incrementalRelNode = transformer.convertRelIncremental(relNode);
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    SqlNode sqlNode = converter.convert(incrementalRelNode);
    return sqlNode.toSqlString(converter.INSTANCE).getSql();
  }

  public String getIncrementalModification(String sql) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    return convert(originalRelNode);
  }

  public void checkAllSnapshotAndDeltaQueries(String sql, Map<String, String> snapshotExpected,
      Map<String, String> deltaExpected) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    RelNodeIncrementalTransformer transformer = new RelNodeIncrementalTransformer();
    transformer.convertRelIncremental(originalRelNode);
    Map<String, RelNode> snapshotRelNodes = transformer.getSnapshotRelNodes();
    Map<String, RelNode> deltaRelNodes = transformer.getDeltaRelNodes();
    for (String key : snapshotRelNodes.keySet()) {
      RelNode actualSnapshotRelNode = snapshotRelNodes.get(key);
      SqlNode sqlNode = converter.convert(actualSnapshotRelNode);
      String actualSql = sqlNode.toSqlString(converter.INSTANCE).getSql();
      assertEquals(actualSql, snapshotExpected.get(key));
    }
    for (String key : deltaRelNodes.keySet()) {
      RelNode actualDeltaRelNode = deltaRelNodes.get(key);
      SqlNode sqlNode = converter.convert(actualDeltaRelNode);
      String actualSql = sqlNode.toSqlString(converter.INSTANCE).getSql();
      assertEquals(actualSql, deltaExpected.get(key));
    }
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.foo";
    String expected = "SELECT *\n" + "FROM test.foo_delta AS foo_delta";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testSimpleJoin() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n" + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testJoinWithFilter() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x WHERE test.bar1.x > 10";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n" + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t0\n" + "WHERE t0.x > 10";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testUnion() {
    String sql = "SELECT * FROM test.bar1 UNION SELECT * FROM test.bar2 UNION SELECT * FROM test.bar3";
    String expected =
        "SELECT t1.x, t1.y\n" + "FROM (SELECT t.x, t.y\n" + "FROM (SELECT *\n" + "FROM test.bar1_delta AS bar1_delta\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar2_delta AS bar2_delta) AS t\n" + "GROUP BY t.x, t.y\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar3_delta AS bar3_delta) AS t1\n" + "GROUP BY t1.x, t1.y";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testSelectSpecific() {
    String sql = "SELECT a FROM test.foo";
    String expected = "SELECT foo_delta.a\n" + "FROM test.foo_delta AS foo_delta";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testSelectSpecificJoin() {
    String sql = "SELECT test.bar2.y FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String expected = "SELECT t0.y0 AS y\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n" + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t0";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testNestedJoin() {
    String nestedJoin = "SELECT a1, a2 FROM test.alpha JOIN test.beta ON test.alpha.a1 = test.beta.b1";
    String sql = "SELECT a2, g1 FROM (" + nestedJoin + ") AS nj JOIN test.gamma ON nj.a2 = test.gamma.g2";
    Map<String, String> snapshotExpected = new LinkedHashMap<>();
    snapshotExpected.put("Table#0",
        "SELECT *\n" + "FROM test.alpha AS alpha\n" + "INNER JOIN test.beta AS beta ON alpha.a1 = beta.b1");
    snapshotExpected.put("Table#1",
        "SELECT *\n" + "FROM Table#0 AS Table#0\n" + "INNER JOIN test.gamma AS gamma ON Table#0.a2 = gamma.g2");
    Map<String, String> deltaExpected = new LinkedHashMap<>();
    deltaExpected.put("Table#0_delta",
        "SELECT t0.a1, t0.a2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.alpha AS alpha0\n"
            + "INNER JOIN test.beta_delta AS beta_delta ON alpha0.a1 = beta_delta.b1\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM test.alpha_delta AS alpha_delta\n"
            + "INNER JOIN test.beta AS beta0 ON alpha_delta.a1 = beta0.b1) AS t\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM test.alpha_delta AS alpha_delta0\n"
            + "INNER JOIN test.beta_delta AS beta_delta0 ON alpha_delta0.a1 = beta_delta0.b1) AS t0");
    deltaExpected.put("Table#1_delta",
        "SELECT t3.a2, t3.g1\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#0 AS Table#00\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta ON Table#00.a2 = gamma_delta.g2\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta\n"
            + "INNER JOIN test.gamma AS gamma0 ON Table#0_delta.a2 = gamma0.g2) AS t2\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM Table#0_delta AS Table#0_delta0\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta0 ON Table#0_delta0.a2 = gamma_delta0.g2) AS t3");
    checkAllSnapshotAndDeltaQueries(sql, snapshotExpected, deltaExpected);
  }

  @Test
  public void testThreeNestedJoins() {
    String nestedJoin1 = "SELECT a1, a2 FROM test.alpha JOIN test.beta ON test.alpha.a1 = test.beta.b1";
    String nestedJoin2 = "SELECT a2, g1 FROM (" + nestedJoin1 + ") AS nj1 JOIN test.gamma ON nj1.a2 = test.gamma.g2";
    String sql = "SELECT g1, e2 FROM (" + nestedJoin2 + ") AS nj2 JOIN test.epsilon ON nj2.g1 = test.epsilon.e1";
    Map<String, String> snapshotExpected = new LinkedHashMap<>();
    snapshotExpected.put("Table#0",
        "SELECT *\n" + "FROM test.alpha AS alpha\n" + "INNER JOIN test.beta AS beta ON alpha.a1 = beta.b1");
    snapshotExpected.put("Table#1",
        "SELECT *\n" + "FROM Table#0 AS Table#0\n" + "INNER JOIN test.gamma AS gamma ON Table#0.a2 = gamma.g2");
    snapshotExpected.put("Table#2",
        "SELECT *\n" + "FROM Table#1 AS Table#1\n" + "INNER JOIN test.epsilon AS epsilon ON Table#1.g1 = epsilon.e1");
    Map<String, String> deltaExpected = new LinkedHashMap<>();
    deltaExpected.put("Table#0_delta",
        "SELECT t0.a1, t0.a2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.alpha AS alpha0\n"
            + "INNER JOIN test.beta_delta AS beta_delta ON alpha0.a1 = beta_delta.b1\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM test.alpha_delta AS alpha_delta\n"
            + "INNER JOIN test.beta AS beta0 ON alpha_delta.a1 = beta0.b1) AS t\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM test.alpha_delta AS alpha_delta0\n"
            + "INNER JOIN test.beta_delta AS beta_delta0 ON alpha_delta0.a1 = beta_delta0.b1) AS t0");
    deltaExpected.put("Table#1_delta",
        "SELECT t3.a2, t3.g1\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#0 AS Table#00\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta ON Table#00.a2 = gamma_delta.g2\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#0_delta AS Table#0_delta\n"
            + "INNER JOIN test.gamma AS gamma0 ON Table#0_delta.a2 = gamma0.g2) AS t2\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM Table#0_delta AS Table#0_delta0\n"
            + "INNER JOIN test.gamma_delta AS gamma_delta0 ON Table#0_delta0.a2 = gamma_delta0.g2) AS t3");
    deltaExpected.put("Table#2_delta",
        "SELECT t6.g1, t6.e2\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM Table#1 AS Table#10\n"
            + "INNER JOIN test.epsilon_delta AS epsilon_delta ON Table#10.g1 = epsilon_delta.e1\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#1_delta AS Table#1_delta\n"
            + "INNER JOIN test.epsilon AS epsilon0 ON Table#1_delta.g1 = epsilon0.e1) AS t5\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM Table#1_delta AS Table#1_delta0\n"
            + "INNER JOIN test.epsilon_delta AS epsilon_delta0 ON Table#1_delta0.g1 = epsilon_delta0.e1) AS t6");
    checkAllSnapshotAndDeltaQueries(sql, snapshotExpected, deltaExpected);
  }
}
