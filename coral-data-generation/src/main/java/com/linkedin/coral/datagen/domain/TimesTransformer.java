package com.linkedin.coral.datagen.domain;

import java.util.List;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

public class TimesTransformer implements DomainTransformer {

    @Override
    public boolean canHandle(RexNode expr) {
        return expr instanceof RexCall
            && ((RexCall) expr).getOperator() == SqlStdOperatorTable.MULTIPLY;
    }

    @Override
    public boolean isVariableOperandPositionValid(RexNode expr) {
        RexCall call = (RexCall) expr;
        RexNode left = call.getOperands().get(0);
        RexNode right = call.getOperands().get(1);
        boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall);
        boolean rightVar = (right instanceof RexInputRef || right instanceof RexCall);
        boolean leftLit = left instanceof RexLiteral;
        boolean rightLit = right instanceof RexLiteral;
        return (leftVar && rightLit) || (rightVar && leftLit);
    }

    @Override
    public RexNode getChildForVariable(RexNode expr) {
        RexCall call = (RexCall) expr;
        RexNode left = call.getOperands().get(0);
        RexNode right = call.getOperands().get(1);
        if (left instanceof RexInputRef || left instanceof RexCall) {
            return left;
        } else {
            return right;
        }
    }

    @Override
    public Domain deriveChildDomain(RexNode expr, Domain outputDomain) {
        RexCall call = (RexCall) expr;
        RexNode left = call.getOperands().get(0);
        RexNode right = call.getOperands().get(1);
        boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall);
        int literal = ((RexLiteral) (leftVar ? right : left)).getValueAs(Integer.class);

        return new Domain() {
            @Override
            public String sample() {
                int out = Integer.parseInt(outputDomain.sample());
                int x = out / literal;
                return String.valueOf(x);
            }

            @Override
            public boolean contains(String value) {
                int x = Integer.parseInt(value);
                int result = leftVar ? x * literal : literal * x;
                return result == Integer.parseInt(outputDomain.sample());
            }
        };
    }
}
