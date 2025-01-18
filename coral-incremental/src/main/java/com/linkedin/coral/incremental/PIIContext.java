/**
 * Copyright 2022-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PIIContext {
  private final List<String> inputPIIFields;
  private final List<String> outputPIIFields;
  private final ConcurrentHashMap<String, String> fieldToFullyQualifiedMap;

  public PIIContext(List<String> inputPIIFields) {
    this.inputPIIFields = inputPIIFields.stream().map(String::toLowerCase).collect(Collectors.toList());
    this.outputPIIFields = new ArrayList<>();
    this.fieldToFullyQualifiedMap = new ConcurrentHashMap<>();
  }

  public List<String> getInputPIIFields() {
    return inputPIIFields;
  }

  public List<String> getOutputPIIFields() {
    return outputPIIFields;
  }

  public void addOutputPIIField(String field) {
    outputPIIFields.add(field);
  }

  public void addFieldToFullyQualifiedMap(String field, String fullyQualifiedField) {

    // Check if the field already exists with the same fully qualified value
    if (fieldToFullyQualifiedMap.containsKey(field)
        && fieldToFullyQualifiedMap.get(field).equals(fullyQualifiedField)) {
      return;
    }
    // If the field exists but with a different fully qualified field, resolve the conflict
    if (fieldToFullyQualifiedMap.containsKey(field)) {
      String[] fieldParts = field.split("\\.");
      for (int i = 0; i < 10; i++) { // Infinite loop, explicitly broken when a unique alias is found
        final int currentIndex = i; // Effectively final variable for the lambda
        String newField = IntStream.range(0, fieldParts.length)
            .mapToObj(
                j -> j == fieldParts.length - 1 && fieldParts.length > 1 ? fieldParts[j] : fieldParts[j] + currentIndex)
            .collect(Collectors.joining("."));
        // If the generated alias is not already in the map, add it and return
        if (!fieldToFullyQualifiedMap.containsKey(newField)) {
          fieldToFullyQualifiedMap.put(newField, fullyQualifiedField);
          return;
        }
      }
      throw new IllegalStateException("Could not resolve the conflict for field: " + field);
    }
    // Add the field to the map if it doesn't already exist
    fieldToFullyQualifiedMap.put(field, fullyQualifiedField);
  }

  public Map<String, String> getFieldToFullyQualifiedMap() {
    return fieldToFullyQualifiedMap;
  }

}
