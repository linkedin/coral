package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexNode;


//TODO(ralam): Add comments and clean up code
public class PigLogicalProject {

  private static final String LOGICAL_PROJECT_TEMPLATE = "%s = FOREACH %s GENERATE %s ;";

  private PigLogicalProject() {
  }

  public static String getScript(LogicalProject logicalProject, String outputRelation, String inputRelation) {
    List<String> inputRefColumnNameList =
        PigRelUtils.convertInputColumnNameReferences(logicalProject.getInput().getRowType());

    List<String> projectList = new ArrayList<>();
    for (RexNode rexNode : logicalProject.getChildExps()) {

      projectList.add(PigRexUtils.convertRexNodePigExpression(rexNode, inputRefColumnNameList));
    }
    String projectListString = String.join(" , ", projectList);

    return String.format(LOGICAL_PROJECT_TEMPLATE, outputRelation, inputRelation, projectListString);
  }
}
