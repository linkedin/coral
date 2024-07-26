/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.incremental.RelNodeIncrementalTransformer;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;
import com.linkedin.coral.trino.trino2rel.TrinoToRelConverter;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class IncrementalUtils {
  public static String getIncrementalQuery(String query, String sourceLanguage, String targetLanguage) {
    RelNode originalNode;

    switch (sourceLanguage.toLowerCase()) {
      case "trino":
        originalNode = new TrinoToRelConverter(hiveMetastoreClient).convertSql(query);
        break;
      case "hive":
      default:
        originalNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
        break;
    }

    RelNode incrementalRelNode = new RelNodeIncrementalTransformer().convertRelIncremental(originalNode);

    switch (targetLanguage.toLowerCase()) {
      case "trino":
      default:
        return new RelToTrinoConverter(hiveMetastoreClient).convert(incrementalRelNode);
      case "spark":
        CoralSpark coralSpark = CoralSpark.create(incrementalRelNode, hiveMetastoreClient);
        return coralSpark.getSparkSql();
    }

  }

}
