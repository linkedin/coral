/**
 * Copyright 2018-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelInput;
import org.apache.calcite.rel.RelNode;


/**
 * @deprecated Use {@link CoralUncollect} instead. This class has been renamed to better reflect
 * that it is used across all Coral translation targets, not just Hive.
 * This class will be removed in a future release.
 */
@Deprecated
public class HiveUncollect extends CoralUncollect {

  public HiveUncollect(RelOptCluster cluster, RelTraitSet traitSet, RelNode input, boolean withOrdinality) {
    super(cluster, traitSet, input, withOrdinality);
  }

  public HiveUncollect(RelInput input) {
    super(input);
  }
}
