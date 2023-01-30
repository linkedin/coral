/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.transformers.SignatureBasedConditionSqlCallTransformer;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;


/**
 * This utility class provides the functionalities of creating SqlCallTransformers and related SqlOperators
 * based on the functions registered in StaticHiveFunctionRegistry to perform the transformation from Coral
 * to Trino on SqlNode layer
 */
public class TrinoSqlCallTransformerUtil {
  public static final SqlOperator TIMESTAMP_OPERATOR =
      new SqlUserDefinedFunction(new SqlIdentifier("timestamp", SqlParserPos.ZERO), FunctionReturnTypes.TIMESTAMP, null,
          OperandTypes.STRING, null, null) {
        @Override
        public void unparse(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
          // for timestamp operator, we need to construct `CAST(x AS TIMESTAMP)`
          Preconditions.checkState(call.operandCount() == 1);
          final SqlWriter.Frame frame = writer.startFunCall("CAST");
          call.operand(0).unparse(writer, 0, 0);
          writer.sep("AS");
          writer.literal("TIMESTAMP");
          writer.endFunCall(frame);
        }
      };

  public static final SqlOperator DATE_OPERATOR = new SqlUserDefinedFunction(
      new SqlIdentifier("date", SqlParserPos.ZERO), ReturnTypes.DATE, null, OperandTypes.STRING, null, null);
  private static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();

  public static SqlOperator createSqlUDF(String functionName, SqlReturnTypeInference typeInference) {
    return new SqlUserDefinedFunction(
        new SqlIdentifier(com.linkedin.coral.com.google.common.collect.ImmutableList.of(functionName),
            SqlParserPos.ZERO),
        typeInference, null, null, null, null);
  }

  public static SqlOperator linkedInFunctionToCoralSqlOperator(String className) {
    return HIVE_FUNCTION_REGISTRY.lookup(className).iterator().next().getSqlOperator();
  }

  public static SqlOperator hiveToCoralSqlOperator(String functionName) {
    Collection<Function> lookup = HIVE_FUNCTION_REGISTRY.lookup(functionName);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  public static void addLinkedInFunctionTransformerFromHiveRegistry(List<SqlCallTransformer> sqlCallTransformerList,
      Set<String> linkedInFunctionSignatureSet) {
    ImmutableMultimap<String, Function> registry = HIVE_FUNCTION_REGISTRY.getRegistry();
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    for (Map.Entry<String, Function> entry : registry.entries()) {
      // we cannot use entry.getKey() as function name directly, because keys are all lowercase, which will
      // fail to be converted to lowercase with underscore correctly
      final String hiveFunctionName = entry.getValue().getFunctionName();
      if (!hiveFunctionName.startsWith("com.linkedin")) {
        continue;
      }
      String[] nameSplit = hiveFunctionName.split("\\.");
      // filter above guarantees we've at least 2 entries
      String className = nameSplit[nameSplit.length - 1];
      String funcName = caseConverter.convert(className);
      SqlOperator op = entry.getValue().getSqlOperator();
      for (int i = op.getOperandCountRange().getMin(); i <= op.getOperandCountRange().getMax(); i++) {
        if (!linkedInFunctionSignatureSet.contains(hiveFunctionName + "_" + i)) {
          sqlCallTransformerList.add(createSignatureBasedConditionSqlCallTransformer(op, i, funcName));
        }
      }
    }
  }

  public static SqlCallTransformer createSignatureBasedConditionSqlCallTransformer(SqlOperator coralOp, int numOperands,
      String trinoFuncName) {
    return new SignatureBasedConditionSqlCallTransformer(coralOp.getName(), numOperands,
        createSqlUDF(trinoFuncName, coralOp.getReturnTypeInference()));
  }

  public static SqlCallTransformer createSignatureBasedConditionSqlCallTransformer(SqlOperator coralOp, int numOperands,
      SqlOperator trinoOp) {
    return new SignatureBasedConditionSqlCallTransformer(coralOp.getName(), numOperands, trinoOp);
  }
}
