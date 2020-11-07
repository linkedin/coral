/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner.rules;

import com.linkedin.beam.operators.BeamUnion;
import com.linkedin.beam.planner.BeamConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalUnion;


public class BeamUnionRule extends ConverterRule {
  public BeamUnionRule() {
    super(LogicalUnion.class, Convention.NONE, BeamConvention.INSTANCE,
        "BeamUnionRule");
  }

  @Override
  public RelNode convert(RelNode rel) {
    final LogicalUnion union = (LogicalUnion) rel;
    return BeamUnion.create(convertList(union.getInputs(), BeamConvention.INSTANCE), union.all);
  }
}
