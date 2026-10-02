/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.functions.GenericProjectFunction;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.FuzzyUnionFixtures.*;


/**
 * Natural fuzzy-UNION regressions: every case converts an existing stored view whose base tables evolved after the
 * view was saved, lets {@code HiveToRelConverter.convertView} insert the {@code generic_project} calls, and checks
 * the exact Avro schema inferred with {@code forceLowercase=false}.
 *
 * <p>Expected schemas are hand-written contracts under {@code fuzzyunion/expected}; {@code @VIEW@} stands for the
 * view name that non-strict inference assigns to the top-level record.
 */
public class FuzzyUnionAvroSchemaTests {
  private FuzzyUnionTestCatalog catalog;
  private ViewToAvroSchemaConverter converter;

  @BeforeClass
  public void beforeClass() {
    registerUdfs();
    catalog = buildCatalog();
    converter = ViewToAvroSchemaConverter.create(catalog);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T1-T4: incident shape, branch order, nested casing, SQL aliases
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT1GeneratedRightBranchKeepsSourceCasing() {
    assertProjections("v_t1", 0, 1);
    assertOperandKinds("v_t1", RexInputRef.class);

    Schema actual = nonStrict("v_t1");
    assertView(actual, "pageview.avsc", "v_t1");
    Assert.assertFalse(actual.toString().contains("trackingCode"), "Evolved extra field must be dropped");
    Assert.assertFalse(actual.toString().contains("rel_avro"), "No synthetic derived identity may leak");
    Assert.assertFalse(actual.toString().contains("generic_project"), "Projected field keeps source metadata");
  }

  @Test
  public void testT2GeneratedLeftBranchKeepsSourceCasing() {
    assertProjections("v_t2", 1, 0);
    assertOperandKinds("v_t2", RexInputRef.class);

    assertView(nonStrict("v_t2"), "pageview.avsc", "v_t2");
  }

  @Test
  public void testT1StrictModeKeepsSourceIdentity() {
    assertSchema(converter.toAvroSchema(DB, "v_t1", true, false), "expected/pageview-strict.avsc");
  }

  @Test
  public void testT3NestedCasingUnderLowercaseTopLevelColumn() {
    assertProjections("v_t3", 0, 1);
    assertView(nonStrict("v_t3"), "header.avsc", "v_t3");

    assertProjections("v_t3r", 1, 0);
    assertView(nonStrict("v_t3r"), "header.avsc", "v_t3r");
  }

  @Test
  public void testT4SqlAliasesAroundProjectedColumn() {
    // eventid AS eventid is a lowercase-only alias (keeps eventId); request_header and tag_col are real renames.
    // The projected column sits between two other columns, exposing any extra or missing name consumption.
    assertProjections("v_t4", 1, 0);
    assertOperandKinds("v_t4", RexInputRef.class);
    assertView(nonStrict("v_t4"), "pageview-aliased.avsc", "v_t4");
  }

  @Test
  public void testT4RenamedProjectionKeepsOnlyDeclaredAvroAliases() {
    // info AS details: the renamed field keeps its declared alias "information" and gains no alias for "info".
    assertProjections("v_t4_meta", 1, 0);
    Schema actual = nonStrict("v_t4_meta");
    assertView(actual, "meta.avsc", "v_t4_meta", "@FIELD@", "details");
    Assert.assertEquals(new ArrayList<>(actual.getField("details").aliases()), list("information"));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T5: three or more naturally rewritten branches and nested views
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT5ThreeBranchesUnionAllMetadata() {
    // Only the third branch makes hdr.memberId nullable; both evolved extras are dropped.
    assertProjections("v_t5", 0, 1, 1);
    Schema actual = nonStrict("v_t5");
    assertView(actual, "ev.avsc", "v_t5");
    Assert.assertFalse(actual.toString().contains("browser") || actual.toString().contains("device"));
  }

  @Test
  public void testT5FourBranches() {
    assertProjections("v_t5_four", 0, 1, 1, 1);
    assertView(nonStrict("v_t5_four"), "ev.avsc", "v_t5_four");
  }

  @Test
  public void testT5NestedViewBranch() {
    RelNode rel = rel("v_t5_nested");
    Assert.assertEquals(allGenericProjects(rel).size(), 2, "inner view and outer view each project one branch");
    assertView(nonStrict("v_t5_nested"), "ev.avsc", "v_t5_nested");
  }

  @Test
  public void testT5IncompatibilityOnlyInThirdBranch() {
    assertProjections("v_t5_bad", 0, 1, 1);
    assertFailsMentioning(() -> nonStrict("v_t5_bad"), "checksum", "Md5", "16", "8");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T7: direct/direct, generated/generated, no evolution, reorder-only and lowercase controls
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT7NoEvolutionDirectDirect() {
    assertProjections("v_t7_same", 0, 0);
    assertView(nonStrict("v_t7_same"), "pageview.avsc", "v_t7_same");
  }

  @Test
  public void testT7GeneratedGenerated() {
    assertProjections("v_t7_gengen", 1, 1);
    assertOperandKinds("v_t7_gengen", RexInputRef.class, RexInputRef.class);
    assertView(nonStrict("v_t7_gengen"), "pageview.avsc", "v_t7_gengen");
  }

  @Test
  public void testT7ReorderOnlyEvolution() {
    assertProjections("v_t7_reorder", 0, 1);
    assertView(nonStrict("v_t7_reorder"), "pageview.avsc", "v_t7_reorder");

    // The first branch defines the common field order.
    assertProjections("v_t7_reorder_r", 0, 1);
    assertView(nonStrict("v_t7_reorder_r"), "pageview-reordered.avsc", "v_t7_reorder_r");
  }

  @Test
  public void testT7AlreadyLowercaseControl() {
    assertProjections("v_t7_lower", 0, 1);
    assertView(nonStrict("v_t7_lower"), "lowercase.avsc", "v_t7_lower");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T8-T12: nested containers, nullability, defaults, record/field metadata, logical/fixed/enum identities
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT8RecordsInsideArraysAndMaps() {
    assertProjections("v_t8", 0, 3);
    Schema actual = nonStrict("v_t8");
    assertView(actual, "nest.avsc", "v_t8");
    Assert.assertEquals(actual.getField("items").schema().getProp("x-array-prop"), "arr");
    Assert.assertEquals(actual.getField("attrs").schema().getProp("x-map-prop"), "map");

    assertProjections("v_t8r", 3, 0);
    assertView(nonStrict("v_t8r"), "nest.avsc", "v_t8r");
  }

  @Test
  public void testT9NullabilityIsUnionOfActualBranches() {
    // Intended tightening: the projected branch keeps its required leaves (req, r, r.a) instead of the blanket
    // nullable fuzzy target type. Optional envelopes, element and value nullability are kept as in the sources.
    assertProjections("v_t9", 0, 2);
    assertView(nonStrict("v_t9"), "nullability.avsc", "v_t9");
    assertProjections("v_t9r", 2, 0);
    assertView(nonStrict("v_t9r"), "nullability.avsc", "v_t9r");
  }

  @Test
  public void testT9NullableBranchWidensRequiredLeaf() {
    // req and r.a are nullable only in the evolved branch. The first branch owns the default: none when the
    // required base branch is first, its explicit null default when the evolved branch is first.
    Schema widened = nonStrict("v_t9n");
    assertView(widened, "nullability-widened-no-default.avsc", "v_t9n");
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(widened.getField("r").schema().getField("a")),
        "A widened field without a canonical default must not gain a fabricated null default");

    assertView(nonStrict("v_t9nr"), "nullability-widened-null-default.avsc", "v_t9nr");
  }

  @Test
  public void testT10DefaultsSurviveProjectionAndMerge() {
    for (String view : list("v_t10", "v_t10r")) {
      Schema actual = nonStrict(view);
      assertView(actual, "defaults.avsc", view);

      Schema d = actual.getField("d").schema();
      Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(d.getField("noDef")), view);
      Assert.assertTrue(AvroCompatibilityHelper.fieldHasDefault(d.getField("nullDef")), view);
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(d.getField("nullDef")), "null", view);
      // In v_t10r the projected complex default drops only the removed field z.
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(d.getField("recDef")),
          "{\"x\":1,\"y\":\"a\"}", view);
    }
    assertProjections("v_t10", 0, 1);
    assertProjections("v_t10r", 1, 0);
  }

  @Test
  public void testT10RecordDefaultsInsideArraysAndMapsAreProjected() {
    // c.recs is array<E>, c.byKey is map<V>, c.rec is a record; each evolved element record gained an extra field that
    // also appears in its complex default. When the projected branch is canonical (first), its defaults must be
    // projected recursively: extras removed from every array element and map value, retained values unchanged.
    // Strict mode: non-strict normalization of array/map-of-record defaults already fails before this change
    // ("Unknown datum class: GenericData$Record", even without UNION) and is escalated separately.
    assertProjections("v_t10cr", 1, 0);
    Schema projectedFirst = converter.toAvroSchema(DB, "v_t10cr", true, false);
    assertSchema(projectedFirst, "expected/cdef-strict-projected.avsc");
    Schema c = projectedFirst.getField("c").schema();
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("recs")),
        "[{\"ea\":1,\"eb\":\"x\"},{\"ea\":4,\"eb\":\"w\"}]");
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("byKey")),
        "{\"k\":{\"va\":2,\"vb\":\"y\"},\"j\":{\"va\":5,\"vb\":\"v\"}}");
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("rec")),
        "{\"ra\":6,\"rb\":\"q\"}");
    Schema element = c.getField("recs").schema().getElementType();
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(element.getField("ea")));
    Assert.assertNull(element.getField("eExtra"));

    // The direct branch is canonical: its own defaults win; the projected branch's differing defaults do not leak.
    assertProjections("v_t10c", 0, 1);
    assertSchema(converter.toAvroSchema(DB, "v_t10c", true, false), "expected/cdef-strict-base.avsc");
  }

  @Test
  public void testT10NonStrictPlainViewsKeepCollectionDefaults() {
    // Pre-existing shared default-copy failure (approved D3 coverage, not a casing reproduction): non-strict namespace
    // normalization must keep array-of-record and map-of-record defaults unchanged. No projection is involved; the
    // evolved control keeps every extra field and its declared default.
    Assert.assertTrue(allGenericProjects(rel("v_cdef_base_plain")).isEmpty());
    Assert.assertTrue(allGenericProjects(rel("v_cdef_plain")).isEmpty());
    assertView(nonStrict("v_cdef_base_plain"), "cdef-nonstrict-base.avsc", "v_cdef_base_plain");
    Schema evolved = nonStrict("v_cdef_plain");
    assertView(evolved, "cdef-nonstrict-evolved-plain.avsc", "v_cdef_plain");
    assertCollectionDefaults(evolved,
        "[{\"ea\":1,\"eExtra\":true,\"eb\":\"x\"},{\"ea\":4,\"eExtra\":false,\"eb\":\"w\"}]",
        "{\"k\":{\"vExtra\":true,\"va\":2,\"vb\":\"y\"},\"j\":{\"vExtra\":false,\"va\":5,\"vb\":\"v\"}}",
        "{\"ra\":6,\"rExtra\":true,\"rb\":\"q\"}");
  }

  @Test
  public void testT10NonStrictUnionKeepsCanonicalCollectionDefaults() {
    // Same defaults through non-strict pre-merge and final view normalization, in both canonical orders.
    assertProjections("v_t10c", 0, 1);
    Schema baseFirst = nonStrict("v_t10c");
    assertView(baseFirst, "cdef-nonstrict-base.avsc", "v_t10c");
    assertCollectionDefaults(baseFirst, "[{\"ea\":1,\"eb\":\"x\"}]", "{\"k\":{\"va\":2,\"vb\":\"y\"}}",
        "{\"ra\":3,\"rb\":\"z\"}");

    assertProjections("v_t10cr", 1, 0);
    Schema projectedFirst = nonStrict("v_t10cr");
    assertView(projectedFirst, "cdef-nonstrict-projected.avsc", "v_t10cr");
    assertCollectionDefaults(projectedFirst, "[{\"ea\":1,\"eb\":\"x\"},{\"ea\":4,\"eb\":\"w\"}]",
        "{\"k\":{\"va\":2,\"vb\":\"y\"},\"j\":{\"va\":5,\"vb\":\"v\"}}", "{\"ra\":6,\"rb\":\"q\"}");
  }

  /** Field defaults of c.recs/c.byKey/c.rec are present with exactly these values; id, c and leaves have none. */
  static void assertCollectionDefaults(Schema view, String recs, String byKey, String rec) {
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(view.getField("id")));
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(view.getField("c")));
    Schema c = view.getField("c").schema();
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("recs")), recs);
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("byKey")), byKey);
    Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(c.getField("rec")), rec);
    Schema element = c.getField("recs").schema().getElementType();
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(element.getField("ea")));
    Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(c.getField("rec").schema().getField("ra")));
  }

  @Test
  public void testT11RecordAndFieldMetadataNonStrict() {
    assertView(nonStrict("v_t11"), "meta.avsc", "v_t11", "@FIELD@", "info");
    assertView(nonStrict("v_t11r"), "meta.avsc", "v_t11r", "@FIELD@", "info");

    Schema info = nonStrict("v_t11r").getField("info").schema();
    Assert.assertTrue(info.getField("errInfo").schema().isError(), "error record flag must survive");
  }

  @Test
  public void testT11RecordAndFieldMetadataStrict() {
    assertSchema(converter.toAvroSchema(DB, "v_t11", true, false), "expected/meta-strict.avsc");
    assertSchema(converter.toAvroSchema(DB, "v_t11r", true, false), "expected/meta-strict.avsc");
  }

  @Test
  public void testT12LogicalFixedAndEnumIdentities() {
    // Decimal stays outside the projected struct: the existing rewriter cannot express DECIMAL in its target type.
    assertProjections("v_t12", 0, 1);
    assertView(nonStrict("v_t12"), "logical.avsc", "v_t12");
    assertProjections("v_t12r", 1, 0);
    assertView(nonStrict("v_t12r"), "logical.avsc", "v_t12r");

    assertSchema(converter.toAvroSchema(DB, "v_t12", true, false), "expected/logical-strict.avsc");
    assertSchema(converter.toAvroSchema(DB, "v_t12r", true, false), "expected/logical-strict.avsc");

    // The enum's declared alias keeps its original qualified identity through non-strict namespace normalization, and
    // its typed custom property survives the enum-specific copy paths.
    for (String view : list("v_t12", "v_t12r")) {
      Schema color = nonStrict(view).getField("l").schema().getField("color").schema();
      Assert.assertEquals(color.getNamespace(), "fz." + view + "." + view + ".L", view);
      Assert.assertEquals(new ArrayList<>(color.getAliases()), list("com.linkedin.lt.OldColor"), view);
      Assert.assertEquals(AvroCompatibilityHelper.getSchemaPropAsJsonString(color, "x-enum-prop"), "{\"v\":1}", view);
      Assert.assertEquals(AvroCompatibilityHelper.getEnumDefault(color), "RED", view);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T13-T15: strict projection failures, ambiguous casing, incompatible named types
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT13IncompatiblePrimitiveLeafFails() {
    // The rewriter only compares structure; string <- int is not a projection.
    assertProjections("v_neg_leaf", 0, 1);
    assertFailsMentioning(() -> nonStrict("v_neg_leaf"), "rh", "alpha");
  }

  @Test
  public void testT13RecordWherePrimitiveRequestedFails() {
    assertFailsMentioning(() -> nonStrict("v_neg_rec"), "rh", "alpha");
  }

  @Test
  public void testT13WrongContainerKindFails() {
    assertFailsMentioning(() -> nonStrict("v_neg_kind"), "rh", "arrfield");
  }

  @Test
  public void testT14AmbiguousCaseInsensitiveProjectionFails() {
    // Avro S has both Foo and foo; the projection requests foo. Exact spelling must not pick one.
    assertProjections("v_amb", 0, 1);
    assertFailsMentioning(() -> nonStrict("v_amb"), "Foo", "foo");
  }

  @Test
  public void testT14AmbiguousFieldAccessOperandFails() {
    assertOperandKinds("v_amb_acc", RexFieldAccess.class);
    assertFailsMentioning(() -> nonStrict("v_amb_acc"), "Foo", "foo");
  }

  @Test
  public void testT14AmbiguousAccessUnderOrdinalReturnUdfFails() {
    assertOperandKinds("v_amb_ord", RexCall.class);
    assertFailsMentioning(() -> nonStrict("v_amb_ord"), "Foo", "foo");
  }

  @Test
  public void testT14AccessPathResolvesCorrectDepth() {
    // Source has s.x.a (int, "outer a") and s.child.x.a (long, "inner a"); the operand is s.child.x.
    assertOperandKinds("v_depth", RexFieldAccess.class);
    assertView(nonStrict("v_depth"), "depth-direct.avsc", "v_depth");
    assertView(nonStrict("v_depth_r"), "depth-projected.avsc", "v_depth_r");

    // Source has s.id and s.child.id; the operand is s.child.
    assertOperandKinds("v_depth2", RexFieldAccess.class);
    assertView(nonStrict("v_depth2"), "depth2-direct.avsc", "v_depth2");
    assertView(nonStrict("v_depth2_r"), "depth2-projected.avsc", "v_depth2_r");
  }

  @Test
  public void testT14ItemAccessOperandResolvesSourceMetadata() {
    // arr[0].child and m['k'].child inline to generic_project(ITEM(...).child). The accessed struct keeps its source
    // identity and the access path's existing nullability (ITEM does not add a null envelope in the existing
    // field-access policy; see the non-helper characterization in the test contract).
    assertOperandAccessOn("v_item_arr", "ITEM");
    assertView(nonStrict("v_item_arr"), "access-direct-doc.avsc", "v_item_arr");

    assertOperandAccessOn("v_item_arr_r", "ITEM");
    assertView(nonStrict("v_item_arr_r"), "access-required.avsc", "v_item_arr_r", "@DOC@", "array child");

    assertOperandAccessOn("v_item_map_r", "ITEM");
    assertView(nonStrict("v_item_map_r"), "access-required.avsc", "v_item_map_r", "@DOC@", "map child");
  }

  @Test
  public void testT14AmbiguousSegmentUnderItemFails() {
    // Array element has both Foo and foo; arr[0].foo must not select the first case-insensitive match.
    assertOperandAccessOn("v_item_amb", "ITEM");
    assertFailsMentioning(() -> nonStrict("v_item_amb"), "Foo", "foo");
  }

  @Test
  public void testT14OrdinaryAmbiguousAccessKeepsExistingPolicy() {
    // Without a generated projection, s.foo keeps its pre-existing first-match lookup (Foo, record F1).
    Assert.assertTrue(allGenericProjects(rel("v_amb_plain")).isEmpty());
    assertView(nonStrict("v_amb_plain"), "ordinary-ambiguous-access.avsc", "v_amb_plain");
  }

  @Test
  public void testT21FieldAccessOnUdfResultOperand() {
    // MakeNested(id).child: the helper operand is a field access on a metadata-free UDF call. Only the accessed child
    // is inferred (existing derived field-access policy: required because id is required, derived nested fields),
    // never the whole parent struct; the evolved extra is dropped.
    assertProjections("v_udf_access", 0, 1);
    assertOperandAccessOn("v_udf_access", "com.linkedin.coral.schema.avro.FuzzyUnionMakeNested");
    assertView(nonStrict("v_udf_access"), "derived-required-direct-doc.avsc", "v_udf_access");

    assertProjections("v_udf_access_r", 1, 0);
    assertOperandAccessOn("v_udf_access_r", "com.linkedin.coral.schema.avro.FuzzyUnionMakeNested");
    assertView(nonStrict("v_udf_access_r"), "udf-access-first.avsc", "v_udf_access_r");
  }

  @Test
  public void testT15FixedSizeMismatchFailsInBothModes() {
    assertFailsMentioning(() -> nonStrict("v_fx_size"), "Md5", "16", "8");
    assertFailsMentioning(() -> converter.toAvroSchema(DB, "v_fx_size", true, false), "Md5", "16", "8");
    assertFailsMentioning(() -> nonStrict("v_fx_name"), "Md5", "Sha", "16", "8");
  }

  @Test
  public void testStrictModeStillRejectsTopLevelNamespaceMismatch() {
    assertFailsMentioning(() -> converter.toAvroSchema(DB, "v_t21", true, false), "namespace");
  }

  @Test
  public void testT15SameSizeFixedControls() {
    assertView(nonStrict("v_fx_same"), "fixed-same.avsc", "v_fx_same");
    // Existing non-strict behavior ignores a namespace-only difference; strict mode still rejects it.
    assertView(nonStrict("v_fx_ns"), "fixed-same.avsc", "v_fx_ns");
    assertFailsMentioning(() -> converter.toAvroSchema(DB, "v_fx_ns", true, false), "Md5");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T17-T18: named-type reuse, opaque unions, source immutability, both catalog entry points
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT17SharedNamedRecordProjectedTwice() {
    assertProjections("v_addr", 0, 2);
    Schema actual = nonStrict("v_addr");
    assertView(actual, "address.avsc", "v_addr");
    // toString prints the second Address as a reference to the first, and Avro record equality ignores docs and
    // aliases. Compare each in-memory definition standalone against the complete contract.
    String address = new Schema.Parser().parse(load("expected/address-definition.avsc")).toString(true);
    Assert.assertEquals(SchemaUtilities.extractIfOption(actual.getField("home").schema()).toString(true), address);
    Assert.assertEquals(SchemaUtilities.extractIfOption(actual.getField("work").schema()).toString(true), address);
  }

  @Test
  public void testT17UnchangedOpaqueUnionInsideProjectedRecord() {
    assertProjections("v_un", 0, 1);
    assertView(nonStrict("v_un"), "opaque-union.avsc", "v_un");
  }

  @Test
  public void testT17ReshapingOpaqueUnionMemberFails() {
    // Requested reshape of a multi-member union member is unsupported: the projection itself must reject it with the
    // field path, rather than a later UNION merge comparing an opaque union with an invented record.
    assertProjections("v_unr", 0, 1);
    assertFailsMentioning(() -> nonStrict("v_unr"), "choice");
    try {
      nonStrict("v_unr");
    } catch (RuntimeException e) {
      Assert.assertFalse(String.valueOf(e.getMessage()).contains("LogicalUnion"),
          "The reshape must be rejected by the projection, not by the later UNION merge: " + e.getMessage());
    }
  }

  @Test
  public void testT17SourcesAreNotMutatedAndRepeatedConversionIsStable() {
    String literal = catalog.avroLiteral(DB, "def_evolved");
    String tableBefore = converter.toAvroSchema(DB, "def_evolved").toString(true);

    String first = nonStrict("v_t10r").toString(true);
    String second = nonStrict("v_t10r").toString(true);

    Assert.assertEquals(second, first);
    Assert.assertEquals(catalog.avroLiteral(DB, "def_evolved"), literal);
    Assert.assertEquals(converter.toAvroSchema(DB, "def_evolved").toString(true), tableBefore);
  }

  @Test
  public void testT18CoralCatalogEntryPointMatchesMetastoreEntryPoint() {
    ViewToAvroSchemaConverter catalogConverter = ViewToAvroSchemaConverter.create(catalog.asCoralCatalog());
    assertView(catalogConverter.toAvroSchema(DB, "v_t1", false, false), "pageview.avsc", "v_t1");
    assertView(catalogConverter.toAvroSchema(DB, "v_t19r", false, false), "access-nested-doc.avsc", "v_t19r");
    assertView(catalogConverter.toAvroSchema(DB, "v_t22", false, false), "derived-required-direct-doc.avsc", "v_t22");
    Assert.assertEquals(catalogConverter.toAvroSchema(DB, "v_t1", true, false).toString(true),
        converter.toAvroSchema(DB, "v_t1", true, false).toString(true));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T19-T23: naturally inlined field-access, expression, UDF and nested-view operands
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT19AliasedNestedStructAccess() {
    assertProjections("v_t19", 0, 1);
    assertOperandKinds("v_t19", RexFieldAccess.class);
    assertView(nonStrict("v_t19"), "access-direct-doc.avsc", "v_t19");

    assertProjections("v_t19r", 1, 0);
    assertOperandKinds("v_t19r", RexFieldAccess.class);
    assertView(nonStrict("v_t19r"), "access-nested-doc.avsc", "v_t19r");
  }

  @Test
  public void testT20NullableAncestorWidensAccessedStruct() {
    // s is nullable, s.child and its leaves are required: b becomes nullable, its retained leaves stay required.
    assertOperandKinds("v_t20", RexFieldAccess.class);
    assertView(nonStrict("v_t20"), "access-nullable-direct-doc.avsc", "v_t20");
    assertOperandKinds("v_t20r", RexFieldAccess.class);
    assertView(nonStrict("v_t20r"), "access-nullable-nested-doc.avsc", "v_t20r");
  }

  @Test
  public void testT21StructConstructorOperand() {
    // Metadata-free named_struct keeps today's derived inference applied to the operand itself: nullable result
    // (literal operands), derived nested nullability/defaults/doc, the evolved "extra" dropped. Alignment keeps the
    // first branch's spelling and metadata.
    assertProjections("v_t21", 0, 1);
    assertOperandKinds("v_t21", RexCall.class);
    assertView(nonStrict("v_t21"), "derived-nullable-direct-doc.avsc", "v_t21");

    assertProjections("v_t21r", 1, 0);
    assertOperandKinds("v_t21r", RexCall.class);
    assertView(nonStrict("v_t21r"), "named-struct-first.avsc", "v_t21r");
  }

  @Test
  public void testT21ConditionalOperand() {
    assertOperandKinds("v_t21_if", RexCall.class);
    assertView(nonStrict("v_t21_if"), "derived-nullable-direct-doc.avsc", "v_t21_if");
  }

  @Test
  public void testT22TransformingUdfOperand() {
    // Intended tightening: the UDF's only operand is a required column, so the operand-derived result is required.
    // The old helper result was always nullable because its literal operands made it look nullable.
    assertProjections("v_t22", 0, 1);
    assertOperandKinds("v_t22", RexCall.class);
    assertView(nonStrict("v_t22"), "derived-required-direct-doc.avsc", "v_t22");

    assertProjections("v_t22r", 1, 0);
    assertView(nonStrict("v_t22r"), "udf-first.avsc", "v_t22r");
  }

  @Test
  public void testT22OrdinalReturnUdfKeepsSelectedArgumentMetadata() {
    assertOperandKinds("v_t22_ord", RexCall.class);
    assertView(nonStrict("v_t22_ord"), "access-nullable-direct-doc.avsc", "v_t22_ord");
    assertOperandKinds("v_t22_ord_r", RexCall.class);
    assertView(nonStrict("v_t22_ord_r"), "access-nullable-nested-doc.avsc", "v_t22_ord_r");
  }

  @Test
  public void testT23NestedViewRenamesInnerStruct() {
    // inner_rename selects s.child AS hdr; after inlining the outer helper's operand is the access $1.child.
    assertOperandKinds("v_t23", RexFieldAccess.class);
    assertView(nonStrict("v_t23"), "access-direct-doc.avsc", "v_t23");
    assertOperandKinds("v_t23r", RexFieldAccess.class);
    assertView(nonStrict("v_t23r"), "access-nested-doc.avsc", "v_t23r");
  }

  @Test
  public void testT23FieldAccessAboveFuzzyUnion() {
    assertView(nonStrict("v_t23_outer"), "outer-access.avsc", "v_t23_outer");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // T24: unique case-only direct/direct UNION (no projection)
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT24UniqueCaseOnlyUnionTakesFirstBranchSpelling() {
    assertProjections("v_t24", 0, 0);
    assertView(nonStrict("v_t24"), "case-camel-first.avsc", "v_t24");
    assertView(nonStrict("v_t24r"), "case-lower-first.avsc", "v_t24r");
  }

  @Test
  public void testT24ForceLowercaseStillLowercasesBothOrders() {
    assertView(converter.toAvroSchema(DB, "v_t24", false, true), "case-forced-lowercase.avsc", "v_t24", "@CASE@",
        "camel");
    assertView(converter.toAvroSchema(DB, "v_t24r", false, true), "case-forced-lowercase.avsc", "v_t24r", "@CASE@",
        "lower");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------------------------

  private Schema nonStrict(String view) {
    return converter.toAvroSchema(DB, view, false, false);
  }

  private RelNode rel(String view) {
    return new HiveToRelConverter(catalog).convertView(DB, view);
  }

  private void assertProjections(String view, Integer... expectedPerBranch) {
    Assert.assertEquals(projectionCounts(rel(view)), counts(expectedPerBranch),
        "generic_project calls per UNION branch of " + view);
  }

  /** Asserts the operand kind of each generic_project call in plan order. */
  private void assertOperandKinds(String view, Class<?>... kinds) {
    List<RexCall> calls = allGenericProjects(rel(view));
    Assert.assertEquals(calls.size(), kinds.length, "generic_project calls in " + view);
    for (int i = 0; i < kinds.length; i++) {
      RexCall call = calls.get(i);
      Assert.assertTrue(call.getOperator() instanceof GenericProjectFunction);
      RexNode operand = call.getOperands().get(0);
      Assert.assertTrue(kinds[i].isInstance(operand), view + ": expected " + kinds[i].getSimpleName()
          + " operand but was " + operand.getClass().getSimpleName() + " " + operand);
    }
  }

  /** The single generic_project operand must be a field access whose reference is a call to {@code operatorName}. */
  private void assertOperandAccessOn(String view, String operatorName) {
    List<RexCall> calls = allGenericProjects(rel(view));
    Assert.assertEquals(calls.size(), 1, "generic_project calls in " + view);
    RexNode operand = calls.get(0).getOperands().get(0);
    Assert.assertTrue(operand instanceof RexFieldAccess, view + ": operand was " + operand);
    RexNode reference = ((RexFieldAccess) operand).getReferenceExpr();
    Assert.assertTrue(
        reference instanceof RexCall && ((RexCall) reference).getOperator().getName().equals(operatorName),
        view + ": expected field access on " + operatorName + " but was " + operand);
  }

  private static void assertView(Schema actual, String resource, String view, String... replacements) {
    String json = load("expected/" + resource).replace("@VIEW@", view);
    for (int i = 0; i < replacements.length; i += 2) {
      json = json.replace(replacements[i], replacements[i + 1]);
    }
    assertReparses(actual);
    Assert.assertEquals(actual.toString(true), new Schema.Parser().parse(json).toString(true));
  }

  private static void assertSchema(Schema actual, String resource) {
    FuzzyUnionFixtures.assertSchema(actual, resource);
  }

  /** Conversion must fail; every fragment must appear somewhere in the exception's message chain. */
  static void assertFailsMentioning(Callable<?> conversion, String... fragments) {
    assertFails(conversion, false, fragments);
  }

  static void assertFailsMentioningIgnoringCase(Callable<?> conversion, String... fragments) {
    assertFails(conversion, true, fragments);
  }

  private static void assertFails(Callable<?> conversion, boolean ignoreCase, String... fragments) {
    Object result;
    try {
      result = conversion.call();
    } catch (RuntimeException e) {
      StringBuilder messages = new StringBuilder();
      for (Throwable t = e; t != null; t = t.getCause()) {
        messages.append(t.getMessage()).append('\n');
      }
      String text = ignoreCase ? messages.toString().toLowerCase(Locale.ROOT) : messages.toString();
      for (String fragment : fragments) {
        Assert.assertTrue(text.contains(ignoreCase ? fragment.toLowerCase(Locale.ROOT) : fragment),
            "Expected failure to mention '" + fragment + "' but was:\n" + messages);
      }
      return;
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    Assert.fail("Expected conversion to fail but it produced:\n"
        + (result instanceof Schema ? ((Schema) result).toString(true) : result));
  }

  private static List<String> list(String... values) {
    List<String> result = new ArrayList<>();
    for (String value : values) {
      result.add(value);
    }
    return result;
  }
}
