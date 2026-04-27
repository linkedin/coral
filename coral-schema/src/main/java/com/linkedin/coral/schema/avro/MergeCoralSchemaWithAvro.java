/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

import com.linkedin.coral.common.types.ArrayType;
import com.linkedin.coral.common.types.BinaryType;
import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.DecimalType;
import com.linkedin.coral.common.types.MapType;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;
import com.linkedin.coral.common.types.TimestampType;

import static com.linkedin.coral.schema.avro.AvroSerdeUtils.*;


/**
 * Merges a Coral schema (from Iceberg) with a partner Avro schema, using Iceberg-first semantics:
 *
 * <ul>
 *   <li>CoralDataType is the source of truth for field existence, name, nullability, and type.</li>
 *   <li>Partner Avro contributes whatever metadata it carries for a matched field — defaults, docs,
 *       field props, aliases, union envelope shape and null placement, enum/fixed/uuid materialization.
 *       Nothing is fabricated: attributes the partner does not carry are absent from the output. When
 *       the partner is derived from a view schema spec ({name, type, default, doc}), only those four
 *       survive; when the partner is a full Avro table schema, the complete set is preserved.</li>
 *   <li>Fields only in the CoralDataType schema are included as optional; fields only in the partner
 *       are dropped. Count mismatches do not fail, matching {@link MergeHiveSchemaWithAvro}.</li>
 * </ul>
 *
 * Field matching is case-insensitive. Output field names use the CoralDataType casing. Iceberg's
 * schema spec disallows sibling fields that differ only in case, so this is unambiguous.
 */
class MergeCoralSchemaWithAvro {

  private final AtomicInteger recordCounter = new AtomicInteger(0);

  /**
   * Entry point: merge a top-level Coral StructType with a partner Avro schema.
   *
   * @param structType top-level Coral schema from CoralTable.getSchema()
   * @param partner partner Avro schema, or null if unavailable
   * @param recordName Avro record name for the top-level record
   * @param recordNamespace Avro record namespace for the top-level record
   * @return merged Avro schema
   */
  static Schema merge(StructType structType, @Nullable Schema partner, String recordName, String recordNamespace) {
    return new MergeCoralSchemaWithAvro().mergeTopLevelStruct(structType, partner, recordName, recordNamespace);
  }

  private Schema mergeTopLevelStruct(StructType structType, @Nullable Schema partner, String recordName,
      String recordNamespace) {
    Schema partnerRecord = extractPartnerRecord(partner);
    List<Schema.Field> fields = new ArrayList<>();

    for (StructField coralField : structType.getFields()) {
      Schema.Field partnerField = findPartnerField(partnerRecord, coralField.getName());
      Schema fieldSchema = mergeType(coralField.getType(), partnerField != null ? partnerField.schema() : null);
      fields.add(mergeField(coralField.getName(), coralField.getType(), partnerField, fieldSchema));
    }

    Schema result;
    if (partnerRecord != null) {
      result = SchemaUtilities.copyRecord(partnerRecord, fields);
    } else {
      result = Schema.createRecord(recordName, null, recordNamespace, false);
      result.setFields(fields);
    }
    // Top-level record is never wrapped in a nullable union — callers expect a record schema
    return result;
  }

  private Schema mergeType(CoralDataType coralType, @Nullable Schema partner) {
    switch (coralType.getKind()) {
      case STRUCT:
        return mergeStruct((StructType) coralType, partner);
      case ARRAY:
        return mergeArray((ArrayType) coralType, partner);
      case MAP:
        return mergeMap((MapType) coralType, partner);
      default:
        return mergeLeaf(coralType, partner);
    }
  }

  private Schema mergeStruct(StructType structType, @Nullable Schema partner) {
    Schema partnerRecord = extractPartnerRecord(partner);
    List<Schema.Field> fields = new ArrayList<>();

    for (StructField coralField : structType.getFields()) {
      Schema.Field partnerField = findPartnerField(partnerRecord, coralField.getName());
      Schema fieldSchema = mergeType(coralField.getType(), partnerField != null ? partnerField.schema() : null);
      fields.add(mergeField(coralField.getName(), coralField.getType(), partnerField, fieldSchema));
    }

    Schema result;
    if (partnerRecord != null) {
      result = SchemaUtilities.copyRecord(partnerRecord, fields);
    } else {
      int recordNum = recordCounter.incrementAndGet();
      result = Schema.createRecord("record" + recordNum, null, "namespace" + recordNum, false);
      result.setFields(fields);
    }
    return applyCoralNullability(result, structType.isNullable(), partner);
  }

  private Schema mergeArray(ArrayType arrayType, @Nullable Schema partner) {
    Schema partnerElement = null;
    if (partner != null) {
      Schema extracted = SchemaUtilities.extractIfOption(partner);
      if (extracted.getType() == Schema.Type.ARRAY) {
        partnerElement = extracted.getElementType();
      }
    }

    Schema elementSchema = mergeType(arrayType.getElementType(), partnerElement);
    Schema result = Schema.createArray(elementSchema);
    return applyCoralNullability(result, arrayType.isNullable(), partner);
  }

  private Schema mergeMap(MapType mapType, @Nullable Schema partner) {
    Schema partnerValue = null;
    if (partner != null) {
      Schema extracted = SchemaUtilities.extractIfOption(partner);
      if (extracted.getType() == Schema.Type.MAP) {
        partnerValue = extracted.getValueType();
      }
    }

    Schema valueSchema = mergeType(mapType.getValueType(), partnerValue);
    Schema result = Schema.createMap(valueSchema);
    return applyCoralNullability(result, mapType.isNullable(), partner);
  }

  private Schema mergeLeaf(CoralDataType coralType, @Nullable Schema partner) {
    Schema coralPrimitive = coralPrimitiveToAvro(coralType);
    Schema result = partner == null ? coralPrimitive : checkCompatibilityAndPromote(coralPrimitive, partner);
    return applyCoralNullability(result, coralType.isNullable(), partner);
  }

  /**
   * Converts a Coral primitive/leaf type to an Avro schema.
   * This is the Iceberg-first equivalent of hivePrimitiveToAvro in MergeHiveSchemaWithAvro.
   */
  private Schema coralPrimitiveToAvro(CoralDataType coralType) {
    switch (coralType.getKind()) {
      case BOOLEAN:
        return Schema.create(Schema.Type.BOOLEAN);
      case TINYINT:
      case SMALLINT:
      case INT:
        return Schema.create(Schema.Type.INT);
      case BIGINT:
        return Schema.create(Schema.Type.LONG);
      case FLOAT:
        return Schema.create(Schema.Type.FLOAT);
      case DOUBLE:
        return Schema.create(Schema.Type.DOUBLE);
      case STRING:
      case CHAR:
      case VARCHAR:
        return Schema.create(Schema.Type.STRING);
      case BINARY:
        return binaryToAvro((BinaryType) coralType);
      case NULL:
        return Schema.create(Schema.Type.NULL);
      case DATE:
        return LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT));
      case TIME:
        return LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG));
      case TIMESTAMP:
        return timestampToAvro((TimestampType) coralType);
      case DECIMAL:
        return decimalToAvro((DecimalType) coralType);
      default:
        throw new UnsupportedOperationException("Unsupported Coral type: " + coralType);
    }
  }

  private Schema binaryToAvro(BinaryType binaryType) {
    if (binaryType.isFixedLength()) {
      return Schema.createFixed("fixed" + binaryType.getLength(), null, null, binaryType.getLength());
    }
    return Schema.create(Schema.Type.BYTES);
  }

  private Schema timestampToAvro(TimestampType timestampType) {
    Schema schema = Schema.create(Schema.Type.LONG);
    if (timestampType.hasPrecision() && timestampType.getPrecision() <= 3) {
      return LogicalTypes.timestampMillis().addToSchema(schema);
    }
    // Default to micros for precision 6, 9, or unspecified
    return LogicalTypes.timestampMicros().addToSchema(schema);
  }

  private Schema decimalToAvro(DecimalType decimalType) {
    return LogicalTypes.decimal(decimalType.getPrecision(), decimalType.getScale())
        .addToSchema(Schema.create(Schema.Type.BYTES));
  }

  /**
   * If the Coral-derived type is compatible with a more specific partner Avro type
   * (BYTES→FIXED, STRING→ENUM, STRING with uuid logicalType), promote to the partner type.
   */
  private Schema checkCompatibilityAndPromote(Schema coralSchema, @Nullable Schema partner) {
    if (partner == null) {
      return coralSchema;
    }
    Schema extractedPartner = SchemaUtilities.extractIfOption(partner);
    switch (coralSchema.getType()) {
      case BYTES:
        if (extractedPartner.getType() == Schema.Type.FIXED) {
          return extractedPartner;
        }
        return coralSchema;
      case STRING:
        if (extractedPartner.getType() == Schema.Type.ENUM) {
          return extractedPartner;
        }
        // Preserve UUID logical type from partner
        if (extractedPartner.getType() == Schema.Type.STRING
            && "uuid".equals(extractedPartner.getProp(LogicalType.LOGICAL_TYPE_PROP))) {
          return extractedPartner;
        }
        return coralSchema;
      default:
        return coralSchema;
    }
  }

  /**
   * Merge a single field: canonical field name is the CoralDataType name; partner contributes
   * doc, default, ordering, and field props. The output field carries only {name, schema, doc,
   * default} plus any field-level props the partner already had — no aliases are introduced.
   * Case-insensitive resolution is the consumer's responsibility.
   */
  private Schema.Field mergeField(String coralFieldName, CoralDataType coralFieldType, @Nullable Schema.Field partner,
      Schema fieldSchema) {
    String safeCoralName = SchemaUtilities.makeCompatibleName(coralFieldName);
    if (partner == null) {
      return AvroCompatibilityHelper.createSchemaField(safeCoralName, fieldSchema, null, null);
    }
    // Avro requires the default value to match the first type in the option, reorder option if required.
    // e.g. fieldSchema is [null, int] and the partner's default value is 1 → reorder to [int, null] so
    // the default is compatible with the first branch.
    Schema reordered = SchemaUtilities.reorderOptionIfRequired(fieldSchema, SchemaUtilities.defaultValue(partner));
    Schema.Field merged = AvroCompatibilityHelper.createSchemaField(safeCoralName, reordered, partner.doc(),
        SchemaUtilities.defaultValue(partner), partner.order());
    SchemaUtilities.replicateFieldProps(partner, merged);
    return merged;
  }

  /**
   * Apply Coral nullability: if the Coral type is nullable, wrap the result in a nullable union.
   * Respects the partner's null placement order when available.
   */
  private Schema applyCoralNullability(Schema result, boolean coralNullable, @Nullable Schema partner) {
    if (coralNullable && !isNullableType(result)) {
      return SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner));
    }
    if (!coralNullable && isNullableType(result)) {
      return SchemaUtilities.extractIfOption(result);
    }
    return result;
  }

  /**
   * Extract the RECORD schema from a partner, unwrapping nullable unions.
   * Returns null if partner is null or not a record.
   */
  @Nullable
  private Schema extractPartnerRecord(@Nullable Schema partner) {
    if (partner == null) {
      return null;
    }
    Schema extracted = SchemaUtilities.extractIfOption(partner);
    return extracted.getType() == Schema.Type.RECORD ? extracted : null;
  }

  /**
   * Find a partner field by case-insensitive name match, returning the first match. Matches the
   * behavior of {@link MergeHiveSchemaWithAvro}'s partner accessor. Iceberg's schema spec disallows
   * sibling fields that differ only in case, so this is unambiguous for Iceberg-sourced Coral schemas.
   */
  @Nullable
  private Schema.Field findPartnerField(@Nullable Schema partnerRecord, String coralFieldName) {
    if (partnerRecord == null) {
      return null;
    }
    for (Schema.Field field : partnerRecord.getFields()) {
      if (field.name().equalsIgnoreCase(coralFieldName)) {
        return field;
      }
    }
    return null;
  }
}
