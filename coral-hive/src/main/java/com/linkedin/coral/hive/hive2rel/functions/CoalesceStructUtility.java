/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */

package com.linkedin.coral.hive.hive2rel.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;

import com.google.common.annotations.VisibleForTesting;


/**
 * A utility class to coalesce the {@link RelDataType} of struct between Trino's representation and
 * hive's extract_union UDF's representation on exploded union.
 *
 */
public class CoalesceStructUtility {

  private static final String NEW_PREFIX = "field";
  private static final String OLD_PREFIX = "tag_";

  private CoalesceStructUtility() {
    // Utility class, does nothing in constructor
  }


  /**
   * Converting a {@link RelDataType} that could potentially contains a Trino-format exploded-union(i.e. a struct
   * in a format of {tag, field0, field1, ..., fieldN} to represent a union after being deserialized)
   * into a exploded-union that complies with Hive's extract_union UDF format
   * (i.e. a struct as {tag_0, tag_1, ..., tag_{N}} to represent a union after being deserialized)
   *
   * For more information, check: https://github.com/trinodb/trino/pull/3483
   */
  @VisibleForTesting
  static RelDataType coalesce(RelDataType inputNode, RelDataTypeFactory typeFactory) {
    // Using type information implicitly carried in the object of RelDateType
    // instead of get down to SqlTypeName since the former contains enough categorization
    // of types to achieve the purpose for this method.

    if (inputNode.isStruct()) {
      List<String> fieldNames = inputNode.getFieldNames();
      return coalesceStruct(inputNode, isTrinoStructPattern(fieldNames), typeFactory);
    } else if (inputNode.getKeyType() != null) {
      return coalesceMap(inputNode, typeFactory);
    } else if (inputNode.getComponentType() != null) {
      return coalesceCollection(inputNode, typeFactory);
    } else {
      return inputNode;
    }
  }

  private static RelDataType coalesceStruct(RelDataType inputNode, boolean coalesceRequired,
      RelDataTypeFactory typeFactory) {
    List<String> originalNames = inputNode.getFieldNames();
    List<String> convertedNames =
        coalesceRequired ? originalNames.stream().map(x -> x.replaceFirst(NEW_PREFIX, OLD_PREFIX))
            .collect(Collectors.toList()).subList(1, originalNames.size()) : originalNames;
    List<RelDataType> originalTypes =
        inputNode.getFieldList().stream().map(RelDataTypeField::getType).collect(Collectors.toList());
    List<RelDataType> convertedTypes =
        new ArrayList<>(coalesceRequired ? originalTypes.size() - 1 : originalTypes.size());
    for (int i = coalesceRequired ? 1 : 0; i < originalTypes.size(); i++) {
      convertedTypes.add(coalesce(originalTypes.get(i), typeFactory));
    }

    return typeFactory.createStructType(convertedTypes, convertedNames);
  }

  private static RelDataType coalesceMap(RelDataType inputNode, RelDataTypeFactory typeFactory) {
    RelDataType coalescedKeyType = coalesce(inputNode.getKeyType(), typeFactory);
    RelDataType coalescedValueType = coalesce(inputNode.getValueType(), typeFactory);
    return typeFactory.createMapType(coalescedKeyType, coalescedValueType);
  }

  private static RelDataType coalesceCollection(RelDataType inputNode, RelDataTypeFactory typeFactory) {
    RelDataType coalescedComponentType = coalesce(inputNode.getComponentType(), typeFactory);
    return typeFactory.createArrayType(coalescedComponentType, -1);
  }

  /**
   * Trino's pattern has two elements:
   * - The first element has to be "tag".
   * - The following elements have to follow the naming pattern as "field{N}" where N
   * represents the position of this element in the struct, starting from 0.
   */
  private static boolean isTrinoStructPattern(List<String> fieldNames) {
    if (fieldNames.isEmpty() || !fieldNames.get(0).equals("tag")) {
      return false;
    } else {
      boolean flag = true;
      StringBuilder fieldNameRef = new StringBuilder("field");
      for (int i = 1; i < fieldNames.size(); i++) {
        int index = i - 1;
        fieldNameRef.append(index);
        if (!fieldNameRef.toString().equals(fieldNames.get(i))) {
          flag = false;
          break;
        }
        fieldNameRef.deleteCharAt(5);
      }
      return flag;
    }
  }
}
