package com.linkedin.coral.spark;

import com.linked.coral.spark.CoralSparkViewCatalog;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
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

import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


public class CoralSparkViewCatalogTest {

  private static SparkSession spark = null;
  @BeforeSuite
  static void setup() {
    final TestHiveMetastore metastore = TestHiveMetastore.INSTANCE;
    HiveConf hiveConf = metastore.hiveConf();
    spark = SparkSession.builder()
        .master("local[2]")
        .config(SQLConf.PARTITION_OVERWRITE_MODE().key(), "dynamic")
        .config("spark.hadoop." + METASTOREURIS.varname, hiveConf.get(METASTOREURIS.varname))
        .enableHiveSupport()
        .getOrCreate();
    spark.conf().set("spark.sql.catalog.spark_catalog", CoralSparkViewCatalog.class.getName());
    spark.sql("CREATE NAMESPACE IF NOT EXISTS default");
    spark.sql(
        "CREATE FUNCTION IF NOT EXISTS default_foo_dali_udf_LessThanHundred AS 'com.linkedin.coral.spark.catalog.CoralTestUDF'");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.spark.catalog.CoralTestUDF",
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
  }

  @AfterTest
  void afterEach() {
    spark.sql("DROP TABLE IF EXISTS default.t");
  }

  @Test
  public void testViewQueryCasingPreservation() {
    spark.sql("CREATE OR REPLACE VIEW default.test_view AS SELECT * FROM default.t");
    final StructType expectedSchema = new StructType().add("intCol", "int").add("dateCol", "date")
        .add("structCol", new StructType().add("doubleCol", "double").add("stringCol", "string"));
    Assert.assertEquals(expectedSchema, spark.table("default.test_view").schema());
  }

  @Test
  public void testViewQueryWithUDF() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.foo_dali_udf",
        "tblproperties('functions' = 'LessThanHundred:com.linkedin.spark.catalog.CoralTestUDF',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.0')", "AS",
        "SELECT default_foo_dali_udf_LessThanHundred(intCol) Col", "FROM default.t"));
    final StructType expectedSchema = new StructType().add("Col", "boolean");
    Assert.assertEquals(expectedSchema, spark.table("default.foo_dali_udf").schema());
    Assert.assertTrue(spark.sessionState()
        .catalog()
        .functionExists(FunctionIdentifier.apply("default_foo_dali_udf_lessthanhundred")));
  }
}