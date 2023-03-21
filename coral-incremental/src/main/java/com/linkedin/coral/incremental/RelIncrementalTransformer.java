/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.RelOptTableImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttle;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalTableScan;


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
    };
    return originalNode.accept(converter);
  }

}
