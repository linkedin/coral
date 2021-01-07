/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;
import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigUDF represents a that can be defined by the user (UDF).
 *
 * For example:
 *   Pig UDFs can be explicitly declared with the following pig statement:
 *       DEFINE [pigFunctionName] [UDF CLASS]('hiveFunctionName')
 *
 *   If we want to access the Hive explode UDF, it can be declared as follows:
 *       DEFINE pigExplode HiveUDF('explode');
 *
 *   It can subsequently be accessed in a script as follows:
 *       [output] = FOREACH [input] GENERATE pigExplode(arrayField);
 */
public class PigUDF extends Function {

  // We add a prefix to Pig UDFs to ensure that there are no naming conflicts with Pig builtin functions
  private static final String PIG_UDF_ALIAS_TEMPLATE = "PIG_UDF_%s";

  private static final String FUNCTION_CALL_TEMPLATE = "%s(%s)";
  private static final String DEFINE_PIG_BUILTIN_UDF_TEMPLATE = "DEFINE %s dali.data.pig.udf.HiveUDF('%s'%s);";
  private static final String CONSTANT_PARAMETER_TEMPLATE = ", \\'(%s)\\'";
  private static final String NOT_ALPHA_NUMERIC_UNDERSCORE_REGEX = "[^a-zA-Z0-9_]";

  // Name of function in Hive
  private final String hiveFunctionName;

  // Set of 0-based indices of parameters that are constants in the Pig UDF.
  private final Set<Integer> constantParameters;

  private PigUDF(String hiveFunctionName, Set<Integer> constantParameters) {
    this.hiveFunctionName = hiveFunctionName;
    this.constantParameters = constantParameters;
  }

  /**
   * Creates a PigUDF with a given functionName without any constant parameters.
   *
   * @param functionName Name of the function
   * @return PigUDF for the given functionName
   */
  public static PigUDF create(String functionName) {
    return new PigUDF(functionName, Collections.emptySet());
  }

  /**
   * Creates a PigUDF with a given functionName and constant parameters.
   * Constant parameters are passed as 0-based indices.
   *
   * @param functionName Name of the function
   * @param constantParameters Set of indices that contain constant parameters.
   * @return PigUDF for the given functionName
   */
  public static PigUDF create(String functionName, Set<Integer> constantParameters) {
    return new PigUDF(functionName, constantParameters);
  }

  /**
   * Transforms a UDF call represented as a RexCall to a target language.
   *
   * The expression of the RexCall will be produced by a pipelined sequence as follows:
   *     translateOperands -&gt; Returns a comma separated list of operands (OPERANDS_STR)
   *     translateFunctionName -&gt; Returns the Pig Latin name for the operator in the RexCall (FUNCTION_STR)
   *     translateFunctionCallOutput -&gt; Returns the Pig Latin on operations after the function call with the
   *                                    function call string passed as an input
   *
   * @param rexCall RexCall to be transformed
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return PigExpression of the RexCall consisting of the following:
   *           - Pig Latin expression for the UDF called in the RexCall
   */
  @Override
  public final String unparse(RexCall rexCall, List<String> inputFieldNames) {
    final List<String> operands = rexCall.getOperands().stream()
        .map(operand -> PigRexUtils.convertRexNodeToPigExpression(operand, inputFieldNames))
        .collect(Collectors.toList());
    final String functionName = translateFunctionName(rexCall, inputFieldNames);
    final String functionCall = String.format(FUNCTION_CALL_TEMPLATE, functionName, String.join(", ", operands));

    return functionCall;
  }

  /**
   * Generates the Pig Latin to define the functions needed for the given rexCall.
   *
   * @param rexCall RexCall representing the function
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return List of Pig DEFINE statements needed for the Function.
   *         For example, to use the 'explode' function in Hive, it would need to be defined in Pig as follows:
   *             {
   *                 "DEFINE explode HiveUDF('explode');"
   *             }
   */
  public List<String> getFunctionDefinitions(RexCall rexCall, List<String> inputFieldNames) {
    final String constantParameterStatement = getConstantParameterStatement(rexCall, inputFieldNames);
    return ImmutableList.of(String.format(DEFINE_PIG_BUILTIN_UDF_TEMPLATE,
        translateFunctionName(rexCall, inputFieldNames), hiveFunctionName, constantParameterStatement));
  }

  /**
   * Generates Pig Latin for the function name of the given rexCall
   *
   * @param rexCall RexCall to be transformed
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return Pig Latin for the function name of the given rexCall
   */
  private String translateFunctionName(RexCall rexCall, List<String> inputFieldNames) {
    String versionedpigFunctionName = getVersionedFunctionName(rexCall);

    // There may exist calls to functions with different constant parameters.
    // We need to add the constant parameters to its name to ensure uniqueness of function names
    // for different constant parameters.
    if (!constantParameters.isEmpty()) {
      final String constantParametersPostfix = constantParameters.stream().map(parameterIndex -> {
        final RexNode operand = rexCall.getOperands().get(parameterIndex);
        final String operandExpression = PigRexUtils.convertRexNodeToPigExpression(operand, inputFieldNames);
        return operandExpression;
      }).collect(Collectors.joining("_")).replaceAll(NOT_ALPHA_NUMERIC_UNDERSCORE_REGEX, "_");
      versionedpigFunctionName = String.join("_", versionedpigFunctionName, constantParametersPostfix);
    }

    return versionedpigFunctionName;
  }

  /**
   * Generates the versioned function name for the given rexCall.
   *
   * If there is no version associated with the functionName, the function is unversioned and is named as follows:
   *     'PIG_UDF_[calciteName]
   *
   * A versioned function is associated with a versioned table.
   * There may exist multiple implementations of the same UserDefinedFunctions in the same view, in which case
   * different versions of a UDF require different aliases.
   * The versioned function name is named as follows:
   *     'PIG_UDF_[db]_[table]_[tableVersion]_[calciteName]
   *
   * @param rexCall RexCall to be transformed
   * @return Versioned functionName using viewDependentFunctionName as a component of the alias.
   */
  private String getVersionedFunctionName(RexCall rexCall) {
    if (!(rexCall.getOperator() instanceof VersionedSqlUserDefinedFunction)) {
      return String.format(PIG_UDF_ALIAS_TEMPLATE, hiveFunctionName.replace(NOT_ALPHA_NUMERIC_UNDERSCORE_REGEX, "_"));
    }

    final VersionedSqlUserDefinedFunction versionedFunction = (VersionedSqlUserDefinedFunction) rexCall.getOperator();
    return String.join("_", PIG_UDF_ALIAS_TEMPLATE, versionedFunction.getViewDependentFunctionName())
        .replace(NOT_ALPHA_NUMERIC_UNDERSCORE_REGEX, "_");
  }

  /**
   * Generates the Constant Parameter Statement.
   *
   * Constant Parameters are parameters in a function that are literals.
   *
   * For example:
   *      infile(field_str, 'file:///user/home/dir/of/file')
   *   In this example, 'file:///user/home/dir/of/file' is a constant parameter.
   *   The Pig Engine cannot distinguish constant parameters, so they must be declared explicitly.
   *
   *   The constant parameter statement for this call would be:
   *       ', (null, \"file:///user/home/dir/of/file\")'
   *
   * @param rexCall RexCall to be transformed
   * @param inputFieldNames List-index based mapping from Calcite index reference to field names of
   *                        the input of the given RexCall.
   * @return Creates a string list of
   */
  private String getConstantParameterStatement(RexCall rexCall, List<String> inputFieldNames) {
    if (constantParameters.isEmpty()) {
      return "";
    }

    final List<String> parameters = new ArrayList<>();

    for (int i = 0; i < rexCall.getOperands().size(); ++i) {
      if (!constantParameters.contains(i)) {
        parameters.add("null");
      } else {
        final RexNode operand = rexCall.getOperands().get(i);
        final String operandExpression = PigRexUtils.convertRexNodeToPigExpression(operand, inputFieldNames);

        parameters.add(operandExpression);
      }
    }

    return String.format(CONSTANT_PARAMETER_TEMPLATE, String.join(", ", parameters));
  }
}
