/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hive.serde2.typeinfo.*;
import org.testng.annotations.Test;

import com.linkedin.coral.common.types.*;

import static org.testng.Assert.*;


/**
 * Unit tests for {@link HiveToCoralTypeConverter}.
 */
public class HiveToCoralTypeConverterTest {

  @Test
  public void testPrimitiveTypes() {
    // Test void/null type
    testPrimitiveType(TypeInfoFactory.voidTypeInfo, CoralTypeKind.NULL, true, null, null);

    // Test boolean
    testPrimitiveType(TypeInfoFactory.booleanTypeInfo, CoralTypeKind.BOOLEAN, true, null, null);

    // Test numeric types
    testPrimitiveType(TypeInfoFactory.byteTypeInfo, CoralTypeKind.TINYINT, true, null, null);
    testPrimitiveType(TypeInfoFactory.shortTypeInfo, CoralTypeKind.SMALLINT, true, null, null);
    testPrimitiveType(TypeInfoFactory.intTypeInfo, CoralTypeKind.INT, true, null, null);
    testPrimitiveType(TypeInfoFactory.longTypeInfo, CoralTypeKind.BIGINT, true, null, null);
    testPrimitiveType(TypeInfoFactory.floatTypeInfo, CoralTypeKind.FLOAT, true, null, null);
    testPrimitiveType(TypeInfoFactory.doubleTypeInfo, CoralTypeKind.DOUBLE, true, null, null);

    // Test string types
    testPrimitiveType(TypeInfoFactory.stringTypeInfo, CoralTypeKind.STRING, true, null, null);

    // Test date/time types
    testPrimitiveType(TypeInfoFactory.dateTypeInfo, CoralTypeKind.DATE, true, null, null);
    // TIMESTAMP has PRECISION_NOT_SPECIFIED (-1) to match legacy TypeConverter behavior
    testPrimitiveType(TypeInfoFactory.timestampTypeInfo, CoralTypeKind.TIMESTAMP, true, -1, null);

    // Test binary
    testPrimitiveType(TypeInfoFactory.binaryTypeInfo, CoralTypeKind.BINARY, true, null, null);

    // Test decimal
    DecimalTypeInfo decimalType = TypeInfoFactory.getDecimalTypeInfo(10, 2);
    testPrimitiveType(decimalType, CoralTypeKind.DECIMAL, true, 10, 2);

    // Test varchar
    VarcharTypeInfo varcharType = TypeInfoFactory.getVarcharTypeInfo(100);
    testPrimitiveType(varcharType, CoralTypeKind.VARCHAR, true, 100, null);

    // Test char
    CharTypeInfo charType = TypeInfoFactory.getCharTypeInfo(10);
    testPrimitiveType(charType, CoralTypeKind.CHAR, true, 10, null);
  }

  private void testPrimitiveType(TypeInfo typeInfo, CoralTypeKind expectedKind, boolean expectedNullable,
      Integer expectedPrecision, Integer expectedScale) {
    CoralDataType result = HiveToCoralTypeConverter.convert(typeInfo);

    // Check basic properties
    assertTrue(result instanceof PrimitiveType || result instanceof DecimalType || result instanceof VarcharType
        || result instanceof CharType || result instanceof TimestampType);
    assertEquals(result.getKind(), expectedKind);
    assertEquals(result.isNullable(), expectedNullable);

    // Check type-specific properties
    if (result instanceof DecimalType) {
      DecimalType decimalType = (DecimalType) result;
      assertEquals(decimalType.getPrecision(), expectedPrecision.intValue());
      assertEquals(decimalType.getScale(), expectedScale.intValue());
    } else if (result instanceof VarcharType) {
      VarcharType varcharType = (VarcharType) result;
      assertEquals(varcharType.getLength(), expectedPrecision.intValue());
    } else if (result instanceof CharType) {
      CharType charType = (CharType) result;
      assertEquals(charType.getLength(), expectedPrecision.intValue());
    } else if (result instanceof TimestampType) {
      TimestampType timestampType = (TimestampType) result;
      assertEquals(timestampType.getPrecision(), expectedPrecision.intValue());
    }
  }

  @Test
  public void testArrayType() {
    // Simple array of integers
    ListTypeInfo arrayOfInts = (ListTypeInfo) TypeInfoFactory.getListTypeInfo(TypeInfoFactory.intTypeInfo);
    testArrayType(arrayOfInts, CoralTypeKind.INT, true);

    // Nested array (array of arrays)
    ListTypeInfo innerArray = (ListTypeInfo) TypeInfoFactory.getListTypeInfo(TypeInfoFactory.stringTypeInfo);
    ListTypeInfo arrayOfArrays = (ListTypeInfo) TypeInfoFactory.getListTypeInfo(innerArray);
    CoralDataType result = HiveToCoralTypeConverter.convert(arrayOfArrays);
    assertTrue(result instanceof ArrayType);
    ArrayType outerArray = (ArrayType) result;
    assertTrue(outerArray.getElementType() instanceof ArrayType);
    ArrayType innerArrayType = (ArrayType) outerArray.getElementType();
    assertEquals(innerArrayType.getElementType().getKind(), CoralTypeKind.STRING);
  }

  private void testArrayType(ListTypeInfo listType, CoralTypeKind expectedElementKind,
      boolean expectedElementNullable) {
    CoralDataType result = HiveToCoralTypeConverter.convert(listType);

    assertTrue(result instanceof ArrayType);
    ArrayType arrayType = (ArrayType) result;
    assertTrue(arrayType.isNullable());

    CoralDataType elementType = arrayType.getElementType();
    assertEquals(elementType.getKind(), expectedElementKind);
    assertEquals(elementType.isNullable(), expectedElementNullable);
  }

  @Test
  public void testMapType() {
    // Simple map: string -> int
    MapTypeInfo stringToIntMap =
        (MapTypeInfo) TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.stringTypeInfo, TypeInfoFactory.intTypeInfo);
    testMapType(stringToIntMap, CoralTypeKind.STRING, CoralTypeKind.INT);

    // Nested map: string -> map<string, int>
    MapTypeInfo innerMap =
        (MapTypeInfo) TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.stringTypeInfo, TypeInfoFactory.intTypeInfo);
    MapTypeInfo stringToMapMap = (MapTypeInfo) TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.stringTypeInfo, innerMap);

    CoralDataType result = HiveToCoralTypeConverter.convert(stringToMapMap);
    assertTrue(result instanceof MapType);
    MapType mapType = (MapType) result;
    assertEquals(mapType.getKeyType().getKind(), CoralTypeKind.STRING);

    assertTrue(mapType.getValueType() instanceof MapType);
    MapType nestedMap = (MapType) mapType.getValueType();
    assertEquals(nestedMap.getKeyType().getKind(), CoralTypeKind.STRING);
    assertEquals(nestedMap.getValueType().getKind(), CoralTypeKind.INT);
  }

  private void testMapType(MapTypeInfo mapType, CoralTypeKind expectedKeyKind, CoralTypeKind expectedValueKind) {
    CoralDataType result = HiveToCoralTypeConverter.convert(mapType);

    assertTrue(result instanceof MapType);
    MapType coralMap = (MapType) result;
    assertTrue(coralMap.isNullable());

    assertEquals(coralMap.getKeyType().getKind(), expectedKeyKind);
    assertEquals(coralMap.getValueType().getKind(), expectedValueKind);
  }

  @Test
  public void testStructType() {
    // Simple struct with primitive fields
    List<String> fieldNames = Arrays.asList("id", "name", "score");
    List<TypeInfo> fieldTypes =
        Arrays.asList(TypeInfoFactory.intTypeInfo, TypeInfoFactory.stringTypeInfo, TypeInfoFactory.doubleTypeInfo);
    StructTypeInfo structType = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypes);

    CoralDataType result = HiveToCoralTypeConverter.convert(structType);
    assertTrue(result instanceof StructType);
    StructType coralStruct = (StructType) result;
    assertTrue(coralStruct.isNullable());

    List<StructField> fields = coralStruct.getFields();
    assertEquals(fields.size(), 3);
    assertEquals(fields.get(0).getName(), "id");
    assertEquals(fields.get(0).getType().getKind(), CoralTypeKind.INT);
    assertEquals(fields.get(1).getName(), "name");
    assertEquals(fields.get(1).getType().getKind(), CoralTypeKind.STRING);
    assertEquals(fields.get(2).getName(), "score");
    assertEquals(fields.get(2).getType().getKind(), CoralTypeKind.DOUBLE);

    // Test nested struct
    List<String> nestedFieldNames = Arrays.asList("person", "age");
    List<TypeInfo> nestedFieldTypes = Arrays.asList(structType, TypeInfoFactory.intTypeInfo);
    StructTypeInfo nestedStructType =
        (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(nestedFieldNames, nestedFieldTypes);

    result = HiveToCoralTypeConverter.convert(nestedStructType);
    assertTrue(result instanceof StructType);
    StructType nestedCoralStruct = (StructType) result;
    assertEquals(nestedCoralStruct.getFields().size(), 2);
    assertTrue(nestedCoralStruct.getFields().get(0).getType() instanceof StructType);
  }

  @Test
  public void testUnionType() {
    // Union of int and string
    List<TypeInfo> unionTypes = Arrays.asList(TypeInfoFactory.intTypeInfo, TypeInfoFactory.stringTypeInfo);
    UnionTypeInfo unionType = (UnionTypeInfo) TypeInfoFactory.getUnionTypeInfo(unionTypes);

    CoralDataType result = HiveToCoralTypeConverter.convert(unionType);
    assertTrue(result instanceof StructType);
    StructType structType = (StructType) result;

    // Union is converted to a struct with "tag" field first, then fields for each possible type
    // This matches the Trino union representation: {tag, field0, field1, ...}
    List<StructField> fields = structType.getFields();
    assertEquals(fields.size(), 3); // tag + 2 union member fields
    assertEquals(fields.get(0).getName(), "tag");
    assertEquals(fields.get(0).getType().getKind(), CoralTypeKind.INT);
    assertEquals(fields.get(1).getName(), "field0");
    assertEquals(fields.get(1).getType().getKind(), CoralTypeKind.INT);
    assertEquals(fields.get(2).getName(), "field1");
    assertEquals(fields.get(2).getType().getKind(), CoralTypeKind.STRING);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullType() {
    HiveToCoralTypeConverter.convert(null);
  }

  @Test
  public void testComplexNestedType() {
    // Create a complex type: STRUCT<
    //   id: INT,
    //   name: STRING,
    //   scores: MAP<STRING, ARRAY<DOUBLE>>,
    //   address: STRUCT<street: STRING, zip: INT>
    // >

    // Inner struct type for address
    StructTypeInfo addressType = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(Arrays.asList("street", "zip"),
        Arrays.asList(TypeInfoFactory.stringTypeInfo, TypeInfoFactory.intTypeInfo));

    // Map type for scores
    ListTypeInfo doubleArrayType = (ListTypeInfo) TypeInfoFactory.getListTypeInfo(TypeInfoFactory.doubleTypeInfo);
    MapTypeInfo scoresType =
        (MapTypeInfo) TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.stringTypeInfo, doubleArrayType);

    // Outer struct type
    StructTypeInfo personType =
        (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(Arrays.asList("id", "name", "scores", "address"),
            Arrays.asList(TypeInfoFactory.intTypeInfo, TypeInfoFactory.stringTypeInfo, scoresType, addressType));

    // Convert and verify
    CoralDataType result = HiveToCoralTypeConverter.convert(personType);
    assertTrue(result instanceof StructType);
    StructType coralStruct = (StructType) result;

    // Verify fields
    List<StructField> fields = coralStruct.getFields();
    assertEquals(fields.size(), 4);

    // id: INT
    assertEquals(fields.get(0).getName(), "id");
    assertEquals(fields.get(0).getType().getKind(), CoralTypeKind.INT);

    // name: STRING
    assertEquals(fields.get(1).getName(), "name");
    assertEquals(fields.get(1).getType().getKind(), CoralTypeKind.STRING);

    // scores: MAP<STRING, ARRAY<DOUBLE>>
    assertEquals(fields.get(2).getName(), "scores");
    assertTrue(fields.get(2).getType() instanceof MapType);
    MapType scoresMap = (MapType) fields.get(2).getType();
    assertEquals(scoresMap.getKeyType().getKind(), CoralTypeKind.STRING);
    assertTrue(scoresMap.getValueType() instanceof ArrayType);
    ArrayType scoresArray = (ArrayType) scoresMap.getValueType();
    assertEquals(scoresArray.getElementType().getKind(), CoralTypeKind.DOUBLE);

    // address: STRUCT<...>
    assertEquals(fields.get(3).getName(), "address");
    assertTrue(fields.get(3).getType() instanceof StructType);
    StructType addressStruct = (StructType) fields.get(3).getType();
    assertEquals(addressStruct.getFields().size(), 2);
    assertEquals(addressStruct.getFields().get(0).getName(), "street");
    assertEquals(addressStruct.getFields().get(0).getType().getKind(), CoralTypeKind.STRING);
    assertEquals(addressStruct.getFields().get(1).getName(), "zip");
    assertEquals(addressStruct.getFields().get(1).getType().getKind(), CoralTypeKind.INT);
  }
}
