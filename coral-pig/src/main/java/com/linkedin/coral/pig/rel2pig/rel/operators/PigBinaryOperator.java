/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.common.functions.UnknownSqlFunctionException;
import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigBinaryOperator translates SqlBinaryOperators to Pig Latin.
 */
public class PigBinaryOperator extends PigOperator {

  public PigBinaryOperator(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  @Override
  public String unparse() {
    String operator = rexCall.getOperator().getName();

    switch (rexCall.getOperator().getKind()) {
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case AND:
      case OR:
      case MINUS:
      case PLUS:
      case TIMES:
      case DIVIDE:
        break;
      case EQUALS:
        operator = "==";
        break;
      case NOT_EQUALS:
        operator = "!=";
        break;
      default:
        throw new UnknownSqlFunctionException(rexCall.getOperator().getName() + "_pig");
    }

    final String leftOperand = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    final String rightOperand =
        PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames);

    return String.format("(%s %s %s)", leftOperand, operator, rightOperand);
  }
}
