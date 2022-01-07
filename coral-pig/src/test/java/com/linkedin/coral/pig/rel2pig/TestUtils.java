/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.Hook;
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


/**
 * Provides utility functions used in unit tests
 */
public class TestUtils {

  public static final String CORAL_PIG_TEST_DIR = "coral.pig.test.dir";

  static final String TEST_JSON_FILE_DIR = "src/test/resources/data";
  static HiveToRelConverter hiveToRelConverter;

  private TestUtils() {
  }

  /**
   * Initializes the tables used in Coral-Pig unit tests
   * @throws HiveException
   * @throws MetaException
   */
  public static void initializeViews(HiveConf conf) throws HiveException, MetaException, IOException {
    String testDir = conf.get(CORAL_PIG_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    HiveMetastoreClient hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);

    // Views and tables used in unit tests
    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS pig"));

    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS functions"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tableA(a int, b int, c int)"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tableB(a int, b int)"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tableLeft(a int, b int, c int)"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tableRight(d int, e int)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tablestruct(a int, b struct<b0:int>, c struct<c0:struct<c00:int>>)"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tablemap(m1 map<string,int>)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tablecast(i int, bi bigint, fl float, do double, str string, boo boolean)"));

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS pig.tablenull(nullablefield string, field string)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS functions.tablefields(i_1 int, i0 int, i1 int, i2 int, i3 int, fl1 double, fl2 double, fl3 double, str string, substr string, exstr string, bootrue boolean, boofalse boolean, bin binary)"));
  }

  /**
   * Turns off optimizations used in the transformation of Calcite SQL to Relational Algebra
   */
  public static void turnOffRelSimplification() {
    // Rel simplification can statically interpret boolean conditions in
    // OR, AND, CASE clauses and simplify those. This has two problems:
    // 1. Our type system is not perfect replication of Hive so this can be incorrect
    // 2. Converted expression is harder to validate for correctness(because it appears different from input)
    Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
  }

  /**
   * Translates the view definition of a SQL query to Pig Latin
   * @param sql A SQL query.
   * @param outputRelation The variable that the output of the table will be stored in
   * @return Ordered list of Pig Latin statements to read the records produced by the view definition of the
   *         given database.table stored in outputRelation
   */
  public static String[] sqlToPigLatin(String sql, String outputRelation) {
    RelNode relNode = hiveToRelConverter.convertSql(sql);

    PigLoadFunction pigLoadFunction = (String db, String t) -> {

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableA")) {
        return "JsonLoader('a:int, b:int, c:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableB")) {
        return "JsonLoader('a:int, b:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableLeft")) {
        return "JsonLoader('a:int, b:int, c:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableRight")) {
        return "JsonLoader('d:int, e:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tablestruct")) {
        return "JsonLoader('a:int, b:(b0:int), c:(c0:(c00:int))')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tablemap")) {
        return "JsonLoader('m1:map[int]')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tablecast")) {
        return "JsonLoader('i:int, bi:long, fl:float, do:double, str:chararray, boo:boolean')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tablenull")) {
        return "JsonLoader('nullablefield:chararray, field:chararray')";
      }

      if (db.equalsIgnoreCase("functions") && t.equalsIgnoreCase("tablefields")) {
        return "JsonLoader('i_1:int, i0:int, i1:int, i2:int, i3:int, fl1:double, fl2:double, fl3:double, str:chararray, substr:chararray, exstr:chararray, bootrue:boolean, boofalse:boolean, bin:bytearray')";
      }

      return "JsonLoader()";
    };

    TableToPigPathFunction tableToPigPathFunction =
        (String db, String t) -> String.format("%s/%s/%s.json", TEST_JSON_FILE_DIR, db, t);

    RelToPigLatinConverter converter = new RelToPigLatinConverter(pigLoadFunction, tableToPigPathFunction);
    String pigScript = converter.convert(relNode, outputRelation);
    return pigScript.split("\n");
  }

  private static void run(Driver driver, String sql) {
    while (true) {
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_PIG_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/pig/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-pig");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/pig");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/pig");
    return hiveConf;
  }
}
