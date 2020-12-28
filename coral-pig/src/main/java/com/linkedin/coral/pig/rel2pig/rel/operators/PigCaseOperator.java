/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigCaseOperator translates SqlCaseOperator to Pig Latin.
 */
public class PigCaseOperator extends PigOperator {

  private static final String CASE_STATEMENT_TEMPLATE = "(CASE %s END)";
  private static final String CONDITION_TEMPLATE = "WHEN %s THEN %s";
  private static final String ELSE_TEMPLATE = "ELSE %s";

  public PigCaseOperator(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  @Override
  public String unparse() {
    // The operands of a case statement are in the form of:
    // [
    //   condition_0, output_0,
    //   condition_1, output_1,
    //     ...
    //   condition_n, output_n,
    //   [OPTIONAL output_ELSE]
    // ]
    final List<String> cases = new ArrayList<>();
    for (int i = 1; i < rexCall.getOperands().size(); i += 2) {
      final String casePredicate =
          PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(i - 1), inputFieldNames);
      final String caseOutput =
          PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(i), inputFieldNames);
      cases.add(String.format(CONDITION_TEMPLATE, casePredicate, caseOutput));
    }

    // If there exists an ELSE condition, add it to the list of cases.
    if (rexCall.getOperands().size() % 2 == 1) {
      final String defaultOutput = PigRexUtils
          .convertRexNodeToPigExpression(rexCall.getOperands().get(rexCall.getOperands().size() - 1), inputFieldNames);
      cases.add(String.format(ELSE_TEMPLATE, defaultOutput));
    }

    return String.format(CASE_STATEMENT_TEMPLATE, String.join(" ", cases));
  }
}
