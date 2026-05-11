/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;

import com.linkedin.coral.datagen.domain.transformer.CastRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.LowerRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.PlusIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.SubstringRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.TimesIntegerTransformer;


/**
 * Generic domain inference program supporting multiple domain types.
 * 
 * This class takes an expression tree and a constraint on the output (as any Domain type),
 * and derives the constraint on the input variable by traversing the expression tree inward,
 * refining the domain at each step.
 * 
 * Supports:
 * - RegexDomain: String constraints represented as regular expressions
 * - IntegerDomain: Numeric constraints represented as intervals
 * - Cross-domain conversions via CAST operations
 * 
 * Example 1 (String operations):
 *   Expression: LOWER(SUBSTRING(name, 1, 3)) = 'abc'
 *   Output: RegexDomain("^abc$")
 *   Result: RegexDomain("^[aA][bB][cC].*$")
 * 
 * Example 2 (Cross-domain with CAST):
 *   Expression: CAST(age * 2 AS STRING) = '50'
 *   Output: RegexDomain("^50$")
 *   Conversion: RegexDomain → IntegerDomain([50])
 *   Through TIMES: IntegerDomain([25])
 *   Result: IntegerDomain([25])
 */
public class DomainInferenceProgram {
  private final List<DomainTransformer> transformers;

  public DomainInferenceProgram(List<DomainTransformer> transformers) {
    this.transformers = transformers;
  }

  /**
   * Creates a DomainInferenceProgram with all built-in transformers.
   * This is the recommended way to create an instance for production use.
   */
  public static DomainInferenceProgram withDefaultTransformers() {
    return new DomainInferenceProgram(Arrays.asList(new LowerRegexTransformer(), new SubstringRegexTransformer(),
        new PlusIntegerTransformer(), new TimesIntegerTransformer(), new CastRegexTransformer()));
  }

  /**
   * Derives the domain constraint on the input variable given an expression
   * and a constraint on the output.
   * 
   * @param expr the expression tree
   * @param outputDomain the domain constraint on the output (RegexDomain or IntegerDomain)
   * @return the refined domain constraint on the input variable
   */
  public Domain<?, ?> deriveInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    // Base case: if we've reached the input variable, return the output constraint
    if (expr instanceof RexInputRef) {
      return outputDomain;
    }

    // Find a transformer that can handle this expression
    for (DomainTransformer transformer : transformers) {
      if (transformer.canHandle(expr) && transformer.isVariableOperandPositionValid(expr)) {
        // Refine the domain for the child expression
        Domain<?, ?> childDomain = transformer.refineInputDomain(expr, outputDomain);

        // Check for empty domain (contradiction)
        if (childDomain.isEmpty()) {
          return createEmptyDomain(outputDomain);
        }

        // Recursively derive the domain for the child
        RexNode child = transformer.getChildForVariable(expr);
        return deriveInputDomain(child, childDomain);
      }
    }

    // No applicable transformer found
    throw new IllegalStateException("No applicable transformer for expression: " + expr);
  }

  /**
   * Creates an empty domain matching the type of the given domain.
   */
  private Domain<?, ?> createEmptyDomain(Domain<?, ?> referenceDomain) {
    if (referenceDomain instanceof RegexDomain) {
      return RegexDomain.empty();
    } else if (referenceDomain instanceof IntegerDomain) {
      return IntegerDomain.empty();
    } else {
      throw new IllegalArgumentException("Unknown domain type: " + referenceDomain.getClass().getSimpleName());
    }
  }
}
