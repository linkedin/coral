/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
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
}
