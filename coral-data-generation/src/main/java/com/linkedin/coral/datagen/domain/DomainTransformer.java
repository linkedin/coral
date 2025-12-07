package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rex.RexNode;

/**
 * Transformer interface for symbolic domain-based constraint propagation.
 * Each transformer refines domain constraints on its input expression based on
 * the domain constraint on its output.
 * 
 * This interface unifies constraint propagation across different domain types
 * (RegexDomain, IntegerDomain, etc.) and supports cross-domain conversions
 * (e.g., CAST between numeric and string domains).
 * 
 * The interface is intentionally non-parameterized to allow transformers to
 * convert between different domain types as needed.
 */
public interface DomainTransformer {
    
    /**
     * Checks if this transformer can handle the given expression.
     * 
     * @param expr the RexNode expression to check
     * @return true if this transformer can handle the expression
     */
    boolean canHandle(RexNode expr);
    
    /**
     * Checks if the variable operand position is valid for domain inference.
     * For example, in PLUS(x, 5), x must be the variable operand.
     * 
     * @param expr the expression to validate
     * @return true if the variable operand position is valid
     */
    boolean isVariableOperandPositionValid(RexNode expr);
    
    /**
     * Gets the child expression that contains the variable.
     * This is used to traverse inward through the expression tree.
     * 
     * @param expr the parent expression
     * @return the child expression containing the variable
     */
    RexNode getChildForVariable(RexNode expr);
    
    /**
     * Refines the domain constraint on the input expression based on
     * the domain constraint on the output.
     * 
     * Examples:
     * - SUBSTRING(input, 1, 4) with output RegexDomain("2000")
     *   produces input RegexDomain("^2000.*$") (starts with 2000)
     * - x + 5 with output IntegerDomain([20, 30])
     *   produces input IntegerDomain([15, 25])
     * - CAST(x AS VARCHAR) with output RegexDomain("123")
     *   produces input IntegerDomain([123, 123])
     * 
     * @param expr the expression being inverted
     * @param outputDomain the domain constraint on the output (may be RegexDomain, IntegerDomain, etc.)
     * @return the refined domain constraint on the input (may be a different domain type than output)
     */
    Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain);
}
