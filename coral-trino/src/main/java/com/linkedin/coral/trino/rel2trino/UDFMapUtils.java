/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.Map;

import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.ImmutableList;


public class UDFMapUtils {
  private UDFMapUtils() {
  }

  /**
   * Creates a mapping for Calcite SQL operator to Trino UDF.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param trinoUDFName Name of Trino UDF
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      String trinoUDFName) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, trinoUDFName, null, null);
  }

  /**
   * Creates a mapping from Calcite SQL operator to Trino UDF with Trino SqlOperator, operands transformer, and result transformers.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param trinoSqlOperator The Trino Sql Operator that is used as the target operator in the map
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      SqlOperator trinoSqlOperator, String operandTransformer, String resultTransformer) {

    udfMap.put(getKey(calciteOp.getName(), numOperands),
        UDFTransformer.of(calciteOp.getName(), trinoSqlOperator, operandTransformer, resultTransformer, null));
  }

  /**
   * Creates a mapping from Calcite SQL operator to Trino UDF with Trino SqlOperator.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param trinoSqlOperator The Trino Sql Operator that is used as the target operator in the map
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      SqlOperator trinoSqlOperator) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, trinoSqlOperator, null, null);
  }

  /**
   * Creates a mapping from Calcite SQL operator to Trino UDF with Trino UDF name, operands transformer, and result transformers.
   * To construct Trino SqlOperator from Trino UDF name, this method reuses the return type inference from calciteOp,
   * assuming equivalence.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param trinoUDFName Name of Trino UDF
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      String trinoUDFName, String operandTransformer, String resultTransformer) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, createUDF(trinoUDFName, calciteOp.getReturnTypeInference()),
        operandTransformer, resultTransformer);
  }

  /**
   * Creates a mapping from a Calcite SQL operator to a Trino UDF determined at runtime
   * by the values of input parameters with operand and result transformers.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param operatorTransformers Operator transformers as a JSON string.
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createRuntimeUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      String operatorTransformers, String operandTransformer, String resultTransformer) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, createUDF("", calciteOp.getReturnTypeInference()),
        operandTransformer, resultTransformer);
  }

  /**
   * Creates Trino UDF for a given Trino UDF name and return type inference.
   *
   * @param udfName udf name
   * @param typeInference {@link SqlReturnTypeInference} of return type
   * @return SQL operator
   */
  public static SqlOperator createUDF(String udfName, SqlReturnTypeInference typeInference) {
    return new SqlUserDefinedFunction(new SqlIdentifier(ImmutableList.of(udfName), SqlParserPos.ZERO), typeInference,
        null, null, null, null);
  }

  static String getKey(String calciteOpName, int numOperands) {
    return calciteOpName + "_" + numOperands;
  }
}
