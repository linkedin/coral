/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.utils.MethodNames;
import com.linkedin.beam.utils.Methods;
import com.linkedin.beam.utils.RexBeamUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.SingleRel;
import org.apache.calcite.rel.type.RelDataType;

import static com.linkedin.beam.utils.MethodNames.*;


public class BeamArrayFlatten extends SingleRel implements BeamNode {
  private final List<String> flattenCols;
  private int beamNodeId;
  private final RelDataType rowType;

  /**
   * Creates a <code>BeamArrayFlatten</code>.
   * @param cluster Cluster this relational expression belongs to
   * @param traits Rel trait
   * @param input Input relational expression
   */
  private BeamArrayFlatten(RelOptCluster cluster, RelTraitSet traits, RelNode input, List<String> flattenCols,
      RelDataType rowType) {
    super(cluster, traits, input);
    this.flattenCols = flattenCols;
    this.rowType = rowType;
  }

  public static BeamArrayFlatten create(final RelNode input, final List<String> flattenCols, RelDataType rowType) {
    final RelOptCluster cluster = input.getCluster();
    final RelTraitSet traitSet = cluster.traitSetOf(BeamConvention.INSTANCE);
    return new BeamArrayFlatten(cluster, traitSet, input, flattenCols, rowType);
  }

  @Override
  public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
    assert traitSet.containsIfApplicable(BeamConvention.INSTANCE);
    final BeamArrayFlatten newRel =
        new BeamArrayFlatten(getCluster(), traitSet, inputs.get(0), this.flattenCols, this.rowType);
    return newRel;
  }

  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final ParameterExpression inputKV = BeamNode.getBeamKVVar((BeamNode) getInput());
    final List<Expression> flattenCallArgs =
        ImmutableList.of(inputKV, getFlattenColList(), BeamNode.getNodeSchemaVar(this));
    final Expression flattenCallExpr = Expressions.call(com.linkedin.beam.excution.BeamArrayFlatten.class, MethodNames.FLATTEN, flattenCallArgs);

    final Expression flatMapFuncClass =
        RexBeamUtils.makeLambdaFunction(Types.of(SimpleFunction.class, KV_TYPE, COLLECTION_KV_TYPE), COLLECTION_KV_TYPE,
            Arrays.asList(inputKV), ImmutableList.of(Expressions.return_(null, flattenCallExpr)), null);

    final Expression viaCall = Expressions.call(FlatMapElements.class, VIA, flatMapFuncClass);
    final Expression applyCall =
        Expressions.call(BeamNode.getBeamNodeVar((BeamNode) getInput()), Methods.P_COLLECTION_APPLY, viaCall);

    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this),
        BeamNode.getSetSchemaCoderExpr(this, applyCall)));

  }

  private Expression getFlattenColList() {
    final List<ConstantExpression> flattenColExprs = new ArrayList<>();
    for (String flattenCol : flattenCols) {
      flattenColExprs.add(BeamNode.getAvroName(flattenCol));
    }
    return Expressions.call(ImmutableList.class, MethodNames.OF, flattenColExprs);
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
  protected RelDataType deriveRowType() {
    return rowType;
  }
}
