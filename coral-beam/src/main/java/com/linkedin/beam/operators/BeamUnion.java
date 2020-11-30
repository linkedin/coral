/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.linkedin.beam.planner.BeamConvention;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Union;

import static com.linkedin.beam.utils.MethodNames.*;


public class BeamUnion extends Union implements BeamNode {
  private int beamNodeId;

  private BeamUnion(RelOptCluster cluster, RelTraitSet traits, List<RelNode> inputs, boolean all) {
    super(cluster, traits, inputs, all);
  }

  public static BeamUnion create(List<RelNode> inputs, boolean all) {
    assert inputs.size() > 0;
    final RelOptCluster cluster = inputs.get(0).getCluster();
    return new BeamUnion(cluster, cluster.traitSetOf(BeamConvention.INSTANCE), inputs, all);
  }

  @Override
  public BeamUnion copy(RelTraitSet traitSet, List<RelNode> inputs, boolean all) {
    final BeamUnion newNode = new BeamUnion(getCluster(), traitSet, inputs, all);
    return newNode;
  }

  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final List<Expression> childVars = new ArrayList<>();
    for (RelNode relNode : getInputs()) {
      childVars.add(BeamNode.getBeamNodeVar((BeamNode) relNode));
    }
    Expression listExpr = Expressions.call(PCollectionList.class, OF, childVars.get(0));
    for (int i = 1; i < childVars.size(); i++) {
      listExpr = Expressions.call(listExpr, AND, childVars.get(i));
    }

    final Method pCollectionApplyMethod = Types.lookupMethod(PCollectionList.class, APPLY, PTransform.class);
    final Expression merge = Expressions.call(Flatten.class, P_COLLECTIONS);

    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this),
        Expressions.call(listExpr, pCollectionApplyMethod, merge)));
  }

  @Override
  public int getBeamNodeId() {
    return beamNodeId;
  }

  @Override
  public void setBeamNodeId(int beamNodeId) {
    this.beamNodeId = beamNodeId;
  }
}
