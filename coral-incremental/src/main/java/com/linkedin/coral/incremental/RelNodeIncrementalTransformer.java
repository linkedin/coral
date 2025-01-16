/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.calcite.plan.RelOptCluster;
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
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;


public class RelNodeIncrementalTransformer {

  private RelNodeIncrementalTransformer() {
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
        RelNode incrementalLeft = convertRelIncremental(left);
        RelNode incrementalRight = convertRelIncremental(right);

        RexBuilder rexBuilder = join.getCluster().getRexBuilder();

        LogicalProject p1 = createProjectOverJoin(join, left, incrementalRight, rexBuilder);
        LogicalProject p2 = createProjectOverJoin(join, incrementalLeft, right, rexBuilder);
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

  public static RelNode addWhereClause(RelNode originalNode, String tableName, String columnName, String columnValue) {
    RelShuttle converter = new RelShuttleImpl() {
      @Override
      public RelNode visit(TableScan scan) {
        // Extract the table name being scanned
        RelOptTable originalTable = scan.getTable();
        List<String> tableBeingScanned = originalTable.getQualifiedName();
        String currentTableName = tableBeingScanned.get(tableBeingScanned.size() - 2) + "."
            + tableBeingScanned.get(tableBeingScanned.size() - 1);

        // Check if this is the table we are interested in
        if (tableName.equals(currentTableName)) {
          // Use the cluster and RexBuilder to construct expressions
          RelOptCluster cluster = scan.getCluster();
          RexBuilder rexBuilder = cluster.getRexBuilder();

          // Get the column index in the schema
          int columnIndex = getColumnIndexInSchema(scan, columnName);
          if (columnIndex == -1) {
            throw new RuntimeException("Column " + columnName + " does not exist in table " + tableName);
          }

          // Build the reference to the column
          RexNode columnRef = rexBuilder.makeInputRef(scan.getRowType(), columnIndex);

          // Build the condition for the WHERE clause
          RexNode condition = rexBuilder.makeCall(SqlStdOperatorTable.EQUALS, // Equality operator
              columnRef, // LHS: Column reference
              rexBuilder.makeLiteral(columnValue) // RHS: Value to compare
          );

          // Create and return a LogicalFilter on top of the TableScan
          return LogicalFilter.create(scan, condition);
        }

        // If this is not the table we are interested in, return the original scan
        return super.visit(scan);
      }
    };
    return originalNode.accept(converter);
  }

  private static int getColumnIndexInSchema(RelNode relNode, String columnName) {
    List<RelDataTypeField> fields = relNode.getRowType().getFieldList();
    for (int i = 0; i < fields.size(); i++) {
      if (fields.get(i).getName().equalsIgnoreCase(columnName)) {
        return i; // Return the column index if found
      }
    }
    return -1; // Column not found
  }
}
