/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import com.linkedin.relocated.org.apache.calcite.sql.SqlCall;
import com.linkedin.relocated.org.apache.calcite.sql.SqlNodeList;
import com.linkedin.relocated.org.apache.calcite.sql.SqlOperator;
import com.linkedin.relocated.org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.VersionedSqlUserDefinedFunction;


/**
 * This transformer converts the Hive UDF SqlCall name from the UDF class name to the
 * corresponding Trino function name.
 * i.e. from `com.linkedin.stdudfs.parsing.hive.Ip2Str` to `ip2str`.
 */
public class HiveUDFTransformer extends SqlCallTransformer {

  @Override
  protected boolean condition(SqlCall sqlCall) {
    final SqlOperator operator = sqlCall.getOperator();
    final String operatorName = operator.getName();
    return operator instanceof VersionedSqlUserDefinedFunction && operatorName.contains(".")
        && !operatorName.equals(".");
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final SqlOperator operator = sqlCall.getOperator();
    final String trinoFunctionName = ((VersionedSqlUserDefinedFunction) operator).getShortFunctionName();
    return createSqlOperator(trinoFunctionName, operator.getReturnTypeInference())
        .createCall(new SqlNodeList(sqlCall.getOperandList(), SqlParserPos.ZERO));
  }
}
