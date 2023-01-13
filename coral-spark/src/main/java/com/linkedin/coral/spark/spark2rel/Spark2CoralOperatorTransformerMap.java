/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

import java.util.HashMap;
import java.util.Map;

import static com.linkedin.coral.spark.spark2rel.Spark2CoralOperatorTransformerMapUtils.getKey;


public class Spark2CoralOperatorTransformerMap {
  private Spark2CoralOperatorTransformerMap() {
  }

  public static final Map<String, OperatorTransformer> TRANSFORMER_MAP = new HashMap<>();

  static {
    // TODO: keep adding Spark-Specific functions as needed
  }

  /**
   * Gets SparkCalciteOperatorTransformer for a given Spark SQL Operator.
   *
   * @param sparkOpName Name of Spark SQL operator
   * @param numOperands Number of operands
   * @return {@link OperatorTransformer} object
   */
  public static OperatorTransformer getOperatorTransformer(String sparkOpName, int numOperands) {
    return TRANSFORMER_MAP.get(getKey(sparkOpName, numOperands));
  }
}
