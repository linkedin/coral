/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.operators;

import com.linkedin.beam.utils.Methods;
import com.linkedin.beam.utils.RexBeamUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.avro.generic.GenericData;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.rel.core.AggregateCall;


/**
 * Implementation Java code for aggregate calls.
 *
 * To implement a new aggregate function, we need to add a new class extending from AggCallImplementation,
 * implement these three methods:
 * 1. init() to provide initial value for the aggregate variable
 * 2. aggregate() to provide a list of statements to aggregate current input message into the aggregate variable
 * 3. result() to provide the final result for the aggregation,
 * and update the switch case statements in getAggCallImpl() to include the new aggregate function.
 *
 * We support two kinds of aggregate functions:
 * 1. Builtin aggregate functions: we will generate code to do the aggregation. This is for simple aggregate
 * functions like COUNT, MIN, MAX, SUM...
 * 2. Custom aggregate function: the aggregate code is written in other libraries and is hooked up into the
 * auto-generated code by appropriate method call. This is for complex aggregate functions like COUNT DISTINCT,
 * QUANTILE,...
 *
 *
 * See sample of a BuiltinAggCall in SumImplementation class
 */
public abstract class AggCallImplementation {
  // Calcite aggregate call
  AggregateCall aggCall;
  // Variable expression for the aggregate
  ParameterExpression aggVar;
  // Map from input column index to a pair of column variable expression and the column value expression
  Map<Integer, List<Expression>> inputColMap;

  private AggCallImplementation(AggregateCall aggCall, ParameterExpression aggVar,
      Map<Integer, List<Expression>> inputColMap) {
    this.aggCall = aggCall;
    this.aggVar = aggVar;
    this.inputColMap = inputColMap;
  }

  /**
   * Gets aggregate call implementation for a given Calcite aggregate call.
   */
  static AggCallImplementation getAggCallImpl(AggregateCall aggCall, ParameterExpression aggVar,
      Map<Integer, List<Expression>> inputColMap) {
    switch (aggCall.getAggregation().kind) {
      case COUNT:
        return new CountImplementation(aggCall, aggVar, inputColMap);
      case SUM:
        return new SumImplementation(aggCall, aggVar, inputColMap);
      case MAX:
        return new MaxImplementation(aggCall, aggVar, inputColMap);
      case MIN:
        return new MinImplementation(aggCall, aggVar, inputColMap);
      case COLLECT:
        return new CollectImplementation(aggCall, aggVar, inputColMap);
      default:
        throw new UnsupportedOperationException("Aggregate function " + aggCall.getAggregation().kind
            + " not yet supported");
    }
  }

  /**
   * Returns an expression to initialize the aggregate variable
   */
  abstract Expression init();

  /**
   * Returns statements for doing aggregate on each input record
   */
  abstract List<Statement> aggregate();

  /**
   * Returns an expression for the final result of the aggregation after consuming all input messages
   */
  abstract Expression result();

  // Adds null check for a statement. Code example:
  // if (var != null) {
  //   ifBody
  // }
  private static Statement checkNotNull(Expression var, Statement ifBody) {
    return Expressions.ifThen(Expressions.notEqual(var, Expressions.constant(null)), ifBody);
  }

  private static abstract class BuiltinAggCall extends AggCallImplementation {

    private BuiltinAggCall(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    public Expression result() {
      // The aggregate result is just the aggregate variable
      return aggVar;
    }
  }

  private static class CollectImplementation extends BuiltinAggCall {

    public CollectImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    public Expression init() {
      final ParameterExpression schema = RexBeamUtils.getSchemaParam(RexBeamUtils.getSchemaName(aggVar.name));
      return Expressions.new_(GenericData.Array.class, schema, Expressions.new_(ArrayList.class));
    }

    @Override
    public List<Statement> aggregate() {
      final List<Statement> results = new ArrayList<>();
      final Expression inputCol = inputColMap.get(aggCall.getArgList().get(0)).get(0);
      final Statement addStatement = Expressions.statement(
          Expressions.call(aggVar, Methods.AVRO_ARRAY_ADD, inputCol));
      results.add(addStatement);
      return results;
    }
  }

  private static class SumImplementation extends BuiltinAggCall {

    public SumImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    public Expression init() {
      return Expressions.constant(0);
    }

    @Override
    public List<Statement> aggregate() {
      final List<Statement> results = new ArrayList<>();
      final Expression inputCol = inputColMap.get(aggCall.getArgList().get(0)).get(0);
      final Statement addStatement = Expressions.statement(
          Expressions.assign(aggVar, Expressions.add(aggVar, inputCol)));
      results.add(checkNotNull(inputCol, addStatement));
      return results;
    }
  }

  private static class CountImplementation extends BuiltinAggCall {

    public CountImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    public Expression init() {
      return Expressions.constant(0);
    }

    @Override
    public List<Statement> aggregate() {
      final List<Statement> results = new ArrayList<>();
      final List<Integer> argList = aggCall.getArgList();
      final Statement incStatement = Expressions.statement(Expressions.assign(aggVar,
          Expressions.add(aggVar, Expressions.constant(1))));
      if (!argList.isEmpty()) {
        // Example code:
        // if (project2longCol != null) {
        //    aggregate3_f8 = aggregate3_f8 + 1;
        // }
        final Expression inputCol = inputColMap.get(aggCall.getArgList().get(0)).get(0);
        results.add(checkNotNull(inputCol, incStatement));
      } else {
        // COUNT(*), always inc
        results.add(incStatement);
      }
      return results;
    }
  }

  private static abstract class MinMaxImplementation extends BuiltinAggCall {

    public MinMaxImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    public Expression init() {
      // Init with the input column value of the first input record. Code example:
      // long aggregate3_f2 = (Long) inputRecord.get("longCol");
      return inputColMap.get(aggCall.getArgList().get(0)).get(1);
    }

    @Override
    public List<Statement> aggregate() {
      final List<Statement> results = new ArrayList<>();
      final Expression inputCol = inputColMap.get(aggCall.getArgList().get(0)).get(0);
      final Expression comparisonExpr = getComparisonExpr(inputCol);
      final Expression binaryCondExpr = Expressions.condition(comparisonExpr, aggVar, inputCol);
      results.add(checkNotNull(inputCol, Expressions.statement(Expressions.assign(aggVar, binaryCondExpr))));
      return results;
    }

    abstract Expression getComparisonExpr(Expression inputCol);
  }

  private static class MaxImplementation extends MinMaxImplementation {

    public MaxImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    Expression getComparisonExpr(Expression inputCol) {
      return inputCol.type != String.class ? Expressions.greaterThan(aggVar, inputCol)
          : Expressions.greaterThan(RexBeamUtils.stringCompareExpr(aggVar, inputCol), Expressions.constant(0));
    }

  }

  private static class MinImplementation extends MinMaxImplementation {
    public MinImplementation(AggregateCall aggCall, ParameterExpression aggVar,
        Map<Integer, List<Expression>> inputColMap) {
      super(aggCall, aggVar, inputColMap);
    }

    @Override
    Expression getComparisonExpr(Expression inputCol) {
      return inputCol.type != String.class ? Expressions.lessThan(aggVar, inputCol)
          : Expressions.lessThan(RexBeamUtils.stringCompareExpr(aggVar, inputCol), Expressions.constant(0));
    }

  }
}
