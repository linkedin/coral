/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.com.google.common.collect.ImmutableMap;
import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.FuzzyUnionAvroSchemaTests.assertFailsMentioning;
import static com.linkedin.coral.schema.avro.FuzzyUnionAvroSchemaTests.assertFailsMentioningIgnoringCase;
import static com.linkedin.coral.schema.avro.FuzzyUnionFixtures.*;


/**
 * Structural checks that natural view SQL cannot reach: an n-ary {@link LogicalUnion}, and internally typed
 * {@link GenericProjectFunction} calls that request missing fields or are malformed. Plans are built from real
 * converted table scans; only the node or call under test is constructed directly.
 */
public class FuzzyUnionStructuralTests {
  private FuzzyUnionTestCatalog catalog;
  private HiveToRelConverter hiveToRelConverter;
  private RelToAvroSchemaConverter relToAvroSchemaConverter;

  @BeforeClass
  public void beforeClass() {
    registerUdfs();
    catalog = buildCatalog();
    hiveToRelConverter = new HiveToRelConverter(catalog);
    relToAvroSchemaConverter = new RelToAvroSchemaConverter(catalog);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T6: a LogicalUnion with more than two inputs
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT6NaryUnionUsesThirdInputNullability() {
    LogicalUnion union =
        union("SELECT id, f FROM fz.req_a", "SELECT id, f FROM fz.req_b", "SELECT id, f FROM fz.req_c");
    Assert.assertEquals(union.getInputs().size(), 3);

    Schema actual = relToAvroSchemaConverter.convert(union, false, false);
    Assert.assertEquals(actual.getField("f").schema().toString(), "[\"null\",\"string\"]",
        "only the third input makes f nullable");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(actual.getField("f")));
  }

  @Test
  public void testT6BinaryUnionControl() {
    Schema actual = relToAvroSchemaConverter.convert(union("SELECT id, f FROM fz.req_a", "SELECT id, f FROM fz.req_b"),
        false, false);
    Assert.assertEquals(actual.getField("f").schema().toString(), "\"string\"");
  }

  @Test
  public void testT6NaryUnionChecksThirdInputCompatibility() {
    // All three inputs are BINARY to Calcite; only the third Avro fixed has a different size.
    LogicalUnion union = union("SELECT id, c FROM fz.fx_a", "SELECT id, c FROM fz.fx_a", "SELECT id, c FROM fz.fx_b");
    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(union, false, false), "Md5", "16", "8");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T13: projection requests a field the source does not have
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT13MissingRequestedFieldFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "nosuchfield");
    RelNode project = projectColumn(scan, 1, "requestheader", genericProject(scan, target, inputRef(scan, 1)));

    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(project, false, false), "nosuchfield");
  }

  @Test
  public void testT13DeclaredAvroAliasIsNotALookupKey() {
    // meta_evolved Info.firstField declares alias first_field; requesting first_field must not resolve to it.
    RelNode scan = scan("SELECT * FROM fz.meta_evolved");
    RelDataType target = struct(scan, "first_field");
    RelNode project = projectColumn(scan, 1, "info", genericProject(scan, target, inputRef(scan, 1)));

    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(project, false, false), "first_field");
  }

  @Test
  public void testT13NonStringTargetMapKeyFails() {
    // Source attrs is a valid Avro map (string keys); the typed target asks for MAP<INTEGER, struct>.
    RelNode scan = scan("SELECT * FROM fz.nest_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType value = typeFactory.createTypeWithNullability(typeFactory.createStructType(
        ImmutableList.of(nullable(typeFactory, SqlTypeName.VARCHAR), nullable(typeFactory, SqlTypeName.INTEGER)),
        ImmutableList.of("attrkey", "attrval")), true);
    RelDataType target = typeFactory
        .createTypeWithNullability(typeFactory.createMapType(nullable(typeFactory, SqlTypeName.INTEGER), value), true);
    RelNode project = projectColumn(scan, 1, "attrs", genericProject(scan, target, inputRef(scan, 1)));

    assertFailsMentioningIgnoringCase(() -> relToAvroSchemaConverter.convert(project, false, false), "attrs", "key");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T12/T13 (structural, typed call): DECIMAL inside a projected record. Natural fuzzy SQL cannot reach this because
  // the existing rewriter cannot express DECIMAL in its Hive target-type string; inference reads the typed return.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT12ProjectedRecordKeepsDecimalIdentity() {
    RelNode scan = scan("SELECT * FROM fz.dec_src");
    RelNode project = projectColumn(scan, 1, "p", genericProject(scan, decimalStruct(scan, 10, 2), inputRef(scan, 1)));

    Schema.Field field = relToAvroSchemaConverter.convert(project, true, false).getField("p");
    Assert.assertNotNull(field, "projected field keeps the source name p");
    Assert.assertEquals(field.doc(), "Price field");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field));
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(("{'type':'record','name':'P','namespace':'com.linkedin.dec','doc':'Priced record',"
            + "'fields':[{'name':'amount','type':{'type':'bytes','logicalType':'decimal','precision':10,'scale':2,"
            + "'x-dec-prop':'d'},'doc':'Amount'}]}").replace('\'', '"')).toString(true));
  }

  @Test
  public void testT13ProjectedDecimalWithDifferentScaleFails() {
    RelNode scan = scan("SELECT * FROM fz.dec_src");
    RelNode project = projectColumn(scan, 1, "p", genericProject(scan, decimalStruct(scan, 10, 3), inputRef(scan, 1)));

    assertFailsMentioning(() -> relToAvroSchemaConverter.convert(project, true, false), "amount");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T12/T13 (structural, typed call): a leaf keeps its Avro schema only when the requested relational type denotes the
  // same value representation, including type parameters. A parameterized request that differs is a cast, which
  // generic projection does not perform: it must be rejected with the field path rather than return the source schema.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT13TopLevelFixedRequestedWithDifferentLengthFails() {
    // fx_a.c is Avro fixed Md5 of size 16; BINARY(8) is the relational form of an 8-byte fixed.
    RelNode scan = scan("SELECT * FROM fz.fx_a");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType binary8 = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BINARY, 8), true);
    RelNode project = projectColumn(scan, 1, "c", genericProject(scan, binary8, inputRef(scan, 1)));

    assertParameterMismatchRejected(project, "c", "BINARY(8)");
  }

  @Test
  public void testT13NestedFixedRequestedWithDifferentLengthFails() {
    RelNode scan = scan("SELECT * FROM fz.lt_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType target = ltProjection(scan, "digest",
        typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BINARY, 8), true));
    RelNode project = projectColumn(scan, 2, "l", genericProject(scan, target, inputRef(scan, 2)));

    assertParameterMismatchRejected(project, "l.digest", "BINARY(8)");
  }

  @Test
  public void testT13NestedTimestampRequestedWithDifferentPrecisionFails() {
    // l.createdAt is Avro long/timestamp-millis; TIMESTAMP(6) is the relational form of timestamp-micros.
    RelNode scan = scan("SELECT * FROM fz.lt_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType target = ltProjection(scan, "createdat",
        typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.TIMESTAMP, 6), true));
    RelNode project = projectColumn(scan, 2, "l", genericProject(scan, target, inputRef(scan, 2)));

    assertParameterMismatchRejected(project, "l.createdAt", "TIMESTAMP(6)");
  }

  @Test
  public void testT12SameRepresentationLeavesKeepLogicalAndFixedIdentity() {
    // Control: requesting every retained leaf with exactly its own relational type is a pure projection; date,
    // timestamp-millis, uuid, fixed (with its property) and enum keep their source schemas, and lExtra is dropped.
    RelNode scan = scan("SELECT * FROM fz.lt_evolved");
    RelNode project =
        projectColumn(scan, 2, "l", genericProject(scan, ltProjection(scan, null, null), inputRef(scan, 2)));

    Schema projected =
        SchemaUtilities.extractIfOption(relToAvroSchemaConverter.convert(project, true, false).getField("l").schema());
    Schema source = new Schema.Parser().parse(load("lt_evolved.avsc")).getField("l").schema();
    Assert.assertNull(projected.getField("lExtra"));
    for (String leaf : ImmutableList.of("birthDate", "createdAt", "token", "digest", "color")) {
      Assert.assertEquals(projected.getField(leaf).schema().toString(true),
          source.getField(leaf).schema().toString(true), leaf);
    }
  }

  @Test
  public void testT13OptionalFixedRequestedWithDifferentLengthFails() {
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelNode project = projectColumn(scan, 1, "r",
        genericProject(scan, repProjection(scan, "optf16", binary(scan, 8)), inputRef(scan, 1)));

    assertParameterMismatchRejected(project, "r.optF16", "BINARY(8)");
  }

  @Test
  public void testT13ArrayFixedElementRequestedWithDifferentLengthFails() {
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType array = typeFactory.createTypeWithNullability(typeFactory.createArrayType(binary(scan, 8), -1), true);
    RelNode project =
        projectColumn(scan, 1, "r", genericProject(scan, repProjection(scan, "arrf16", array), inputRef(scan, 1)));

    assertParameterMismatchRejected(project, "r.arrF16", "BINARY(8)");
  }

  @Test
  public void testT13MapTimestampValueRequestedWithDifferentPrecisionFails() {
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType sourceMap =
        scan.getRowType().getField("r", false, false).getType().getField("mapmillis", false, false).getType();
    RelDataType map = typeFactory.createTypeWithNullability(typeFactory.createMapType(sourceMap.getKeyType(),
        typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.TIMESTAMP, 6), true)), true);
    RelNode project =
        projectColumn(scan, 1, "r", genericProject(scan, repProjection(scan, "mapmillis", map), inputRef(scan, 1)));

    assertParameterMismatchRejected(project, "r.mapMillis", "TIMESTAMP(6)");
  }

  @Test
  public void testT13ShorterFixedRequestedAsLongerFails() {
    // Opposite direction: an 8-byte fixed requested as BINARY(16).
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelNode project = projectColumn(scan, 1, "r",
        genericProject(scan, repProjection(scan, "f8", binary(scan, 16)), inputRef(scan, 1)));

    assertParameterMismatchRejected(project, "r.f8", "BINARY(16)");
  }

  @Test
  public void testT12ParameterizedRequestsMatchingAvroIdentityAreKept() {
    // Controls: BINARY(16) is the relational form of a 16-byte fixed and TIMESTAMP(3) of timestamp-millis, so both
    // denote the source representation even though the source relational types (from Hive) carry no parameters.
    // Every other retained leaf, including optional/array/map ones, is requested at its own type; extra is dropped.
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelDataType target = projection(scan, "r", "extra",
        ImmutableMap.of("f16", binary(scan, 16), "millis", rexBuilder(scan).getTypeFactory().createTypeWithNullability(
            rexBuilder(scan).getTypeFactory().createSqlType(SqlTypeName.TIMESTAMP, 3), true)));
    RelNode project = projectColumn(scan, 1, "r", genericProject(scan, target, inputRef(scan, 1)));

    Schema projected =
        SchemaUtilities.extractIfOption(relToAvroSchemaConverter.convert(project, true, false).getField("r").schema());
    Schema source = new Schema.Parser().parse(load("rep_src.avsc")).getField("r").schema();
    Assert.assertNull(projected.getField("extra"));
    for (String leaf : ImmutableList.of("millis", "f8", "f16", "optF16", "arrF16", "mapMillis")) {
      Assert.assertEquals(projected.getField(leaf).schema().toString(true),
          source.getField(leaf).schema().toString(true), leaf);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------
  // F5 (structural, typed call; tester-clarifications section 11): VARBINARY(n) is a maximum length. Over Avro
  // fixed(N) it is satisfied unchanged iff n >= N; over Avro bytes no source bound is proven, so an explicit finite
  // VARBINARY(n) is an unproven narrowing. Unbounded VARBINARY and unparameterized BINARY keep the existing coarse
  // behavior. These are typed inference-boundary checks, not support for a natural Hive VARBINARY flow.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testF5VarbinaryShorterThanFixedFails() {
    RelNode scan = scan("SELECT * FROM fz.lt_evolved");
    RelNode project = projectColumn(scan, 2, "l",
        genericProject(scan, ltProjection(scan, "digest", varbinary(scan, 8)), inputRef(scan, 2)));

    assertParameterMismatchRejected(project, "l.digest", "VARBINARY(8)", "16");
  }

  @Test
  public void testF5VarbinaryAtLeastFixedSizeKeepsFixedUnchanged() {
    // n == N, n > N and unbounded: the 16-byte fixed fits, so it is kept exactly (name, namespace, size, property),
    // never widened to the requested maximum or turned into bytes.
    String literal = catalog.avroLiteral(DB, "lt_evolved");
    Schema sourceDigest =
        new Schema.Parser().parse(load("lt_evolved.avsc")).getField("l").schema().getField("digest").schema();
    RelNode scan = scan("SELECT * FROM fz.lt_evolved");
    for (RelDataType requested : ImmutableList.of(varbinary(scan, 16), varbinary(scan, 32), varbinary(scan))) {
      RelNode project =
          projectColumn(scan, 2, "l", genericProject(scan, ltProjection(scan, "digest", requested), inputRef(scan, 2)));
      Schema l = SchemaUtilities
          .extractIfOption(relToAvroSchemaConverter.convert(project, true, false).getField("l").schema());
      Assert.assertEquals(l.getField("digest").schema().toString(true), sourceDigest.toString(true),
          requested.getFullTypeString());
      Assert.assertNull(l.getField("lExtra"));
    }
    Assert.assertEquals(catalog.avroLiteral(DB, "lt_evolved"), literal, "source metadata must not be mutated");
  }

  @Test
  public void testF5ShorterVarbinaryOverOptionalAndArrayFixedFails() {
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelNode optional = projectColumn(scan, 1, "r",
        genericProject(scan, repProjection(scan, "optf16", varbinary(scan, 8)), inputRef(scan, 1)));
    assertParameterMismatchRejected(optional, "r.optF16", "VARBINARY(8)", "16");

    RelDataType shortArray =
        typeFactory.createTypeWithNullability(typeFactory.createArrayType(varbinary(scan, 8), -1), true);
    RelNode array =
        projectColumn(scan, 1, "r", genericProject(scan, repProjection(scan, "arrf16", shortArray), inputRef(scan, 1)));
    assertParameterMismatchRejected(array, "r.arrF16", "VARBINARY(8)", "16");
  }

  @Test
  public void testF5LooserVarbinaryOverOptionalAndArrayFixedKeepsThem() {
    // Controls for the test above: looser bounds over the same optional and array fixed leaves keep them exactly.
    RelNode scan = scan("SELECT * FROM fz.rep_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType wideArray =
        typeFactory.createTypeWithNullability(typeFactory.createArrayType(varbinary(scan, 16), -1), true);
    RelDataType target =
        projection(scan, "r", "extra", ImmutableMap.of("optf16", varbinary(scan, 32), "arrf16", wideArray));
    Schema r = SchemaUtilities.extractIfOption(relToAvroSchemaConverter
        .convert(projectColumn(scan, 1, "r", genericProject(scan, target, inputRef(scan, 1))), true, false)
        .getField("r").schema());
    Schema source = new Schema.Parser().parse(load("rep_src.avsc")).getField("r").schema();
    for (String leaf : ImmutableList.of("optF16", "arrF16")) {
      Assert.assertEquals(r.getField(leaf).schema().toString(true), source.getField(leaf).schema().toString(true),
          leaf);
    }
  }

  @Test
  public void testF7AnnotatedFixedWithinBoundIsKept() {
    // Final-review F7: a fixed12 carrying logicalType duration is coherently inferred as BINARY; its fixed size proves
    // the bound regardless of the annotation. VARBINARY(12) and VARBINARY(16) keep it exactly, annotation, property and
    // field doc included, at the top level and inside an option.
    RelNode scan = scan("SELECT * FROM fz.dur_src");
    Assert.assertEquals(scan.getRowType().getField("r", false, false).getType().getField("duration", false, false)
        .getType().getSqlTypeName(), SqlTypeName.BINARY);
    String literal = catalog.avroLiteral(DB, "dur_src");
    Schema source = new Schema.Parser().parse(load("dur_src.avsc")).getField("r").schema();
    for (int maximum : new int[] { 12, 16 }) {
      RelDataType target = projection(scan, "r", "extra",
          ImmutableMap.of("duration", varbinary(scan, maximum), "optduration", varbinary(scan, maximum)));
      Schema r = SchemaUtilities.extractIfOption(relToAvroSchemaConverter
          .convert(projectColumn(scan, 1, "r", genericProject(scan, target, inputRef(scan, 1))), true, false)
          .getField("r").schema());
      for (String leaf : ImmutableList.of("duration", "optDuration")) {
        Assert.assertEquals(r.getField(leaf).schema().toString(true), source.getField(leaf).schema().toString(true),
            leaf + " as VARBINARY(" + maximum + ")");
      }
      Assert.assertEquals(r.getField("duration").doc(), "Elapsed");
      Assert.assertNull(r.getField("extra"));
    }
    Assert.assertEquals(catalog.avroLiteral(DB, "dur_src"), literal, "source metadata must not be mutated");
  }

  @Test
  public void testF7AnnotatedFixedNarrowerBoundFails() {
    // Guard for the test above: a maximum below the annotated fixed's 12 bytes stays rejected, top-level and optional.
    RelNode scan = scan("SELECT * FROM fz.dur_src");
    RelDataType narrow = projection(scan, "r", "extra", ImmutableMap.of("duration", varbinary(scan, 8)));
    assertParameterMismatchRejected(projectColumn(scan, 1, "r", genericProject(scan, narrow, inputRef(scan, 1))),
        "r.duration", "VARBINARY(8)", "12");
    RelDataType narrowOptional = projection(scan, "r", "extra", ImmutableMap.of("optduration", varbinary(scan, 8)));
    assertParameterMismatchRejected(
        projectColumn(scan, 1, "r", genericProject(scan, narrowOptional, inputRef(scan, 1))), "r.optDuration",
        "VARBINARY(8)", "12");
  }

  @Test
  public void testF5FiniteVarbinaryOverBytesFails() {
    // Avro bytes declares no maximum and the coarse Hive operand (BINARY) proves none.
    RelNode scan = scan("SELECT * FROM fz.bin_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    assertParameterMismatchRejected(
        projectColumn(scan, 1, "b",
            genericProject(scan, binProjection(scan, "raw", varbinary(scan, 8)), inputRef(scan, 1))),
        "b.raw", "VARBINARY(8)");
    assertParameterMismatchRejected(
        projectColumn(scan, 1, "b",
            genericProject(scan, binProjection(scan, "optraw", varbinary(scan, 64)), inputRef(scan, 1))),
        "b.optRaw", "VARBINARY(64)");
    RelDataType array =
        typeFactory.createTypeWithNullability(typeFactory.createArrayType(varbinary(scan, 8), -1), true);
    assertParameterMismatchRejected(
        projectColumn(scan, 1, "b", genericProject(scan, binProjection(scan, "arrraw", array), inputRef(scan, 1))),
        "b.arrRaw", "VARBINARY(8)");
  }

  @Test
  public void testF5CoarseBinaryFamilyControlsOverBytes() {
    // Unchanged from the reviewed implementation: unbounded VARBINARY and unparameterized BINARY keep bytes exactly
    // (doc and property included); BINARY(8) over bytes stays rejected; a VARCHAR request over a string keeps the
    // existing character-family behavior.
    RelNode scan = scan("SELECT * FROM fz.bin_src");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    Schema source = new Schema.Parser().parse(load("bin_src.avsc")).getField("b").schema();
    RelDataType unboundedArray =
        typeFactory.createTypeWithNullability(typeFactory.createArrayType(varbinary(scan), -1), true);
    RelDataType target = projection(scan, "b", "extra",
        ImmutableMap.of("raw", varbinary(scan), "optraw", varbinary(scan), "arrraw", unboundedArray, "text",
            typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR, 8), true)));
    Schema b = SchemaUtilities.extractIfOption(relToAvroSchemaConverter
        .convert(projectColumn(scan, 1, "b", genericProject(scan, target, inputRef(scan, 1))), true, false)
        .getField("b").schema());
    for (String leaf : ImmutableList.of("raw", "optRaw", "arrRaw", "text")) {
      Assert.assertEquals(b.getField(leaf).schema().toString(true), source.getField(leaf).schema().toString(true),
          leaf);
    }
    Assert.assertEquals(b.getField("raw").doc(), "Raw bytes");
    Assert.assertEquals(b.getField("raw").getObjectProp("x-raw"), "r");

    Schema coarse = SchemaUtilities.extractIfOption(relToAvroSchemaConverter
        .convert(projectColumn(scan, 1, "b",
            genericProject(scan, binProjection(scan, "raw", binary(scan)), inputRef(scan, 1))), true, false)
        .getField("b").schema());
    Assert.assertEquals(coarse.getField("raw").schema().toString(), "\"bytes\"");

    assertParameterMismatchRejected(
        projectColumn(scan, 1, "b",
            genericProject(scan, binProjection(scan, "raw", binary(scan, 8)), inputRef(scan, 1))),
        "b.raw", "BINARY(8)");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T10 (structural, typed call): retained fields reordered by the target, inside a record and inside array elements
  // and map values. The projected field is canonical here, so its reshaped complex defaults are observable.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT10ReorderedProjectionReshapesNestedRecordDefaults() {
    RelNode project = reorderedCdefProjection();

    Schema.Field field = relToAvroSchemaConverter.convert(project, true, false).getField("c");
    Assert.assertNotNull(field, "projected field keeps the source name c");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field));
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(load("expected/cdef-reordered-c.avsc")).toString(true));
    Assert.assertEquals(new Schema.Parser().parse(field.schema().toString()).toString(true),
        field.schema().toString(true));
  }

  @Test
  public void testT10ReorderedDefaultsSurviveNamespaceNormalization() {
    // The reordered projection followed by the existing stored-view namespace normalization (the operation under
    // test), compared with an independently written normalized contract. Pre-existing shared default-copy coverage.
    Schema projected = relToAvroSchemaConverter.convert(reorderedCdefProjection(), true, false);
    Schema normalized = SchemaUtilities.setupNameAndNamespace(projected, "v", "fz.v");

    Assert.assertEquals(new Schema.Parser().parse(normalized.toString()).toString(true), normalized.toString(true));
    Assert.assertEquals(normalized.toString(true),
        new Schema.Parser().parse(load("expected/cdef-reordered-normalized.avsc")).toString(true));
    Schema c = normalized.getField("c").schema();
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("recs")),
        "[{\"eb\":\"x\",\"ea\":1},{\"eb\":\"w\",\"ea\":4}]");
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("byKey")),
        "{\"k\":{\"vb\":\"y\",\"va\":2},\"j\":{\"vb\":\"v\",\"va\":5}}");
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("rec")),
        "{\"rb\":\"q\",\"ra\":6}");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(normalized.getField("c")));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T17 (structural, typed call): a requested reshape of a multi-member union member is rejected by the projection
  // itself, with no surrounding UNION to fail later.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT17ReshapingOpaqueUnionIsRejectedByProjection() {
    // Calcite represents uniontype<int,struct<a,extra>> as struct<tag,field0,field1>; the target trims field1.
    RelNode scan = scan("SELECT * FROM fz.unr_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType integer = nullable(typeFactory, SqlTypeName.INTEGER);
    RelDataType target = nullableStruct(typeFactory,
        ImmutableList.of(integer, integer, nullableStruct(typeFactory, ImmutableList.of(integer), "a")), "tag",
        "field0", "field1");
    RelNode project = projectColumn(scan, 1, "choice", genericProject(scan, target, inputRef(scan, 1)));

    try {
      Schema result = relToAvroSchemaConverter.convert(project, true, false);
      Assert.fail("Expected the opaque union reshape to be rejected but produced:\n" + result.toString(true));
    } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
      Assert.fail("Reshape must be rejected explicitly, not by " + e, e);
    } catch (RuntimeException e) {
      Assert.assertTrue(String.valueOf(e.getMessage()).contains("choice"),
          "Expected the rejection to name the field path but was: " + e.getMessage());
    }
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T16: truly malformed internal calls, and a different operator printed as generic_project
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT16GenericProjectWithoutOperandsFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "path");
    RexNode call = rexBuilder(scan).makeCall(target, new GenericProjectFunction(target), ImmutableList.of());
    RelNode project = projectColumn(scan, 1, "requestheader", call);

    assertFailsClearly(() -> relToAvroSchemaConverter.convert(project, false, false));
  }

  @Test
  public void testT16GenericProjectWithNonStructReturnForStructOperandFails() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType bigint = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BIGINT), true);
    RelNode project = projectColumn(scan, 1, "requestheader", genericProject(scan, bigint, inputRef(scan, 1)));

    assertFailsMentioningIgnoringCase(() -> relToAvroSchemaConverter.convert(project, false, false), "requestheader");
  }

  @Test
  public void testT16DifferentOperatorNamedGenericProjectKeepsOrdinaryUdfInference() {
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RelDataType target = struct(scan, "pagekey", "path");
    SqlOperator sameName = new SqlUserDefinedFunction(new SqlIdentifier("generic_project", SqlParserPos.ZERO),
        ReturnTypes.explicit(target), null, OperandTypes.VARIADIC, null, null);
    RexBuilder rexBuilder = rexBuilder(scan);
    RexNode call = rexBuilder.makeCall(target, sameName, ImmutableList.of(inputRef(scan, 1),
        rexBuilder.makeLiteral("requestheader"), rexBuilder.makeLiteral("struct<pagekey:string,path:string>")));
    RelNode project = projectColumn(scan, 1, "requestheader", call);

    Schema.Field field = relToAvroSchemaConverter.convert(project, false, false).getField("requestheader");
    Assert.assertNotNull(field, "ordinary UDF output keeps the suggested lowercase column name");
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(("['null',{'type':'record','name':'requestheader','namespace':'rel_avro','fields':["
            + "{'name':'pagekey','type':['null','string'],'default':null},"
            + "{'name':'path','type':['null','string'],'default':null}]}]").replace('\'', '"')).toString(true));
    Assert.assertEquals(field.doc(),
        "Field created in view by applying UDF 'generic_project' with argument(s): "
            + "com.linkedin.events.PageViewEvent.requestHeader, \"requestheader\", "
            + "\"struct<pagekey:string,path:string>\"");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field));
  }

  @Test
  public void testNestedGenericProjectionKeepsSourceMetadata() {
    // generic_project(generic_project($1, {pagekey, path}), {pagekey}): each level projects the source field.
    RelNode scan = scan("SELECT * FROM fz.pv_evolved");
    RexNode inner = genericProject(scan, struct(scan, "pagekey", "path"), inputRef(scan, 1));
    RexNode outer = genericProject(scan, struct(scan, "pagekey"), inner);
    RelNode project = projectColumn(scan, 1, "requestheader", outer);

    Schema.Field field = relToAvroSchemaConverter.convert(project, false, false).getField("requestHeader");
    Assert.assertNotNull(field, "projected field keeps the source spelling requestHeader");
    Assert.assertEquals(field.doc(), "The request header");
    Assert.assertEquals(field.schema().toString(true),
        new Schema.Parser().parse(("{'type':'record','name':'RequestHeader','namespace':'com.linkedin.events',"
            + "'doc':'Request header record','fields':[{'name':'pageKey','type':'string','doc':'Page key'}]}")
            .replace('\'', '"')).toString(true));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // F9 (tester-clarifications section 12a): a standalone root Project is itself the public result, so it must be one
  // complete serializable schema. A non-canonical UNION input stays unresolved until canonical merging.
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testF9StandaloneProjectRetainingIncompatibleSharedBodiesRejects() {
    // f8neg_l uses Shared{x,y} for leftUse and rightUse; projecting {x} and {y} retains both bodies under Shared.
    RelNode scan = scan("SELECT * FROM fz.f8neg_l");
    String literal = catalog.avroLiteral(DB, "f8neg_l");
    assertSharedBodiesRejected(f8negProjection(scan, sharedProjection(scan, 1, "y")));
    Assert.assertEquals(catalog.avroLiteral(DB, "f8neg_l"), literal, "source metadata must not be mutated");
  }

  @Test
  public void testF9StandaloneProjectWithPassThroughSharedUseRejects() {
    // A raw pass-through use keeps the whole source body {x,y}, which differs from the projected {x}.
    RelNode scan = scan("SELECT * FROM fz.f8neg_l");
    assertSharedBodiesRejected(f8negProjection(scan, inputRef(scan, 1)));
  }

  @Test
  public void testF9StandaloneProjectRetainingOneSharedBodySucceeds() {
    // Both uses projected to {x}: one Shared definition, which every use denotes, serialized once and referenced.
    RelNode scan = scan("SELECT * FROM fz.f8neg_l");
    RelNode project = f8negProjection(scan, sharedProjection(scan, 1, "x"));
    String shared =
        "{'type':'record','name':'Shared','namespace':'com.linkedin.f8','fields':[{'name':'x','type':'int'}]}"
            .replace('\'', '"');
    String whole = ("{'type':'record','name':'F8Neg','namespace':'com.linkedin.f8','fields':[{'name':'leftUse','type':"
        + "{'type':'record','name':'Shared','fields':[{'name':'x','type':'int'}]}},{'name':'rightUse','type':'Shared'}]}")
        .replace('\'', '"');
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = relToAvroSchemaConverter.convert(project, strict, false);
      for (String use : new String[] { "leftUse", "rightUse" }) {
        Schema.Field field = actual.getField(use);
        Assert.assertNotNull(field, "strict=" + strict + ": " + use);
        Assert.assertEquals(field.schema().toString(true), new Schema.Parser().parse(shared).toString(true),
            "strict=" + strict + ": " + use + " in memory");
        Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field), use);
      }
      assertReparses(actual);
      Assert.assertEquals(actual.toString(true), new Schema.Parser().parse(whole).toString(true), "strict=" + strict);
    }
  }

  @Test
  public void testF9SameProjectAsNonCanonicalUnionInputIsNotRejectedEagerly() {
    // The rejected standalone Project, as the second UNION input under canonical f8neg_r: the output types are the
    // canonical A{x} and B{y}, so its two differently projected Shared uses never become a retained output identity.
    RelNode scan = scan("SELECT * FROM fz.f8neg_l");
    RelNode project = f8negProjection(scan, sharedProjection(scan, 1, "y"));
    LogicalUnion union =
        LogicalUnion.create(ImmutableList.of(hiveToRelConverter.convertSql("SELECT * FROM fz.f8neg_r"), project), true);
    for (boolean strict : new boolean[] { true, false }) {
      // Raw RelNode conversion: non-strict keeps the existing pre-merge mapping under the F8Neg record.
      String ns = strict ? "com.linkedin.f8" : "com.linkedin.f8.F8Neg";
      String whole =
          ("{'type':'record','name':'F8Neg','namespace':'com.linkedin.f8','fields':[{'name':'leftUse','type':"
              + "{'type':'record','name':'A','namespace':'" + ns + "','fields':[{'name':'x','type':'int'}]}},"
              + "{'name':'rightUse','type':{'type':'record','name':'B','namespace':'" + ns + "','fields':"
              + "[{'name':'y','type':'int'}]}}]}").replace('\'', '"');
      Schema actual = relToAvroSchemaConverter.convert(union, strict, false);
      Assert.assertEquals(actual.getField("leftUse").schema().getFullName(), ns + ".A", "strict=" + strict);
      Assert.assertEquals(actual.getField("rightUse").schema().getFullName(), ns + ".B", "strict=" + strict);
      assertReparses(actual);
      Assert.assertFalse(actual.toString().contains("Shared"), actual.toString());
      Assert.assertEquals(actual.toString(true), new Schema.Parser().parse(whole).toString(true), "strict=" + strict);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------------------------

  private LogicalUnion union(String... branchSql) {
    ImmutableList.Builder<RelNode> inputs = ImmutableList.builder();
    for (String sql : branchSql) {
      inputs.add(hiveToRelConverter.convertSql(sql));
    }
    return LogicalUnion.create(inputs.build(), true);
  }

  /** The table scan under a converted {@code SELECT *}. */
  private RelNode scan(String selectStar) {
    RelNode rel = hiveToRelConverter.convertSql(selectStar);
    return rel instanceof TableScan ? rel : rel.getInput(0);
  }

  private static RexBuilder rexBuilder(RelNode node) {
    return node.getCluster().getRexBuilder();
  }

  private static RexNode inputRef(RelNode input, int index) {
    return rexBuilder(input).makeInputRef(input, index);
  }

  /** A nullable struct of nullable VARCHAR fields, like the rewriter's fuzzy target types. */
  private static RelDataType struct(RelNode node, String... names) {
    RelDataTypeFactory typeFactory = rexBuilder(node).getTypeFactory();
    RelDataType varchar = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARCHAR), true);
    ImmutableList.Builder<RelDataType> types = ImmutableList.builder();
    for (int i = 0; i < names.length; i++) {
      types.add(varchar);
    }
    return typeFactory
        .createTypeWithNullability(typeFactory.createStructType(types.build(), ImmutableList.copyOf(names)), true);
  }

  /** lt_evolved.l without lExtra, every field at its source type except {@code replacedField} (lowercase name). */
  private static RelDataType ltProjection(RelNode scan, String replacedField, RelDataType replacement) {
    return projection(scan, "l", "lextra",
        replacedField == null ? ImmutableMap.of() : ImmutableMap.of(replacedField, replacement));
  }

  /** rep_src.r without extra, every field at its source type except {@code replacedField} (lowercase name). */
  private static RelDataType repProjection(RelNode scan, String replacedField, RelDataType replacement) {
    return projection(scan, "r", "extra", ImmutableMap.of(replacedField, replacement));
  }

  /**
   * The relational type of struct column {@code column} without {@code droppedField}, every field keeping exactly its
   * source relational type except those in {@code replacements} (keyed by lowercase Calcite name).
   */
  private static RelDataType projection(RelNode scan, String column, String droppedField,
      Map<String, RelDataType> replacements) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType source = scan.getRowType().getField(column, false, false).getType();
    ImmutableList.Builder<RelDataType> types = ImmutableList.builder();
    ImmutableList.Builder<String> names = ImmutableList.builder();
    for (RelDataTypeField field : source.getFieldList()) {
      if (field.getName().equals(droppedField)) {
        continue;
      }
      names.add(field.getName());
      types.add(replacements.getOrDefault(field.getName(), field.getType()));
    }
    return typeFactory.createTypeWithNullability(typeFactory.createStructType(types.build(), names.build()), true);
  }

  /** bin_src.b without extra, every field at its source type except {@code replacedField} (lowercase name). */
  private static RelDataType binProjection(RelNode scan, String replacedField, RelDataType replacement) {
    return projection(scan, "b", "extra", ImmutableMap.of(replacedField, replacement));
  }

  /** Unbounded VARBINARY: the Hive type system leaves the binary-family precision unspecified. */
  private static RelDataType varbinary(RelNode scan) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType type = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARBINARY), true);
    Assert.assertEquals(type.getPrecision(), RelDataType.PRECISION_NOT_SPECIFIED, type.getFullTypeString());
    return type;
  }

  private static RelDataType varbinary(RelNode scan, int length) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.VARBINARY, length), true);
  }

  /** Unparameterized BINARY, as the coarse Hive type converter produces. */
  private static RelDataType binary(RelNode scan) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType type = typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BINARY), true);
    Assert.assertEquals(type.getPrecision(), RelDataType.PRECISION_NOT_SPECIFIED, type.getFullTypeString());
    return type;
  }

  private static RelDataType binary(RelNode scan, int length) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.BINARY, length), true);
  }

  /** Must fail explicitly, naming the field path and the requested type; not an incidental runtime error. */
  private void assertParameterMismatchRejected(RelNode project, String path, String requestedType, String... context) {
    try {
      Schema result = relToAvroSchemaConverter.convert(project, true, false);
      Assert.fail("Expected a request for " + requestedType + " at " + path + " to be rejected but produced:\n"
          + result.toString(true));
    } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
      Assert.fail("Mismatch must be rejected explicitly, not by " + e, e);
    } catch (RuntimeException e) {
      String message = String.valueOf(e.getMessage());
      Assert.assertTrue(message.contains(path) && message.contains(requestedType),
          "Expected the rejection to name '" + path + "' and '" + requestedType + "' but was: " + message);
      for (String fragment : context) {
        Assert.assertTrue(message.contains(fragment),
            "Expected the rejection to mention '" + fragment + "' but was: " + message);
      }
    }
  }

  private static RelDataType nullable(RelDataTypeFactory typeFactory, SqlTypeName typeName) {
    return typeFactory.createTypeWithNullability(typeFactory.createSqlType(typeName), true);
  }

  /** cdef_evolved with c projected to {@code struct<rec:struct<rb,ra>, recs:array<struct<eb,ea>>, bykey:map<struct<vb,va>>>}. */
  private RelNode reorderedCdefProjection() {
    RelNode scan = scan("SELECT * FROM fz.cdef_evolved");
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType varchar = nullable(typeFactory, SqlTypeName.VARCHAR);
    RelDataType integer = nullable(typeFactory, SqlTypeName.INTEGER);
    RelDataType rec = nullableStruct(typeFactory, ImmutableList.of(varchar, integer), "rb", "ra");
    RelDataType element = nullableStruct(typeFactory, ImmutableList.of(varchar, integer), "eb", "ea");
    RelDataType value = nullableStruct(typeFactory, ImmutableList.of(varchar, integer), "vb", "va");
    RelDataType target = nullableStruct(typeFactory,
        ImmutableList.of(rec, typeFactory.createTypeWithNullability(typeFactory.createArrayType(element, -1), true),
            typeFactory.createTypeWithNullability(typeFactory.createMapType(varchar, value), true)),
        "rec", "recs", "bykey");
    RelNode project = projectColumn(scan, 1, "c", genericProject(scan, target, inputRef(scan, 1)));
    return project;
  }

  private static RelDataType nullableStruct(RelDataTypeFactory typeFactory, List<RelDataType> types, String... names) {
    return typeFactory.createTypeWithNullability(typeFactory.createStructType(types, ImmutableList.copyOf(names)),
        true);
  }

  /** {@code struct<amount:decimal(precision,scale)>}, dropping the source's extra field. */
  private static RelDataType decimalStruct(RelNode node, int precision, int scale) {
    RelDataTypeFactory typeFactory = rexBuilder(node).getTypeFactory();
    RelDataType decimal =
        typeFactory.createTypeWithNullability(typeFactory.createSqlType(SqlTypeName.DECIMAL, precision, scale), true);
    return typeFactory.createTypeWithNullability(
        typeFactory.createStructType(ImmutableList.of(decimal), ImmutableList.of("amount")), true);
  }

  /** An internally typed call shaped like the rewriter's: operand, column-name literal, Hive type string. */
  private static RexNode genericProject(RelNode node, RelDataType target, RexNode operand) {
    RexBuilder rexBuilder = rexBuilder(node);
    return rexBuilder.makeCall(target, new GenericProjectFunction(target),
        ImmutableList.of(operand, rexBuilder.makeLiteral("col"), rexBuilder.makeLiteral("struct<...>")));
  }

  /** Projects every input column unchanged except {@code index}, which becomes {@code replacement}. */
  private static RelNode projectColumn(RelNode input, int index, String name, RexNode replacement) {
    ImmutableList.Builder<RexNode> exprs = ImmutableList.builder();
    ImmutableList.Builder<String> names = ImmutableList.builder();
    List<String> inputNames = input.getRowType().getFieldNames();
    for (int i = 0; i < inputNames.size(); i++) {
      exprs.add(i == index ? replacement : inputRef(input, i));
      names.add(i == index ? name : inputNames.get(i));
    }
    return LogicalProject.create(input, exprs.build(), names.build());
  }

  /** Must fail at conversion, naming the qualified Shared and both uses; not Avro's later redefinition error. */
  private void assertSharedBodiesRejected(RelNode project) {
    for (boolean strict : new boolean[] { true, false }) {
      Schema result;
      try {
        result = relToAvroSchemaConverter.convert(project, strict, false);
      } catch (SchemaParseException e) {
        throw new AssertionError("Expected an explicit converter rejection, not Avro's redefinition error", e);
      } catch (RuntimeException e) {
        String message = String.valueOf(e.getMessage());
        Assert.assertFalse(message.contains("Can't redefine"), message);
        for (String fragment : new String[] { "com.linkedin.f8.Shared", "leftUse", "rightUse" }) {
          Assert.assertTrue(message.contains(fragment),
              "strict=" + strict + ": expected the rejection to mention '" + fragment + "' but was: " + message);
        }
        continue;
      }
      Assert.fail("strict=" + strict + ": expected the conversion itself to reject, but it returned a schema whose "
          + "Shared uses are " + result.getField("leftUse").schema().getFields() + " and "
          + result.getField("rightUse").schema().getFields());
    }
  }

  /** f8neg_l projected as {@code leftuse: generic_project(leftuse, struct<x>)} and {@code rightuse: right}. */
  private static RelNode f8negProjection(RelNode scan, RexNode right) {
    return LogicalProject.create(scan, ImmutableList.of(sharedProjection(scan, 0, "x"), right),
        ImmutableList.of("leftuse", "rightuse"));
  }

  /** {@code generic_project(column, struct<member>)}, keeping the member's exact source relational type. */
  private static RexNode sharedProjection(RelNode scan, int column, String member) {
    RelDataTypeFactory typeFactory = rexBuilder(scan).getTypeFactory();
    RelDataType source = scan.getRowType().getFieldList().get(column).getType();
    RelDataType target = typeFactory.createStructType(ImmutableList.of(source.getField(member, false, false).getType()),
        ImmutableList.of(member));
    RexBuilder rexBuilder = rexBuilder(scan);
    return rexBuilder.makeCall(target, new GenericProjectFunction(target),
        ImmutableList.of(inputRef(scan, column), rexBuilder.makeLiteral(scan.getRowType().getFieldNames().get(column)),
            rexBuilder.makeLiteral("struct<" + member + ":int>")));
  }

  /** Must fail with an exception that names the generic projection, not an incidental index/null error. */
  private static void assertFailsClearly(Runnable conversion) {
    try {
      conversion.run();
    } catch (IndexOutOfBoundsException | NullPointerException e) {
      Assert.fail("Malformed call must be rejected explicitly, not by " + e, e);
    } catch (RuntimeException e) {
      String message = String.valueOf(e.getMessage()).toLowerCase(Locale.ROOT);
      Assert.assertTrue(message.contains("generic_project") || message.contains("genericproject"),
          "Expected the error to name the generic projection but was: " + e.getMessage());
      return;
    }
    Assert.fail("Expected malformed generic_project call to be rejected");
  }
}
