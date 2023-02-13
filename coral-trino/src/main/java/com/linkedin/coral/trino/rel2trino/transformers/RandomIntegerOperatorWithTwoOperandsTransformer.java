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
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SourceOperatorMatchSqlCallTransformer;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * This class is a subclass of {@link SourceOperatorMatchSqlCallTransformer} transforms a Coral SqlCall of "RAND_INTEGER" operator
 * with 2 operands into a Trino SqlCall of an operator named "RANDOM"
 */
public class RandomIntegerOperatorWithTwoOperandsTransformer extends SourceOperatorMatchSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "RAND_INTEGER";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlOperator("RANDOM", SqlStdOperatorTable.RAND_INTEGER.getReturnTypeInference());

  public RandomIntegerOperatorWithTwoOperandsTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> newOperands = new ArrayList<>();
    newOperands.add(sqlCall.getOperandList().get(1));
    return createCall(TARGET_OPERATOR, newOperands, SqlParserPos.ZERO);
  }
}
