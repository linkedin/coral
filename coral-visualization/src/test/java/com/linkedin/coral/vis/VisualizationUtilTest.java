/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlVisitor;
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
  public void testRenderToFile() {
    String[] queries = new String[] {
        "SELECT * FROM foo, bar WHERE a = 1",
        "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value"
    };
    File imagesTempDir = new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
    HiveMscAdapter mscAdapter = createMscAdapter();
    HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imagesTempDir);
    for (int i = 0; i < queries.length; i++) {
      visualizationUtil.visualizeCoralSqlNodeToFile(converter.toSqlNode(queries[i]), "/test" + i + ".png");
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
    HiveMscAdapter mscAdapter = createMscAdapter();
    HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
    String jsonString = convertToGraphVisJsonString(converter.toSqlNode(s));
    Gson gson = new Gson();
    return gson.fromJson(jsonString, JsonObject.class);
  }

  private static String convertToGraphVisJsonString(SqlNode sqlNode) {
    SqlVisitor<Node> sqlVisitor = new SqlNodeVisualizationVisitor();
    Node node = sqlNode.accept(sqlVisitor);
    Graph graph =
        Factory.graph("test").directed().nodeAttr().with(Font.size(10)).linkAttr().with(Font.size(10)).with(node);
    return Graphviz.fromGraph(graph).width(1000).render(Format.JSON).toString();
  }

  private static boolean jsonLabelsExist(Object jsonObject, String... labels) {
    String[] keys = new String[labels.length];
    Arrays.fill(keys, "label");
    return jsonKeyValuePairsExist(jsonObject, keys, labels);
  }

  private static boolean jsonKeyValuePairsExist(Object object, String[] keys, String[] values) {
    for (int i = 0; i < keys.length; i++) {
      if (!jsonKeyValueExists(object, keys[i], values[i])) {
        return false;
      }
    }
    return true;
  }

  private static boolean jsonKeyValueExists(Object object, String key, String value) {
    if (object instanceof JsonObject) {
      JsonObject jsonObject = (JsonObject) object;
      if (jsonObject.has(key)) {
        if (jsonObject.get(key).getAsString().equals(value)) {
          return true;
        }
      } else {
        for (Map.Entry entry : jsonObject.entrySet()) {
          if (jsonKeyValueExists(entry.getValue(), key, value)) {
            return true;
          }
        }
      }
    } else if (object instanceof JsonArray) {
      JsonArray jsonArray = (JsonArray) object;
      for (Object element : jsonArray) {
        if (jsonKeyValueExists(element, key, value)) {
          return true;
        }
      }
    }
    return false;
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
