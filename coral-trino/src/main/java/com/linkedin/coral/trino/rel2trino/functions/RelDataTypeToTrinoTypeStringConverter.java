/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.MapSqlType;


/**
 * Transforms a RelDataType to a Trino type string such that it is parseable and semantically correct.
 * The output of the utility's entry-point, buildTrinoTypeString, can be used as a castable type in Trino.
 * If a column, colA, has a RelDataType, relDataTypeA, with a Trino type string, trinoTypeStringA = buildStructDataTypeString(relDataTypeA),
 * then the following operation is syntactically and semantically correct in Trino: CAST(colA as trinoTypeStringA)
 */
class RelDataTypeToTrinoTypeStringConverter {
  private RelDataTypeToTrinoTypeStringConverter() {
  }

  /**
   * Creates a Trino type string for a given RelDataType
   * Some examples of Trino type strings produced for a RelDataType are as follows:
   *
   *   Example 1:
   *     - RelDataType:
   *       - map(string, string)
   *     - Trino type String
   *       - map(varchar, varchar)
   *
   *   Example 2:
   *     - RelDataType:
   *       - array(string)
   *     - Trino type string:
   *       - array(varchar)
   *
   *   Example 3:
   *     - RelDataType:
   *       - struct(a:string, b:int)
   *     - Trino type string:
   *       - row(a varchar, b integer)
   *
   *   Example 4:
   *     - RelDataType:
   *       - array(struct(a:string, b:int))
   *     - Trino type string:
   *       - array(row(a varchar, b integer))
   *
   *   Example 5:
   *     - RelDataType:
   *       - map(string, struct(a:string, b:int))
   *     - Trino type string:
   *       - map(varchar, row(a varchar, b integer))
   *
   *   Example 6:
   *     - RelDataType:
   *       - map(array(struct(a:string, b:struct(c:int))))
   *     - Trino type string:
   *       - map(array(row(a varchar, b row(c integer))))
   *
   * @param relDataType a given RelDataType
   * @return a syntactically and semantically correct Trino type string for relDataType
   */
  public static String buildTrinoTypeString(RelDataType relDataType) {
    switch (relDataType.getSqlTypeName()) {
      case ROW:
        return buildStructDataTypeString((RelRecordType) relDataType);
      case ARRAY:
        return buildArrayDataTypeString((ArraySqlType) relDataType);
      case MAP:
        return buildMapDataTypeString((MapSqlType) relDataType);
      case INTEGER:
        return "integer";
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
        return "real";
      case BOOLEAN:
        return "boolean";
      case CHAR:
        return "char";
      case VARCHAR:
        return "varchar";
      case DATE:
        return "date";
      case TIMESTAMP:
        return "timestamp";
      case TIME:
        return "time";
      case BINARY:
      case VARBINARY:
      case OTHER:
        return "varbinary";
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
      case NULL:
      default:
        throw new RuntimeException(String.format(
            "Unhandled RelDataType %s in Converter from RelDataType to Spark DataType", relDataType.getSqlTypeName()));
    }
  }

  /**
   * Build a Trino struct/row string with format:
   *   row([field_name] [field_type], ...)
   * @param relRecordType a given struct RelDataType
   * @return a string that represents the given relRecordType
   */
  private static String buildStructDataTypeString(RelRecordType relRecordType) {
    List<String> structFieldStrings = new ArrayList<>();
    for (RelDataTypeField relDataTypeField : relRecordType.getFieldList()) {
      structFieldStrings
          .add(String.format("%s %s", TrinoKeywordsConverter.quoteWordIfNotQuoted(relDataTypeField.getName()),
              buildTrinoTypeString(relDataTypeField.getType())));
    }
    String subFieldsString = String.join(", ", structFieldStrings);
    return String.format("row(%s)", subFieldsString);
  }

  /**
   * Build a Trino array string with format:
   *   array([field_type])
   * @param arraySqlType a given array RelDataType
   * @return a string that represents the given arraySqlType
   */
  private static String buildArrayDataTypeString(ArraySqlType arraySqlType) {
    String elementDataTypeString = buildTrinoTypeString(arraySqlType.getComponentType());
    return String.format("array(%s)", elementDataTypeString);
  }

  /**
   * Build a Trino map string with format:
   *   map([key_type], [value_type])
   * @param mapSqlType a given map RelDataType
   * @return a string that represents the given mapSqlType
   */
  private static String buildMapDataTypeString(MapSqlType mapSqlType) {
    String keyDataTypeString = buildTrinoTypeString(mapSqlType.getKeyType());
    String valueDataTypeString = buildTrinoTypeString(mapSqlType.getValueType());
    return String.format("map(%s, %s)", keyDataTypeString, valueDataTypeString);
  }
}
