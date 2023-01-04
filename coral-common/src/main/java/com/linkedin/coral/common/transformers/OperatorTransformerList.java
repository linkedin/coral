/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.sql.SqlCall;


/**
 * Container for OperatorTransformers
 */
public class OperatorTransformerList {
  private final List<OperatorTransformer> operatorTransformers;

  OperatorTransformerList(List<OperatorTransformer> operatorTransformers) {
    this.operatorTransformers = operatorTransformers;
  }

  public static OperatorTransformerList of(OperatorTransformer... operatorTransformers) {
    return new OperatorTransformerList(Arrays.asList(operatorTransformers));
  }

  public static OperatorTransformerList of(List<OperatorTransformer> operatorTransformers) {
    return new OperatorTransformerList(operatorTransformers);
  }

  public void register(OperatorTransformer operatorTransformer) {
    operatorTransformers.add(operatorTransformer);
  }

  public SqlCall apply(SqlCall sqlCall) {
    for (OperatorTransformer operatorTransformer : operatorTransformers) {
      sqlCall = operatorTransformer.apply(sqlCall);
    }
    return sqlCall;
  }
}
