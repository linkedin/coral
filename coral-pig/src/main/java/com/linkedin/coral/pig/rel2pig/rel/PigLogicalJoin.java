/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.commons.lang3.tuple.Pair;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexCallException;


/**
 * PigLogicalJoin translates a Calcite LogicalJoin into Pig Latin.
 */
public class PigLogicalJoin {

  private static final String LOGICAL_JOIN_TEMPLATE = "%s = JOIN %s, %s;";
  private static final String LOGICAL_PROJECT_TEMPLATE = "%s = FOREACH %s GENERATE %s;";
  private static final String JOIN_BRANCH_TEMPLATE = "%s BY (%s)";
  private static final String FULLY_QUALIFIED_FIELD_TEMPLATE = "%s::%s AS %s";

  private PigLogicalJoin() {

  }

  /**
   * Translates a Calcite LogicalJoin into Pig Latin
   *
   * @param logicalJoin The Calcite LogicalJoin to be translated
   * @param outputRelation The variable that stores the filtered output
   * @param leftInputRelation The variable that stores the Pig relation on the left of the join
   * @param rightInputRelation The variable that stores the Pig relation on the right of the join
   * @return The Pig Latin for the logicalJoin in the form of:
   *             [outputRelation] = JOIN [leftInputRelation] BY [fields] [join type], [rightInputRelation] BY [fields];
   *             [outputRelation] = FOREACH [outputRelation] GENERATE [fields in leftInputRelation], [fields in rightInputRelation];
   */
  public static String getScript(LogicalJoin logicalJoin, String outputRelation, String leftInputRelation,
      String rightInputRelation) {

    final List<String> leftInputFieldNames = PigRelUtils.getOutputFieldNames(logicalJoin.getLeft());
    final List<String> rightInputFieldNames = PigRelUtils.getOutputFieldNames(logicalJoin.getRight());

    final List<String> allInputFieldNames =
        Stream.concat(leftInputFieldNames.stream(), rightInputFieldNames.stream()).collect(Collectors.toList());

    final Pair<List<String>, List<String>> conditionFields =
        getConditionFields(logicalJoin.getCondition(), allInputFieldNames);

    final Pair<String, String> joinStatements =
        getJoinStatements(logicalJoin, leftInputRelation, rightInputRelation, conditionFields);

    final String joinStatement =
        String.format(LOGICAL_JOIN_TEMPLATE, outputRelation, joinStatements.getLeft(), joinStatements.getRight());

    final List<String> outputFieldNames = PigRelUtils.getOutputFieldNames(logicalJoin);
    final String unwrapStatement = getForEachStatement(outputRelation, outputFieldNames, outputRelation,
        leftInputRelation, leftInputFieldNames, rightInputRelation, rightInputFieldNames);

    return String.join("\n", joinStatement, unwrapStatement);
  }

  /**
   * Creates an ordered field list for the left and right branches of a JOIN.
   * Only EQUIJOINS with one or more condition are supported.
   *
   * For example, if we have a query that joins two tables, tableA and tableB on:
   *     tableA.fa = tableB.fb AND tableA.ga = tableB.gb AND tableA.ha = tableB.hb
   *
   * The output of this method will contain the following:
   *     Pair { {"fa", "ga", "ha"}, {"fb", "gb", "hb"} }
   *
   * An element at index 'n' corresponds the field that is being compared in the
   * left and right branches:
   *     Pair.getLeft.get(n) = Pair.getRight.get(n)
   *
   * @param rexNode Join condition represented as a rexNode
   * @param inputFieldNames List-index based mapping from Calcite index reference to field accessors of
   *                        the left and right inputs of the join.
   * @return Pair of Lists corresponding to the fields used in the join condition of the respective left and right
   *         branches of the JOIN.
   */
  private static Pair<List<String>, List<String>> getConditionFields(RexNode rexNode, List<String> inputFieldNames) {
    if (!(rexNode instanceof RexCall)) {
      throw new UnsupportedOperationException(
          String.format("Operator '%s' is not supported in a JOIN condition.", rexNode.getKind()));
    }
    final RexCall rexCall = (RexCall) rexNode;
    final List<String> leftConditionFields = new ArrayList<>();
    final List<String> rightConditionFields = new ArrayList<>();

    switch (rexCall.getOperator().getKind()) {
      case AND:
        for (RexNode childRexNode : rexCall.getOperands()) {
          final Pair<List<String>, List<String>> conditionFields = getConditionFields(childRexNode, inputFieldNames);
          leftConditionFields.addAll(conditionFields.getLeft());
          rightConditionFields.addAll(conditionFields.getRight());
        }
        break;
      case EQUALS:
        leftConditionFields
            .add(PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames));
        rightConditionFields
            .add(PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames));
        break;
      default:
        throw new UnsupportedRexCallException(String.format(
            "Invalid operator '%s'. Only EQUIJOINs with one or more AND conditions are supported. Found in query: %s",
            rexCall.getOperator().getName(), rexCall.toString()));
    }
    return Pair.of(leftConditionFields, rightConditionFields);
  }

  /**
   * Creates the JOIN conditions of the given Logical Join.
   *
   * For example:
   * Suppose we had a FULL OUTER JOIN on two relations:
   *   - RELATION_A
   *   - RELATION_B.
   *
   * Suppose they join on:
   *   - RELATION_A.a1 = RELATION_B.a2
   *   - RELATION_A.b1 = RELATION_B.c2
   *
   * Then, the output of this function will the JOIN statements of this logical join for each branch:
   *   Pair {
   *     left: 'RELATION_A BY (a1, b2) FULL OUTER',
   *     right: 'RELATION_B BY (a2, c2)'
   *   }
   *
   * @param logicalJoin The Calcite LogicalJoin to be translated
   * @param leftInputRelation The variable that stores the Pig relation on the left of the join
   * @param conditionFields Pair of Lists corresponding to the fields used in the join condition of the respective left and right
   *                        branches of the JOIN.
   * @return Pair of Strings corresponding to the JOIN conditions of the respective left and right
   *         branches of the JOIN.
   */
  private static Pair<String, String> getJoinStatements(LogicalJoin logicalJoin, String leftInputRelation,
      String rightInputRelation, Pair<List<String>, List<String>> conditionFields) {

    final String rightJoinStatement =
        String.format(JOIN_BRANCH_TEMPLATE, rightInputRelation, String.join(", ", conditionFields.getRight()));
    String leftJoinStatement =
        String.format(JOIN_BRANCH_TEMPLATE, leftInputRelation, String.join(", ", conditionFields.getLeft()));
    switch (logicalJoin.getJoinType()) {
      case FULL:
        leftJoinStatement = String.join(" ", leftJoinStatement, "FULL OUTER");
        break;
      case LEFT:
        leftJoinStatement = String.join(" ", leftJoinStatement, "LEFT OUTER");
        break;
      case RIGHT:
        leftJoinStatement = String.join(" ", leftJoinStatement, "RIGHT OUTER");
        break;
      case INNER:
        break;
      default:
        throw new UnsupportedRexCallException(String.format("JOIN type '%s' is not supported. Found in query: %s",
            logicalJoin.getJoinType().name(), logicalJoin.toString()));
    }
    return Pair.of(leftJoinStatement, rightJoinStatement);
  }

  /**
   * Generates Pig Latin to unwrap a relation that stores JOIN-ed inputs such that the
   * outputRelation projects the given outputFieldNames schema over the inputRelation.
   *
   * Example:
   * Suppose tableA (left) and tableB (right) both have a schema:
   *     (a int, b int)
   * The output of the join will have an output of:
   *     (a int, b int, a int, b int)
   * Where the first two columns pertain to tableA and the latter two columns pertain to tableB.
   *
   * This leads to ambiguous addressing, so Pig addresses it by using namespacing such as:
   *     (tableA::a int, tableA::b int, tableB::a int, tableB::b int)
   *
   * Now suppose we want to rename those fields into the following:
   *     (o1 int, o2 int, o3 int, o4 int)
   *
   * This function will generate Pig Latin to do the projection in the form of:
   *     [outputVar] = FOREACH [inputVar] GENERATE tableA::a as o1, tableA::b as o2, tableB::a as o3, tableB::b as o4 ;
   *
   * @param outputRelation The variable that stores the unwrapped output
   * @param outputFieldNames List-index based mapping from Calcite index reference to field names of
   *                         the output.
   * @param inputRelation The input variable that stores the join left and right relation
   * @param leftInputRelation The variable that stores the Pig relation on the left of the join
   * @param leftInputFieldNames List-index based mapping from Calcite index reference to field names of
   *                            the left input of the join.
   * @param rightInputRelation The variable that stores the Pig relation on the right of the join
   * @param rightInputFieldNames List-index based mapping from Calcite index reference to field names of
   *                             the right input of the join.
   * @return Pig Latin to unwrap the inputRelation with identifiers given by outputFieldNames into outputRelation.
   */
  private static String getForEachStatement(String outputRelation, List<String> outputFieldNames, String inputRelation,
      String leftInputRelation, List<String> leftInputFieldNames, String rightInputRelation,
      List<String> rightInputFieldNames) {

    final List<String> unwrappedFields = new ArrayList<>();

    int inputFieldOffset = 0;

    for (String leftInputField : leftInputFieldNames) {
      unwrappedFields.add(String.format(FULLY_QUALIFIED_FIELD_TEMPLATE, leftInputRelation, leftInputField,
          outputFieldNames.get(inputFieldOffset)));
      ++inputFieldOffset;
    }

    for (String rightInputField : rightInputFieldNames) {
      unwrappedFields.add(String.format(FULLY_QUALIFIED_FIELD_TEMPLATE, rightInputRelation, rightInputField,
          outputFieldNames.get(inputFieldOffset)));
      ++inputFieldOffset;
    }

    return String.format(LOGICAL_PROJECT_TEMPLATE, outputRelation, inputRelation, String.join(", ", unwrappedFields));
  }

}
