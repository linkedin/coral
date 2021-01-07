/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.rel.operators.PigCaseOperator;


/**
 * PigIfFunction represents the translation from Calcite IF UDF to builtin Pig functions.
 * The output of the PigIfFunction has the following form:
 *     CASE WHEN condition THEN value ELSE default
 */
public class PigIfFunction extends PigBuiltinFunction {

  private static final String IF_FUNCTION_NAME = "IF";

  private PigIfFunction() {
    super(IF_FUNCTION_NAME);
  }

  public static PigIfFunction create() {
    return new PigIfFunction();
  }

  @Override
  public String unparse(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a UDF 'if([condition], [value], [default])' that cannot be modelled as a function call/UDF in Pig.
    //
    // Instead, we model the Hive IF UDF as a CASE statement:
    //     - CASE WHEN condition THEN value ELSE default
    final PigCaseOperator caseOperator = new PigCaseOperator(rexCall, inputFieldNames);
    return caseOperator.unparse();
  }

}
