/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rel2sql.SqlImplementor;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.spark.containers.SparkRelInfo;
import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;

import static org.testng.Assert.*;


public class CoralSqlNodeConversionTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_SPARK_TEST_DIR)));
  }

  @Test
  public void testLateralView() {
    RelNode coralRelNode = TestUtils.toRelNode(
        String.join("\n", "", "SELECT a, t.ccol", "FROM complex", "LATERAL VIEW explode(complex.c) t as ccol"));
    String targetSql =
        "SELECT complex.a, t0.ccol\n" + "FROM default.complex LATERAL VIEW EXPLODE(complex.c) t0 AS ccol";

    SqlNode coralSqlNode = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;
    SqlNode sparkNewSqlNode = getCoralSqlNodeToSparkSqlNodeTransformer().convert(coralSqlNode);
    SqlNode sparkOldSqlNode = createOriginalSparkSqlNode(coralRelNode);

    assertEquals(sparkNewSqlNode.toString(), sparkOldSqlNode.toString());
  }

  private CoralRelToSqlNodeConverter getCoralRelToSqlNodeTransformer() {
    return new CoralRelToSqlNodeConverter();
  }

  private CoralSqlNodeToSparkSqlNodeConverter getCoralSqlNodeToSparkSqlNodeTransformer() {
    return new CoralSqlNodeToSparkSqlNodeConverter();
  }

  private SqlNode createOriginalSparkSqlNode(RelNode irRelNode) {
    SparkRelInfo sparkRelInfo = IRRelToSparkRelTransformer.transform(irRelNode);
    RelNode sparkRelNode = sparkRelInfo.getSparkRelNode();

    SparkRelToSparkSqlConverter rel2sql = new SparkRelToSparkSqlConverter();
    // Create temporary objects r and rewritten to make debugging easier
    SqlImplementor.Result r = rel2sql.visitChild(0, sparkRelNode);
    return r.node;
  }

}
