/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import javax.annotation.Nonnull;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * This class is a subclass of SqlCallTransformer which transforms a function operator on SqlNode layer
 * if the signature of the operator to be transformed, including both the name and the number of operands,
 * matches the target values in the condition function.
 */
public class OperatorBasedSqlCallTransformer extends SqlCallTransformer {
  public final String fromOperatorName;
  public final int numOperands;
  public final SqlOperator targetOperator;

  public OperatorBasedSqlCallTransformer(@Nonnull String fromOperatorName, int numOperands,
      @Nonnull SqlOperator targetOperator) {
    this.fromOperatorName = fromOperatorName;
    this.numOperands = numOperands;
    this.targetOperator = targetOperator;
  }

  public OperatorBasedSqlCallTransformer(@Nonnull SqlOperator coralOp, int numOperands, @Nonnull String trinoFuncName) {
    this(coralOp.getName(), numOperands, createSqlUDF(trinoFuncName, coralOp.getReturnTypeInference()));
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return fromOperatorName.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == numOperands;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return createCall(targetOperator, sqlCall.getOperandList(), SqlParserPos.ZERO);
  }

}
