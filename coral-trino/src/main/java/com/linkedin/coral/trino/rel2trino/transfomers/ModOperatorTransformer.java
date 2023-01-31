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
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;

import static com.linkedin.coral.trino.rel2trino.utils.CoralToTrinoSqlCallTransformersUtil.*;


/**
 * This class transforms a Coral SqlCall of "pmod" operator with 2 operands into a Trino SqlCall of an operator
 * named "mod"
 */
public class ModOperatorTransformer extends OperatorBasedSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "pmod";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlUDF("mod", hiveToCoralSqlOperator(FROM_OPERATOR_NAME).getReturnTypeInference());

  public ModOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM, TARGET_OPERATOR);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = transformOperands(sourceOperands);
    return TARGET_OPERATOR.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }

  private List<SqlNode> transformOperands(List<SqlNode> sourceOperands) {
    List<SqlNode> newTopLevelOperands = new ArrayList<>();

    SqlNode modOpSqlNode = SqlStdOperatorTable.MOD.createCall(new SqlNodeList(sourceOperands, SqlParserPos.ZERO));
    List<SqlNode> operandsOfPlusOp = new ArrayList<>();
    operandsOfPlusOp.add(modOpSqlNode);
    operandsOfPlusOp.add(sourceOperands.get(1));
    SqlNode plusOpSqlNode = SqlStdOperatorTable.PLUS.createCall(new SqlNodeList(operandsOfPlusOp, SqlParserPos.ZERO));

    newTopLevelOperands.add(plusOpSqlNode);
    newTopLevelOperands.add(sourceOperands.get(1));
    return newTopLevelOperands;
  }
}
