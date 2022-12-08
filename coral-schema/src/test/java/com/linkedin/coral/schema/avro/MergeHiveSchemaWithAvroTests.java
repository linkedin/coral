/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
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
