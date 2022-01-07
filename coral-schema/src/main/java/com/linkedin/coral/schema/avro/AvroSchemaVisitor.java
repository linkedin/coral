/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.Deque;
import java.util.List;

import org.apache.avro.Schema;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.com.google.common.collect.Lists;


public abstract class AvroSchemaVisitor<T> {
  public static <T> T visit(Schema schema, AvroSchemaVisitor<T> visitor) {
    switch (schema.getType()) {
      case RECORD:
        // check to make sure this hasn't been visited before
        String name = schema.getFullName();
        Preconditions.checkState(!visitor.recordLevels.contains(name), "Cannot process recursive Avro record %s", name);

        visitor.recordLevels.push(name);

        List<Schema.Field> fields = schema.getFields();
        List<String> names = Lists.newArrayListWithExpectedSize(fields.size());
        List<T> results = Lists.newArrayListWithExpectedSize(fields.size());
        for (Schema.Field field : schema.getFields()) {
          names.add(field.name());
          T result = visitWithName(field.name(), field.schema(), visitor);
          results.add(result);
        }

        visitor.recordLevels.pop();

        return visitor.record(schema, names, results);

      case UNION:
        List<Schema> types = schema.getTypes();
        List<T> options = Lists.newArrayListWithExpectedSize(types.size());
        for (Schema type : types) {
          options.add(visit(type, visitor));
        }
        return visitor.union(schema, options);

      case ARRAY:
        // TODO: handle logical map
        return visitor.array(schema, visitWithName("element", schema.getElementType(), visitor));

      case MAP:
        return visitor.map(schema, visitWithName("value", schema.getValueType(), visitor));

      default:
        return visitor.primitive(schema);
    }
  }

  private final Deque<String> recordLevels = Lists.newLinkedList();
  private final Deque<String> fieldNames = Lists.newLinkedList();

  protected Deque<String> fieldNames() {
    return fieldNames;
  }

  private static <T> T visitWithName(String name, Schema schema, AvroSchemaVisitor<T> visitor) {
    try {
      visitor.fieldNames.addLast(name);
      return visit(schema, visitor);
    } finally {
      visitor.fieldNames.removeLast();
    }
  }

  public T record(Schema record, List<String> names, List<T> fields) {
    return null;
  }

  public T union(Schema union, List<T> options) {
    return null;
  }

  public T array(Schema array, T element) {
    return null;
  }

  public T map(Schema map, T value) {
    return null;
  }

  public T primitive(Schema primitive) {
    return null;
  }
}
