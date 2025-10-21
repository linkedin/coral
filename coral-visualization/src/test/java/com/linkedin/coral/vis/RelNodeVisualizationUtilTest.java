/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import static com.linkedin.coral.vis.TestUtils.*;
import static org.testng.Assert.*;


public class RelNodeVisualizationUtilTest {
  private static final String CORAL_VISUALIZATION_TEST_DIR = "coral.visualization.test.dir";
  private HiveConf conf;
  private HiveToRelConverter converter;
  private static boolean graphvizUnavailable;

  @BeforeClass
  public void setup() {
    conf = getHiveConf();
    String testDir = conf.get(CORAL_VISUALIZATION_TEST_DIR);
    try {
      FileUtils.deleteDirectory(new File(testDir));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    run(driver, String.join("\n", "", "CREATE DATABASE IF NOT EXISTS test"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.foo (a INT, b STRING)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.bar (c INT, d STRING)"));
    converter = new HiveToRelConverter(createMscAdapter(conf));

    // Skip by default unless explicitly enabled
    if (!Boolean.getBoolean("coral.enable.graphviz.tests")) {
      graphvizUnavailable = true;
      throw new SkipException("Graphviz tests disabled. Enable with -Dcoral.enable.graphviz.tests=true");
    }

    // Probe Graphviz engine availability (some environments lack native J2V8)
    try {
      Node n = Factory.node("probe");
      Graph g = Factory.graph("probe").with(n);
      // Rendering to a string triggers engine load
      Graphviz.fromGraph(g).render(Format.SVG).toString();
      graphvizUnavailable = false;
    } catch (Throwable t) {
      graphvizUnavailable = true;
      throw new SkipException("Graphviz engine not available on this platform; skipping visualization tests");
    }
  }

  @AfterClass
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_VISUALIZATION_TEST_DIR)));
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

  @Test(enabled = false) // Disabled due to UnsatisfiedLinkError with native library dependencies
  public void testRenderToFile() {
    if (graphvizUnavailable) {
      throw new SkipException("Skipping visualization render test: Graphviz engine not available on this platform");
    }
    String[] queries =
        new String[] { "SELECT * FROM test.foo JOIN test.bar ON a = c", "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value" };
    File imagesTempDir = new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imagesTempDir);
    try {
      for (int i = 0; i < queries.length; i++) {
        visualizationUtil.visualizeRelNodeToFile(converter.convertSql(queries[i]), "/test" + i + ".svg");
      }
      assertEquals(imagesTempDir.list().length, 2);
    } catch (NoClassDefFoundError | UnsatisfiedLinkError e) {
      throw new SkipException("Skipping visualization render test due to missing native/class dependencies: " + e);
    } finally {
      imagesTempDir.delete();
    }
  }
}
