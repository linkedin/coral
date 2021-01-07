/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;

import org.apache.calcite.rel.logical.LogicalUnion;


/**
 * PigLogicalUnion translates a Calcite LogicalUnion into Pig Latin.
 */
public class PigLogicalUnion {

  private static final String LOGICAL_UNION_TEMPLATE = "%s = UNION %s;";

  private PigLogicalUnion() {

  }

  /**
   * Translates a Calcite LogicalUnion into Pig Latin
   *
   * @param logicalUnion The Calcite LogicalUnion to be translated
   * @param outputRelation The variable that stores the union output
   * @param inputRelations The list of relations that are part of the union
   * @return The PigLatin for the logicalUnion over 'n' tables/relations in the form of:
   *           [outputRelation] = UNION [inputRelations].get(0), ... [inputRelations].get(n-1);
   */
  public static String getScript(LogicalUnion logicalUnion, String outputRelation, List<String> inputRelations) {
    if (inputRelations.size() < 2) {
      throw new RuntimeException(
          String.format("LogicalUnion was performed with fewer than two tables in query: %s", logicalUnion.toString()));
    }

    return String.format(LOGICAL_UNION_TEMPLATE, outputRelation, String.join(", ", inputRelations));
  }
}
