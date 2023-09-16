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

  public static File createImageDir() {
    return new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
  }

  public boolean isValidFromLanguage(String fromLanguage) {
    return fromLanguage.equalsIgnoreCase("trino") || fromLanguage.equalsIgnoreCase("hive");
  }

  public ArrayList<UUID> generateIRVisualizations(String query, String fromLanguage, File imageDir,
      RewriteType rewriteType) {
    ArrayList<UUID> imageIDList = new ArrayList<>();

    // Always generate the pre/no rewrite images first
    RelNodeAndID relNodeAndID = generateRelNodeVisualization(getRelNode(query, fromLanguage), imageDir);
    imageIDList.add(relNodeAndID.id);

    SqlNodeAndID sqlNodeAndID = generateSqlNodeVisualization(getSqlNode(query, fromLanguage), imageDir);
    imageIDList.add(sqlNodeAndID.id);

    // Generate rewritten IR images if requested, otherwise, simply return the non rewritten image ids
    RelNode preRewriteRelNode = relNodeAndID.relNode;
    RelNode postRewriteRelNode;

    if (rewriteType != RewriteType.NONE && rewriteType != null) {
      switch (rewriteType) {
        case INCREMENTAL:
          postRewriteRelNode = RelNodeIncrementalTransformer.convertRelIncremental(preRewriteRelNode);
          break;
        case DATAMASKING:
        default:
          return imageIDList;
      }
      RelNodeAndID postRewroteRelNodeAndID = generateRelNodeVisualization(postRewriteRelNode, imageDir);
      imageIDList.add(postRewroteRelNodeAndID.id);

      SqlNode postRewriteSqlNode = new CoralRelToSqlNodeConverter().convert(postRewriteRelNode);
      SqlNodeAndID postRewriteSqlNodeAndID = generateSqlNodeVisualization(postRewriteSqlNode, imageDir);
      imageIDList.add(postRewriteSqlNodeAndID.id);
    }

    return imageIDList;
  }

  private SqlNodeAndID generateSqlNodeVisualization(SqlNode sqlNode, File imageDir) {
    // Generate graphviz svg using sqlNode
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);
    UUID sqlNodeId = UUID.randomUUID();
    visualizationUtil.visualizeSqlNodeToFile(sqlNode, "/" + sqlNodeId + ".svg");

    return new SqlNodeAndID(sqlNodeId, sqlNode);
  }

  private RelNodeAndID generateRelNodeVisualization(RelNode relNode, File imageDir) {
    // Generate graphviz svg using relNode
    VisualizationUtil visualizationUtil = VisualizationUtil.create(imageDir);
    UUID relNodeID = UUID.randomUUID();
    visualizationUtil.visualizeRelNodeToFile(relNode, "/" + relNodeID + ".svg");

    return new RelNodeAndID(relNodeID, relNode);
  }

  private RelNode getRelNode(String query, String fromLanguage) {
    RelNode relNode = null;
    if (fromLanguage.equalsIgnoreCase("trino")) {
      relNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
    } else if (fromLanguage.equalsIgnoreCase("hive")) {
      relNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    }

    return relNode;
  }

  private SqlNode getSqlNode(String query, String fromLanguage) {
    SqlNode sqlNode = null;
    if (fromLanguage.equalsIgnoreCase("trino")) {
      sqlNode = new TrinoToRelConverter(hiveMetastoreClient).toSqlNode(query);
    } else if (fromLanguage.equalsIgnoreCase("hive")) {
      sqlNode = new HiveToRelConverter(hiveMetastoreClient).toSqlNode(query);
    }

    return sqlNode;
  }

  private class RelNodeAndID {
    private UUID id;
    private RelNode relNode;

    public RelNodeAndID(UUID id, RelNode relNode) {
      this.id = id;
      this.relNode = relNode;
    }
  }

  private class SqlNodeAndID {
    private UUID id;
    private SqlNode sqlNode;

    public SqlNodeAndID(UUID id, SqlNode sqlNode) {
      this.id = id;
      this.sqlNode = sqlNode;
    }
  }
}
