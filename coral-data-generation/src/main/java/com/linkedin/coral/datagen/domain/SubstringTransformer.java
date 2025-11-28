package com.linkedin.coral.datagen.domain;

import java.util.List;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


public class SubstringTransformer implements DomainTransformer {

    @Override
    public boolean canHandle(RexNode expr) {
        return expr instanceof RexCall
            && ((RexCall) expr).getOperator().getKind() == SqlKind.OTHER_FUNCTION
            && ((RexCall) expr).getOperator().getName().equals("substr");
    }

    @Override
    public boolean isVariableOperandPositionValid(RexNode expr) {
        RexCall call = (RexCall) expr;
        RexNode arg0 = call.getOperands().get(0);
        RexNode arg1 = call.getOperands().get(1);
        RexNode arg2 = call.getOperands().get(2);
        return (arg0 instanceof RexInputRef || arg0 instanceof RexCall)
                && arg1 instanceof RexLiteral
                && arg2 instanceof RexLiteral;
    }

    @Override
    public RexNode getChildForVariable(RexNode expr) {
        RexCall call = (RexCall) expr;
        return call.getOperands().get(0);
    }

    @Override
    public Domain deriveChildDomain(RexNode expr, Domain outputDomain) {
        RexCall call = (RexCall) expr;
        List<RexNode> ops = call.getOperands();
        int start = ((RexLiteral) ops.get(1)).getValueAs(Integer.class) - 1;
        int len = ((RexLiteral) ops.get(2)).getValueAs(Integer.class);

        return new Domain() {
            @Override
            public String sample() {
                String target = outputDomain.sample();
                StringBuilder sb = new StringBuilder();
                while (sb.length() < start) {
                    sb.append('X');
                }
                if (target.length() != len) {
                    if (target.length() > len) {
                        target = target.substring(0, len);
                    } else {
                        StringBuilder padded = new StringBuilder(target);
                        while (padded.length() < len) {
                            padded.append('Y');
                        }
                        target = padded.toString();
                    }
                }
                sb.append(target);
                sb.append("_SUFFIX");
                return sb.toString();
            }

            @Override
            public boolean contains(String value) {
                if (value == null || value.length() < start + len) {
                    return false;
                }
                String sub = value.substring(start, start + len);
                return sub.equals(outputDomain.sample());
            }
        };
    }
}
