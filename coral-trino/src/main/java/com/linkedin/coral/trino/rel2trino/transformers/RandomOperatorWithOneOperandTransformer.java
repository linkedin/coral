/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.Collections;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.SourceOperatorMatchSqlCallTransformer;

import static com.linkedin.coral.common.calcite.CalciteUtil.*;


/**
 * This class is a subclass of {@link SourceOperatorMatchSqlCallTransformer} transforms a Coral SqlCall of "RAND" operator
 * with 1 operand into a Trino SqlCall of an operator named "RANDOM"
 */
public class RandomOperatorWithOneOperandTransformer extends SourceOperatorMatchSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "RAND";
  private static final int OPERAND_NUM = 1;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlOperator("RANDOM", SqlStdOperatorTable.RAND.getReturnTypeInference());

  public RandomOperatorWithOneOperandTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return createCall(TARGET_OPERATOR, Collections.emptyList(), SqlParserPos.ZERO);
  }
}
