/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType;
import org.apache.calcite.rel.RelNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.schema.avro.FuzzyUnionFixtures.*;
import static org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
import static org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;


/**
 * T25 (D6, user-directed): non-strict stored-view namespace reconstruction keeps the legacy omission of RECORD and ENUM
 * aliases, for namespace-free and qualified aliases alike, at the top level and inside records, arrays, maps and
 * unions. FIXED aliases and field aliases are not rebuilt and stay intact. Strict conversion and inference without the
 * stored-view normalization keep every declared alias.
 *
 * <p>Alias oracles use resolved {@code getAliases()} full names before and after a fresh-parser reparse, because a
 * namespace-free alias can serialize to identical JSON yet resolve to a different full name. Reader/writer
 * compatibility against a minimal writer named by a former alias shows the observable consequence.
 */
public class FuzzyUnionAliasTests {
  private static final String NONE = "";

  private FuzzyUnionTestCatalog catalog;
  private ViewToAvroSchemaConverter converter;

  @BeforeClass
  public void beforeClass() {
    registerUdfs();
    catalog = buildCatalog();
    converter = ViewToAvroSchemaConverter.create(catalog);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Non-strict stored views: RECORD/ENUM aliases are omitted; FIXED and field aliases remain
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT25PlainNonStrictOmitsNamespaceFreeRecordAndEnumAliases() {
    String literal = catalog.avroLiteral(DB, "alias_src");
    Schema actual = converter.toAvroSchema(DB, "alias_view", false, false);

    assertContract(actual, "alias-nonstrict.avsc", "alias_view");
    Assert.assertEquals(actual.getFullName(), "fz.alias_view.alias_view");
    assertAliasesBeforeAndAfterReparse(actual, nonStrictNamedAliases("OldHash", "OldAHash"), FIELD_ALIASES);

    // The documented limitation: a writer named by a former alias is not readable through the rebuilt schema.
    assertCompatibilityBeforeAndAfterReparse(actual, "OldSource", INCOMPATIBLE);
    assertCompatibilityBeforeAndAfterReparse(actual.getField("nested").schema(), "OldNested", INCOMPATIBLE);
    assertCompatibilityBeforeAndAfterReparse(actual.getField("kind").schema(), "OldKind", INCOMPATIBLE);
    // The unchanged fixed keeps its alias identity.
    assertCompatibilityBeforeAndAfterReparse(actual.getField("hash").schema(), "OldHash", COMPATIBLE);

    Assert.assertEquals(catalog.avroLiteral(DB, "alias_src"), literal, "source metadata must not be mutated");
    Assert.assertEquals(converter.toAvroSchema(DB, "alias_src", true, false).toString(true),
        new Schema.Parser().parse(load("alias_src.avsc")).toString(true));
  }

  @Test
  public void testT25PlainNonStrictOmitsQualifiedRecordAndEnumAliases() {
    Schema actual = converter.toAvroSchema(DB, "alias_qview", false, false);

    assertContract(actual, "alias-q-nonstrict.avsc", "alias_qview");
    assertAliasesBeforeAndAfterReparse(actual,
        nonStrictNamedAliases("com.linkedin.legacy.OldQHash", "com.linkedin.alias.OldQAHash"), FIELD_ALIASES);

    assertCompatibilityBeforeAndAfterReparse(actual, "com.linkedin.legacy.OldQSource", INCOMPATIBLE);
    assertCompatibilityBeforeAndAfterReparse(actual.getField("kind").schema(), "com.linkedin.legacy.OldQKind",
        INCOMPATIBLE);
    assertCompatibilityBeforeAndAfterReparse(actual.getField("hash").schema(), "com.linkedin.legacy.OldQHash",
        COMPATIBLE);
  }

  @Test
  public void testT25FuzzyNonStrictOmitsRecordAndEnumAliasesBothOrders() {
    // One generated projection trims nested.extra; final records/enums still omit aliases, everything else is exact.
    for (String view : new String[] { "v_alias_fz", "v_alias_fz_r" }) {
      Schema actual = converter.toAvroSchema(DB, view, false, false);
      assertContract(actual, "alias-nonstrict.avsc", view);
      assertAliasesBeforeAndAfterReparse(actual, nonStrictNamedAliases("OldHash", "OldAHash"), FIELD_ALIASES);
      assertCompatibilityBeforeAndAfterReparse(actual.getField("levels").schema().getValueType(), "OldLevel",
          INCOMPATIBLE);
    }
    for (String view : new String[] { "v_alias_qfz", "v_alias_qfz_r" }) {
      Schema actual = converter.toAvroSchema(DB, view, false, false);
      assertContract(actual, "alias-q-nonstrict.avsc", view);
      assertAliasesBeforeAndAfterReparse(actual,
          nonStrictNamedAliases("com.linkedin.legacy.OldQHash", "com.linkedin.alias.OldQAHash"), FIELD_ALIASES);
    }
    Assert.assertEquals(projectionCounts(rel("v_alias_fz")), counts(0, 1));
    Assert.assertEquals(projectionCounts(rel("v_alias_fz_r")), counts(1, 0));
    Assert.assertEquals(projectionCounts(rel("v_alias_qfz")), counts(0, 1));
    Assert.assertEquals(projectionCounts(rel("v_alias_qfz_r")), counts(1, 0));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // Strict and inference-only controls keep every declared alias (forceLowercase=false; see D6-N1)
  // ---------------------------------------------------------------------------------------------------------------

  @Test
  public void testT25StrictPlainAndFuzzyViewsKeepAllAliases() {
    for (String view : new String[] { "alias_view", "v_alias_fz", "v_alias_fz_r" }) {
      Schema actual = converter.toAvroSchema(DB, view, true, false);
      FuzzyUnionFixtures.assertSchema(actual, "alias_src.avsc");
      assertAliasesBeforeAndAfterReparse(actual, STRICT_ALIASES, FIELD_ALIASES);
      assertCompatibilityBeforeAndAfterReparse(actual, "OldSource", COMPATIBLE);
      assertCompatibilityBeforeAndAfterReparse(actual.getField("nested").schema(), "OldNested", COMPATIBLE);
      assertCompatibilityBeforeAndAfterReparse(actual.getField("kind").schema(), "OldKind", COMPATIBLE);
    }
    for (String view : new String[] { "alias_qview", "v_alias_qfz", "v_alias_qfz_r" }) {
      Schema actual = converter.toAvroSchema(DB, view, true, false);
      FuzzyUnionFixtures.assertSchema(actual, "alias_qsrc.avsc");
      assertAliasesBeforeAndAfterReparse(actual, STRICT_Q_ALIASES, FIELD_ALIASES);
      assertCompatibilityBeforeAndAfterReparse(actual, "com.linkedin.legacy.OldQSource", COMPATIBLE);
      assertCompatibilityBeforeAndAfterReparse(actual, "com.linkedin.alias.OldQ2", COMPATIBLE);
      assertCompatibilityBeforeAndAfterReparse(actual.getField("kind").schema(), "com.linkedin.legacy.OldQKind",
          COMPATIBLE);
    }
  }

  @Test
  public void testT25InferenceWithoutViewNormalizationKeepsAllAliases() {
    // Converting the RelNode directly performs no stored-view namespace reconstruction, so a false strictMode alone
    // must not strip aliases.
    Schema actual = new RelToAvroSchemaConverter(catalog)
        .convert(new HiveToRelConverter(catalog).convertView(DB, "alias_view"), false, false);
    FuzzyUnionFixtures.assertSchema(actual, "alias_src.avsc");
    assertAliasesBeforeAndAfterReparse(actual, STRICT_ALIASES, FIELD_ALIASES);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // oracles
  // ---------------------------------------------------------------------------------------------------------------

  private static final Map<String, List<String>> FIELD_ALIASES =
      map("fieldA", list("oldFieldA"), "nested.n", list("oldN"));

  private static final Map<String, List<String>> STRICT_ALIASES =
      map("RECORD <root>", list("OldSource"), "FIXED hash", list("OldHash"), "ENUM kind", list("OldKind"),
          "RECORD nested", list("OldNested"), "RECORD items[]", list("OldItem"), "ENUM levels{}", list("OldLevel"),
          "FIXED hashes[]", list("OldAHash"), "RECORD err|", list("OldErr"));

  private static final Map<String, List<String>> STRICT_Q_ALIASES =
      map("RECORD <root>", list("com.linkedin.alias.OldQ2", "com.linkedin.legacy.OldQSource"), "FIXED hash",
          list("com.linkedin.legacy.OldQHash"), "ENUM kind", list("com.linkedin.legacy.OldQKind"), "RECORD nested",
          list("com.linkedin.legacy.OldQNested"), "RECORD items[]", list("com.linkedin.alias.OldQItem"),
          "ENUM levels{}", list("com.linkedin.legacy.OldQLevel"), "FIXED hashes[]",
          list("com.linkedin.alias.OldQAHash"), "RECORD err|", list("com.linkedin.legacy.OldQErr"));

  /** Non-strict: every rebuilt RECORD/ENUM has no alias; the two FIXED types keep theirs. */
  private static Map<String, List<String>> nonStrictNamedAliases(String hashAlias, String hashesAlias) {
    return map("RECORD <root>", list(), "FIXED hash", list(hashAlias), "ENUM kind", list(), "RECORD nested", list(),
        "RECORD items[]", list(), "ENUM levels{}", list(), "FIXED hashes[]", list(hashesAlias), "RECORD err|", list());
  }

  private RelNode rel(String view) {
    return new HiveToRelConverter(catalog).convertView(DB, view);
  }

  private static void assertContract(Schema actual, String resource, String view) {
    String json = load("expected/" + resource).replace("@VIEW@", view);
    assertReparses(actual);
    Assert.assertEquals(actual.toString(true), new Schema.Parser().parse(json).toString(true));
  }

  /** Resolved named-type and field alias sets, both in memory and after a fresh-parser reparse. */
  private static void assertAliasesBeforeAndAfterReparse(Schema actual, Map<String, List<String>> named,
      Map<String, List<String>> fields) {
    Schema reparsed = new Schema.Parser().parse(actual.toString());
    for (Schema schema : new Schema[] { actual, reparsed }) {
      String which = schema == actual ? "in memory" : "after reparse";
      Map<String, List<String>> actualNamed = new LinkedHashMap<>();
      Map<String, List<String>> actualFields = new LinkedHashMap<>();
      collectAliases(schema, "<root>", "", actualNamed, actualFields);
      Assert.assertEquals(actualNamed, named, "named-type aliases " + which);
      Assert.assertEquals(actualFields, fields, "field aliases " + which);
    }
  }

  private static void collectAliases(Schema schema, String path, String fieldPrefix, Map<String, List<String>> named,
      Map<String, List<String>> fields) {
    switch (schema.getType()) {
      case RECORD:
        named.put("RECORD " + path, new ArrayList<>(new TreeSet<>(schema.getAliases())));
        for (Schema.Field field : schema.getFields()) {
          if (!field.aliases().isEmpty()) {
            fields.put(fieldPrefix + field.name(), new ArrayList<>(new TreeSet<>(field.aliases())));
          }
          collectAliases(field.schema(), field.name(), fieldPrefix + field.name() + ".", named, fields);
        }
        break;
      case ENUM:
      case FIXED:
        named.put(schema.getType() + " " + path, new ArrayList<>(new TreeSet<>(schema.getAliases())));
        break;
      case ARRAY:
        collectAliases(schema.getElementType(), path + "[]", fieldPrefix, named, fields);
        break;
      case MAP:
        collectAliases(schema.getValueType(), path + "{}", fieldPrefix, named, fields);
        break;
      case UNION:
        for (Schema member : schema.getTypes()) {
          collectAliases(member, path + "|", fieldPrefix, named, fields);
        }
        break;
      default:
        break;
    }
  }

  /**
   * Reads a minimal writer that is {@code reader} renamed to {@code writerFullName} (same fields, symbols or size), so
   * the result depends only on the reader's name and resolved aliases.
   */
  private static void assertCompatibilityBeforeAndAfterReparse(Schema reader, String writerFullName,
      SchemaCompatibilityType expected) {
    Schema writer = renamed(reader, writerFullName);
    Schema reparsed = new Schema.Parser().parse(reader.toString());
    Assert.assertEquals(SchemaCompatibility.checkReaderWriterCompatibility(reader, writer).getType(), expected,
        reader.getFullName() + " reading " + writerFullName + " in memory");
    Assert.assertEquals(SchemaCompatibility.checkReaderWriterCompatibility(reparsed, writer).getType(), expected,
        reader.getFullName() + " reading " + writerFullName + " after reparse");
  }

  private static Schema renamed(Schema schema, String fullName) {
    int dot = fullName.lastIndexOf('.');
    String name = fullName.substring(dot + 1);
    String namespace = dot < 0 ? NONE : fullName.substring(0, dot);
    switch (schema.getType()) {
      case RECORD:
        List<Schema.Field> fields = new ArrayList<>();
        for (Schema.Field field : schema.getFields()) {
          fields.add(new Schema.Field(field, field.schema()));
        }
        return Schema.createRecord(name, null, namespace, schema.isError(), fields);
      case ENUM:
        return Schema.createEnum(name, null, namespace, schema.getEnumSymbols());
      case FIXED:
        return Schema.createFixed(name, null, namespace, schema.getFixedSize());
      default:
        throw new IllegalArgumentException("Not a named schema: " + schema);
    }
  }

  private static List<String> list(String... values) {
    List<String> result = new ArrayList<>();
    for (String value : values) {
      result.add(value);
    }
    result.sort(null);
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, List<String>> map(Object... entries) {
    Map<String, List<String>> result = new LinkedHashMap<>();
    for (int i = 0; i < entries.length; i += 2) {
      result.put((String) entries[i], (List<String>) entries[i + 1]);
    }
    return result;
  }
}
