/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.linkedin.beam.operators.BeamNode;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.RelTraitSet;


public enum BeamConvention implements Convention {
  INSTANCE;

  @Override
  public String toString() {
    return getName();
  }

  public Class getInterface() {
    return BeamNode.class;
  }

  public String getName() {
    return "BEAM";
  }

  public RelTraitDef getTraitDef() {
    return ConventionTraitDef.INSTANCE;
  }

  public boolean satisfies(RelTrait trait) {
    return this == trait;
  }

  public void register(RelOptPlanner planner) {
  }

  public boolean canConvertConvention(Convention toConvention) {
    return false;
  }

  public boolean useAbstractConvertersForConversion(RelTraitSet fromTraits, RelTraitSet toTraits) {
    return false;
  }
}
