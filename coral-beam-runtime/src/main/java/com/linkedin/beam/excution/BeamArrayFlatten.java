/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.excution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.values.KV;


public class BeamArrayFlatten {
  private BeamArrayFlatten() {
  }

  /**
   * Flattens a Beam KV pair of String and Avro record on a given of array columns in the Avro record. These columns
   * must have ARRAY schema type and the input values for those column must be either Java array or Collection.
   *
   * @param inputKV Input Beam KV pair of String and Avro record
   * @param flattenCols Columns to be flattened
   * @param outputSchema Schemas of ouput records
   * @return Collection of output records.
   */
  public static Collection<KV<String, GenericRecord>> flatten(KV<String, GenericRecord> inputKV,
      List<String> flattenCols, Schema outputSchema) {
    final List<KV<String, GenericRecord>> results = new ArrayList<>();
    final String key = inputKV.getKey();
    final Collection<GenericRecord> resultRecords = flatten(inputKV.getValue(), flattenCols, outputSchema);
    for (GenericRecord record : resultRecords) {
      results.add(KV.of(key, record));
    }
    return results;
  }

  /**
   * Flattens a record on a given of array columns. These columns must have ARRAY schema type
   * and the input values for those column must be either Java array or Collection.
   *
   * @param inputRecord Input Avro records
   * @param flattenCols Columns to be flattened
   * @param outputSchema Schemas of ouput records
   * @return Collection of output records.
   */
  public static Collection<GenericRecord> flatten(GenericRecord inputRecord, List<String> flattenCols,
      Schema outputSchema) {
    List<GenericRecord> results = new ArrayList<>();
    if (inputRecord == null) {
      return results;
    }
    Schema inputSchema = inputRecord.getSchema();
    Map<String, Object> partialRecord = new HashMap<>();
    for (Schema.Field field : inputSchema.getFields()) {
      partialRecord.put(field.name(), inputRecord.get(field.name()));
    }
    List<Map<String, Object>> partialResults = new ArrayList<>();
    partialResults.add(partialRecord);

    for (String flattenCol : flattenCols) {
      Schema flattenSchema = getFlattenElementSchema(inputSchema, flattenCol);
      if (flattenSchema == null) {
        throw new RuntimeException("Column " + flattenCol + " is not an array in records"
            + " with schema: " + inputSchema);
      }
      Object obj = inputRecord.get(flattenCol);
      List<Object> arrayValue = null;
      if (obj != null) {
        if (obj.getClass().isArray()) {
          arrayValue = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
          if (obj instanceof List) {
            arrayValue = (List<Object>) obj;
          } else {
            arrayValue = new ArrayList<>((Collection<Object>) obj);
          }
        } else {
          throw new RuntimeException("Invalid avro array value. Value type: " + obj.getClass().getName());
        }
      }
      partialResults = extendFlattenRecords(partialResults, flattenCol, arrayValue, flattenSchema);
    }

    for (Map<String, Object> outputRec : partialResults) {
      GenericRecord record = new GenericData.Record(outputSchema);
      for (Schema.Field field : outputSchema.getFields()) {
        record.put(field.name(), outputRec.get(field.name()));
      }
      results.add(record);
    }
    return results;
  }

  private static List<Map<String, Object>> extendFlattenRecords(List<Map<String, Object>> partialResults,
      String flattenCol, List<Object> flattenValue, Schema flattenSchema) {
    // Special optimization: no need to create new Map and List if array has only one value
    if (flattenValue == null || flattenValue.size() <= 1) {
      Object value = (flattenValue == null || flattenValue.isEmpty()) ? null : flattenValue.get(0);
      for (Map<String, Object> partialRecord : partialResults) {
        projectNestedFields(value, flattenCol, flattenSchema, partialRecord);
      }
      return partialResults;
    }

    List<Map<String, Object>> results = new ArrayList<>();
    for (Map<String, Object> partialRecord : partialResults) {
      for (Object value : flattenValue) {
        Map<String, Object> newRecord = new HashMap<>(partialRecord);
        projectNestedFields(value, flattenCol, flattenSchema, newRecord);
        results.add(newRecord);
      }
    }
    return results;
  }

  private static Schema getFlattenElementSchema(Schema recordSchema, String flattenCol) {
    if (recordSchema.getField(flattenCol) == null) {
      return null;
    }
    Schema colSchema = recordSchema.getField(flattenCol).schema();
    if (colSchema.getType() == Schema.Type.ARRAY) {
      return colSchema.getElementType();
    }
    if (colSchema.getType() == Schema.Type.UNION) {
      for (Schema typeSchema : colSchema.getTypes()) {
        if (typeSchema.getType() ==  Schema.Type.ARRAY) {
          return typeSchema.getElementType();
        }
      }
    }

    return null;
  }

  private static void projectNestedFields(Object nestedValue, String flattenCol, Schema flattenSchema,
      Map<String, Object> partialRecord) {
    if (flattenSchema.getType() == Schema.Type.RECORD) {
      for (Schema.Field field : flattenSchema.getFields()) {
        Object fieldValue = (nestedValue != null) ? ((GenericRecord) nestedValue).get(field.name()) : null;
        partialRecord.put(field.name(), fieldValue);
      }
    } else {
      partialRecord.put(flattenCol, nestedValue);
    }
  }
}
