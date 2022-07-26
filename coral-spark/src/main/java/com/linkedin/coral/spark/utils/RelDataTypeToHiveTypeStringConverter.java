/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;


/**
 * Transforms a RelDataType to a Hive type string such that it is parseable and semantically correct.
 * Some example of Hive type string for a RelDataType are as follows:
 *
 * Example 1:
 * RelDataType:
 *   struct(s1:integer,s2:varchar)
 * Hive Type String:
 *   struct&lt;s1:int,s2:string&gt;
 *
 * Example 2:
 * RelDataType:
 *   map(varchar,struct(s1:integer,s2:varchar))
 * Hive Type String:
 *   map&lt;string,struct&lt;s1:int,s2:string&gt;&gt;
 *
 * Example 3:
 * RelDataType:
 *   array(struct(s1:integer,s2:varchar))
 * Hive Type String:
 *   array&lt;struct&lt;s1:int,s2:string&gt;&gt;
 */
public class RelDataTypeToHiveTypeStringConverter {
  private RelDataTypeToHiveTypeStringConverter() {
  }

  /**
   * @param relDataType a given RelDataType
   * @return a syntactically and semantically correct Hive type string for relDataType
   */
  public static String convertRelDataType(RelDataType relDataType) {
    switch (relDataType.getSqlTypeName()) {
      case ROW:
        return buildStructDataTypeString((RelRecordType) relDataType);
      case ARRAY:
        return buildArrayDataTypeString((ArraySqlType) relDataType);
      case MAP:
        return buildMapDataTypeString((MapSqlType) relDataType);
      case INTEGER:
        return "int";
      case SMALLINT:
        return "smallint";
      case TINYINT:
        return "tinyint";
      case BIGINT:
        return "bigint";
      case DOUBLE:
        return "double";
      case REAL:
      case FLOAT:
        return "float";
      case BOOLEAN:
        return "boolean";
      case CHAR:
        return "char";
      case VARCHAR:
        return "string";
      case DATE:
        return "date";
      case TIME:
      case TIMESTAMP:
        return "timestamp";
      case BINARY:
      case VARBINARY:
      case OTHER:
        return "binary";
      case INTERVAL_DAY:
      case INTERVAL_DAY_HOUR:
      case INTERVAL_DAY_MINUTE:
      case INTERVAL_DAY_SECOND:
      case INTERVAL_HOUR:
      case INTERVAL_HOUR_MINUTE:
      case INTERVAL_HOUR_SECOND:
      case INTERVAL_MINUTE:
      case INTERVAL_MINUTE_SECOND:
      case INTERVAL_SECOND:
      case INTERVAL_MONTH:
      case INTERVAL_YEAR:
      case INTERVAL_YEAR_MONTH:
        return "interval";
      case NULL:
        return "null";
      default:
        throw new RuntimeException(String.format(
            "Unhandled RelDataType %s in Converter from RelDataType to Hive DataType", relDataType.getSqlTypeName()));
    }
  }

  /**
   * Build a Hive struct/row string with format:
   *   row<[field_name]:[field_type],...>
   * @param relRecordType a given struct RelDataType
   * @return a string that represents the given relRecordType
   */
  private static String buildStructDataTypeString(RelRecordType relRecordType) {
    List<String> structFieldStrings = new ArrayList<>();
    for (RelDataTypeField fieldRelDataType : relRecordType.getFieldList()) {
      structFieldStrings
          .add(String.format("%s:%s", fieldRelDataType.getName(), convertRelDataType(fieldRelDataType.getType())));
    }
    String subFieldsString = String.join(",", structFieldStrings);
    return String.format("struct<%s>", subFieldsString);
  }

  /**
   * Build a Hive array string with format:
   *   array<[field_type]>
   * @param arraySqlType a given array RelDataType
   * @return a string that represents the given arraySqlType
   */
  private static String buildArrayDataTypeString(ArraySqlType arraySqlType) {
    String elementDataTypeString = convertRelDataType(arraySqlType.getComponentType());
    return String.format("array<%s>", elementDataTypeString);
  }

  /**
   * Build a Hive map string with format:
   *   map<[key_type],[value_type]>
   * @param mapSqlType a given map RelDataType
   * @return a string that represents the given mapSqlType
   */
  private static String buildMapDataTypeString(MapSqlType mapSqlType) {
    String keyDataTypeString = convertRelDataType(mapSqlType.getKeyType());
    String valueDataTypeString = convertRelDataType(mapSqlType.getValueType());
    return String.format("map<%s,%s>", keyDataTypeString, valueDataTypeString);
  }
}
