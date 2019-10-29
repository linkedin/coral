/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;
import java.util.List;
import org.apache.calcite.rex.RexCall;


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
      case EQUALS:
        operator = "==";
        break;
      case NOT_EQUALS:
        operator = "!=";
        break;
      default:
    }

    final String leftOperand = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    final String rightOperand = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames);

    return String.format("(%s %s %s)", leftOperand, operator, rightOperand);
  }
}
