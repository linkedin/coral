/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;


/**
 * Domain transformer for the ITEM operator (array/map element access).
 *
 * Handles nested access patterns like ITEM(ITEM($5, 1), 'name') by passing
 * the domain constraint through to the inner expression. Terminal ITEM access
 * (ITEM on a bare RexInputRef) is handled by the base case in DomainInferenceProgram.
 *
 * The ITEM operator simply selects an element — the constraint propagates inward unchanged.
 */
public class ItemTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    return isItemOperator(call) && call.getOperands().size() == 2;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode operand0 = call.getOperands().get(0);
    RexNode operand1 = call.getOperands().get(1);
    // Variable must be in operand 0 (the collection); operand 1 must be a literal index/key
    return (operand0 instanceof RexInputRef || operand0 instanceof RexCall || operand0 instanceof RexFieldAccess)
        && operand1 instanceof RexLiteral;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexCall call = (RexCall) expr;
    return call.getOperands().get(0);
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    // ITEM just selects an element — pass the constraint through unchanged
    return outputDomain;
  }

  public static boolean isItemOperator(RexCall call) {
    return "ITEM".equalsIgnoreCase(call.getOperator().getName());
  }
}
