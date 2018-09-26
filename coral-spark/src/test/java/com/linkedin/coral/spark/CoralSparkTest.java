package com.linkedin.coral.spark;

import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.util.List;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class CoralSparkTest {

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException {
    TestUtils.initializeViews();
  }

  @Test
  public void testGetBaseTablesFromView(){
    RelNode relNode = TestUtils.toRelNode("default","foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<String> base_tables = coralSpark.getBaseTables();
    assertTrue(base_tables.contains("default.foo"));
    assertTrue(base_tables.contains("default.bar"));
  }

  @Test
  public void testGetSQLFromView(){
    String targetSql = "SELECT t0.bcol, bar.x\n" + "FROM (SELECT b bcol, SUM(c) sum_c\n" + "FROM default.foo\n"
        + "GROUP BY b) t0\n" + "INNER JOIN default.bar ON t0.sum_c = bar.y";
    RelNode relNode = TestUtils.toRelNode("default","foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSQL();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testDaliUdf() {
    RelNode relNode = TestUtils.toRelNode("default","foo_dali_udf");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(1, udfJars.size());
  }

  @Test
  public void testNoUdf() {
    RelNode relNode = TestUtils.toRelNode("default","foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertTrue(udfJars.isEmpty());
  }

}
