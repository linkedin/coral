/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;


/**
 * RelDataTypeToSparkDataTypeStringConverter converts a RelDataType to a Spark DataType JSON schema string.
 * The schema JSON string is parseable by Spark.DataType and is fully-qualified.
 *
 * Some example Spark.DataType schema JSON strings for a RelDataType are as follows:
 *
 * Example 1:
 * RelDataType:
 *   struct(s1: integer, s2: varchar)
 * Spark.DataType String:
 *   {
 *     'type': 'struct',
 *     'fields': [
 *       {
 *         'name': 's1',
 *         'type': 'int',
 *         'nullable': true,
 *         'metadata': {}
 *       },
 *       {
 *         'name': 's1',
 *         'type': 'int',
 *         'nullable': true,
 *         'metadata': {}
 *       }
 *     ]
 *   }
 *
 * Example 2:
 * RelDataType:
 *   map(varchar, struct(s1: integer, s2: varchar))
 * Spark.DataType String:
 *   {
 *     'type': 'map',
 *     'keyType': 'string',
 *     'valueType': {
 *       'type': 'struct',
 *       'fields': [
 *          {
 *           'name': 's1',
 *           'type': 'int',
 *           'nullable': true,
 *           'metadata': {}
 *         },
 *         {
 *           'name': 's1',
 *           'type': 'int',
 *           'nullable': true,
 *           'metadata': {}
 *         }
 *       ]
 *     },
 *     'valueContainsNull': true
 *   }
 *
 * Example 3:
 * RelDataType:
 *   array(struct(s1: integer, s2: varchar))
 * Spark.DataType String:
 *   {
 *     'type': 'array',
 *     'elementType': {
 *       'type': 'struct',
 *       'fields': [
 *          {
 *           'name': 's1',
 *           'type': 'int',
 *           'nullable': true,
 *           'metadata': {}
 *         },
 *         {
 *           'name': 's1',
 *           'type': 'int',
 *           'nullable': true,
 *           'metadata': {}
 *         }
 *       ]
 *     },
 *     'containsNull': true
 *   }
 *
 */
public class RelDataTypeToSparkDataTypeStringConverter {

  private RelDataTypeToSparkDataTypeStringConverter() {
  }

  private static final String TYPE = "type";
  private static final String KEY_TYPE = "keyType";
  private static final String VALUE_TYPE = "valueType";
  private static final String FIELDS = "fields";
  private static final String ELEMENT_TYPE = "elementType";
  private static final String NAME = "name";
  private static final String NULLABLE = "nullable";
  private static final String METADATA = "metadata";
  private static final String VALUE_CONTAINS_NULL = "valueContainsNull";
  private static final String CONTAINS_NULL = "containsNull";

  /**
   * Converts a RelDataType to a Spark.DataType schema JSON string
   * @param relDataType a given RelDataType object
   * @return a Spark.DataType schema JSON string
   */
  public static String convertRelDataType(RelDataType relDataType) {
    JsonElement jsonEle = createRelDataTypeJsonEle(relDataType);
    return jsonEle.toString();
  }

  /**
   * Converts a RelDataType to a JSON object representing its schema in Spark.DataType
   * @param relDataType a given RelDataType object
   * @return a Spark.DataType schema JSON string
   */
  private static JsonElement createRelDataTypeJsonEle(RelDataType relDataType) {

    switch (relDataType.getSqlTypeName()) {
      case ROW:
        return getRowRelDataTypeJsonObj((RelRecordType) relDataType);
      case ARRAY:
        return getArrayRelDataTypeJsonObj((ArraySqlType) relDataType);
      case MAP:
        return getMapRelDataTypeJsonObj((MapSqlType) relDataType);
      case INTEGER:
        return new JsonPrimitive("integer");
      case SMALLINT:
      case TINYINT:
        return new JsonPrimitive("short");
      case BIGINT:
        return new JsonPrimitive("long");
      case DOUBLE:
        return new JsonPrimitive("double");
      case FLOAT:
        return new JsonPrimitive("float");
      case BOOLEAN:
        return new JsonPrimitive("boolean");
      case CHAR:
      case VARCHAR:
        return new JsonPrimitive("string");
      case DATE:
        return new JsonPrimitive("date");
      case TIMESTAMP:
      case TIME:
        return new JsonPrimitive("timestamp");
      case INTERVAL_DAY:
      case INTERVAL_DAY_HOUR:
      case INTERVAL_DAY_MINUTE:
      case INTERVAL_DAY_SECOND:
      case INTERVAL_HOUR:
      case INTERVAL_HOUR_MINUTE:
      case INTERVAL_HOUR_SECOND:
      case INTERVAL_MINUTE:
      case INTERVAL_MINUTE_SECOND:
      case INTERVAL_MONTH:
      case INTERVAL_SECOND:
      case INTERVAL_YEAR:
      case INTERVAL_YEAR_MONTH:
        return new JsonPrimitive("calendarinterval");
      case NULL:
        return new JsonPrimitive("null");
      case BINARY:
      case VARBINARY:
      case OTHER:
        return new JsonPrimitive("binary");
      default:
        throw new RuntimeException(String.format(
            "Unhandled RelDataType %s in Converter from RelDataType to Spark DataType", relDataType.getSqlTypeName()));
    }
  }

  /**
   * Converts a RelRecordType(struct) to a Spark.DataType struct schema
   * @param relRecordType a given struct RelRecordType object
   * @return a Spark.DataType schema JSON string
   */
  private static JsonElement getRowRelDataTypeJsonObj(RelRecordType relRecordType) {
    JsonObject sparkDataTypeJsonObject = new JsonObject();
    sparkDataTypeJsonObject.addProperty(TYPE, "struct");
    JsonArray fields = new JsonArray();
    for (RelDataTypeField fieldRelDataType : relRecordType.getFieldList()) {
      JsonObject field = new JsonObject();
      field.add(TYPE, createRelDataTypeJsonEle(fieldRelDataType.getType()));
      field.addProperty(NAME, fieldRelDataType.getName());
      field.addProperty(NULLABLE, true);
      field.add(METADATA, new JsonObject());
      fields.add(field);
    }
    sparkDataTypeJsonObject.add(FIELDS, fields);
    return sparkDataTypeJsonObject;
  }

  /**
   * Converts a ArraySqlType(array) to a Spark.DataType struct schema
   * @param arraySqlType a given struct ArraySqlType object
   * @return a Spark.DataType schema JSON string
   */
  private static JsonElement getArrayRelDataTypeJsonObj(ArraySqlType arraySqlType) {
    JsonObject sparkDataTypeJsonObject = new JsonObject();
    sparkDataTypeJsonObject.addProperty(TYPE, "array");
    sparkDataTypeJsonObject.addProperty(CONTAINS_NULL, true);
    sparkDataTypeJsonObject.add(ELEMENT_TYPE, createRelDataTypeJsonEle(arraySqlType.getComponentType()));
    return sparkDataTypeJsonObject;
  }

  /**
   * Converts a MapSqlType(map) to a Spark.DataType struct schema
   * @param mapSqlType a given struct MapSqlType object
   * @return a Spark.DataType schema JSON string
   */
  private static JsonElement getMapRelDataTypeJsonObj(MapSqlType mapSqlType) {
    JsonObject sparkDataTypeJsonObject = new JsonObject();
    sparkDataTypeJsonObject.addProperty(TYPE, "map");
    sparkDataTypeJsonObject.addProperty(VALUE_CONTAINS_NULL, true);
    sparkDataTypeJsonObject.add(KEY_TYPE, createRelDataTypeJsonEle(mapSqlType.getKeyType()));
    sparkDataTypeJsonObject.add(VALUE_TYPE, createRelDataTypeJsonEle(mapSqlType.getValueType()));
    return sparkDataTypeJsonObject;
  }
}
