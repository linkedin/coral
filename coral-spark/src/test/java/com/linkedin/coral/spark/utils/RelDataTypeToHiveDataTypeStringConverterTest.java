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


public class RelDataTypeToHiveDataTypeStringConverterTest {
  @Test
  public void testPrimitiveRelDataType() {
    Multimap<String, SqlTypeName> hiveDataTypeStringToSqlTypeNameMap =
        new ImmutableMultimap.Builder<String, SqlTypeName>().put("string", SqlTypeName.VARCHAR)
            .put("int", SqlTypeName.INTEGER).put("smallint", SqlTypeName.SMALLINT).put("bigint", SqlTypeName.BIGINT)
            .put("double", SqlTypeName.DOUBLE).put("float", SqlTypeName.FLOAT).put("boolean", SqlTypeName.BOOLEAN)
            .put("date", SqlTypeName.DATE).put("timestamp", SqlTypeName.TIMESTAMP).put("binary", SqlTypeName.BINARY)
            .put("interval", SqlTypeName.INTERVAL_DAY).put("null", SqlTypeName.NULL).build();

    for (Map.Entry<String, SqlTypeName> entry : hiveDataTypeStringToSqlTypeNameMap.entries()) {
      String expectedHiveDataTypeSchemaString = entry.getKey();
      SqlTypeName sqlTypeName = entry.getValue();
      RelDataType relDataType = new BasicSqlType(RelDataTypeSystem.DEFAULT, sqlTypeName);
      String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(relDataType);
      assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
    }
  }

  @Test
  public void testStructRelDataType() {
    String expectedHiveDataTypeSchemaString = "struct<str:string,int:int>";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);
    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(relRecordType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testArrayRelDataType() {
    String expectedHiveDataTypeSchemaString = "array<int>";

    ArraySqlType arraySqlType =
        new ArraySqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);

    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(arraySqlType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testMapRelDataType() {
    String expectedHiveDataTypeSchemaString = "map<int,int>";

    MapSqlType mapSqlType = new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER),
        new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);

    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testNestedStructRelDataType() {
    String expectedHiveDataTypeSchemaString = "struct<str:string,struct:struct<str:string,int:int>>";

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
    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(relRecordType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testMapWithStructValueRelDataType() {
    String expectedHiveDataTypeSchemaString = "map<int,struct<str:string,int:int>>";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    MapSqlType mapSqlType =
        new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), relRecordType, true);

    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testArrayWithStructEleRelDataType() {
    String expectedHiveDataTypeSchemaString = "array<struct<str:string,int:int>>";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    ArraySqlType arraySqlType = new ArraySqlType(relRecordType, true);

    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(arraySqlType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

  @Test
  public void testComplexRelDataType() {
    String expectedHiveDataTypeSchemaString = "map<int,array<struct<str:string,struct:struct<str:string,int:int>>>>";

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

    String hiveDataTypeSchemaString = RelDataTypeToHiveTypeStringConverter.convertRelDataType(mapSqlType);

    assertEquals(hiveDataTypeSchemaString, expectedHiveDataTypeSchemaString);
  }

}
