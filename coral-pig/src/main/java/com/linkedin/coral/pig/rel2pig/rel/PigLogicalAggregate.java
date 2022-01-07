/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.rel.logical.LogicalAggregate;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexCallException;


/**
 * PigLogicalAggregate translates a Calcite LogicalAggregate into Pig Latin.
 */
public class PigLogicalAggregate {

  private static final String GROUP_BY_TEMPLATE = "%s = GROUP %s BY (%s);";
  private static final String GROUP_ALL_TEMPLATE = "%s = GROUP %s ALL;";
  private static final String AGGREGATE_TEMPLATE = "%s = FOREACH %s GENERATE %s;";
  private static final String FIELD_TEMPLATE = "%s AS %s";
  private static final String AGGREGATE_CALL_TEMPLATE = "%s(%s) AS %s";

  private PigLogicalAggregate() {

  }

  /**
   * Translates a Calcite LogicalAggregate into Pig Latin
   *
   * @param logicalAggregate The Calcite LogicalAggregate to be translated
   * @param outputRelation The variable that stores the aggregate output
   * @param inputRelation The variable that has stored the Pig relation to be aggregated
   * @return The Pig Latin for the logicalAggregate in the form of:
   *             [outputRelation] = GROUP [inputRelation] BY [groupingSet];
   *             [outputRelation] = FILTER [inputRelation] BY [logicalFilter.expressions]
   */
  public static String getScript(LogicalAggregate logicalAggregate, String outputRelation, String inputRelation) {

    // TODO: Add support for GROUPING SETS using null literal projections and UNIONs
    if (logicalAggregate.getGroupSets().size() != 1) {
      throw new UnsupportedRexCallException("Only grouping sets of size 1 is supported");
    }

    final String groupBy = getGroupByStatement(logicalAggregate, outputRelation, inputRelation);
    final String aggregate = getForEachStatement(logicalAggregate, outputRelation, inputRelation, inputRelation);

    return String.join("\n", groupBy, aggregate);
  }

  /**
   * Translates SQL GROUP BY sets in LogicalAggregates into Pig Latin
   *
   * @param logicalAggregate The Calcite LogicalAggregate to be translated
   * @param outputRelation The variable that stores the aggregate output
   * @param inputRelation The variable that has stored the Pig relation to be aggregated
   * @return The Pig Latin for a SQL GROUP BY operator in the form of:
   *             [outputRelation] = GROUP [inputRelation] BY [groupingSet];
   *                 OR
   *             [outputRelation] = GROUP [inputRelation] ALL;
   *
   *         For example, if 'input' is grouped by fields (a,b) and dumped to 'output',
   *         the GROUP BY call will look like:
   *             "output = GROUP input BY (a,b);"
   */
  private static String getGroupByStatement(LogicalAggregate logicalAggregate, String outputRelation,
      String inputRelation) {

    final List<Integer> groupSet = logicalAggregate.getGroupSet().toList();

    if (groupSet.isEmpty()) {
      return String.format(GROUP_ALL_TEMPLATE, outputRelation, inputRelation);
    }

    final List<String> inputFieldNames = PigRelUtils.getOutputFieldNames(logicalAggregate.getInput());

    final String groupByFieldNames = groupSet.stream().map(inputFieldNames::get).collect(Collectors.joining(", "));

    return String.format(GROUP_BY_TEMPLATE, outputRelation, inputRelation, groupByFieldNames);
  }

  /**
   * Translates aggregate function calls in LogicalAggregates into Pig Latin
   *
   * @param logicalAggregate The Calcite LogicalAggregate to be translated
   * @param outputRelation The variable that stores the aggregate output
   * @param inputRelation The variable that has stored the Pig relation to be aggregated
   * @param bagIdentifier The identifier for the bag that is formed by the Pig group used for the aggregate.
   * @return The Pig Latin for a SQL Aggregate operator in the form of:
   *             [outputRelation] = FOREACH [inputRelation] GENERATE [groupingSets] , [aggregateFunctionCalls] ;
   *
   *         For example, if the AVG function is called on field 'b' for an 'input' that is grouped by fields (a,b)
   *         and dumped to 'output', the AGGREGATE call will look like:
   *             "output = FOREACH input GENERATE group.a, group.b, AVG(b);"
   */
  private static String getForEachStatement(LogicalAggregate logicalAggregate, String outputRelation,
      String inputRelation, String bagIdentifier) {

    final List<String> outputFieldNames = PigRelUtils.getOutputFieldNames(logicalAggregate);
    final List<String> inputFieldNames = PigRelUtils.getOutputFieldNames(logicalAggregate.getInput());

    String aggregateProjection =
        getAggregateFunctionCalls(logicalAggregate, outputFieldNames, inputFieldNames, bagIdentifier);

    final List<Integer> groupSet = logicalAggregate.getGroupSet().toList();
    if (!groupSet.isEmpty()) {
      final String groupSetFields = getGroupSetFields(groupSet, outputFieldNames, inputFieldNames);
      aggregateProjection = String.join(", ", groupSetFields, aggregateProjection);
    }

    return String.format(AGGREGATE_TEMPLATE, outputRelation, inputRelation, aggregateProjection);
  }

  /**
   * Translates a GROUP BY set into a projection list in Pig Latin
   *
   * @param groupSet List of column index references
   * @param outputFieldNames List-index based mapping from Calcite index reference to field name of
   *                         the output.
   * @param inputFieldNames List-index based mapping from Calcite index reference to field name of
   *                        the input.
   * @return The Pig Latin for the projection list of grouped columns in the form of:
   *             ([groupedColumnN] AS [columnAliasN] (,)* )+
   *
   *         For example, if an Aggregate function is grouped by fields (a,b), its group set fields would look like:
   *             "group.a AS a, group.b AS b"
   *
   *         If there is only one field (a), its group set fields would look like:
   *             "group AS a"
   */
  private static String getGroupSetFields(List<Integer> groupSet, List<String> outputFieldNames,
      List<String> inputFieldNames) {

    if (groupSet.size() == 1) {
      return String.format(FIELD_TEMPLATE, "group", outputFieldNames.get(groupSet.get(0)));
    }

    return groupSet.stream()
        .map(groupByFieldIndex -> String.format(FIELD_TEMPLATE,
            String.format("group.%s", inputFieldNames.get(groupByFieldIndex)), outputFieldNames.get(groupByFieldIndex)))
        .collect(Collectors.joining(", "));
  }

  /**
   * Translates the aggregate functions called in a LogicalAggregate into a projection list of expressions for
   * each aggregate function called.
   *
   * @param logicalAggregate The Calcite LogicalAggregate to be translated
   * @param outputFieldNames List-index based mapping from Calcite index reference to field accessors of
   *                         the output.
   * @param inputFieldNames List-index based mapping from Calcite index reference to field accessors of
   *                        the input.
   * @param bagIdentifier The identifier of the bag produced by the aggregate.
   * @return The Pig Latin for the projection list of aggregate functions in the form of:
   *             ([AggregateFunction] ( [arguments] ) AS [aggregateFunctionAlias] (,)* )+
   *
   *         For example, a relation with a bagIdentifier 'id' grouped by fields ('a', 'b') with function calls to
   *         AVG(a) as 'average' and SUM(b) as 'total' in Pig Latin would look like:
   *             "AVG(id.a) AS average , SUM(id.b) AS total"
   */
  private static String getAggregateFunctionCalls(LogicalAggregate logicalAggregate, List<String> outputFieldNames,
      List<String> inputFieldNames, String bagIdentifier) {

    final int groupBySetOffset = logicalAggregate.getGroupSet().toList().size();
    final List<String> aggregateStatements = new ArrayList<>();

    for (int i = 0; i < logicalAggregate.getAggCallList().size(); ++i) {
      final AggregateCall aggregateCall = logicalAggregate.getAggCallList().get(i);
      String aggregateCallArguments = aggregateCall.getArgList().stream()
          .map(inputFieldIndex -> String.format("%s.%s", bagIdentifier, inputFieldNames.get(inputFieldIndex)))
          .collect(Collectors.joining(", "));

      // We project the entire bag if there are no aggregate calls.
      // This is used in Calcite for operations such as joins.
      if (aggregateCallArguments.isEmpty()) {
        aggregateCallArguments = bagIdentifier;
      }

      // TODO Create a function mapping from Hive/Transport/Built-in functions to Pig
      aggregateStatements
          .add(String.format(AGGREGATE_CALL_TEMPLATE, aggregateCall.getAggregation().getName().toUpperCase(),
              aggregateCallArguments, outputFieldNames.get(groupBySetOffset + i)));
    }

    return String.join(", ", aggregateStatements);
  }

}
