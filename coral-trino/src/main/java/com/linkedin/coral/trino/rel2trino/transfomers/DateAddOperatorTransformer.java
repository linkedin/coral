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
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;
import static com.linkedin.coral.trino.rel2trino.utils.CoralToTrinoSqlCallTransformersUtil.*;


/**
 * This class transforms a Coral SqlCall of "date_add" operator with 2 operands into a Trino SqlCall of an operator
 * named "date_add"
 */
public class DateAddOperatorTransformer extends OperatorBasedSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "date_add";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlUDF("date_add", hiveToCoralSqlOperator("date_add").getReturnTypeInference());

  public DateAddOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM, TARGET_OPERATOR);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = new ArrayList<>();
    newOperands.add(createStringLiteral("day", SqlParserPos.ZERO));
    newOperands.add(sourceOperands.get(1));

    List<SqlNode> timestampOperatorOperands = new ArrayList<>();
    timestampOperatorOperands.add(sourceOperands.get(0));
    SqlCall timestampSqlCall =
        TIMESTAMP_OPERATOR.createCall(new SqlNodeList(timestampOperatorOperands, SqlParserPos.ZERO));

    List<SqlNode> dateOperatorOperands = new ArrayList<>();
    dateOperatorOperands.add(timestampSqlCall);
    SqlCall dateOpSqlCall = DATE_OPERATOR.createCall(new SqlNodeList(dateOperatorOperands, SqlParserPos.ZERO));
    newOperands.add(dateOpSqlCall);

    return TARGET_OPERATOR.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }
}
