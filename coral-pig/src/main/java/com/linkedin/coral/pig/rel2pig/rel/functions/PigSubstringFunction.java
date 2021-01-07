/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigSubstringFunction represents the translation from Calcite Substring UDF to builtin Pig functions.
 * The output of the PigSubstring has the following form:
 *     SUBSTRING(string, startIndex, endIndex)
 */
public class PigSubstringFunction extends PigBuiltinFunction {
  private static final String SUBSTRING_FUNCTION_NAME = "SUBSTRING";
  private static final String START_INDEX_TEMPLATE = "(int)(%s) - 1";
  private static final String ADDITION_TEMPLATE = "(int)(%s) + (int)(%s)";
  private static final String SIZE_FUNCTION_TEMPLATE = "(int)SIZE(%s)";

  private PigSubstringFunction() {
    super(SUBSTRING_FUNCTION_NAME);
  }

  public static PigSubstringFunction create() {
    return new PigSubstringFunction();
  }

  @Override
  List<String> transformOperands(RexCall rexCall, List<String> inputFieldNames) {
    // Hive has a builtin SUBSTRING function that takes two or three arguments.
    // The semantics of the arguments depend on the number of arguments as follows:
    //     - SUBSTRING (2 arguments)
    //         - string
    //         - beginIndex (1-based index)
    //     - SUBSTRING (3 arguments)
    //         - string
    //         - beginIndex (1-based index)
    //         - substringLength
    // Pig has a SUBSTRING function with the following semantics:
    //     - SUBSTRING (3 arguments)
    //         - string
    //         - beginIndex (0-based index)
    //         - endIndex (character following the last character of the substring)
    final String value = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);
    final String startIndex = String.format(START_INDEX_TEMPLATE,
        PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames));
    // If there are three arguments, the endIndex can be set as:
    //     endIndex = startIndex + substringLength
    // Otherwise, the endIndex can be set as:
    //     endIndex = SIZE(string)
    final String endIndex = rexCall.getOperands().size() >= 3
        ? String.format(ADDITION_TEMPLATE, startIndex,
            PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(2), inputFieldNames))
        : String.format(SIZE_FUNCTION_TEMPLATE, value);
    return Arrays.asList(value, startIndex, endIndex);
  }

}
