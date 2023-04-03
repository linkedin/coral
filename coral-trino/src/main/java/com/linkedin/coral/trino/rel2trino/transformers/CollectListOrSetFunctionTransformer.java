/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.common.transformers.SqlCallTransformer;


/**
 * This class implements the transformation from the operations "collect_list" and "collect_set" to their
 * respective Trino-compatible versions.
 *
 * For example, "collect_list(col)" is transformed into "array_agg(col)", and
 * "collect_set(col)" is transformed into "array_distinct(array_agg(col))".
 */
public class CollectListOrSetFunctionTransformer extends SqlCallTransformer {

  private static final String COLLECT_LIST = "collect_list";
  private static final String COLLECT_SET = "collect_set";
  private static final String ARRAY_AGG = "array_agg";
  private static final String ARRAY_DISTINCT = "array_distinct";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    final String operatorName = sqlCall.getOperator().getName();
    return operatorName.equalsIgnoreCase(COLLECT_LIST) || operatorName.equalsIgnoreCase(COLLECT_SET);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    final String operatorName = sqlCall.getOperator().getName();
    final SqlOperator arrayAgg = createSqlOperator(ARRAY_AGG, FunctionReturnTypes.ARRAY_OF_ARG0_TYPE);
    final SqlOperator arrayDistinct = createSqlOperator(ARRAY_DISTINCT, FunctionReturnTypes.ARRAY_OF_ARG0_TYPE);

    final List<SqlNode> operands = new ArrayList<>(sqlCall.getOperandList());

    if (operatorName.equalsIgnoreCase(COLLECT_LIST)) {
      return arrayAgg.createCall(SqlParserPos.ZERO, operands);
    } else {
      return arrayDistinct.createCall(SqlParserPos.ZERO, arrayAgg.createCall(SqlParserPos.ZERO, operands));
    }
  }
}
