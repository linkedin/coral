/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


public class TypeInfoToAvroSchemaConverterTests {

  // Asserts that precision and scale appear as JSON integers (not quoted strings) in the schema.
  // The expected strings match what Jackson IntNode serialization produces:
  //   Jackson1Utils.toJsonString(factory.numberNode(N)) == String.valueOf(N),
  // and Avro's toString(true) renders integer props as  "key" : N  (no quotes around N).
  private static void assertDecimalProps(Schema schema, int expectedPrecision, int expectedScale) {
    String json = schema.toString(true);
    Assert.assertTrue(json.contains("\"logicalType\" : \"decimal\""), "Expected logicalType decimal in: " + json);
    Assert.assertTrue(json.contains("\"precision\" : " + expectedPrecision),
        "Expected integer precision " + expectedPrecision + " in: " + json);
    Assert.assertTrue(json.contains("\"scale\" : " + expectedScale),
        "Expected integer scale " + expectedScale + " in: " + json);
  }

  @Test
  public void shouldConvertDecimalWithTypicalValues() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(10, 5), "ns", "Test");
    Assert.assertEquals(actual.getType(), Schema.Type.BYTES);
    assertDecimalProps(actual, 10, 5);
  }

  @Test
  public void shouldConvertDecimalWithZeroScale() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(10, 0), "ns", "Test");
    Assert.assertEquals(actual.getType(), Schema.Type.BYTES);
    assertDecimalProps(actual, 10, 0);
  }

  @Test
  public void shouldConvertDecimalWithMaxHivePrecision() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(38, 10), "ns", "Test");
    Assert.assertEquals(actual.getType(), Schema.Type.BYTES);
    assertDecimalProps(actual, 38, 10);
  }
}
