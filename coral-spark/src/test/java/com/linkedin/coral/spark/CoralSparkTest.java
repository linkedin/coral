package com.linkedin.coral.spark;

import com.linkedin.coral.hive.hive2rel.parsetree.UnhandledASTTokenException;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import java.util.List;
import org.apache.calcite.plan.RelOptUtil;
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
    String targetSql = String.join("\n",
        "SELECT t0.bcol, bar.x",
        "FROM (SELECT b bcol, SUM(c) sum_c",
        "FROM default.foo",
        "GROUP BY b) t0",
        "INNER JOIN default.bar ON t0.sum_c = bar.y");
    RelNode relNode = TestUtils.toRelNode("default","foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testAllowBaseTableInView(){
    RelNode relNode = TestUtils.toRelNode("default","foo");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<String> base_tables = coralSpark.getBaseTables();
    assertTrue(base_tables.contains("default.foo"));
  }

  @Test
  public void testDaliUdf() {
    RelNode relNode = TestUtils.toRelNode("default","foo_dali_udf");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(1, udfJars.size());
  }

  @Test
  public void testDaliUdf2() {
    RelNode relNode = TestUtils.toRelNode("default","foo_dali_udf2");
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

  @Test
  public void testLateralView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT a, t.ccol",
        "FROM complex",
        "LATERAL VIEW explode(complex.c) t as ccol"
    ));
    String targetSql = String.join("\n",
        "SELECT complex.a, t1.ccol",
        "FROM default.complex LATERAL VIEW EXPLODE(complex.c) t1 AS ccol"
    );
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewOuter() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT a, t.ccol",
        "FROM complex",
        "LATERAL VIEW OUTER explode(complex.c) t as ccol"
    ));
    String relNodePlan = RelOptUtil.toString(relNode);
    System.out.println(relNodePlan);
    String convertToSparkSql = CoralSpark.create(relNode).getSparkSql();

    String targetSql = String.join("\n",
        "SELECT complex.a, t1.ccol",
        "FROM default.complex "+
        "LATERAL VIEW OUTER "+
        "EXPLODE("+
            "if(complex.c IS NOT NULL AND size(complex.c) > 0, complex.c, ARRAY (NULL))"+
        ") t1 AS ccol"
    );
    assertEquals(convertToSparkSql, targetSql);
  }

  @Test
  public void testMultipleLateralView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT a, t.ccol, t2.ccol2",
        "FROM complex ",
        "LATERAL VIEW explode(complex.c) t AS ccol ",
        "LATERAL VIEW explode(complex.c) t2 AS ccol2 "
    ));
    String targetSql = String.join("\n",
        "SELECT complex.a, t1.ccol, t4.ccol2",
        "FROM default.complex " +
        "LATERAL VIEW EXPLODE(complex.c) t1 AS ccol "+
        "LATERAL VIEW EXPLODE(complex.c) t4 AS ccol2");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeArrayMap() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT array(map('abc', 123, 'def', 567), map('pqr', 65, 'xyz', 89))[0]['abc']",
        "FROM bar"
    ));

    String targetSql = String.join("\n",
        "SELECT ARRAY (MAP ('abc', 123, 'def', 567), MAP ('pqr', 65, 'xyz', 89))[0]['abc']",
        "FROM default.bar"
    );
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeNamedStruct() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT named_struct('abc', 123, 'def', 'xyz').def",
        "FROM bar"
    ));
    String targetSql = String.join("\n",
        "SELECT named_struct('abc', 123, 'def', 'xyz').def",
        "FROM default.bar"
    );
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeString() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT CAST(1 AS STRING)",
        "FROM bar"
    ));
    String targetSql = String.join("\n",
        "SELECT CAST(1 AS STRING)",
        "FROM default.bar"
    );
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  /**
   * Following Queries are not supported
   */

  @Test(expectedExceptions = IllegalStateException.class)
  public void testLateralViewStarNotSupported() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT a, t.*",
        "FROM complex",
        "LATERAL VIEW explode(complex.c) t"
    ));
    CoralSpark.create(relNode);
  }

  @Test(expectedExceptions = UnhandledASTTokenException.class)
  public void testLateralViewMapNotSupported() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT a, t.ccol1, t.ccol2",
        "FROM complex",
        "LATERAL VIEW explode(complex.m) t as ccol1, ccol2"
    ));
    CoralSpark.create(relNode);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testNamedStructViewWithSelectNotSupported() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n","",
        "SELECT named_struct_view.named_struc",
        "FROM named_struct_view"
    ));

    /*  Produces following SQL which is incorrect
     *
     *  SELECT CAST(named_struct('abc', 123, 'def', 'xyz')AS ROW(abc INTEGER, def VARCHAR(2147483647))) named_struc
     *  FROM default.bar
     */
    String targetSql = String.join("\n",
        "SELECT named_struct('abc', 123, 'def', 'xyz') named_struc",
        "FROM default.bar"
    );
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test(expectedExceptions = AssertionError.class)
  public void testLateralViewGroupByNotSupported() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT adid, count(1)",
        "FROM complex",
        "LATERAL VIEW explode(c) t as adid",
        "GROUP BY adid"
    ));
    CoralSpark.create(relNode);
  }

}
