/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import javax.annotation.Nonnull;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;

import static com.linkedin.coral.trino.rel2trino.CoralToTrinoSqlCallConverter.*;


/**
 * This is a subclass of OperatorBasedSqlCallTransformer which transforms a Coral operator into a Trino operator
 * on SqlNode layer for the LinkedIn specific functions
 */
public class LinkedInOperatorBasedSqlCallTransformer extends OperatorBasedSqlCallTransformer {

  public LinkedInOperatorBasedSqlCallTransformer(@Nonnull String linkedInFuncName, int numOperands,
      @Nonnull String targetOpName) {
    super(HIVE_FUNCTION_REGISTRY.lookup(linkedInFuncName).iterator().next().getSqlOperator(), numOperands,
        targetOpName);
  }

  public LinkedInOperatorBasedSqlCallTransformer(@Nonnull SqlOperator fromOperator, int numOperands,
      @Nonnull String targetOpName) {
    super(fromOperator, numOperands, targetOpName);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return super.getTargetOperator().createCall(new SqlNodeList(sqlCall.getOperandList(), SqlParserPos.ZERO));
  }
}
