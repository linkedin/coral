/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Arrays;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Types;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.types.CoralTypeToRelDataTypeConverter;
import com.linkedin.coral.common.types.StructType;

import static org.testng.Assert.*;


/**
 * Test to verify that IcebergTypeConverter (direct) produces the same results
 * as IcebergToCoralTypeConverter + CoralTypeToRelDataTypeConverter (two-stage).
 *
 * This ensures backward compatibility when switching from direct to two-stage conversion.
 */
public class IcebergTypeConverterEquivalenceTest {

  private RelDataTypeFactory typeFactory;

  @BeforeClass
  public void setup() {
    typeFactory = new CoralJavaTypeFactoryImpl(new HiveTypeSystem());
  }

  @Test
  public void testPrimitiveTypesEquivalence() {
    // Create schema with all primitive types
    Schema schema = new Schema(Arrays.asList(Types.NestedField.required(1, "bool_col", Types.BooleanType.get()),
        Types.NestedField.required(2, "int_col", Types.IntegerType.get()),
        Types.NestedField.required(3, "long_col", Types.LongType.get()),
        Types.NestedField.required(4, "float_col", Types.FloatType.get()),
        Types.NestedField.required(5, "double_col", Types.DoubleType.get()),
        Types.NestedField.required(6, "date_col", Types.DateType.get()),
        Types.NestedField.required(7, "time_col", Types.TimeType.get()),
        Types.NestedField.required(8, "timestamp_col", Types.TimestampType.withoutZone()),
        Types.NestedField.required(9, "string_col", Types.StringType.get()),
        Types.NestedField.required(10, "uuid_col", Types.UUIDType.get()),
        Types.NestedField.required(11, "fixed_col", Types.FixedType.ofLength(16)), // FIXED type
        Types.NestedField.required(12, "binary_col", Types.BinaryType.get()),
        Types.NestedField.required(13, "decimal_col", Types.DecimalType.of(10, 2))));

    // Direct conversion
    RelDataType directType = IcebergTypeConverter.convert(schema, "test_table", typeFactory);

    // Two-stage conversion
    StructType coralSchema = IcebergToCoralTypeConverter.convert(schema);
    RelDataType twoStageType = convertCoralToCalcite(coralSchema);

    // Compare
    System.out.println("Direct:    " + directType);
    System.out.println("Two-stage: " + twoStageType);

    // This will fail for FIXED type due to length mismatch
    assertEquals(twoStageType.toString(), directType.toString(), "Two-stage conversion should match direct conversion");
  }

  @Test
  public void testNullabilityEquivalence() {
    // Test optional (nullable) fields
    Schema schema = new Schema(Arrays.asList(Types.NestedField.optional(1, "nullable_int", Types.IntegerType.get()),
        Types.NestedField.required(2, "required_int", Types.IntegerType.get())));

    RelDataType directType = IcebergTypeConverter.convert(schema, "test_table", typeFactory);
    StructType coralSchema = IcebergToCoralTypeConverter.convert(schema);
    RelDataType twoStageType = convertCoralToCalcite(coralSchema);

    assertEquals(twoStageType.toString(), directType.toString(),
        "Nullability should be preserved in two-stage conversion");
  }

  @Test
  public void testComplexTypesEquivalence() {
    // Test LIST, MAP, STRUCT
    Schema schema = new Schema(
        Arrays.asList(Types.NestedField.required(1, "list_col", Types.ListType.ofRequired(2, Types.IntegerType.get())),
            Types.NestedField.required(3, "map_col",
                Types.MapType.ofRequired(4, 5, Types.StringType.get(), Types.IntegerType.get())),
            Types.NestedField.required(6, "struct_col",
                Types.StructType.of(Types.NestedField.required(7, "nested_field", Types.StringType.get())))));

    RelDataType directType = IcebergTypeConverter.convert(schema, "test_table", typeFactory);
    StructType coralSchema = IcebergToCoralTypeConverter.convert(schema);
    RelDataType twoStageType = convertCoralToCalcite(coralSchema);

    assertEquals(twoStageType.toString(), directType.toString(), "Complex types should match in two-stage conversion");
  }

  private RelDataType convertCoralToCalcite(StructType structType) {
    return CoralTypeToRelDataTypeConverter.convert(structType, typeFactory);
  }
}
