package com.linkedin.coral.datagen.domain;

import java.util.List;
import org.apache.calcite.rex.RexNode;

/**
 * Backward-compatible entry point for regex-based domain inference.
 * 
 * This class delegates to the generic DomainInferenceProgram but ensures
 * the result is always a RegexDomain for backward compatibility.
 *
 * Example:
 * Expression: LOWER(SUBSTRING(name, 1, 3)) = 'abc'
 * Output constraint: 'abc' (literal)
 *
 * Traversal:
 * 1. LOWER: output='abc' => input='[aA][bB][cC]' (case-insensitive)
 * 2. SUBSTRING(x,1,3): output='[aA][bB][cC]' => input='^[aA][bB][cC].*$'
 * 3. Result: name must match ^[aA][bB][cC].*$
 * 
 * @deprecated Use {@link DomainInferenceProgram} for full cross-domain support
 */
public class RegexDomainInferenceProgram {
    private final DomainInferenceProgram genericProgram;

    public RegexDomainInferenceProgram(List<DomainTransformer> transformers) {
        this.genericProgram = new DomainInferenceProgram(transformers);
    }

    /**
     * Derives the regex constraint on the input variable given an expression
     * and a constraint on the output.
     *
     * @param expr the expression tree
     * @param outputRegex the regex constraint on the output
     * @return the refined regex constraint on the input variable
     */
    public RegexDomain deriveInputRegex(RexNode expr, RegexDomain outputRegex) {
        return genericProgram.deriveInputRegex(expr, outputRegex);
    }
}
