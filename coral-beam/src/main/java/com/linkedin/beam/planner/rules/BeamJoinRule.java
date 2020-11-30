/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamJoin;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import com.linkedin.beam.planner.CalciteBeamConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.JoinInfo;
import org.apache.calcite.rel.logical.LogicalJoin;


public class BeamJoinRule extends ConverterRule {
  public BeamJoinRule() {
    super(LogicalJoin.class, Convention.NONE, BeamConvention.INSTANCE, "BeamJoinRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalJoin join = (LogicalJoin) rel;
    final List<RelNode> newInputs = new ArrayList<>();
    for (RelNode input : join.getInputs()) {
      newInputs.add(convert(input, input.getTraitSet().replace(BeamConvention.INSTANCE)));
    }
    RelNode left = newInputs.get(0);
    RelNode right = newInputs.get(1);
    final JoinInfo info = JoinInfo.of(left, right, join.getCondition());
    if (!info.isEqui()) {
      // Only accept equi-join
      throw new UnsupportedOperationException("None equi-join not supported");
    }

    final CalciteBeamConfig calciteBeamConfig = ((BeamPlanner) rel.getCluster().getPlanner())._calciteBeamConfig;
    return BeamJoin.create(left, calciteBeamConfig.timestampField, right, calciteBeamConfig.timestampField,
        join.getCondition(), join.getVariablesSet(), join.getJoinType(), calciteBeamConfig.joinWindowMinutes);
  }
}
