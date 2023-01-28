/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlCall;


/**
 * Container for SqlCallTransformer
 */
public final class SqlCallTransformers {
  private final ImmutableList<SqlCallTransformer> sqlCallTransformers;

  SqlCallTransformers(ImmutableList<SqlCallTransformer> sqlCallTransformers) {
    this.sqlCallTransformers = sqlCallTransformers;
  }

  public static SqlCallTransformers of(SqlCallTransformer... sqlCallTransformers) {
    return new SqlCallTransformers(
        ImmutableList.<SqlCallTransformer> builder().addAll(Arrays.asList(sqlCallTransformers)).build());
  }

  public static SqlCallTransformers of(ImmutableList<SqlCallTransformer> sqlCallTransformers) {
    return new SqlCallTransformers(sqlCallTransformers);
  }

  public SqlCall apply(SqlCall sqlCall) {
    for (SqlCallTransformer sqlCallTransformer : sqlCallTransformers) {
      sqlCall = sqlCallTransformer.apply(sqlCall);
    }
    return sqlCall;
  }
}
