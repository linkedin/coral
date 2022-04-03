/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.io.*;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.schema.avro.ViewToAvroSchemaConverter;


public class TestUtils {

  public static final String CORAL_SPARK_TEST_DIR = "coral.spark.test.dir";
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  static HiveToRelConverter hiveToRelConverter;
  static ViewToAvroSchemaConverter viewToAvroSchemaConverter;

  static void run(Driver driver, String sql) {
    while (true) {
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  public static void initializeViews(HiveConf conf) throws HiveException, MetaException, IOException {
    String testDir = conf.get(CORAL_SPARK_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    HiveMetastoreClient hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    run(driver, "CREATE TABLE IF NOT EXISTS foo(a int, b varchar(30), c double)");
    run(driver, "CREATE TABLE IF NOT EXISTS bar(x int, y double)");
    run(driver,
        "CREATE TABLE IF NOT EXISTS complex(a int, b string, c array<double>, s struct<name:string, age:int>, m map<string, int>, sarr array<struct<name:string, age:int>>)");

    String baseComplexSchema = loadSchema("base-complex.avsc");
    executeCreateTableQuery(driver, "default", "basecomplex", baseComplexSchema);

    run(driver,
        "CREATE FUNCTION default_foo_dali_udf_LessThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF'");
    run(driver,
        "CREATE FUNCTION default_foo_dali_udf2_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver,
        "CREATE FUNCTION default_foo_dali_udf3_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver,
        "CREATE FUNCTION default_foo_dali_udf3_FuncSquare as 'com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare'");
    run(driver,
        "CREATE FUNCTION default_foo_dali_udf4_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver,
        "CREATE FUNCTION default_foo_dali_udf5_UnsupportedUDF as 'com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF'");
    run(driver,
        "create function default_foo_lateral_udtf_CountOfRow as 'com.linkedin.coral.hive.hive2rel.CoralTestUDTF'");
    run(driver,
        "create function default_foo_duplicate_udf_LessThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF'");

    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_view", "AS", "SELECT b AS bcol, sum(c) AS sum_c",
        "FROM foo", "GROUP BY b"));
    run(driver, "DROP VIEW IF EXISTS foo_v1");
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_v1 ", "AS ",
            "SELECT DATE '2013-01-01', '2017-08-22 01:02:03', CAST(123 AS SMALLINT), CAST(123 AS TINYINT) ", "FROM foo",
            "LIMIT 1"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_bar_view", "AS", "SELECT foo_view.bcol, bar.x",
        "FROM foo_view JOIN bar", "ON bar.y = foo_view.sum_c"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_dali_udf",
            "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF',",
            "              'dependencies' = 'ivy://com.linkedin:udf:1.0')", "AS",
            "SELECT default_foo_dali_udf_LessThanHundred(a)", "FROM foo"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_dali_udf2",
            "tblproperties('functions' = 'GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
            "              'dependencies' = 'com.linkedin:udf:1.0')", "AS",
            "SELECT default_foo_dali_udf2_GreaterThanHundred(a)", "FROM foo"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_dali_udf3", "tblproperties('functions' = ",
        "'FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.1 ivy://com.linkedin:udf:1.0')", "AS",
        "SELECT default_foo_dali_udf3_FuncSquare(a), default_foo_dali_udf3_GreaterThanHundred(a) ", "FROM foo"));
    // foo_dali_udf4 is same as foo_dali_udf2, except we add extra space in dependencies parameter
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_dali_udf4",
            "tblproperties('functions' = 'GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
            "              'dependencies' = '  ivy://com.linkedin:udf:1.0  ')", "AS",
            "SELECT default_foo_dali_udf4_GreaterThanHundred(a)", "FROM foo"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_dali_udf5",
            "tblproperties('functions' = 'UnsupportedUDF:com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF',",
            "              'dependencies' = 'com.linkedin:udf:1.0')", "AS",
            "SELECT default_foo_dali_udf5_UnsupportedUDF(a)", "FROM foo"));

    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_lateral_udtf",
            "tblproperties('functions' = 'CountOfRow:com.linkedin.coral.hive.hive2rel.CoralTestUDTF')", "AS",
            "SELECT a, t.col1 FROM complex LATERAL VIEW default_foo_lateral_udtf_CountOfRow(complex.a) t"));

    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS named_struct_view", "AS",
        "SELECT named_struct('abc', 123, 'def', 'xyz') AS named_struc", "FROM bar"));

    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS duplicate_column_name"));
    run(driver, "CREATE TABLE duplicate_column_name.tableA (some_id string)");
    run(driver, "CREATE TABLE duplicate_column_name.tableB (some_id string)");
    run(driver, "CREATE VIEW IF NOT EXISTS duplicate_column_name.view_namesake_column_names AS "
        + "SELECT a.some_id FROM duplicate_column_name.tableA a LEFT JOIN (SELECT trim(some_id) AS SOME_ID FROM duplicate_column_name.tableB) b ON a.some_id = b.some_id WHERE a.some_id != ''");

    // Views and tables used in FuzzyUnionViewTest
    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS fuzzy_union"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableA(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view", "AS",
        "SELECT * from fuzzy_union.tableA", "union all", "SELECT *", "from fuzzy_union.tableA"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_more_than_two_tables", "AS",
            "SELECT *", "from fuzzy_union.tableA", "union all", "SELECT *", "from fuzzy_union.tableA", "union all",
            "SELECT *", "from fuzzy_union.tableA"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_alias", "AS", "SELECT *", "FROM",
            "(SELECT * from fuzzy_union.tableA)", "as viewFirst", "union all", "SELECT *",
            "FROM (SELECT * from fuzzy_union.tableA)", "as viewSecond"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableB(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableC(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_single_branch_evolved", "AS",
        "SELECT *", "from fuzzy_union.tableB", "union all", "SELECT *", "from fuzzy_union.tableC"));
    run(driver, String.join("\n", "", "ALTER TABLE fuzzy_union.tableC CHANGE COLUMN b b struct<b1:string, b2:int>"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableD(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableE(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_same",
        "AS", "SELECT *", "from fuzzy_union.tableD", "union all", "SELECT *", "from fuzzy_union.tableE"));
    run(driver, String.join("\n", "", "ALTER TABLE fuzzy_union.tableD CHANGE COLUMN b b struct<b1:string, b2:int>"));
    run(driver, String.join("\n", "", "ALTER TABLE fuzzy_union.tableE CHANGE COLUMN b b struct<b1:string, b2:int>"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableF(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableG(a int, b struct<b1:string>)"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_different", "AS",
            "SELECT *", "from fuzzy_union.tableF", "union all", "SELECT *", "from fuzzy_union.tableG"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_more_than_two_branches_evolved", "AS",
            "SELECT *", "from fuzzy_union.tableF", "union all", "SELECT *", "from fuzzy_union.tableG", "union all",
            "SELECT *", "from fuzzy_union.tableF"));
    run(driver, String.join("\n", "", "ALTER TABLE fuzzy_union.tableF CHANGE COLUMN b b struct<b1:string, b3:string>"));
    run(driver, String.join("\n", "", "ALTER TABLE fuzzy_union.tableG CHANGE COLUMN b b struct<b1:string, b2:int>"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableH(a int, b map<string, struct<b1:string>>)"));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableI(a int, b map<string, struct<b1:string>>)"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_map_with_struct_value_evolved",
        "AS", "SELECT *", "from fuzzy_union.tableH", "union all", "SELECT *", "from fuzzy_union.tableI"));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableH CHANGE COLUMN b b map<string, struct<b1:string, b2:int>>"));

    run(driver,
        String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableJ(a int, b array<struct<b1:string>>)"));
    run(driver,
        String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableK(a int, b array<struct<b1:string>>)"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_array_with_struct_value_evolved", "AS",
            "SELECT *", "from fuzzy_union.tableJ", "union all", "SELECT *", "from fuzzy_union.tableK"));
    run(driver,
        String.join("\n", "", "ALTER TABLE fuzzy_union.tableJ CHANGE COLUMN b b array<struct<b1:string, b2:int>>"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableL(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)"));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableM(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)"));
    run(driver, String.join("\n", "", "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_deeply_nested_struct_evolved",
        "AS", "SELECT *", "from fuzzy_union.tableL", "union all", "SELECT *", "from fuzzy_union.tableM"));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableL CHANGE COLUMN b b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string, b6:string>>>"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableN(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS fuzzy_union.tableO(a int, b struct<b1:string>)"));
    run(driver,
        String.join("\n", "",
            "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_same_schema_evolution_with_different_ordering", "AS",
            "SELECT *", "from fuzzy_union.tableN", "union all", "SELECT *", "from fuzzy_union.tableO"));
    run(driver,
        String.join("\n", "", "ALTER TABLE fuzzy_union.tableN CHANGE COLUMN b b struct<b2:double, b1:string, b0:int>"));
    run(driver,
        String.join("\n", "", "ALTER TABLE fuzzy_union.tableO CHANGE COLUMN b b struct<b0:int, b1:string, b2:int>"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS schema_promotion(a int, b array<int>)"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS view_schema_promotion AS SELECT * from schema_promotion"));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS view_schema_promotion_wrapper AS SELECT * from view_schema_promotion"));
    run(driver, String.join("\n", "", "ALTER TABLE schema_promotion CHANGE COLUMN b b array<double>"));

    run(driver,
        "CREATE TABLE IF NOT EXISTS union_table(foo uniontype<int, double, array<string>, struct<a:int,b:string>>)");

    run(driver, "CREATE TABLE IF NOT EXISTS nested_union(a uniontype<int, struct<a:uniontype<int, double>, b:int>>)");

    run(driver, "CREATE VIEW IF NOT EXISTS view_expand_array_index AS SELECT c[1] c1 FROM default.complex");

    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS foo_duplicate_udf",
            "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF',",
            "              'dependencies' = 'ivy://com.linkedin:udf:1.0')", "AS",
            "SELECT default_foo_duplicate_udf_LessThanHundred(a), default_foo_duplicate_udf_LessThanHundred(a)",
            "FROM foo"));
  }

  private static void executeCreateTableQuery(Driver driver, String dbName, String tableName, String schema) {
    run(driver, "DROP TABLE IF EXISTS " + dbName + "." + tableName);
    run(driver,
        "CREATE EXTERNAL TABLE " + tableName + " " + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' "
            + "STORED AS " + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
            + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' " + "TBLPROPERTIES ('"
            + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static String loadSchema(String fileName) {
    InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(fileName);
    return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
  }

  public static RelNode toRelNode(String db, String view) {
    return hiveToRelConverter.convertView(db, view);
  }

  public static RelNode toRelNode(String sql) {
    return hiveToRelConverter.convertSql(sql);
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_SPARK_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/spark/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-spark");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/spark");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/spark");
    return hiveConf;
  }

  public static Schema getAvroSchemaForView(String db, String view, boolean lowercase) {
    return viewToAvroSchemaConverter.toAvroSchema(db, view, false, lowercase);
  }

  public static Schema getAvroSchemaForView(String sql, boolean lowercase) {
    return viewToAvroSchemaConverter.toAvroSchema(sql, false, lowercase);
  }

}
