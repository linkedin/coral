/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;

import com.linkedin.coral.common.HiveMscAdapter;

import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;


public class TestUtils {
  static final String CORAL_VISUALIZATION_TEST_DIR = "coral.visualization.test.dir";

  static HiveConf getHiveConf() {
    InputStream hiveConfStream = SqlNodeVisualizationVisitor.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_VISUALIZATION_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/visualization/" + UUID.randomUUID());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
    hiveConf.set("_hive.local.session.path", "/tmp/coral");
    return hiveConf;
  }

  static HiveMscAdapter createMscAdapter(HiveConf conf) {
    try {
      return new HiveMscAdapter(Hive.get(conf).getMSC());
    } catch (MetaException | HiveException e) {
      throw new RuntimeException("Could not initialize Hive Metastore Client Adapter:" + e);
    }
  }

  static String convertToGraphVisJsonString(SqlNode sqlNode) {
    SqlVisitor<Node> sqlVisitor = new SqlNodeVisualizationVisitor();
    Node node = sqlNode.accept(sqlVisitor);
    Graph graph =
        Factory.graph("test").directed().nodeAttr().with(Font.size(10)).linkAttr().with(Font.size(10)).with(node);
    return Graphviz.fromGraph(graph).width(1000).render(Format.JSON).toString();
  }

  static boolean jsonLabelsExist(Object jsonObject, String... labels) {
    String[] keys = new String[labels.length];
    Arrays.fill(keys, "label");
    return jsonKeyValuePairsExist(jsonObject, keys, labels);
  }

  static boolean jsonKeyValuePairsExist(Object object, String[] keys, String[] values) {
    for (int i = 0; i < keys.length; i++) {
      if (!jsonKeyValueExists(object, keys[i], values[i])) {
        return false;
      }
    }
    return true;
  }

  static boolean jsonKeyValueExists(Object object, String key, String value) {
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

}
