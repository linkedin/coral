/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

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


public class RelDataTypeToTrinoTypeStringConverterTest {

  private static final String CHAR = "char";
  private static final String VARCHAR = "varchar";
  private static final String INTEGER = "integer";
  private static final String SMALLINT = "smallint";
  private static final String TINYINT = "tinyint";
  private static final String BIGINT = "bigint";
  private static final String DOUBLE = "double";
  private static final String REAL = "real";
  private static final String BOOLEAN = "boolean";
  private static final String DATE = "date";
  private static final String TIMESTAMP = "timestamp";
  private static final String TIME = "time";
  private static final String VARBINARY = "varbinary";

  @Test
  public void testPrimitiveRelDataType() {
    Multimap<String, SqlTypeName> trinoTypeCastStringToSqlTypeNameMap =
        new ImmutableMultimap.Builder<String, SqlTypeName>().put(CHAR, SqlTypeName.CHAR)
            .put(VARCHAR, SqlTypeName.VARCHAR).put(INTEGER, SqlTypeName.INTEGER).put(SMALLINT, SqlTypeName.SMALLINT)
            .put(TINYINT, SqlTypeName.TINYINT).put(BIGINT, SqlTypeName.BIGINT).put(DOUBLE, SqlTypeName.DOUBLE)
            .put(REAL, SqlTypeName.REAL).put(REAL, SqlTypeName.FLOAT).put(BOOLEAN, SqlTypeName.BOOLEAN)
            .put(DATE, SqlTypeName.DATE).put(TIMESTAMP, SqlTypeName.TIMESTAMP).put(TIME, SqlTypeName.TIME)
            .put(VARBINARY, SqlTypeName.BINARY).put(VARBINARY, SqlTypeName.VARBINARY).build();
    for (Map.Entry<String, SqlTypeName> entry : trinoTypeCastStringToSqlTypeNameMap.entries()) {
      String expectedTrinoTypeCastString = entry.getKey();
      SqlTypeName sqlTypeName = entry.getValue();
      RelDataType relDataType = new BasicSqlType(RelDataTypeSystem.DEFAULT, sqlTypeName);
      String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(relDataType);
      assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
    }
  }

  @Test
  public void testStructRelDataType() {
    String expectedTrinoTypeCastString = "row(\"str\" varchar, \"int\" integer)";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(relRecordType);
    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testArrayRelDataType() {
    String expectedTrinoTypeCastString = "array(integer)";

    ArraySqlType arraySqlType =
        new ArraySqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(arraySqlType);

    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testMapRelDataType() {
    String expectedTrinoTypeCastString = "map(integer, integer)";

    MapSqlType mapSqlType = new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER),
        new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), true);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(mapSqlType);

    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testNestedStructRelDataType() {
    String expectedTrinoTypeCastString = "row(\"str\" varchar, \"struct\" row(\"values\" varchar, \"int\" integer))";

    List<RelDataTypeField> nestedFields = new ArrayList<>();
    nestedFields
        .add(new RelDataTypeFieldImpl("values", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    nestedFields
        .add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType nestedRelRecordType = new RelRecordType(nestedFields);

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("str", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("struct", 0, nestedRelRecordType));

    RelRecordType relRecordType = new RelRecordType(fields);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(relRecordType);
    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testMapWithStructValueRelDataType() {
    String expectedTrinoTypeCastString = "map(integer, row(\"values\" varchar, \"int\" integer))";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("values", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    MapSqlType mapSqlType =
        new MapSqlType(new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER), relRecordType, true);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(mapSqlType);

    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testArrayWithStructEleRelDataType() {
    String expectedTrinoTypeCastString = "array(row(\"values\" varchar, \"int\" integer))";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("values", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));

    RelRecordType relRecordType = new RelRecordType(fields);

    ArraySqlType arraySqlType = new ArraySqlType(relRecordType, true);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(arraySqlType);

    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testComplexRelDataType() {
    String expectedTrinoTypeCastString =
        "map(integer, array(row(\"str\" varchar, \"struct\" row(\"values\" varchar, \"int\" integer))))";

    List<RelDataTypeField> nestedFields = new ArrayList<>();
    nestedFields
        .add(new RelDataTypeFieldImpl("values", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
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
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(mapSqlType);

    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }

  @Test
  public void testDifferentTypeStructRelDataType() {
    String expectedTrinoTypeCastString =
        "row(\"int\" integer, \"small\" smallint, \"tiny\" tinyint, \"big\" bigint, \"rea\" real, \"flo\" real, \"bool\" boolean, \"ch\" char, \"vch\" varchar, \"dat\" date, \"tstamp\" timestamp, \"tim\" time, \"bin\" varbinary, \"vbin\" varbinary)";

    List<RelDataTypeField> fields = new ArrayList<>();
    fields.add(new RelDataTypeFieldImpl("int", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER)));
    fields.add(new RelDataTypeFieldImpl("small", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.SMALLINT)));
    fields.add(new RelDataTypeFieldImpl("tiny", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.TINYINT)));
    fields.add(new RelDataTypeFieldImpl("big", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.BIGINT)));
    fields.add(new RelDataTypeFieldImpl("rea", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.REAL)));
    fields.add(new RelDataTypeFieldImpl("flo", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.FLOAT)));
    fields.add(new RelDataTypeFieldImpl("bool", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.BOOLEAN)));
    fields.add(new RelDataTypeFieldImpl("ch", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.CHAR)));
    fields.add(new RelDataTypeFieldImpl("vch", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARCHAR)));
    fields.add(new RelDataTypeFieldImpl("dat", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.DATE)));
    fields
        .add(new RelDataTypeFieldImpl("tstamp", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.TIMESTAMP)));
    fields.add(new RelDataTypeFieldImpl("tim", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.TIME)));
    fields.add(new RelDataTypeFieldImpl("bin", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.BINARY)));
    fields.add(new RelDataTypeFieldImpl("vbin", 0, new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.VARBINARY)));

    RelRecordType relRecordType = new RelRecordType(fields);
    String trinoTypeCastString = RelDataTypeToTrinoTypeStringConverter.buildTrinoTypeString(relRecordType);
    assertEquals(trinoTypeCastString, expectedTrinoTypeCastString);
  }
}
