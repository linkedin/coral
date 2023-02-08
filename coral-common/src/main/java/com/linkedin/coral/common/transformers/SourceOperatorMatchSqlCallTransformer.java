/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import org.apache.calcite.sql.SqlCall;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * This class is a subclass of {@link SqlCallTransformer} which transforms a function operator on SqlNode layer
 * if the signature of the operator to be transformed, including both the name and the number of operands,
 * matches the target values in the condition function.
 */
public abstract class SourceOperatorMatchSqlCallTransformer extends SqlCallTransformer {
  protected final String sourceOpName;
  protected final int numOperands;

  public SourceOperatorMatchSqlCallTransformer(String sourceOpName, int numOperands) {
    this.sourceOpName = sourceOpName;
    this.numOperands = numOperands;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return sourceOpName.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == numOperands;
  }
}
