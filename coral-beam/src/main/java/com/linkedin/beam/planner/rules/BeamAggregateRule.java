/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamAggregate;
import com.linkedin.beam.planner.CalciteBeamConfig;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalAggregate;


public class BeamAggregateRule extends ConverterRule {
  public BeamAggregateRule() {
    super(LogicalAggregate.class, Convention.NONE, BeamConvention.INSTANCE, "BeamAggregateRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalAggregate aggregate = (LogicalAggregate) rel;
    final CalciteBeamConfig calciteBeamConfig = ((BeamPlanner) rel.getCluster().getPlanner())._calciteBeamConfig;
    return BeamAggregate.create(
        convert(aggregate.getInput(), aggregate.getInput().getTraitSet().replace(BeamConvention.INSTANCE)),
        aggregate.getGroupSet(),
        aggregate.getGroupSets(),
        aggregate.getAggCallList(),
        calciteBeamConfig.aggregateWindowMinutes);
  }
}
