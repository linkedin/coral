/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.rel.RelNode;

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
   *   getOutputFieldNames(relRecordType) -> {"a", "b", "c"}
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
    return relNode.getRowType().getFieldList().stream()
        .map(field -> field.getKey().replace('$', 'x'))
        .collect(Collectors.toList());
  }
}