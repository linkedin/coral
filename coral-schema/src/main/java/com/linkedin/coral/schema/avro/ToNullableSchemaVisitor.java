/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.apache.avro.Schema;
import org.codehaus.jackson.JsonNode;


/**
 * Converts all the fields (including nested fields) to be nullable for a given schema
 */
public class ToNullableSchemaVisitor extends AvroSchemaVisitor<Schema> {
  public static Schema visit(Schema schema) {
    return SchemaUtilities.makeNonNullable(AvroSchemaVisitor.visit(schema, new ToNullableSchemaVisitor()));
  }

  @Override
  public Schema record(Schema record, List<String> names, List<Schema> fields) {
    Schema nullableSchema =
        Schema.createRecord(record.getName(), record.getDoc(), record.getNamespace(), record.isError());

    List<Schema.Field> nullableFields = Lists.newArrayListWithExpectedSize(fields.size());
    for (int i = 0; i < fields.size(); i++) {
      nullableFields.add(nullableField(record.getFields().get(i), fields.get(i)));
    }

    nullableSchema.setFields(nullableFields);
    SchemaUtilities.replicateSchemaProps(record, nullableSchema);

    return SchemaUtilities.makeNullable(nullableSchema);
  }

  @Override
  public Schema union(Schema union, List<Schema> options) {
    List<Schema> unionOptions = new ArrayList<>(options);
    return Schema.createUnion(unionOptions);
  }

  @Override
  public Schema array(Schema array, Schema element) {
    return Schema.createArray(element);
  }

  @Override
  public Schema map(Schema map, Schema value) {
    return Schema.createMap(value);
  }

  @Override
  public Schema primitive(Schema primitive) {
    return primitive;
  }

  private Schema.Field nullableField(Schema.Field field, Schema schema) {
    Schema.Field nullableField = new Schema.Field(field.name(), SchemaUtilities.makeNullable(schema), field.doc(),
        field.defaultValue(), field.order());

    for (Map.Entry<String, JsonNode> prop : field.getJsonProps().entrySet()) {
      nullableField.addProp(prop.getKey(), prop.getValue());
    }

    return nullableField;
  }
}
