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
import com.linkedin.coral.common.types.CoralTypeKind;
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
 *   <li>CoralDataType is the source of truth for field existence, name, and type.</li>
 *   <li><b>Nullability is always relaxed:</b> where CoralDataType and the partner disagree, the output
 *       is nullable. The merge never narrows. Iceberg's {@code required} flag is frequently inherited
 *       from a Hive migration rather than from an enforced constraint, so treating it as authoritative
 *       would drop a {@code null} branch the partner declared and produce a schema stricter than the
 *       data. This supersedes the earlier Iceberg-first nullability rule.</li>
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
        StructType structType = (StructType) coralType;
        if (isReconstructableUnionStruct(structType, partner)) {
          return mergeUnionStruct(structType, partner);
        }
        return mergeStruct(structType, partner);
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
    return applyRelaxedNullability(result, structType.isNullable(), partner);
  }

  /**
   * Decides whether {@code structType} is a Trino-style union-struct whose partner Avro is a genuine
   * multi-branch union, and must therefore be emitted as an Avro union rather than a record.
   *
   * <p>Iceberg's type system has no union type, so a Hive {@code uniontype<A,B,C>} that has been persisted
   * into Iceberg surfaces as the struct {@code {tag:INT, field0:A, field1:B, field2:C}} — the same encoding
   * {@link com.linkedin.coral.common.HiveToCoralTypeConverter#convertUnion} produces (Trino union
   * representation, see <a href="https://github.com/trinodb/trino/pull/3483">trinodb/trino#3483</a>). To
   * stay faithful to the legacy Hive Avro path ({@link MergeHiveSchemaWithAvro#union}), such a column must be
   * emitted as an Avro union rather than a record.
   *
   * <p>The struct shape alone is ambiguous — a genuine record could be named {@code {tag, field0, ...}} — so
   * two further conditions are required: the partner must be an Avro union, and its non-null branch count
   * must equal the number of {@code fieldN} members.
   *
   * <p>For two or more members the count check alone is conclusive, because a genuine nullable struct yields
   * {@code [null, record]} (one non-null branch) and can never match. A <em>single</em>-member union-struct
   * is genuinely ambiguous on counts, since both {@code uniontype<X>} and a nullable struct named
   * {@code {tag, field0}} present one non-null branch. The partner's sole branch breaks the tie: for a
   * genuine struct it is the record describing that struct, and so carries its own {@code tag} field, whereas
   * for {@code uniontype<X>} it is the member type {@code X}. Single unions must be reconstructed rather than
   * left as structs — {@link com.linkedin.coral.common.HiveToCoralTypeConverter#convertUnion} emits the
   * {@code {tag, field0}} encoding for every arity, so treating them as structs leaks that internal encoding
   * into the output schema.
   *
   * <p>The only residual ambiguity is {@code uniontype<struct<tag:...>>}, whose member is itself a struct
   * carrying a {@code tag} field; it is treated as a struct.
   */
  private boolean isReconstructableUnionStruct(StructType structType, @Nullable Schema partner) {
    if (partner == null || partner.getType() != Schema.Type.UNION || !isUnionStruct(structType)) {
      return false;
    }
    int memberCount = structType.getFields().size() - 1; // exclude the leading "tag" field
    List<Schema> nonNullBranches = SchemaUtilities.discardNullFromUnionIfExist(partner).getTypes();
    if (memberCount != nonNullBranches.size()) {
      return false;
    }
    if (memberCount >= 2) {
      return true;
    }
    return !describesStructItself(nonNullBranches.get(0));
  }

  /**
   * Whether a partner union branch is the record describing the union-struct itself — i.e. the partner says
   * "this really is a struct named {tag, field0}" — rather than the member type of a single
   * {@code uniontype<X>}. See {@link #isReconstructableUnionStruct}.
   */
  private boolean describesStructItself(Schema branch) {
    Schema extracted = SchemaUtilities.extractIfOption(branch);
    if (extracted.getType() != Schema.Type.RECORD) {
      return false;
    }
    for (Schema.Field field : extracted.getFields()) {
      if ("tag".equalsIgnoreCase(field.name())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recognizes the union-struct shape: a leading {@code tag} field of type INT followed by
   * {@code field0, field1, ..., fieldN-1} in order. See {@link #isReconstructableUnionStruct}.
   */
  private boolean isUnionStruct(StructType structType) {
    List<StructField> fields = structType.getFields();
    if (fields.size() < 2) {
      return false;
    }
    StructField tag = fields.get(0);
    // Names are matched case-insensitively: the union-struct encoding may arrive through catalogs that
    // normalize field casing, and a case-only mismatch must not silently downgrade a union to a record.
    if (!"tag".equalsIgnoreCase(tag.getName()) || tag.getType().getKind() != CoralTypeKind.INT) {
      return false;
    }
    for (int i = 1; i < fields.size(); i++) {
      if (!("field" + (i - 1)).equalsIgnoreCase(fields.get(i).getName())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Reconstructs an Avro union from a union-struct, merging each {@code fieldN} member against the
   * corresponding partner union branch (by ordinal). Caller ({@link #isReconstructableUnionStruct})
   * guarantees the member count matches the partner's non-null branch count.
   *
   * <p>The envelope is nullable when <em>either</em> the union-struct or the partner union says so,
   * matching the relaxed rule in {@link #applyRelaxedNullability}. {@code unionStruct.isNullable()} is
   * real signal here: only Iceberg-backed tables reach this class ({@code SchemaUtilities} delegates a
   * {@code HiveTable} to the Hive path), and {@code IcebergToCoralTypeConverter} passes Iceberg's
   * {@code isOptional()} straight through, so the flag reflects the Iceberg column rather than a
   * constant. The NULL branch is emitted first, matching {@link MergeHiveSchemaWithAvro#union}.
   *
   * <p>Member nullability is deliberately <em>not</em> relaxed into the envelope. Avro forbids a union
   * directly containing a union, and in the union-struct encoding only one member is live at a time, so
   * members are optional regardless of whether the union itself is nullable. Relaxing from them would
   * make every union nullable; each member therefore keeps its option wrapper stripped.
   */
  private Schema mergeUnionStruct(StructType unionStruct, Schema partnerUnion) {
    List<Schema> partnerBranches = SchemaUtilities.discardNullFromUnionIfExist(partnerUnion).getTypes();
    List<StructField> members = unionStruct.getFields(); // [tag, field0, field1, ...]
    List<Schema> unionTypes = new ArrayList<>();
    if (unionStruct.isNullable() || SchemaUtilities.nullExistInUnion(partnerUnion)) {
      unionTypes.add(Schema.create(Schema.Type.NULL));
    }
    for (int i = 1; i < members.size(); i++) {
      Schema branch = mergeType(members.get(i).getType(), partnerBranches.get(i - 1));
      // A union member is never itself nullable in Avro — the union's own NULL branch carries nullability,
      // so strip any option wrapper the recursive merge may have added around the member.
      unionTypes.add(SchemaUtilities.extractIfOption(branch));
    }
    return Schema.createUnion(unionTypes);
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
    return applyRelaxedNullability(result, arrayType.isNullable(), partner);
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
    return applyRelaxedNullability(result, mapType.isNullable(), partner);
  }

  private Schema mergeLeaf(CoralDataType coralType, @Nullable Schema partner) {
    // A Hive uniontype<X> whose Iceberg column was flattened to a plain field still arrives with a
    // partner of ["X"]. Merge against the sole branch so promotions still apply, then restore the
    // envelope below.
    Schema soleBranch = nullFreeSingleBranchOrNull(partner);
    Schema effectivePartner = soleBranch != null ? soleBranch : partner;
    Schema coralPrimitive = coralPrimitiveToAvro(coralType);
    Schema result =
        effectivePartner == null ? coralPrimitive : checkCompatibilityAndPromote(coralPrimitive, effectivePartner);
    Schema withNullability = applyRelaxedNullability(result, coralType.isNullable(), partner);
    if (soleBranch != null && withNullability.getType() != Schema.Type.UNION) {
      // Preserve the single-element union envelope the partner declared. Dropping it would lose the
      // fact that the column is a uniontype, which coalesce_struct and the Hive path both rely on.
      // When the field is nullable the envelope is already subsumed by ["null", X].
      List<Schema> envelope = new ArrayList<>();
      envelope.add(withNullability);
      return Schema.createUnion(envelope);
    }
    return withNullability;
  }

  /**
   * The sole branch of a partner that is a single-element union carrying no NULL ({@code ["int"]}), or
   * null for anything else. This is the Avro shape of a Hive {@code uniontype<X>} whose Iceberg column
   * has been flattened to a plain field, so the envelope must survive the merge.
   *
   * <p>{@code ["null","X"]} is deliberately excluded: it is simultaneously a plain nullable field and
   * the only representation of a nullable single union, so it needs no special handling.
   */
  @Nullable
  private Schema nullFreeSingleBranchOrNull(@Nullable Schema partner) {
    if (partner == null || partner.getType() != Schema.Type.UNION || partner.getTypes().size() != 1) {
      return null;
    }
    Schema branch = partner.getTypes().get(0);
    return branch.getType() == Schema.Type.NULL ? null : branch;
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
    // Default to micros for precision 6, 9, or unspecified.
    // (RelDataTypeToAvroType, the derived-expression path, instead defaults unspecified precision to
    // millis to preserve Hive-view output; reconcile deliberately if Hive ever moves onto this engine.)
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
   * Applies relaxed nullability: the result is nullable when <em>either</em> the Coral type or the
   * partner says so, and is never narrowed. Null placement follows the partner when it has one.
   *
   * <p>Consulting the partner is the whole point. Iceberg's {@code required} flag is often inherited
   * from a Hive migration rather than from an enforced constraint, so honouring it alone would drop a
   * {@code null} branch the partner declared and publish a schema stricter than the data — breaking
   * any consumer that has legitimate nulls.
   */
  private Schema applyRelaxedNullability(Schema result, boolean coralNullable, @Nullable Schema partner) {
    boolean partnerNullable = partner != null && isNullableType(partner);
    if ((coralNullable || partnerNullable) && !isNullableType(result)) {
      return SchemaUtilities.makeNullable(result, SchemaUtilities.isNullSecond(partner));
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
