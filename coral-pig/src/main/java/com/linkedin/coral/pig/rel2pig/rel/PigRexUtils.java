package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;


//TODO(ralam): Add comments and clean up code
public class PigRexUtils {

  private PigRexUtils() {

  }

  public static String convertRexNodePigExpression(RexNode rexNode, List<String> inputRefColumnNameList) {
    if (rexNode instanceof RexInputRef) {
      return convertRexInputRef((RexInputRef) rexNode, inputRefColumnNameList);
    } else if (rexNode instanceof RexCall) {
      return convertRexCall((RexCall) rexNode, inputRefColumnNameList);
    } else if (rexNode instanceof RexLiteral) {
      return convertRexLiteral((RexLiteral) rexNode);
    }
    return rexNode.toString();
  }

  private static String convertRexInputRef(RexInputRef rexInputRef, List<String> inputRefColumnNameList) {
    if (rexInputRef.getIndex() >= inputRefColumnNameList.size()) {
      //TODO(ralam): Create better exception messages
      throw new RuntimeException(String.format(
          "RexInputRef failed to access field at index %d with RexInputRef column name mapping of size %d",
          rexInputRef.getIndex(), inputRefColumnNameList.size()));
    }
    return inputRefColumnNameList.get(rexInputRef.getIndex());
  }

  private static String convertRexLiteral(RexLiteral rexLiteral) {
    switch (rexLiteral.getTypeName()) {
      case CHAR:
        return String.format("'%s'", rexLiteral.toString());
      default:
        return rexLiteral.toString();
    }
  }

  private static String convertRexCall(RexCall rexCall, List<String> inputRefColumnNameList) {
    // TODO(ralam): Flesh out the RexCall function.

    List<String> operandExpressions = new ArrayList<>();

    for (RexNode operand : rexCall.getOperands()) {
      operandExpressions.add(convertRexNodePigExpression(operand, inputRefColumnNameList));
    }

    // TODO(ralam): Implement RexCall resolution. Throw an exception in the meantime.
    throw new UnsupportedOperationException("Function calls are not supported.");
  }
}
