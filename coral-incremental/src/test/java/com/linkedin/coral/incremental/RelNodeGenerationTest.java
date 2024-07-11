/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import static org.testng.Assert.*;


public class RelNodeGenerationTest {
  private HiveConf conf;

  private RelNodeCostEstimator estimator;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    estimator = new RelNodeCostEstimator();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_INCREMENTAL_TEST_DIR)));
  }

  public List<RelNode> generateIncrementalRelNodes(RelNode relNode) {
    RelNode incrementalRelNode = RelNodeGenerationTransformer.convertRelIncremental(relNode);
    List<RelNode> relNodes = new ArrayList<>();
    relNodes.add(relNode);
    relNodes.add(incrementalRelNode);
    return relNodes;
  }

  public String convertOptimalSql(RelNode relNode) {
    List<RelNode> relNodes = generateIncrementalRelNodes(relNode);
    List<Double> costs = getCosts(relNodes);
    int minIndex = 0;
    for (int i = 1; i < relNodes.size(); i++) {
      if (costs.get(i) < costs.get(minIndex)) {
        minIndex = i;
      }
    }
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    SqlNode sqlNode = converter.convert(relNodes.get(minIndex));
    return sqlNode.toSqlString(converter.INSTANCE).getSql();
  }

  public List<Double> getCosts(List<RelNode> relNodes) {
    List<Double> costs = new ArrayList<>();
    for (RelNode relNode : relNodes) {
      costs.add(estimator.getCost(relNode));
    }
    return costs;
  }

  public Map<String, Double> fakeStatData() {
    Map<String, Double> stat = new HashMap<>();
    stat.put("hive.test.bar1", 100.0);
    stat.put("hive.test.bar2", 20.0);
    stat.put("hive.test.bar1_prev", 70.0);
    stat.put("hive.test.bar2_prev", 15.0);
    stat.put("hive.test.bar1_delta", 30.0);
    stat.put("hive.test.bar2_delta", 5.0);
    return stat;
  }

  public Map<String, Double> fakeStatData2() {
    Map<String, Double> stat = new HashMap<>();
    stat.put("hive.test.bar1", 100.0);
    stat.put("hive.test.bar2", 20.0);
    stat.put("hive.test.bar1_prev", 40.0);
    stat.put("hive.test.bar2_prev", 10.0);
    stat.put("hive.test.bar1_delta", 60.0);
    stat.put("hive.test.bar2_delta", 10.0);
    return stat;
  }

  public Map<String, Double> fakeDistinctStatData() {
    Map<String, Double> distinctStat = new HashMap<>();
    distinctStat.put("hive.test.bar1:x", 10.0);
    distinctStat.put("hive.test.bar2:x", 5.0);
    distinctStat.put("hive.test.bar1_prev:x", 10.0);
    distinctStat.put("hive.test.bar2_prev:x", 5.0);
    distinctStat.put("hive.test.bar1_delta:x", 10.0);
    distinctStat.put("hive.test.bar2_delta:x", 5.0);
    return distinctStat;
  }

  public String getIncrementalModification(String sql) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    return convertOptimalSql(originalRelNode);
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.foo";
    estimator.setStat(fakeStatData());
    estimator.setDistinctStat(fakeDistinctStatData());
    // assertEquals(getIncrementalModification(sql), sql);
  }

  @Test
  public void testSimpleJoin() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String prevSql = "SELECT *\n" + "FROM test.bar1 AS bar1\n" + "INNER JOIN test.bar2 AS bar2 ON bar1.x = bar2.x";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x";
    estimator.setIOCostParam(2.0);
    estimator.setStat(fakeStatData());
    estimator.setDistinctStat(fakeDistinctStatData());
    assertEquals(getIncrementalModification(sql), expected);
    estimator.setStat(fakeStatData2());
    assertEquals(getIncrementalModification(sql), prevSql);
  }

  @Test
  public void testJoinCost() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testJoinWithFilter() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x WHERE test.bar1.x > 10";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t0\n" + "WHERE t0.x > 10";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testJoinWithNestedFilter() {
    String sql =
        "WITH tmp AS (SELECT * from test.bar1 WHERE test.bar1.x > 10), tmp2 AS (SELECT * from test.bar2) SELECT * FROM tmp JOIN tmp2 ON tmp.x = tmp2.x";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "WHERE bar1_prev.x > 10) AS t\n" + "INNER JOIN test.bar2_delta AS bar2_delta ON t.x = bar2_delta.x\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_delta AS bar1_delta\n"
        + "WHERE bar1_delta.x > 10) AS t0\n" + "INNER JOIN test.bar2_prev AS bar2_prev ON t0.x = bar2_prev.x) AS t1\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
        + "WHERE bar1_delta0.x > 10) AS t2\n" + "INNER JOIN test.bar2_delta AS bar2_delta0 ON t2.x = bar2_delta0.x";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testNestedJoin() {
    String sql =
        "WITH tmp AS (SELECT * FROM test.bar1 INNER JOIN test.bar2 ON test.bar1.x = test.bar2.x) SELECT * FROM tmp INNER JOIN test.bar3 ON tmp.x = test.bar3.x";
    String expected = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_prev.x = bar2_prev.x\n"
        + "INNER JOIN test.bar3_delta AS bar3_delta ON bar1_prev.x = bar3_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev0.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev0 ON bar1_delta.x = bar2_prev0.x) AS t\n" + "UNION ALL\n"
        + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t0\n"
        + "INNER JOIN test.bar3_prev AS bar3_prev ON t0.x = bar3_prev.x) AS t1\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta1 ON bar1_prev1.x = bar2_delta1.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta1\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev1 ON bar1_delta1.x = bar2_prev1.x) AS t2\n" + "UNION ALL\n"
        + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta2\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta2 ON bar1_delta2.x = bar2_delta2.x) AS t3\n"
        + "INNER JOIN test.bar3_delta AS bar3_delta0 ON t3.x = bar3_delta0.x";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testUnion() {
    String sql = "SELECT * FROM test.bar1 UNION SELECT * FROM test.bar2 UNION SELECT * FROM test.bar3";
    String expected =
        "SELECT t1.x, t1.y\n" + "FROM (SELECT t.x, t.y\n" + "FROM (SELECT *\n" + "FROM test.bar1_delta AS bar1_delta\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar2_delta AS bar2_delta) AS t\n" + "GROUP BY t.x, t.y\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar3_delta AS bar3_delta) AS t1\n" + "GROUP BY t1.x, t1.y";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testSelectSpecific() {
    String sql = "SELECT a FROM test.foo";
    String expected = "SELECT foo_delta.a\n" + "FROM test.foo_delta AS foo_delta";
    // assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testSelectSpecificJoin() {
    String sql = "SELECT test.bar2.y FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String expected =
        "SELECT t0.y0 AS y\n" + "FROM (SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
            + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
            + "FROM test.bar1_delta AS bar1_delta\n"
            + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n"
            + "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta0\n"
            + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t0";
    // assertEquals(getIncrementalModification(sql), expected);
  }
}
