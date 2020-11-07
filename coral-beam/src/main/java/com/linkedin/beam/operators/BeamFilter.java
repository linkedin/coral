/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import com.linkedin.beam.utils.RexBeamUtils;
import com.linkedin.beam.utils.RexToBeamConverter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rex.RexNode;

import static com.linkedin.beam.utils.MethodNames.*;


public final class BeamFilter extends Filter implements BeamNode {
  private int beamNodeId;

  private BeamFilter(RelOptCluster cluster, RelTraitSet traitSet, RelNode child, RexNode condition) {
    super(cluster, traitSet, child, condition);
  }

  public static BeamFilter create(final RelNode input, RexNode condition) {
    final RelOptCluster cluster = input.getCluster();
    return new BeamFilter(cluster, cluster.traitSetOf(BeamConvention.INSTANCE), input, condition);
  }

  @Override
  public BeamFilter copy(RelTraitSet traitSet, RelNode input, RexNode condition) {
    assert traitSet.containsIfApplicable(BeamConvention.INSTANCE);
    final BeamFilter newNode = new BeamFilter(getCluster(), traitSet, input, condition);
    return newNode;
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final List<Statement> filterStatements = new ArrayList<>();

    // Get inputRecord from KVPair
    filterStatements.add(BeamNode.getRecordFromKV((BeamNode) getInput()));

    final Set<Class> bodyExceptions = new HashSet<>();
    generateFilterStatements(filterStatements, bodyExceptions);

    final ParameterExpression inputKV = BeamNode.getBeamKVVar((BeamNode) getInput());
    final Expression filterFuncClass =
        RexBeamUtils.makeLambdaFunction(Types.of(SerializableFunction.class, KV_TYPE, Boolean.class), Boolean.class,
            Arrays.asList(inputKV), filterStatements, bodyExceptions);

    final Expression filterCall = Expressions.call(org.apache.beam.sdk.transforms.Filter.class, BY, filterFuncClass);
    final Method pCollectionApplyMethod = Types.lookupMethod(PCollection.class, APPLY, PTransform.class);
    final Expression applyCall =
        Expressions.call(BeamNode.getBeamNodeVar((BeamNode) getInput()), pCollectionApplyMethod, filterCall);

    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this), applyCall));
  }

  private void generateFilterStatements(List<Statement> filterStatements, Set<Class> bodyExceptions) {
    final RexToBeamConverter rexTranslator = ((BeamPlanner) getCluster().getPlanner()).getRexTranslator();
    final Expression filter = rexTranslator.convert(getCondition(), getInput());
    filterStatements.addAll(rexTranslator.getLocalVarDeclarations());
    filterStatements.add(Expressions.return_(null, filter));
    bodyExceptions.addAll(rexTranslator.getLocalExeptions());
  }

  @Override
  public int getBeamNodeId() {
    return beamNodeId;
  }

  @Override
  public void setBeamNodeId(int beamNodeId) {
    this.beamNodeId = beamNodeId;
  }

  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
  }
}
