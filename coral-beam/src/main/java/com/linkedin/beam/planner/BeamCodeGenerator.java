/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.excution.KafkaIOGenericRecord;
import com.linkedin.beam.operators.BeamNode;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.ClassDeclaration;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MemberDeclaration;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.rel.RelNode;

import static com.linkedin.beam.utils.MethodNames.*;


/**
 * Code generator for Beam APIs.
 */
public class BeamCodeGenerator extends CodeGenerator {
  private static final ParameterExpression ARGS = Expressions.parameter(String[].class, "args");
  public static final ParameterExpression PIPELINE_OPTIONS =
      Expressions.parameter(PipelineOptions.class, "pipelineOpts");
  public static final ParameterExpression PIPELINE = Expressions.parameter(Pipeline.class, "pipeline");

  protected BeamCodeGenerator(CalciteBeamConfig calciteBeamConfig) {
    super(calciteBeamConfig);
  }

  @Override
  protected String toJavaCode(RelNode beamPlan) {
    final List<MemberDeclaration> initVarDeclarations = new ArrayList<>();

    final List<Statement> mainMethodStmts = new ArrayList<>();
    final Statement initStaticVarsStmt = initStaticVars(initVarDeclarations);
    if (initStaticVarsStmt != null) {
      mainMethodStmts.add(initStaticVarsStmt);
    }

    final List<MemberDeclaration> classDeclarations = new ArrayList<>();

    final MemberDeclaration mainMethod = generateMainMethod(beamPlan, mainMethodStmts);

    // FIRST: declare all required schema constants
    classDeclarations.addAll(declareSchemas(beamPlan));

    // SECOND: declare other static variables
    classDeclarations.addAll(initVarDeclarations);

    // THIRD: declare the main() method
    classDeclarations.add(mainMethod);

    final ClassDeclaration classDeclaration =
        Expressions.classDecl(Modifier.PUBLIC, _calciteBeamConfig.applicationName, null,
            ImmutableList.of(), classDeclarations);

    return Expressions.toString(classDeclaration);
  }

  @Override
  protected void beamNodeToStatement(BeamNode beamNode, List<Statement> statements) {
    beamNode.toBeamStatement(statements);
  }

  private MemberDeclaration generateMainMethod(RelNode beamPlan, List<Statement> stmts) {
    // PipelineOption
    stmts.add(Expressions.declare(Modifier.FINAL, PIPELINE_OPTIONS,
        Expressions.call(PipelineOptionsFactory.class, CREATE)));

    // Pipeline
    stmts.add(Expressions.declare(Modifier.FINAL, PIPELINE,
        Expressions.call(Pipeline.class, CREATE, ImmutableList.of(PIPELINE_OPTIONS))));

    // Convert all Beam nodes into Java code
    relNodeToJava(beamPlan, stmts);

    // Add the final write
    stmts.add(addFinalWrite(beamPlan));

    // run
    stmts.add(Expressions.statement(Expressions.call(PIPELINE, RUN)));

    final BlockBuilder body = new BlockBuilder(false);
    stmts.forEach(body::add);

    return Expressions.methodDecl(Modifier.PUBLIC | Modifier.STATIC, void.class, MAIN,
        ImmutableList.of(ARGS), body.toBlock());
  }

  private Statement addFinalWrite(RelNode beamPlan) {
    final Expression rootExpr = BeamNode.getBeamNodeVar((BeamNode) beamPlan);
    final Expression writeRecord = Expressions.call(KafkaIOGenericRecord.class, WRITE);
    final Expression withTopic = Expressions.call(writeRecord, WITH_TOPIC,
        Expressions.constant(_calciteBeamConfig.kafkaOutputTopic, String.class));
    final Method pipelineApplyMethod = Types.lookupMethod(Pipeline.class, APPLY, String.class, PTransform.class);
    final Expression writeKafka = Expressions.call(rootExpr, pipelineApplyMethod, withTopic);
    return Expressions.statement(writeKafka);
  }
}
