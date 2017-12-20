package com.linkedin.coral.presto.rel2presto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.linkedin.coral.javax.annotation.Nonnull;
import com.linkedin.coral.javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


/**
 * Object for transforming UDF from one SQL language to another SQL language.
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
 * Example:
 * In Calcite SQL, TRUNCATE(aDouble, numDigitAfterDot) truncates aDouble by removing
 * any digit from the position numDigitAfterDot after the dot, like truncate(11.45, 0) = 11,
 * truncate(11.45, 1) = 11.4
 *
 * In PrestoSQL, TRUNCATE(aDouble) only takes one argument and removes all digits after the dot,
 * like truncate(11.45) = 11.
 *
 * The transformation from Calcite TRUNCATE to PrestoSQL TRUNCATE is represented as follows:
 * 1. PrestoSQL name: TRUNCATE
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
 */
public class UDFTransformer {
  private static final Map<String, SqlOperator> OP_MAP = new HashMap<>();

  // Operators allowed in the transformation
  static {
    OP_MAP.put("+", SqlStdOperatorTable.PLUS);
    OP_MAP.put("-", SqlStdOperatorTable.MINUS);
    OP_MAP.put("*", SqlStdOperatorTable.MULTIPLY);
    OP_MAP.put("/", SqlStdOperatorTable.DIVIDE);
    OP_MAP.put("^", SqlStdOperatorTable.POWER);
  }

  public static final String OPERATOR = "op";
  public static final String OPERANDS = "operands";
  /**
   * For input node:
   * - input = 0 refers to the result
   * - input > 0 refers to the index of source operand (starting from 1)
   */
  public static final String INPUT = "input";
  public static final String VALUE = "value";

  public final SqlOperator targetOperator;
  public final List<JsonObject> operandTransformers;
  public final JsonObject resultTransformer;

  private UDFTransformer(SqlOperator targetOperator, List<JsonObject> operandTransformers, JsonObject resultTransformer) {
    this.targetOperator = targetOperator;
    this.operandTransformers = operandTransformers;
    this.resultTransformer = resultTransformer;
  }

  /**
   * Creates a new transformer.
   *
   * @param targetOperator Target operator (a UDF in the target language)
   * @param operandTransformers JSON string representing the operand transformations,
   *                            null for identity transformations
   * @param resultTransformer JSON string representing the result transformation,
   *                          null for identity transformation
   */
  public static UDFTransformer of(@Nonnull SqlOperator targetOperator,
      @Nullable String operandTransformers, @Nullable String resultTransformer) {
    List<JsonObject> operands = null;
    JsonObject result = null;
    if (operandTransformers != null) {
      operands = new ArrayList<>();
      JsonArray transformerArray = new JsonParser().parse(operandTransformers).getAsJsonArray();
      for (JsonElement transformer : transformerArray) {
        operands.add(transformer.getAsJsonObject());
      }
    }
    if (resultTransformer != null) {
      result = new JsonParser().parse(resultTransformer).getAsJsonObject();
    }
    return new UDFTransformer(targetOperator, operands, result);
  }

  /**
   * Transforms a call to the source operator.
   *
   * @param rexBuilder Rex Builder
   * @param sourceOperands Source operands
   * @return An expression calling the target operator that is equivalent to the source operator call
   */
  public RexNode transformCall(RexBuilder rexBuilder, List<RexNode> sourceOperands) {
    final List<RexNode> newOperands = transformOperands(rexBuilder, sourceOperands);
    final RexNode newCall = rexBuilder.makeCall(targetOperator, newOperands);
    return transformResult(rexBuilder, newCall, sourceOperands);
  }

  private List<RexNode> transformOperands(RexBuilder rexBuilder, List<RexNode> sourceOperands) {
    if (operandTransformers == null) {
      return sourceOperands;
    }
    final List<RexNode> sources = new ArrayList<>();
    // Add a dummy expression for input 0
    sources.add(null);
    sources.addAll(sourceOperands);
    final List<RexNode> results = new ArrayList<>();
    for (JsonObject operandTransformer : operandTransformers) {
      results.add(transformExpression(rexBuilder, operandTransformer, sources));
    }
    return results;
  }

  private RexNode transformResult(RexBuilder rexBuilder, RexNode result, List<RexNode> sourceOperands) {
    if (resultTransformer == null) {
      return result;
    }
    final List<RexNode> sources = new ArrayList<>();
    // Result will be input 0
    sources.add(result);
    sources.addAll(sourceOperands);
    return transformExpression(rexBuilder, resultTransformer, sources);
  }

  /**
   * Performs a single transformer.
   */
  private RexNode transformExpression(RexBuilder rexBuilder, JsonObject transformer, List<RexNode> sourceOperands) {
    if (transformer.get(OPERATOR) != null) {
      final List<RexNode> inputOperands = new ArrayList<>();
      for (JsonElement inputOperand: transformer.getAsJsonArray(OPERANDS)) {
        if (inputOperand.isJsonObject()) {
          inputOperands.add(transformExpression(rexBuilder, inputOperand.getAsJsonObject(), sourceOperands));
        }
      }
      final String operatorName = transformer.get(OPERATOR).getAsString();
      final SqlOperator op = OP_MAP.get(operatorName);
      if (op == null) {
        throw new UnsupportedOperationException("Operator " + operatorName + " is not supported in transformation");
      }
      return rexBuilder.makeCall(op, inputOperands);
    }
    if (transformer.get(INPUT) != null) {
      int index = transformer.get(INPUT).getAsInt();
      if (index < 0 || index >= sourceOperands.size() || sourceOperands.get(index) == null) {
        throw new IllegalArgumentException("Invalid input value: " + index
        + ". Number of source operands: " + sourceOperands.size());
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
      return rexBuilder.makeLiteral(primitive.getAsString());
    }
    if (primitive.isBoolean()) {
      return rexBuilder.makeLiteral(primitive.getAsBoolean());
    }
    if (primitive.isNumber()) {
      return rexBuilder.makeBigintLiteral(value.getAsBigDecimal());
    }

    throw new UnsupportedOperationException("Invalid JSON literal value: " + primitive);
  }
}
