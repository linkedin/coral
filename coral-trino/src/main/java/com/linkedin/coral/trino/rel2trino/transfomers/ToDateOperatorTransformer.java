/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transfomers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SqlCallTransformer;
import com.linkedin.coral.trino.rel2trino.utils.CoralToTrinoSqlCallTransformersUtil;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 *  This class implements the transformation from the operation of "to_date"
 *  for example, "to_date('2023-01-01')" is transformed into "date(CAST('2023-01-01') AS TIMESTAMP)"
 */
public class ToDateOperatorTransformer extends SqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "to_date";
  private static final String TO_OPERATOR_NAME = "date";
  private static final int NUM_OPERANDS = 1;
  private static final SqlOperator TRINO_OPERATOR = createSqlUDF(TO_OPERATOR_NAME,
      CoralToTrinoSqlCallTransformersUtil.hiveToCoralSqlOperator(FROM_OPERATOR_NAME).getReturnTypeInference());

  private final boolean avoidTransformToDateUDF;

  public ToDateOperatorTransformer(boolean avoidTransformToDateUDF) {
    this.avoidTransformToDateUDF = avoidTransformToDateUDF;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return !avoidTransformToDateUDF && FROM_OPERATOR_NAME.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == NUM_OPERANDS;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = new ArrayList<>();
    SqlNode timestampSqlCall = createCall(TIMESTAMP_OPERATOR, sourceOperands, SqlParserPos.ZERO);
    newOperands.add(timestampSqlCall);
    return createCall(TRINO_OPERATOR, newOperands, SqlParserPos.ZERO);
  }
}
