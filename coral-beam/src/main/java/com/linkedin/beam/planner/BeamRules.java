/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.linkedin.beam.planner.rules.BeamAggregateRule;
import com.linkedin.beam.planner.rules.BeamArrayFlattenRule;
import com.linkedin.beam.planner.rules.BeamFilterRule;
import com.linkedin.beam.planner.rules.BeamJoinRule;
import com.linkedin.beam.planner.rules.BeamProjectRule;
import com.linkedin.beam.planner.rules.BeamTableScanRule;
import com.linkedin.beam.planner.rules.BeamUnionRule;
import org.apache.calcite.plan.RelOptRule;


public class BeamRules {
  private BeamRules() {
  }

  public static final RelOptRule BEAM_TABLE_SCAN_RULE = new BeamTableScanRule();
  public static final RelOptRule BEAM_FILTER_RULE = new BeamFilterRule();
  public static final RelOptRule BEAM_PROJECT_RULE = new BeamProjectRule();
  public static final RelOptRule BEAM_JOIN_RULE = new BeamJoinRule();
  public static final RelOptRule BEAM_UNION_RULE = new BeamUnionRule();
  public static final RelOptRule BEAM_AGGREGATE_RULE = new BeamAggregateRule();
  public static final RelOptRule BEAM_ARRAY_FLATTEN_RULE = new BeamArrayFlattenRule();
}
