/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.exceptions.UnsupportedUDFException;

import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.testng.Assert.*;


public class CoralSparkTest {

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException {
    TestUtils.initializeViews();

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

    UnsupportedHiveUDFsInSpark.add("com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF");

    TransportableUDFMap.add("com.linkedin.coral.hive.hive2rel.CoralTestUDF", "com.linkedin.coral.spark.CoralTestUDF",
        "ivy://com.linkedin.coral.spark.CoralTestUDF", null);
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
    // [LIHADOOP-47172] use date literal in view definition
    String targetSql =
        String.join("\n", "SELECT '2013-01-01', '2017-08-22 01:02:03', 123, 123", "FROM default.foo", "LIMIT 1");
    RelNode relNode = TestUtils.toRelNode("default", "foo_v1");
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testGetSQLFromView() {
    String targetSql = String.join("\n", "SELECT t0.bcol, bar.x", "FROM (SELECT b bcol, SUM(c) sum_c",
        "FROM default.foo", "GROUP BY b) t0", "INNER JOIN default.bar ON t0.sum_c = bar.y");
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
  public void testDaliUdf() {
    // Dali view foo_dali_udf contains a UDF defined in TransportableUDFMap.
    // The actual values are determined by the parameter values of TransportableUDFMap.add() call.
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
    // check if CoralSpark can fetch artifactory url from TransportableUDFMap
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    String targetArtifactoryUrl = "ivy://com.linkedin.coral.spark.CoralTestUDF";
    assertTrue(listOfUriStrings.contains(targetArtifactoryUrl));
    // LIHADOOP-48379: need to check the UDF type
    SparkUDFInfo.UDFTYPE testUdfType = udfJars.get(0).getUdfType();
    SparkUDFInfo.UDFTYPE targetUdfType = SparkUDFInfo.UDFTYPE.TRANSPORTABLE_UDF;
    assertEquals(testUdfType, targetUdfType);
    String sparkSqlStmt = coralSpark.getSparkSql();
    String targetSqlStmt = "SELECT default_foo_dali_udf_LessThanHundred(a)\nFROM default.foo";
    assertEquals(sparkSqlStmt, targetSqlStmt);
  }

  @Test
  public void testFallbackToHiveUdf() {
    // Dali view foo_dali_udf2 contains a UDF not defined in BuiltinUDFMap and TransportableUDFMap.
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
    // LIHADOOP-48635: check if CoralSpark can fetch artifactory url from Dali View definition.
    List<String> listOfUriStrings = convertToListOfUriStrings(udfJars.get(0).getArtifactoryUrls());
    String targetArtifactoryUrl = "ivy://com.linkedin:udf:1.0";
    assertTrue(listOfUriStrings.contains(targetArtifactoryUrl));
    // LIHADOOP-48379: need to check the UDF type
    SparkUDFInfo.UDFTYPE testUdfType = udfJars.get(0).getUdfType();
    SparkUDFInfo.UDFTYPE targetUdfType = SparkUDFInfo.UDFTYPE.HIVE_CUSTOM_UDF;
    assertEquals(testUdfType, targetUdfType);
    String sparkSqlStmt = coralSpark.getSparkSql();
    String targetSqlStmt = "SELECT default_foo_dali_udf2_GreaterThanHundred(a)\nFROM default.foo";
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
    // Dali view foo_dali_udf3 contains 2 UDFs.  One UDF is defined in TransportableUDFMap.  The other one is not.
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
    String targetSql = String.join("\n", "SELECT complex.a, t1.ccol",
        "FROM default.complex LATERAL VIEW EXPLODE(complex.c) t1 AS ccol");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewOuter() {
    RelNode relNode = TestUtils.toRelNode(
        String.join("\n", "", "SELECT a, t.ccol", "FROM complex", "LATERAL VIEW OUTER explode(complex.c) t as ccol"));
    String relNodePlan = RelOptUtil.toString(relNode);
    System.out.println(relNodePlan);
    String convertToSparkSql = CoralSpark.create(relNode).getSparkSql();

    String targetSql = String.join("\n", "SELECT complex.a, t1.ccol", "FROM default.complex " + "LATERAL VIEW OUTER "
        + "EXPLODE(" + "if(complex.c IS NOT NULL AND size(complex.c) > 0, complex.c, ARRAY (NULL))" + ") t1 AS ccol");
    assertEquals(convertToSparkSql, targetSql);
  }

  @Test
  public void testMultipleLateralView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol, t2.ccol2", "FROM complex ",
        "LATERAL VIEW explode(complex.c) t AS ccol ", "LATERAL VIEW explode(complex.c) t2 AS ccol2 "));
    String targetSql = String.join("\n", "SELECT complex.a, t1.ccol, t4.ccol2", "FROM default.complex "
        + "LATERAL VIEW EXPLODE(complex.c) t1 AS ccol " + "LATERAL VIEW EXPLODE(complex.c) t4 AS ccol2");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMap() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol1, t.ccol2", "FROM complex",
        "LATERAL VIEW explode(complex.m) t as ccol1, ccol2"));
    String targetSql = String.join("\n", "SELECT complex.a, t1.KEY ccol1, t1.VALUE ccol2",
        "FROM default.complex LATERAL VIEW EXPLODE(complex.m) t1 AS KEY, VALUE");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralViewMapOuter() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT a, t.ccol1, t.ccol2", "FROM complex",
        "LATERAL VIEW OUTER explode(complex.m) t as ccol1, ccol2"));
    String targetSql = String.join("\n", "SELECT complex.a, t1.KEY ccol1, t1.VALUE ccol2",
        "FROM default.complex LATERAL VIEW OUTER EXPLODE(if(complex.m IS NOT NULL AND size(complex.m) > 0, complex.m, MAP (NULL, NULL))) t1 AS KEY, VALUE");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testLateralUDTF() {
    RelNode relNode = TestUtils.toRelNode("default", "foo_lateral_udtf");
    String targetSql = "SELECT complex.a, t.col1\n"
        + "FROM default.complex LATERAL VIEW default_foo_lateral_udtf_CountOfRow(complex.a) t AS col1";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeArrayMap() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "",
        "SELECT array(map('abc', 123, 'def', 567), map('pqr', 65, 'xyz', 89))[0]['abc']", "FROM bar"));

    String targetSql = String.join("\n",
        "SELECT ARRAY (MAP ('abc', 123, 'def', 567), MAP ('pqr', 65, 'xyz', 89))[0]['abc']", "FROM default.bar");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testArrayElementWithFunctionArgument() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT c[size(c) - 1]", "FROM complex"));

    String targetSql = String.join("\n", "SELECT c[size(c) - 1 + 1 - 1]", "FROM default.complex");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeNamedStruct() {
    RelNode relNode =
        TestUtils.toRelNode(String.join("\n", "", "SELECT named_struct('abc', 123, 'def', 'xyz').def", "FROM bar"));
    String targetSql = String.join("\n", "SELECT named_struct('abc', 123, 'def', 'xyz').def", "FROM default.bar");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testDataTypeString() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT CAST(1 AS STRING)", "FROM bar"));
    String targetSql = String.join("\n", "SELECT CAST(1 AS STRING)", "FROM default.bar");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testNamedStructViewWithSelectSupported() {
    RelNode relNode =
        TestUtils.toRelNode(String.join("\n", "", "SELECT named_struct_view.named_struc", "FROM named_struct_view"));
    String relNodePlan = RelOptUtil.toString(relNode);
    System.out.println(relNodePlan);
    String convertToSparkSql = CoralSpark.create(relNode).getSparkSql();

    /*  [LIHADOOP-43199] the test query is translated to:
     *  SELECT named_struct('abc', 123, 'def', 'xyz') named_struc FROM default.bar;
     */
    String targetSql =
        String.join("\n", "SELECT named_struct('abc', 123, 'def', 'xyz') named_struc", "FROM default.bar");
    assertEquals(convertToSparkSql, targetSql);
  }

  /**
   * Following Queries are not supported
   */

  @Test(expectedExceptions = IllegalStateException.class)
  public void testLateralViewStarNotSupported() {
    RelNode relNode = TestUtils
        .toRelNode(String.join("\n", "", "SELECT a, t.*", "FROM complex", "LATERAL VIEW explode(complex.c) t"));
    CoralSpark.create(relNode);
  }

  @Test
  public void testLateralViewGroupBy() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT adid, count(1)", "FROM complex",
        "LATERAL VIEW explode(c) t as adid", "GROUP BY adid"));
    String targetSql = String.join("\n", "SELECT t1.adid, COUNT(*)",
        "FROM default.complex LATERAL VIEW EXPLODE(complex.c) t1 AS adid", "GROUP BY t1.adid");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testTimestampConversion() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT cast(b AS timestamp)", "FROM complex"));
    String targetSql = String.join("\n", "SELECT CAST(b AS TIMESTAMP)", "FROM default.complex");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSelectNullAs() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT NULL AS alias", "FROM complex"));
    String targetSql = String.join("\n", "SELECT NULL alias", "FROM default.complex");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSelectSubstring() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT substring(b,1,2)", "FROM complex"));
    // Default operator SqlSubstringFunction would generate SUBSTRING(b FROM 1 for 2)
    String targetSql = String.join("\n", "SELECT SUBSTRING(b, 1, 2)", "FROM default.complex");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testCastAsBinary() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT CAST(NULL AS BINARY)", "FROM complex"));
    // without fix in CORAL-120 the default translation is CAST(NULL AS VARBINARY)
    // which is not supported in Spark
    String targetSql = String.join("\n", "SELECT CAST(NULL AS BINARY)", "FROM default.complex");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testSchemaPromotionView() {
    RelNode relNode = TestUtils.toRelNode(String.join("\n", "", "SELECT * ", "FROM view_schema_promotion_wrapper"));
    String targetSql = String.join("\n", "SELECT a, CAST(b AS ARRAY<INTEGER>) b", "FROM default.schema_promotion");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  @Test
  public void testUnionExtractUDF() {
    RelNode relNode = TestUtils.toRelNode("SELECT extract_union(foo) from union_table");
    String targetSql = String.join("\n", "SELECT foo", "FROM default.union_table");
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);

    RelNode relNode2 = TestUtils.toRelNode("SELECT extract_union(foo, 2) from union_table");
    String targetSql2 = String.join("\n", "SELECT foo.tag_2", "FROM default.union_table");
    assertEquals(CoralSpark.create(relNode2).getSparkSql(), targetSql2);
  }

  @Test
  public void testDateFunction() {
    RelNode relNode = TestUtils.toRelNode("SELECT date('2021-01-02') as a FROM foo");
    String targetSql = "SELECT date('2021-01-02') a\n" + "FROM default.foo";
    assertEquals(CoralSpark.create(relNode).getSparkSql(), targetSql);
  }

  private List<String> convertToListOfUriStrings(List<URI> listOfUris) {
    List<String> listOfUriStrings = new LinkedList<>();
    for (URI uri : listOfUris) {
      listOfUriStrings.add(uri.toString());
    }
    return listOfUriStrings;
  }

}
