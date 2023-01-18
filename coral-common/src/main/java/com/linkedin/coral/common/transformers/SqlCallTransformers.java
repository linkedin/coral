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
 * Container for SqlCallTransformer
 */
public class SqlCallTransformers {
  private final List<SqlCallTransformer> operatorTransformers;

  SqlCallTransformers(List<SqlCallTransformer> operatorTransformers) {
    this.operatorTransformers = operatorTransformers;
  }

  public static SqlCallTransformers of(SqlCallTransformer... operatorTransformers) {
    return new SqlCallTransformers(Arrays.asList(operatorTransformers));
  }

  public static SqlCallTransformers of(List<SqlCallTransformer> operatorTransformers) {
    return new SqlCallTransformers(operatorTransformers);
  }

  public void register(SqlCallTransformer operatorTransformer) {
    operatorTransformers.add(operatorTransformer);
  }

  public SqlCall apply(SqlCall sqlCall) {
    for (SqlCallTransformer sqlCallTransformer : operatorTransformers) {
      sqlCall = sqlCallTransformer.apply(sqlCall);
    }
    return sqlCall;
  }
}
