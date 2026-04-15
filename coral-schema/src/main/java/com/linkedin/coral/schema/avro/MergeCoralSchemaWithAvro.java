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

import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.avro.AvroSerDe;

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
 *   <li>Coral/Iceberg is the source of truth for field existence, name, nullability, and type</li>
 *   <li>Partner Avro provides metadata: defaults, docs, field props, aliases, enum/fixed/uuid materialization</li>
 *   <li>If partner Avro says a field is a union, the union envelope shape and null placement are preserved from Avro</li>
 *   <li>Fields only in Coral are included as optional; fields only in Avro are dropped</li>
 * </ul>
 *
 * Field matching: exact case-sensitive match first, then unique case-insensitive match.
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
  static Schema visit(StructType structType, @Nullable Schema partner, String recordName, String recordNamespace) {
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
      case DATE: {
        Schema dateSchema = Schema.create(Schema.Type.INT);
        dateSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DATE_TYPE_NAME);
        return dateSchema;
      }
      case TIME: {
        // time-micros: long with logicalType
        Schema timeSchema = Schema.create(Schema.Type.LONG);
        timeSchema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, "time-micros");
        return timeSchema;
      }
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
      schema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, "timestamp-millis");
    } else {
      // Default to micros for precision 6, 9, or unspecified
      schema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, "timestamp-micros");
    }
    return schema;
  }

  private Schema decimalToAvro(DecimalType decimalType) {
    Schema schema = Schema.create(Schema.Type.BYTES);
    schema.addProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE, AvroSerDe.DECIMAL_TYPE_NAME);
    AvroCompatibilityHelper.setSchemaPropFromJsonString(schema, AvroSerDe.AVRO_PROP_PRECISION,
        String.valueOf(decimalType.getPrecision()), false);
    AvroCompatibilityHelper.setSchemaPropFromJsonString(schema, AvroSerDe.AVRO_PROP_SCALE,
        String.valueOf(decimalType.getScale()), false);
    return schema;
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
            && "uuid".equals(extractedPartner.getProp(AvroSerDe.AVRO_PROP_LOGICAL_TYPE))) {
          return extractedPartner;
        }
        return coralSchema;
      default:
        return coralSchema;
    }
  }

  /**
   * Merge a single field: use Coral field name, apply partner metadata (docs, defaults, props).
   */
  private Schema.Field mergeField(String coralFieldName, CoralDataType coralFieldType, @Nullable Schema.Field partner,
      Schema fieldSchema) {
    if (partner == null) {
      return AvroCompatibilityHelper.createSchemaField(SchemaUtilities.makeCompatibleName(coralFieldName), fieldSchema,
          null, null);
    }
    Schema reordered = SchemaUtilities.reorderOptionIfRequired(fieldSchema, SchemaUtilities.defaultValue(partner));
    return SchemaUtilities.copyField(partner, reordered);
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
   * Find a partner field using the matching rules from the design doc:
   * 1. Exact case-sensitive match first
   * 2. If not found, unique case-insensitive match
   * 3. If case-insensitive match is ambiguous, treat as no match
   */
  @Nullable
  private Schema.Field findPartnerField(@Nullable Schema partnerRecord, String coralFieldName) {
    if (partnerRecord == null) {
      return null;
    }

    // 1. Exact case-sensitive match
    Schema.Field exact = partnerRecord.getField(coralFieldName);
    if (exact != null) {
      return exact;
    }

    // 2. Unique case-insensitive match
    Schema.Field caseInsensitiveMatch = null;
    int matchCount = 0;
    for (Schema.Field field : partnerRecord.getFields()) {
      if (field.name().equalsIgnoreCase(coralFieldName)) {
        caseInsensitiveMatch = field;
        matchCount++;
      }
    }

    // 3. Ambiguous → no match
    return matchCount == 1 ? caseInsensitiveMatch : null;
  }
}
