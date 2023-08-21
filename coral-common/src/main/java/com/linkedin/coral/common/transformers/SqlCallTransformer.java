/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.common.utils.TypeDerivationUtil;


/**
 * Abstract class for generic transformations on SqlCalls
 */
public abstract class SqlCallTransformer {
  private TypeDerivationUtil typeDerivationUtil;

  public SqlCallTransformer() {
  }

  public SqlCallTransformer(TypeDerivationUtil typeDerivationUtil) {
    this.typeDerivationUtil = typeDerivationUtil;
  }

  /**
   * Condition of the transformer, it's used to determine if the SqlCall should be transformed or not
   */
  protected abstract boolean condition(SqlCall sqlCall);

  /**
   * Implementation of the transformation, returns the transformed SqlCall
   */
  protected abstract SqlCall transform(SqlCall sqlCall);

  /**
   * Public entry of the transformer, it returns the result of transformed SqlCall if `condition(SqlCall)` returns true,
   * otherwise returns the input SqlCall without any transformation
   */
  public SqlCall apply(SqlCall sqlCall) {
    if (condition(sqlCall)) {
      return transform(sqlCall);
    } else {
      return sqlCall;
    }
  }

  /**
   * Computes the RelDataType for the passed SqlNode
   * @param sqlNode input SqlNode
   * @return derived RelDataType
   */
  protected RelDataType deriveRelDatatype(SqlNode sqlNode) {
    if (typeDerivationUtil == null) {
      throw new RuntimeException("TypeDerivationUtil does not exist to derive the RelDataType for SqlNode: " + sqlNode);
    }
    return typeDerivationUtil.getRelDataType(sqlNode);
  }

  /**
   * This function creates a {@link SqlOperator} for a function with the function name and return type inference.
   */
  protected static SqlOperator createSqlOperator(String functionName, SqlReturnTypeInference typeInference) {
    SqlIdentifier sqlIdentifier = new SqlIdentifier(ImmutableList.of(functionName), SqlParserPos.ZERO);
    return new SqlUserDefinedFunction(sqlIdentifier, typeInference, null, null, null, null);
  }
}
