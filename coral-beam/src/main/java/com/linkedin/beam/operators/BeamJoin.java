/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.utils.RexBeamUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.beam.sdk.schemas.utils.AvroUtils;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.Row;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.CorrelationId;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.JoinInfo;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rex.RexNode;

import static com.linkedin.beam.utils.MethodNames.*;
import static com.linkedin.beam.utils.Methods.*;


public class BeamJoin extends Join implements BeamNode {
  // Stream record ttl in minutes for STREAM_STREAM joins
  // or table record ttl in days for STREAM_TABLE joins
  private final long ttl;
  private int beamNodeId;
  private final String leftTimestampField;
  private final String rightTimestampField;
  private Expression leftKey = null;
  private Expression rightKey = null;

  public BeamJoin(RelOptCluster cluster, RelTraitSet traitSet, RelNode left, String leftTimestampField, RelNode right,
      String rightTimestampField, RexNode condition, Set<CorrelationId> variablesSet, JoinRelType joinType, long ttl) {
    super(cluster, traitSet, left, right, condition, variablesSet, joinType);
    this.leftTimestampField = leftTimestampField;
    this.rightTimestampField = rightTimestampField;
    this.ttl = ttl;
  }

  public static BeamJoin create(RelNode left, String leftTimestampField, RelNode right, String rightTimestampField,
      RexNode conditionExpr, Set<CorrelationId> variablesSet, JoinRelType joinType, long joinDuration) {
    return new BeamJoin(left.getCluster(), left.getCluster().traitSetOf(BeamConvention.INSTANCE), left,
        leftTimestampField, right, rightTimestampField, conditionExpr, variablesSet, joinType, joinDuration);
  }

  @Override
  public Join copy(RelTraitSet traitSet, RexNode conditionExpr, RelNode left, RelNode right, JoinRelType joinType,
      boolean semiJoinDone) {
    assert traitSet.containsIfApplicable(BeamConvention.INSTANCE);
    final BeamJoin newNode =
        new BeamJoin(getCluster(), getCluster().traitSetOf(Convention.NONE), left, leftTimestampField, right,
            rightTimestampField, conditionExpr, variablesSet, joinType, ttl);
    return newNode;
  }

  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    buildKeyExprs();
    generateStreamStreamJoin(statements);
  }

  private void generateStreamStreamJoin(List<Statement> statements) {
    final ParameterExpression leftVar =
        Expressions.parameter(BeamNode.PCOLLECTION_KV_TYPE, getVariableName() + "Left");
    statements.add(Expressions.declare(0, leftVar, getBeamJoinChildExpr(getLeft(), leftKey, getLeftRecord())));

    final ParameterExpression rightVar =
        Expressions.parameter(BeamNode.PCOLLECTION_KV_TYPE, getVariableName() + "Right");
    statements.add(Expressions.declare(0, rightVar, getBeamJoinChildExpr(getRight(), rightKey, getRightRecord())));

    Expression joinExpr = Expressions.call(org.apache.beam.sdk.schemas.transforms.Join.class,
        getBeamJoinMethodName(), rightVar);
    joinExpr = Expressions.call(leftVar, P_COLLECTION_APPLY, joinExpr);

    joinExpr =
        BeamNode.getSetSchemaCoderExpr(this, Expressions.call(joinExpr, P_COLLECTION_APPLY, buildBeamJoinMapFunc()));
    // Reset to global window
    final Expression globalWinExpr = Expressions.call(Window.class, INTO, Expressions.new_(GlobalWindows.class));
    joinExpr = Expressions.call(joinExpr, P_COLLECTION_APPLY, globalWinExpr);
    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this), joinExpr));
  }

  private String getBeamJoinMethodName() {
    switch (joinType) {
      case FULL:
        return "fullOuterJoin";
      case LEFT:
        return "leftOuterJoin";
      case RIGHT:
        return "rightOuterJoin";
      default:
        return "innerJoin";
    }
  }

  private Expression applyBeamJoinKey(RelNode child, Expression joinKey, ParameterExpression childRecord) {
    final Expression valueCreate = Expressions.call(Values.class, CREATE);
    final Expression finalExpr =
        Expressions.call(BeamNode.getBeamNodeVar((BeamNode) child), P_COLLECTION_APPLY, valueCreate);
    return RexBeamUtils.getBeamKeyFunc(finalExpr, childRecord, joinKey);
  }

  private Expression getBeamJoinChildExpr(RelNode child, Expression joinKey, ParameterExpression childRecord) {
    final Expression keyExpr = applyBeamJoinKey(child, joinKey, childRecord);
    final Expression duration =
        Expressions.call(org.joda.time.Duration.class, STANDARD_MINUTES, Expressions.constant(ttl, long.class));
    final Expression fixedWindow = Expressions.call(FixedWindows.class, OF, duration);
    return Expressions.call(keyExpr, P_COLLECTION_APPLY, Expressions.call(Window.class, INTO, fixedWindow));
  }

private Expression buildBeamJoinMapFunc() {
  final ParameterExpression rowVar = Expressions.parameter(Row.class, getVariableName() + "Row");
  final List<Statement> joinStatements = new ArrayList<>();
  joinStatements.add(Expressions.declare(0, getJoinedRecord(),
      Expressions.call(AvroUtils.class, "toGenericRecord", rowVar, BeamNode.getNodeSchemaVar(this))));
  final ParameterExpression keyVar = Expressions.parameter(String.class, "key");
  joinStatements.add(Expressions.declare(0, keyVar,
      RexBeamUtils.getStringKeyExpr(joinInfo.leftKeys, getRowType(), getJoinedRecord())));

  joinStatements.add(Expressions.return_(null, Expressions.call(KV.class, OF,
      keyVar, getJoinedRecord())));
  final Expression joinMapExpr =
      RexBeamUtils.makeLambdaFunction(Types.of(SimpleFunction.class, Row.class, KV_TYPE), KV_TYPE,
          Collections.singletonList(rowVar), joinStatements, new HashSet<>());
  return Expressions.call(MapElements.class, VIA, joinMapExpr);
}

  private void buildKeyExprs() {
    if (leftKey == null || rightKey == null) {
      final JoinInfo joinInfo = analyzeCondition();
      leftKey = RexBeamUtils.getStringKeyExpr(joinInfo.leftKeys, getLeft().getRowType(), getLeftRecord());
      rightKey = RexBeamUtils.getStringKeyExpr(joinInfo.rightKeys, getRight().getRowType(), getRightRecord());
    }
  }

  private ParameterExpression getLeftRecord() {
    return BeamNode.getRecordVar((BeamNode) getLeft());
  }

  private ParameterExpression getRightRecord() {
    return BeamNode.getRecordVar((BeamNode) getRight());
  }

  private ParameterExpression getJoinedRecord() {
    return BeamNode.getRecordVar(this);
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
