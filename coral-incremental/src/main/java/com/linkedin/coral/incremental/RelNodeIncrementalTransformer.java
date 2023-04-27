/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.calcite.plan.RelOptSchema;
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


public class RelNodeIncrementalTransformer {

  private static RelOptSchema relOptSchema;

  private RelNodeIncrementalTransformer() {
  }

  public static IncrementalTransformerResults performIncrementalTransformation(RelNode originalNode) {
    IncrementalTransformerResults incrementalTransformerResults = convertRelIncremental(originalNode);
    return incrementalTransformerResults;
  }

  private static IncrementalTransformerResults convertRelIncremental(RelNode originalNode) {
    IncrementalTransformerResults incrementalTransformerResults = new IncrementalTransformerResults();
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        RelOptTable originalTable = scan.getTable();

        // Set relOptSchema
        if (relOptSchema == null) {
          relOptSchema = originalTable.getRelOptSchema();
        }

        // Create delta scan
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
        IncrementalTransformerResults incrementalTransformerResultsLeft = convertRelIncremental(left);
        IncrementalTransformerResults incrementalTransformerResultsRight = convertRelIncremental(right);
        RelNode incrementalLeft = incrementalTransformerResultsLeft.getIncrementalRelNode();
        RelNode incrementalRight = incrementalTransformerResultsRight.getIncrementalRelNode();
        incrementalTransformerResults
            .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsLeft.getIntermediateQueryRelNodes());
        incrementalTransformerResults
            .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsRight.getIntermediateQueryRelNodes());

        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        // Check if we can replace the left and right nodes with a scan of a materialized table
        if (incrementalTransformerResults.containsIntermediateQueryRelNodeKey(getTableNameFromDescription(left))) {
          String description = getTableNameFromDescription(left);
          String deterministicDescription =
              "Table#" + incrementalTransformerResults.getIndexOfIntermediateOrdering(description);
          LogicalProject leftLastProject =
              createReplacementProjectNodeForGivenRelNode(deterministicDescription, left, rexBuilder);
          left = leftLastProject;
          LogicalProject leftDeltaProject = createReplacementProjectNodeForGivenRelNode(
              deterministicDescription + "_delta", incrementalLeft, rexBuilder);
          incrementalLeft = leftDeltaProject;
        }
        if (incrementalTransformerResults.containsIntermediateQueryRelNodeKey(getTableNameFromDescription(right))) {
          String description = getTableNameFromDescription(right);
          String deterministicDescription =
              "Table#" + incrementalTransformerResults.getIndexOfIntermediateOrdering(description);
          LogicalProject rightLastProject =
              createReplacementProjectNodeForGivenRelNode(deterministicDescription, right, rexBuilder);
          right = rightLastProject;
          LogicalProject rightDeltaProject = createReplacementProjectNodeForGivenRelNode(
              deterministicDescription + "_delta", incrementalRight, rexBuilder);
          incrementalRight = rightDeltaProject;
        }

        LogicalProject p1 = createProjectOverJoin(join, left, incrementalRight, rexBuilder);
        LogicalProject p2 = createProjectOverJoin(join, incrementalLeft, right, rexBuilder);
        LogicalProject p3 = createProjectOverJoin(join, incrementalLeft, incrementalRight, rexBuilder);

        LogicalUnion unionAllJoins =
            LogicalUnion.create(Arrays.asList(LogicalUnion.create(Arrays.asList(p1, p2), true), p3), true);

        return unionAllJoins;
      }

      @Override
      public RelNode visit(LogicalFilter filter) {
        IncrementalTransformerResults incrementalTransformerResultsChild = convertRelIncremental(filter.getInput());
        RelNode transformedChild = incrementalTransformerResultsChild.getIncrementalRelNode();
        incrementalTransformerResults
            .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsChild.getIntermediateQueryRelNodes());
        return LogicalFilter.create(transformedChild, filter.getCondition());
      }

      @Override
      public RelNode visit(LogicalProject project) {
        IncrementalTransformerResults incrementalTransformerResultsChild = convertRelIncremental(project.getInput());
        RelNode transformedChild = incrementalTransformerResultsChild.getIncrementalRelNode();
        incrementalTransformerResults
            .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsChild.getIntermediateQueryRelNodes());
        incrementalTransformerResults.addIntermediateQueryRelNode(getTableNameFromDescription(project), project);
        LogicalProject transformedProject =
            LogicalProject.create(transformedChild, project.getProjects(), project.getRowType());
        incrementalTransformerResults.addIntermediateQueryRelNode(getTableNameFromDescription(project) + "_delta",
            transformedProject);
        return transformedProject;
      }

      @Override
      public RelNode visit(LogicalUnion union) {
        List<RelNode> children = union.getInputs();
        List<IncrementalTransformerResults> incrementalTransformerResultsChildren =
            children.stream().map(child -> convertRelIncremental(child)).collect(Collectors.toList());
        List<RelNode> transformedChildren = new ArrayList<>();
        for (IncrementalTransformerResults incrementalTransformerResultsChild : incrementalTransformerResultsChildren) {
          transformedChildren.add(incrementalTransformerResultsChild.getIncrementalRelNode());
          incrementalTransformerResults
              .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsChild.getIntermediateQueryRelNodes());
        }
        return LogicalUnion.create(transformedChildren, union.all);
      }

      @Override
      public RelNode visit(LogicalAggregate aggregate) {
        IncrementalTransformerResults incrementalTransformerResultsChild = convertRelIncremental(aggregate.getInput());
        RelNode transformedChild = incrementalTransformerResultsChild.getIncrementalRelNode();
        incrementalTransformerResults
            .addMultipleIntermediateQueryRelNodes(incrementalTransformerResultsChild.getIntermediateQueryRelNodes());
        return LogicalAggregate.create(transformedChild, aggregate.getGroupSet(), aggregate.getGroupSets(),
            aggregate.getAggCallList());
      }
    };
    incrementalTransformerResults.setIncrementalRelNode(originalNode.accept(converter));
    return incrementalTransformerResults;
  }

  private static String getTableNameFromDescription(RelNode relNode) {
    String identifier = relNode.getDescription().split("#")[1];
    return "Table#" + identifier;
  }

  private static LogicalProject createReplacementProjectNodeForGivenRelNode(String relOptTableName, RelNode relNode,
      RexBuilder rexBuilder) {
    RelOptTable table =
        RelOptTableImpl.create(relOptSchema, relNode.getRowType(), Collections.singletonList(relOptTableName), null);
    TableScan scan = LogicalTableScan.create(relNode.getCluster(), table);
    return createProjectOverNode(scan, rexBuilder);
  }

  private static LogicalProject createProjectOverNode(RelNode relNode, RexBuilder rexBuilder) {
    ArrayList<RexNode> projects = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    IntStream.range(0, relNode.getRowType().getFieldList().size()).forEach(i -> {
      projects.add(rexBuilder.makeInputRef(relNode, i));
      names.add(relNode.getRowType().getFieldNames().get(i));
    });
    return LogicalProject.create(relNode, projects, names);
  }

  private static LogicalProject createProjectOverJoin(LogicalJoin join, RelNode left, RelNode right,
      RexBuilder rexBuilder) {
    LogicalJoin incrementalJoin =
        LogicalJoin.create(left, right, join.getCondition(), join.getVariablesSet(), join.getJoinType());
    return createProjectOverNode(incrementalJoin, rexBuilder);
  }

}
