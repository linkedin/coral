/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.RelOptTableImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;


public class RelIncrementalTransformer {

  private RelIncrementalTransformer() {
  }

  public static RelNode convertRelIncremental(RelNode originalNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        RelOptTable originalTable = scan.getTable();
        List<String> modifiedNames = new ArrayList<>(originalTable.getQualifiedName());
        String deltaTableName = modifiedNames.remove(modifiedNames.size() - 1) + "_delta";
        modifiedNames.add(deltaTableName);
        RelOptTable modifiedTable =
            RelOptTableImpl.create(originalTable.getRelOptSchema(), originalTable.getRowType(), modifiedNames, null);
        return LogicalTableScan.create(scan.getCluster(), modifiedTable);
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        RelNode left = join.getLeft();
        RelNode right = join.getRight();
        RelNode incrementalLeft = transformToIncremental(left);
        RelNode incrementalRight = transformToIncremental(right);

        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        LogicalProject p1 = createProjectOverJoin(join, left, incrementalRight, rexBuilder);
        LogicalProject p2 = createProjectOverJoin(join, incrementalLeft, right, rexBuilder);
        LogicalProject p3 = createProjectOverJoin(join, incrementalLeft, incrementalRight, rexBuilder);

        LogicalUnion unionAllJoins =
            LogicalUnion.create(Arrays.asList(LogicalUnion.create(Arrays.asList(p1, p2), true), p3), true);
        return unionAllJoins;
      }
    };
    return originalNode.accept(converter);
  }

  private static LogicalProject createProjectOverJoin(LogicalJoin join, RelNode left, RelNode right,
      RexBuilder rexBuilder) {
    LogicalJoin incrementalJoin =
        LogicalJoin.create(left, right, join.getCondition(), join.getVariablesSet(), join.getJoinType());
    ArrayList<RexNode> projects = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    IntStream.range(0, incrementalJoin.getRowType().getFieldList().size()).forEach(i -> {
      projects.add(rexBuilder.makeInputRef(incrementalJoin, i));
      names.add(incrementalJoin.getRowType().getFieldNames().get(i));
    });
    return LogicalProject.create(incrementalJoin, projects, names);
  }

  private static RelNode transformToIncremental(RelNode target) {
    // Recurse until we hit a TableScan to transform
    if (target instanceof TableScan) {
      return convertRelIncremental(target);
    } else {
      List<RelNode> children = target.getInputs();
      List<RelNode> transformedChildren =
          children.stream().map(child -> convertRelIncremental(child)).collect(Collectors.toList());

      // Construct target with transformed children nodes
      // TODO: Add other types
      if (target instanceof LogicalFilter) {
        return LogicalFilter.create(transformedChildren.get(0), ((LogicalFilter) target).getCondition());
      } else if (target instanceof LogicalProject) {
        return LogicalProject.create(transformedChildren.get(0), ((LogicalProject) target).getProjects(),
            target.getRowType());
      } else if (target instanceof LogicalJoin) {
        return LogicalJoin.create(transformedChildren.get(0), transformedChildren.get(1),
            ((LogicalJoin) target).getCondition(), target.getVariablesSet(), ((LogicalJoin) target).getJoinType());
      } else if (target instanceof LogicalUnion) {
        return LogicalUnion.create(transformedChildren, ((LogicalUnion) target).all);
      }
    }
    return target;
  }

}
