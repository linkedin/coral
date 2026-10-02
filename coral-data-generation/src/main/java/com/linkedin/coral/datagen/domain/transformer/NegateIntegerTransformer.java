/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.IntegerDomain;


/**
 * Integer domain transformer for UNARY_MINUS (negation) operations.
 *
 * Inverts -(x) = output_value
 * to produce input constraint x = -(output_value)
 *
 * Example:
 * -(x) = -5
 * produces input constraint: x = 5
 */
public class NegateIntegerTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    return call.getOperator() == SqlStdOperatorTable.UNARY_MINUS && call.getOperands().size() == 1;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode operand = call.getOperands().get(0);
    return operand instanceof RexInputRef || operand instanceof RexCall || operand instanceof RexFieldAccess;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexCall call = (RexCall) expr;
    return call.getOperands().get(0);
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    if (!(outputDomain instanceof IntegerDomain)) {
      throw new IllegalArgumentException(
          getClass().getSimpleName() + " expects IntegerDomain but got " + outputDomain.getClass().getSimpleName());
    }

    IntegerDomain intDomain = (IntegerDomain) outputDomain;
    return intDomain.negate();
  }
}
