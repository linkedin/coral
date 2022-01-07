/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.HashSet;


/**
 * [CORAL-75] Some dali UDFs get registered correctly in a SparkSession, and hence a DataFrame is successfully
 * created for the views containing those UDFs, but those UDFs fail going forward during the execution phase.
 * We cannot use a fallback mechanism for such cases because DaliSpark.createDataFrame runs successfully.
 * Because of this, we need to proactively fail during the CoralSpark view analysis phase when we encounter such udfs,
 * so that DaliSpark.createDataFrame can fall back to its stable execution.
 */
class UnsupportedHiveUDFsInSpark {
  private UnsupportedHiveUDFsInSpark() {
  }

  private static final HashSet<String> unsupportedUDFSet = new HashSet<>();

  /**
   * Add hive UDFs that do not work with Spark.
   */
  static {
    // Removing this blocklist in the long term can be tracked here: CORAL-78
    add("com.linkedin.dali.udf.userinterfacelookup.hive.UserInterfaceLookup");
    add("com.linkedin.dali.udf.portallookup.hive.PortalLookup");
  }

  /**
   * This API can be used for adding test UDFs
   */
  static void add(String udfName) {
    unsupportedUDFSet.add(udfName);
  }

  /**
   * This API is used for checking if a UDF is on the blocklist
   *
   * @param udfName Name of the udf, for example com.linkedin.coral.hive.hive2rel.CoralTestUnsupportedUDF
   * @return Boolean returns true if a UDF is on the blocklist
   */
  static Boolean contains(String udfName) {
    return unsupportedUDFSet.contains(udfName);
  }

}
