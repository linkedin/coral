/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.Collections;
import java.util.List;

import org.apache.calcite.rex.RexCall;


/**
 * PigRandomFunction represents the translation from Calcite RAND UDF to builtin Pig functions.
 * The output of PigRandomFunction has the following form:
 *     RANDOM()
 */
public class PigRandomFunction extends PigBuiltinFunction {

  private static final String RANDOM_FUNCTION_NAME = "RANDOM";

  private PigRandomFunction() {
    super(RANDOM_FUNCTION_NAME);
  }

  public static PigRandomFunction create() {
    return new PigRandomFunction();
  }

  @Override
  List<String> transformOperands(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a builtin RAND function that optionally takes an argument for a seed.
    // Pig does not have the option to specify a seed.
    return Collections.emptyList();
  }
}
