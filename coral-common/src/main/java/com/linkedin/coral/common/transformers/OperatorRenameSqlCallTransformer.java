/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.transformers;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;


/**
 * This class is a subclass of {@link SourceOperatorMatchSqlCallTransformer} which transform the operator name
 * from the name of the source operator to the name of the target operator
 */
public class OperatorRenameSqlCallTransformer extends SourceOperatorMatchSqlCallTransformer {
  private SqlOperator sourceOperator;
  private String targetOpName;

  public OperatorRenameSqlCallTransformer(SqlOperator sourceOperator, int numOperands, String targetOpName) {
    super(sourceOperator.getName(), numOperands);
    this.sourceOperator = sourceOperator;
    this.targetOpName = targetOpName;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    return createSqlOperator(targetOpName, sourceOperator.getReturnTypeInference())
        .createCall(new SqlNodeList(sqlCall.getOperandList(), SqlParserPos.ZERO));
  }
}
