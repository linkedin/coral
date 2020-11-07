/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamFilter;
import com.linkedin.beam.planner.BeamConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalFilter;


public class BeamFilterRule extends ConverterRule {
  public BeamFilterRule() {
    super(LogicalFilter.class, RelOptUtil.FILTER_PREDICATE, Convention.NONE,
        BeamConvention.INSTANCE, "BeamFilterRule");
  }
  @Override
  public RelNode convert(RelNode rel) {
    final LogicalFilter filter = (LogicalFilter) rel;
    return BeamFilter.create(
        convert(filter.getInput(),
            filter.getInput().getTraitSet()
                .replace(BeamConvention.INSTANCE)),
        filter.getCondition());
  }
}
