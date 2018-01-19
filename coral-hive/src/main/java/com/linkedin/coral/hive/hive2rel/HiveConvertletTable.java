package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.sql2rel.SqlRexContext;
import org.apache.calcite.sql2rel.SqlRexConvertlet;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.StandardConvertletTable;


public class HiveConvertletTable implements SqlRexConvertletTable {

  @Override
  public SqlRexConvertlet get(SqlCall call) {
    SqlOperator operator = call.getOperator();
    if (operator instanceof SqlUserDefinedFunction && operator.getName().equals("named_struct")) {
      return new SqlRexConvertlet() {
        @Override
        public RexNode convertCall(SqlRexContext cx, SqlCall call) {
          List<RexNode> operandExpressions = new ArrayList<>(call.operandCount() / 2);
          for (int i = 0; i < call.operandCount(); i += 2) {
            operandExpressions.add(cx.convertExpression(call.operand(i + 1)));
          }
          RelDataType retType = cx.getValidator().getValidatedNodeType(call);
          RexNode rowNode = cx.getRexBuilder().makeCall(retType, SqlStdOperatorTable.ROW, operandExpressions);
          return cx.getRexBuilder().makeCast(retType, rowNode);
        }
      };
    }
    return StandardConvertletTable.INSTANCE.get(call);
  }
}
