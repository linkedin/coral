/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.rel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexUtil;


/**
 * Utility to convert the predicates produced by {@link CanonicalPredicateExtractor}
 * into Disjunctive Normal Form (DNF) and return only the top-level disjuncts
 * (OR terms), while preserving the same sequential scan nodes for field index
 * orientation.
 *
 * <p>Usage pattern:</p>
 * <pre>
 *   CanonicalPredicateExtractor.Output extracted = CanonicalPredicateExtractor.extract(rel);
 *   CanonicalPredicateDnf.Output dnf = CanonicalPredicateDnf.convert(extracted, rexBuilder);
 * </pre>
 **/
public final class DnfRewriter {

  private DnfRewriter() {
  }

  public static final class Output {
    public final List<RelNode> sequentialScans;
    public final List<RexNode> disjuncts; // top-level OR terms of the DNF

    public Output(List<RelNode> sequentialScans, List<RexNode> disjuncts) {
      this.sequentialScans = sequentialScans;
      this.disjuncts = disjuncts;
    }
  }

  /**
   * Convert to DNF with options.
   *
   * @param input predicates and scans from CanonicalPredicateExtractor
   * @param rexBuilder RexBuilder to construct Rex expressions
   */
  public static Output convert(CanonicalPredicateExtractor.Output input, RexBuilder rexBuilder) {

    // Compose all canonical predicates into a single conjunction
    RexNode conj;
    if (input.canonicalPredicates.isEmpty()) {
      conj = rexBuilder.makeLiteral(true);
    } else {
      // Compose AND of all predicates. RexUtil handles flattening.
      conj = RexUtil.composeConjunction(rexBuilder, input.canonicalPredicates, false);
    }

    RexNode current = conj;

    // Convert to DNF
    RexNode dnf = RexUtil.toDnf(rexBuilder, current);

    // Extract disjuncts (top-level OR terms)
    List<RexNode> disjuncts = new ArrayList<>(RelOptUtil.disjunctions(dnf));

    return new Output(input.sequentialScans, Collections.unmodifiableList(disjuncts));
  }
}
