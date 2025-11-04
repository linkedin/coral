/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;


/**
 * Lowercase field names for Avro schema
 */
public class ToLowercaseSchemaVisitor extends AvroSchemaVisitor<Schema> {
  public static Schema visit(Schema schema) {
    return AvroSchemaVisitor.visit(schema, new ToLowercaseSchemaVisitor());
  }

  @Override
  public Schema record(Schema record, List<String> names, List<Schema> fields) {
    Schema lowercasedSchema =
        Schema.createRecord(record.getName().toLowerCase(), record.getDoc(), record.getNamespace(), record.isError());

    List<Schema.Field> lowercasedFields = Lists.newArrayListWithExpectedSize(fields.size());
    for (int i = 0; i < fields.size(); i++) {
      lowercasedFields.add(lowercaseField(record.getFields().get(i), fields.get(i)));
    }

    lowercasedSchema.setFields(lowercasedFields);
    SchemaUtilities.replicateSchemaProps(record, lowercasedSchema);

    return lowercasedSchema;
  }

  @Override
  public Schema union(Schema union, List<Schema> options) {
    List<Schema> unionOptions = Lists.newArrayListWithExpectedSize(options.size());
    for (Schema option : options) {
      unionOptions.add(option);
    }

    return Schema.createUnion(unionOptions);
  }

  @Override
  public Schema array(Schema array, Schema element) {
    return Schema.createArray(element);
  }

  @Override
  public Schema map(Schema map, Schema value) {
    return Schema.createMap(value);
  }

  @Override
  public Schema primitive(Schema primitive) {
    return primitive;
  }

  /**
   * Lowercases a field, including lowercasing any field names within its default value.
   * @param field The original field
   * @param schema The lowercased schema for this field
   * @return A new field with lowercased name and lowercased default value
   */
  private Schema.Field lowercaseField(Schema.Field field, Schema schema) {
    Object originalDefaultValue = SchemaUtilities.defaultValue(field);
    Object lowercasedDefaultValue = lowercaseDefaultValue(originalDefaultValue, schema);

    Schema.Field lowercasedField = AvroCompatibilityHelper.createSchemaField(field.name().toLowerCase(), schema,
        field.doc(), lowercasedDefaultValue, field.order());

    SchemaUtilities.replicateFieldProps(field, lowercasedField);

    return lowercasedField;
  }

  /**
   * Recursively lowercases field names within default values based on the schema structure.
   * This handles complex types like records, maps, and arrays where field names appear in default values.
   * 
   * @param defaultValue The original default value (can be null, primitive, Map, List, etc.)
   * @param schema The schema that describes the structure of this default value
   * @return The default value with all field names lowercased
   */
  @SuppressWarnings("unchecked")
  private Object lowercaseDefaultValue(Object defaultValue, Schema schema) {
    if (defaultValue == null) {
      return null;
    }

    Schema actualSchema = schema;
    
    // Handle union types - get the actual schema based on the default value type
    if (schema.getType() == Schema.Type.UNION) {
      // For unions, the default value corresponds to the first type in the union
      actualSchema = schema.getTypes().get(0);
    }

    switch (actualSchema.getType()) {
      case RECORD:
        // For records, the default value can be either a Map or GenericData.Record
        if (defaultValue instanceof GenericData.Record) {
          GenericData.Record record = (GenericData.Record) defaultValue;
          return lowercaseRecordDefaultValue(actualSchema, lowercasedFieldName -> {
            // Find the matching field in the original record's schema (case-insensitive)
            Schema.Field originalField = record.getSchema().getField(lowercasedFieldName);
            if (originalField == null) {
              for (Schema.Field f : record.getSchema().getFields()) {
                if (f.name().equalsIgnoreCase(lowercasedFieldName)) {
                  originalField = f;
                  break;
                }
              }
            }
            return originalField != null ? record.get(originalField.pos()) : null;
          });
        } else if (defaultValue instanceof Map) {
          Map<?, ?> recordMap = (Map<?, ?>) defaultValue;
          return lowercaseRecordDefaultValue(actualSchema, lowercasedFieldName -> {
            // Find the matching key in the original map (case-insensitive)
            String matchingKey = findMatchingKeyForLowercased(recordMap, lowercasedFieldName);
            return matchingKey != null ? recordMap.get(matchingKey) : null;
          });
        }
        // If neither Map nor GenericData.Record, return as-is
        return defaultValue;

      case MAP:
        // For maps, lowercase the keys and recursively process values
        if (defaultValue instanceof Map) {
          Map<?, ?> mapValue = (Map<?, ?>) defaultValue; // Use wildcards to handle Utf8 keys
          Map<String, Object> lowercasedMap = new LinkedHashMap<>();
          Schema valueSchema = actualSchema.getValueType();
          
          for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
            String originalKey = entry.getKey().toString(); // Handle both String and Utf8
            String lowercasedKey = originalKey.toLowerCase();
            Object lowercasedValue = lowercaseDefaultValue(entry.getValue(), valueSchema);
            lowercasedMap.put(lowercasedKey, lowercasedValue);
          }
          return lowercasedMap;
        }
        return defaultValue;

      case ARRAY:
        // For arrays, recursively process each element
        if (defaultValue instanceof List) {
          List<Object> arrayValue = (List<Object>) defaultValue;
          Schema elementSchema = actualSchema.getElementType();
          
          return arrayValue.stream()
              .map(element -> lowercaseDefaultValue(element, elementSchema))
              .collect(Collectors.toList());
        }
        return defaultValue;

      case NULL:
      case BOOLEAN:
      case INT:
      case LONG:
      case FLOAT:
      case DOUBLE:
      case BYTES:
      case STRING:
      case ENUM:
      case FIXED:
      default:
        // Primitive types and others: return as-is
        return defaultValue;
    }
  }

  /**
   * Helper method that extracts the common logic for lowercasing record default values.
   * This handles both GenericData.Record and Map-based default values.
   * 
   * @param actualSchema The lowercased schema for the record
   * @param valueExtractor Function that retrieves the original field value given a lowercased field name
   * @return A Map with lowercased field names and recursively lowercased values
   */
  private Map<String, Object> lowercaseRecordDefaultValue(Schema actualSchema,
      Function<String, Object> valueExtractor) {
    Map<String, Object> lowercasedRecordMap = new LinkedHashMap<>();
    
    // Iterate through the lowercased schema fields
    for (Schema.Field field : actualSchema.getFields()) {
      String lowercasedFieldName = field.name();
      Object fieldValue = valueExtractor.apply(lowercasedFieldName);
      
      if (fieldValue != null) {
        Object lowercasedFieldValue = lowercaseDefaultValue(fieldValue, field.schema());
        lowercasedRecordMap.put(lowercasedFieldName, lowercasedFieldValue);
      }
    }
    
    return lowercasedRecordMap;
  }

  /**
   * Finds a key in the original default value map that matches the lowercased field name.
   * This is needed because the original default value may have field names in mixed case.
   * 
   * @param map The map containing the original default value
   * @param lowercasedFieldName The lowercased field name from the transformed schema
   * @return The matching key from the original map, or null if not found
   */
  private String findMatchingKeyForLowercased(Map<?, ?> map, String lowercasedFieldName) {
    // Try case-insensitive match to find the original key
    for (Object keyObj : map.keySet()) {
      String key = keyObj.toString(); // Handle both String and Utf8
      if (key.equalsIgnoreCase(lowercasedFieldName)) {
        return key;
      }
    }
    
    return null;
  }
}
