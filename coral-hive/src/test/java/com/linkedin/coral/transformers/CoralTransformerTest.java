/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.transformers;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.ToRelConverterTestUtils;
import com.linkedin.coral.hive.hive2rel.TestUtils;

import static com.linkedin.coral.common.ToRelConverterTestUtils.*;
import static org.testng.Assert.*;


public class CoralTransformerTest {
  private static HiveConf conf;

  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    ToRelConverterTestUtils.setup(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_HIVE_TEST_DIR)));
  }

  @Test
  public void testLateralViewForArrayOfDouble() {
    final String sql = "SELECT complex.a, t0.ccol from default.complex lateral view explode(complex.c) t0 as ccol";
    SqlNode coralSqlNodeExpected = converter.toSqlNode(sql);

    RelNode coralRelNode = converter.convertSql(sql);
    SqlNode coralSqlNodeResult = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;

    assertEquals(coralSqlNodeResult.toString(), coralSqlNodeExpected.toString());
  }

  @Test
  public void testLateralViewForArrayOfStruct() {
    final String sql =
        "SELECT complex.a, t0.flat_s from default.complex lateral view explode(complex.sarr) t0 as flat_s";
    SqlNode coralSqlNodeExpected = converter.toSqlNode(sql);

    RelNode coralRelNode = converter.convertSql(sql);
    SqlNode coralSqlNodeResult = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;

    assertEquals(coralSqlNodeResult.toString(), coralSqlNodeExpected.toString());
  }

  @Test
  public void testLateralViewForMap() {
    final String sql =
        "SELECT complex.a, t0.mkey, t0.mvalue from default.complex lateral view explode(complex.m) t0 as mkey, mvalue";
    SqlNode coralSqlNodeExpected = converter.toSqlNode(sql);

    RelNode coralRelNode = converter.convertSql(sql);
    SqlNode coralSqlNodeResult = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;

    assertEquals(coralSqlNodeResult.toString(), coralSqlNodeExpected.toString());
  }

  @Test
  public void testLateralViewOuterForArrayOfDouble() {
    final String sql =
        "SELECT complex.a, t0.ccol from default.complex lateral view outer explode(complex.c) t0 as ccol";
    SqlNode coralSqlNodeExpected = converter.toSqlNode(sql);

    RelNode coralRelNode = converter.convertSql(sql);
    SqlNode coralSqlNodeResult = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;

    System.out.println(coralSqlNodeExpected);
    System.out.println(coralSqlNodeResult);

    assertEquals(coralSqlNodeResult.toString(), coralSqlNodeExpected.toString());
  }

  @Test
  public void testLateralViewPosExplodeForArrayOfDouble() {
    final String sql =
        "SELECT complex.a, t0.ccol from default.complex lateral view posexplode(complex.c) t0 as pos, ccol";
    SqlNode coralSqlNodeExpected = converter.toSqlNode(sql);

    RelNode coralRelNode = converter.convertSql(sql);
    SqlNode coralSqlNodeResult = getCoralRelToSqlNodeTransformer().visitChild(0, coralRelNode).node;

    System.out.println(coralSqlNodeExpected);
    System.out.println(coralSqlNodeResult);

    assertEquals(coralSqlNodeResult.toString(), coralSqlNodeExpected.toString());
  }

  private CoralRelToSqlNodeConverter getCoralRelToSqlNodeTransformer() {
    return new CoralRelToSqlNodeConverter();
  }
}
