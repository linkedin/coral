/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.com.google.common.collect.ImmutableMultimap;
import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.transformers.OperatorTransformer;
import com.linkedin.coral.common.transformers.OperatorTransformerList;
import com.linkedin.coral.common.transformers.StandardOperatorTransformer;
import com.linkedin.coral.hive.hive2rel.functions.HiveRLikeOperator;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.rel2trino.functions.TrinoElementAtFunction;


public class CalciteTrinoOperatorTransformers extends OperatorTransformerList {
  private static final StaticHiveFunctionRegistry HIVE_REGISTRY = new StaticHiveFunctionRegistry();
  private static CalciteTrinoOperatorTransformers instance;
  private CalciteTrinoOperatorTransformers() {
  }

  private CalciteTrinoOperatorTransformers(List<OperatorTransformer> operatorTransformers) {
    super(operatorTransformers);
  }

  public static CalciteTrinoOperatorTransformers getInstance() {
    if (instance == null) {
      return new CalciteTrinoOperatorTransformers(initializeOperatorTransformers());
    }
    return instance;
  }

  private static List<OperatorTransformer> initializeOperatorTransformers() {
    List<OperatorTransformer> operatorTransformers = new ArrayList<>();

    SqlOperator calciteOp = hiveToCalciteOp("pmod");
    SqlOperator trinoOp = UDFMapUtils.createUDF("mod", calciteOp.getReturnTypeInference());
    OperatorTransformer transformer = new ModOperatorTransformer(trinoOp);
    operatorTransformers.add(transformer);

    return operatorTransformers;
  }

  private static SqlOperator hiveToCalciteOp(String functionName) {
    Collection<Function> lookup = HIVE_REGISTRY.lookup(functionName);
    // TODO: provide overloaded function resolution
    return lookup.iterator().next().getSqlOperator();
  }

  private static void addDaliFunctionId(Set<String> daliFunctionIdSet, String daliFunctionName, int operandNum) {
    daliFunctionIdSet.add(daliFunctionName + "_" + operandNum);
  }

  private static void addDaliUDFs(List<OperatorTransformer> operatorTransformers, Set<String> daliFunctionIdSet) {
    ImmutableMultimap<String, Function> registry = HIVE_REGISTRY.getRegistry();
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
        if (!daliFunctionIdSet.contains(hiveFunctionName + "_" + i)) {
          operatorTransformers.add(createOperatorTransformer(op, i, funcName));
        }
      }
    }
  }

  private static SqlOperator daliToCalciteOp(String className) {
    return HIVE_REGISTRY.lookup(className).iterator().next().getSqlOperator();
  }

  private static OperatorTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      String trinoUDFName) {
    return createOperatorTransformer(calciteOp, numOperands,
        UDFMapUtils.createUDF(trinoUDFName, calciteOp.getReturnTypeInference()));
  }

  private static OperatorTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      String trinoUDFName, String operandTransformer, String resultTransformer) {
    return createOperatorTransformer(calciteOp, numOperands,
        UDFMapUtils.createUDF(trinoUDFName, calciteOp.getReturnTypeInference()), operandTransformer, resultTransformer);
  }

  private static OperatorTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp) {
    return createOperatorTransformer(calciteOp, numOperands, trinoOp, null, null, null);
  }

  private static OperatorTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp, String operandTransformer, String resultTransformer) {
    return createOperatorTransformer(calciteOp, numOperands, trinoOp, operandTransformer, resultTransformer, null);
  }

  private static OperatorTransformer createOperatorTransformer(SqlOperator calciteOp, int numOperands,
      SqlOperator trinoOp, String operandTransformer, String resultTransformer, String operatorTransformer) {
    return new StandardOperatorTransformer(calciteOp.getName(), numOperands, trinoOp, operandTransformer,
        resultTransformer, operatorTransformer);
  }
}
