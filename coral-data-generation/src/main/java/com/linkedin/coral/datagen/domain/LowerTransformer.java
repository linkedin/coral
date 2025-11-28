package com.linkedin.coral.datagen.domain;

import java.util.List;
import java.util.Random;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

public class LowerTransformer implements DomainTransformer {
    private final Random random = new Random();

    @Override
    public boolean canHandle(RexNode expr) {
        return expr instanceof RexCall
            && ((RexCall) expr).getOperator() == SqlStdOperatorTable.LOWER;
    }

    @Override
    public boolean isVariableOperandPositionValid(RexNode expr) {
        RexCall call = (RexCall) expr;
        RexNode arg = call.getOperands().get(0);
        return arg instanceof RexInputRef || arg instanceof RexCall;
    }

    @Override
    public RexNode getChildForVariable(RexNode expr) {
        RexCall call = (RexCall) expr;
        return call.getOperands().get(0);
    }

    @Override
    public Domain deriveChildDomain(RexNode expr, Domain outputDomain) {
        return new Domain() {
            @Override
            public String sample() {
                String lower = outputDomain.sample();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < lower.length(); i++) {
                    char c = lower.charAt(i);
                    if (Character.isLetter(c) && random.nextBoolean()) {
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }

            @Override
            public boolean contains(String value) {
                return value != null && value.toLowerCase().equals(outputDomain.sample());
            }
        };
    }
}
