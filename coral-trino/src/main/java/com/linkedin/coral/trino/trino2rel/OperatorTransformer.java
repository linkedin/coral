/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * Object for transforming Operator from one SQL language to another SQL language at the SqlNode layer.
 *
 * Suppose f1(a1, a2, ..., an) in the first language can be computed by
 * f2(b1, b2, ..., bm) in the second language as follows:
 *    (b1, b2, ..., bm) = g(a1, a2, ..., an)
 *    f1(a1, a2, ..., an) = h(f2(g(a1, a2, ..., an)))
 *
 * We need to define two transformation functions:
 * - A vector function g for transforming all operands
 * - A function h for transforming the result.
 *
 * This class will represent g and h as expressions in JSON format as follows:
 * - Operators: +, -, *, /, and ^
 * - Operands: source operands and literal values
 *
 * There may also be situations where a function in one language can map to more than one functions in the other
 * language depending on the set of input parameters.
 * We define a set of matching functions to determine what function name is used.
 * Currently, there is no use-case more complicated than matching a parameter string to a static regex.
 *
 * Example 1:
 * In the input IR, TRUNCATE(aDouble, numDigitAfterDot) truncates aDouble by removing
 * any digit from the position numDigitAfterDot after the dot, like truncate(11.45, 0) = 11,
 * truncate(11.45, 1) = 11.4
 *
 * In the target IR, TRUNCATE(aDouble) only takes one argument and removes all digits after the dot,
 * like truncate(11.45) = 11.
 *
 * The transformation of TRUNCATE from one IR to another is represented as follows:
 * 1. Target IR name: TRUNCATE
 *
 * 2. Operand transformers:
 * g(b1) = a1 * 10 ^ a2, with JSON format:
 * [
 *  { "op":"*",
 *    "operands":[
 *      {"input":1}, // input 0 is reserved for result transformer. source inputs start from 1
 *      { "op":"^",
 *        "operands":[
 *          {"value":10},
 *          {"input":2}]}]}]
 *
 * 3. Result transformer:
 * h(result) = result / 10 ^ a2
 * { "op":"/",
 *    "operands":[
 *      {"input":0}, // input 0 is for result transformer
 *      { "op":"^",
 *        "operands":[
 *          {"value":10},
 *          {"input":2}]}]}]
 *
 *
 * 4. Operator transformers:
 * none
 *
 * Example 2:
 * In the input IR, there exists a hive-derived function to decode binary data given a format, DECODE(binary, scheme).
 * In the target IR, there is no generic decoding function that takes a decoding-scheme.
 * Instead, there exist specific decoding functions that are first-class functions like FROM_UTF8(binary).
 * Consequently, we would need to know the operands in the function in order to determine the corresponding call.
 *
 * The transformation of DECODE from one IR to another is represented as follows:
 * 1. Target IR name: There is no function name determined at compile time.
 * null
 *
 * 2. Operand transformers: We want to retain column 1 and drop column 2:
 * [{"input":1}]
 *
 * 3. Result transformer: No transformation is performed on output.
 * null
 *
 * 4. Operator transformers: Check the second parameter (scheme) matches 'utf-8' with any casing using Java Regex.
 * [ {
 *    "regex" : "^.*(?i)(utf-8).*$",
 *    "input" : 2,
 *    "name" : "from_utf8"
 *   }
 * ]
 */
class OperatorTransformer {
  private static final Map<String, SqlOperator> OP_MAP = new HashMap<>();

  // Operators allowed in the transformation
  static {
    OP_MAP.put("+", SqlStdOperatorTable.PLUS);
    OP_MAP.put("-", SqlStdOperatorTable.MINUS);
    OP_MAP.put("*", SqlStdOperatorTable.MULTIPLY);
    OP_MAP.put("/", SqlStdOperatorTable.DIVIDE);
    OP_MAP.put("^", SqlStdOperatorTable.POWER);
    OP_MAP.put("%", SqlStdOperatorTable.MOD);
  }

  public static final String OPERATOR = "op";
  public static final String OPERANDS = "operands";
  /**
   * For input node:
   * - input equals 0 refers to the result
   * - input great than 0 refers to the index of source operand (starting from 1)
   */
  public static final String INPUT = "input";
  public static final String VALUE = "value";
  public static final String REGEX = "regex";
  public static final String NAME = "name";

  public final String fromOperatorName;
  public final SqlOperator targetOperator;
  public final List<JsonObject> operandTransformers;
  public final JsonObject resultTransformer;
  public final List<JsonObject> operatorTransformers;

  private OperatorTransformer(String fromOperatorName, SqlOperator targetOperator, List<JsonObject> operandTransformers,
      JsonObject resultTransformer, List<JsonObject> operatorTransformers) {
    this.fromOperatorName = fromOperatorName;
    this.targetOperator = targetOperator;
    this.operandTransformers = operandTransformers;
    this.resultTransformer = resultTransformer;
    this.operatorTransformers = operatorTransformers;
  }

  /**
   * Creates a new transformer.
   *
   * @param fromOperatorName Name of the function associated with this Operator in the input IR
   * @param targetOperator Operator in the target language
   * @param operandTransformers JSON string representing the operand transformations,
   *                            null for identity transformations
   * @param resultTransformer JSON string representing the result transformation,
   *                          null for identity transformation
   * @param operatorTransformers JSON string representing an array of transformers that can vary the name of the target
   *                             operator based on runtime parameter values.
   *                             In the order of the JSON Array, the first transformer that matches the JSON string will
   *                             have its given operator named selected as the target operator name.
   *                             Operands are indexed beginning at index 1.
   *                             An operatorTransformer has the following serialized JSON string format:
   *                             "[
   *                               {
   *                                  \"name\" : \"{Name of function if this matches}\",
   *                                  \"input\" : {Index of the parameter starting at index 1 that is evaluated },
   *                                  \"regex\" : \"{Java Regex string matching the parameter at given input}\"
   *                               },
   *                               ...
   *                             ]"
   *                             For example, a transformer for a operator named "foo" when parameter 2 matches exactly
   *                             "bar" is specified as:
   *                             "[
   *                               {
   *                                  \"name\" : \"foo\",
   *                                  \"input\" : 2,
   *                                  \"regex\" : \"'bar'\"
   *                               }
   *                             ]"
   *                             NOTE: A string literal is represented exactly as ['STRING_LITERAL'] with the single
   *                             quotation marks INCLUDED.
   *                             As seen in the example above, the single quotation marks are also present in the
   *                             regex matcher.
   *
   * @return {@link OperatorTransformer} object
   */

  public static OperatorTransformer of(@Nonnull String fromOperatorName, @Nonnull SqlOperator targetOperator,
      @Nullable String operandTransformers, @Nullable String resultTransformer, @Nullable String operatorTransformers) {
    List<JsonObject> operands = null;
    JsonObject result = null;
    List<JsonObject> operators = null;
    if (operandTransformers != null) {
      operands = parseJsonObjectsFromString(operandTransformers);
    }
    if (resultTransformer != null) {
      result = new JsonParser().parse(resultTransformer).getAsJsonObject();
    }
    if (operatorTransformers != null) {
      operators = parseJsonObjectsFromString(operatorTransformers);
    }
    return new OperatorTransformer(fromOperatorName, targetOperator, operands, result, operators);
  }

  /**
   * Transforms a call to the source operator.
   *
   * @param sourceOperands Source operands
   * @return An expression calling the target operator that is equivalent to the source operator call
   */
  public SqlNode transformCall(List<SqlNode> sourceOperands) {
    final SqlOperator newTargetOperator = transformTargetOperator(targetOperator, sourceOperands);
    if (newTargetOperator == null || newTargetOperator.getName().isEmpty()) {
      String operands = sourceOperands.stream().map(SqlNode::toString).collect(Collectors.joining(","));
      throw new IllegalArgumentException(
          String.format("An equivalent operator in the target IR was not found for the function call: %s(%s)",
              fromOperatorName, operands));
    }
    final List<SqlNode> newOperands = transformOperands(sourceOperands);
    final SqlCall newCall = createCall(newTargetOperator, newOperands, SqlParserPos.ZERO);
    return transformResult(newCall, sourceOperands);
  }

  private List<SqlNode> transformOperands(List<SqlNode> sourceOperands) {
    if (operandTransformers == null) {
      return sourceOperands;
    }
    final List<SqlNode> sources = new ArrayList<>();
    // Add a dummy expression for input 0
    sources.add(null);
    sources.addAll(sourceOperands);
    final List<SqlNode> results = new ArrayList<>();
    for (JsonObject operandTransformer : operandTransformers) {
      results.add(transformExpression(operandTransformer, sources));
    }
    return results;
  }

  private SqlNode transformResult(SqlNode result, List<SqlNode> sourceOperands) {
    if (resultTransformer == null) {
      return result;
    }
    final List<SqlNode> sources = new ArrayList<>();
    // Result will be input 0
    sources.add(result);
    sources.addAll(sourceOperands);
    return transformExpression(resultTransformer, sources);
  }

  /**
   * Performs a single transformer.
   */
  private SqlNode transformExpression(JsonObject transformer, List<SqlNode> sourceOperands) {
    if (transformer.get(OPERATOR) != null) {
      final List<SqlNode> inputOperands = new ArrayList<>();
      for (JsonElement inputOperand : transformer.getAsJsonArray(OPERANDS)) {
        if (inputOperand.isJsonObject()) {
          inputOperands.add(transformExpression(inputOperand.getAsJsonObject(), sourceOperands));
        }
      }
      final String operatorName = transformer.get(OPERATOR).getAsString();
      final SqlOperator op = OP_MAP.get(operatorName);
      if (op == null) {
        throw new UnsupportedOperationException("Operator " + operatorName + " is not supported in transformation");
      }
      return createCall(op, inputOperands, SqlParserPos.ZERO);
    }
    if (transformer.get(INPUT) != null) {
      int index = transformer.get(INPUT).getAsInt();
      if (index < 0 || index >= sourceOperands.size() || sourceOperands.get(index) == null) {
        throw new IllegalArgumentException(
            "Invalid input value: " + index + ". Number of source operands: " + sourceOperands.size());
      }
      return sourceOperands.get(index);
    }
    final JsonElement value = transformer.get(VALUE);
    if (value == null) {
      throw new IllegalArgumentException("JSON node for transformation should be either op, input, or value");
    }
    if (!value.isJsonPrimitive()) {
      throw new IllegalArgumentException("Value should be of primitive type: " + value);
    }

    final JsonPrimitive primitive = value.getAsJsonPrimitive();
    if (primitive.isString()) {
      return createStringLiteral(primitive.getAsString(), SqlParserPos.ZERO);
    }
    if (primitive.isBoolean()) {
      return createLiteralBoolean(primitive.getAsBoolean(), SqlParserPos.ZERO);
    }
    if (primitive.isNumber()) {
      return createLiteralNumber(value.getAsBigDecimal().longValue(), SqlParserPos.ZERO);
    }

    throw new UnsupportedOperationException("Invalid JSON literal value: " + primitive);
  }

  /**
   * Returns a SqlOperator with a function name based on the value of the source operands.
   */
  private SqlOperator transformTargetOperator(SqlOperator operator, List<SqlNode> sourceOperands) {
    if (operatorTransformers == null) {
      return operator;
    }

    for (JsonObject operatorTransformer : operatorTransformers) {
      if (!operatorTransformer.has(REGEX) || !operatorTransformer.has(INPUT) || !operatorTransformer.has(NAME)) {
        throw new IllegalArgumentException(
            "JSON node for target operator transformer must have a matcher, input and name");
      }
      // We use the same convention as operand and result transformers.
      // Therefore, we start source index values at index 1 instead of index 0.
      // Acceptable index values are set to be [1, size]
      int index = operatorTransformer.get(INPUT).getAsInt() - 1;
      if (index < 0 || index >= sourceOperands.size()) {
        throw new IllegalArgumentException(
            String.format("Index is not within the acceptable range [%d, %d]", 1, sourceOperands.size()));
      }
      String functionName = operatorTransformer.get(NAME).getAsString();
      if (functionName.isEmpty()) {
        throw new IllegalArgumentException("JSON node for transformation must have a non-empty name");
      }
      String matcher = operatorTransformer.get(REGEX).getAsString();

      if (Pattern.matches(matcher, sourceOperands.get(index).toString())) {
        return Trino2CoralOperatorTransformerMapUtils.createOperator(functionName, operator.getReturnTypeInference(),
            null);
      }
    }
    return operator;
  }

  /**
   * Creates an ArrayList of JsonObjects from a string input.
   * The input string must be a serialized JSON array.
   */
  private static List<JsonObject> parseJsonObjectsFromString(String s) {
    List<JsonObject> objects = new ArrayList<>();
    JsonArray transformerArray = new JsonParser().parse(s).getAsJsonArray();
    for (JsonElement object : transformerArray) {
      objects.add(object.getAsJsonObject());
    }
    return objects;
  }
}
