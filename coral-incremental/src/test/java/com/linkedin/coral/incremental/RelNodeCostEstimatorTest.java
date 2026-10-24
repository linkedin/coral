/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.rel.RelNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.incremental.TestUtils.*;
import static org.testng.Assert.*;


public class RelNodeCostEstimatorTest {
  private HiveConf conf;

  private RelNodeCostEstimator estimator;

  static final String TEST_JSON_FILE_DIR = "src/test/resources/";

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    estimator = new RelNodeCostEstimator(2.0, 1.0);
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_INCREMENTAL_TEST_DIR)));
  }

  public Map<String, Double> fakeStatData() {
    Map<String, Double> stat = new HashMap<>();
    stat.put("hive.test.bar1", 80.0);
    stat.put("hive.test.bar2", 20.0);
    stat.put("hive.test.bar1_prev", 40.0);
    stat.put("hive.test.bar2_prev", 10.0);
    stat.put("hive.test.bar1_delta", 60.0);
    stat.put("hive.test.bar2_delta", 10.0);
    return stat;
  }

  @Test
  public void testSimpleSelectAll() throws IOException {
    String sql = "SELECT * FROM test.bar1";
    RelNode relNode = hiveToRelConverter.convertSql(sql);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    assertEquals(estimator.getCost(relNode), 300.0);
  }

  @Test
  public void testSimpleJoin() throws IOException {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x";
    RelNode relNode = hiveToRelConverter.convertSql(sql);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    assertEquals(estimator.getCost(relNode), 500.0);
  }

  @Test
  public void testSimpleUnion() throws IOException {
    String sql = "SELECT *\n" + "FROM test.bar1 AS bar1\n" + "INNER JOIN test.bar2 AS bar2 ON bar1.x = bar2.x\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM test.bar3 AS bar3\n" + "INNER JOIN test.bar2 AS bar2 ON bar3.x = bar2.x";
    RelNode relNode = hiveToRelConverter.convertSql(sql);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    assertEquals(estimator.getCost(relNode), 680.0);
  }

  @Test
  public void testUnsupportOperator() throws IOException {
    String sql = "SELECT * FROM test.bar1 WHERE x = 1";
    RelNode relNode = hiveToRelConverter.convertSql(sql);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    try {
      estimator.getCost(relNode);
      fail("Should throw exception");
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Unsupported relational operation: " + "LogicalFilter");
    }
  }

  @Test
  public void testNoStatistic() throws IOException {
    String sql = "SELECT * FROM test.foo";
    RelNode relNode = hiveToRelConverter.convertSql(sql);
    estimator.loadStatistic(TEST_JSON_FILE_DIR + "statistic.json");
    try {
      estimator.getCost(relNode);
      fail("Should throw exception");
    } catch (RuntimeException e) {
      assertEquals(e.getMessage(), "Table statistics not found for table: " + "hive.test.foo");
    }
  }
}
