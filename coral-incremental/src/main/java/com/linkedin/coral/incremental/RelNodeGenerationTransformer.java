/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
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
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;


public class RelNodeGenerationTransformer {

  static RelNode convertRelPrev(RelNode originalNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        RelOptTable originalTable = scan.getTable();
        List<String> incrementalNames = new ArrayList<>(originalTable.getQualifiedName());
        String deltaTableName = incrementalNames.remove(incrementalNames.size() - 1) + "_prev";
        incrementalNames.add(deltaTableName);
        RelOptTable incrementalTable =
            RelOptTableImpl.create(originalTable.getRelOptSchema(), originalTable.getRowType(), incrementalNames, null);
        return LogicalTableScan.create(scan.getCluster(), incrementalTable);
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        RelNode left = join.getLeft();
        RelNode right = join.getRight();
        RelNode prevLeft = convertRelPrev(left);
        RelNode prevRight = convertRelPrev(right);
        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        LogicalProject p3 = createProjectOverJoin(join, prevLeft, prevRight, rexBuilder);

        return p3;
      }

      @Override
      public RelNode visit(LogicalFilter filter) {
        RelNode transformedChild = convertRelPrev(filter.getInput());

        return LogicalFilter.create(transformedChild, filter.getCondition());
      }

      @Override
      public RelNode visit(LogicalProject project) {
        RelNode transformedChild = convertRelPrev(project.getInput());
        return LogicalProject.create(transformedChild, project.getProjects(), project.getRowType());
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        List<RelNode> children = union.getInputs();
        List<RelNode> transformedChildren =
            children.stream().map(child -> convertRelPrev(child)).collect(Collectors.toList());
        return LogicalUnion.create(transformedChildren, union.all);
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        RelNode transformedChild = convertRelPrev(aggregate.getInput());
        return LogicalAggregate.create(transformedChild, aggregate.getGroupSet(), aggregate.getGroupSets(),
            aggregate.getAggCallList());
      }
    };
    return originalNode.accept(converter);
  }

  private RelNodeGenerationTransformer() {
  }

  public static RelNode convertRelIncremental(RelNode originalNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        RelOptTable originalTable = scan.getTable();
        List<String> incrementalNames = new ArrayList<>(originalTable.getQualifiedName());
        String deltaTableName = incrementalNames.remove(incrementalNames.size() - 1) + "_delta";
        incrementalNames.add(deltaTableName);
        RelOptTable incrementalTable =
            RelOptTableImpl.create(originalTable.getRelOptSchema(), originalTable.getRowType(), incrementalNames, null);
        return LogicalTableScan.create(scan.getCluster(), incrementalTable);
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        RelNode left = join.getLeft();
        RelNode right = join.getRight();
        RelNode prevLeft = convertRelPrev(left);
        RelNode prevRight = convertRelPrev(right);
        RelNode incrementalLeft = convertRelIncremental(left);
        RelNode incrementalRight = convertRelIncremental(right);

        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        LogicalProject p1 = createProjectOverJoin(join, prevLeft, incrementalRight, rexBuilder);
        LogicalProject p2 = createProjectOverJoin(join, incrementalLeft, prevRight, rexBuilder);
        LogicalProject p3 = createProjectOverJoin(join, incrementalLeft, incrementalRight, rexBuilder);

        LogicalUnion unionAllJoins =
            LogicalUnion.create(Arrays.asList(LogicalUnion.create(Arrays.asList(p1, p2), true), p3), true);
        return unionAllJoins;
      }

      @Override
      public RelNode visit(LogicalFilter filter) {
        RelNode transformedChild = convertRelIncremental(filter.getInput());
        return LogicalFilter.create(transformedChild, filter.getCondition());
      }

      @Override
      public RelNode visit(LogicalProject project) {
        RelNode transformedChild = convertRelIncremental(project.getInput());
        return LogicalProject.create(transformedChild, project.getProjects(), project.getRowType());
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        List<RelNode> children = union.getInputs();
        List<RelNode> transformedChildren =
            children.stream().map(child -> convertRelIncremental(child)).collect(Collectors.toList());
        return LogicalUnion.create(transformedChildren, union.all);
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        RelNode transformedChild = convertRelIncremental(aggregate.getInput());
        return LogicalAggregate.create(transformedChild, aggregate.getGroupSet(), aggregate.getGroupSets(),
            aggregate.getAggCallList());
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
}
