/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamTableScan;
import com.linkedin.beam.planner.BeamConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalTableScan;


public class BeamTableScanRule extends ConverterRule {
  public BeamTableScanRule() {
    super(LogicalTableScan.class, Convention.NONE,
        BeamConvention.INSTANCE, "BeamTableScanRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalTableScan scan = (LogicalTableScan) rel;
    return BeamTableScan.create(scan.getCluster(), scan.getTable());
  }
}
