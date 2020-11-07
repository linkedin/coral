/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import com.linkedin.beam.utils.Methods;
import com.linkedin.beam.utils.RexBeamUtils;
import com.linkedin.beam.utils.RexToBeamConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.avro.generic.GenericData;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexNode;

import static com.linkedin.beam.utils.MethodNames.*;


public class BeamProject extends Project implements BeamNode {
  private int beamNodeId;

  private BeamProject(RelOptCluster cluster, RelTraitSet traits, RelNode input, List<? extends RexNode> projects,
      RelDataType rowType) {
    super(cluster, traits, input, projects, rowType);
  }

  @Override
  public Project copy(RelTraitSet traitSet, RelNode input, List<RexNode> projects, RelDataType rowType) {
    final BeamProject newNode = new BeamProject(getCluster(), traitSet, input, exps, rowType);
    return newNode;
  }

  public static BeamProject create(final RelNode input, final List<? extends RexNode> projects, RelDataType rowType) {
    final RelOptCluster cluster = input.getCluster();
    return new BeamProject(cluster, cluster.traitSet().replace(BeamConvention.INSTANCE), input, projects, rowType);
  }

  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final List<Statement> projectStatements = new ArrayList<>();

    // Get inputRecord from KVPair
    projectStatements.add(BeamNode.getRecordFromKV((BeamNode) getInput()));

    final Set<Class> bodyExceptions = new HashSet<>();
    generateProjectStatements(projectStatements, bodyExceptions);
    // Add  the final return statement
    final ParameterExpression inputKV = BeamNode.getBeamKVVar((BeamNode) getInput());
    projectStatements.add(Expressions.return_(null, Expressions.call(KV.class, OF,
        ImmutableList.of(Expressions.call(inputKV, GET_KEY), BeamNode.getRecordVar(this)))));

    final Expression mapFuncClass =
        RexBeamUtils.makeLambdaFunction(Types.of(SimpleFunction.class, KV_TYPE, KV_TYPE), KV_TYPE,
            Arrays.asList(inputKV), projectStatements, bodyExceptions);

    final Expression viaCall = Expressions.call(MapElements.class, VIA, mapFuncClass);
    final Expression applyCall =
        Expressions.call(BeamNode.getBeamNodeVar((BeamNode) getInput()), Methods.P_COLLECTION_APPLY, viaCall);

    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this),
        BeamNode.getSetSchemaCoderExpr(this, applyCall)));
  }

  private void generateProjectStatements(List<Statement> projectStatements, Set<Class> bodyExceptions) {
    final RexToBeamConverter rexTranslator = ((BeamPlanner) getCluster().getPlanner()).getRexTranslator();
    final ParameterExpression projectedSchema = BeamNode.getNodeSchemaVar(this);
    final ParameterExpression projectedRecord = BeamNode.getRecordVar(this);
    projectStatements.add(
        Expressions.declare(0, projectedRecord, Expressions.new_(GenericData.Record.class, projectedSchema)));

    // Do projection
    final List<String> fieldNames = getRowType().getFieldNames();
    final List<RexNode> projects = getProjects();
    for (int i = 0; i < projects.size(); i++) {
      final Expression projection = rexTranslator.convert(projects.get(i), getInput(), getFieldSchemaName(i));
      // Add local var declaration statements first
      projectStatements.addAll(rexTranslator.getLocalVarDeclarations());

      // Then add value of the field to avro record
      projectStatements.add(Expressions.statement(
          Expressions.call(projectedRecord, Methods.AVRO_PUT, BeamNode.getAvroName(fieldNames.get(i)), projection)));
      // Capture exceptions if any
      bodyExceptions.addAll(rexTranslator.getLocalExeptions());
    }
  }

  private String getFieldSchemaName(int i) {
    return getVariableName().toUpperCase() + "_COLUMN" + i + "_SCHEMA";
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
