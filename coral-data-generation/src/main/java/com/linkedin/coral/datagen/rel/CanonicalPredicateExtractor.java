/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.rel;

import java.util.*;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rex.*;


/**
 * Extracts a canonical set of predicates and sequential scans from a relational tree.
 *
 * <p>Precondition: all {@link Project} nodes have been pulled up to the top of the tree
 * before calling {@link #extract(RelNode)}. Use
 * {@link com.linkedin.coral.datagen.rel.ProjectPullUpController#applyUntilFixedPoint(RelNode)}
 * as a preprocessing step to normalize the tree. This extractor treats Project nodes as
 * transparent and traverses through them when collecting scans and predicates.</p>
 */
public final class CanonicalPredicateExtractor {

  public static final class Output {
    public final List<RelNode> sequentialScans;
    public final List<RexNode> canonicalPredicates;
    public Output(List<RelNode> sequentialScans, List<RexNode> canonicalPredicates) {
      this.sequentialScans = sequentialScans;
      this.canonicalPredicates = canonicalPredicates;
    }
  }

  private CanonicalPredicateExtractor() {
  }

  public static Output extract(RelNode root) {
    // Reset state for this extraction
    predicateOriginMap.clear();

    List<RelNode> scans = new ArrayList<>();
    collectSequentialScans(root, scans);

    Map<RelNode, Integer> scanIndex = new IdentityHashMap<>();
    for (int i = 0; i < scans.size(); i++) {
      scanIndex.put(scans.get(i), i);
    }

    int[] scanFieldOffsets = new int[scans.size()];
    int off = 0;
    for (int i = 0; i < scans.size(); i++) {
      scanFieldOffsets[i] = off;
      off += scans.get(i).getRowType().getFieldCount();
    }

    Map<RelNode, Integer> nodeStartScanIndex = new IdentityHashMap<>();
    computeStartScan(root, scanIndex, nodeStartScanIndex);

    List<RexNode> preds = new ArrayList<>();
    collectPredicates(root, preds);

    List<RexNode> remapped = new ArrayList<>();
    for (RexNode p : preds) {
      RelNode origin = predicateOriginMap.get(p); // origin stored during collection
      int startScan = nodeStartScanIndex.get(origin);
      int base = scanFieldOffsets[startScan];
      remapped.add(remap(p, base));
    }

    return new Output(scans, remapped);
  }

  private static final Map<RexNode, RelNode> predicateOriginMap = new IdentityHashMap<>();

  private static void collectPredicates(RelNode node, List<RexNode> out) {
    if (node instanceof Filter) {
      Filter f = (Filter) node;
      predicateOriginMap.put(f.getCondition(), f);
      out.add(f.getCondition());
      collectPredicates(f.getInput(), out);
    } else if (node instanceof Join) {
      Join j = (Join) node;
      predicateOriginMap.put(j.getCondition(), j);
      out.add(j.getCondition());
      collectPredicates(j.getLeft(), out);
      collectPredicates(j.getRight(), out);
    } else if (node instanceof Project) {
      Project p = (Project) node;
      collectPredicates(p.getInput(), out);
    }
  }

  private static RexNode remap(RexNode expr, int baseOffset) {
    return expr.accept(new RexShuttle() {
      @Override
      public RexNode visitInputRef(RexInputRef ref) {
        int newIdx = baseOffset + ref.getIndex();
        return new RexInputRef(newIdx, ref.getType());
      }
    });
  }

  private static void collectSequentialScans(RelNode node, List<RelNode> out) {
    if (node instanceof Filter) {
      collectSequentialScans(((Filter) node).getInput(), out);
    } else if (node instanceof Join) {
      Join j = (Join) node;
      collectSequentialScans(j.getLeft(), out);
      collectSequentialScans(j.getRight(), out);
    } else if (node instanceof Project) {
      collectSequentialScans(((Project) node).getInput(), out);
    } else {
      out.add(node);
    }
  }

  private static int computeStartScan(RelNode node, Map<RelNode, Integer> scanIndex, Map<RelNode, Integer> nodeStart) {

    if (node instanceof Filter) {
      Filter f = (Filter) node;
      int childStart = computeStartScan(f.getInput(), scanIndex, nodeStart);
      nodeStart.put(node, childStart);
      return childStart;

    } else if (node instanceof Join) {
      Join j = (Join) node;
      int leftStart = computeStartScan(j.getLeft(), scanIndex, nodeStart);
      int rightStart = computeStartScan(j.getRight(), scanIndex, nodeStart);
      int start = Math.min(leftStart, rightStart);
      nodeStart.put(node, start);
      return start;

    } else if (node instanceof Project) {
      Project p = (Project) node;
      int childStart = computeStartScan(p.getInput(), scanIndex, nodeStart);
      nodeStart.put(node, childStart);
      return childStart;

    } else {
      int idx = scanIndex.get(node);
      nodeStart.put(node, idx);
      return idx;
    }
  }
}
