/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;


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

    return SchemaUtilities.makeNullable(nullableSchema, false);
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
    final Object defaultValue = SchemaUtilities.defaultValue(field);
    // While calling `makeNullable` method, if the default value is not null, we need to put null as the second option
    // i.e. schema = int, defaultValue = 1, the resultant schema should be [int, null] rather than [null, int]
    Schema.Field nullableField = AvroCompatibilityHelper.createSchemaField(field.name(),
        SchemaUtilities.makeNullable(schema, defaultValue != null), field.doc(), defaultValue, field.order());

    SchemaUtilities.replicateFieldProps(field, nullableField);
    return nullableField;
  }
}
