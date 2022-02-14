/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Map;

import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;


public class TrinoCalciteUDFMapUtils {
  private TrinoCalciteUDFMapUtils() {
  }

  /**
   * Creates a mapping for Trino SQL operator to Calcite UDF.
   *
   * @param transformerMap Map to store the result
   * @param trinoOp Trino SQL operator
   * @param numOperands Number of operands
   * @param calciteUDFName Name of Calcite UDF
   */
  static void createUDFMapEntry(Map<String, TrinoCalciteUDFTransformer> transformerMap, SqlOperator trinoOp,
      int numOperands, String calciteUDFName) {
    createUDFMapEntry(transformerMap, trinoOp, numOperands, calciteUDFName, null, null);
  }

  /**
   * Creates a mapping from Trino SQL operator to Calcite UDF with Calcite UDF name, operands transformer, and result transformers.
   * To construct Calcite SqlOperator from Calcite UDF name, this method reuses the return type inference from trinoOp,
   * assuming equivalence.
   *
   * @param transformerMap Map to store the result
   * @param trinoOp Trino SQL operator
   * @param numOperands Number of operands
   * @param calciteUDFName Name of Calcite UDF
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, TrinoCalciteUDFTransformer> transformerMap, SqlOperator trinoOp,
      int numOperands, String calciteUDFName, String operandTransformer, String resultTransformer) {
    createUDFMapEntry(transformerMap, trinoOp, numOperands,
        createUDF(calciteUDFName, trinoOp.getReturnTypeInference(), trinoOp.getOperandTypeChecker()),
        operandTransformer, resultTransformer);
  }

  /**
   * Creates a mapping from Trino SQL operator to Calcite UDF with Calcite SqlOperator, operands transformer, and result transformers.
   *
   * @param transformerMap Map to store the result
   * @param trinoOp Trino SQL operator
   * @param numOperands Number of operands
   * @param calciteSqlOperator The Calcite Sql Operator that is used as the target operator in the map
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, TrinoCalciteUDFTransformer> transformerMap, SqlOperator trinoOp,
      int numOperands, SqlOperator calciteSqlOperator, String operandTransformer, String resultTransformer) {

    StaticHiveFunctionRegistry.createAddUserDefinedFunction(calciteSqlOperator.getName(),
        calciteSqlOperator.getReturnTypeInference(), calciteSqlOperator.getOperandTypeChecker());

    transformerMap.put(getKey(trinoOp.getName(), numOperands), TrinoCalciteUDFTransformer.of(trinoOp.getName(),
        calciteSqlOperator, operandTransformer, resultTransformer, null));
  }

  public static SqlOperator createUDF(String transformerName, SqlReturnTypeInference typeInference) {
    return new SqlUserDefinedFunction(new SqlIdentifier(ImmutableList.of(transformerName), SqlParserPos.ZERO),
        typeInference, null, null, null, null);
  }

  static SqlOperator createUDF(String functionName, SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeChecker operandTypeChecker) {
    return new SqlUserDefinedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), returnTypeInference, null,
        operandTypeChecker, null, null);
  }

  static String getKey(String trinoOpName, int numOperands) {
    return trinoOpName + "_" + numOperands;
  }
}
