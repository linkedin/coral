/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.ir;

import java.util.List;
import java.util.Map;

import org.apache.calcite.runtime.Hook;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;


public class CoralSqlNodeToRelConverter extends HiveToRelConverter {

  public CoralSqlNodeToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
    super(hiveMetastoreClient);
  }

  public CoralSqlNodeToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
    super(localMetaStore);
  }

  @Override
  public RelBuilder getRelBuilder() {
    // Turn off Rel simplification. Rel simplification can statically interpret boolean conditions in
    // OR, AND, CASE clauses and simplify those. This has two problems:
    // 1. Our type system is not perfect replication of Hive so this can be incorrect
    // 2. Converted expression is harder to validate for correctness(because it appears different from input)
    if (relBuilder == null) {
      Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
      relBuilder = CoralIRRelBuilder.create(config);
    }
    return relBuilder;
  }

  @Override
  protected SqlNode toSqlNode(String sql, Table hiveView) {
    throw new RuntimeException("unsupported operation");
  }
}
