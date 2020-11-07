/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.calcite.rel.type.DynamicRecordType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.MultisetSqlType;
import org.apache.calcite.sql.type.SqlTypeName;


public class RelDataTypeToAvro {
  public static final String DEFAULT_RECORD_NAME = "record";

  private int recordId;

  public RelDataTypeToAvro() {
    recordId = 0;
  }

  public Schema convertRelDataType(RelDataType relType) {
    return convertRelDataType(relType, null);
  }

  public Schema convertRelDataType(RelDataType relType, String recName) {
    final Schema avroSchema = convertRelDataTypeNoneNull(relType, recName);
    if (relType.isNullable() && avroSchema.getType() != Schema.Type.NULL) {
      return Schema.createUnion(Arrays.asList(avroSchema, Schema.create(Schema.Type.NULL)));
    }
    return avroSchema;
  }

  public Schema convertRelDataTypeNoneNull(RelDataType relType, String recName) {
    if (relType instanceof RelRecordType || relType instanceof DynamicRecordType) {
      return convertRecord(relType, recName);
    }
    if (relType instanceof BasicSqlType) {
      return convertBasicSqlType((BasicSqlType) relType);
    }
    if (relType instanceof MultisetSqlType || relType instanceof ArraySqlType) {
      return Schema.createArray(convertRelDataType(relType.getComponentType(), recName));
    }
    if (relType instanceof MapSqlType) {
      final MapSqlType mapSqlType = (MapSqlType) relType;
      if (!SqlTypeName.CHAR_TYPES.contains(mapSqlType.getKeyType().getSqlTypeName())) {
        throw new UnsupportedOperationException("Key of Map can only be a String: " + mapSqlType.getKeyType().getSqlTypeName().getName());
      }
      return Schema.createMap(convertRelDataType(mapSqlType.getValueType(), recName));
    }

    throw new UnsupportedOperationException("Unsupported RelDataType from class: " + relType.getClass().toString());
  }

  private Schema convertBasicSqlType(BasicSqlType relType) {
    switch (relType.getSqlTypeName()) {
      case BOOLEAN:
        return Schema.create(Schema.Type.BOOLEAN);
      case TINYINT:
      case INTEGER:
        return Schema.create(Schema.Type.INT);
      case BIGINT:
        return Schema.create(Schema.Type.LONG);
      case FLOAT:
        return Schema.create(Schema.Type.FLOAT);
      case DOUBLE:
        return Schema.create(Schema.Type.DOUBLE);
      case VARCHAR:
      case CHAR:
        return Schema.create(Schema.Type.STRING);
      case BINARY:
        return Schema.create(Schema.Type.BYTES);
      case NULL:
        return Schema.create(Schema.Type.NULL);
      case ANY:
        return Schema.create(Schema.Type.BYTES);
      default:
        throw new UnsupportedOperationException(relType.getSqlTypeName() + " is not supported.");
    }
  }

  public Schema convertRecord(RelDataType relRecord) {
    return convertRecord(relRecord, null);
  }

  public Schema convertRecord(RelDataType relRecord,  String name) {
    return convertRecord(relRecord, null, name, "rel_avro", null);
  }

  public Schema convertRecord(RelDataType relRecord, List<String> columnComments, String name, String namespace,
      String doc) {
    final List<Field> fields = new ArrayList();
    String schemaName = name != null &&  !name.isEmpty() ? name : DEFAULT_RECORD_NAME;
    for (RelDataTypeField relField : relRecord.getFieldList()) {
      final String comment = columnComments != null && columnComments.size() > relField.getIndex() ? columnComments.get(
          relField.getIndex()) : null;
      final String fieldSchemaName = schemaName + "_" + toAvroQualifedName(relField.getName());
      fields.add(new Field(toAvroQualifedName(relField.getName()), convertRelDataType(relField.getType(), fieldSchemaName), comment, null));
    }

    final Schema avroSchema = Schema.createRecord(schemaName, doc, namespace, false);
    avroSchema.setFields(fields);
    return avroSchema;
  }

  public static String toAvroQualifedName(String relName) {
    return relName.replace("$", "_");
  }
}
