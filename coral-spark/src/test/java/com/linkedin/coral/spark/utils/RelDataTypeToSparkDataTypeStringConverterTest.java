/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class RelDataTypeToSparkDataTypeStringConverterTest {
  @Test
  public void testPrimitiveRelDataType() {
    Multimap<String, SqlTypeName> sparkDataTypeStringToSqlTypeNameMap =
        new ImmutableMultimap.Builder<String, SqlTypeName>().put("\"string\"", SqlTypeName.VARCHAR)
            .put("\"integer\"", SqlTypeName.INTEGER).put("\"short\"", SqlTypeName.SMALLINT)
            .put("\"long\"", SqlTypeName.BIGINT).put("\"double\"", SqlTypeName.DOUBLE)
            .put("\"float\"", SqlTypeName.FLOAT).put("\"boolean\"", SqlTypeName.BOOLEAN)
            .put("\"date\"", SqlTypeName.DATE).put("\"timestamp\"", SqlTypeName.TIMESTAMP)
            .put("\"calendarinterval\"", SqlTypeName.INTERVAL_DAY).put("\"binary\"", SqlTypeName.BINARY)
            .put("\"null\"", SqlTypeName.NULL).build();

    for (Map.Entry<String, SqlTypeName> entry : sparkDataTypeStringToSqlTypeNameMap.entries()) {
      String expectedSparkDataTypeSchemaString = entry.getKey();
      SqlTypeName sqlTypeName = entry.getValue();
      RelDataType relDataType = new BasicSqlType(RelDataTypeSystem.DEFAULT, sqlTypeName);
      String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(relDataType);
      assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
    }
  }

  @Test
  public void testStructRelDataType() {
    String expectedSparkDataTypeSchemaString = "{" + "\"type\":\"struct\"," + "\"fields\":["
        + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
        + "{\"type\":\"integer\",\"name\":\"int\",\"nullable\":true,\"metadata\":{}}" + "]}";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);
    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(relRecordType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testArrayRelDataType() {
    String expectedSparkDataTypeSchemaString = "{\"type\":\"array\",\"containsNull\":true,\"elementType\":\"integer\"}";

    ArraySqlType arraySqlType =
        new ArraySqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);

    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(arraySqlType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testMapRelDataType() {
    String expectedSparkDataTypeSchemaString =
        "{\"type\":\"map\",\"valueContainsNull\":true,\"keyType\":\"integer\",\"valueType\":\"integer\"}";

    MapSqlType mapSqlType = new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER),
        new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);

    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testNestedStructRelDataType() {
    String expectedSparkDataTypeSchemaString = "{" + "\"type\":\"struct\"," + "\"fields\":["
        + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
        + "{\"type\":{\"type\":\"struct\",\"fields\":["
        + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
        + "{\"type\":\"integer\",\"name\":\"int\",\"nullable\":true,\"metadata\":{}}]},"
        + "\"name\":\"struct\",\"nullable\":true,\"metadata\":{}}]}";

    List<RelDataTypeField> nestedFields = new ArrayList<>();
    nestedFields
        .add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    nestedFields
        .add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType nestedRelRecordType = new RelRecordType(nestedFields);

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("struct", 0, nestedRelRecordType));

    RelRecordType relRecordType = new RelRecordType(fields);
    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(relRecordType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testMapWithStructValueRelDataType() {
    String expectedSparkDataTypeSchemaString = "{" + "\"type\":\"map\"," + "\"valueContainsNull\":true,"
        + "\"keyType\":\"integer\"," + "\"valueType\":" + "{\"type\":\"struct\",\"fields\":["
        + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
        + "{\"type\":\"integer\",\"name\":\"int\",\"nullable\":true,\"metadata\":{}}]}}";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    MapSqlType mapSqlType =
        new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), relRecordType, true);

    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testArrayWithStructEleRelDataType() {
    String expectedSparkDataTypeSchemaString = "{" + "\"type\":\"array\"," + "\"containsNull\":true,"
        + "\"elementType\":" + "{\"type\":\"struct\",\"fields\":["
        + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
        + "{\"type\":\"integer\",\"name\":\"int\",\"nullable\":true,\"metadata\":{}}]}}";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    ArraySqlType arraySqlType = new ArraySqlType(relRecordType, true);

    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(arraySqlType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

  @Test
  public void testComplexRelDataType() {
    String expectedSparkDataTypeSchemaString =
        "{" + "\"type\":\"map\"," + "\"valueContainsNull\":true," + "\"keyType\":\"integer\"," + "\"valueType\":"
            + "{\"type\":\"array\",\"containsNull\":true,\"elementType\":" + "{\"type\":\"struct\",\"fields\":["
            + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
            + "{\"type\":{\"type\":\"struct\",\"fields\":["
            + "{\"type\":\"string\",\"name\":\"str\",\"nullable\":true,\"metadata\":{}},"
            + "{\"type\":\"integer\",\"name\":\"int\",\"nullable\":true,\"metadata\":{}}]},"
            + "\"name\":\"struct\",\"nullable\":true,\"metadata\":{}}]}}}";

    List<RelDataTypeField> nestedFields = new ArrayList<>();
    nestedFields
        .add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    nestedFields
        .add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType nestedRelRecordType = new RelRecordType(nestedFields);

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("struct", 0, nestedRelRecordType));

    RelRecordType relRecordType = new RelRecordType(fields);

    ArraySqlType arraySqlType = new ArraySqlType(relRecordType, true);

    MapSqlType mapSqlType =
        new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), arraySqlType, true);

    String sparkDataTypeSchemaString = RelDataTypeToSparkDataTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(sparkDataTypeSchemaString, expectedSparkDataTypeSchemaString);
  }

}
