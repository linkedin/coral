/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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


public class RelNodeGenerationTransformer {
  private final String TABLE_NAME_PREFIX = "Table#";
  private final String DELTA_SUFFIX = "_delta";

  private RelOptSchema relOptSchema;
  private Map<String, RelNode> snapshotRelNodes;
  private Map<String, RelNode> deltaRelNodes;
  private RelNode tempLastRelNode;

  public RelNodeGenerationTransformer() {
    relOptSchema = null;
    snapshotRelNodes = new LinkedHashMap<>();
    deltaRelNodes = new LinkedHashMap<>();
    tempLastRelNode = null;
  }

  /**
   * Returns snapshotRelNodes with deterministic keys.
   */
  public Map<String, RelNode> getSnapshotRelNodes() {
    Map<String, RelNode> deterministicSnapshotRelNodes = new LinkedHashMap<>();
    for (String description : snapshotRelNodes.keySet()) {
      deterministicSnapshotRelNodes.put(getDeterministicDescriptionFromDescription(description, false),
          snapshotRelNodes.get(description));
    }
    return deterministicSnapshotRelNodes;
  }

  /**
   * Returns deltaRelNodes with deterministic keys.
   */
  public Map<String, RelNode> getDeltaRelNodes() {
    Map<String, RelNode> deterministicDeltaRelNodes = new LinkedHashMap<>();
    for (String description : deltaRelNodes.keySet()) {
      deterministicDeltaRelNodes.put(getDeterministicDescriptionFromDescription(description, true),
          deltaRelNodes.get(description));
    }
    return deterministicDeltaRelNodes;
  }

  private RelNode convertRelPrev(RelNode originalNode) {
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

  /**
   * Convert an input RelNode to an incremental RelNode. Populates snapshotRelNodes and deltaRelNodes.
   * @param originalNode input RelNode to generate an incremental version for.
   */
  public RelNode convertRelIncremental(RelNode originalNode) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        RelOptTable originalTable = scan.getTable();

        // Set RelNodeIncrementalTransformer class relOptSchema if not already set
        if (relOptSchema == null) {
          relOptSchema = originalTable.getRelOptSchema();
        }

        // Create delta scan
        List<String> incrementalNames = new ArrayList<>(originalTable.getQualifiedName());
        String deltaTableName = incrementalNames.remove(incrementalNames.size() - 1) + DELTA_SUFFIX;
        incrementalNames.add(deltaTableName);
        RelOptTable incrementalTable =
            RelOptTableImpl.create(originalTable.getRelOptSchema(), originalTable.getRowType(), incrementalNames, null);
        return LogicalTableScan.create(scan.getCluster(), incrementalTable);
      }

      @Override
      public RelNode visit(LogicalJoin join) {
        RelNode left = join.getLeft();
        RelNode right = join.getRight();
        RelNode incrementalLeft = convertRelIncremental(left);
        RelNode incrementalRight = convertRelIncremental(right);

        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        // Check if we can replace the left and right nodes with a scan of a materialized table
        String leftDescription = getDescriptionFromRelNode(left, false);
        String leftIncrementalDescription = getDescriptionFromRelNode(left, true);
        if (snapshotRelNodes.containsKey(leftDescription)) {
          left =
              susbstituteWithMaterializedView(getDeterministicDescriptionFromDescription(leftDescription, false), left);
          incrementalLeft = susbstituteWithMaterializedView(
              getDeterministicDescriptionFromDescription(leftIncrementalDescription, true), incrementalLeft);
        }
        String rightDescription = getDescriptionFromRelNode(right, false);
        String rightIncrementalDescription = getDescriptionFromRelNode(right, true);
        if (snapshotRelNodes.containsKey(rightDescription)) {
          right = susbstituteWithMaterializedView(getDeterministicDescriptionFromDescription(rightDescription, false),
              right);
          incrementalRight = susbstituteWithMaterializedView(
              getDeterministicDescriptionFromDescription(rightIncrementalDescription, true), incrementalRight);
        }
        RelNode prevLeft = convertRelPrev(left);
        RelNode prevRight = convertRelPrev(right);

        // We need to do this in the join to get potentially updated left and right nodes
        tempLastRelNode = createProjectOverJoin(join, left, right, rexBuilder);

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
        RelNode materializedProject = getTempLastRelNode();
        if (materializedProject != null) {
          snapshotRelNodes.put(getDescriptionFromRelNode(project, false), materializedProject);
        } else {
          snapshotRelNodes.put(getDescriptionFromRelNode(project, false), project);
        }
        LogicalProject transformedProject =
            LogicalProject.create(transformedChild, project.getProjects(), project.getRowType());
        deltaRelNodes.put(getDescriptionFromRelNode(project, true), transformedProject);
        return transformedProject;
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

  /**
   * Returns the tempLastRelNode and sets the variable back to null. Should only be called once for each retrieval
   * instance since subsequent consecutive calls will yield null.
   */
  private RelNode getTempLastRelNode() {
    RelNode currentTempLastRelNode = tempLastRelNode;
    tempLastRelNode = null;
    return currentTempLastRelNode;
  }

  /**
   * Returns the corresponding description for a given RelNode by extracting the identifier (ex. the identifier for
   * LogicalProject#22 is 22) and prepending the TABLE_NAME_PREFIX. Depending on the delta value, a delta suffix may be
   * appended.
   * @param relNode RelNode from which the identifier will be retrieved.
   * @param delta configure whether to get the delta name
   */
  private String getDescriptionFromRelNode(RelNode relNode, boolean delta) {
    String identifier = relNode.getDescription().split("#")[1];
    String description = TABLE_NAME_PREFIX + identifier;
    if (delta) {
      return description + DELTA_SUFFIX;
    }
    return description;
  }

  /**
   * Returns a description based on mapping index order that will stay the same across different runs of the same
   * query. The description consists of the table prefix, the index, and optionally, the delta suffix.
   * @param description output from calling getDescriptionFromRelNode()
   * @param delta configure whether to get the delta name
   */
  private String getDeterministicDescriptionFromDescription(String description, boolean delta) {
    if (delta) {
      List<String> deltaKeyOrdering = new ArrayList<>(deltaRelNodes.keySet());
      return TABLE_NAME_PREFIX + deltaKeyOrdering.indexOf(description) + DELTA_SUFFIX;
    } else {
      List<String> snapshotKeyOrdering = new ArrayList<>(snapshotRelNodes.keySet());
      return TABLE_NAME_PREFIX + snapshotKeyOrdering.indexOf(description);
    }
  }

  /**
   * Accepts a table name and RelNode and creates a TableScan over the RelNode using the class relOptSchema.
   * @param relOptTableName table name corresponding to table to scan over
   * @param relNode top-level RelNode that will be replaced with the TableScan
   */
  private TableScan susbstituteWithMaterializedView(String relOptTableName, RelNode relNode) {
    RelOptTable table =
        RelOptTableImpl.create(relOptSchema, relNode.getRowType(), Collections.singletonList(relOptTableName), null);
    return LogicalTableScan.create(relNode.getCluster(), table);
  }

  /** Creates a LogicalProject whose input is an incremental LogicalJoin node that is constructed from a left and right
   * RelNode and LogicalJoin.
   * @param join LogicalJoin to create the incremental join from
   * @param left left RelNode child of the incremental join
   * @param right right RelNode child of the incremental join
   * @param rexBuilder RexBuilder for LogicalProject creation
   */
  private LogicalProject createProjectOverJoin(LogicalJoin join, RelNode left, RelNode right, RexBuilder rexBuilder) {
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
