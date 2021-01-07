/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexNode;


/**
 * PigLogicalProject translates a Calcite LogicalProjection into Pig Latin.
 */
public class PigLogicalProject {

  private static final String LOGICAL_PROJECT_TEMPLATE = "%s = FOREACH %s GENERATE %s;";
  private static final String FIELD_TEMPLATE = "%s AS %s";

  private PigLogicalProject() {
  }

  /**
   * Translates a Calcite LogicalProject into Pig Latin
   * @param logicalProject The Calcite LogicalProject to be translated
   * @param outputRelation The variable that stores the projection output
   * @param inputRelation The variable that has stored the Pig relation to perform a projection over
   * @return The Pig Latin for the logicalProject in the form of:
   *           [outputRelation] = FOREACH [inputRelation] GENERATE [logicalProject.fields]
   */
  public static String getScript(LogicalProject logicalProject, String outputRelation, String inputRelation) {

    List<String> outputFieldNames = PigRelUtils.getOutputFieldNames(logicalProject);
    List<String> inputFieldNames = PigRelUtils.getOutputFieldNames(logicalProject.getInput());

    List<String> projectList = new ArrayList<>();
    for (int i = 0; i < logicalProject.getChildExps().size(); ++i) {
      RexNode rexNode = logicalProject.getChildExps().get(i);
      projectList.add(String.format(FIELD_TEMPLATE, PigRexUtils.convertRexNodeToPigExpression(rexNode, inputFieldNames),
          outputFieldNames.get(i)));
    }
    String projectListString = String.join(", ", projectList);

    return String.format(LOGICAL_PROJECT_TEMPLATE, outputRelation, inputRelation, projectListString);
  }
}
