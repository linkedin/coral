/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.List;

import com.google.common.collect.Lists;
import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;


/**
 * Lowercase field names for Avro schema
 */
public class ToLowercaseSchemaVisitor extends AvroSchemaVisitor<Schema> {
  public static Schema visit(Schema schema) {
    return AvroSchemaVisitor.visit(schema, new ToLowercaseSchemaVisitor());
  }

  @Override
  public Schema record(Schema record, List<String> names, List<Schema> fields) {
    Schema lowercasedSchema =
        Schema.createRecord(record.getName().toLowerCase(), record.getDoc(), record.getNamespace(), record.isError());

    List<Schema.Field> lowercasedFields = Lists.newArrayListWithExpectedSize(fields.size());
    for (int i = 0; i < fields.size(); i++) {
      lowercasedFields.add(lowercaseField(record.getFields().get(i), fields.get(i)));
    }

    lowercasedSchema.setFields(lowercasedFields);
    SchemaUtilities.replicateSchemaProps(record, lowercasedSchema);

    return lowercasedSchema;
  }

  @Override
  public Schema union(Schema union, List<Schema> options) {
    List<Schema> unionOptions = Lists.newArrayListWithExpectedSize(options.size());
    for (Schema option : options) {
      unionOptions.add(option);
    }

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

  private Schema.Field lowercaseField(Schema.Field field, Schema schema) {
    Schema.Field lowercasedField = AvroCompatibilityHelper.createSchemaField(field.name().toLowerCase(), schema,
        field.doc(), SchemaUtilities.defaultValue(field), field.order());

    SchemaUtilities.replicateFieldProps(field, lowercasedField);

    return lowercasedField;
  }
}
