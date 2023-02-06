/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import org.apache.calcite.sql.SqlIdentifier;


/**
 * This is the abstract class which defines the transformation of the function inside SqlIdentifier
 */
public abstract class SqlIdentifierTransformer {
  /**
   * This function determines if a function inside SqlIdentifier needs to be transformed
   * @param sqlIdentifier
   * @return boolean
   */
  protected abstract boolean condition(SqlIdentifier sqlIdentifier);

  /**
   * This function performs the transformation of a function inside SqlIdentifier
   * @param sqlIdentifier
   * @return SqlIdentifier
   */
  protected abstract SqlIdentifier transform(SqlIdentifier sqlIdentifier);

  /**
   * This function provides the public interface to be called for this class. It calls the transform function if
   * the returning value of the condition function is true
   * @param sqlIdentifier
   * @return SqlIdentifier
   */
  public SqlIdentifier apply(SqlIdentifier sqlIdentifier) {
    if (condition(sqlIdentifier)) {
      return transform(sqlIdentifier);
    } else {
      return sqlIdentifier;
    }
  }
}
