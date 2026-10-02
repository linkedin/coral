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
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.RegexDomain;


/**
 * Regex-based transformer for CONCAT operations.
 *
 * Inverts CONCAT(x, literal) = output or CONCAT(literal, x) = output
 * by stripping the known literal prefix/suffix from the output constraint.
 *
 * Example:
 * CONCAT(x, 'World') = 'HelloWorld'
 * produces input constraint: literal 'Hello'
 *
 * CONCAT('Hello', x) = 'HelloWorld'
 * produces input constraint: literal 'World'
 */
public class ConcatRegexTransformer implements DomainTransformer {

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    if (call.getOperands().size() != 2) {
      return false;
    }
    SqlOperator op = call.getOperator();
    // Standard SQL ||, or Hive's concat() function (which arrives as OTHER_FUNCTION).
    return op == SqlStdOperatorTable.CONCAT || "concat".equalsIgnoreCase(op.getName());
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
    if (!(outputDomain instanceof RegexDomain)) {
      throw new IllegalArgumentException(
          "ConcatRegexTransformer expects RegexDomain but got " + outputDomain.getClass().getSimpleName());
    }

    RegexDomain outputRegex = (RegexDomain) outputDomain;
    RexCall call = (RexCall) expr;
    RexNode left = call.getOperands().get(0);
    RexNode right = call.getOperands().get(1);
    boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall || left instanceof RexFieldAccess);

    // Only handle literal concatenation for now
    if (!outputRegex.isLiteral()) {
      return outputRegex;
    }

    String outputLiteral = outputRegex.getLiteralValue();

    if (leftVar) {
      // CONCAT(x, suffix) = output => x = output with suffix stripped
      RexLiteral suffixLiteral = (RexLiteral) right;
      String suffix = suffixLiteral.getValueAs(String.class);

      if (outputLiteral.endsWith(suffix)) {
        String prefix = outputLiteral.substring(0, outputLiteral.length() - suffix.length());
        return RegexDomain.literal(prefix);
      } else {
        // Contradiction: output doesn't end with the suffix
        return RegexDomain.empty();
      }
    } else {
      // CONCAT(prefix, x) = output => x = output with prefix stripped
      RexLiteral prefixLiteral = (RexLiteral) left;
      String prefix = prefixLiteral.getValueAs(String.class);

      if (outputLiteral.startsWith(prefix)) {
        String remainder = outputLiteral.substring(prefix.length());
        return RegexDomain.literal(remainder);
      } else {
        // Contradiction: output doesn't start with the prefix
        return RegexDomain.empty();
      }
    }
  }
}
