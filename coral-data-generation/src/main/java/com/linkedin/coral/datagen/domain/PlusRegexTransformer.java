package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

/**
 * Integer domain transformer for PLUS (addition) operations.
 * 
 * Inverts x + literal = output_value
 * to produce input constraint x = output_value - literal
 * 
 * Example:
 * x + 5 = 25
 * produces input constraint: x = 20
 * which becomes domain: {20}
 *
 * For interval constraints:
 * x + 5 in [20, 30]
 * produces: x in [15, 25]
 */
public class PlusRegexTransformer implements DomainTransformer {

    @Override
    public boolean canHandle(RexNode expr) {
        return expr instanceof RexCall
            && ((RexCall) expr).getOperator() == SqlStdOperatorTable.PLUS;
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
    public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
        if (!(outputDomain instanceof IntegerDomain)) {
            throw new IllegalArgumentException(
                "PlusRegexTransformer expects IntegerDomain but got " + outputDomain.getClass().getSimpleName());
        }
        
        IntegerDomain intDomain = (IntegerDomain) outputDomain;
        RexCall call = (RexCall) expr;
        RexNode left = call.getOperands().get(0);
        RexNode right = call.getOperands().get(1);
        boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall);
        
        // Get the literal value
        RexLiteral literalNode = (RexLiteral) (leftVar ? right : left);
        long literal = literalNode.getValueAs(Long.class);
        
        // Invert the addition: x + literal = output => x = output - literal
        return intDomain.add(-literal);
    }
}
