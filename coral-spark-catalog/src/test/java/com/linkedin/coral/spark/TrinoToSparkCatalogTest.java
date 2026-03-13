/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.internal.SQLConf;
import org.apache.spark.sql.types.StructType;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.trino.trino2rel.TrinoToRelConverter;

import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


/**
 * Tests the Trino SQL -> Coral IR -> Spark SQL translation pipeline.
 *
 * Trino SQL is parsed via {@link TrinoToRelConverter} into a Calcite RelNode,
 * then translated to Spark SQL via {@link CoralSpark}, and finally executed
 * through a Spark session to validate the resulting schema.
 *
 * Note: TrinoToRelConverter uppercases SQL before parsing, so column aliases
 * from explicit projections will be uppercased in the output schema.
 */
public class TrinoToSparkCatalogTest {

  private static SparkSession spark;
  private static HiveMscAdapter hmsAdapter;
  private static TrinoToRelConverter trinoToRelConverter;

  @BeforeSuite
  static void setup() throws MetaException {
    final TestHiveMetastore metastore = TestHiveMetastore.INSTANCE;
    HiveConf hiveConf = metastore.hiveConf();
    spark = SparkSession.builder().master("local[2]").config(SQLConf.PARTITION_OVERWRITE_MODE().key(), "dynamic")
        .config("spark.hadoop." + METASTOREURIS.varname, hiveConf.get(METASTOREURIS.varname)).enableHiveSupport()
        .getOrCreate();
    spark.sql("CREATE NAMESPACE IF NOT EXISTS default");

    HiveMetaStoreClient msc = metastore.getMetastoreClient();
    hmsAdapter = new HiveMscAdapter(msc);
    trinoToRelConverter = new TrinoToRelConverter(hmsAdapter);
  }

  @AfterSuite
  static void clean() {
    spark.close();
  }

  @BeforeTest
  void beforeEach() {
    spark.sql("CREATE TABLE IF NOT EXISTS default.foo (a int, b string, c double)");
    spark.sql("CREATE TABLE IF NOT EXISTS default.bar (x int, y string)");
    spark.sql(
        "CREATE TABLE IF NOT EXISTS default.complex_table (id int, arr array<int>, m map<string,int>, s struct<name:string,age:int>)");
  }

  @AfterTest
  void afterEach() {
    spark.sql("DROP TABLE IF EXISTS default.foo");
    spark.sql("DROP TABLE IF EXISTS default.bar");
    spark.sql("DROP TABLE IF EXISTS default.complex_table");
  }

  private Dataset<Row> executeTrinoSqlViaSpark(String trinoSql) {
    RelNode relNode = trinoToRelConverter.convertSql(trinoSql);
    CoralSpark coralSpark = CoralSpark.create(relNode, hmsAdapter);
    String sparkSql = coralSpark.getSparkSql();
    return spark.sql(sparkSql);
  }

  @Test
  public void testSimpleSelect() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT a, b FROM foo");
    StructType expectedSchema = new StructType().add("A", "int").add("B", "string");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testSelectStar() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT * FROM foo");
    StructType expectedSchema = new StructType().add("a", "int").add("b", "string").add("c", "double");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testJoin() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT foo.a, bar.y FROM foo JOIN bar ON foo.a = bar.x");
    StructType expectedSchema = new StructType().add("A", "int").add("Y", "string");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testSubquery() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT sub.a, sub.c FROM (SELECT a, c FROM foo) sub");
    StructType expectedSchema = new StructType().add("A", "int").add("C", "double");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testUnion() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT a, b FROM foo UNION ALL SELECT x, y FROM bar");
    StructType expectedSchema = new StructType().add("A", "int").add("B", "string");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testUnnest() {
    Dataset<Row> result =
        executeTrinoSqlViaSpark("SELECT id, t.col FROM complex_table CROSS JOIN UNNEST(arr) AS t(col)");
    StructType expectedSchema = new StructType().add("ID", "int").add("COL", "int");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testCast() {
    Dataset<Row> result =
        executeTrinoSqlViaSpark("SELECT CAST(a AS BIGINT) AS a_long, CAST(c AS VARCHAR) AS c_str FROM foo");
    StructType expectedSchema = new StructType().add("A_LONG", "long").add("C_STR", "string");
    Assert.assertEquals(result.schema(), expectedSchema);
  }

  @Test
  public void testStructFieldAccess() {
    Dataset<Row> result = executeTrinoSqlViaSpark("SELECT id, s.name, s.age FROM complex_table");
    StructType expectedSchema = new StructType().add("ID", "int").add("NAME", "string").add("AGE", "int");
    Assert.assertEquals(result.schema(), expectedSchema);
  }
}
