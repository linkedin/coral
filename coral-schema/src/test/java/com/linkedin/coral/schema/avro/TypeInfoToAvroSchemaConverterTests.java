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

  @Test
  public void shouldConvertDecimalWithTypicalValues() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(10, 5), "ns", "Test");
    // Hard-coded expected matches what Jackson IntNode serialization produces:
    // Jackson1Utils.toJsonString(factory.numberNode(N)) == String.valueOf(N),
    // and Avro's toString(true) renders integer props as  "key" : N  (no quotes around N).
    String expected = "{\n  \"type\" : \"bytes\",\n  \"logicalType\" : \"decimal\",\n"
        + "  \"precision\" : 10,\n  \"scale\" : 5\n}";
    Assert.assertEquals(actual.toString(true), expected);
  }

  @Test
  public void shouldConvertDecimalWithZeroScale() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(10, 0), "ns", "Test");
    String expected = "{\n  \"type\" : \"bytes\",\n  \"logicalType\" : \"decimal\",\n"
        + "  \"precision\" : 10,\n  \"scale\" : 0\n}";
    Assert.assertEquals(actual.toString(true), expected);
  }

  @Test
  public void shouldConvertDecimalWithMaxHivePrecision() {
    TypeInfoToAvroSchemaConverter converter = new TypeInfoToAvroSchemaConverter("ns", false);
    Schema actual = converter.convertTypeInfoToAvroSchema(TypeInfoFactory.getDecimalTypeInfo(38, 10), "ns", "Test");
    String expected = "{\n  \"type\" : \"bytes\",\n  \"logicalType\" : \"decimal\",\n"
        + "  \"precision\" : 38,\n  \"scale\" : 10\n}";
    Assert.assertEquals(actual.toString(true), expected);
  }
}
