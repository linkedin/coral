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

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;


/**
 * Domain transformer for struct field access (RexFieldAccess).
 *
 * Handles nested struct access patterns where the inner expression is not a bare
 * RexInputRef (e.g., accessing a field on the result of an ITEM call for array-of-structs).
 * Terminal field access (directly on a RexInputRef) is handled by the base case
 * in DomainInferenceProgram.
 *
 * Field access simply selects a field — the constraint propagates inward unchanged.
 */
public class FieldAccessTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    return expr instanceof RexFieldAccess;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexFieldAccess fa = (RexFieldAccess) expr;
    RexNode ref = fa.getReferenceExpr();
    return ref instanceof RexInputRef || ref instanceof RexCall || ref instanceof RexFieldAccess;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexFieldAccess fa = (RexFieldAccess) expr;
    return fa.getReferenceExpr();
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    // Field access just selects a field — pass the constraint through unchanged
    return outputDomain;
  }
}
