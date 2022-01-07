/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigBuiltinFunction represents a builtin function in Pig.
 *
 * Examples of Pig builtin functions include and is not limited to:
 *   - REGEX_EXTRACT
 *   - SIN
 *   - COS
 *   - LOWER
 */
public class PigBuiltinFunction extends Function {

  private static final String FUNCTION_CALL_TEMPLATE = "%s(%s)";

  final String pigFunctionName;

  PigBuiltinFunction(String functionName) {
    this.pigFunctionName = functionName;
  }

  /**
   * Creates a PigBuiltinFunction with the given functionName
   *
   * @param functionName Name of the function
   * @return PigBuiltinFunction for the given functionName
   */
  public static PigBuiltinFunction create(String functionName) {
    return new PigBuiltinFunction(functionName);
  }

  @Override
  public String unparse(RexCall rexCall, List<String> inputFieldNames) {
    final String functionName = transformFunctionName(rexCall, inputFieldNames);
    final String operands = String.join(", ", transformOperands(rexCall, inputFieldNames));
    return String.format(FUNCTION_CALL_TEMPLATE, functionName, operands);
  }

  /**
   * Generates Pig Latin for an identity projection of operands.
   *
   * @param rexCall RexCall to be transformed
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return Pig Latin to do an identity projection of operands.
   */
  String transformFunctionName(RexCall rexCall, List<String> inputFieldNames) {
    return pigFunctionName;
  }

  /**
   * Generates Pig Latin for the function name of the given rexCall
   *
   * @param rexCall RexCall to be transformed
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return Pig Latin for the function name of the given rexCall
   */
  List<String> transformOperands(RexCall rexCall, List<String> inputFieldNames) {
    final List<String> operands = rexCall.getOperands().stream()
        .map(operand -> PigRexUtils.convertRexNodeToPigExpression(operand, inputFieldNames))
        .collect(Collectors.toList());
    return operands;
  }

}
