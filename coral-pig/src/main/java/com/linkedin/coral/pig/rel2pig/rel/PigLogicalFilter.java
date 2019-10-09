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
   * @param inputRelation The variable that stores the Pig relation after filtering
   * @return The Pig Latin for the logicalFilter in the form of:
   *           [outputRelation] = FILTER [inputRelation] BY [logicalFilter.expressions]
   */
  public static String getScript(LogicalFilter logicalFilter, String outputRelation, String inputRelation) {
    List<String> inputRelToPigFieldsMapping =
        PigRelUtils.convertInputColumnNameReferences(logicalFilter.getInput().getRowType());

    String conditionExpression =
        PigRexUtils.convertRexNodePigExpression(logicalFilter.getCondition(), inputRelToPigFieldsMapping);
    return String.format(LOGICAL_FILTER_TEMPLATE, outputRelation, inputRelation, conditionExpression);
  }
}
