/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.operators.BeamNode;
import com.linkedin.beam.utils.RexToBeamConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.calcite.piglet.PigToSqlAggregateRule;
import org.apache.calcite.piglet.PigUserDefinedFunction;
import org.apache.calcite.plan.Context;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptCostFactory;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.BiRel;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.SingleRel;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.core.Correlate;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.core.Uncollect;
import org.apache.calcite.rel.core.Union;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.logical.LogicalAggregate;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.rules.FilterMergeRule;
import org.apache.calcite.rel.rules.FilterProjectTransposeRule;
import org.apache.calcite.rel.rules.ProjectMergeRule;
import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RuleSets;


public class BeamPlanner extends VolcanoPlanner {
  static final List<RelOptRule> IMPLEMENTATION_RULES =
      ImmutableList.of(
          PigToSqlAggregateRule.INSTANCE,
          BeamRules.BEAM_TABLE_SCAN_RULE,
          BeamRules.BEAM_FILTER_RULE,
          BeamRules.BEAM_PROJECT_RULE,
          BeamRules.BEAM_JOIN_RULE,
          BeamRules.BEAM_UNION_RULE,
          BeamRules.BEAM_AGGREGATE_RULE,
          BeamRules.BEAM_ARRAY_FLATTEN_RULE);

  // Add a new transformation rule here only if you know what the rule does.
  static final List<RelOptRule> TRANSFORMATION_RULES =
      ImmutableList.of(
          FilterMergeRule.INSTANCE,
          ProjectMergeRule.INSTANCE,
          FilterProjectTransposeRule.INSTANCE);

  private int beamNodeId = 0;
  private final Map<Integer, Integer> nodeIdMap = new HashMap<>();
  public final CalciteBeamConfig _calciteBeamConfig;
  private final RexToBeamConverter rexTranslator;

  public BeamPlanner(RelOptCostFactory costFactory, Context externalContext,
      CalciteBeamConfig calciteBeamConfig, Map<String, PigUserDefinedFunction> pigUDFs, boolean planOptimized) {
    super(costFactory, externalContext);
    this._calciteBeamConfig = calciteBeamConfig;
    rexTranslator = new RexToBeamConverter(pigUDFs);
    for (RelOptRule rule : IMPLEMENTATION_RULES) {
      addRule(rule);
    }
    if (planOptimized) {
      for (RelOptRule rule : TRANSFORMATION_RULES) {
        addRule(rule);
      }
    }
    addRelTraitDef(ConventionTraitDef.INSTANCE);
    addRelTraitDef(RelCollationTraitDef.INSTANCE);
  }

  private int getBeamNodeId(int relId) {
    Integer nodeId = nodeIdMap.get(relId);
    if (nodeId == null) {
      nodeId = ++beamNodeId;
      nodeIdMap.putIfAbsent(relId, nodeId);
    }
    return nodeId;
  }

  /**
   * Returns a {@link RexToBeamConverter}. This method will also reset the local variable counter.
   * So do not call it multiple times for the same scope of generated code.
   *
   * @return a {@link RexToBeamConverter}.
   */
  public RexToBeamConverter getRexTranslator() {
    rexTranslator.resetVarCounter();
    return rexTranslator;
  }

  /**
   * Creates a {@link BeamPlanner} from a @{@link RelOptPlanner} template.
   *
   * @param template RelOptPlanner template
   * @param calciteBeamConfig Beam app config
   * @param pigUDFs Pig UDF map
   * @param planOptimization Whether to optimize the Calcite plan
   * @return A {@link BeamPlanner}
   */
  public static BeamPlanner createPlanner(RelOptPlanner template, CalciteBeamConfig calciteBeamConfig,
      Map<String, PigUserDefinedFunction> pigUDFs, boolean planOptimization) {
    return new BeamPlanner(template.getCostFactory(), template.getContext(), calciteBeamConfig, pigUDFs,
        planOptimization);
  }

  @Override
  public RelOptCost getCost(RelNode rel, RelMetadataQuery mq) {
    RelOptCost cost = super.getCost(rel, mq);
    if (rel instanceof Aggregate) {
      final Aggregate agg = (Aggregate) rel;
      if (agg.getAggCallList().size() == 1) {
        AggregateCall aggCall = agg.getAggCallList().get(0);
        // Make Pig aggregates 10 times more expensive to have the @PigToSqlAggregateRule applied.
        if (aggCall.getAggregation().getName().equals("COLLECT")) {
          cost = costFactory.makeCost(10 * cost.getRows(), 10 * cost.getCpu(), 10 * cost.getIo());
        }
      }
    }
    return cost;
  }



  public RelNode toBeamPlan(RelNode logicalPlan) {
    final Program program = Programs.of(RuleSets.ofList(ruleSet));
    final RelNode resetPlan = resetPlanner(logicalPlan);
    final RelCollation collation = resetPlan instanceof Sort ? ((Sort) resetPlan).collation : RelCollations.EMPTY;
    final RelNode physicalPlan = program.run(this, resetPlan,
        resetPlan.getTraitSet().replace(BeamConvention.INSTANCE).replace(collation).simplify(),
        ImmutableList.of(), ImmutableList.of());
    setNodeIds(physicalPlan);
    return physicalPlan;
  }

  private void setNodeIds(RelNode relNode) {
    for (RelNode relNode1: relNode.getInputs()) {
      setNodeIds(relNode1);
    }
    final BeamNode beamNode = (BeamNode) relNode;
    beamNode.setBeamNodeId(this.getBeamNodeId(relNode.getId()));
  }

  private RelNode resetPlanner(RelNode root) {
    final RelOptCluster newCluster = RelOptCluster.create(this, root.getCluster().getRexBuilder());
    return copyPlan(root, newCluster);
  }

  private RelNode copyPlan(RelNode relNode, RelOptCluster newCluster) {
    if (relNode instanceof TableScan) {
      return LogicalTableScan.create(newCluster, relNode.getTable());
    }

    if (relNode instanceof Values) {
      return LogicalValues.create(newCluster, relNode.getRowType(), ((Values) relNode).tuples);
    }

    if (relNode instanceof SingleRel) {
      final RelNode inputRel = copyPlan(((SingleRel) relNode).getInput(), newCluster);
      if (relNode instanceof Filter) {
        return LogicalFilter.create(inputRel, ((Filter) relNode).getCondition());
      }
      if (relNode instanceof Project) {
        final Project project = (Project) relNode;
        return LogicalProject.create(inputRel, project.getProjects(), project.getRowType());
      }
      if (relNode instanceof Aggregate) {
        final Aggregate aggregate = (Aggregate) relNode;
        return LogicalAggregate.create(
            inputRel, aggregate.getGroupSet(), aggregate.getGroupSets(), aggregate.getAggCallList());
      }
      if (relNode instanceof Uncollect) {
        final Uncollect uncollect = (Uncollect) relNode;
        return Uncollect.create(uncollect.getTraitSet(), inputRel, uncollect.withOrdinality);
      }
    }

    if (relNode instanceof BiRel) {
      final RelNode left = copyPlan(((BiRel) relNode).getLeft(), newCluster);
      final RelNode right = copyPlan(((BiRel) relNode).getRight(), newCluster);
      if (relNode instanceof Join) {
        final Join join = (Join) relNode;
        return LogicalJoin.create(left, right, join.getCondition(), join.getVariablesSet(), join.getJoinType());
      }
      if (relNode instanceof Correlate) {
        final Correlate correl = (Correlate) relNode;
        return LogicalCorrelate.create(
            left, right, correl.getCorrelationId(), correl.getRequiredColumns(), correl.getJoinType());
      }
    }

    if (relNode instanceof Union) {
      final Union union = (Union) relNode;
      final List<RelNode> newInputs = new ArrayList<>();
      for (RelNode child : union.getInputs()) {
        newInputs.add(copyPlan(child, newCluster));
      }
      return LogicalUnion.create(newInputs, union.all);
    }

    throw new UnsupportedOperationException(
        relNode.getRelTypeName() + " is not supported for being translated into Beam code");
  }
}
