/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

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

import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;

import static com.linkedin.coral.incremental.TestUtils.*;
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
    RelNode incrementalRelNode = RelNodeIncrementalTransformer.convertRelIncremental(relNode);
    CoralRelToSqlNodeConverter converter = new CoralRelToSqlNodeConverter();
    SqlNode sqlNode = converter.convert(incrementalRelNode);
    return sqlNode.toSqlString(converter.INSTANCE).getSql();
  }

  public String getIncrementalModification(String sql) {
    RelNode originalRelNode = hiveToRelConverter.convertSql(sql);
    return convert(originalRelNode);
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
    String expected = "SELECT t3.x, t3.y, t3.x2 AS x0, t3.y3 AS y0\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar1.x, bar1.y, bar2_delta.x AS x2, bar2_delta.y AS y3\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n"
        + "SELECT bar1_delta.x, bar1_delta.y, bar2.x AS x2, bar2.y AS y3\n" + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t1\n" + "UNION ALL\n"
        + "SELECT bar1_delta0.x, bar1_delta0.y, bar2_delta0.x AS x2, bar2_delta0.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t3";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testJoinWithFilter() {
    String sql = "SELECT * FROM test.bar1 JOIN test.bar2 ON test.bar1.x = test.bar2.x WHERE test.bar1.x > 10";
    String expected = "SELECT t3.x, t3.y, t3.x2 AS x0, t3.y3 AS y0\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar1.x, bar1.y, bar2_delta.x AS x2, bar2_delta.y AS y3\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n"
        + "SELECT bar1_delta.x, bar1_delta.y, bar2.x AS x2, bar2.y AS y3\n" + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t1\n" + "UNION ALL\n"
        + "SELECT bar1_delta0.x, bar1_delta0.y, bar2_delta0.x AS x2, bar2_delta0.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t3\n" + "WHERE t3.x > 10";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testJoinWithNestedFilter() {
    String sql =
        "WITH tmp AS (SELECT * from test.bar1 WHERE test.bar1.x > 10), tmp2 AS (SELECT * from test.bar2) SELECT * FROM tmp JOIN tmp2 ON tmp.x = tmp2.x";
    String expected = "SELECT t6.x, t6.y, t6.x2 AS x0, t6.y3 AS y0\n" + "FROM (SELECT *\n"
        + "FROM (SELECT t.x, t.y, bar2_delta.x AS x2, bar2_delta.y AS y3\n" + "FROM (SELECT *\n"
        + "FROM test.bar1 AS bar1\n" + "WHERE bar1.x > 10) AS t\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON t.x = bar2_delta.x\n" + "UNION ALL\n"
        + "SELECT t1.x, t1.y, bar2.x AS x2, bar2.y AS y3\n" + "FROM (SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta\n" + "WHERE bar1_delta.x > 10) AS t1\n"
        + "INNER JOIN test.bar2 AS bar2 ON t1.x = bar2.x) AS t3\n" + "UNION ALL\n"
        + "SELECT t4.x, t4.y, bar2_delta0.x AS x2, bar2_delta0.y AS y3\n" + "FROM (SELECT *\n"
        + "FROM test.bar1_delta AS bar1_delta0\n" + "WHERE bar1_delta0.x > 10) AS t4\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON t4.x = bar2_delta0.x) AS t6";
    assertEquals(getIncrementalModification(sql), expected);
  }

  @Test
  public void testNestedJoin() {
    String sql =
        "WITH tmp AS (SELECT * FROM test.bar1 INNER JOIN test.bar2 ON test.bar1.x = test.bar2.x) SELECT * FROM tmp INNER JOIN test.bar3 ON tmp.x = test.bar3.x";
    String expected = "SELECT t15.x, t15.y, t15.x0, t15.y0, t15.x4 AS x1, t15.y5 AS y1\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar1.x, bar1.y, bar2.x AS x0, bar2.y AS y0, bar3_delta.x AS x4, bar3_delta.y AS y5\n"
        + "FROM test.bar1 AS bar1\n" + "INNER JOIN test.bar2 AS bar2 ON bar1.x = bar2.x\n"
        + "INNER JOIN test.bar3_delta AS bar3_delta ON bar1.x = bar3_delta.x\n" + "UNION ALL\n"
        + "SELECT t5.x, t5.y, t5.x0, t5.y0, bar3.x AS x4, bar3.y AS y5\n"
        + "FROM (SELECT t4.x, t4.y, t4.x2 AS x0, t4.y3 AS y0\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar10.x, bar10.y, bar2_delta.x AS x2, bar2_delta.y AS y3\n" + "FROM test.bar1 AS bar10\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar10.x = bar2_delta.x\n" + "UNION ALL\n"
        + "SELECT bar1_delta.x, bar1_delta.y, bar20.x AS x2, bar20.y AS y3\n" + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2 AS bar20 ON bar1_delta.x = bar20.x) AS t2\n" + "UNION ALL\n"
        + "SELECT bar1_delta0.x, bar1_delta0.y, bar2_delta0.x AS x2, bar2_delta0.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t4) AS t5\n"
        + "INNER JOIN test.bar3 AS bar3 ON t5.x = bar3.x) AS t7\n" + "UNION ALL\n"
        + "SELECT t13.x, t13.y, t13.x0, t13.y0, bar3_delta0.x AS x4, bar3_delta0.y AS y5\n"
        + "FROM (SELECT t12.x, t12.y, t12.x2 AS x0, t12.y3 AS y0\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar11.x, bar11.y, bar2_delta1.x AS x2, bar2_delta1.y AS y3\n" + "FROM test.bar1 AS bar11\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta1 ON bar11.x = bar2_delta1.x\n" + "UNION ALL\n"
        + "SELECT bar1_delta1.x, bar1_delta1.y, bar21.x AS x2, bar21.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta1\n" + "INNER JOIN test.bar2 AS bar21 ON bar1_delta1.x = bar21.x) AS t10\n"
        + "UNION ALL\n" + "SELECT bar1_delta2.x, bar1_delta2.y, bar2_delta2.x AS x2, bar2_delta2.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta2\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta2 ON bar1_delta2.x = bar2_delta2.x) AS t12) AS t13\n"
        + "INNER JOIN test.bar3_delta AS bar3_delta0 ON t13.x = bar3_delta0.x) AS t15";
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
    String expected = "SELECT t3.y3 AS y\n" + "FROM (SELECT *\n"
        + "FROM (SELECT bar1.x, bar1.y, bar2_delta.x AS x2, bar2_delta.y AS y3\n" + "FROM test.bar1 AS bar1\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta ON bar1.x = bar2_delta.x\n" + "UNION ALL\n"
        + "SELECT bar1_delta.x, bar1_delta.y, bar2.x AS x2, bar2.y AS y3\n" + "FROM test.bar1_delta AS bar1_delta\n"
        + "INNER JOIN test.bar2 AS bar2 ON bar1_delta.x = bar2.x) AS t1\n" + "UNION ALL\n"
        + "SELECT bar1_delta0.x, bar1_delta0.y, bar2_delta0.x AS x2, bar2_delta0.y AS y3\n"
        + "FROM test.bar1_delta AS bar1_delta0\n"
        + "INNER JOIN test.bar2_delta AS bar2_delta0 ON bar1_delta0.x = bar2_delta0.x) AS t3";
    assertEquals(getIncrementalModification(sql), expected);
  }

}
