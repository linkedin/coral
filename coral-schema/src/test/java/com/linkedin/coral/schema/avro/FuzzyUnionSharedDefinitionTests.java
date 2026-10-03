/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.FuzzyUnionFixtures.*;


/**
 * Final-review F8 (tester-clarifications section 12): one output named record used by several fields, arrays, maps or
 * records must resolve to one complete definition. Retained containing defaults of every occurrence constrain its
 * nullable option order together; a default-less occurrence imposes nothing. Each case first compares every in-memory
 * occurrence of the shared full name with the single expected definition (docs, aliases, properties included), then
 * the whole schema with an exact contract that is serialized and reparsed with default validation.
 *
 * <p>Fixtures: {@code com.linkedin.f8.Shared{x:int}} in {@code *_l}; {@code *_r} evolves it to {@code x:[null,int]} plus
 * {@code extra}. Non-strict names follow the existing namespace mapping unchanged.
 */
public class FuzzyUnionSharedDefinitionTests {
  private static final String SHARED = "com.linkedin.f8.Shared";

  private FuzzyUnionTestCatalog catalog;
  private ViewToAvroSchemaConverter converter;

  @BeforeClass
  public void beforeClass() {
    registerUdfs();
    catalog = buildCatalog();
    converter = ViewToAvroSchemaConverter.create(catalog);
  }

  @Test
  public void testF8LateDefaultConstrainsEarlierDefaultlessUse() {
    // a (no default) precedes b (default {"x":1}); both use Shared, whose x must be int-first everywhere.
    Assert.assertEquals(placement("v_f8"), counts(0, 2));
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = convert("v_f8", strict);
      assertSharedOccurrences(actual, "f8-fwd", "v_f8", strict, sharedName("v_f8", strict), 2);
      assertContract(actual, "f8-fwd", "v_f8", strict);
      Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(actual.getField("a")));
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(actual.getField("b")), "{\"x\":1}");
      Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(actual.getField("a").schema().getField("x")));
    }
  }

  @Test
  public void testF8EarlyDefaultConstrainsLaterDefaultlessUse() {
    // Same constraint discovered first (b precedes a): the field order is kept and the shared body is the same.
    Assert.assertEquals(placement("v_f8e"), counts(0, 2));
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = convert("v_f8e", strict);
      assertSharedOccurrences(actual, "f8e-fwd", "v_f8e", strict, sharedName("v_f8e", strict), 2);
      assertContract(actual, "f8e-fwd", "v_f8e", strict);
    }
  }

  @Test
  public void testF8SharedBodyThroughRecordArrayAndMapUses() {
    // d (no default), arr (2 element defaults), m (2 entry defaults) and h.s (record member default) all reach Shared.
    // Strict: one full name for all four. Non-strict: existing mapping puts h.s under the Holder namespace.
    Assert.assertEquals(placement("v_f8c"), counts(0, 4));
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = convert("v_f8c", strict);
      assertSharedOccurrences(actual, "f8c-fwd", "v_f8c", strict, sharedName("v_f8c", strict), strict ? 4 : 3);
      if (!strict) {
        assertSharedOccurrences(actual, "f8c-fwd", "v_f8c", false, "fz.v_f8c.v_f8c.Holder.Shared", 1);
      }
      assertContract(actual, "f8c-fwd", "v_f8c", strict);
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(actual.getField("arr")),
          "[{\"x\":1},{\"x\":2}]");
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(actual.getField("m")),
          "{\"k\":{\"x\":3},\"j\":{\"x\":4}}");
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(actual.getField("h")),
          "{\"s\":{\"x\":5}}");
      Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(actual.getField("d")));
    }
  }

  @Test
  public void testF8IndependentEnvelopesShareOnlyTheBody() {
    // a is ["null", Shared] with default null (an envelope null supplies no member value); b's {"x":1} decides x.
    Assert.assertEquals(placement("v_f8env"), counts(0, 2));
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = convert("v_f8env", strict);
      assertSharedOccurrences(actual, "f8env-fwd", "v_f8env", strict, sharedName("v_f8env", strict), 2);
      assertContract(actual, "f8env-fwd", "v_f8env", strict);
      Assert.assertEquals(AvroCompatibilityHelper.getDefaultValueAsJsonString(actual.getField("a")), "null");
      Assert.assertEquals(actual.getField("a").schema().getTypes().get(0).getType(), Schema.Type.NULL);
    }
  }

  @Test
  public void testF8ControlsThatAlreadyAgree() {
    // Several compatible non-null defaults ({"x":1} and {"x":2}) agree on int-first, each field keeping its own value.
    Assert.assertEquals(placement("v_f8m"), counts(0, 2));
    // No retained default at all: the established merge order (null-first) stays, with no fabricated defaults.
    Assert.assertEquals(placement("v_f8n"), counts(0, 2));
    for (boolean strict : new boolean[] { true, false }) {
      Schema multiple = convert("v_f8m", strict);
      assertSharedOccurrences(multiple, "f8m-fwd", "v_f8m", strict, sharedName("v_f8m", strict), 2);
      assertContract(multiple, "f8m-fwd", "v_f8m", strict);

      Schema none = convert("v_f8n", strict);
      assertSharedOccurrences(none, "f8n-fwd", "v_f8n", strict, sharedName("v_f8n", strict), 2);
      assertContract(none, "f8n-fwd", "v_f8n", strict);
      Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(none.getField("b")));
    }
  }

  @Test
  public void testF8ReverseOrderControls() {
    // The evolved branch is canonical: its projected null defaults keep x null-first at every use; extra is absent.
    String[][] cases = { { "v_f8_r", "f8-rev" }, { "v_f8c_r", "f8c-rev" }, { "v_f8env_r", "f8env-rev" } };
    for (String[] c : cases) {
      for (boolean strict : new boolean[] { true, false }) {
        Schema actual = convert(c[0], strict);
        int uses = "v_f8c_r".equals(c[0]) ? (strict ? 4 : 3) : 2;
        assertSharedOccurrences(actual, c[1], c[0], strict, sharedName(c[0], strict), uses);
        assertContract(actual, c[1], c[0], strict);
      }
    }
    Assert.assertEquals(placement("v_f8_r"), counts(2, 0));
  }

  @Test
  public void testF8DifferentFullNamesStaySeparate() {
    // com.linkedin.f8.one.Shared (no default) and com.linkedin.f8.two.Shared (default {"x":1}) share a short name only:
    // they stay separate definitions with their own option orders. Non-strict keeps the existing -0/-1 mapping.
    Assert.assertEquals(placement("v_f8ns"), counts(0, 2));
    for (boolean strict : new boolean[] { true, false }) {
      Schema actual = convert("v_f8ns", strict);
      String one = strict ? "com.linkedin.f8.one.Shared" : "fz.v_f8ns.v_f8ns-0.Shared";
      String two = strict ? "com.linkedin.f8.two.Shared" : "fz.v_f8ns.v_f8ns-1.Shared";
      assertSharedOccurrences(actual, "f8ns-fwd", "v_f8ns", strict, one, 1);
      assertSharedOccurrences(actual, "f8ns-fwd", "v_f8ns", strict, two, 1);
      assertContract(actual, "f8ns-fwd", "v_f8ns", strict);
    }
  }

  @Test
  public void testF8IncompatibleProjectedBodiesRejectExplicitly() {
    // Canonical left uses Shared{x,y} for leftUse and rightUse; the other branch types them as A{x} and B{y}, so the
    // common shapes are Shared{x} and Shared{y}: no single definition can carry both under one full name.
    Assert.assertEquals(placement("v_f8neg"), counts(2, 0));
    String literal = catalog.avroLiteral(DB, "f8neg_l");
    for (boolean strict : new boolean[] { true, false }) {
      Schema result;
      try {
        result = convert("v_f8neg", strict);
      } catch (SchemaParseException e) {
        throw new AssertionError("Expected an explicit converter rejection, not Avro's redefinition error", e);
      } catch (RuntimeException e) {
        String message = String.valueOf(e.getMessage());
        Assert.assertFalse(message.contains("Can't redefine"), message);
        for (String fragment : new String[] { "leftUse", "rightUse" }) {
          Assert.assertTrue(message.contains(fragment),
              "strict=" + strict + ": expected the rejection to mention '" + fragment + "' but was: " + message);
        }
        // The qualified output identity at the rejection boundary, under the existing mapping: the source name (at
        // projection), or in non-strict mode the pre-merge (F8Neg-nested) or final view name. A bare "Shared" is not
        // enough, since namespace-separated Shared definitions are distinct types.
        String[] qualified = strict ? new String[] { SHARED }
            : new String[] { SHARED, "com.linkedin.f8.F8Neg.Shared", "fz.v_f8neg.v_f8neg.Shared" };
        Assert.assertTrue(Arrays.stream(qualified).anyMatch(message::contains), "strict=" + strict
            + ": expected a qualified Shared name " + Arrays.toString(qualified) + " but was: " + message);
        continue;
      }
      Assert.fail("strict=" + strict + ": expected a rejection but produced " + result);
    }
    Assert.assertEquals(catalog.avroLiteral(DB, "f8neg_l"), literal, "source metadata must not be mutated");
  }

  @Test
  public void testF8ReverseIncompatibleSourceBodiesTakeCanonicalNames() {
    // Section 12a: with f8neg_r canonical, the output types are its A{x} and B{y}. The other branch's two differently
    // projected Shared occurrences are not a retained output identity, so the view succeeds in both modes, with no
    // Shared definition or reference left and nothing fabricated.
    Assert.assertEquals(placement("v_f8neg_r"), counts(0, 2));
    String left = catalog.avroLiteral(DB, "f8neg_l");
    String right = catalog.avroLiteral(DB, "f8neg_r");
    for (boolean strict : new boolean[] { true, false }) {
      String suffix = strict ? "-strict" : "";
      String ns = strict ? "com.linkedin.f8" : "fz.v_f8neg_r.v_f8neg_r";
      Schema actual = convert("v_f8neg_r", strict);

      // In memory, before anything is serialized: A and B as specified, no Shared anywhere, no defaults.
      Schema a = actual.getField("leftUse").schema();
      Schema b = actual.getField("rightUse").schema();
      Assert.assertEquals(a.getFullName(), ns + ".A");
      Assert.assertEquals(b.getFullName(), ns + ".B");
      Assert.assertEquals(a.toString(true),
          new Schema.Parser().parse(load("expected/f8neg-rev-A" + suffix + ".avsc")).toString(true));
      Assert.assertEquals(b.toString(true),
          new Schema.Parser().parse(load("expected/f8neg-rev-B" + suffix + ".avsc")).toString(true));
      for (String name : new String[] { SHARED, "com.linkedin.f8.F8Neg.Shared", ns + ".Shared" }) {
        List<Schema> shared = new ArrayList<>();
        collectRecords(actual, name, shared);
        Assert.assertTrue(shared.isEmpty(), "strict=" + strict + ": no " + name + " may survive");
      }
      for (Schema.Field field : actual.getFields()) {
        Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(field), field.name());
        for (Schema.Field member : field.schema().getFields()) {
          Assert.assertFalse(AvroCompatibilityHelper.fieldHasDefault(member), field.name() + "." + member.name());
        }
      }

      assertReparses(actual);
      Assert.assertFalse(actual.toString().contains("Shared"), actual.toString());
      Assert.assertEquals(actual.toString(true),
          new Schema.Parser().parse(load("expected/f8neg-rev" + suffix + ".avsc")).toString(true));
      Assert.assertEquals(convert("v_f8neg_r", strict).toString(true), actual.toString(true), "repeat conversion");
    }
    Assert.assertEquals(catalog.avroLiteral(DB, "f8neg_l"), left, "source metadata must not be mutated");
    Assert.assertEquals(catalog.avroLiteral(DB, "f8neg_r"), right, "source metadata must not be mutated");
  }

  // ---------------------------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------------------------

  private Schema convert(String view, boolean strict) {
    return converter.toAvroSchema(DB, view, strict, false);
  }

  private List<Integer> placement(String view) {
    return projectionCounts(new HiveToRelConverter(catalog).convertView(DB, view));
  }

  private static String sharedName(String view, boolean strict) {
    return strict ? SHARED : "fz." + view + "." + view + ".Shared";
  }

  private static Schema expected(String contract, String view, boolean strict) {
    String json = load("expected/" + contract + (strict ? "-strict" : "") + ".avsc").replace("@VIEW@", view);
    return new Schema.Parser().parse(json);
  }

  private static void assertContract(Schema actual, String contract, String view, boolean strict) {
    assertReparses(actual);
    Assert.assertEquals(actual.toString(true), expected(contract, view, strict).toString(true),
        view + " strict=" + strict);
  }

  /**
   * Every in-memory occurrence of {@code fullName} must be the one expected complete definition, compared standalone
   * before anything is serialized (Avro record equality ignores docs and aliases, and serialization would fail on, or
   * silently pick, the first of two conflicting definitions).
   */
  private static void assertSharedOccurrences(Schema actual, String contract, String view, boolean strict,
      String fullName, int expectedUses) {
    List<Schema> expectedDefs = new ArrayList<>();
    collectRecords(expected(contract, view, strict), fullName, expectedDefs);
    Assert.assertFalse(expectedDefs.isEmpty(), "contract defines " + fullName);
    String definition = expectedDefs.get(0).toString(true);

    List<Schema> uses = new ArrayList<>();
    collectRecords(actual, fullName, uses);
    Assert.assertEquals(uses.size(), expectedUses, view + " strict=" + strict + ": uses of " + fullName);
    for (int i = 0; i < uses.size(); i++) {
      Assert.assertEquals(uses.get(i).toString(true), definition,
          view + " strict=" + strict + ": occurrence " + i + " of " + fullName);
    }
  }

  private static void collectRecords(Schema schema, String fullName, List<Schema> found) {
    switch (schema.getType()) {
      case RECORD:
        if (schema.getFullName().equals(fullName)) {
          found.add(schema);
        }
        for (Schema.Field field : schema.getFields()) {
          collectRecords(field.schema(), fullName, found);
        }
        break;
      case ARRAY:
        collectRecords(schema.getElementType(), fullName, found);
        break;
      case MAP:
        collectRecords(schema.getValueType(), fullName, found);
        break;
      case UNION:
        for (Schema member : schema.getTypes()) {
          collectRecords(member, fullName, found);
        }
        break;
      default:
        break;
    }
  }
}
