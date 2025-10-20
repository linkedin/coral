/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TypeConverterTest {

  @Test
  public void testTimestampWithoutPrecision() {

    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    // Test regular timestamp (no precision specified)
    RelDataType timestampType = TypeConverter.convert(TypeInfoFactory.timestampTypeInfo, typeFactory);

    assertEquals(timestampType.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    // When no precision is specified, RelDataType.getPrecision() returns -1 or PRECISION_NOT_SPECIFIED
    assertTrue(timestampType.getPrecision() <= 0 || timestampType.getPrecision() == Integer.MAX_VALUE);
  }

  @Test
  public void testTimestampWithPrecision3() {
    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    // Create a PrimitiveTypeInfo for timestamp(3) - Hive default
    // We create a custom TypeInfo that properly returns "timestamp(3)" from getTypeName()
    TypeInfo typeInfo = createTimestampTypeInfoWithPrecision(3);

    RelDataType timestampType = TypeConverter.convert(typeInfo, typeFactory);

    assertEquals(timestampType.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(timestampType.getPrecision(), 3);
  }

  /**
   * Helper method to create a TypeInfo for timestamp with precision.
   * Hive's TypeInfoUtils.getTypeInfoFromTypeString() doesn't properly preserve
   * timestamp precision, so we create a custom PrimitiveTypeInfo that overrides
   * getTypeName() to return "timestamp(N)" where N is the precision.
   */
  private TypeInfo createTimestampTypeInfoWithPrecision(int precision) {
    return new org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo() {
      {
        // Set the primitive category to TIMESTAMP
        this.setTypeName("timestamp");
      }

      @Override
      public String getTypeName() {
        return "timestamp(" + precision + ")";
      }

      @Override
      public org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory getPrimitiveCategory() {
        return org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory.TIMESTAMP;
      }

    };
  }

  @Test
  public void testTimestampWithPrecision6() {
    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    TypeInfo typeInfo = createTimestampTypeInfoWithPrecision(6);

    RelDataType timestampType = TypeConverter.convert(typeInfo, typeFactory);

    assertEquals(timestampType.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(timestampType.getPrecision(), 6);
  }

  @Test
  public void testTimestampWithPrecision9() {
    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    TypeInfo typeInfo = createTimestampTypeInfoWithPrecision(9);

    RelDataType timestampType = TypeConverter.convert(typeInfo, typeFactory);

    assertEquals(timestampType.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(timestampType.getPrecision(), 9);
  }

  @Test
  public void testTimestampInStructWithPrecision() {
    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    // Test struct with timestamp(6) field
    // Create a custom StructTypeInfo with int and timestamp(6) fields
    TypeInfo typeInfo = createStructTypeInfoWithTimestamp(6);

    RelDataType structType = TypeConverter.convert(typeInfo, typeFactory);

    assertEquals(structType.getSqlTypeName(), SqlTypeName.ROW);
    assertEquals(structType.getFieldCount(), 2);

    // Check the timestamp field has precision 6
    RelDataType tsFieldType = structType.getFieldList().get(1).getType();
    assertEquals(tsFieldType.getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(tsFieldType.getPrecision(), 6);
  }

  /**
   * Helper method to create a StructTypeInfo containing an int field and a timestamp field with precision.
   * Returns a struct equivalent to: struct<id:int,ts:timestamp(N)>
   */
  private TypeInfo createStructTypeInfoWithTimestamp(int timestampPrecision) {
    java.util.List<String> fieldNames = java.util.Arrays.asList("id", "ts");
    java.util.List<TypeInfo> fieldTypes =
        java.util.Arrays.asList(TypeInfoFactory.intTypeInfo, createTimestampTypeInfoWithPrecision(timestampPrecision));
    return TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypes);
  }

  @Test
  public void testOtherPrimitiveTypesUnaffected() {
    RelDataTypeFactory typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());

    // Test that other types are not affected by the timestamp changes
    RelDataType intType = TypeConverter.convert(TypeInfoFactory.intTypeInfo, typeFactory);
    assertEquals(intType.getSqlTypeName(), SqlTypeName.INTEGER);

    RelDataType stringType = TypeConverter.convert(TypeInfoFactory.stringTypeInfo, typeFactory);
    assertEquals(stringType.getSqlTypeName(), SqlTypeName.VARCHAR);

    RelDataType dateType = TypeConverter.convert(TypeInfoFactory.dateTypeInfo, typeFactory);
    assertEquals(dateType.getSqlTypeName(), SqlTypeName.DATE);
  }
}
