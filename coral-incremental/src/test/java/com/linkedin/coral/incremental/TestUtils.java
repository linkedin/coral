/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;


public class TestUtils {

  public static final String CORAL_INCREMENTAL_TEST_DIR = "coral.incremental.test.dir";

  private static HiveMscAdapter hiveMetastoreClient;
  public static HiveToRelConverter hiveToRelConverter;

  static void run(Driver driver, String sql) {
    while (true) {
      try {
        CommandProcessorResponse result = driver.run(sql);
        if (result.getException() != null) {
          throw new RuntimeException("Execution failed for: " + sql, result.getException());
        }
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  public static void initializeViews(HiveConf conf) throws HiveException, MetaException, IOException {
    String testDir = conf.get(CORAL_INCREMENTAL_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);

    run(driver, "CREATE DATABASE IF NOT EXISTS test");

    run(driver, "CREATE TABLE IF NOT EXISTS test.foo(a int, b varchar(30), c double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.bar1(x int, y double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.bar2(x int, y double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.bar3(x int, y double)");

    run(driver, "CREATE TABLE IF NOT EXISTS test.alpha(a1 int, a2 double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.beta(b1 int, b2 double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.gamma(g1 int, g2 double)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.epsilon(e1 int, e2 double)");
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_INCREMENTAL_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/incremental/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-incremental");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/incremental");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/incremental");
    return hiveConf;
  }

}
