/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;

import com.linkedin.coral.pig.rel2pig.rel.functions.Function;
import com.linkedin.coral.pig.rel2pig.rel.functions.PigUDF;


/**
 * PigRelUtils provides utilities to translate SQL relational operators represented as
 * Calcite RelNodes into Pig Latin.
 */
public class PigRelUtils {

  private PigRelUtils() {
  }

  /**
   * Returns a list-index-based map from Calcite indexed references to fully qualified field names for the given
   * RelDataType.
   *
   * For example, if we had a RelNode with RelDataType as follows:
   *   relRecordType = RelRecordType(a int, b int, c int)
   *
   * Calling getOutputFieldNames for relRecordType would return:
   *   getOutputFieldNames(relRecordType) -&gt; {"a", "b", "c"}
   *
   * Complex Types:
   *   For complex types (nested structs, maps, arrays), input references are not flattened.
   *   For example, suppose we had a data type as follows:
   *     RelRecordType(a int, b RelRecordType(b1 int), c int)
   *   Its output field-name map will be as follows:
   *     {"a", "b", "c"}
   *
   * @param relNode RelNode whose Pig fields are to be derived
   * @return Mapping from list-index field reference to its associated Pig alias.
   */
  public static List<String> getOutputFieldNames(RelNode relNode) {
    return relNode.getRowType().getFieldList().stream().map(field -> field.getKey().replace('$', 'x'))
        .collect(Collectors.toList());
  }

  /**
   * Returns a set of all function definitions necessary for the relNode and its children.
   *
   * A function definition is in the form of:
   *     "DEFINE [pigFunctionName] HiveUDF([hiveFunctionName])"
   *
   * @param relNode RelNode whose dependencies are to be derived
   * @return Set of all function definitions necessary for the relNode and its children.
   */
  public static Set<String> getAllFunctionDefinitions(RelNode relNode) {

    final List<RexNode> childExprs = new ArrayList<>();
    if (relNode instanceof LogicalProject) {
      final LogicalProject logicalProject = (LogicalProject) relNode;
      childExprs.addAll(logicalProject.getChildExps());
    } else if (relNode instanceof LogicalFilter) {
      final LogicalFilter logicalFilter = (LogicalFilter) relNode;
      childExprs.addAll(logicalFilter.getChildExps());
    } else if (relNode instanceof LogicalJoin) {
      final LogicalJoin logicalJoin = (LogicalJoin) relNode;
      childExprs.addAll(logicalJoin.getChildExps());
    }

    final Set<String> dependencyList = new HashSet<>();
    childExprs.forEach(rexNode -> dependencyList.addAll(getAllFunctionDefinitions(rexNode)));

    return dependencyList;
  }

  /**
   * Returns a set of all function definitions necessary for the rexNode and its children.
   *
   * A function definition is in the form of:
   *     "DEFINE [pigFunctionName] HiveUDF([hiveFunctionName])"
   *
   * @param rexNode RexNode whose dependencies are to be derived
   * @return Set of all function definitions necessary for the rexNode and its children.
   */
  private static Set<String> getAllFunctionDefinitions(RexNode rexNode) {
    if (!(rexNode instanceof RexCall)) {
      return Collections.emptySet();
    }

    final RexCall rexCall = (RexCall) rexNode;
    final Set<String> dependencyList = new HashSet<>();

    final Function function = CalcitePigOperatorMap.lookup(rexCall.getOperator().getName());
    if (function instanceof PigUDF) {
      final PigUDF pigUDF = (PigUDF) function;
      dependencyList.addAll(pigUDF.getFunctionDefinitions(rexCall, Collections.emptyList()));
    }

    rexCall.getOperands().forEach(childNode -> dependencyList.addAll(getAllFunctionDefinitions(childNode)));
    return dependencyList;
  }

}
