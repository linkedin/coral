/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.linkedin.beam.excution.BeamExecUtil;
import com.linkedin.beam.excution.MessageDedup;
import com.linkedin.beam.planner.BeamConvention;
import com.linkedin.beam.planner.BeamPlanner;
import com.linkedin.beam.planner.CalciteBeamConfig;
import com.linkedin.beam.utils.MethodNames;
import com.linkedin.beam.utils.Methods;
import com.linkedin.beam.utils.RelDataTypeToAvro;
import com.linkedin.beam.utils.RexBeamUtils;
import com.linkedin.beam.utils.RexToBeamConverter;
import com.linkedin.beam.utils.AvroJavaTypeFactory;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.util.ImmutableBitSet;

import static com.linkedin.beam.utils.MethodNames.*;


public class BeamAggregate extends Aggregate implements BeamNode {
  private int beamNodeId;
  private final long aggregateWindowMinutes;

  private BeamAggregate(RelOptCluster cluster, RelTraitSet traitSet, RelNode input,
      ImmutableBitSet groupSet, List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls,
      long aggregateWindowMinutes) {
    super(cluster, traitSet, input, groupSet, groupSets, aggCalls);
    this.aggregateWindowMinutes = aggregateWindowMinutes;
  }

  public static BeamAggregate create(RelNode input, ImmutableBitSet groupSet,
      List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls, long aggregateWindowMinutes) {
    final RelOptCluster cluster = input.getCluster();
    return new BeamAggregate(cluster, cluster.traitSetOf(BeamConvention.INSTANCE), input, groupSet,
        groupSets, aggCalls, aggregateWindowMinutes);
  }
  @Override
  public String getVariableName() {
    return BeamNode.getNodeName(this);
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
  public Aggregate copy(RelTraitSet traitSet, RelNode input, ImmutableBitSet groupSet,
      List<ImmutableBitSet> groupSets, List<AggregateCall> aggCalls) {
    assert traitSet.containsIfApplicable(BeamConvention.INSTANCE);
    final BeamAggregate newNode =
        new BeamAggregate(getCluster(), traitSet, input, groupSet, groupSets, aggCalls,
            aggregateWindowMinutes);
    return newNode;
  }

  @Override
  public void toBeamStatement(List<Statement> statements) {
    final ParameterExpression childRecord = BeamNode.getRecordVar((BeamNode) getInput());

    if (aggCalls.isEmpty()) {
      // Handle distinct
      final Expression streamKey = getDistinctStringKey(childRecord);
      statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this), buildDistinctExpr(streamKey, childRecord)));
      return;
    }

    final Expression groupKey = RexBeamUtils.getStringKeyExpr(groupSet, getInput().getRowType(), childRecord);
    final ParameterExpression aggregateKV =
        Expressions.parameter(BeamNode.PCOLLECTION_KV_TYPE, getVariableName() + "KV");
    statements.add(Expressions.declare(0, aggregateKV, getAggregateKVExpr(groupKey, childRecord)));

    ParameterExpression groupedRecords =
        Expressions.parameter(VITER_TYPE, ((BeamNode) getInput()).getVariableName() + "GroupedRecords");

    final Expression iterator =
        Expressions.call(groupedRecords, Types.lookupMethod(Iterable.class, MethodNames.ITERATOR));
    List<Statement> aggStatements = getMapBodyStatements(iterator);
    final Expression perKeyFunc =
        RexBeamUtils.makeLambdaFunction(Types.of(SerializableFunction.class, VITER_TYPE, GenericRecord.class),
            GenericRecord.class, ImmutableList.of(groupedRecords), aggStatements, new HashSet<>());

    final Expression combineExpr = Expressions.call(Combine.class, PER_KEY, perKeyFunc);
    Expression aggExpr = Expressions.call(aggregateKV, Methods.P_COLLECTION_APPLY, combineExpr);

    if (!aggCalls.isEmpty()) {
      aggExpr = BeamNode.getSetSchemaCoderExpr(this, aggExpr);
    }
    // Reset to global window
    final Expression globalWinExpr = Expressions.call(org.apache.beam.sdk.transforms.windowing.Window.class, INTO,
        Expressions.new_(GlobalWindows.class));
    aggExpr = Expressions.call(aggExpr, Methods.P_COLLECTION_APPLY, globalWinExpr);
    statements.add(Expressions.declare(0, BeamNode.getBeamNodeVar(this), aggExpr));
  }

  private Expression buildDistinctExpr(Expression groupKey, ParameterExpression childRecord) {
    final Expression finalExpr = resetKeyWithGroupbyCols(groupKey, childRecord);
    final Expression dedupCall = Expressions.call(MessageDedup.class, WITH_IN, buildDedupDuration());
    return Expressions.call(finalExpr, Methods.P_COLLECTION_APPLY, dedupCall);
  }

  private Expression buildDedupDuration() {
    final CalciteBeamConfig config = ((BeamPlanner) getCluster().getPlanner())._calciteBeamConfig;
    if (config.dedupTimeGranularity == Calendar.DATE) {
      return Expressions.call(org.joda.time.Duration.class, STANDARD_DAYS,
          Expressions.constant(config.dedupTimeValue, int.class));
    }
    throw new UnsupportedOperationException(
        "Calendar field of " + config.dedupTimeGranularity + " not supported");
  }

  private Expression resetKeyWithGroupbyCols(Expression groupKey, ParameterExpression childRecord) {
    final ParameterExpression childExpr = BeamNode.getBeamNodeVar((BeamNode) getInput());
    final Expression valueCreate = Expressions.call(Values.class, CREATE);
    final Expression finalExpr = Expressions.call(childExpr, Methods.P_COLLECTION_APPLY, valueCreate);
    return RexBeamUtils.getBeamKeyFunc(finalExpr, childRecord, groupKey);
  }

  private Expression getAggregateKVExpr(Expression groupKey, ParameterExpression childRecord) {
    final Expression finalExpr = resetKeyWithGroupbyCols(groupKey, childRecord);
    final Expression duration = Expressions.call(org.joda.time.Duration.class, STANDARD_MINUTES,
        Expressions.constant(aggregateWindowMinutes, long.class));
    final Expression fixedWindow = Expressions.call(FixedWindows.class, OF, duration);
    return Expressions.call(finalExpr, Methods.P_COLLECTION_APPLY,
        Expressions.call(org.apache.beam.sdk.transforms.windowing.Window.class, INTO, fixedWindow));
  }

  private Expression getDistinctStringKey(Expression recordName) {
    final CalciteBeamConfig config = ((BeamPlanner) getCluster().getPlanner())._calciteBeamConfig;
    final Expression timestampCol = Expressions.constant(config.timestampField, String.class);
    final Expression prefix = Expressions.constant(getVariableName() + "_", String.class);
    return Expressions.call(BeamExecUtil.class, MethodNames.BUILD_DISTINCT_STRING_KEY_FROM_RECORD, prefix, recordName,
        timestampCol);
  }

  // Generate statements for body of map methods
  private List<Statement> getMapBodyStatements(Expression getIterator) {
    if (aggCalls.isEmpty()) {
      final Expression msgExpr = Expressions.call(getIterator, Types.lookupMethod(Iterator.class, MethodNames.NEXT));
      return Collections.singletonList(Expressions.return_(null, msgExpr));
    }

    final List<Statement> statements = new ArrayList<>();
    final ParameterExpression aggSchema = BeamNode.getNodeSchemaVar(this);
    final ParameterExpression aggRecord = BeamNode.getRecordVar(this);

    // Declare aggregate record. Code example:
    // GenericRecord aggregate3Record = new GenericData.Record(AGGREGATE3_SCHEMA);
    statements.add(Expressions.declare(0, aggRecord, Expressions.new_(GenericData.Record.class, aggSchema)));

    // Declare iterator. Code example:
    // Iterator<GenericRecord> iterator = windowPane.getMessage()).iterator();
    final ParameterExpression iterator =
        Expressions.parameter(Types.of(Iterator.class, GenericRecord.class), MethodNames.ITERATOR);
    statements.add(Expressions.declare(0, iterator, getIterator));

    // Declare input record. Code example:
    // GenericRecord inputRecord = iterator.next();
    final ParameterExpression inputRecord = Expressions.parameter(GenericRecord.class, "inputRecord");
    final Expression iteratorNext = Expressions.call(iterator, MethodNames.NEXT);
    statements.add(Expressions.declare(0, inputRecord, iteratorNext));

    // Get all group columns for the aggregate records
    statements.addAll(getGroupColumns(inputRecord, aggRecord));

    // Get aggregate calls
    statements.addAll(getAggregateCalls(inputRecord, aggRecord, iterator));

    statements.add(Expressions.return_(null, aggRecord));
    return statements;
  }

  // Statements to retrieve group columns
  private List<Statement> getGroupColumns(ParameterExpression inputRecord, ParameterExpression aggRecord) {
    final RelDataType inputRowtype = getInput().getRowType();
    final List<Statement> results = new ArrayList<>();
    for (int col : groupSet) {
      final Expression valueExpr =
          RexBeamUtils.toObjectType(RexBeamUtils.getFieldExpression(inputRowtype, col, inputRecord));
      // Then add value of the field to avro record. Code example:
      // aggregate3Record.put("longGroupCol", inputRecord.get("longGroupCol"));
      results.add(Expressions.statement(Expressions.call(aggRecord, MethodNames.AVRO_PUT,
          BeamNode.getAvroName(inputRowtype.getFieldNames().get(col)), valueExpr)));
    }
    return results;
  }

  // Statements to implement aggregate calls
  private List<Statement> getAggregateCalls(ParameterExpression inputRecord, ParameterExpression aggRecord,
      Expression iterator) {
    final List<Statement> results = new ArrayList<>();

    // Implementation for all aggregate calls
    final List<AggCallImplementation> aggCallImps = new ArrayList<>();
    // Map from input column index to a pair of column variable expression and the column value expression
    final Map<Integer, List<Expression>> inputColMap = new HashMap<>();
    // Statements after while statement to save the final aggregate values into the aggregate record
    final List<Statement> afterWhileStmts = new ArrayList<>();

    int aggCol = getGroupCount();
    for (AggregateCall aggCall : getAggCallList()) {
      final ParameterExpression aggVar = getColumnVar(this, aggCol, true);
      if (aggCall.getAggregation().getKind() == SqlKind.COLLECT) {
        // Need to register schema for COLLECT
        final RexToBeamConverter rexTranslator = ((BeamPlanner) getCluster().getPlanner()).getRexTranslator();
        rexTranslator.registerSchema(RexBeamUtils.getSchemaName(aggVar.name), aggCall.getType());
      }

      // Collect all input variable for aggregate calls, add input col var decl into while body. Code example:
      // Long project2longCol = (Long) inputRecord.get("longCol");
      collectAggregateInput(inputRecord, aggCall, inputColMap);

      // Get agg call implementation
      final AggCallImplementation callImp = AggCallImplementation.getAggCallImpl(aggCall, aggVar, inputColMap);
      aggCallImps.add(callImp);

      // Declare and initialize aggregate variable. Code example:
      // long aggregate3_f13 = 1;
      results.add(Expressions.declare(0, aggVar, callImp.init()));

      // Store the final aggregate values to the aggregate record. Code example:
      // aggregate3Record.put("_f3", (Object) aggregate3_f3);
      final Expression aggResult = RexBeamUtils.toObjectType(callImp.result());
      afterWhileStmts.add(Expressions.statement(Expressions.call(aggRecord, MethodNames.AVRO_PUT,
          BeamNode.getAvroName(getRowType().getFieldNames().get(aggCol)), aggResult)));

      aggCol++;
    }

    // Construct the while body
    final List<Statement> whileBodyStmts = new ArrayList<>();

    // Declare local variable. Code example (for COUNT):
    // long aggregate3_f3 = 1;
    inputColMap.values()
        .stream()
        .forEach(f -> whileBodyStmts.add(Expressions.declare(0, (ParameterExpression) f.get(0), f.get(1))));

    // Add aggregate implementation to while body. Code example for COUNT:
    // if (project2longCol != null) {
    //    aggregate3_f3 = aggregate3_f3 + 1;
    //  }
    aggCallImps.stream().forEach(f -> whileBodyStmts.addAll(f.aggregate()));

    // Loop condition variable. Code example:
    // boolean doLoop = true;
    final ParameterExpression loopCond = Expressions.parameter(boolean.class, "doLoop");
    results.add(Expressions.declare(0, loopCond, Expressions.constant(true)));

    // Advance to next iterator when necessary. Code example:
    // if (iterator.hasNext()) {
    //   inputRecord = iterator.next();
    // } else {
    //   doLoop = false;
    // }
    final Expression ifCond = Expressions.call(iterator, MethodNames.HAS_NEXT);
    final Expression nextIter = Expressions.assign(inputRecord, Expressions.call(iterator, MethodNames.NEXT));
    final Expression loopEnd = Expressions.assign(loopCond, Expressions.constant(false));
    whileBodyStmts.add(Expressions.ifThenElse(ifCond, Expressions.statement(nextIter), Expressions.statement(loopEnd)));

    // Add while block to main code. Code example:
    // while (doLoop) {
    //   // while body
    // }
    results.add(Expressions.while_(loopCond, Expressions.block(whileBodyStmts)));
    results.addAll(afterWhileStmts);
    return results;
  }

  // Collects statements for getting input cols of an aggregate call
  private void collectAggregateInput(ParameterExpression inputRecord, AggregateCall aggCall,
      Map<Integer, List<Expression>> inputColMap) {
    final RelDataType inputRowtype = getInput().getRowType();
    for (int col : aggCall.getArgList()) {
      if (!inputColMap.containsKey(col)) {
        // Declare input col var. Code example:
        // Long project2longCol = (Long) inputRecord.get("longCol");
        final Expression colExpr = RexBeamUtils.getFieldExpression(inputRowtype, col, inputRecord);
        final Expression colVar = getColumnVar(getInput(), col, false);
        inputColMap.put(col, Lists.newArrayList(colVar, colExpr));
      }
    }
  }

  // Gets variable expression for a column
  private static ParameterExpression getColumnVar(RelNode relNode, int colIndex, boolean mutableType) {
    final RelDataTypeField colField = relNode.getRowType().getFieldList().get(colIndex);
    final Type colType = mutableType ? AvroJavaTypeFactory.AVRO_TYPE_FACTORY.getMutableJavaClass(colField.getType())
        : AvroJavaTypeFactory.AVRO_TYPE_FACTORY.getImmutableJavaClass(colField.getType());
    final String varName =
        ((BeamNode) relNode).getVariableName() + RelDataTypeToAvro.toAvroQualifedName(colField.getName());
    return Expressions.parameter(colType, varName);
  }
}
