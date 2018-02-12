package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.functions.HiveInOperator;
import com.linkedin.coral.hive.hive2rel.functions.FunctionFieldReferenceOperator;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
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
    // TODO: Turn this is into a hashmap before adding more operators
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
    } else if (operator instanceof HiveInOperator) {
      return new SqlRexConvertlet() {
        @Override
        public RexNode convertCall(SqlRexContext cx, SqlCall call) {
          List<SqlNode> operandList = call.getOperandList();
          Preconditions.checkState(operandList.size() == 2 && operandList.get(1) instanceof SqlNodeList);
          RexNode lhs = cx.convertExpression(operandList.get(0));
          SqlNodeList rhsNodes = (SqlNodeList) operandList.get(1);
          ImmutableList.Builder<RexNode> rexNodes = ImmutableList.<RexNode>builder().add(lhs);
          for (int i = 0; i < rhsNodes.size(); i++) {
            rexNodes.add(cx.convertExpression(rhsNodes.get(i)));
          }

          RelDataType retType = cx.getValidator().getValidatedNodeType(call);
          return cx.getRexBuilder().makeCall(retType, HiveInOperator.IN, rexNodes.build());
        }
      };
    } else if (operator instanceof FunctionFieldReferenceOperator) {
      return new SqlRexConvertlet() {
        @Override
        public RexNode convertCall(SqlRexContext cx, SqlCall call) {
          RexNode funcExpr = cx.convertExpression(call.operand(0));
          String fieldName = FunctionFieldReferenceOperator.fieldNameStripQuotes(call.operand(1));
          return cx.getRexBuilder().makeFieldAccess(funcExpr, fieldName, false);
        }
      };
    }
    return StandardConvertletTable.INSTANCE.get(call);
  }
}
