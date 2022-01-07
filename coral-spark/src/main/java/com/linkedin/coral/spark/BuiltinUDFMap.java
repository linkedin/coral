/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


/**
 * This is Mapping class for built in functions from Calcite IR to Spark
 *
 * The mapping is created statically and queried in CoralSpark's SparkRelToSparkSqlConverter
 * with the lookup API.
 */
class BuiltinUDFMap {
  private BuiltinUDFMap() {
  }

  private static final Map<String, String> UDF_MAP = new HashMap<>();

  /**
   * Add new built in function mappings here.
   */
  static {
    createUDFMapEntry(SqlStdOperatorTable.CARDINALITY, "size");
  }

  /**
   * This API is used for querying Spark-specific built in function for a coral IR function
   *
   * @return Optional<String>
   *   String will be null if mapping is not found, and can be confirmed with Optional.isPresent()
   */
  static Optional<String> lookup(String calciteOpName) {
    return Optional.ofNullable(UDF_MAP.get(calciteOpName));
  }

  private static void createUDFMapEntry(SqlOperator calciteOp, String hiveUDFName) {
    BuiltinUDFMap.UDF_MAP.put(calciteOp.getName(), hiveUDFName);
  }

}
