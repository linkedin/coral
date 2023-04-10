/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.incremental.RelNodeIncrementalTransformer;
import com.linkedin.coral.spark.CoralSpark;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class IncrementalUtils {

  public static String getSparkIncrementalQueryFromUserSql(String query) {
    RelNode originalNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    RelNode incrementalRelNode = RelNodeIncrementalTransformer.convertRelIncremental(originalNode);
    CoralSpark coralSpark = CoralSpark.create(incrementalRelNode);
    return coralSpark.getSparkSql();
  }

}
