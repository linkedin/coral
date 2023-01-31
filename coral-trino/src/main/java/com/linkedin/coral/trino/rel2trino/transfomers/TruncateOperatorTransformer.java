/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transfomers;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;

import com.linkedin.coral.common.transformers.OperatorBasedSqlCallTransformer;


/**
 * This class transforms a Coral SqlCall of "TRUNCATE" operator with 2 operands into a Trino SqlCall of an operator
 * named "TRUNCATE"
 */
public class TruncateOperatorTransformer extends OperatorBasedSqlCallTransformer {
  private static final String FROM_OPERATOR_NAME = "TRUNCATE";
  private static final int OPERAND_NUM = 2;
  private static final SqlOperator TARGET_OPERATOR =
      createSqlUDF("TRUNCATE", SqlStdOperatorTable.TRUNCATE.getReturnTypeInference());

  public TruncateOperatorTransformer() {
    super(FROM_OPERATOR_NAME, OPERAND_NUM, TARGET_OPERATOR);
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = transformOperands(sourceOperands);
    SqlCall newSqlCall = TARGET_OPERATOR.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
    return transformResult(newSqlCall, sourceOperands);
  }

  private List<SqlNode> transformOperands(List<SqlNode> sourceOperands) {
    SqlNode powerOpSqlNode = createPowerOpSqlNode(sourceOperands);

    List<SqlNode> operandsOfMultiplyOp = new ArrayList<>();
    operandsOfMultiplyOp.add(sourceOperands.get(0));
    operandsOfMultiplyOp.add(powerOpSqlNode);
    SqlNode multiplyOpSqlNode =
        SqlStdOperatorTable.MULTIPLY.createCall(new SqlNodeList(operandsOfMultiplyOp, SqlParserPos.ZERO));

    List<SqlNode> topLevelOperands = new ArrayList<>();
    topLevelOperands.add(multiplyOpSqlNode);
    return topLevelOperands;
  }

  private SqlCall transformResult(SqlNode result, List<SqlNode> sourceOperands) {
    List<SqlNode> newOperands = new ArrayList<>();
    newOperands.add(result);
    SqlNode powerOpSqlNode = createPowerOpSqlNode(sourceOperands);
    newOperands.add(powerOpSqlNode);
    return SqlStdOperatorTable.DIVIDE.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }

  private SqlCall createPowerOpSqlNode(List<SqlNode> sourceOperands) {
    List<SqlNode> operandsOfPowerOp = new ArrayList<>();
    operandsOfPowerOp.add(SqlLiteral.createExactNumeric(String.valueOf(10), SqlParserPos.ZERO));
    operandsOfPowerOp.add(sourceOperands.get(1));
    return SqlStdOperatorTable.POWER.createCall(new SqlNodeList(operandsOfPowerOp, SqlParserPos.ZERO));
  }
}
