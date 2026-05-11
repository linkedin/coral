/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import java.util.Arrays;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.RegexDomain;

import dk.brics.automaton.Automaton;


/**
 * Regex-based transformer for SUBSTRING operations.
 *
 * Inverts SUBSTRING(input, start, len) = output_pattern
 * to produce a positional regex constraint on the input.
 *
 * Example:
 * SUBSTRING(input, 1, 4) = "2000"
 * produces input constraint: ^2000.*$
 *
 * SUBSTRING(input, 5, 2) = "US"
 * produces input constraint: ^.{4}US.*$
 */
public class SubstringRegexTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    return expr instanceof RexCall && ((RexCall) expr).getOperator().getKind() == SqlKind.OTHER_FUNCTION
        && ((RexCall) expr).getOperator().getName().equals("substr");
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    // SUBSTRING has 3 operands: (string, start, length)
    // The first operand (string) must be the variable
    RexNode stringArg = call.getOperands().get(0);
    RexNode startArg = call.getOperands().get(1);
    RexNode lengthArg = call.getOperands().get(2);

    // String arg must be variable (RexInputRef) or another call (nested expression)
    boolean stringIsVariable = (stringArg instanceof RexInputRef) || (stringArg instanceof RexCall);

    // Start and length must be literals
    boolean startIsLiteral = startArg instanceof RexLiteral;
    boolean lengthIsLiteral = lengthArg instanceof RexLiteral;

    return stringIsVariable && startIsLiteral && lengthIsLiteral;
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
          "SubstringRegexTransformer expects RegexDomain but got " + outputDomain.getClass().getSimpleName());
    }

    RegexDomain outputRegex = (RegexDomain) outputDomain;
    RexCall call = (RexCall) expr;

    // Extract start and length from literals
    RexLiteral startLiteral = (RexLiteral) call.getOperands().get(1);
    RexLiteral lengthLiteral = (RexLiteral) call.getOperands().get(2);

    int start = startLiteral.getValueAs(Integer.class);
    int length = lengthLiteral.getValueAs(Integer.class);

    // Convert to 0-based index
    int startIdx = start - 1;

    if (startIdx < 0 || length < 0) {
      // Invalid substring parameters
      return RegexDomain.empty();
    }

    Automaton outputAutomaton = outputRegex.getAutomaton();

    // Constrain output to strings of exactly the required length
    Automaton exactLength = Automaton.makeAnyChar().repeat(length, length);
    Automaton constrainedOutput = outputAutomaton.intersection(exactLength);

    if (constrainedOutput.isEmpty()) {
      return RegexDomain.empty();
    }

    // Create positional constraint: .{startIdx}(output).*
    Automaton prefix = Automaton.makeAnyChar().repeat(startIdx, startIdx);
    Automaton suffix = Automaton.makeAnyString();
    Automaton inputAutomaton = Automaton.concatenate(Arrays.asList(prefix, constrainedOutput, suffix));

    return new RegexDomain(inputAutomaton);
  }
}
