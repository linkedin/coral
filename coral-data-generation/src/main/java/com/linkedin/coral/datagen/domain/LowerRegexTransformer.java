/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


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
    String outputPattern = outputRegex.getRegex();

    // If output is a literal lowercase string, make it case-insensitive
    if (outputRegex.isLiteral()) {
      String literalValue = unescapeLiteral(outputPattern);
      StringBuilder caseInsensitive = new StringBuilder("^");

      for (char c : literalValue.toCharArray()) {
        if (Character.isLetter(c)) {
          char lower = Character.toLowerCase(c);
          char upper = Character.toUpperCase(c);
          caseInsensitive.append('[').append(lower).append(upper).append(']');
        } else {
          // Non-letter characters remain as-is (escaped if needed)
          caseInsensitive.append(escapeRegexChar(c));
        }
      }
      caseInsensitive.append('$');

      return new RegexDomain(caseInsensitive.toString());
    }

    // For complex patterns, wrap with case-insensitive flag
    // Note: Java regex doesn't have inline (?i) in automaton library,
    // so we return the pattern as-is and rely on character-level matching
    return outputRegex;
  }

  /**
   * Unescapes a regex literal to get the actual string value.
   * Handles anchored patterns like "^ABC$".
   */
  private String unescapeLiteral(String escaped) {
    String result = escaped;
    // Remove anchors if present
    if (result.startsWith("^")) {
      result = result.substring(1);
    }
    if (result.endsWith("$")) {
      result = result.substring(0, result.length() - 1);
    }
    // Remove escape sequences
    return result.replaceAll("\\\\(.)", "$1");
  }

  private String escapeRegexChar(char c) {
    if (".+*?^$()[]{}|\\".indexOf(c) >= 0) {
      return "\\" + c;
    }
    return String.valueOf(c);
  }
}
