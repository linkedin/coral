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

import com.linkedin.coral.common.transformers.SignatureBasedConditionSqlCallTransformer;

import static com.linkedin.coral.trino.rel2trino.utils.TrinoSqlCallTransformerUtil.*;


/**
 * This class transforms a Coral SqlCall of "decode" operator with 2 operands into a Trino SqlCall of an operator
 * named "[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]"
 */
public class DecodeOperatorTransformer extends SignatureBasedConditionSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "decode";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlUDF("[{\"regex\":\"(?i)('utf-8')\", \"input\":2, \"name\":\"from_utf8\"}]",
          hiveToCoralSqlOperator("decode").getReturnTypeInference());

  public DecodeOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM, TARGET_OPERATOR);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = new ArrayList<>();
    newOperands.add(sourceOperands.get(0));
    return TARGET_OPERATOR.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }
}
