/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

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
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * Class to instantiate standard transformers, whose condition(SqlCall) method only checks operator name and number of operands
 * and transform(SqlCall) method is achieved by JSON-format transformers
 */
public class StandardOperatorTransformer extends OperatorTransformer {
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
  public final int numOperands;
  public final SqlOperator targetOperator;
  public List<JsonObject> operandTransformers;
  public JsonObject resultTransformer;
  public List<JsonObject> operatorTransformers;

  /**
   * Creates a new transformer.
   *
   * @param fromOperatorName Name of the function associated with this Operator in the input IR
   * @param numOperands Number of operands
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
   */

  public StandardOperatorTransformer(@Nonnull String fromOperatorName, int numOperands,
      @Nonnull SqlOperator targetOperator, @Nullable String operandTransformers, @Nullable String resultTransformer,
      @Nullable String operatorTransformers) {
    this.fromOperatorName = fromOperatorName;
    this.numOperands = numOperands;
    this.targetOperator = targetOperator;
    if (operandTransformers != null) {
      this.operandTransformers = parseJsonObjectsFromString(operandTransformers);
    }
    if (resultTransformer != null) {
      this.resultTransformer = new JsonParser().parse(resultTransformer).getAsJsonObject();
    }
    if (operatorTransformers != null) {
      this.operatorTransformers = parseJsonObjectsFromString(operatorTransformers);
    }
  }

  @Override
  public boolean condition(SqlCall sqlCall) {
    return fromOperatorName.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == numOperands;
  }

  @Override
  public SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    final SqlOperator newTargetOperator = transformTargetOperator(targetOperator, sourceOperands);
    if (newTargetOperator == null || newTargetOperator.getName().isEmpty()) {
      String operands = sourceOperands.stream().map(SqlNode::toString).collect(Collectors.joining(","));
      throw new IllegalArgumentException(
          String.format("An equivalent operator in the target IR was not found for the function call: %s(%s)",
              fromOperatorName, operands));
    }
    final List<SqlNode> newOperands = transformOperands(sourceOperands);
    final SqlCall newCall = createCall(newTargetOperator, newOperands, SqlParserPos.ZERO);
    return (SqlCall) transformResult(newCall, sourceOperands);
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
        return createOperator(functionName, operator.getReturnTypeInference(), null);
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

  public static SqlOperator createOperator(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference, null,
        operandTypeChecker, null, null);
  }
}
