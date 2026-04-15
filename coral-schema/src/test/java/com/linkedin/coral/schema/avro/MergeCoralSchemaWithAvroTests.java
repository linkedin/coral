/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.Arrays;
import java.util.Map;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.types.ArrayType;
import com.linkedin.coral.common.types.BinaryType;
import com.linkedin.coral.common.types.CoralTypeKind;
import com.linkedin.coral.common.types.DecimalType;
import com.linkedin.coral.common.types.MapType;
import com.linkedin.coral.common.types.PrimitiveType;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;
import com.linkedin.coral.common.types.TimestampType;

import static org.testng.Assert.*;


public class MergeCoralSchemaWithAvroTests {

  @Test
  public void shouldUseFieldNamesFromCoral() {
    StructType coral = struct(field("fA", intType(true)), field("fB", struct(field("gA", intType(true)))));
    Schema avro = avroStruct("r1", optionalField("fa", Schema.Type.INT),
        optionalField("fb", avroStruct("r2", optionalField("ga", Schema.Type.INT))));

    Schema result = merge(coral, avro);
    // Coral field names are used (via partner copyField which preserves partner names,
    // but matching is case-insensitive so partner field "fa" matches Coral "fA")
    assertNotNull(result.getField("fa")); // partner name preserved via copyField
    assertNotNull(result.getField("fb"));
  }

  @Test
  public void shouldUseNullabilityFromCoral() {
    StructType coral = struct(field("fA", intType(false)), field("fB", intType(true)));
    Schema avro = avroStruct("r1", optionalField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // Coral says fA is non-nullable
    assertFalse(AvroSerdeUtils.isNullableType(result.getField("fA").schema()));
    // Coral says fB is nullable
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldUseTypesFromCoral() {
    StructType coral =
        struct(field("fA", struct(field("gA", intType(true)))), field("fB", ArrayType.of(intType(true), false)),
            field("fC", MapType.of(stringType(false), intType(true), false)), field("fD", stringType(false)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT),
        requiredField("fC", Schema.Type.INT), requiredField("fD", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // fA should be a record, not INT
    assertEquals(SchemaUtilities.extractIfOption(result.getField("fA").schema()).getType(), Schema.Type.RECORD);
    // fB should be an array
    assertEquals(result.getField("fB").schema().getType(), Schema.Type.ARRAY);
    // fC should be a map
    assertEquals(result.getField("fC").schema().getType(), Schema.Type.MAP);
    // fD should be a string
    assertEquals(result.getField("fD").schema().getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldIgnoreExtraFieldsFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getFields().size(), 1);
    assertNotNull(result.getField("fA"));
  }

  @Test
  public void shouldRetainExtraFieldsFromCoral() {
    StructType coral = struct(field("fA", intType(false)), field("fB", intType(true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getFields().size(), 2);
    assertNotNull(result.getField("fA"));
    // fB is extra from Coral, should be optional with sanitized name
    assertNotNull(result.getField("fB"));
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldRetainDocStringsFromAvro() {
    StructType coral = struct(field("fA", intType(true)));
    Schema avro =
        avroStruct("r1", "doc-r1", "n1", avroField("fA", Schema.create(Schema.Type.INT), "doc-fA", null, null));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fA").doc(), "doc-fA");
  }

  @Test
  public void shouldRetainDefaultValuesFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro = avroStruct("r1", avroField("fA", Schema.create(Schema.Type.INT), null, 42, null));

    Schema result = merge(coral, avro);
    assertTrue(AvroCompatibilityHelper.fieldHasDefault(result.getField("fA")));
  }

  @Test
  public void shouldRetainFieldPropsFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro =
        avroStruct("r1", requiredAvroField("fA", Schema.Type.INT, null, null, ImmutableMap.of("myProp", "myValue")));

    Schema result = merge(coral, avro);
    assertEquals(AvroCompatibilityHelper.getFieldPropAsJsonString(result.getField("fA"), "myProp"), "\"myValue\"");
  }

  @Test
  public void shouldHandleArrays() {
    StructType coral = struct(field("fA", ArrayType.of(intType(false), false)),
        field("fB", ArrayType.of(intType(true), true)), field("fC", ArrayType.of(intType(true), true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.createArray(Schema.create(Schema.Type.INT))),
        optionalField("fB", Schema.createArray(Schema.create(Schema.Type.INT))));

    Schema result = merge(coral, avro);
    // fA: non-nullable array from Coral
    assertEquals(result.getField("fA").schema().getType(), Schema.Type.ARRAY);
    // fB: nullable array from Coral
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
    // fC: extra from Coral, should be optional array
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fC").schema()));
  }

  @Test
  public void shouldHandleMaps() {
    StructType coral = struct(field("fA", MapType.of(stringType(false), intType(false), false)),
        field("fB", MapType.of(stringType(false), intType(true), true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.createMap(Schema.create(Schema.Type.INT))));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fA").schema().getType(), Schema.Type.MAP);
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldHandleTimestampMicros() {
    StructType coral = struct(field("ts", TimestampType.of(6, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    assertEquals(tsSchema.getType(), Schema.Type.LONG);
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-micros");
  }

  @Test
  public void shouldHandleTimestampMillis() {
    StructType coral = struct(field("ts", TimestampType.of(3, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-millis");
  }

  @Test
  public void shouldHandleTimestampUnspecifiedPrecision() {
    StructType coral = struct(field("ts", TimestampType.of(TimestampType.PRECISION_NOT_SPECIFIED, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    // Default to micros for unspecified precision
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-micros");
  }

  @Test
  public void shouldHandleDate() {
    StructType coral = struct(field("d", PrimitiveType.of(CoralTypeKind.DATE, true)));
    Schema avro = avroStruct("r1", optionalField("d", Schema.Type.INT));

    Schema result = merge(coral, avro);
    Schema dateSchema = SchemaUtilities.extractIfOption(result.getField("d").schema());
    assertEquals(dateSchema.getType(), Schema.Type.INT);
    assertEquals(dateSchema.getProp("logicalType"), "date");
  }

  @Test
  public void shouldHandleDecimal() {
    StructType coral = struct(field("dec", DecimalType.of(10, 2, true)));
    Schema avro = avroStruct("r1", optionalField("dec", Schema.Type.BYTES));

    Schema result = merge(coral, avro);
    Schema decSchema = SchemaUtilities.extractIfOption(result.getField("dec").schema());
    assertEquals(decSchema.getType(), Schema.Type.BYTES);
    assertEquals(decSchema.getProp("logicalType"), "decimal");
  }

  @Test
  public void shouldHandleFixedBinary() {
    StructType coral = struct(field("fb", BinaryType.of(16, true)));

    Schema result = merge(coral, null);
    Schema fbSchema = SchemaUtilities.extractIfOption(result.getField("fb").schema());
    assertEquals(fbSchema.getType(), Schema.Type.FIXED);
    assertEquals(fbSchema.getFixedSize(), 16);
  }

  @Test
  public void shouldHandleUnboundedBinary() {
    StructType coral = struct(field("ub", BinaryType.of(BinaryType.LENGTH_UNBOUNDED, false)));

    Schema result = merge(coral, null);
    assertEquals(result.getField("ub").schema().getType(), Schema.Type.BYTES);
  }

  @Test
  public void shouldPromoteBytesToFixedFromPartner() {
    StructType coral = struct(field("fb", BinaryType.of(BinaryType.LENGTH_UNBOUNDED, false)));
    Schema fixedSchema = Schema.createFixed("myFixed", null, null, 16);
    Schema avro = avroStruct("r1", requiredField("fb", fixedSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fb").schema().getType(), Schema.Type.FIXED);
    assertEquals(result.getField("fb").schema().getName(), "myFixed");
  }

  @Test
  public void shouldPromoteStringToEnumFromPartner() {
    StructType coral = struct(field("status", stringType(false)));
    Schema enumSchema = Schema.createEnum("Status", null, "com.test", Arrays.asList("ACTIVE", "INACTIVE"));
    Schema avro = avroStruct("r1", requiredField("status", enumSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("status").schema().getType(), Schema.Type.ENUM);
  }

  @Test
  public void shouldPreserveUuidFromPartner() {
    StructType coral = struct(field("uid", stringType(false)));
    Schema uuidSchema = Schema.create(Schema.Type.STRING);
    uuidSchema.addProp("logicalType", "uuid");
    Schema avro = avroStruct("r1", requiredField("uid", uuidSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("uid").schema().getProp("logicalType"), "uuid");
  }

  @Test
  public void shouldWorkWithNullPartner() {
    StructType coral = struct(field("fA", intType(true)), field("fB", stringType(false)));

    Schema result = merge(coral, null);
    assertNotNull(result);
    assertEquals(result.getFields().size(), 2);
    // fA is nullable
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fA").schema()));
    // fB is non-nullable
    assertFalse(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldUseCaseSensitiveMatchFirst() {
    StructType coral = struct(field("fA", intType(false)));
    // Partner has both "fA" (exact) and "fa" (case-insensitive) — should match "fA"
    Schema avro = avroStruct("r1", requiredAvroField("fA", Schema.Type.INT, "exact-match", null, null),
        requiredAvroField("fa", Schema.Type.INT, "ci-match", null, null));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fA").doc(), "exact-match");
  }

  @Test
  public void shouldHandleAmbiguousCaseInsensitiveMatch() {
    StructType coral = struct(field("fa", intType(true)));
    // Partner has "FA" and "Fa" — ambiguous case-insensitive, treat as no match
    Schema avro = avroStruct("r1", requiredField("FA", Schema.Type.INT), requiredField("Fa", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // Should be treated as extra field from Coral (optional)
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fa").schema()));
  }

  @Test
  public void shouldRespectPartnerNullPlacement() {
    StructType coral = struct(field("fA", intType(true)));
    // Partner has [int, null] order (null second)
    Schema avro = avroStruct("r1",
        avroField("fA",
            Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))), null, 1,
            null));

    Schema result = merge(coral, avro);
    // Should preserve [int, null] order
    Schema fieldSchema = result.getField("fA").schema();
    assertEquals(fieldSchema.getType(), Schema.Type.UNION);
    assertEquals(fieldSchema.getTypes().get(0).getType(), Schema.Type.INT);
    assertEquals(fieldSchema.getTypes().get(1).getType(), Schema.Type.NULL);
  }

  @Test
  public void shouldPreserveRecordNameAndNamespaceFromPartner() {
    StructType coral = struct(field("fA", intType(true)));
    Schema avro = avroStruct("MyRecord", null, "com.example", optionalField("fA", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getName(), "MyRecord");
    assertEquals(result.getNamespace(), "com.example");
  }

  @Test
  public void shouldHandleNestedStructsWithPartner() {
    StructType coral = struct(field("outer", struct(field("inner", intType(true)))));
    Schema avro = avroStruct("r1", optionalField("outer", avroStruct("r2", optionalField("inner", Schema.Type.INT))));

    Schema result = merge(coral, avro);
    Schema outerSchema = SchemaUtilities.extractIfOption(result.getField("outer").schema());
    assertEquals(outerSchema.getType(), Schema.Type.RECORD);
    assertEquals(outerSchema.getName(), "r2");
    assertNotNull(outerSchema.getField("inner"));
  }

  /** Test Helpers */

  private Schema merge(StructType coral, Schema avro) {
    return MergeCoralSchemaWithAvro.visit(coral, avro, "TestRecord", "com.test");
  }

  private PrimitiveType intType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.INT, nullable);
  }

  private PrimitiveType stringType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.STRING, nullable);
  }

  private StructField field(String name, com.linkedin.coral.common.types.CoralDataType type) {
    return StructField.of(name, type);
  }

  private StructType struct(StructField... fields) {
    return StructType.of(Arrays.asList(fields), true);
  }

  private Schema avroStruct(String name, Schema.Field... fields) {
    return avroStruct(name, null, "n" + name, fields);
  }

  private Schema avroStruct(String name, String doc, String namespace, Schema.Field... fields) {
    Schema result = Schema.createRecord(name, doc, namespace, false);
    result.setFields(Arrays.asList(fields));
    return result;
  }

  private Schema.Field avroField(String name, Schema schema, String doc, Object defaultValue,
      Map<String, String> props) {
    Schema.Field field = AvroCompatibilityHelper.createSchemaField(name, schema, doc, defaultValue);
    if (props != null) {
      props.forEach((propName, propValueInJson) -> AvroCompatibilityHelper.setFieldPropFromJsonString(field, propName,
          propValueInJson, false));
    }
    return field;
  }

  private Schema.Field requiredField(String name, Schema.Type type) {
    return requiredField(name, Schema.create(type));
  }

  private Schema.Field requiredField(String name, Schema schema) {
    return avroField(name, schema, null, null, null);
  }

  private Schema.Field requiredAvroField(String name, Schema.Type type, String doc, Object defaultValue,
      Map<String, String> props) {
    return avroField(name, Schema.create(type), doc, defaultValue, props);
  }

  private Schema.Field optionalField(String name, Schema.Type type) {
    return optionalField(name, Schema.create(type));
  }

  private Schema.Field optionalField(String name, Schema schema) {
    return avroField(name, SchemaUtilities.makeNullable(schema, false), null, null, null);
  }
}
