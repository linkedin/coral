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
 * PigLog2Function represents the translation from Calcite LOG2 UDF to builtin Pig functions.
 * The output of the PigLogFunction has the following form:
 *     LOG(value)/LOG(2)
 */
public class PigLog2Function extends PigBuiltinFunction {
  private static final String LOG2_FUNCTION_NAME = "LOG2";
  private static final String LOG2_FUNCTION_TEMPLATE = "LOG(%s)/LOG(2)";

  private PigLog2Function() {
    super(LOG2_FUNCTION_NAME);
  }

  public static PigLog2Function create() {
    return new PigLog2Function();
  }

  @Override
  public String unparse(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a builtin LOG2 function that takes a the logarithm of a value with base 2.
    // Pig only has logarithmic functions that have static bases:
    //     - LOG (base e)
    //     - LOG10 (base 10)
    // We need to represent Hive LOG as: LOG(value)/LOG(2)
    final String value = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    return String.format(LOG2_FUNCTION_TEMPLATE, value);
  }

}
