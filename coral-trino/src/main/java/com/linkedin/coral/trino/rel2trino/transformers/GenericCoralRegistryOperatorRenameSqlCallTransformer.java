/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.com.google.common.base.CaseFormat;
import com.linkedin.coral.com.google.common.base.Converter;
import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;


public class GenericCoralRegistryOperatorRenameSqlCallTransformer extends SqlCallTransformer {

  private static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();

  @Override
  protected boolean condition(SqlCall sqlCall) {
    String operatorName = sqlCall.getOperator().getName();
    return HIVE_FUNCTION_REGISTRY.getRegistry().containsKey(operatorName) && operatorName.startsWith("com.linkedin");
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    Converter<String, String> caseConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    SqlOperator sourceOp =
        HIVE_FUNCTION_REGISTRY.lookup(sqlCall.getOperator().getName()).iterator().next().getSqlOperator();
    String[] nameSplit = sourceOp.getName().split("\\.");
    String targetName = caseConverter.convert(nameSplit[nameSplit.length - 1]);
    SqlOperator targetOp = createSqlOperatorOfFunction(targetName, sourceOp.getReturnTypeInference());
    return targetOp.createCall(new SqlNodeList(sqlCall.getOperandList(), SqlParserPos.ZERO));
  }
}
