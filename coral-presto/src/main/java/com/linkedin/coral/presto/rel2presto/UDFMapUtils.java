package com.linkedin.coral.presto.rel2presto;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import java.util.Map;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;


public class UDFMapUtils {
  private UDFMapUtils() {
  }

  /**
   * Creates a mapping for Calcite SQL operator to Presto UDF.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param prestoUDFName Name of Presto UDF
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp,
      int numOperands, String prestoUDFName) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, prestoUDFName, null, null);
  }

  /**
   * Creates a mapping from Calcite SQL operator to Presto UDF with Presto SqlOperator, operands transformer, and result transformers.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param prestoSqlOperator The Presto Sql Operator that is used as the target operator in the map
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      SqlOperator prestoSqlOperator, String operandTransformer, String resultTransformer) {

    udfMap.put(getKey(calciteOp.getName(), numOperands),
        UDFTransformer.of(calciteOp.getName(), prestoSqlOperator, operandTransformer, resultTransformer,
            null));
  }

  /**
   * Creates a mapping from Calcite SQL operator to Presto UDF with Presto SqlOperator.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param prestoSqlOperator The Presto Sql Operator that is used as the target operator in the map
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      SqlOperator prestoSqlOperator) {
    createUDFMapEntry(udfMap, calciteOp, numOperands, prestoSqlOperator, null, null);
  }

  /**
   * Creates a mapping from Calcite SQL operator to Presto UDF with Presto UDF name, operands transformer, and result transformers.
   * To construct Presto SqlOperator from Presto UDF name, this method reuses the return type inference from calciteOp,
   * assuming equivalence.
   *
   * @param udfMap Map to store the result
   * @param calciteOp Calcite SQL operator
   * @param numOperands Number of operands
   * @param prestoUDFName Name of Presto UDF
   * @param operandTransformer Operand transformers, null for identity transformation
   * @param resultTransformer Result transformer, null for identity transformation
   */
  static void createUDFMapEntry(Map<String, UDFTransformer> udfMap, SqlOperator calciteOp, int numOperands,
      String prestoUDFName, String operandTransformer, String resultTransformer) {
    createUDFMapEntry(
        udfMap,
        calciteOp,
        numOperands,
        createUDF(prestoUDFName, calciteOp.getReturnTypeInference()),
        operandTransformer,
        resultTransformer
    );
  }

  /**
   * Creates a mapping from a Calcite SQL operator to a Presto UDF determined at runtime
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
    createUDFMapEntry(
        udfMap,
        calciteOp,
        numOperands,
        createUDF("", calciteOp.getReturnTypeInference()),
        operandTransformer,
        resultTransformer
    );
  }

  /**
   * Creates Presto UDF for a given Presto UDF name and return type inference.
   */
  public static SqlOperator createUDF(String udfName, SqlReturnTypeInference typeInference) {
    return new SqlUserDefinedFunction(new SqlIdentifier(ImmutableList.of(udfName), SqlParserPos.ZERO),
        typeInference, null, null, null, null);
  }

  static String getKey(String calciteOpName, int numOperands) {
    return calciteOpName + "_" + numOperands;
  }
}
