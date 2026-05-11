/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.RegexDomain;

import dk.brics.automaton.Automaton;


/**
 * Regex-based transformer for LOWER operations.
 *
 * Inverts LOWER(input) = output_pattern
 * to produce a case-insensitive regex constraint on the input.
 *
 * Example:
 * LOWER(input) = "abc"
 * produces input constraint: [aA][bB][cC]
 */
public class LowerRegexTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    return expr instanceof RexCall && ((RexCall) expr).getOperator() == SqlStdOperatorTable.LOWER;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode arg = call.getOperands().get(0);
    return arg instanceof RexInputRef || arg instanceof RexCall;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexCall call = (RexCall) expr;
    return call.getOperands().get(0);
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    if (!(outputDomain instanceof RegexDomain)) {
      throw new IllegalArgumentException(
          "LowerRegexTransformer expects RegexDomain but got " + outputDomain.getClass().getSimpleName());
    }

    RegexDomain outputRegex = (RegexDomain) outputDomain;

    // If output is a literal lowercase string, make it case-insensitive
    if (outputRegex.isLiteral()) {
      String literalValue = outputRegex.getLiteralValue();
      List<Automaton> parts = new ArrayList<>();

      for (char c : literalValue.toCharArray()) {
        if (Character.isLetter(c)) {
          char lower = Character.toLowerCase(c);
          char upper = Character.toUpperCase(c);
          parts.add(Automaton.makeChar(lower).union(Automaton.makeChar(upper)));
        } else {
          parts.add(Automaton.makeChar(c));
        }
      }

      return new RegexDomain(Automaton.concatenate(parts));
    }

    // For complex patterns, return as-is
    return outputRegex;
  }
}
