/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;

import org.apache.calcite.rel.logical.LogicalFilter;


/**
 * PigLogicalProject translates a Calcite LogicalFilter into Pig Latin.
 */
public class PigLogicalFilter {

  private static final String LOGICAL_FILTER_TEMPLATE = "%s = FILTER %s BY %s;";

  private PigLogicalFilter() {

  }

  /**
   * Translates a Calcite LogicalFilter into Pig Latin
   * @param logicalFilter The Calcite LogicalFilter to be translated
   * @param outputRelation The variable that stores the filtered output
   * @param inputRelation The variable that has stored the Pig relation to be filtered
   * @return The Pig Latin for the logicalFilter in the form of:
   *           [outputRelation] = FILTER [inputRelation] BY [logicalFilter.expressions]
   */
  public static String getScript(LogicalFilter logicalFilter, String outputRelation, String inputRelation) {
    List<String> inputFieldNames = PigRelUtils.getOutputFieldNames(logicalFilter.getInput());
    String conditionExpression =
        PigRexUtils.convertRexNodeToPigExpression(logicalFilter.getCondition(), inputFieldNames);
    return String.format(LOGICAL_FILTER_TEMPLATE, outputRelation, inputRelation, conditionExpression);
  }
}
