/**
 * Copyright 2019-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;


public class SchemaUtilitiesTests {
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
    Schema nestedRecord1 = SchemaBuilder.record("FooRecord").namespace("com.foo.bar").fields().name("field1")
        .type().intType().noDefault().endRecord();

    // Create second nested record with the same name but different namespace "com.baz.qux"
    Schema nestedRecord2 = SchemaBuilder.record("FooRecord").namespace("com.baz.qux").fields().name("field2")
        .type().stringType().noDefault().endRecord();

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
    Assert.assertNotEquals(namespace1, namespace2,
        "Namespaces should be different to avoid conflicts. Got namespace1: " + namespace1 + ", namespace2: "
            + namespace2);

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
    Schema resultSchema =
        SchemaUtilities.setupNameAndNamespace(parentSchema, "ApplicationConfig", "com.app.config");

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
}
