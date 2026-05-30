/**
 * Copyright 2019-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.calcite.rel.type.DynamicRecordType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.sql.type.ArraySqlType;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.MapSqlType;
import org.apache.calcite.sql.type.MultisetSqlType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.serde2.avro.AvroSerDe;

import com.linkedin.coral.com.google.common.base.Preconditions;


/**
 * This class provides RelDataType to avro data type mapping
 *
 * relDataTypeToAvroType is the main API
 */
class RelDataTypeToAvroType {
  // private constructor for utility class
  private RelDataTypeToAvroType() {
  }

  /**
   * This method converts RelDataType to avro data type
   *
   * The return schema is always non nullable (instead of [null, type]) since the nullability if decided
   * out of this method
   *
   * @param relDataType
   * @return Schema of Avro data type corresponding to input RelDataType
   */
  static Schema relDataTypeToAvroTypeNonNullable(@Nonnull RelDataType relDataType, String recordName) {
    Preconditions.checkNotNull(relDataType);

    if (relDataType instanceof RelRecordType || relDataType instanceof DynamicRecordType) {
      String uniqueNamespace = getUniqueNamespace(relDataType, recordName);
      return relRecordTypeToAvroType(relDataType, null, recordName, uniqueNamespace, null);
    }

    if (relDataType instanceof BasicSqlType) {
      return basicSqlTypeToAvroType((BasicSqlType) relDataType);
    }

    if (relDataType instanceof MultisetSqlType || relDataType instanceof ArraySqlType) {
      return Schema.createArray(relDataTypeToAvroType(relDataType.getComponentType(), recordName));
    }

    if (relDataType instanceof MapSqlType) {
      final MapSqlType mapSqlType = (MapSqlType) relDataType;
      if (SqlTypeName.NULL == mapSqlType.getKeyType().getSqlTypeName()
          && SqlTypeName.NULL == mapSqlType.getValueType().getSqlTypeName()) {
        return Schema.createMap(SchemaUtilities.makeNullable(Schema.create(Schema.Type.STRING), false));
      }
      if (!SqlTypeName.CHAR_TYPES.contains(mapSqlType.getKeyType().getSqlTypeName())) {
        throw new UnsupportedOperationException(
            "Key of Map can only be a String: " + mapSqlType.getKeyType().getSqlTypeName().getName());
      }
      return Schema.createMap(relDataTypeToAvroType(mapSqlType.getValueType(), recordName));
    }

    throw new UnsupportedOperationException(
        "Unsupported RelDataType to be converted to Avro type: " + relDataType.toString());
  }

  private static Schema relDataTypeToAvroType(RelDataType relDataType, String recordName) {
    final Schema avroSchema = relDataTypeToAvroTypeNonNullable(relDataType, recordName);
    // TODO: Current logic ALWAYS sets the inner fields of RelDataType record nullable.
    //  Modify this to be applied only when RelDataType record was generated from a HIVE_UDF RexCall
    return SchemaUtilities.makeNullable(avroSchema, false);
  }

  private static Schema basicSqlTypeToAvroType(BasicSqlType relDataType) {
    Schema schema;
    switch (relDataType.getSqlTypeName()) {
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
        // Fixed-length BINARY(n) (e.g. an Iceberg FixedType) carries an explicit positive length and
        // maps to an Avro fixed. Unbounded BINARY (Hive binary, Iceberg variable-length binary)
        // reports PRECISION_NOT_SPECIFIED (-1), so the same > 0 check leaves it as bytes.
        if (relDataType.getPrecision() > 0) {
          return Schema.createFixed("fixed" + relDataType.getPrecision(), null, null, relDataType.getPrecision());
        }
        return Schema.create(Schema.Type.BYTES);
      case NULL:
        return Schema.create(Schema.Type.NULL);
      case TIMESTAMP:
        // Precision > 3 (e.g. Iceberg's TIMESTAMP(6), microsecond resolution) maps to timestamp-micros.
        // Unspecified precision (Hive timestamps, PRECISION_NOT_SPECIFIED == -1) and precision <= 3
        // stay timestamp-millis, preserving the existing Hive-path behavior.
        // Note: MergeCoralSchemaWithAvro.timestampToAvro defaults unspecified precision to
        // timestamp-micros instead. The two cannot collide today (Iceberg is always TIMESTAMP(6), and
        // Hive tables route through MergeHiveSchemaWithAvro); a future Hive-on-Coral consolidation
        // should reconcile these defaults deliberately.
        schema = Schema.create(Schema.Type.LONG);
        schema.addProp("logicalType", relDataType.getPrecision() > 3 ? "timestamp-micros" : "timestamp-millis");
        return schema;
      case TIME:
        // Iceberg TIME is microsecond resolution -- "time: Time of day, microsecond precision,
        // without date, timezone" (https://iceberg.apache.org/spec/#primitive-types) -- so Avro
        // represents it as a long with time-micros.
        schema = Schema.create(Schema.Type.LONG);
        schema.addProp("logicalType", "time-micros");
        return schema;
      case DATE:
        // In Avro, "date" type is represented as {"type": "int", "logicalType": "date"}.
        schema = Schema.create(Schema.Type.INT);
        schema.addProp("logicalType", "date");
        return schema;
      case DECIMAL:
        Schema decimalSchema = Schema.create(Schema.Type.BYTES);
        decimalSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DECIMAL_TYPE_NAME);
        AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_PRECISION,
            String.valueOf(relDataType.getPrecision()), false);
        AvroCompatibilityHelper.setSchemaPropFromJsonString(decimalSchema, AvroSerDe.AVRO_PROP_SCALE,
            String.valueOf(relDataType.getScale()), false);
        return decimalSchema;
      default:
        throw new UnsupportedOperationException(relDataType.getSqlTypeName() + " is not supported.");
    }
  }

  /**
   * This method converts RelRecordType or DynamicRecordType to Avro type
   *
   * It iterates each RelDataTypeField in the field list of record type and convert them recursively
   *
   * @param relRecord RelRecordType or DynamicRecordType to convert to Avro Record type
   * @param fieldComments documentations of sub-fields in Avro Record type
   * @param recordName record name of Avro Record type
   * @param recordNamespace record namespace of Avro Record type
   * @param doc documentation of Avro Record type
   * @return Avro type corresponding to RelDataType
   */
  private static Schema relRecordTypeToAvroType(RelDataType relRecord, List<String> fieldComments, String recordName,
      String recordNamespace, String doc) {
    final List<Schema.Field> fields = new ArrayList<>();
    final Schema avroSchema = Schema.createRecord(recordName, doc, recordNamespace, false);

    for (RelDataTypeField relField : relRecord.getFieldList()) {
      final String comment = fieldComments != null && fieldComments.size() > relField.getIndex()
          ? fieldComments.get(relField.getIndex()) : null;
      fields.add(AvroCompatibilityHelper.createSchemaField(toAvroQualifiedName(relField.getName()),
          relDataTypeToAvroType(relField.getType(), toAvroQualifiedName(relField.getName())), comment, null));
    }

    avroSchema.setFields(fields);
    return avroSchema;
  }

  private static String getUniqueNamespace(RelDataType relDataType, String recordName) {
    if (relDataType.getFieldList() == null || relDataType.getFieldCount() == 0) {
      return "rel_avro";
    }
    List<RelDataTypeField> fields = relDataType.getFieldList();

    if (fields.stream().anyMatch(field -> field.getName().equals(recordName))) {
      String interim_namespace = fields.stream().map(RelDataTypeField::getName).collect(Collectors.joining("_"));
      return "namespace_from_nested_fields_" + interim_namespace;
    } else {
      return "rel_avro";
    }
  }

  private static String toAvroQualifiedName(String relName) {
    return relName.replace("$", "_");
  }
}
