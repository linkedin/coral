package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;

/**
 * Regex-based transformer for SUBSTRING operations.
 *
 * Inverts SUBSTRING(input, start, len) = output_pattern
 * to produce a positional regex constraint on the input.
 *
 * Example:
 * SUBSTRING(input, 1, 4) = "2000"
 * produces input constraint: ^2000.*$
 *
 * SUBSTRING(input, 5, 2) = "US"
 * produces input constraint: ^.{4}US.*$
 */
public class SubstringRegexTransformer implements DomainTransformer {

    @Override
    public boolean canHandle(RexNode expr) {
        return expr instanceof RexCall
            && ((RexCall) expr).getOperator().getKind() == SqlKind.OTHER_FUNCTION
            && ((RexCall) expr).getOperator().getName().equals("substr");
    }

    @Override
    public boolean isVariableOperandPositionValid(RexNode expr) {
        RexCall call = (RexCall) expr;
        // SUBSTRING has 3 operands: (string, start, length)
        // The first operand (string) must be the variable
        RexNode stringArg = call.getOperands().get(0);
        RexNode startArg = call.getOperands().get(1);
        RexNode lengthArg = call.getOperands().get(2);

        // String arg must be variable (RexInputRef) or another call (nested expression)
        boolean stringIsVariable = (stringArg instanceof RexInputRef) || (stringArg instanceof RexCall);

        // Start and length must be literals
        boolean startIsLiteral = startArg instanceof RexLiteral;
        boolean lengthIsLiteral = lengthArg instanceof RexLiteral;

        return stringIsVariable && startIsLiteral && lengthIsLiteral;
    }

    @Override
    public RexNode getChildForVariable(RexNode expr) {
        RexCall call = (RexCall) expr;
        return call.getOperands().get(0);
    }

    @Override
    public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
        if (!(outputDomain instanceof RegexDomain)) {
            throw new IllegalArgumentException(
                "SubstringRegexTransformer expects RegexDomain but got " + outputDomain.getClass().getSimpleName());
        }
        
        RegexDomain outputRegex = (RegexDomain) outputDomain;
        RexCall call = (RexCall) expr;

        // Extract start and length from literals
        RexLiteral startLiteral = (RexLiteral) call.getOperands().get(1);
        RexLiteral lengthLiteral = (RexLiteral) call.getOperands().get(2);

        int start = startLiteral.getValueAs(Integer.class);
        int length = lengthLiteral.getValueAs(Integer.class);

        // Convert to 0-based index
        int startIdx = start - 1;

        if (startIdx < 0 || length < 0) {
            // Invalid substring parameters
            return RegexDomain.empty();
        }

        // Get the output pattern
        String outputPattern = outputRegex.getRegex();
        
        // Strip anchors from output pattern since we'll add our own
        String patternWithoutAnchors = outputPattern;
        if (patternWithoutAnchors.startsWith("^")) {
            patternWithoutAnchors = patternWithoutAnchors.substring(1);
        }
        if (patternWithoutAnchors.endsWith("$")) {
            patternWithoutAnchors = patternWithoutAnchors.substring(0, patternWithoutAnchors.length() - 1);
        }

        // If output is a literal, we can compute expected length
        if (outputRegex.isLiteral()) {
            // The output must have exactly 'length' characters
            // Unescape the literal to get actual length
            String literalValue = unescapeLiteral(outputPattern);
            if (literalValue.length() != length) {
                // Contradiction: output length doesn't match substring length
                return RegexDomain.empty();
            }
        }

        // Create positional constraint: ^.{startIdx}(outputPattern).*$
        String inputRegex = String.format("^.{%d}(%s).*$", startIdx, patternWithoutAnchors);

        return new RegexDomain(inputRegex);
    }

    /**
     * Unescapes a regex literal to get the actual string value.
     * Handles anchored patterns like "^50$".
     */
    private String unescapeLiteral(String escaped) {
        String result = escaped;
        // Remove anchors if present
        if (result.startsWith("^")) {
            result = result.substring(1);
        }
        if (result.endsWith("$")) {
            result = result.substring(0, result.length() - 1);
        }
        // Remove escape sequences
        return result.replaceAll("\\\\(.)", "$1");
    }
}
