/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static org.testng.Assert.*;


public class VisualizationUtilTest {
  private static final String CORAL_VISUALIZATION_TEST_DIR = "coral.visualization.test.dir";

  @Test
  public void testBasicQuery() {
    File imagesTempDir = new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
    HiveMscAdapter mscAdapter = createMscAdapter();
    HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imagesTempDir);
    visualizationUtil.visualizeCoralSqlNodeToFile(converter.toSqlNode("SELECT * FROM foo, bar WHERE a = 1"),
        "/test1.png");
    // For now we test the presence of the file.
    // In the future, we can add tests to validate the structure of the generated graph by inspecting the corresponding
    // JSON file.
    assertEquals(imagesTempDir.list().length, 1);
    imagesTempDir.delete();
  }

  private static HiveMscAdapter createMscAdapter() {
    try {
      InputStream hiveConfStream = SqlNodeVisualizationVisitor.class.getClassLoader().getResourceAsStream("hive.xml");
      HiveConf hiveConf = new HiveConf();
      hiveConf.set(CORAL_VISUALIZATION_TEST_DIR,
          System.getProperty("java.io.tmpdir") + "/coral/visualization/" + UUID.randomUUID());
      hiveConf.addResource(hiveConfStream);
      hiveConf.set("mapreduce.framework.name", "local");
      hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
      hiveConf.set("_hive.local.session.path", "/tmp/coral");
      return new HiveMscAdapter(Hive.get(hiveConf).getMSC());
    } catch (MetaException | HiveException e) {
      throw new RuntimeException("Could not initialize Hive Metastore Client Adapter:" + e);
    }
  }
}
