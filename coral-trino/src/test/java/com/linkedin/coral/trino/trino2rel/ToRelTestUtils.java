/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

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


public class ToRelTestUtils {
  public static final String CORAL_FROM_TRINO_TEST_DIR = "coral.trino.test.dir";

  private static HiveMscAdapter hiveMetastoreClient;
  public static TrinoToRelConverter trinoToRelConverter;

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
    String testDir = conf.get(CORAL_FROM_TRINO_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    trinoToRelConverter = new TrinoToRelConverter(hiveMetastoreClient);

    // Views and tables used in TrinoToTrinoConverterTest
    run(driver, "CREATE DATABASE IF NOT EXISTS default");
    run(driver, "CREATE TABLE IF NOT EXISTS default.foo(show int, a int, b int, x date, y date)");
    run(driver, "CREATE TABLE IF NOT EXISTS default.my_table(x array<int>, y array<array<int>>, z int)");
    run(driver, "CREATE TABLE IF NOT EXISTS default.a(b int, id int, x int)");
    run(driver, "CREATE TABLE IF NOT EXISTS default.b(foobar int, id int, y int)");
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = ToRelTestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_FROM_TRINO_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/trino/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-trino");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/trino");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/trino");
    return hiveConf;
  }
}
