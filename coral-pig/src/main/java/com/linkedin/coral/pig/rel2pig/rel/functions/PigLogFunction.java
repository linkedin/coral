/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigLogFunction represents the translation from Calcite LOG UDF to builtin Pig functions.
 * The output of the PigLogFunction has the following form:
 *     LOG(value)/LOG(base)
 */
public class PigLogFunction extends PigBuiltinFunction {

  private static final String LOG_FUNCTION_NAME = "LOG";
  private static final String LOG_FUNCTION_TEMPLATE = "LOG(%s)/LOG(%s)";

  private PigLogFunction() {
    super(LOG_FUNCTION_NAME);
  }

  public static PigLogFunction create() {
    return new PigLogFunction();
  }

  @Override
  public String unparse(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a builtin LOG function that takes two arguments:
    //     - base (the logarithmic base)
    //     - value (the value that is operated on)
    // Pig only has logarithmic functions that have static bases:
    //     - LOG (base e)
    //     - LOG10 (base 10)
    // We need to represent Hive LOG as: LOG(value)/LOG(base)
    final String base = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    final String value = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames);
    return String.format(LOG_FUNCTION_TEMPLATE, value, base);
  }

}
