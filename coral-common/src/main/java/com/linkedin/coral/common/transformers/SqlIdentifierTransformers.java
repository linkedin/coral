/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlIdentifier;


/**
 * This class is the container of one or more SqlIdentifierTransformer
 */
public class SqlIdentifierTransformers {
  private final ImmutableList<SqlIdentifierTransformer> sqlIdentifierTransformers;

  SqlIdentifierTransformers(ImmutableList<SqlIdentifierTransformer> sqlIdentifierTransformers) {
    this.sqlIdentifierTransformers = sqlIdentifierTransformers;
  }

  public static SqlIdentifierTransformers of(ImmutableList<SqlIdentifierTransformer> sqlCallTransformers) {
    return new SqlIdentifierTransformers(sqlCallTransformers);
  }

  public static SqlIdentifierTransformers of(SqlIdentifierTransformer... sqlIdentifierTransformers) {
    return new SqlIdentifierTransformers(
        ImmutableList.<SqlIdentifierTransformer> builder().addAll(Arrays.asList(sqlIdentifierTransformers)).build());
  }

  /**
   * This function goes through all SqlIdentifierTransformer to transform the function inside the given SqlIdentifier if necessary
   * @param sqlIdentifier
   * @return SqlIdentifier
   */
  public SqlIdentifier apply(SqlIdentifier sqlIdentifier) {
    for (SqlIdentifierTransformer sqlIdentifierTransformer : sqlIdentifierTransformers) {
      sqlIdentifier = sqlIdentifierTransformer.apply(sqlIdentifier);
    }
    return sqlIdentifier;
  }
}
