/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.List;

import org.apache.calcite.rex.RexCall;


/**
 * PigRoundFunction represents the translation from Calcite/Hive ROUND to builtin Pig functions.
 * The output of PigRoundFunction has the following form:
 *     - No precision:
 *          ROUND(value)
 *     - With precision:
 *          ROUND_TO(value, precision)
 */
public class PigRoundFunction extends PigBuiltinFunction {

  private static final String ROUND_FUNCTION_NAME = "ROUND";
  private static final String ROUND_TO_FUNCTION_NAME = "ROUND_TO";

  private PigRoundFunction() {
    super(ROUND_FUNCTION_NAME);
  }

  public static PigRoundFunction create() {
    return new PigRoundFunction();
  }

  @Override
  String transformFunctionName(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a builtin ROUND function that takes up to two arguments
    //     - value
    //     - precision [optional]
    // Pig has two functions to do rounding:
    //     - ROUND (1 argument; no precision parameter)
    //     - ROUND_TO (2 arguments; has precision parameter)
    // Depending on the number of arguments, we need to delegate to different functions.
    return rexCall.getOperands().size() == 1 ? ROUND_FUNCTION_NAME : ROUND_TO_FUNCTION_NAME;
  }

}
