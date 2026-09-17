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
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.IntegerDomain;


/**
 * Integer domain transformer for MINUS (subtraction) operations.
 *
 * Inverts x - literal = output_value
 * to produce input constraint x = output_value + literal
 *
 * Also handles literal - x = output_value
 * producing input constraint x = literal - output_value
 *
 * Example:
 * x - 3 = 7
 * produces input constraint: x = 10
 */
public class MinusIntegerTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    return expr instanceof RexCall && ((RexCall) expr).getOperator() == SqlStdOperatorTable.MINUS;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode left = call.getOperands().get(0);
    RexNode right = call.getOperands().get(1);
    boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall || left instanceof RexFieldAccess);
    boolean rightVar = (right instanceof RexInputRef || right instanceof RexCall || right instanceof RexFieldAccess);
    boolean leftLit = left instanceof RexLiteral;
    boolean rightLit = right instanceof RexLiteral;
    return (leftVar && rightLit) || (rightVar && leftLit);
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode left = call.getOperands().get(0);
    RexNode right = call.getOperands().get(1);
    if (left instanceof RexInputRef || left instanceof RexCall || left instanceof RexFieldAccess) {
      return left;
    } else {
      return right;
    }
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    if (!(outputDomain instanceof IntegerDomain)) {
      throw new IllegalArgumentException(
          getClass().getSimpleName() + " expects IntegerDomain but got " + outputDomain.getClass().getSimpleName());
    }

    IntegerDomain intDomain = (IntegerDomain) outputDomain;
    RexCall call = (RexCall) expr;
    RexNode left = call.getOperands().get(0);
    RexNode right = call.getOperands().get(1);
    boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall || left instanceof RexFieldAccess);

    RexLiteral literalNode = (RexLiteral) (leftVar ? right : left);
    long literal = literalNode.getValueAs(Long.class);

    if (leftVar) {
      // x - literal = output => x = output + literal
      return intDomain.add(literal);
    } else {
      // literal - x = output => x = literal - output
      // For each interval [a, b] in output: x in [literal - b, literal - a]
      return intDomain.negate().add(literal);
    }
  }
}
