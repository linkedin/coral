package com.linkedin.coral.trino.rel2trino;

import com.linkedin.coral.common.transformers.OperatorTransformer;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;


public class ModOperatorTransformer extends OperatorTransformer {
  private static final String FROM_OPERATOR_NAME = "pmod";
  private static final int OPERAND_NUM = 2;

  private final SqlOperator targetOperator;

  public ModOperatorTransformer(SqlOperator trinoOperator) {
    this.targetOperator = trinoOperator;
  }

  @Override
  protected boolean condition(SqlCall sqlCall) {
    return FROM_OPERATOR_NAME.equalsIgnoreCase(sqlCall.getOperator().getName())
        && sqlCall.getOperandList().size() == OPERAND_NUM;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    List<SqlNode> sourceOperands = sqlCall.getOperandList();
    List<SqlNode> newOperands = transformOperands(sourceOperands);
    return targetOperator.createCall(new SqlNodeList(newOperands, SqlParserPos.ZERO));
  }

  private List<SqlNode> transformOperands(List<SqlNode> sourceOperands) {
    List<SqlNode> newTopOperands = new ArrayList<>();

    SqlNode modOpSqlNode = SqlStdOperatorTable.MOD.createCall(new SqlNodeList(sourceOperands, SqlParserPos.ZERO));
    List<SqlNode> operandsOfPlusOp = new ArrayList<>();
    operandsOfPlusOp.add(modOpSqlNode);
    operandsOfPlusOp.add(sourceOperands.get(1));
    SqlNode plusOpSqlNode = SqlStdOperatorTable.PLUS.createCall(new SqlNodeList(operandsOfPlusOp, SqlParserPos.ZERO));

    newTopOperands.add(plusOpSqlNode);
    newTopOperands.add(sourceOperands.get(1));
    return newTopOperands;
  }


}
