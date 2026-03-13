/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import com.linked.coral.spark.CoralSparkViewCatalog;

import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.internal.SQLConf;
import org.apache.spark.sql.types.StructType;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


public class CoralSparkViewCatalogTest {

  private static SparkSession spark = null;
  @BeforeSuite
  static void setup() {
    final TestHiveMetastore metastore = TestHiveMetastore.INSTANCE;
    HiveConf hiveConf = metastore.hiveConf();
    spark = SparkSession.builder().master("local[2]").config(SQLConf.PARTITION_OVERWRITE_MODE().key(), "dynamic")
        .config("spark.hadoop." + METASTOREURIS.varname, hiveConf.get(METASTOREURIS.varname)).enableHiveSupport()
        .getOrCreate();
    spark.conf().set("spark.sql.catalog.spark_catalog", CoralSparkViewCatalog.class.getName());
    spark.sql("CREATE NAMESPACE IF NOT EXISTS default");
    spark.sql(
        "CREATE FUNCTION IF NOT EXISTS default_foo_dali_udf_LessThanHundred AS 'com.linkedin.coral.spark.CoralTestUDF'");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.spark.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
  }

  @AfterSuite
  static void clean() {
    spark.close();
  }

  @BeforeTest
  void beforeEach() {
    spark.sql(
        "CREATE TABLE IF NOT EXISTS default.t (intCol int, dateCol date, structCol struct<doubleCol:double,stringCol:string>)");
    spark.sql("CREATE TABLE IF NOT EXISTS default.t2 (id int, name string, value double)");
    spark.sql(
        "CREATE TABLE IF NOT EXISTS default.t_complex (id int, arr array<int>, m map<string,int>, nested struct<inner_struct:struct<x:double,y:double>,label:string>)");
  }

  @AfterTest
  void afterEach() {
    spark.sql("DROP TABLE IF EXISTS default.t");
    spark.sql("DROP TABLE IF EXISTS default.t2");
    spark.sql("DROP TABLE IF EXISTS default.t_complex");
  }

  @Test
  public void testViewQueryCasingPreservation() {
    spark.sql("CREATE OR REPLACE VIEW default.test_view AS SELECT * FROM default.t");
    final StructType expectedSchema = new StructType().add("intCol", "int").add("dateCol", "date").add("structCol",
        new StructType().add("doubleCol", "double").add("stringCol", "string"));
    Assert.assertEquals(expectedSchema, spark.table("default.test_view").schema());
  }

  @Test
  public void testViewWithJoin() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.join_view AS", "SELECT t.intCol, t2.name, t2.value",
        "FROM default.t", "JOIN default.t2 ON t.intCol = t2.id"));
    final StructType expectedSchema =
        new StructType().add("intCol", "int").add("name", "string").add("value", "double");
    Assert.assertEquals(expectedSchema, spark.table("default.join_view").schema());
  }

  @Test
  public void testViewWithLateralViewExplode() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.lateral_view AS", "SELECT id, arr_elem",
        "FROM default.t_complex", "LATERAL VIEW EXPLODE(arr) arr_table AS arr_elem"));
    final StructType expectedSchema = new StructType().add("id", "int").add("arr_elem", "int");
    Assert.assertEquals(expectedSchema, spark.table("default.lateral_view").schema());
  }

  @Test
  public void testViewWithSubquery() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.subquery_view AS", "SELECT sub.intCol, sub.dateCol",
        "FROM (SELECT intCol, dateCol FROM default.t) sub"));
    final StructType expectedSchema = new StructType().add("intCol", "int").add("dateCol", "date");
    Assert.assertEquals(expectedSchema, spark.table("default.subquery_view").schema());
  }

  @Test
  public void testViewWithAggregation() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.agg_view AS",
        "SELECT dateCol, COUNT(intCol) AS cnt, SUM(intCol) AS total", "FROM default.t", "GROUP BY dateCol"));
    final StructType expectedSchema =
        new StructType().add("dateCol", "date", true).add("cnt", "long", false).add("total", "long", true);
    Assert.assertEquals(expectedSchema, spark.table("default.agg_view").schema());
  }

  @Test
  public void testViewWithComplexTypes() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.complex_view AS",
        "SELECT id, arr, m, nested.inner_struct.x AS x, nested.label AS label", "FROM default.t_complex"));
    final StructType expectedSchema = new StructType().add("id", "int").add("arr", "array<int>")
        .add("m", "map<string,int>").add("x", "double").add("label", "string");
    Assert.assertEquals(expectedSchema, spark.table("default.complex_view").schema());
  }

  @Test
  public void testViewWithNamedStruct() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.named_struct_view AS",
        "SELECT NAMED_STRUCT('a', intCol, 'b', dateCol) AS s", "FROM default.t"));
    final StructType expectedSchema =
        new StructType().add("s", new StructType().add("a", "int").add("b", "date"), false);
    Assert.assertEquals(expectedSchema, spark.table("default.named_struct_view").schema());
  }

  @Test
  public void testViewQueryWithUDF() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.foo_dali_udf",
        "tblproperties('functions' = 'LessThanHundred:com.linkedin.spark.catalog.CoralTestUDF',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.0')", "AS",
        "SELECT default_foo_dali_udf_LessThanHundred(intCol) Col", "FROM default.t"));
    final StructType expectedSchema = new StructType().add("Col", "boolean");
    Assert.assertEquals(expectedSchema, spark.table("default.foo_dali_udf").schema());
    Assert.assertTrue(spark.sessionState().catalog()
        .functionExists(FunctionIdentifier.apply("default_foo_dali_udf_lessthanhundred")));
  }
}
