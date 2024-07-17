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


public class RelNodeCostEstimatorTest {
  private HiveConf conf;

  private RelNodeCostEstimator estimator;

  static final String TEST_JSON_FILE_DIR = "src/test/resources/";

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
    stat.put("hive.test.bar1_prev", 40.0);
    stat.put("hive.test.bar2_prev", 10.0);
    stat.put("hive.test.bar1_delta", 60.0);
    stat.put("hive.test.bar2_delta", 10.0);
    return stat;
  }

  public String getIncrementalModification(String sql) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    return convertOptimalSql(originalRelNode);
  }

  @Test
  public void testSimpleSelectAll() {
    String sql = "SELECT * FROM test.bar1";
    String incrementalSql = "SELECT *\n" + "FROM test.bar1_delta AS bar1_delta";
    estimator.setIOCostParam(2.0);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    assertEquals(getIncrementalModification(sql), incrementalSql);
    estimator.setStat(fakeStatData());
    assertEquals(getIncrementalModification(sql), incrementalSql);
  }

  @Test
  public void testSimpleJoin() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    String prevSql = "SELECT *\n" + "FROM test.bar1 AS bar1\n" + "INNER JOIN test.bar2 AS bar2 ON bar1.x = bar2.x";
    String incrementalSql = "SELECT *\n" + "FROM (SELECT *\n" + "FROM test.bar1_prev AS bar1_prev\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1_prev.x = bar2_delta.x\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2_prev AS bar2_prev ON bar1_delta.x = bar2_prev.x) AS t\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x";
    estimator.setIOCostParam(2.0);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    assertEquals(getIncrementalModification(sql), incrementalSql);
    estimator.setStat(fakeStatData());
    assertEquals(getIncrementalModification(sql), prevSql);
  }
}
