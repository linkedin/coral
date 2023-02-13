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
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SourceOperatorMatchSqlCallTransformer;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;
import static com.linkedin.coral.trino.rel2trino.CoralToTrinoSqlCallConverter.*;


/**
 * This class is a subclass of {@link SourceOperatorMatchSqlCallTransformer} which transforms a Coral SqlCall of "date_add"
 * operator with 2 operands into a Trino SqlCall of an operator named "date_add"
 */
public class DateAddOperatorTransformer extends SourceOperatorMatchSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "date_add";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlOperator("date_add", hiveToCoralSqlOperator("date_add").getReturnTypeInference());

  public DateAddOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM);
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
