/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

import java.util.Map;

import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;


public class Spark2CoralOperatorTransformerMapUtils {

  private Spark2CoralOperatorTransformerMapUtils() {
  }

  /**
   * Creates a mapping for Spark SqlOperator name to SparkCalciteOperatorTransformer.
   *
   * @param transformerMap Map to store the result
   * @param sparkOp Spark SQL operator
   * @param numOperands Number of operands
   * @param calciteOperatorName Name of Calcite Operator
   */
  static void createTransformerMapEntry(Map<String, OperatorTransformer> transformerMap, SqlOperator sparkOp,
      int numOperands, String calciteOperatorName) {
    createTransformerMapEntry(transformerMap, sparkOp, numOperands, calciteOperatorName, null, null);
  }

  /**
   * Creates a mapping from Spark SqlOperator name to Calcite Operator with Calcite Operator name, operands transformer, and result transformers.
   * To construct Calcite SqlOperator from Calcite Operator name, this method reuses the return type inference from sparkOp,
   * assuming equivalence.
   *
   * @param transformerMap Map to store the result
   * @param sparkOp Spark SQL operator
   * @param numOperands Number of operands
   * @param calciteOperatorName Name of Calcite Operator
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createTransformerMapEntry(Map<String, OperatorTransformer> transformerMap, SqlOperator sparkOp,
      int numOperands, String calciteOperatorName, String operandTransformer, String resultTransformer) {
    createTransformerMapEntry(transformerMap, sparkOp, numOperands,
        createOperator(calciteOperatorName, sparkOp.getReturnTypeInference(), sparkOp.getOperandTypeChecker()),
        operandTransformer, resultTransformer);
  }

  /**
   * Creates a mapping from Spark SqlOperator name to Calcite UDF with Calcite SqlOperator, operands transformer, and result transformers.
   *
   * @param transformerMap Map to store the result
   * @param sparkOp Spark SQL operator
   * @param numOperands Number of operands
   * @param calciteSqlOperator The Calcite Sql Operator that is used as the target operator in the map
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createTransformerMapEntry(Map<String, OperatorTransformer> transformerMap, SqlOperator sparkOp,
      int numOperands, SqlOperator calciteSqlOperator, String operandTransformer, String resultTransformer) {

    transformerMap.put(getKey(sparkOp.getName(), numOperands),
        OperatorTransformer.of(sparkOp.getName(), calciteSqlOperator, operandTransformer, resultTransformer, null));
  }

  static SqlOperator createOperator(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference, null,
        operandTypeChecker, null, null);
  }

  static String getKey(String sparkOpName, int numOperands) {
    return sparkOpName + "_" + numOperands;
  }
}
