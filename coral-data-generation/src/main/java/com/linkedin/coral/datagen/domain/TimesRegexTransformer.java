package com.linkedin.coral.datagen.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;

/**
 * Integer domain transformer for TIMES (multiplication) operations.
 * 
 * Inverts x * literal = output_value
 * to produce input constraint x = output_value / literal
 * 
 * Example:
 * x * 2 = 50
 * produces input constraint: x = 25
 * which becomes domain: {25}
 *
 * For interval constraints:
 * x * 2 in [20, 40]
 * produces: x in [10, 20]
 *
 * Handles cases where division is not exact by filtering valid values.
 */
public class TimesRegexTransformer implements DomainTransformer {

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
    public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
        if (!(outputDomain instanceof IntegerDomain)) {
            throw new IllegalArgumentException(
                "TimesRegexTransformer expects IntegerDomain but got " + outputDomain.getClass().getSimpleName());
        }
        
        IntegerDomain intDomain = (IntegerDomain) outputDomain;
        RexCall call = (RexCall) expr;
        RexNode left = call.getOperands().get(0);
        RexNode right = call.getOperands().get(1);
        boolean leftVar = (left instanceof RexInputRef || left instanceof RexCall);
        
        // Get the literal value
        RexLiteral literalNode = (RexLiteral) (leftVar ? right : left);
        long literal = literalNode.getValueAs(Long.class);
        
        if (literal == 0) {
            // x * 0 = output
            // Only valid if output contains 0
            if (intDomain.contains(0)) {
                // Any input works
                return IntegerDomain.all();
            } else {
                // Contradiction
                return IntegerDomain.empty();
            }
        }
        
        // Invert multiplication: x * literal = output => x = output / literal
        // For each interval in output, compute the corresponding input interval
        List<IntegerDomain.Interval> inputIntervals = new ArrayList<>();
        
        for (IntegerDomain.Interval outputInterval : intDomain.getIntervals()) {
            long outMin = outputInterval.getMin();
            long outMax = outputInterval.getMax();
            
            // Compute input range that could produce this output
            // We need x such that x * literal is in [outMin, outMax]
            long inMin, inMax;
            
            if (literal > 0) {
                // Positive multiplier: order preserved
                // x * literal >= outMin => x >= ceil(outMin / literal)
                inMin = ceilDiv(outMin, literal);
                // x * literal <= outMax => x <= floor(outMax / literal)
                inMax = floorDiv(outMax, literal);
            } else {
                // Negative multiplier: order reversed
                // x * literal >= outMin => x <= floor(outMin / literal)
                inMax = floorDiv(outMin, literal);
                // x * literal <= outMax => x >= ceil(outMax / literal)
                inMin = ceilDiv(outMax, literal);
            }
            
            if (inMin <= inMax) {
                inputIntervals.add(new IntegerDomain.Interval(inMin, inMax));
            }
        }
        
        return IntegerDomain.of(inputIntervals);
    }
    
    /**
     * Ceiling division: ceil(a / b)
     */
    private long ceilDiv(long a, long b) {
        if (b > 0) {
            return (a >= 0) ? (a + b - 1) / b : a / b;
        } else {
            return (a <= 0) ? (a + b + 1) / b : a / b;
        }
    }
    
    /**
     * Floor division: floor(a / b)
     */
    private long floorDiv(long a, long b) {
        if (b > 0) {
            return (a >= 0) ? a / b : (a - b + 1) / b;
        } else {
            return (a <= 0) ? a / b : (a - b - 1) / b;
        }
    }
}
