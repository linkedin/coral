/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.IntegerDomain;


/**
 * Integer domain transformer for ABS (absolute value) operations.
 *
 * Inverts ABS(x) = output_value
 * to produce input constraint x = output_value OR x = -output_value
 *
 * Example:
 * ABS(x) = 5
 * produces input constraint: x in {-5} ∪ {5}
 *
 * ABS(x) = 0
 * produces input constraint: x in {0}
 */
public class AbsIntegerTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    return call.getOperator() == SqlStdOperatorTable.ABS && call.getOperands().size() == 1;
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

    // ABS(x) = v means x = v OR x = -v (for v >= 0)
    // For each interval [a, b] where a >= 0: input can be [a, b] or [-b, -a]
    List<IntegerDomain.Interval> inputIntervals = new ArrayList<>();

    for (IntegerDomain.Interval interval : intDomain.getIntervals()) {
      long min = interval.getMin();
      long max = interval.getMax();

      // ABS output must be >= 0; skip negative parts
      if (max < 0) {
        continue;
      }
      if (min < 0) {
        min = 0;
      }

      // Positive side: [min, max]
      inputIntervals.add(new IntegerDomain.Interval(min, max));

      // Negative side: [-max, -min]
      if (max > 0 || min > 0) {
        inputIntervals.add(new IntegerDomain.Interval(-max, -min));
      }
    }

    if (inputIntervals.isEmpty()) {
      return IntegerDomain.empty();
    }

    return IntegerDomain.of(inputIntervals);
  }
}
