/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.Arrays;
import java.util.Map;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.types.ArrayType;
import com.linkedin.coral.common.types.BinaryType;
import com.linkedin.coral.common.types.CoralTypeKind;
import com.linkedin.coral.common.types.DecimalType;
import com.linkedin.coral.common.types.MapType;
import com.linkedin.coral.common.types.PrimitiveType;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;
import com.linkedin.coral.common.types.TimestampType;

import static org.testng.Assert.*;


public class MergeCoralSchemaWithAvroTests {

  @Test
  public void shouldUseFieldNamesFromCoral() {
    StructType coral = struct(field("fA", intType(true)), field("fB", struct(field("gA", intType(true)))));
    Schema avro = avroStruct("r1", optionalField("fa", Schema.Type.INT),
        optionalField("fb", avroStruct("r2", optionalField("ga", Schema.Type.INT))));

    Schema result = merge(coral, avro);
    // Iceberg-first: only the Coral casing appears in the output; the partner casing is dropped.
    // Case-insensitive resolution is the consumer's responsibility.
    assertNotNull(result.getField("fA"));
    assertNull(result.getField("fa"));
    assertNotNull(result.getField("fB"));
    assertNull(result.getField("fb"));
    // Same rule applies to nested fields.
    Schema fbRecord = SchemaUtilities.extractIfOption(result.getField("fB").schema());
    assertNotNull(fbRecord.getField("gA"));
    assertNull(fbRecord.getField("ga"));
  }

  @Test
  public void shouldRelaxNullabilityWhenEitherSourceSaysNullable() {
    StructType coral = struct(field("fA", intType(false)), field("fB", intType(true)));
    Schema avro = avroStruct("r1", optionalField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // Coral says fA is required but the partner declares [null,int]: nullability is always relaxed, so
    // the partner's null branch survives rather than being dropped.
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fA").schema()));
    // Coral says fB is nullable while the partner says required: relaxed the other way round.
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldKeepFieldRequiredWhenNeitherSourceSaysNullable() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // Relaxing never fabricates nullability: with both sources agreeing the field stays required.
    assertFalse(AvroSerdeUtils.isNullableType(result.getField("fA").schema()));
  }

  @Test
  public void shouldUseTypesFromCoral() {
    StructType coral =
        struct(field("fA", struct(field("gA", intType(true)))), field("fB", ArrayType.of(intType(true), false)),
            field("fC", MapType.of(stringType(false), intType(true), false)), field("fD", stringType(false)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT),
        requiredField("fC", Schema.Type.INT), requiredField("fD", Schema.Type.INT));

    Schema result = merge(coral, avro);
    // fA should be a record, not INT
    assertEquals(SchemaUtilities.extractIfOption(result.getField("fA").schema()).getType(), Schema.Type.RECORD);
    // fB should be an array
    assertEquals(result.getField("fB").schema().getType(), Schema.Type.ARRAY);
    // fC should be a map
    assertEquals(result.getField("fC").schema().getType(), Schema.Type.MAP);
    // fD should be a string
    assertEquals(result.getField("fD").schema().getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldIgnoreExtraFieldsFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT), requiredField("fB", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getFields().size(), 1);
    assertNotNull(result.getField("fA"));
  }

  @Test
  public void shouldRetainExtraFieldsFromCoral() {
    StructType coral = struct(field("fA", intType(false)), field("fB", intType(true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getFields().size(), 2);
    assertNotNull(result.getField("fA"));
    // fB is extra from Coral, should be optional with sanitized name
    assertNotNull(result.getField("fB"));
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldRetainDocStringsFromAvro() {
    StructType coral = struct(field("fA", intType(true)));
    Schema avro =
        avroStruct("r1", "doc-r1", "n1", avroField("fA", Schema.create(Schema.Type.INT), "doc-fA", null, null));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fA").doc(), "doc-fA");
  }

  @Test
  public void shouldRetainDefaultValuesFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro = avroStruct("r1", avroField("fA", Schema.create(Schema.Type.INT), null, 42, null));

    Schema result = merge(coral, avro);
    assertTrue(AvroCompatibilityHelper.fieldHasDefault(result.getField("fA")));
  }

  @Test
  public void shouldRetainFieldPropsFromAvro() {
    StructType coral = struct(field("fA", intType(false)));
    Schema avro =
        avroStruct("r1", requiredAvroField("fA", Schema.Type.INT, null, null, ImmutableMap.of("myProp", "myValue")));

    Schema result = merge(coral, avro);
    assertEquals(AvroCompatibilityHelper.getFieldPropAsJsonString(result.getField("fA"), "myProp"), "\"myValue\"");
  }

  @Test
  public void shouldHandleArrays() {
    StructType coral = struct(field("fA", ArrayType.of(intType(false), false)),
        field("fB", ArrayType.of(intType(true), true)), field("fC", ArrayType.of(intType(true), true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.createArray(Schema.create(Schema.Type.INT))),
        optionalField("fB", Schema.createArray(Schema.create(Schema.Type.INT))));

    Schema result = merge(coral, avro);
    // fA: non-nullable array from Coral
    assertEquals(result.getField("fA").schema().getType(), Schema.Type.ARRAY);
    // fB: nullable array from Coral
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
    // fC: extra from Coral, should be optional array
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fC").schema()));
  }

  @Test
  public void shouldHandleMaps() {
    StructType coral = struct(field("fA", MapType.of(stringType(false), intType(false), false)),
        field("fB", MapType.of(stringType(false), intType(true), true)));
    Schema avro = avroStruct("r1", requiredField("fA", Schema.createMap(Schema.create(Schema.Type.INT))));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fA").schema().getType(), Schema.Type.MAP);
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldHandleTimestampMicros() {
    StructType coral = struct(field("ts", TimestampType.of(6, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    assertEquals(tsSchema.getType(), Schema.Type.LONG);
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-micros");
  }

  @Test
  public void shouldHandleTimestampMillis() {
    StructType coral = struct(field("ts", TimestampType.of(3, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-millis");
  }

  @Test
  public void shouldHandleTimestampUnspecifiedPrecision() {
    StructType coral = struct(field("ts", TimestampType.of(TimestampType.PRECISION_NOT_SPECIFIED, true)));
    Schema avro = avroStruct("r1", optionalField("ts", Schema.Type.LONG));

    Schema result = merge(coral, avro);
    Schema tsSchema = SchemaUtilities.extractIfOption(result.getField("ts").schema());
    // Default to micros for unspecified precision
    assertEquals(tsSchema.getProp("logicalType"), "timestamp-micros");
  }

  @Test
  public void shouldHandleDate() {
    StructType coral = struct(field("d", PrimitiveType.of(CoralTypeKind.DATE, true)));
    Schema avro = avroStruct("r1", optionalField("d", Schema.Type.INT));

    Schema result = merge(coral, avro);
    Schema dateSchema = SchemaUtilities.extractIfOption(result.getField("d").schema());
    assertEquals(dateSchema.getType(), Schema.Type.INT);
    assertEquals(dateSchema.getProp("logicalType"), "date");
  }

  @Test
  public void shouldHandleDecimal() {
    StructType coral = struct(field("dec", DecimalType.of(10, 2, true)));
    Schema avro = avroStruct("r1", optionalField("dec", Schema.Type.BYTES));

    Schema result = merge(coral, avro);
    Schema decSchema = SchemaUtilities.extractIfOption(result.getField("dec").schema());
    assertEquals(decSchema.getType(), Schema.Type.BYTES);
    assertEquals(decSchema.getProp("logicalType"), "decimal");
  }

  @Test
  public void shouldHandleFixedBinary() {
    StructType coral = struct(field("fb", BinaryType.of(16, true)));

    Schema result = merge(coral, null);
    Schema fbSchema = SchemaUtilities.extractIfOption(result.getField("fb").schema());
    assertEquals(fbSchema.getType(), Schema.Type.FIXED);
    assertEquals(fbSchema.getFixedSize(), 16);
  }

  @Test
  public void shouldHandleUnboundedBinary() {
    StructType coral = struct(field("ub", BinaryType.of(BinaryType.LENGTH_UNBOUNDED, false)));

    Schema result = merge(coral, null);
    assertEquals(result.getField("ub").schema().getType(), Schema.Type.BYTES);
  }

  @Test
  public void shouldPromoteBytesToFixedFromPartner() {
    StructType coral = struct(field("fb", BinaryType.of(BinaryType.LENGTH_UNBOUNDED, false)));
    Schema fixedSchema = Schema.createFixed("myFixed", null, null, 16);
    Schema avro = avroStruct("r1", requiredField("fb", fixedSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("fb").schema().getType(), Schema.Type.FIXED);
    assertEquals(result.getField("fb").schema().getName(), "myFixed");
  }

  @Test
  public void shouldPromoteStringToEnumFromPartner() {
    StructType coral = struct(field("status", stringType(false)));
    Schema enumSchema = Schema.createEnum("Status", null, "com.test", Arrays.asList("ACTIVE", "INACTIVE"));
    Schema avro = avroStruct("r1", requiredField("status", enumSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("status").schema().getType(), Schema.Type.ENUM);
  }

  @Test
  public void shouldPreserveUuidFromPartner() {
    StructType coral = struct(field("uid", stringType(false)));
    Schema uuidSchema = Schema.create(Schema.Type.STRING);
    uuidSchema.addProp("logicalType", "uuid");
    Schema avro = avroStruct("r1", requiredField("uid", uuidSchema));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("uid").schema().getProp("logicalType"), "uuid");
  }

  @Test
  public void shouldWorkWithNullPartner() {
    StructType coral = struct(field("fA", intType(true)), field("fB", stringType(false)));

    Schema result = merge(coral, null);
    assertNotNull(result);
    assertEquals(result.getFields().size(), 2);
    // fA is nullable
    assertTrue(AvroSerdeUtils.isNullableType(result.getField("fA").schema()));
    // fB is non-nullable
    assertFalse(AvroSerdeUtils.isNullableType(result.getField("fB").schema()));
  }

  @Test
  public void shouldMatchPartnerFieldCaseInsensitively() {
    StructType coral = struct(field("fA", intType(false)));
    // Partner uses lowercase "fa" — case-insensitive match should find it and copy its doc.
    Schema avro = avroStruct("r1", requiredAvroField("fa", Schema.Type.INT, "ci-match", null, null));

    Schema result = merge(coral, avro);
    // Output uses Coral casing; the partner casing does not appear. Doc is copied through.
    assertNotNull(result.getField("fA"));
    assertEquals(result.getField("fA").doc(), "ci-match");
    assertNull(result.getField("fa"));
  }

  @Test
  public void shouldRespectPartnerNullPlacement() {
    StructType coral = struct(field("fA", intType(true)));
    // Partner has [int, null] order (null second)
    Schema avro = avroStruct("r1",
        avroField("fA",
            Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))), null, 1,
            null));

    Schema result = merge(coral, avro);
    // Should preserve [int, null] order
    Schema fieldSchema = result.getField("fA").schema();
    assertEquals(fieldSchema.getType(), Schema.Type.UNION);
    assertEquals(fieldSchema.getTypes().get(0).getType(), Schema.Type.INT);
    assertEquals(fieldSchema.getTypes().get(1).getType(), Schema.Type.NULL);
  }

  @Test
  public void shouldPreserveRecordNameAndNamespaceFromPartner() {
    StructType coral = struct(field("fA", intType(true)));
    Schema avro = avroStruct("MyRecord", null, "com.example", optionalField("fA", Schema.Type.INT));

    Schema result = merge(coral, avro);
    assertEquals(result.getName(), "MyRecord");
    assertEquals(result.getNamespace(), "com.example");
  }

  @Test
  public void shouldHandleNestedStructsWithPartner() {
    StructType coral = struct(field("outer", struct(field("inner", intType(true)))));
    Schema avro = avroStruct("r1", optionalField("outer", avroStruct("r2", optionalField("inner", Schema.Type.INT))));

    Schema result = merge(coral, avro);
    Schema outerSchema = SchemaUtilities.extractIfOption(result.getField("outer").schema());
    assertEquals(outerSchema.getType(), Schema.Type.RECORD);
    assertEquals(outerSchema.getName(), "r2");
    assertNotNull(outerSchema.getField("inner"));
  }

  @Test
  public void shouldReconstructMultiBranchUnionFromUnionStruct() {
    // Hive uniontype<int,string,boolean> persisted into Iceberg is the struct {tag, field0, field1, field2}.
    // The partner Avro keeps it as a union, so the engine must emit a union with the same branches/order.
    StructType coral =
        struct(field("u", unionStruct(intType(true), stringType(true), PrimitiveType.of(CoralTypeKind.BOOLEAN, true))));
    Schema avro = avroStruct("r1", avroField("u",
        avroUnion(Schema.Type.NULL, Schema.Type.INT, Schema.Type.STRING, Schema.Type.BOOLEAN), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.INT);
    assertEquals(u.getTypes().get(2).getType(), Schema.Type.STRING);
    assertEquals(u.getTypes().get(3).getType(), Schema.Type.BOOLEAN);
  }

  @Test
  public void shouldReconstructUnionWithoutNullBranch() {
    // Neither the union-struct nor the partner is nullable, so no null branch may be fabricated.
    StructType coral = struct(field("u", unionStruct(false, intType(true), stringType(true))));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.INT, Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.INT);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldRelaxUnionEnvelopeWhenOnlyTheStructIsNullable() {
    // Iceberg marks the union column optional while the partner union carries no null branch. The
    // envelope is relaxed to nullable, matching the field-level rule.
    StructType coral = struct(field("u", unionStruct(true, intType(true), stringType(true))));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.INT, Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 3);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.INT);
    assertEquals(u.getTypes().get(2).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldRelaxUnionEnvelopeWhenOnlyThePartnerIsNullable() {
    // Mirror image: Iceberg says required, the partner declares a null branch — the null survives.
    StructType coral = struct(field("u", unionStruct(false, intType(true), stringType(true))));
    Schema avro = avroStruct("r1",
        avroField("u", avroUnion(Schema.Type.NULL, Schema.Type.INT, Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 3);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
  }

  @Test
  public void shouldMergeRecordMemberAgainstItsPartnerBranch() {
    // uniontype<int, struct<x:int>>: the struct member must become a record branch whose fields are
    // merged from the corresponding partner branch, and must NOT be double-wrapped in its own
    // [null, record] option — the union's own NULL branch already carries nullability.
    StructType coral = struct(field("u", unionStruct(intType(true), struct(field("x", intType(true))))));
    Schema partnerRecordBranch = avroStruct("branchRec", optionalField("x", Schema.Type.INT));
    Schema avro = avroStruct("r1",
        avroField("u",
            avroUnionOf(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.INT), partnerRecordBranch), null,
            null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 3);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.INT);

    Schema recordBranch = u.getTypes().get(2);
    // Not wrapped in an option: the branch is the record itself, not [null, record].
    assertEquals(recordBranch.getType(), Schema.Type.RECORD);
    // The member's fields were merged against the partner branch rather than regenerated blindly.
    assertEquals(recordBranch.getName(), "branchRec");
    assertNotNull(recordBranch.getField("x"));
  }

  @Test
  public void shouldKeepArrayMemberAsArrayBranch() {
    // uniontype<int, array<string>>: the array member must stay an array branch rather than being
    // collapsed or unwrapped into its element type.
    StructType coral = struct(field("u", unionStruct(intType(true), ArrayType.of(stringType(true), true))));
    Schema partnerArrayBranch =
        Schema.createArray(SchemaUtilities.makeNullable(Schema.create(Schema.Type.STRING), false));
    Schema avro = avroStruct("r1",
        avroField("u", avroUnionOf(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.INT), partnerArrayBranch),
            null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 3);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.INT);

    Schema arrayBranch = u.getTypes().get(2);
    assertEquals(arrayBranch.getType(), Schema.Type.ARRAY);
    assertEquals(SchemaUtilities.extractIfOption(arrayBranch.getElementType()).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPromoteStringMemberToPartnerEnumBranch() {
    // uniontype<string, int> whose partner branch is an enum: branches go through the normal promotion
    // path (checkCompatibilityAndPromote), so the STRING member must surface as the partner's ENUM
    // rather than a bare string.
    StructType coral = struct(field("u", unionStruct(stringType(true), intType(true))));
    Schema partnerEnumBranch = Schema.createEnum("Color", null, "com.test", Arrays.asList("RED", "GREEN"));
    Schema avro = avroStruct("r1",
        avroField("u", avroUnionOf(Schema.create(Schema.Type.NULL), partnerEnumBranch, Schema.create(Schema.Type.INT)),
            null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 3);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);

    Schema enumBranch = u.getTypes().get(1);
    assertEquals(enumBranch.getType(), Schema.Type.ENUM);
    assertEquals(enumBranch.getName(), "Color");
    assertEquals(enumBranch.getEnumSymbols(), Arrays.asList("RED", "GREEN"));
    assertEquals(u.getTypes().get(2).getType(), Schema.Type.INT);
  }

  @Test
  public void shouldReconstructNullableSingleUnion() {
    // Covers the case where the union-struct encoding SURVIVED into Iceberg: uniontype<string> stored as
    // {tag, field0}. This is not an assertion that Iceberg always preserves it — a single uniontype is
    // often flattened to a plain column instead, which is handled separately in mergeType and covered by
    // shouldPreserveSingleElementUnionEnvelopeWhenIcebergFlattensIt. The preserved encoding is confirmed in
    // production for multi-branch unions (the facetClauses array<union> tables); for arity 1 this test is
    // defensive, since HiveToCoralTypeConverter.convertUnion emits {tag, field0} for EVERY arity and a
    // writer may persist that shape. A nullable single union must come back as [null, string]; leaving it
    // on the struct path would leak {tag, field0} into the output.
    StructType coral = struct(field("u", unionStruct(stringType(true))));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.NULL, Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldReconstructNonNullableSingleUnion() {
    // Same as above without a null branch anywhere: nothing may fabricate one.
    StructType coral = struct(field("u", unionStruct(false, stringType(true))));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldTreatGenuineNullableStructNamedLikeUnionStructAsStruct() {
    // A real struct that happens to be named {tag, field0} is shape-indistinguishable from a single
    // uniontype. The partner disambiguates: its sole branch is the record describing the struct, so it
    // carries its own "tag" field. This must stay a record, not become a union.
    StructType coral = struct(field("u", struct(field("tag", intType(true)), field("field0", stringType(true)))));
    Schema partnerRecord =
        avroStruct("genuineStruct", optionalField("tag", Schema.Type.INT), optionalField("field0", Schema.Type.STRING));
    Schema avro =
        avroStruct("r1", avroField("u", avroUnionOf(Schema.create(Schema.Type.NULL), partnerRecord), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    Schema inner = SchemaUtilities.extractIfOption(u);
    assertEquals(inner.getType(), Schema.Type.RECORD);
    assertEquals(inner.getName(), "genuineStruct");
    assertNotNull(inner.getField("tag"));
    assertNotNull(inner.getField("field0"));
  }

  @Test
  public void shouldReconstructSingleUnionWhoseMemberIsARecord() {
    // uniontype<struct<x:int>>: the partner's sole branch is a record WITHOUT a "tag" field, so it is the
    // member type rather than a description of the union-struct — reconstruct as a union.
    StructType coral = struct(field("u", unionStruct(struct(field("x", intType(true))))));
    Schema memberRecord = avroStruct("memberRec", optionalField("x", Schema.Type.INT));
    Schema avro =
        avroStruct("r1", avroField("u", avroUnionOf(Schema.create(Schema.Type.NULL), memberRecord), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.RECORD);
    assertEquals(u.getTypes().get(1).getName(), "memberRec");
    assertNotNull(u.getTypes().get(1).getField("x"));
  }

  @Test
  public void shouldDetectUnionStructMarkerNamesCaseInsensitively() {
    // A catalog that normalizes field casing must not silently downgrade a union to a record.
    StructType coral = struct(field("u",
        struct(field("TAG", intType(true)), field("Field0", stringType(true)), field("FIELD1", intType(true)))));
    Schema avro = avroStruct("r1",
        avroField("u", avroUnion(Schema.Type.NULL, Schema.Type.STRING, Schema.Type.INT), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.STRING);
    assertEquals(u.getTypes().get(2).getType(), Schema.Type.INT);
  }

  @Test
  public void shouldRelaxNullabilityForArrayElementAndMapValue() {
    // The relax rule is applied at a single choke point, so nested positions inherit it. Coral marks the
    // element and value required while the partner declares both nullable.
    StructType coral = struct(field("arr", ArrayType.of(intType(false), false)),
        field("m", MapType.of(stringType(false), intType(false), false)));
    Schema nullableInt = SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false);
    Schema avro = avroStruct("r1", requiredField("arr", Schema.createArray(nullableInt)),
        requiredField("m", Schema.createMap(nullableInt)));

    Schema result = merge(coral, avro);
    Schema arr = SchemaUtilities.extractIfOption(result.getField("arr").schema());
    Schema map = SchemaUtilities.extractIfOption(result.getField("m").schema());
    assertTrue(AvroSerdeUtils.isNullableType(arr.getElementType()));
    assertTrue(AvroSerdeUtils.isNullableType(map.getValueType()));
  }

  @Test
  public void shouldRelaxNullabilityForNestedStructField() {
    StructType coral = struct(field("outer", struct(field("inner", intType(false)))));
    Schema avro = avroStruct("r1", requiredField("outer", avroStruct("r2", optionalField("inner", Schema.Type.INT))));

    Schema result = merge(coral, avro);
    Schema outer = SchemaUtilities.extractIfOption(result.getField("outer").schema());
    assertTrue(AvroSerdeUtils.isNullableType(outer.getField("inner").schema()));
  }

  @Test
  public void shouldPreserveSingleElementUnionEnvelopeWhenIcebergFlattensIt() {
    // Iceberg has no union type, so uniontype<string> may surface as a plain field while the partner
    // still declares ["string"]. The old path reconstructs uniontype<string> from the partner via
    // AvroAwareHiveSchemaUtil and emits ["string"], so dropping the envelope here would regress.
    StructType coral = struct(field("u", stringType(false)));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldSubsumeFlattenedSingleUnionEnvelopeIntoTheNullableForm() {
    // Same input but Iceberg marks the column optional. ["null","string"] is the only Avro
    // representation of a nullable single union, so no extra envelope is added.
    StructType coral = struct(field("u", stringType(true)));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPromoteThroughAFlattenedSingleUnionEnvelope() {
    // The branch is still merged, so an enum partner branch promotes the Coral string as usual.
    StructType coral = struct(field("u", stringType(false)));
    Schema enumBranch = Schema.createEnum("Color", null, "com.test", Arrays.asList("RED", "GREEN"));
    Schema avro = avroStruct("r1", avroField("u", avroUnionOf(enumBranch), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.ENUM);
    assertEquals(u.getTypes().get(0).getName(), "Color");
  }

  @Test
  public void shouldNotFabricateAnEnvelopeForAPlainPartner() {
    StructType coral = struct(field("u", stringType(false)));
    Schema avro = avroStruct("r1", requiredField("u", Schema.Type.STRING));

    Schema result = merge(coral, avro);
    assertEquals(result.getField("u").schema().getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPreserveSingleElementUnionEnvelopeAroundAnArray() {
    // The production shape behind organization_mp.product_dbchanges_hourly $.value.productCategoryUrns:
    // uniontype<array<string>> flattened by Iceberg to array<string> while the partner still declares
    // [{"type":"array","items":"string"}]. Dispatch is on the Coral kind, so this reaches mergeArray
    // rather than mergeLeaf — the envelope handling has to sit above that switch to cover it.
    StructType coral = struct(field("u", ArrayType.of(stringType(false), false)));
    Schema arrayBranch = Schema.createArray(Schema.create(Schema.Type.STRING));
    Schema avro = avroStruct("r1", avroField("u", avroUnionOf(arrayBranch), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.ARRAY);
    assertEquals(u.getTypes().get(0).getElementType().getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPreserveSingleElementUnionEnvelopeAroundAMap() {
    StructType coral = struct(field("u", MapType.of(stringType(false), stringType(false), false)));
    Schema mapBranch = Schema.createMap(Schema.create(Schema.Type.STRING));
    Schema avro = avroStruct("r1", avroField("u", avroUnionOf(mapBranch), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.MAP);
    assertEquals(u.getTypes().get(0).getValueType().getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPreserveSingleElementUnionEnvelopeAroundAStructAndStillMergeIt() {
    // The partner record must still be merged through the envelope, otherwise its name, docs and
    // props are lost exactly as they were before the envelope was recognised at all.
    StructType coral = struct(field("u", StructType.of(Arrays.asList(field("x", intType(false))), false)));
    Schema recordBranch = avroStruct("Inner", "inner-doc", "com.test",
        avroField("x", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "x-doc", null, null));
    Schema avro = avroStruct("r1", avroField("u", avroUnionOf(recordBranch), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 1);
    Schema inner = u.getTypes().get(0);
    assertEquals(inner.getType(), Schema.Type.RECORD);
    assertEquals(inner.getName(), "Inner");
    assertEquals(inner.getField("x").doc(), "x-doc");
    // Relaxed nullability still applies inside the envelope: the partner says x is nullable.
    assertEquals(inner.getField("x").schema().getType(), Schema.Type.UNION);
  }

  @Test
  public void shouldSubsumeAFlattenedArrayEnvelopeIntoTheNullableForm() {
    // Same as the array case but Iceberg marks the column optional, so ["null", array] already
    // carries the union and no extra envelope is added.
    StructType coral = struct(field("u", ArrayType.of(stringType(true), true)));
    Schema arrayBranch = Schema.createArray(Schema.create(Schema.Type.STRING));
    Schema avro = avroStruct("r1", avroField("u", avroUnionOf(arrayBranch), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.NULL);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.ARRAY);
  }

  @Test
  public void shouldStillReconstructAMultiBranchUnionStructWhenPartnerIsAUnion() {
    // Guard on precedence: a genuine union-struct must keep going through mergeUnionStruct and must
    // not be diverted by the single-branch envelope path.
    StructType coral = struct(field("u", unionStruct(false, intType(true), stringType(true))));
    Schema avro = avroStruct("r1", avroField("u", avroUnion(Schema.Type.INT, Schema.Type.STRING), null, null, null));

    Schema result = merge(coral, avro);
    Schema u = result.getField("u").schema();
    assertEquals(u.getType(), Schema.Type.UNION);
    assertEquals(u.getTypes().size(), 2);
    assertEquals(u.getTypes().get(0).getType(), Schema.Type.INT);
    assertEquals(u.getTypes().get(1).getType(), Schema.Type.STRING);
  }

  @Test
  public void shouldPreferAnExactCaseMatchOverAnEarlierCaseInsensitiveOne() {
    // Avro permits fa and fA as siblings. The exact match must win even though the inexact one is
    // declared first, otherwise the wrong field's doc/default/props get copied.
    StructType coral = struct(field("fA", intType(true)));
    Schema avro = avroStruct("r1",
        avroField("fa", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "lowercase-one", null,
            null),
        avroField("fA", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "exact-one", null, null));

    Schema result = merge(coral, avro);
    assertNotNull(result.getField("fA"));
    assertEquals(result.getField("fA").doc(), "exact-one");
  }

  @Test
  public void shouldNotGuessBetweenTwoInexactCaseMatches() {
    // Neither partner field matches exactly and both match case-insensitively: refuse to pick one
    // arbitrarily, so no partner metadata is inherited.
    StructType coral = struct(field("fA", intType(true)));
    Schema avro = avroStruct("r1",
        avroField("fa", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "first", null, null),
        avroField("FA", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "second", null, null));

    Schema result = merge(coral, avro);
    assertNotNull(result.getField("fA"));
    assertNull(result.getField("fA").doc());
  }

  @Test
  public void shouldStillMatchAUniqueCaseInsensitivePartner() {
    // Rule 2 is unchanged: a single inexact candidate still matches and still carries its metadata.
    StructType coral = struct(field("fA", intType(true)));
    Schema avro = avroStruct("r1",
        avroField("fa", SchemaUtilities.makeNullable(Schema.create(Schema.Type.INT), false), "ci-match", null, null));

    Schema result = merge(coral, avro);
    assertNotNull(result.getField("fA"));
    assertEquals(result.getField("fA").doc(), "ci-match");
  }

  @Test
  public void shouldLowercaseAnIcebergMergedSchemaWithoutLosingStructure() {
    // Production reads spark.sql.force.lowercase.dali.schema (default false), and the parity harness
    // runs with forceLowercase=false, so the lowercase visitor over an Iceberg-merged schema had no
    // coverage. Nullability, union envelopes and nested shape must all survive the rename.
    StructType coral =
        struct(field("fA", intType(false)), field("uU", unionStruct(false, intType(true), stringType(true))),
            field("nEst", struct(field("iNner", intType(false)))));
    Schema avro = avroStruct("r1", optionalField("fA", Schema.Type.INT),
        avroField("uU", avroUnion(Schema.Type.INT, Schema.Type.STRING), null, null, null),
        requiredField("nEst", avroStruct("r2", optionalField("iNner", Schema.Type.INT))));

    Schema merged = merge(coral, avro);
    Schema lowercased = ToLowercaseSchemaVisitor.visit(merged);

    // Names are lowercased ...
    assertNotNull(lowercased.getField("fa"));
    assertNotNull(lowercased.getField("uu"));
    assertNotNull(lowercased.getField("nest"));
    // ... the relaxed nullability survives ...
    assertTrue(AvroSerdeUtils.isNullableType(lowercased.getField("fa").schema()));
    // ... the reconstructed union envelope survives ...
    Schema union = lowercased.getField("uu").schema();
    assertEquals(union.getType(), Schema.Type.UNION);
    assertEquals(union.getTypes().size(), 2);
    // ... and nested fields are lowercased too, keeping their relaxed nullability.
    Schema nested = SchemaUtilities.extractIfOption(lowercased.getField("nest").schema());
    assertNotNull(nested.getField("inner"));
    assertTrue(AvroSerdeUtils.isNullableType(nested.getField("inner").schema()));
  }

  /** Test Helpers */

  /** Builds a Trino-style union-struct {tag:INT, field0, field1, ...} from the given union member types. */
  private StructType unionStruct(com.linkedin.coral.common.types.CoralDataType... members) {
    return unionStruct(true, members);
  }

  /**
   * Union-struct with explicit nullability. Iceberg's {@code optional}/{@code required} flag on the
   * column reaches the merge engine as this flag, and the envelope is nullable when either it or the
   * partner says so — so tests that assert an envelope without a NULL branch must pass {@code false}.
   */
  private StructType unionStruct(boolean nullable, com.linkedin.coral.common.types.CoralDataType... members) {
    StructField[] fields = new StructField[members.length + 1];
    fields[0] = field("tag", intType(true));
    for (int i = 0; i < members.length; i++) {
      fields[i + 1] = field("field" + i, members[i]);
    }
    return StructType.of(Arrays.asList(fields), nullable);
  }

  private Schema avroUnion(Schema.Type... branchTypes) {
    Schema[] branches = new Schema[branchTypes.length];
    for (int i = 0; i < branchTypes.length; i++) {
      branches[i] = Schema.create(branchTypes[i]);
    }
    return Schema.createUnion(Arrays.asList(branches));
  }

  /** Union builder for branches that are not plain primitives (records, arrays, enums). */
  private Schema avroUnionOf(Schema... branches) {
    return Schema.createUnion(Arrays.asList(branches));
  }

  private Schema merge(StructType coral, Schema avro) {
    return MergeCoralSchemaWithAvro.merge(coral, avro, "TestRecord", "com.test");
  }

  private PrimitiveType intType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.INT, nullable);
  }

  private PrimitiveType stringType(boolean nullable) {
    return PrimitiveType.of(CoralTypeKind.STRING, nullable);
  }

  private StructField field(String name, com.linkedin.coral.common.types.CoralDataType type) {
    return StructField.of(name, type);
  }

  private StructType struct(StructField... fields) {
    return StructType.of(Arrays.asList(fields), true);
  }

  private Schema avroStruct(String name, Schema.Field... fields) {
    return avroStruct(name, null, "n" + name, fields);
  }

  private Schema avroStruct(String name, String doc, String namespace, Schema.Field... fields) {
    Schema result = Schema.createRecord(name, doc, namespace, false);
    result.setFields(Arrays.asList(fields));
    return result;
  }

  private Schema.Field avroField(String name, Schema schema, String doc, Object defaultValue,
      Map<String, String> props) {
    Schema.Field field = AvroCompatibilityHelper.createSchemaField(name, schema, doc, defaultValue);
    if (props != null) {
      props.forEach((propName, propValueInJson) -> AvroCompatibilityHelper.setFieldPropFromJsonString(field, propName,
          propValueInJson, false));
    }
    return field;
  }

  private Schema.Field requiredField(String name, Schema.Type type) {
    return requiredField(name, Schema.create(type));
  }

  private Schema.Field requiredField(String name, Schema schema) {
    return avroField(name, schema, null, null, null);
  }

  private Schema.Field requiredAvroField(String name, Schema.Type type, String doc, Object defaultValue,
      Map<String, String> props) {
    return avroField(name, Schema.create(type), doc, defaultValue, props);
  }

  private Schema.Field optionalField(String name, Schema.Type type) {
    return optionalField(name, Schema.create(type));
  }

  private Schema.Field optionalField(String name, Schema schema) {
    return avroField(name, SchemaUtilities.makeNullable(schema, false), null, null, null);
  }
}
