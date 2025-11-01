/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveTypeSystem;

import static org.testng.Assert.*;


/**
 * Unit tests for the Coral type system.
 */
public class CoralTypeSystemTest {

  private RelDataTypeFactory typeFactory;

  @BeforeMethod
  public void setUp() {
    typeFactory = new SqlTypeFactoryImpl(new HiveTypeSystem());
  }

  @Test
  public void testPrimitiveTypes() {
    // Test basic primitive types
    CoralPrimitiveType intType = new CoralPrimitiveType(CoralTypeKind.INT, false);
    assertEquals(intType.getKind(), CoralTypeKind.INT);
    assertFalse(intType.isNullable());

    CoralPrimitiveType nullableStringType = new CoralPrimitiveType(CoralTypeKind.STRING, true);
    assertEquals(nullableStringType.getKind(), CoralTypeKind.STRING);
    assertTrue(nullableStringType.isNullable());

    // Test conversion to Calcite types
    RelDataType relIntType = CoralTypeToRelDataTypeConverter.convert(intType, typeFactory);
    assertEquals(relIntType.getSqlTypeName(), SqlTypeName.INTEGER);
    assertFalse(relIntType.isNullable());

    RelDataType relStringType = CoralTypeToRelDataTypeConverter.convert(nullableStringType, typeFactory);
    assertEquals(relStringType.getSqlTypeName(), SqlTypeName.VARCHAR);
    assertTrue(relStringType.isNullable());
  }

  @Test
  public void testDecimalType() {
    CoralDecimalType decimalType = new CoralDecimalType(10, 2, true);
    assertEquals(decimalType.getPrecision(), 10);
    assertEquals(decimalType.getScale(), 2);
    assertTrue(decimalType.isNullable());
    assertEquals(decimalType.getKind(), CoralTypeKind.DECIMAL);

    RelDataType relDecimalType = CoralTypeToRelDataTypeConverter.convert(decimalType, typeFactory);
    assertEquals(relDecimalType.getSqlTypeName(), SqlTypeName.DECIMAL);
    assertEquals(relDecimalType.getPrecision(), 10);
    assertEquals(relDecimalType.getScale(), 2);
    assertTrue(relDecimalType.isNullable());
  }

  @Test
  public void testCharAndVarcharTypes() {
    CoralCharType charType = new CoralCharType(10, false);
    assertEquals(charType.getLength(), 10);
    assertFalse(charType.isNullable());
    assertEquals(charType.getKind(), CoralTypeKind.CHAR);

    CoralVarcharType varcharType = new CoralVarcharType(255, true);
    assertEquals(varcharType.getLength(), 255);
    assertTrue(varcharType.isNullable());
    assertEquals(varcharType.getKind(), CoralTypeKind.VARCHAR);

    RelDataType relCharType = CoralTypeToRelDataTypeConverter.convert(charType, typeFactory);
    assertEquals(relCharType.getSqlTypeName(), SqlTypeName.CHAR);
    assertEquals(relCharType.getPrecision(), 10);
    assertFalse(relCharType.isNullable());

    RelDataType relVarcharType = CoralTypeToRelDataTypeConverter.convert(varcharType, typeFactory);
    assertEquals(relVarcharType.getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(relVarcharType.getPrecision(), 255);
    assertTrue(relVarcharType.isNullable());
  }

  @Test
  public void testArrayType() {
    CoralPrimitiveType elementType = new CoralPrimitiveType(CoralTypeKind.INT, false);
    CoralArrayType arrayType = new CoralArrayType(elementType, true);

    assertEquals(arrayType.getKind(), CoralTypeKind.ARRAY);
    assertTrue(arrayType.isNullable());
    assertEquals(arrayType.getElementType(), elementType);

    RelDataType relArrayType = CoralTypeToRelDataTypeConverter.convert(arrayType, typeFactory);
    assertEquals(relArrayType.getSqlTypeName(), SqlTypeName.ARRAY);
    assertTrue(relArrayType.isNullable());
    assertEquals(relArrayType.getComponentType().getSqlTypeName(), SqlTypeName.INTEGER);
  }

  @Test
  public void testMapType() {
    CoralPrimitiveType keyType = new CoralPrimitiveType(CoralTypeKind.STRING, false);
    CoralPrimitiveType valueType = new CoralPrimitiveType(CoralTypeKind.INT, true);
    CoralMapType mapType = new CoralMapType(keyType, valueType, false);

    assertEquals(mapType.getKind(), CoralTypeKind.MAP);
    assertFalse(mapType.isNullable());
    assertEquals(mapType.getKeyType(), keyType);
    assertEquals(mapType.getValueType(), valueType);

    RelDataType relMapType = CoralTypeToRelDataTypeConverter.convert(mapType, typeFactory);
    assertEquals(relMapType.getSqlTypeName(), SqlTypeName.MAP);
    assertFalse(relMapType.isNullable());
    assertEquals(relMapType.getKeyType().getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(relMapType.getValueType().getSqlTypeName(), SqlTypeName.INTEGER);
  }

  @Test
  public void testStructType() {
    CoralPrimitiveType intType = new CoralPrimitiveType(CoralTypeKind.INT, false);
    CoralPrimitiveType stringType = new CoralPrimitiveType(CoralTypeKind.STRING, true);

    List<CoralStructField> fields =
        Arrays.asList(new CoralStructField("id", intType), new CoralStructField("name", stringType));

    CoralStructType structType = new CoralStructType(fields, false);
    assertEquals(structType.getKind(), CoralTypeKind.STRUCT);
    assertFalse(structType.isNullable());
    assertEquals(structType.getFields().size(), 2);
    assertEquals(structType.getFields().get(0).getName(), "id");
    assertEquals(structType.getFields().get(1).getName(), "name");

    RelDataType relStructType = CoralTypeToRelDataTypeConverter.convert(structType, typeFactory);
    assertEquals(relStructType.getSqlTypeName(), SqlTypeName.ROW);
    assertFalse(relStructType.isNullable());
    assertEquals(relStructType.getFieldCount(), 2);
    assertEquals(relStructType.getFieldList().get(0).getName(), "id");
    assertEquals(relStructType.getFieldList().get(1).getName(), "name");
    assertEquals(relStructType.getFieldList().get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    assertEquals(relStructType.getFieldList().get(1).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
  }

  @Test
  public void testComplexNestedType() {
    // Create a complex nested type: STRUCT<id: INT, tags: ARRAY<STRING>, metadata: MAP<STRING, STRING>>
    CoralPrimitiveType intType = new CoralPrimitiveType(CoralTypeKind.INT, false);
    CoralPrimitiveType stringType = new CoralPrimitiveType(CoralTypeKind.STRING, false);
    CoralArrayType stringArrayType = new CoralArrayType(stringType, true);
    CoralMapType stringMapType = new CoralMapType(stringType, stringType, true);

    List<CoralStructField> fields = Arrays.asList(new CoralStructField("id", intType),
        new CoralStructField("tags", stringArrayType), new CoralStructField("metadata", stringMapType));

    CoralStructType complexType = new CoralStructType(fields, false);

    RelDataType relComplexType = CoralTypeToRelDataTypeConverter.convert(complexType, typeFactory);
    assertEquals(relComplexType.getSqlTypeName(), SqlTypeName.ROW);
    assertEquals(relComplexType.getFieldCount(), 3);

    // Verify nested array type
    RelDataType tagsField = relComplexType.getFieldList().get(1).getType();
    assertEquals(tagsField.getSqlTypeName(), SqlTypeName.ARRAY);
    assertEquals(tagsField.getComponentType().getSqlTypeName(), SqlTypeName.VARCHAR);

    // Verify nested map type
    RelDataType metadataField = relComplexType.getFieldList().get(2).getType();
    assertEquals(metadataField.getSqlTypeName(), SqlTypeName.MAP);
    assertEquals(metadataField.getKeyType().getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(metadataField.getValueType().getSqlTypeName(), SqlTypeName.VARCHAR);
  }

  @Test
  public void testTimestampPrecisionMapping() {
    CoralTimestampType tsSec = new CoralTimestampType(0, false);
    RelDataType relSec = CoralTypeToRelDataTypeConverter.convert(tsSec, typeFactory);
    assertEquals(relSec.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(relSec.getPrecision(), 0);
    assertFalse(relSec.isNullable());

    CoralTimestampType tsMillis = new CoralTimestampType(3, true);
    RelDataType relMillis = CoralTypeToRelDataTypeConverter.convert(tsMillis, typeFactory);
    assertEquals(relMillis.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(relMillis.getPrecision(), 3);
    assertTrue(relMillis.isNullable());

    CoralTimestampType tsMicros = new CoralTimestampType(6, false);
    RelDataType relMicros = CoralTypeToRelDataTypeConverter.convert(tsMicros, typeFactory);
    assertEquals(relMicros.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(relMicros.getPrecision(), 6);
    assertFalse(relMicros.isNullable());
  }

  @Test
  public void testTypeEquality() {
    CoralPrimitiveType intType1 = new CoralPrimitiveType(CoralTypeKind.INT, false);
    CoralPrimitiveType intType2 = new CoralPrimitiveType(CoralTypeKind.INT, false);
    CoralPrimitiveType nullableIntType = new CoralPrimitiveType(CoralTypeKind.INT, true);

    assertEquals(intType1, intType2);
    assertNotEquals(intType1, nullableIntType);
    assertEquals(intType1.hashCode(), intType2.hashCode());
    assertNotEquals(intType1.hashCode(), nullableIntType.hashCode());
  }
}
