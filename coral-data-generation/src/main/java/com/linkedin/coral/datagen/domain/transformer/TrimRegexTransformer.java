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
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.RegexDomain;

import dk.brics.automaton.Automaton;


/**
 * Regex-based transformer for TRIM operations.
 *
 * Inverts TRIM(x) = output_pattern
 * to produce a constraint allowing optional leading/trailing whitespace.
 *
 * Example:
 * TRIM(x) = 'abc'
 * produces input constraint: ' *abc *'
 */
public class TrimRegexTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    SqlOperator op = call.getOperator();
    int arity = call.getOperands().size();
    // Calcite's standard TRIM has 3 operands: (flag, trim_char, source).
    // Hive's trim() arrives as an OTHER_FUNCTION with a single source operand.
    boolean isCalciteStandard = op == SqlStdOperatorTable.TRIM && arity == 3;
    boolean isHiveStyle = "trim".equalsIgnoreCase(op.getName()) && arity == 1;
    return isCalciteStandard || isHiveStyle;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode source = sourceOperand(call);
    return source instanceof RexInputRef || source instanceof RexCall || source instanceof RexFieldAccess;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    return sourceOperand((RexCall) expr);
  }

  private static RexNode sourceOperand(RexCall call) {
    // Calcite's standard TRIM puts source at operand 2; Hive's 1-operand trim puts it at 0.
    return call.getOperands().size() == 3 ? call.getOperands().get(2) : call.getOperands().get(0);
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    if (!(outputDomain instanceof RegexDomain)) {
      throw new IllegalArgumentException(
          "TrimRegexTransformer expects RegexDomain but got " + outputDomain.getClass().getSimpleName());
    }

    RegexDomain outputRegex = (RegexDomain) outputDomain;

    // Wrap the inner pattern with optional leading and trailing spaces so any
    // value matching it after trimming becomes valid before trimming.
    Automaton optionalSpaces = Automaton.makeChar(' ').repeat();

    if (outputRegex.isLiteral()) {
      String literalValue = outputRegex.getLiteralValue();
      List<Automaton> parts = new ArrayList<>();
      for (char c : literalValue.toCharArray()) {
        parts.add(Automaton.makeChar(c));
      }
      Automaton inner = parts.isEmpty() ? Automaton.makeEmptyString() : Automaton.concatenate(parts);
      return new RegexDomain(Automaton.concatenate(java.util.Arrays.asList(optionalSpaces, inner, optionalSpaces)));
    }

    // For complex patterns, conservatively return as-is.
    return outputRegex;
  }
}
