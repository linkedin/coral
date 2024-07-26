/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.vis.TestUtils.*;
import static org.testng.Assert.*;


public class SqlNodeVisualizationUtilTest {
  private HiveConf conf;
  HiveToRelConverter converter;

  @BeforeClass
  public void setup() {
    conf = getHiveConf();
    converter = new HiveToRelConverter(createMscAdapter(conf));
  }

  @AfterClass
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(CORAL_VISUALIZATION_TEST_DIR)));
  }

  @Test
  public void testRenderToFile() {
    String[] queries =
        new String[] { "SELECT * FROM foo, bar WHERE a = 1", "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value" };
    File imagesTempDir = new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imagesTempDir);
    for (int i = 0; i < queries.length; i++) {
      visualizationUtil.visualizeSqlNodeToFile(converter.toSqlNode(queries[i]), "/test" + i + ".svg");
    }
    assertEquals(imagesTempDir.list().length, 2);
    imagesTempDir.delete();
  }

  @Test
  public void testBasicQueryJson() {
    JsonObject jsonObject = getJsonObject("SELECT * FROM foo, bar WHERE a = 1");
    assertTrue(jsonLabelsExist(jsonObject, "SELECT", "JOIN", "=", "LIST", "foo", "bar", "a", "1", "*"));
  }

  @Test
  public void testLateralJoinQueryJson() {
    JsonObject jsonObject = getJsonObject(
        "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value");
    assertTrue(jsonLabelsExist(jsonObject, "SELECT", "JOIN", "LATERAL", "UNNEST", "AS", "MAP", "LIST"));
  }

  private JsonObject getJsonObject(String s) {
    String jsonString = convertToGraphVisJsonString(converter.toSqlNode(s));
    Gson gson = new Gson();
    return gson.fromJson(jsonString, JsonObject.class);
  }
}
