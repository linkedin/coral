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

  private PigLogicalProject() {
  }

  /**
   * Translates a Calcite LogicalProject into Pig Latin
   * @param logicalProject The Calcite LogicalProject to be translated
   * @param outputRelation The variable that stores the projection output
   * @param inputRelation The variable that has stores the Pig relation after the projection
   * @return The Pig Latin for the logicalProject in the form of:
   *           [outputRelation] = FOREACH [inputRelation] GENERATE [logicalProject.fields]
   */
  public static String getScript(LogicalProject logicalProject, String outputRelation, String inputRelation) {
    List<String> inputRelToPigFieldsMapping =
        PigRelUtils.convertInputColumnNameReferences(logicalProject.getInput().getRowType());

    List<String> projectList = new ArrayList<>();
    for (RexNode rexNode : logicalProject.getChildExps()) {

      projectList.add(PigRexUtils.convertRexNodePigExpression(rexNode, inputRelToPigFieldsMapping));
    }
    String projectListString = String.join(", ", projectList);

    return String.format(LOGICAL_PROJECT_TEMPLATE, outputRelation, inputRelation, projectListString);
  }
}
