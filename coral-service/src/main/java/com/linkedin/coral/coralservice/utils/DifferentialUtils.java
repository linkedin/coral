/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.differential.RelToDiffSqlConverter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


public class DifferentialUtils {

  public static String getModifiedQueryFromUserSql(String query) {
    RelNode originalNode = new HiveToRelConverter(hiveMetastoreClient).convertSql(query);
    String modifiedQuery = new RelToDiffSqlConverter().convert(originalNode);
    return modifiedQuery;
  }

}