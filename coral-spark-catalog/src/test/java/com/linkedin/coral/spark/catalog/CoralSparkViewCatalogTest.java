/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.catalog;

import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.internal.SQLConf;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static org.apache.calcite.sql.type.OperandTypes.*;
import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


public class CoralSparkViewCatalogTest {

  private SparkSession spark = null;

  @BeforeClass
  public void setup() {
    TestHiveMetastore metastore = new TestHiveMetastore();
    HiveConf hiveConf = metastore.hiveConf();
    spark = SparkSession.builder().master("local[2]").config(SQLConf.PARTITION_OVERWRITE_MODE().key(), "dynamic")
        .config("spark.hadoop." + METASTOREURIS.varname, hiveConf.get(METASTOREURIS.varname)).enableHiveSupport()
        .getOrCreate();
    // set CoralSparkView catalog as the default catalog
    spark.conf().set("spark.sql.catalog.spark_catalog", CoralSparkViewCatalog.class.getName());
    spark.sql("CREATE NAMESPACE IF NOT EXISTS default");
    spark.sql(
        "CREATE TABLE IF NOT EXISTS default.t (intCol int, structCol struct<doubleCol:double,stringCol:string>, boolCol boolean) STORED AS ORC TBLPROPERTIES ('write.format.default'='avro')");
    spark
        .sql("CREATE FUNCTION default_foo_dali_udf_LessThanHundred as 'com.linkedin.coral.spark.catalog.CoralTestUDF'");
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.spark.catalog.CoralTestUDF",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
  }

  @Test
  public void testView() {
    spark.sql("CREATE OR REPLACE VIEW default.test_view AS SELECT * FROM default.t");
    System.out.println(spark.sql("select * from default.test_view").schema());
    spark.sql("DROP VIEW default.test_view");
  }

  @Test
  public void testUDF() {
    spark.sql(String.join("\n", "CREATE OR REPLACE VIEW default.foo_dali_udf",
        "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.spark.catalog.CoralTestUDF',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.0')", "AS",
        "SELECT default_foo_dali_udf_LessThanHundred(intCol) col", "FROM default.t"));
    System.out.println(spark.table("default.foo_dali_udf").schema());
    Assert.assertTrue(spark.sessionState().catalog()
        .functionExists(FunctionIdentifier.apply("default_foo_dali_udf_lessthanhundred")));
  }
}
