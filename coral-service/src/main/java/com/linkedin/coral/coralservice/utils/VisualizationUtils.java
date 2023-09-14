/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import java.io.File;
import java.util.UUID;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.incremental.RelNodeIncrementalTransformer;
import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;
import com.linkedin.coral.trino.trino2rel.TrinoToRelConverter;
import com.linkedin.coral.vis.VisualizationUtil;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class VisualizationUtils {

  public static File createImageDir() {
    return new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
  }

  public static RelNode incrementalRewrittenRelNode = null;

  public static UUID generateSqlNodeVisualization(String query, String fromLanguage, File imageDir,
      RewriteType rewriteType) {

    SqlNode sqlNode = null;
    if (fromLanguage.equalsIgnoreCase("trino")) {
      sqlNode = new TrinoToRelConverter(hiveMetastoreClient).toSqlNode(query);
    } else if (fromLanguage.equalsIgnoreCase("hive")) {
      sqlNode = new HiveToRelConverter(hiveMetastoreClient).toSqlNode(query);
    }

    if (incrementalRewrittenRelNode != null && rewriteType == RewriteType.INCREMENTAL) {
      // We want to instead generate the visualization of SqlNode2 of the RHS of Coral's translation
      sqlNode = new CoralRelToSqlNodeConverter().convert(incrementalRewrittenRelNode);
    }

    assert sqlNode != null;
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);

    UUID sqlNodeId = UUID.randomUUID();
    visualizationUtil.visualizeSqlNodeToFile(sqlNode, "/" + sqlNodeId + ".svg");

    return sqlNodeId;
  }

  public static UUID generateRelNodeVisualization(String query, String fromLanguage, File imageDir,
      RewriteType rewriteType) {
    RelNode relNode = null;
    if (fromLanguage.equalsIgnoreCase("trino")) {
      relNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
    } else if (fromLanguage.equalsIgnoreCase("hive")) {
      relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    }

    if (rewriteType == RewriteType.INCREMENTAL) {
      relNode = RelNodeIncrementalTransformer.convertRelIncremental(relNode);
      incrementalRewrittenRelNode = relNode;
    }

    assert relNode != null;
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);

    UUID relNodeID = UUID.randomUUID();
    visualizationUtil.visualizeRelNodeToFile(relNode, "/" + relNodeID + ".svg");

    return relNodeID;
  }
}
