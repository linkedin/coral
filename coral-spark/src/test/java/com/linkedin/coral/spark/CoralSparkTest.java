/**
 * Copyright 2018-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;

import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;


public class CoralSparkTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);

    // add the following 3 test UDF to StaticHiveFunctionRegistry for testing purpose.
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedTableFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDTF",
        ImmutableList.of("col1"), ImmutableList.of(SqlTypeName.INTEGER), family(SqlTypeFamily.INTEGER));
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_SPARK_TEST_DIR)));
  }

  @Test
  public void testGetBaseTablesFromView() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<String> base_tables = coralSpark.getBaseTables();
    assertTrue(base_tables.contains("default.foo"));
    assertTrue(base_tables.contains("default.bar"));
  }

  @Test
  public void testLiteralColumnsFromView() {
    // use date literal in view definition
    String targetSql = "SELECT '2013-01-01', '2017-08-22 01:02:03', CAST(123 AS SMALLINT), CAST(123 AS TINYINT)\n"
        + "FROM default.foo foo\n" + "LIMIT 1";
    RelNode relNode = TestUtils.toRelNode("default", "foo_v1");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testGetSQLFromView() {
    String targetSql = "SELECT t0.bcol, bar.x\n" + "FROM (SELECT foo.b bcol, SUM(foo.c) sum_c\n"
        + "FROM default.foo foo\n" + "GROUP BY foo.b) t0\n" + "INNER JOIN default.bar bar ON t0.sum_c = bar.y";
    RelNode relNode = TestUtils.toRelNode("default", "foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testAllowBaseTableInView() {
    RelNode relNode = TestUtils.toRelNode("default", "foo");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<String> base_tables = coralSpark.getBaseTables();
    assertTrue(base_tables.contains("default.foo"));
  }

  @Test
  public void testTransportUDFTransformer() {
    // Dali view foo_dali_udf contains a UDF defined with TransportUDFTransformer.
    // The actual values are determined by the parameter values of TransportUDFTransformer.
    RelNode relNode = TestUtils.toRelNode("default", "foo_dali_udf");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(1, udfJars.size());

    String udfClassName = udfJars.get(0).getClassName();
    String targetClassName = "com.linkedin.coral.spark.CoralTestUDF";
    assertEquals(udfClassName, targetClassName);
    String udfFunctionName = udfJars.get(0).getFunctionName();
    String targetFunctionName = "default_foo_dali_udf_LessThanHundred";
    assertEquals(udfFunctionName, targetFunctionName);
    // check if CoralSpark can fetch artifactory url defined in TransportUDFTransformer
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    String targetArtifactoryUrl = "ivy://com.linkedin.coral.spark.CoralTestUDF?classifier=spark_2.11";
    assertTrue(listOfUriStrings.contains(targetArtifactoryUrl));
    // need to check the UDF type
    SparkUDFInfo.UDFTYPE testUdfType = udfJars.get(0).getUdfType();
    SparkUDFInfo.UDFTYPE targetUdfType = SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF;
    assertEquals(testUdfType, targetUdfType);
    String sparkSqlStmt = coralSpark.getSparkSql();
    String targetSqlStmt = "SELECT default_foo_dali_udf_LessThanHundred(foo.a)\n" + "FROM default.foo foo";
    assertEquals(sparkSqlStmt, targetSqlStmt);
  }

  @Test
  public void testFallBackToLinkedInHiveUDFTransformer() {
    // Dali view foo_dali_udf2 contains a UDF not defined with OperatorBasedSqlCallTransformer or TransportUDFTransformer.
    // We need to fall back to the udf initially defined in HiveFunctionRegistry.
    // Then the function Name comes from Hive metastore in the format dbName_viewName_funcBaseName.
    RelNode relNode = TestUtils.toRelNode("default", "foo_dali_udf2");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();

    String udfClassName = udfJars.get(0).getClassName();
    String targetClassName = "com.linkedin.coral.hive.hive2rel.CoralTestUDF2";
    assertEquals(udfClassName, targetClassName);
    String udfFunctionName = udfJars.get(0).getFunctionName();
    String targetFunctionName = "default_foo_dali_udf2_GreaterThanHundred";
    assertEquals(udfFunctionName, targetFunctionName);
    // check if CoralSpark can fetch artifactory url from Dali View definition.
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    String targetArtifactoryUrl = "ivy://com.linkedin:udf:1.0";
    assertTrue(listOfUriStrings.contains(targetArtifactoryUrl));
    // need to check the UDF type
    SparkUDFInfo.UDFTYPE testUdfType = udfJars.get(0).getUdfType();
    SparkUDFInfo.UDFTYPE targetUdfType = SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF;
    assertEquals(testUdfType, targetUdfType);
    String sparkSqlStmt = coralSpark.getSparkSql();
    String targetSqlStmt = "SELECT default_foo_dali_udf2_GreaterThanHundred(foo.a)\n" + "FROM default.foo foo";
    assertEquals(sparkSqlStmt, targetSqlStmt);
  }

  @Test(expectedExceptions = UnsupportedUDFException.class)
  public void testUnsupportedUdf() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_dali_udf5");
    // this step should proactively fail because UDF is not supported.
    CoralSpark.create(relNode);
  }

  @Test
  public void testTwoFunctionsWithDependencies() {
    // Dali view foo_dali_udf3 contains 2 UDFs.  One UDF is defined with TransportUDFTransformer.  The other one is not.
    // We need to fall back the second one to the udf initially defined in HiveFunctionRegistry.
    RelNode relNode = TestUtils.toRelNode("default", "foo_dali_udf3");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(2, udfJars.size());
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    // contains only one dependency as added by StaticHiveFunctionRegistry.createAddUserDefinedFunction.
    assertTrue(listOfUriStrings.contains("ivy://com.linkedin:udf:1.1"));
  }

  @Test
  public void testExtraSpaceInDependencyParam() {
    // Dali view foo_dali_udf4 is same as foo_dali_udf2, except it contains extra space in dependencies parameter
    // inside TBLPROPERTIES clause.
    RelNode relNode = TestUtils.toRelNode("default", "foo_dali_udf4");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(1, udfJars.size());
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    String targetArtifactoryUrl = "ivy://com.linkedin:udf:1.0";
    assertTrue(listOfUriStrings.contains(targetArtifactoryUrl));
  }

  @Test
  public void testNoUdf() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_bar_view");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertTrue(udfJars.isEmpty());
  }

  @Test
  public void testLateralView() {
    RelNode relNode = TestUtils.toRelNode(
        String.join("\n", "", "SELECT a, t.ccol", "FROM complex", "LATERAL VIEW explode(complex.c) t as ccol"));
    String targetSql =
        "SELECT complex.a, t0.ccol\n" + "FROM default.complex complex LATERAL VIEW EXPLODE(complex.c) t0 AS ccol";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewOuter() {
    RelNode relNode = TestUtils.toRelNode(
        String.join("\n", "", "SELECT a, t.ccol", "FROM complex", "LATERAL VIEW OUTER explode(complex.c) t as ccol"));
    String relNodePlan = RelOptUtil.toString(relNode);
    System.out.println(relNodePlan);
    String convertToSparkSql = CoralSpark.create(relNode).getSparkSql();

    String targetSql =
        "SELECT complex.a, t0.ccol\n" + "FROM default.complex complex LATERAL VIEW OUTER EXPLODE(complex.c) t0 AS ccol";
    assertEquals(convertToSparkSql, targetSql);
  }

  @Test
  public void testMultipleLateralView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol, t2.ccol2", "FROM complex ",
        "LATERAL VIEW explode(complex.c) t AS ccol ", "LATERAL VIEW explode(complex.c) t2 AS ccol2 "));
    String targetSql = "SELECT complex.a, t0.ccol, t2.ccol2\n"
        + "FROM default.complex complex LATERAL VIEW EXPLODE(complex.c) t0 AS ccol LATERAL VIEW EXPLODE(complex.c) t2 AS ccol2";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMap() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol1, t.ccol2", "FROM complex",
        "LATERAL VIEW explode(complex.m) t as ccol1, ccol2"));
    String targetSql = "SELECT complex.a, t0.ccol1, t0.ccol2\n"
        + "FROM default.complex complex LATERAL VIEW EXPLODE(complex.m) t0 AS ccol1, ccol2";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMapWithStructValue() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol1, t.ccol2", "FROM fuzzy_union.tableH",
        "LATERAL VIEW explode(tableH.b) t as ccol1, ccol2"));
    String targetSql = "SELECT tableh.a, t0.ccol1, t0.ccol2\n"
        + "FROM fuzzy_union.tableh tableh LATERAL VIEW EXPLODE(tableh.b) t0 AS ccol1, ccol2";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMapOuter() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol1, t.ccol2", "FROM complex",
        "LATERAL VIEW OUTER explode(complex.m) t as ccol1, ccol2"));
    String targetSql = "SELECT complex.a, t0.ccol1, t0.ccol2\n"
        + "FROM default.complex complex LATERAL VIEW OUTER EXPLODE(complex.m) t0 AS ccol1, ccol2";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralUDTF() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_lateral_udtf");
    String targetSql = "SELECT complex.a, t.col1\n"
        + "FROM default.complex complex LATERAL VIEW default_foo_lateral_udtf_CountOfRow(complex.a) t AS col1";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeArrayMap() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT array(map('abc', 123, 'def', 567), map('pqr', 65, 'xyz', 89))[0]['abc']", "FROM bar"));

    String targetSql =
        "SELECT ARRAY (MAP ('abc', 123, 'def', 567), MAP ('pqr', 65, 'xyz', 89))[0]['abc']\n" + "FROM default.bar bar";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testArrayElementWithFunctionArgument() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT c[size(c) - 1]", "FROM complex"));

    String targetSql = "SELECT complex.c[size(complex.c) - 1 + 1 - 1]\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeNamedStruct() {
    RelNode relNode =
        TestUtils.toRelNode(String.join("\n", "", "SELECT named_struct('abc', 123, 'def', 'xyz').def", "FROM bar"));
    String targetSql = "SELECT named_struct('abc', 123, 'def', 'xyz').def\n" + "FROM default.bar bar";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeString() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT CAST(1 AS STRING)", "FROM bar"));
    String targetSql = "SELECT CAST(1 AS STRING)\n" + "FROM default.bar bar";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testNamedStructViewWithSelectSupported() {
    RelNode relNode =
        TestUtils.toRelNode(String.join("\n", "", "SELECT named_struct_view.named_struc", "FROM named_struct_view"));
    String relNodePlan = RelOptUtil.toString(relNode);
    System.out.println(relNodePlan);
    String convertToSparkSql = CoralSpark.create(relNode).getSparkSql();

    /*  the test query is translated to:
     *  SELECT named_struct('abc', 123, 'def', 'xyz') named_struc FROM default.bar;
     */
    String targetSql = "SELECT named_struct('abc', 123, 'def', 'xyz') named_struc\n" + "FROM default.bar bar";
    assertEquals(convertToSparkSql, targetSql);
  }

  @Test
  public void testLateralViewStar() {
    RelNode relNode = TestUtils
        .toRelNode(String.join("\n", "", "SELECT a, t.*", "FROM complex", "LATERAL VIEW explode(complex.c) t"));
    String targetSql =
        "SELECT complex.a, t0.col\n" + "FROM default.complex complex LATERAL VIEW EXPLODE(complex.c) t0 AS col";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewGroupBy() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT adid, count(1)", "FROM complex",
        "LATERAL VIEW explode(c) t as adid", "GROUP BY adid"));
    String targetSql = "SELECT t0.adid, COUNT(*)\n"
        + "FROM default.complex complex LATERAL VIEW EXPLODE(complex.c) t0 AS adid\n" + "GROUP BY t0.adid";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testTimestampConversion() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT cast(b AS timestamp)", "FROM complex"));
    String targetSql = "SELECT CAST(complex.b AS TIMESTAMP)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSelectNullAs() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT NULL AS alias", "FROM complex"));
    String targetSql = "SELECT NULL alias\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSelectSubstring() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT substring(b,1,2)", "FROM complex"));
    // Default operator SqlSubstringFunction would generate SUBSTRING(b FROM 1 for 2)
    String targetSql = "SELECT substr(complex.b, 1, 2)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastAsBinary() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT CAST(NULL AS BINARY)", "FROM complex"));
    String targetSql = "SELECT CAST(NULL AS BINARY)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testInterval() {
    RelNode relNode = TestUtils.toRelNode("SELECT CAST('2021-08-31' AS DATE) + INTERVAL '7' DAY FROM default.complex");
    String targetSql = "SELECT (CAST('2021-08-31' AS DATE) + INTERVAL '7' DAY)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testIntervalNegative() {
    RelNode relNode = TestUtils.toRelNode("SELECT CAST('2021-08-31' AS DATE) + INTERVAL '-7' DAY FROM default.complex");
    String targetSql = "SELECT (CAST('2021-08-31' AS DATE) + INTERVAL '-7' DAY)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testIntervalDayToSecond() {
    RelNode relNode = TestUtils
        .toRelNode("SELECT CAST('2021-08-31' AS DATE) + INTERVAL '7 01:02:03' DAY TO SECOND FROM default.complex");
    String targetSql =
        "SELECT (CAST('2021-08-31' AS DATE) + INTERVAL '7 01:02:03' DAY TO SECOND)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testIntervalYearToMonth() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT CAST('2021-08-31' AS DATE) + INTERVAL '1-6' YEAR TO MONTH FROM default.complex");
    String targetSql =
        "SELECT (CAST('2021-08-31' AS DATE) + INTERVAL '1-6' YEAR TO MONTH)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSchemaPromotionView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT * ", "FROM view_schema_promotion_wrapper"));
    String targetSql = "SELECT schema_promotion.a, CAST(schema_promotion.b AS ARRAY<INTEGER>) b\n"
        + "FROM default.schema_promotion schema_promotion";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testUnionExtractUDF() {
    RelNode relNode = TestUtils.toRelNode("SELECT extract_union(foo) from union_table");
    String targetSql = "SELECT coalesce_struct(union_table.foo)\n" + "FROM default.union_table union_table";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    RelNode relNode1 = TestUtils.toRelNode("SELECT extract_union(foo, 2) from union_table");
    String targetSql1 = "SELECT coalesce_struct(union_table.foo, 3)\n" + "FROM default.union_table union_table";
    assertEquals(CoralSpark.create(relNode1).getSparkSql(), targetSql1);

    // Nested union case
    RelNode relNode2 = TestUtils.toRelNode("SELECT extract_union(a) from nested_union");
    String targetSql2 = "SELECT coalesce_struct(nested_union.a)\n" + "FROM default.nested_union nested_union";
    assertEquals(CoralSpark.create(relNode2).getSparkSql(), targetSql2);
  }

  @Test
  public void testDateFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT date('2021-01-02') as a FROM foo");
    String targetSql = "SELECT date('2021-01-02') a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  private List<String> convertToListOfUriStrings(List<URI> listOfUris) {
    List<String> listOfUriStrings = new LinkedList<>();
    for (URI uri : listOfUris) {
      listOfUriStrings.add(uri.toString());
    }
    return listOfUriStrings;
  }

  @Test
  public void testLateralViewArray() {
    RelNode relNode = TestUtils
        .toRelNode("SELECT col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias AS col");

    String targetSql = "SELECT t2.col\n" + "FROM (SELECT ARRAY ('a1', 'a2') a\n"
        + "FROM (VALUES  (0)) t (ZERO)) t0 LATERAL VIEW EXPLODE(t0.a) t2 AS col";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewArray2() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT arr.alias FROM foo tmp LATERAL VIEW EXPLODE(ARRAY('a', 'b')) arr as alias");

    String targetSql = "SELECT t0.alias\n" + "FROM default.foo foo LATERAL VIEW EXPLODE(ARRAY ('a', 'b')) t0 AS alias";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewArrayWithoutColumns() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias");

    String targetSql = "SELECT t2.col\n" + "FROM (SELECT ARRAY ('a1', 'a2') a\n"
        + "FROM (VALUES  (0)) t (ZERO)) t0 LATERAL VIEW EXPLODE(t0.a) t2 AS col";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMap2() {
    RelNode relNode = TestUtils.toRelNode(
        "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value");

    String targetSql = "SELECT t2.key, t2.value\n" + "FROM (SELECT MAP ('key1', 'value1') m\n"
        + "FROM (VALUES  (0)) t (ZERO)) t0 LATERAL VIEW EXPLODE(t0.m) t2 AS key, value";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMapRenameColumns() {
    RelNode relNode = TestUtils.toRelNode(
        "SELECT k1, v1 FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS k1, v1");

    String targetSql = "SELECT t2.k1, t2.v1\n" + "FROM (SELECT MAP ('key1', 'value1') m\n"
        + "FROM (VALUES  (0)) t (ZERO)) t0 LATERAL VIEW EXPLODE(t0.m) t2 AS k1, v1";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMapWithoutColumns() {
    RelNode relNode = TestUtils
        .toRelNode("SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias");

    String targetSql = "SELECT t2.KEY key, t2.VALUE value\n" + "FROM (SELECT MAP ('key1', 'value1') m\n"
        + "FROM (VALUES  (0)) t (ZERO)) t0 LATERAL VIEW EXPLODE(t0.m) t2 AS KEY, VALUE";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testXpathFunctions() {
    RelNode relNode = TestUtils.toRelNode("select xpath('<a><b>b1</b><b>b2</b></a>','a/*') FROM foo");
    String targetSql = "SELECT xpath('<a><b>b1</b><b>b2</b></a>', 'a/*')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_string('<a><b>bb</b><c>cc</c></a>', 'a/b') FROM foo");
    targetSql = "SELECT xpath_string('<a><b>bb</b><c>cc</c></a>', 'a/b')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_boolean('<a><b>b</b></a>', 'a/b') FROM foo");
    targetSql = "SELECT xpath_boolean('<a><b>b</b></a>', 'a/b')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_int('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_int('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_short('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_short('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_long('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_long('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_float('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_float('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_double('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_double('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT xpath_number('<a>b</a>', 'a = 10') FROM foo");
    targetSql = "SELECT xpath_number('<a>b</a>', 'a = 10')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewPosExplodeWithColumns() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT arr.alias FROM foo tmp LATERAL VIEW POSEXPLODE(ARRAY('a', 'b')) arr AS pos, alias");

    String targetSql =
        "SELECT t0.alias\n" + "FROM default.foo foo LATERAL VIEW POSEXPLODE(ARRAY ('a', 'b')) t0 AS pos, alias";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewOuterPosExplodeWithColumns() {
    RelNode relNode = TestUtils
        .toRelNode("SELECT arr.alias FROM foo tmp LATERAL VIEW OUTER POSEXPLODE(ARRAY('a', 'b')) arr AS pos, alias");

    String targetSql =
        "SELECT t0.alias\n" + "FROM default.foo foo LATERAL VIEW OUTER POSEXPLODE(ARRAY ('a', 'b')) t0 AS pos, alias";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewPosExplodeWithoutColumns() {
    RelNode relNode = TestUtils.toRelNode("SELECT arr.col FROM foo tmp LATERAL VIEW POSEXPLODE(ARRAY('a', 'b')) arr");

    String targetSql =
        "SELECT t0.col\n" + "FROM default.foo foo LATERAL VIEW POSEXPLODE(ARRAY ('a', 'b')) t0 AS ORDINALITY, col";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testConcat() {
    RelNode relNode = TestUtils.toRelNode("SELECT 'a' || 'b'");

    String targetSql = "SELECT concat('a', 'b')\nFROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
    RelNode relNode2 = TestUtils.toRelNode("SELECT 'a' || 'b' || 'c'");

    String targetSql2 = "SELECT concat(concat('a', 'b'), 'c')\nFROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode2).getSparkSql(), targetSql2);
  }

  @Test
  public void testIfWithNullAsSecondParameter() {
    RelNode relNode = TestUtils.toRelNode("SELECT if(FALSE, NULL, named_struct('a', ''))");

    String targetSql = "SELECT if(FALSE, NULL, named_struct('a', ''))\n" + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testIfWithNullAsThirdParameter() {
    RelNode relNode = TestUtils.toRelNode("SELECT if(FALSE, named_struct('a', ''), NULL)");

    String targetSql = "SELECT if(FALSE, named_struct('a', ''), NULL)\n" + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testMd5Function() {
    RelNode relNode = TestUtils.toRelNode("SELECT md5('ABC') as a FROM foo");
    String targetSql = "SELECT md5('ABC') a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testShaFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT sha1('ABC') as a FROM foo");
    String targetSql = "SELECT sha1('ABC') a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    RelNode relNode2 = TestUtils.toRelNode("SELECT sha('ABC') as a FROM foo");
    String targetSql2 = "SELECT sha('ABC') a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode2).getSparkSql(), targetSql2);
  }

  @Test
  public void testCrc32Function() {
    RelNode relNode = TestUtils.toRelNode("SELECT crc32('ABC') as a FROM foo");
    String targetSql = "SELECT crc32('ABC') a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testTranslateFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT translate('aaa', 'a', 'b') FROM default.foo");
    String targetSql = "SELECT TRANSLATE('aaa', 'a', 'b')\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastByTypeName() {
    RelNode relNode = TestUtils.toRelNode(
        "SELECT CAST(1 AS DOUBLE), CAST(1.5 AS INT), CAST(2.3 AS STRING), CAST(1631142817 AS TIMESTAMP), CAST('' AS BOOLEAN) FROM default.complex");
    String targetSql =
        "SELECT CAST(1 AS DOUBLE), CAST(1.5 AS INTEGER), CAST(2.3 AS STRING), CAST(1631142817 AS TIMESTAMP), CAST('' AS BOOLEAN)\n"
            + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testReflectFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT reflect('java.lang.String', 'valueOf', 1) FROM default.complex");
    String targetSql = "SELECT reflect('java.lang.String', 'valueOf', 1)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testReflectFunctionReturnType() {
    RelNode relNode = TestUtils.toRelNode("SELECT reflect('java.lang.String', 'valueOf', 1) + 1 FROM default.complex");
    String targetSql =
        "SELECT CAST(reflect('java.lang.String', 'valueOf', 1) AS INTEGER) + 1\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT reflect('java.lang.String', 'valueOf', 1) || 'a' FROM default.complex");
    targetSql = "SELECT concat(reflect('java.lang.String', 'valueOf', 1), 'a')\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testJavaMethodFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT java_method('java.lang.String', 'valueOf', 1) FROM default.complex");
    String targetSql = "SELECT reflect('java.lang.String', 'valueOf', 1)\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testJavaMethodFunctionReturnType() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT java_method('java.lang.String', 'valueOf', 1) + 1 FROM default.complex");
    String targetSql =
        "SELECT CAST(reflect('java.lang.String', 'valueOf', 1) AS INTEGER) + 1\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    relNode = TestUtils.toRelNode("SELECT java_method('java.lang.String', 'valueOf', 1) || 'a' FROM default.complex");
    targetSql = "SELECT concat(reflect('java.lang.String', 'valueOf', 1), 'a')\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testNegationOperator() {
    RelNode relNode = TestUtils.toRelNode("SELECT !FALSE as a FROM foo");
    String targetSql = "SELECT NOT FALSE a\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testAliasOrderBy() {
    RelNode relNode =
        TestUtils.toRelNode("SELECT a, SUBSTR(b, 1, 1) AS aliased_column, c FROM foo ORDER BY aliased_column DESC");
    String targetSql = "SELECT foo.a, substr(foo.b, 1, 1) aliased_column, foo.c\n" + "FROM default.foo foo\n"
        + "ORDER BY substr(foo.b, 1, 1) DESC NULLS LAST";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testAliasHaving() {
    RelNode relNode = TestUtils.toRelNode(
        "SELECT a, SUBSTR(b, 1, 1) AS aliased_column FROM foo GROUP BY a, b HAVING aliased_column in ('dummy_value')");
    String targetSql = "SELECT foo.a, substr(foo.b, 1, 1) aliased_column\n" + "FROM default.foo foo\n"
        + "GROUP BY foo.a, foo.b\n" + "HAVING substr(foo.b, 1, 1)\n" + "IN ('dummy_value')";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastDecimal() {
    RelNode relNode = TestUtils.toRelNode("SELECT CAST(a as DECIMAL(6, 2)) as casted_decimal FROM default.foo");
    String targetSql = "SELECT CAST(foo.a AS DECIMAL(6, 2)) casted_decimal\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastDecimalDefault() {
    RelNode relNode = TestUtils.toRelNode("SELECT CAST(a as DECIMAL) as casted_decimal FROM default.foo");
    String targetSql = "SELECT CAST(foo.a AS DECIMAL(10, 0)) casted_decimal\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCollectListFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT collect_list(a) FROM default.foo");
    String targetSql = "SELECT collect_list(foo.a)\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCollectSetFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT collect_set(a) FROM default.foo");
    String targetSql = "SELECT collect_set(foo.a)\n" + "FROM default.foo foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSelectArrayIndex() {
    RelNode relNode = TestUtils.toRelNode("SELECT * FROM default.view_expand_array_index");
    String targetSql = "SELECT complex.c[1] c1\n" + "FROM default.complex complex";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDeduplicateUdf() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_duplicate_udf");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    List<SparkUDFInfo> udfJars = coralSpark.getSparkUDFInfoList();
    assertEquals(1, udfJars.size());
  }

  @Test
  public void testNameSakeColumnNamesShouldGetUniqueIdentifiers() {
    String targetSql = "SELECT t0.some_id\n" + "FROM (SELECT tablea.some_id, t.SOME_ID SOME_ID0\n"
        + "FROM duplicate_column_name.tablea tablea\n"
        + "LEFT JOIN (SELECT TRIM(tableb.some_id) SOME_ID, CAST(TRIM(tableb.some_id) AS STRING) $f1\n"
        + "FROM duplicate_column_name.tableb tableb) t ON tablea.some_id = t.$f1) t0\n" + "WHERE t0.some_id <> ''";
    RelNode relNode = TestUtils.toRelNode("duplicate_column_name", "view_namesake_column_names");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testNestedFieldProjectionWithSameNameAlias() {
    String sourceSql = "SELECT complex.s.name as name FROM default.complex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT complex.s.name name\n" + "FROM default.complex complex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testProjectionOfFunctionCall() {
    String sourceSql = "SELECT LOWER(complex.s.name) FROM default.complex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT LOWER(complex.s.name) EXPR_0\n" + "FROM default.complex complex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testProjectionOfCastCall() {
    String sourceSql = "SELECT CAST(complex.a AS STRING) FROM default.complex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT CAST(complex.a AS STRING) EXPR_0\n" + "FROM default.complex complex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testCasePreservedAlias() {
    String sourceSql = "SELECT basecomplex.id FROM default.basecomplex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT basecomplex.id Id\n" + "FROM default.basecomplex basecomplex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testSubFieldProjection() {
    String sourceSql = "SELECT basecomplex.Struct_Col.String_Field FROM default.basecomplex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql =
        "SELECT basecomplex.struct_col.string_field String_Field\n" + "FROM default.basecomplex basecomplex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testAccessingMapField() {
    String sourceSql = "SELECT basecomplex.map_col['foo'] FROM default.basecomplex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT basecomplex.map_col['foo'] EXPR_0\n" + "FROM default.basecomplex basecomplex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testAccessingMapField2() {
    String sourceSql = "SELECT basecomplex.map_col['foo'] as foo FROM default.basecomplex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT basecomplex.map_col['foo'] foo\n" + "FROM default.basecomplex basecomplex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void tesCTEViewWithAlias() {
    String sourceSql =
        "with tmp as (SELECT b bcol, SUM(c) sum_c from default.foo group by b) select tmp.bcol, bar.x from tmp inner join default.bar on tmp.sum_c = bar.y";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT t1.bcol bcol, bar.x x\n" + "FROM (SELECT foo.b bcol, SUM(foo.c) sum_c\n"
        + "FROM default.foo foo\n" + "GROUP BY foo.b) t1\n" + "INNER JOIN default.bar bar ON t1.sum_c = bar.y";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testSelectStar() {
    String sourceSql = "SELECT * FROM default.basecomplex";
    String expandedSql = getCoralSparkTranslatedSqlWithAliasFromCoralSchema(sourceSql);

    String targetSql = "SELECT *\n" + "FROM default.basecomplex basecomplex";
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testConvertFieldAccessOnFunctionCall() {
    RelNode relNode = TestUtils.toRelNode("SELECT named_struct('a', named_struct('b', 1)).a.b");

    String targetSql = "SELECT (named_struct('a', named_struct('b', 1)).a).b\n" + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testAvoidCastToRow() {
    RelNode relNode = TestUtils.toRelNode("SELECT named_struct('a', array(named_struct('b', 1)))");

    String targetSql = "SELECT named_struct('a', ARRAY (named_struct('b', 1)))\n" + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastOnString() {
    RelNode relNode = TestUtils.toRelNode("SELECT CAST('99999999999' AS BIGINT) > 0");

    String targetSql = "SELECT CAST('99999999999' AS BIGINT) > 0\n" + "FROM (VALUES  (0)) t (ZERO)";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  private static String getCoralSparkTranslatedSqlWithAliasFromCoralSchema(String db, String view) {
    RelNode relNode = TestUtils.toRelNode(db, view);
    Schema schema = TestUtils.getAvroSchemaForView(db, view, false);
    CoralSpark coralSpark = CoralSpark.create(relNode, schema);
    return coralSpark.getSparkSql();
  }

  private static String getCoralSparkTranslatedSqlWithAliasFromCoralSchema(String source) {
    RelNode relNode = TestUtils.toRelNode(source);
    Schema schema = TestUtils.getAvroSchemaForView(source, false);
    CoralSpark coralSpark = CoralSpark.create(relNode, schema);
    return coralSpark.getSparkSql();
  }
}
