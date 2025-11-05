/**
 * Copyright 2020-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;
import com.linkedin.avroutil1.compatibility.Jackson1Utils;

import org.apache.avro.Schema;
import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.hive.serde2.avro.AvroSerDe;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.codehaus.jackson.node.IntNode;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.com.google.common.collect.Lists;

import static org.testng.Assert.*;


public class MergeHiveSchemaWithAvroTests {

  @Test
  public void shouldUseFieldNamesFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro =
        struct("r1", optional("fA", Schema.Type.INT), optional("fB", struct("r2", optional("gA", Schema.Type.INT))));

    assertSchema(avro, merge(hive, avro));
  }

  @Test
  public void shouldUseNullabilityFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro =
        struct("r1", required("fA", Schema.Type.INT), required("fB", struct("r2", required("gA", Schema.Type.INT))));

    assertSchema(avro, merge(hive, avro));
  }

  @Test
  public void shouldUseTypesFromHive() {
    String hive = "struct<fa:struct<ga:int>,fb:array<int>,fc:map<string,int>,fd:string>";
    Schema avro = struct("r1", required("fA", Schema.Type.INT), required("fB", Schema.Type.INT),
        required("fC", Schema.Type.INT), required("fD", Schema.Type.INT));

    Schema expected =
        struct("r1", required("fA", struct("record1", null, "namespace1", optional("ga", Schema.Type.INT))),
            required("fB", array(nullable(Schema.Type.INT))), required("fC", map(nullable(Schema.Type.INT))),
            required("fD", Schema.Type.STRING));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldIgnoreExtraFieldsFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro = struct("r1", required("fA", Schema.Type.INT),
        required("fB", struct("r2", required("gA", Schema.Type.INT), required("gB", Schema.Type.INT))),
        required("fC", Schema.Type.INT));

    Schema expected =
        struct("r1", required("fA", Schema.Type.INT), required("fB", struct("r2", required("gA", Schema.Type.INT))));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldRetainExtraFieldsFromHive() {
    String hive = "struct<fa:int,fb:struct<ga:int,gb:int>,fc:int,fd:struct<ha:int>>";
    Schema avro =
        struct("r1", required("fA", Schema.Type.INT), required("fB", struct("r2", required("gA", Schema.Type.INT))));

    Schema expected = struct("r1", required("fA", Schema.Type.INT),
        required("fB", struct("r2", required("gA", Schema.Type.INT),
            // Nested field missing in Avro
            optional("gb", Schema.Type.INT))),
        // Top level field missing in Avro
        optional("fc", Schema.Type.INT),
        // Top level struct missing in Avro
        optional("fd", struct("record1", null, "namespace1", optional("ha", Schema.Type.INT))));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldRetainDocStringsFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro = struct("r1", "doc-r1", "n1", required("fA", Schema.Type.INT, "doc-fA", null, null), required("fB",
        struct("r2", "doc-r2", "n2", required("gA", Schema.Type.INT, "doc-gA", null, null)), "doc-fB", null, null));

    assertSchema(avro, merge(hive, avro));
  }

  @Test
  public void shouldRetainDefaultValuesFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro = struct("r1", field("fA", Schema.create(Schema.Type.INT), null, 1, null),
        field("fB", struct("r2", field("gA", Schema.create(Schema.Type.INT), null, 2, null)), null,
            ImmutableMap.of("gA", 3), null));

    assertSchema(avro, merge(hive, avro));
  }

  @Test
  public void shouldRetainFieldPropsFromAvro() {
    String hive = "struct<fa:int,fb:struct<ga:int>>";
    Schema avro = struct("r1", required("fA", Schema.Type.INT, null, null, ImmutableMap.of("pfA", "vfA")),
        required("fB", struct("r2", required("gA", Schema.Type.INT, null, null, ImmutableMap.of("pfB", "vfB"))), null,
            null, ImmutableMap.of("pgA", "vgA")));

    assertSchema(avro, merge(hive, avro));
  }

  @Test
  public void shouldHandleLists() {
    String hive = "struct<fa:array<int>,fb:array<int>,fc:array<struct<ga:int>>,fd:array<int>>";
    Schema avro = struct("r1", required("fA", array(Schema.Type.INT)), optional("fB", array(Schema.Type.INT)),
        required("fC", array(struct("r2", required("gA", Schema.Type.INT)))));

    Schema expected = struct("r1", required("fA", array(Schema.Type.INT)), optional("fB", array(Schema.Type.INT)),
        required("fC", array(struct("r2", required("gA", Schema.Type.INT)))),
        // Array element type is also nullable because it is generated from Hive
        optional("fd", array(nullable(Schema.Type.INT))));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldHandleMaps() {
    String hive = "struct<fa:map<string,int>,fb:map<string,int>,fc:map<string,struct<ga:int>>,fd:map<string,int>>";
    Schema avro = struct("r1", required("fA", map(Schema.Type.INT)), optional("fB", map(Schema.Type.INT)),
        required("fC", map(struct("r2", required("gA", Schema.Type.INT)))));

    Schema expected = struct("r1", required("fA", map(Schema.Type.INT)), optional("fB", map(Schema.Type.INT)),
        required("fC", map(struct("r2", required("gA", Schema.Type.INT)))),
        // Map value type is also nullable because it is generated from Hive
        optional("fd", map(nullable(Schema.Type.INT))));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldSanitizeIncompatibleFieldNames() {
    StructTypeInfo typeInfo = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(Lists.newArrayList("a.b.c", "$#@%!"),
        Lists.newArrayList(TypeInfoFactory.intTypeInfo, TypeInfoFactory.intTypeInfo));
    Schema avro = struct("r1");

    Schema expected =
        struct("r1", optional("a_x2Eb_x2Ec", Schema.Type.INT), optional("_x24_x23_x40_x25_x21", Schema.Type.INT));
    assertSchema(expected, merge(typeInfo, avro));
  }

  @Test
  public void makeNullableShouldRespectOptionOrderOfPartner() {
    String hive = "struct<fa:int,fb:struct<ga:int>,fc:int>";
    Schema avro = struct("r1",
        field("fA", Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))),
            null, 1, null),
        field("fB",
            Schema.createUnion(Arrays.asList(struct("r2", field("gA", Schema.create(Schema.Type.INT), null, 2, null)),
                Schema.create(Schema.Type.NULL))),
            null, null, null),
        field("fC", Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))),
            null, null, null));

    Schema expectedAvro = struct("r1",
        field("fA", Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))),
            null, 1, null),
        field("fB",
            Schema.createUnion(Arrays.asList(struct("r2", field("gA", Schema.create(Schema.Type.INT), null, 2, null)),
                Schema.create(Schema.Type.NULL))),
            null, null, null),
        field("fC", Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))),
            null, null, null));

    assertSchema(expectedAvro, merge(hive, avro));
  }

  @Test
  public void shouldFailForMapsWithNonStringKey() {
    String hive = "struct<fa:map<int,int>>";
    Schema avro = struct("r1");

    try {
      assertSchema(avro, merge(hive, avro));
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "Map keys should always be non-nullable strings. Found: [\"null\",\"int\"]");
    }
  }

  @Test
  public void shouldRecoverLogicalType() {
    String hive = "struct<fa:date,fb:timestamp,fc:decimal(4,2)>";
    Schema avro = struct("r1", optional("fa", Schema.Type.INT), optional("fb", Schema.Type.LONG),
        optional("fc", Schema.Type.BYTES));
    Schema merged = merge(hive, avro);

    Schema dateSchema = Schema.create(Schema.Type.INT);
    dateSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DATE_TYPE_NAME);

    Schema timestampSchema = Schema.create(Schema.Type.LONG);
    timestampSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.TIMESTAMP_TYPE_NAME);

    Schema decimalSchema = Schema.create(Schema.Type.BYTES);
    decimalSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DECIMAL_TYPE_NAME);
    AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_PRECISION,
        Jackson1Utils.toJsonString(new IntNode(4)), false);
    AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_SCALE,
        Jackson1Utils.toJsonString(new IntNode(2)), false);

    Schema expected =
        struct("r1", optional("fa", dateSchema), optional("fb", timestampSchema), optional("fc", decimalSchema));

    assertSchema(expected, merged);
  }

  @Test
  public void shouldHandleUnions() {
    String hive = "struct<fa:uniontype<string,int>,fb:uniontype<string,int>,fc:uniontype<string,int>>";
    Schema avro = struct("r1", required("fA", union(Schema.Type.NULL, Schema.Type.STRING, Schema.Type.INT)),
        required("fB", union(Schema.Type.STRING, Schema.Type.INT)),
        required("fC", union(Schema.Type.STRING, Schema.Type.INT, Schema.Type.NULL)));

    Schema expected = struct("r1", required("fA", union(Schema.Type.NULL, Schema.Type.STRING, Schema.Type.INT)),
        required("fB", union(Schema.Type.STRING, Schema.Type.INT)),
        // our merge logic always put the NULL alternative in the front
        required("fC", union(Schema.Type.NULL, Schema.Type.STRING, Schema.Type.INT)));

    assertSchema(expected, merge(hive, avro));
  }

  @Test
  public void shouldHandleSingleElementUnionsInArraysAndMaps() {
    // This test verifies that single-element unions in array items and map values are properly unwrapped
    // and the nested field nullability is preserved during schema merging.
    // This reproduces the fix for handling avro.schema.literal with single-element unions like:
    // - Array items: "items": [{"type":"record",...}]
    // - Map values: "values": [{"type":"record",...}]
    // These single-element unions appear in real-world Avro schemas stored as avro.schema.literal

    String hive = "struct<id:bigint,items:array<struct<fooconfiguration:struct<name:string,urlvalue:string,source:string>,"
        + "barconfiguration:struct<name:string,domain:string>>>,"
        + "metadata:map<string,struct<category:string,priority:int>>>";

    // Define an Avro schema literal similar to what would be stored in avro.schema.literal table property
    // Note the single-element unions in array items and map values: [{"type":"record",...}]
    String avroSchemaLiteral =
        "{\"type\":\"record\",\"name\":\"test_complex_array_table\",\"namespace\":\"com.example.test\",\"fields\":["
            + "{\"name\":\"id\",\"type\":[\"null\",\"long\"],\"default\":null},"
            + "{\"name\":\"items\",\"type\":[\"null\",{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"ItemConfig\",\"namespace\":\"com.example.data\",\"fields\":["
            + "{\"name\":\"fooConfiguration\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"FooConfiguration\",\"fields\":["
            + "{\"name\":\"name\",\"type\":\"string\"},"
            + "{\"name\":\"urlValue\",\"type\":\"string\"},"
            + "{\"name\":\"source\",\"type\":\"string\"}"
            + "]}],\"default\":null},"
            + "{\"name\":\"barConfiguration\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"BarConfiguration\",\"fields\":["
            + "{\"name\":\"name\",\"type\":\"string\"},"
            + "{\"name\":\"domain\",\"type\":\"string\"}"
            + "]}],\"default\":null}"
            + "]}]}],\"default\":null},"
            + "{\"name\":\"metadata\",\"type\":[\"null\",{\"type\":\"map\",\"values\":[{\"type\":\"record\",\"name\":\"MetadataValue\",\"namespace\":\"com.example.data\",\"fields\":["
            + "{\"name\":\"category\",\"type\":\"string\"},"
            + "{\"name\":\"priority\",\"type\":\"int\"}"
            + "]}]}],\"default\":null}"
            + "]}";

    Schema avro = new Schema.Parser().parse(avroSchemaLiteral);
    Schema merged = merge(hive, avro);

    // Verify that single-element unions were properly handled
    // Extract items array
    Schema mergedItemsArray = SchemaUtilities.extractIfOption(merged.getField("items").schema());
    Schema mergedItemConfig = mergedItemsArray.getElementType();

    // The fix ensures that single-element union [ItemConfig] is unwrapped to ItemConfig
    // Without the fix, this would fail because the union wouldn't be unwrapped
    assertEquals(mergedItemConfig.getType(), Schema.Type.RECORD, "Array element should be a record, not a union");

    // Extract fooConfiguration and verify nested field nullability is preserved
    Schema mergedFooConfig =
        SchemaUtilities.extractIfOption(mergedItemConfig.getField("fooConfiguration").schema());

    // Nested fields should be non-nullable (required) as defined in the avro.schema.literal
    assertEquals(mergedFooConfig.getField("name").schema().getType(), Schema.Type.STRING,
        "name field should be non-nullable string");
    assertEquals(mergedFooConfig.getField("urlValue").schema().getType(), Schema.Type.STRING,
        "urlValue field should be non-nullable string");
    assertEquals(mergedFooConfig.getField("source").schema().getType(), Schema.Type.STRING,
        "source field should be non-nullable string");

    // Verify barConfiguration nested fields
    Schema mergedBarConfig =
        SchemaUtilities.extractIfOption(mergedItemConfig.getField("barConfiguration").schema());
    assertEquals(mergedBarConfig.getField("name").schema().getType(), Schema.Type.STRING,
        "bar name field should be non-nullable string");
    assertEquals(mergedBarConfig.getField("domain").schema().getType(), Schema.Type.STRING,
        "domain field should be non-nullable string");

    // Extract metadata map value and verify
    // Ensures that single-element union [MetadataValue] is unwrapped to MetadataValue
    Schema mergedMetadataMap = SchemaUtilities.extractIfOption(merged.getField("metadata").schema());
    Schema mergedMetadataValue = mergedMetadataMap.getValueType();

    assertEquals(mergedMetadataValue.getType(), Schema.Type.RECORD, "Map value should be a record, not a union");

    // Fields in MetadataValue should be non-nullable as defined in avro.schema.literal
    assertEquals(mergedMetadataValue.getField("category").schema().getType(), Schema.Type.STRING,
        "category field should be non-nullable string");
    assertEquals(mergedMetadataValue.getField("priority").schema().getType(), Schema.Type.INT,
        "priority field should be non-nullable int");
  }

  @Test
  public void shouldHandleUnionEncodedAsStruct() {
    // This test verifies that when Hive has unions encoded as structs with tag/field0/field1/...
    // and the Avro partner has the unions in their original form,
    // the merged schema correctly preserves nullability from the Avro union branches.
    // Tests both 2-way unions ["null", T] and 3-way unions ["null", T1, T2].
    // Expected: tag should be non-nullable, array items should be non-nullable,
    // and nested field nullability should be preserved from Avro.

    String hive = "struct<"
        + "twowayunion:struct<tag:int,field0:array<struct<name:string,value:bigint>>>,"
        + "threewayunion:struct<tag:int,field0:bigint,field1:array<struct<description:string,metadata:string>>>"
        + ">";

    // Avro partner schema with both 2-way and 3-way unions
    String avroSchemaLiteral = "{\"type\":\"record\",\"name\":\"TestRecord\",\"namespace\":\"test\",\"fields\":["
        // 2-way union: null or array
        + "{\"name\":\"twoWayUnion\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"TwoWayItem\",\"fields\":["
        + "{\"name\":\"name\",\"type\":\"string\"},"
        + "{\"name\":\"value\",\"type\":[\"null\",\"long\"]}"
        + "]}}]},"
        // 3-way union: null, long, or array
        + "{\"name\":\"threeWayUnion\",\"type\":[\"null\",\"long\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"ThreeWayItem\",\"fields\":["
        + "{\"name\":\"description\",\"type\":\"string\"},"
        + "{\"name\":\"metadata\",\"type\":[\"null\",\"string\"]}"
        + "]}}]}"
        + "]}";

    Schema avro = new Schema.Parser().parse(avroSchemaLiteral);
    Schema merged = merge(hive, avro);

    // ===== Test 2-way union =====
    Schema.Field twoWayField = merged.getField("twoWayUnion");
    assertNotNull(twoWayField, "twoWayUnion field should exist");
    Schema twoWayStruct = twoWayField.schema();
    assertEquals(twoWayStruct.getType(), Schema.Type.RECORD, "twoWayUnion should be a record (union encoded as struct)");

    // Verify tag field is non-nullable int
    Schema.Field twoWayTagField = twoWayStruct.getField("tag");
    assertNotNull(twoWayTagField, "tag field should exist in twoWayUnion");
    assertEquals(twoWayTagField.schema().getType(), Schema.Type.INT, "tag should be non-nullable int");

    // Extract field0 (the array branch)
    Schema.Field twoWayField0 = twoWayStruct.getField("field0");
    assertNotNull(twoWayField0, "field0 should exist in twoWayUnion");
    Schema twoWayArraySchema = SchemaUtilities.extractIfOption(twoWayField0.schema());
    assertEquals(twoWayArraySchema.getType(), Schema.Type.ARRAY, "field0 should be an array");

    // Verify array items are records (not ["null", record])
    Schema twoWayItemSchema = twoWayArraySchema.getElementType();
    assertEquals(twoWayItemSchema.getType(), Schema.Type.RECORD,
        "Array items should be records, not unions with null");

    // Verify nested field nullability: name is non-nullable, value is nullable
    assertEquals(twoWayItemSchema.getField("name").schema().getType(), Schema.Type.STRING,
        "name field should be non-nullable string");
    assertTrue(AvroSerdeUtils.isNullableType(twoWayItemSchema.getField("value").schema()),
        "value field should be nullable");

    // ===== Test 3-way union =====
    Schema.Field threeWayField = merged.getField("threeWayUnion");
    assertNotNull(threeWayField, "threeWayUnion field should exist");
    Schema threeWayStruct = threeWayField.schema();
    assertEquals(threeWayStruct.getType(), Schema.Type.RECORD,
        "threeWayUnion should be a record (union encoded as struct)");

    // Verify tag field is non-nullable int
    Schema.Field threeWayTagField = threeWayStruct.getField("tag");
    assertNotNull(threeWayTagField, "tag field should exist in threeWayUnion");
    assertEquals(threeWayTagField.schema().getType(), Schema.Type.INT, "tag should be non-nullable int");

    // Extract field1 (the array branch - field0 is the long branch)
    Schema.Field threeWayField1 = threeWayStruct.getField("field1");
    assertNotNull(threeWayField1, "field1 should exist in threeWayUnion");
    Schema threeWayArraySchema = SchemaUtilities.extractIfOption(threeWayField1.schema());
    assertEquals(threeWayArraySchema.getType(), Schema.Type.ARRAY, "field1 should be an array");

    // Verify array items are records (not ["null", record])
    Schema threeWayItemSchema = threeWayArraySchema.getElementType();
    assertEquals(threeWayItemSchema.getType(), Schema.Type.RECORD,
        "Array items should be records, not unions with null");

    // Verify nested field nullability: description is non-nullable, metadata is nullable
    assertEquals(threeWayItemSchema.getField("description").schema().getType(), Schema.Type.STRING,
        "description field should be non-nullable string");
    assertTrue(AvroSerdeUtils.isNullableType(threeWayItemSchema.getField("metadata").schema()),
        "metadata field should be nullable");
  }

  // TODO: tests to retain schema props
  // TODO: tests for explicit type compatibility check between hive and avro primitives, once we implement it
  // TODO: tests for error case => default value in Avro does not match with type from hive

  /** Test Helpers */

  private Schema union(Schema.Type... types) {
    return Schema.createUnion(Arrays.stream(types).map(Schema::create).collect(Collectors.toList()));
  }

  private void assertSchema(Schema expected, Schema actual) {
    assertEquals(actual.toString(true), expected.toString(true));
  }

  private Schema merge(StructTypeInfo typeInfo, Schema avro) {
    return MergeHiveSchemaWithAvro.visit(typeInfo, avro);
  }

  private Schema merge(String hive, Schema avro) {
    StructTypeInfo typeInfo = (StructTypeInfo) TypeInfoUtils.getTypeInfoFromTypeString(hive);
    return merge(typeInfo, avro);
  }

  private Schema struct(String name, String doc, String namespace, Schema.Field... fields) {
    Schema result = Schema.createRecord(name, doc, namespace, false);
    result.setFields(Arrays.asList(fields));

    return result;
  }

  private Schema struct(String name, Schema.Field... fields) {
    return struct(name, null, "n" + name, fields);
  }

  private Schema array(Schema element) {
    return Schema.createArray(element);
  }

  private Schema array(Schema.Type elementType) {
    return array(Schema.create(elementType));
  }

  private Schema map(Schema value) {
    return Schema.createMap(value);
  }

  private Schema map(Schema.Type valueType) {
    return map(Schema.create(valueType));
  }

  private Schema.Field nullable(Schema.Field field) {
    Preconditions.checkArgument(!AvroSerdeUtils.isNullableType(field.schema()));
    Map<String, String> props = new HashedMap();
    AvroCompatibilityHelper.getAllPropNames(field)
        .forEach(propName -> props.put(propName, AvroCompatibilityHelper.getFieldPropAsJsonString(field, propName)));
    return field(field.name(), nullable(field.schema()), field.doc(), null, props);
  }

  private Schema nullable(Schema schema) {
    return SchemaUtilities.makeNullable(schema, false);
  }

  private Schema nullable(Schema.Type type) {
    return nullable(Schema.create(type));
  }

  private Schema.Field field(String name, Schema schema, String doc, Object defaultValue, Map<String, String> props) {
    Schema.Field field = AvroCompatibilityHelper.createSchemaField(name, schema, doc, defaultValue);
    if (props != null) {
      props.forEach((propName, propValueInJson) -> AvroCompatibilityHelper.setFieldPropFromJsonString(field, propName,
          propValueInJson, false));
    }
    return field;
  }

  private Schema.Field required(String name, Schema schema, String doc, Object defaultValue,
      Map<String, String> props) {
    return field(name, schema, doc, defaultValue, props);
  }

  private Schema.Field required(String name, Schema schema) {
    return required(name, schema, null, null, null);
  }

  private Schema.Field required(String name, Schema.Type type, String doc, Object defaultValue,
      Map<String, String> props) {
    return required(name, Schema.create(type), doc, defaultValue, props);
  }

  private Schema.Field required(String name, Schema.Type type) {
    return required(name, type, null, null, null);
  }

  private Schema.Field optional(String name, Schema schema, String doc) {
    return nullable(field(name, schema, doc, null, null));
  }

  private Schema.Field optional(String name, Schema schema) {
    return optional(name, schema, null);
  }

  private Schema.Field optional(String name, Schema.Type type, String doc) {
    return optional(name, Schema.create(type), doc);
  }

  private Schema.Field optional(String name, Schema.Type type) {
    return optional(name, type, null);
  }
}
