package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;
import org.apache.calcite.rel.logical.LogicalFilter;


//TODO(ralam): Add comments and clean up code
public class PigLogicalFilter {

  private static final String LOGICAL_PROJECT_TEMPLATE = "%s = FILTER %s BY (%s);";

  private PigLogicalFilter() {

  }

  //TODO(ralam): Implement this method
  public static String getScript(LogicalFilter logicalFilter, String outputRelation, String inputRelation) {

    List<String> inputRefColumnNameList =
        PigRelUtils.convertInputColumnNameReferences(logicalFilter.getInput().getRowType());

    // TODO(ralam): Implement LogicalFilter in Pig. Throw an exception in the meantime.
    throw new UnsupportedOperationException("LogicalFilters are not supported.");
  }
}
