/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.RelOptTableImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.logical.LogicalUnion;


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

        // TODO: Figure out what to pass into projects when creating LogicalProject
        LogicalJoin j1 =
            LogicalJoin.create(left, incrementalRight, join.getCondition(), join.getVariablesSet(), join.getJoinType());
        //        LogicalProject p1 = LogicalProject.create(j1, null, join.getRowType());

        LogicalJoin j2 =
            LogicalJoin.create(incrementalLeft, right, join.getCondition(), join.getVariablesSet(), join.getJoinType());
        //        LogicalProject p2 = LogicalProject.create(j2, null, join.getRowType());

        LogicalJoin j3 = LogicalJoin.create(incrementalLeft, incrementalRight, join.getCondition(),
            join.getVariablesSet(), join.getJoinType());
        //        LogicalProject p3 = LogicalProject.create(j3, null, join.getRowType());

        LogicalUnion unionAllJoins =
            LogicalUnion.create(Arrays.asList(LogicalUnion.create(Arrays.asList(j1, j2), true), j3), true);
        return unionAllJoins;
      }
    };
    return originalNode.accept(converter);
  }

  private static RelNode transformToIncremental(RelNode target) {
    // Loop until we hit a TableScan to transform
    RelNode tempTarget = target;
    while (tempTarget.getTable() == null) {
      // TODO: Check if we hit a join first, if so, stop and return since we have handled everything below already
      tempTarget = tempTarget.getInput(0); // TODO: Check logic (right now only fetching left-most child)
    }
    // Transform the base table once we hit the TableScan layer
    RelOptTable originalTable = target.getTable();
    List<String> modifiedNames = new ArrayList<>(originalTable.getQualifiedName());
    String deltaTableName = modifiedNames.remove(modifiedNames.size() - 1) + "_delta";
    modifiedNames.add(deltaTableName);
    RelOptTable modifiedTable =
        RelOptTableImpl.create(originalTable.getRelOptSchema(), originalTable.getRowType(), modifiedNames, null);
    RelNode modifiedTableScan = LogicalTableScan.create(target.getCluster(), modifiedTable);
    // TODO: Rebuild the target node with new modified TableScan -> possible to reset this scan instead or use RelShuttle?
    // TODO: Change return
    return modifiedTableScan;
  }

}
