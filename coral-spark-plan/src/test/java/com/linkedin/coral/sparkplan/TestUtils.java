/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.sparkplan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;


public class TestUtils {

  public static final String CORAL_SPARKPLAN_TEST_DIR = "coral.sparkplan.test.dir";

  static TestHive hive;

  public static class TestHive {
    private final HiveConf conf;
    List<DB> databases;

    public TestHive(HiveConf conf) {
      this.conf = conf;
    }

    public HiveConf getConf() {
      return conf;
    }

    public static class DB {
      DB(String name, Iterable<String> tables) {
        this.name = name;
        this.tables = ImmutableList.copyOf(tables);
      }
      final String name;
      final List<String> tables;
    }

    public List<String> getDbNames() {
      return databases.stream().map(db -> db.name).collect(Collectors.toList());
    }

    public List<String> getTables(String db) {
      return databases.stream().filter(d -> d.name.equals(db)).findFirst()
          .orElseThrow(() -> new RuntimeException("DB " + db + " not found")).tables;
    }

    public IMetaStoreClient getMetastoreClient() throws HiveException, MetaException {
      return Hive.get(conf).getMSC();
    }
  }

  public static TestHive setupDefaultHive(HiveConf conf) throws IOException {
    if (hive != null) {
      return hive;
    }
    String testDir = conf.get(CORAL_SPARKPLAN_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    TestHive testHive = new TestHive(conf);
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    try {
      driver.run("DROP TABLE IF EXISTS test.airport");
      driver.run("DROP DATABASE IF EXISTS test CASCADE");
      driver.run("CREATE DATABASE IF NOT EXISTS test");
      driver.run(
          "CREATE TABLE IF NOT EXISTS test.airport(name string, country string, area_code int, code string, datepartition string)");
      hive = testHive;
      return hive;
    } catch (Exception e) {
      throw new RuntimeException("Failed to setup database", e);
    }
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_SPARKPLAN_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/sparkplan/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
    hiveConf.set("_hive.local.session.path", "/tmp/coral");
    return hiveConf;
  }
}
