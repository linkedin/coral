/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;


public class SchemaUtilitiesTests {
  @Test
  public void testCloneFieldList() {
    Schema dummySchema = SchemaBuilder.record("test").fields().name("a").type().intType().noDefault().endRecord();
    Schema.Field field1 =
        new Schema.Field("one", dummySchema, "", dummySchema.getJsonProp("key"), Schema.Field.Order.IGNORE);
    field1.addProp("field_key1", "field_value1");
    Schema.Field field2 =
        new Schema.Field("two", dummySchema, "", dummySchema.getJsonProp("key"), Schema.Field.Order.IGNORE);
    field2.addProp("field_key2", "field_value2");
    List<Schema.Field> originalList = new ArrayList<>();
    originalList.add(field1);
    originalList.add(field2);
    List<Schema.Field> resultList = SchemaUtilities.cloneFieldList(originalList);

    Assert.assertTrue(resultList.contains(field1));
    Assert.assertTrue(resultList.contains(field2));

    // Without props being identical, equal-check will not pass.
    // A dummy field3 with only property being different from field1
    Schema.Field field3 =
        new Schema.Field("one", dummySchema, "", dummySchema.getJsonProp("key"), Schema.Field.Order.IGNORE);
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
}
