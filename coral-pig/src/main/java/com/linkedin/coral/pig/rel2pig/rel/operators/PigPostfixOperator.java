/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexCallException;
import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigPostfixOperator translates SqlPostfixOperators to Pig Latin.
 * Currently, we only support the following SqlPostFixOperators:
 *   - IS_NULL
 *   - IS_NOT_NULL
 */
public class PigPostfixOperator extends PigOperator {

  public PigPostfixOperator(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  @Override
  public String unparse() {
    final String inputField = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    switch (rexCall.getOperator().getKind()) {
      case IS_NULL:
      case IS_NOT_NULL:
        return String.format("%s %s", inputField, rexCall.getOperator().getName());
      default:
        throw new UnsupportedRexCallException(rexCall);
    }
  }
}
