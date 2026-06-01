/**
 * Copyright 2019-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.CoralTypeKind;
import com.linkedin.coral.common.types.PrimitiveType;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;
import com.linkedin.coral.schema.avro.exceptions.SchemaNotFoundException;


public class SchemaUtilitiesTests {

  private static final String SIMPLE_AVRO_SCHEMA =
      "{\"type\":\"record\",\"name\":\"TestRecord\",\"namespace\":\"com.test\","
          + "\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"}]}";

  @Test
  public void testGetCasePreservedSchemaFromPropertyMapsWithSchemaLiteral() {
    Map<String, String> tableProperties = new HashMap<>();
    tableProperties.put("avro.schema.literal", SIMPLE_AVRO_SCHEMA);

    Schema result = SchemaUtilities.getCasePreservedSchemaFromPropertyMaps(tableProperties, null, "test@table");

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "TestRecord");
    Assert.assertEquals(result.getFields().size(), 2);
  }

  @Test
  public void testGetCasePreservedSchemaFromPropertyMapsWithSerdeProperties() {
    Map<String, String> tableProperties = new HashMap<>();
    Map<String, String> serdeProperties = new HashMap<>();
    serdeProperties.put("avro.schema.literal", SIMPLE_AVRO_SCHEMA);

    Schema result =
        SchemaUtilities.getCasePreservedSchemaFromPropertyMaps(tableProperties, serdeProperties, "test@table");

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "TestRecord");
  }

  @Test
  public void testGetCasePreservedSchemaFromPropertyMapsTablePropsTakePrecedence() {
    String tableSchema = "{\"type\":\"record\",\"name\":\"FromTable\",\"namespace\":\"com.test\","
        + "\"fields\":[{\"name\":\"id\",\"type\":\"int\"}]}";
    String serdeSchema = "{\"type\":\"record\",\"name\":\"FromSerde\",\"namespace\":\"com.test\","
        + "\"fields\":[{\"name\":\"id\",\"type\":\"int\"}]}";

    Map<String, String> tableProperties = new HashMap<>();
    tableProperties.put("avro.schema.literal", tableSchema);
    Map<String, String> serdeProperties = new HashMap<>();
    serdeProperties.put("avro.schema.literal", serdeSchema);

    Schema result =
        SchemaUtilities.getCasePreservedSchemaFromPropertyMaps(tableProperties, serdeProperties, "test@table");

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "FromTable");
  }

  @Test
  public void testGetCasePreservedSchemaFromPropertyMapsWithDaliRowSchema() {
    Map<String, String> tableProperties = new HashMap<>();
    tableProperties.put("dali.row.schema", SIMPLE_AVRO_SCHEMA);

    Schema result = SchemaUtilities.getCasePreservedSchemaFromPropertyMaps(tableProperties, null, "test@table");

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "TestRecord");
    // dali.row.schema results are converted to nullable
    for (Schema.Field field : result.getFields()) {
      Assert.assertEquals(field.schema().getType(), Schema.Type.UNION);
    }
  }

  @Test
  public void testGetCasePreservedSchemaFromPropertyMapsReturnsNullWhenEmpty() {
    Map<String, String> tableProperties = new HashMap<>();

    Schema result = SchemaUtilities.getCasePreservedSchemaFromPropertyMaps(tableProperties, null, "test@table");

    Assert.assertNull(result);
  }

  @Test
  public void testGetAvroSchemaForCoralTableIcebergWithPartner() {
    StructType coralSchema = struct(field("id", intType(false)), field("name", stringType(true)));
    Map<String, String> properties = new HashMap<>();
    properties.put("avro.schema.literal", SIMPLE_AVRO_SCHEMA);

    CoralTable coralTable = new TestCoralTable("db.tbl", coralSchema, properties);
    Schema result = SchemaUtilities.getAvroSchemaForTable(coralTable, false);

    // Partner-provided record name preserved; merge engine populated both fields.
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "TestRecord");
    Assert.assertNotNull(result.getField("id"));
    Assert.assertNotNull(result.getField("name"));
  }

  @Test
  public void testGetAvroSchemaForCoralTableIcebergCoralNullabilityWins() {
    // Coral declares id as non-nullable; partner Avro declares id as nullable ([null,int]).
    // Iceberg-first semantics require the Coral nullability to win, which proves the merge
    // engine ran rather than the partner schema being passed through unchanged.
    StructType coralSchema = struct(field("id", intType(false)));
    String nullablePartner = "{\"type\":\"record\",\"name\":\"R\",\"namespace\":\"x\","
        + "\"fields\":[{\"name\":\"id\",\"type\":[\"null\",\"int\"],\"default\":null}]}";
    Map<String, String> properties = new HashMap<>();
    properties.put("avro.schema.literal", nullablePartner);

    CoralTable coralTable = new TestCoralTable("db.tbl", coralSchema, properties);
    Schema result = SchemaUtilities.getAvroSchemaForTable(coralTable, false);

    Assert.assertNotNull(result.getField("id"));
    Assert.assertFalse(AvroSerdeUtils.isNullableType(result.getField("id").schema()),
        "Coral nullability (non-nullable) must win over partner Avro nullability");
  }

  @Test
  public void testGetAvroSchemaForCoralTableIcebergNoPartnerNonStrict() {
    StructType coralSchema = struct(field("id", intType(false)), field("name", stringType(true)));

    CoralTable coralTable = new TestCoralTable("db.tbl", coralSchema, new HashMap<>());
    Schema result = SchemaUtilities.getAvroSchemaForTable(coralTable, false);

    // Pure Coral-derived schema: matches the legacy convertHiveSchemaToAvro shape — recordName is
    // the bare table name and recordNamespace is the fully qualified name.
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "tbl");
    Assert.assertEquals(result.getNamespace(), "db.tbl");
    Assert.assertNotNull(result.getField("id"));
    Assert.assertNotNull(result.getField("name"));
  }

  @Test(expectedExceptions = SchemaNotFoundException.class)
  public void testGetAvroSchemaForCoralTableIcebergNoPartnerStrictThrows() {
    StructType coralSchema = struct(field("id", intType(false)));
    CoralTable coralTable = new TestCoralTable("db.tbl", coralSchema, new HashMap<>());

    SchemaUtilities.getAvroSchemaForTable(coralTable, true);
  }

  @Test
  public void testGetAvroSchemaForCoralTableUnqualifiedNameUsesNameAsNamespace() {
    StructType coralSchema = struct(field("id", intType(false)));
    CoralTable coralTable = new TestCoralTable("flat", coralSchema, new HashMap<>());

    Schema result = SchemaUtilities.getAvroSchemaForTable(coralTable, false);

    // For an unqualified CoralTable name, recordName equals the name itself and
    // recordNamespace equals the fully qualified name (which is also "flat").
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getName(), "flat");
    Assert.assertEquals(result.getNamespace(), "flat");
  }

  /**
   * Minimal {@link CoralTable} test fixture. Used to exercise the non-Hive path of
   * {@link SchemaUtilities#getAvroSchemaForTable(CoralTable, boolean)} without requiring a real
   * Iceberg or Hive metastore. The Hive-delegation branch is covered by integration tests via
   * the existing {@code getAvroSchemaForTable(Table, boolean)} surface.
   */
  private static class TestCoralTable implements CoralTable {
    private final String name;
    private final StructType schema;
    private final Map<String, String> properties;

    TestCoralTable(String name, StructType schema, Map<String, String> properties) {
      this.name = name;
      this.schema = schema;
      this.properties = properties;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Map<String, String> properties() {
      return properties;
    }

    @Override
    public TableType tableType() {
      return TableType.TABLE;
    }

    @Override
    public CoralDataType getSchema() {
      return schema;
    }
  }

  private static StructField field(String name, CoralDataType type) {
    return StructField.of(name, type);
  }

  private static StructType struct(StructField... fields) {
    return StructType.of(Arrays.asList(fields), true);
  }

  private static PrimitiveType intType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.INT, nullable);
  }

  private static PrimitiveType stringType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.STRING, nullable);
  }

  @Test
  public void testCloneFieldList() {
    Schema dummySchema = SchemaBuilder.record("test").fields().name("a").type().intType().noDefault().endRecord();
    Schema.Field field1 = AvroCompatibilityHelper.createSchemaField("one", dummySchema, "", dummySchema.getProp("key"),
        Schema.Field.Order.IGNORE);
    field1.addProp("field_key1", "field_value1");
    Schema.Field field2 = AvroCompatibilityHelper.createSchemaField("two", dummySchema, "", dummySchema.getProp("key"),
        Schema.Field.Order.IGNORE);
    field2.addProp("field_key2", "field_value2");
    List<Schema.Field> originalList = new ArrayList<>();
    originalList.add(field1);
    originalList.add(field2);
    List<Schema.Field> resultList = SchemaUtilities.cloneFieldList(originalList);

    Assert.assertTrue(resultList.contains(field1));
    Assert.assertTrue(resultList.contains(field2));

    // Without props being identical, equal-check will not pass.
    // A dummy field3 with only property being different from field1
    Schema.Field field3 = AvroCompatibilityHelper.createSchemaField("one", dummySchema, "", dummySchema.getProp("key"),
        Schema.Field.Order.IGNORE);
    field3.addProp("field_key1", "random");
    Assert.assertFalse(resultList.contains(field3));
  }

  @Test
  public void testSchemaPropReplicate() {
    Schema dummySchema = SchemaBuilder.record("test").fields().name("a").type().intType().noDefault().endRecord();
    dummySchema.addProp("schema_key", "schema_value");
    Schema schemaReplicate = SchemaBuilder.record("test").fields().name("a").type().intType().noDefault().endRecord();
    Assert.assertNotEquals(schemaReplicate, dummySchema);
    SchemaUtilities.replicateSchemaProps(dummySchema, schemaReplicate);
    Assert.assertEquals(schemaReplicate, dummySchema);

    // Testing overwrite behavior: No overwrite should happen.
    Schema dummySchema2 = SchemaBuilder.record("test").fields().name("a").type().intType().noDefault().endRecord();
    dummySchema2.addProp("schema_key", "schema_value_overwrite");
    SchemaUtilities.replicateSchemaProps(dummySchema2, schemaReplicate);
    Assert.assertEquals(schemaReplicate.getProp("schema_key"), "schema_value");
  }

  @Test
  public void testHasDuplicateLowercaseColumnNames() {
    Schema schema = AvroCompatibilityHelper.parse(TestUtils.loadSchema("testHasDuplicateLowercaseColumnNames.avsc"));
    boolean hasDuplicateLowercaseColumnNames = SchemaUtilities.HasDuplicateLowercaseColumnNames.visit(schema);

    Assert.assertTrue(hasDuplicateLowercaseColumnNames);
  }

  @Test
  public void testNotHasDuplicateLowercaseColumnNames() {
    Schema schema = AvroCompatibilityHelper.parse(TestUtils.loadSchema("testNotHasDuplicateLowercaseColumnNames.avsc"));
    boolean hasDuplicateLowercaseColumnNames = SchemaUtilities.HasDuplicateLowercaseColumnNames.visit(schema);

    Assert.assertFalse(hasDuplicateLowercaseColumnNames);
  }

  @Test
  public void testForceLowercaseSchemaTrue() {
    Schema inputSchema = AvroCompatibilityHelper.parse(TestUtils.loadSchema("base-complex.avsc"));
    Schema outputSchema = ToLowercaseSchemaVisitor.visit(inputSchema);

    Assert.assertEquals(outputSchema.toString(true),
        TestUtils.loadSchema("testForceLowercaseSchemaTrue-expected.avsc"));
  }

  @Test
  public void testToNullableSchema() {
    Schema inputSchema = AvroCompatibilityHelper.parse(TestUtils.loadSchema("base-complex-non-nullable.avsc"));
    Schema outputSchema = ToNullableSchemaVisitor.visit(inputSchema);

    Assert.assertEquals(outputSchema.toString(true), TestUtils.loadSchema("testToNullableSchema-expected.avsc"));
  }

  /**
   * Test to verify that setupNameAndNamespace preserves original namespaces for nested records with the same name.
   * This prevents namespace collisions when two fields have nested records with the same name but different original namespaces.
   */
  @Test
  public void testSetupNameAndNamespacePreservesOriginalNamespace() {
    // Create first nested record with namespace "com.foo.bar"
    Schema nestedRecord1 = SchemaBuilder.record("FooRecord").namespace("com.foo.bar").fields().name("field1").type()
        .intType().noDefault().endRecord();

    // Create second nested record with the same name but different namespace "com.baz.qux"
    Schema nestedRecord2 = SchemaBuilder.record("FooRecord").namespace("com.baz.qux").fields().name("field2").type()
        .stringType().noDefault().endRecord();

    // Create nullable unions for both nested records
    Schema nullableRecord1 = Schema.createUnion(Schema.create(Schema.Type.NULL), nestedRecord1);
    Schema nullableRecord2 = Schema.createUnion(Schema.create(Schema.Type.NULL), nestedRecord2);

    // Create parent schema with two fields containing the nested records
    Schema parentSchema = SchemaBuilder.record("ParentRecord").namespace("com.parent").fields().name("contextV1")
        .type(nullableRecord1).noDefault().name("contextV2").type(nullableRecord2).noDefault().endRecord();

    // Apply setupNameAndNamespace
    Schema resultSchema = SchemaUtilities.setupNameAndNamespace(parentSchema, "ParentRecord", "com.parent.test");

    // Extract the nested record schemas from the result
    Schema.Field contextV1Field = resultSchema.getField("contextV1");
    Schema.Field contextV2Field = resultSchema.getField("contextV2");

    // Get the non-null type from the union
    Schema resultRecord1 = contextV1Field.schema().getTypes().get(1);
    Schema resultRecord2 = contextV2Field.schema().getTypes().get(1);

    // Verify that both records still have different namespaces (preserving original namespace info)
    // The new namespace should incorporate the original namespace to avoid conflicts
    String namespace1 = resultRecord1.getNamespace();
    String namespace2 = resultRecord2.getNamespace();

    // Both records have the same name
    Assert.assertEquals(resultRecord1.getName(), "FooRecord");
    Assert.assertEquals(resultRecord2.getName(), "FooRecord");

    // But they should have different namespaces with numeric suffixes to avoid conflicts
    Assert.assertNotEquals(namespace1, namespace2, "Namespaces should be different to avoid conflicts. Got namespace1: "
        + namespace1 + ", namespace2: " + namespace2);

    // Verify that numeric suffixes are appended to distinguish the colliding records
    Assert.assertTrue(namespace1.endsWith("-0") || namespace1.endsWith("-1"),
        "First record namespace should have numeric suffix. Got: " + namespace1);
    Assert.assertTrue(namespace2.endsWith("-0") || namespace2.endsWith("-1"),
        "Second record namespace should have numeric suffix. Got: " + namespace2);
  }

  /**
   * Test to verify that collision detection works for direct nested records (not in unions).
   * When two fields have direct nested records with the same name but different original namespaces,
   * the system should detect the collision and preserve the original namespaces.
   */
  @Test
  public void testSetupNameAndNamespaceDetectsDirectRecordCollisions() {
    // Create first nested record with namespace ending in "admin"
    Schema nestedRecord1 = SchemaBuilder.record("ConfigRecord").namespace("com.foo.admin").fields().name("setting1")
        .type().intType().noDefault().endRecord();

    // Create second nested record with the same name but namespace ending in "client"
    Schema nestedRecord2 = SchemaBuilder.record("ConfigRecord").namespace("com.bar.client").fields().name("setting2")
        .type().stringType().noDefault().endRecord();

    // Create parent schema with two fields containing the nested records directly (NOT in unions)
    Schema parentSchema = SchemaBuilder.record("ApplicationConfig").namespace("com.app").fields().name("serviceConfig1")
        .type(nestedRecord1).noDefault().name("serviceConfig2").type(nestedRecord2).noDefault().endRecord();

    // Apply setupNameAndNamespace
    Schema resultSchema = SchemaUtilities.setupNameAndNamespace(parentSchema, "ApplicationConfig", "com.app.config");

    // Extract the nested record schemas from the result
    Schema.Field config1Field = resultSchema.getField("serviceConfig1");
    Schema.Field config2Field = resultSchema.getField("serviceConfig2");

    Schema resultRecord1 = config1Field.schema();
    Schema resultRecord2 = config2Field.schema();

    // Verify that both records still have different namespaces (collision was detected and handled)
    String namespace1 = resultRecord1.getNamespace();
    String namespace2 = resultRecord2.getNamespace();

    // Both records have the same name
    Assert.assertEquals(resultRecord1.getName(), "ConfigRecord");
    Assert.assertEquals(resultRecord2.getName(), "ConfigRecord");

    // But they should have different namespaces with numeric suffixes because collision was detected
    Assert.assertNotEquals(namespace1, namespace2,
        "Namespaces should be different when collision is detected. Got namespace1: " + namespace1 + ", namespace2: "
            + namespace2);

    // Verify that numeric suffixes are appended to distinguish the colliding records
    Assert.assertTrue(namespace1.endsWith("-0") || namespace1.endsWith("-1"),
        "First record namespace should have numeric suffix. Got: " + namespace1);
    Assert.assertTrue(namespace2.endsWith("-0") || namespace2.endsWith("-1"),
        "Second record namespace should have numeric suffix. Got: " + namespace2);
  }

  /**
   * Test to verify that collision detection works for deeply nested records.
   * This reproduces the real-world scenario where a record with the same name appears twice with different namespaces,
   * but both are nested inside an intermediate record, which is itself nested in the parent.
   * 
   * Schema structure:
   * ParentRecord (top-level)
   *   └─ intermediateField (IntermediateRecord - contains the colliding records)
   *       ├─ collidingField1 (CollidingRecord from com.foo.v1 namespace)
   *       └─ collidingField2 (CollidingRecord from com.bar.v2 namespace)
   */
  @Test
  public void testSetupNameAndNamespaceDetectsDeeplyNestedCollisions() {
    // Create two "CollidingRecord" records with the same name but different namespaces
    // These represent the deeply nested records that will collide
    Schema collidingRecord1 = SchemaBuilder.record("CollidingRecord").namespace("com.foo.v1").fields().name("field1")
        .type().stringType().noDefault().endRecord();

    Schema collidingRecord2 = SchemaBuilder.record("CollidingRecord").namespace("com.bar.v2").fields().name("field2")
        .type().intType().noDefault().endRecord();

    // Create a "Metadata" record that appears at a different hierarchical level
    // This should NOT get a suffix since it's not colliding with anything at its level
    Schema metadataRecord = SchemaBuilder.record("Metadata").namespace("com.original").fields().name("version").type()
        .stringType().noDefault().endRecord();

    // Create an intermediate record that contains both colliding records
    // This represents the middle layer in the nesting hierarchy
    Schema intermediateRecord = SchemaBuilder.record("IntermediateRecord").namespace("com.intermediate").fields()
        .name("collidingField1").type(collidingRecord1).noDefault().name("collidingField2").type(collidingRecord2)
        .noDefault().name("metadata").type(metadataRecord).noDefault().endRecord();

    // Create top-level parent schema that contains the intermediate record
    Schema parentSchema = SchemaBuilder.record("ParentRecord").namespace("com.parent").fields()
        .name("intermediateField").type(intermediateRecord).noDefault().endRecord();

    // Apply setupNameAndNamespace
    Schema resultSchema = SchemaUtilities.setupNameAndNamespace(parentSchema, "ParentRecord", "com.result");

    // Navigate to the deeply nested colliding records
    Schema.Field intermediateField = resultSchema.getField("intermediateField");
    Schema intermediateSchema = intermediateField.schema();

    Schema.Field collidingField1 = intermediateSchema.getField("collidingField1");
    Schema.Field collidingField2 = intermediateSchema.getField("collidingField2");
    Schema.Field metadataField = intermediateSchema.getField("metadata");

    Schema resultColliding1 = collidingField1.schema();
    Schema resultColliding2 = collidingField2.schema();
    Schema resultMetadata = metadataField.schema();

    String namespace1 = resultColliding1.getNamespace();
    String namespace2 = resultColliding2.getNamespace();
    String metadataNamespace = resultMetadata.getNamespace();

    // Both colliding records have the same name
    Assert.assertEquals(resultColliding1.getName(), "CollidingRecord");
    Assert.assertEquals(resultColliding2.getName(), "CollidingRecord");

    // But they should have different namespaces with numeric suffixes because collision was detected
    Assert.assertNotEquals(namespace1, namespace2,
        "Namespaces should be different when collision is detected in deeply nested records. Got namespace1: "
            + namespace1 + ", namespace2: " + namespace2);

    // Verify that numeric suffixes are appended to distinguish the colliding records
    Assert.assertTrue(namespace1.endsWith("-0") || namespace1.endsWith("-1"),
        "First colliding record namespace should have numeric suffix. Got: " + namespace1);
    Assert.assertTrue(namespace2.endsWith("-0") || namespace2.endsWith("-1"),
        "Second colliding record namespace should have numeric suffix. Got: " + namespace2);

    // Verify that the non-colliding Metadata record does NOT have a numeric suffix
    Assert.assertEquals(resultMetadata.getName(), "Metadata");
    Assert.assertFalse(metadataNamespace.matches(".*-\\d+$"),
        "Metadata record should NOT have numeric suffix since it's not colliding at its level. Got: "
            + metadataNamespace);
    Assert.assertTrue(metadataNamespace.contains("IntermediateRecord"),
        "Metadata namespace should follow hierarchical naming. Got: " + metadataNamespace);
  }
}
