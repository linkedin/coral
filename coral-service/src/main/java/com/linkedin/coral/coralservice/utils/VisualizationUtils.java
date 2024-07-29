/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import java.io.File;
import java.util.ArrayList;
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

  public static File getImageDir() {
    return new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
  }

  public ArrayList<UUID> generateIRVisualizations(String query, String sourceLanguage, File imageDir,
      RewriteType rewriteType) {
    ArrayList<UUID> imageIDList = new ArrayList<>();

    // Always generate the pre/no rewrite images first
    RelNode relNode = getRelNode(query, sourceLanguage);
    UUID relNodeID = generateRelNodeVisualization(relNode, imageDir);
    imageIDList.add(relNodeID);

    UUID sqlNodeID = generateSqlNodeVisualization(getSqlNode(query, sourceLanguage), imageDir);
    imageIDList.add(sqlNodeID);

    // Generate rewritten IR images if requested, otherwise, simply return the non rewritten image ids
    RelNode postRewriteRelNode;

    if (rewriteType != RewriteType.NONE && rewriteType != null) {
      // Pass in pre-rewrite rel node
      switch (rewriteType) {
        case INCREMENTAL:
          postRewriteRelNode = RelNodeIncrementalTransformer.convertRelIncremental(relNode);
          break;
        case DATAMASKING:
        default:
          return imageIDList;
      }
      UUID postRewroteRelNodeID = generateRelNodeVisualization(postRewriteRelNode, imageDir);
      imageIDList.add(postRewroteRelNodeID);

      SqlNode postRewriteSqlNode = new CoralRelToSqlNodeConverter().convert(postRewriteRelNode);
      UUID postRewriteSqlNodeID = generateSqlNodeVisualization(postRewriteSqlNode, imageDir);
      imageIDList.add(postRewriteSqlNodeID);
    }

    return imageIDList;
  }

  private UUID generateSqlNodeVisualization(SqlNode sqlNode, File imageDir) {
    // Generate graphviz svg using sqlNode
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);
    UUID sqlNodeId = UUID.randomUUID();
    visualizationUtil.visualizeSqlNodeToFile(sqlNode, "/" + sqlNodeId + ".svg");

    return sqlNodeId;
  }

  private UUID generateRelNodeVisualization(RelNode relNode, File imageDir) {
    // Generate graphviz svg using relNode
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);
    UUID relNodeID = UUID.randomUUID();
    visualizationUtil.visualizeRelNodeToFile(relNode, "/" + relNodeID + ".svg");

    return relNodeID;
  }

  private RelNode getRelNode(String query, String sourceLanguage) {
    RelNode relNode = null;
    if (sourceLanguage.equalsIgnoreCase("trino")) {
      relNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
    } else if (sourceLanguage.equalsIgnoreCase("hive") || sourceLanguage.equalsIgnoreCase("spark")) {
      relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    }

    return relNode;
  }

  private SqlNode getSqlNode(String query, String sourceLanguage) {
    SqlNode sqlNode = null;
    if (sourceLanguage.equalsIgnoreCase("trino")) {
      sqlNode = new TrinoToRelConverter(hiveMetastoreClient).toSqlNode(query);
    } else if (sourceLanguage.equalsIgnoreCase("hive") || sourceLanguage.equalsIgnoreCase("spark")) {
      sqlNode = new HiveToRelConverter(hiveMetastoreClient).toSqlNode(query);
    }

    return sqlNode;
  }
}
