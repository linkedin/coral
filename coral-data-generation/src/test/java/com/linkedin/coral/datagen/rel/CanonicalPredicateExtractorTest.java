/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.rel;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Targeted unit tests for {@link CanonicalPredicateExtractor}.
 *
 * Tests predicate extraction and field index remapping to a global coordinate space
 * using programmatically built Calcite RelNode trees.
 */
public class CanonicalPredicateExtractorTest {

  private RelBuilder builder;

  @BeforeClass
  public void setup() {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);

    // T1(a VARCHAR, b INT, c VARCHAR) — 3 columns
    rootSchema.add("T1", new RelTestTable("T1", new String[] { "a", "b", "c" },
        new SqlTypeName[] { SqlTypeName.VARCHAR, SqlTypeName.INTEGER, SqlTypeName.VARCHAR }));

    // T2(x INT, y VARCHAR) — 2 columns
    rootSchema.add("T2", new RelTestTable("T2", new String[] { "x", "y" },
        new SqlTypeName[] { SqlTypeName.INTEGER, SqlTypeName.VARCHAR }));

    // T3(p INT, q INT) — 2 columns
    rootSchema.add("T3", new RelTestTable("T3", new String[] { "p", "q" },
        new SqlTypeName[] { SqlTypeName.INTEGER, SqlTypeName.INTEGER }));

    FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(rootSchema).build();
    builder = RelBuilder.create(config);
  }

  // ==================== Single Table with Filter ====================

  @Test
  public void testSingleFilterExtraction() {
    // Filter(b > 5) -> Scan(T1)
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.sequentialScans.size(), 1, "Should have 1 scan");
    assertEquals(output.canonicalPredicates.size(), 1, "Should have 1 predicate");

    // The predicate should reference field $1 (b is the 2nd column) with base offset 0
    RexNode pred = output.canonicalPredicates.get(0);
    String predStr = pred.toString();
    assertTrue(predStr.contains("$1"), "Predicate should reference $1 (column b), got: " + predStr);
  }

  @Test
  public void testSingleFilterPredicateForm() {
    // Filter(a = 'abc') -> Scan(T1)
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("abc"))).build();

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.canonicalPredicates.size(), 1);

    // Verify the predicate is an EQUALS call with $0 on one side
    RexNode pred = output.canonicalPredicates.get(0);
    assertTrue(pred.toString().contains("$0"), "Should reference $0 (column a), got: " + pred);
  }

  // ==================== Join Condition Extraction ====================

  @Test
  public void testJoinConditionExtraction() {
    // Join(T1.b = T2.x) -> [Scan(T1), Scan(T2)]
    builder.scan("T1");
    builder.scan("T2");
    RelNode tree = builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x"))).build();

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.sequentialScans.size(), 2, "Should have 2 scans");
    assertEquals(output.canonicalPredicates.size(), 1, "Should have 1 predicate (the join condition)");

    // T1 has fields [a=$0, b=$1, c=$2], T2 has fields [x=$3, y=$4]
    // Join condition T1.b = T2.x should be remapped to $1 = $3
    RexNode pred = output.canonicalPredicates.get(0);
    String predStr = pred.toString();
    assertTrue(predStr.contains("$1"), "Should reference $1 (T1.b), got: " + predStr);
    assertTrue(predStr.contains("$3"), "Should reference $3 (T2.x), got: " + predStr);
  }

  @Test
  public void testFilterOnJoinExtraction() {
    // Filter(T1.a = 'x') -> Join(T1.b = T2.x) -> [Scan(T1), Scan(T2)]
    builder.scan("T1");
    builder.scan("T2");
    builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x")));
    RelNode tree =
        builder.filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("x"))).build();

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.sequentialScans.size(), 2, "Should have 2 scans");
    assertEquals(output.canonicalPredicates.size(), 2, "Should have 2 predicates (filter + join)");
  }

  // ==================== Global Offset Remapping ====================

  @Test
  public void testThreeWayJoinOffsets() {
    // Join( Join(T1, T2), T3 )
    // T1: [a=$0, b=$1, c=$2] — 3 fields, offset 0
    // T2: [x=$3, y=$4] — 2 fields, offset 3
    // T3: [p=$5, q=$6] — 2 fields, offset 5
    builder.scan("T1");
    builder.scan("T2");
    builder.join(JoinRelType.INNER,
        builder.call(SqlStdOperatorTable.EQUALS, builder.field(2, 0, "b"), builder.field(2, 1, "x")));
    builder.scan("T3");
    RelNode tree = builder.join(JoinRelType.INNER, builder.call(SqlStdOperatorTable.EQUALS,
        // Left side (T1+T2 join) field $1 = T1.b, right side T3 field $0 = T3.p
        builder.field(2, 0, "b"), builder.field(2, 1, "p"))).build();

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.sequentialScans.size(), 3, "Should have 3 scans");
    assertEquals(output.canonicalPredicates.size(), 2, "Should have 2 join conditions");

    // Verify that T3's fields are remapped with the correct offset
    // Inner join: T1.b = T2.x should be remapped to $1 = $3
    // Outer join: T1.b = T3.p should be remapped to $1 = $5
    boolean foundT3Reference = false;
    for (RexNode pred : output.canonicalPredicates) {
      String predStr = pred.toString();
      if (predStr.contains("$5")) {
        foundT3Reference = true;
      }
    }
    assertTrue(foundT3Reference, "Should have a predicate referencing T3.p at global offset $5");
  }

  // ==================== Project Transparency ====================

  @Test
  public void testExtractorTraversesProject() {
    // After pull-up, tree looks like: Project -> Filter -> Scan
    // Extractor should look through the Project to find the Filter
    RelNode pullUpInput = builder.scan("T1").project(builder.field("a"), builder.field("b"))
        .filter(builder.call(SqlStdOperatorTable.GREATER_THAN, builder.field("b"), builder.literal(5))).build();

    // Apply pull-up first
    RelNode tree = ProjectPullUpController.applyUntilFixedPoint(pullUpInput, 10);

    CanonicalPredicateExtractor.Output output = CanonicalPredicateExtractor.extract(tree);

    assertEquals(output.sequentialScans.size(), 1, "Should have 1 scan");
    assertEquals(output.canonicalPredicates.size(), 1, "Should have 1 predicate");
  }

  // ==================== DnfRewriter Integration ====================

  @Test
  public void testDnfRewriterWithExtractedPredicates() {
    // Filter(a = 'x') -> Scan(T1)
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("x"))).build();

    CanonicalPredicateExtractor.Output extracted = CanonicalPredicateExtractor.extract(tree);
    DnfRewriter.Output dnf = DnfRewriter.convert(extracted, tree.getCluster().getRexBuilder());

    assertEquals(dnf.sequentialScans.size(), 1);
    assertFalse(dnf.disjuncts.isEmpty(), "Should have at least one disjunct");
  }

  @Test
  public void testDnfRewriterWithDisjunction() {
    // Filter(a = 'x' OR b = 5) -> Scan(T1)
    RelNode tree = builder.scan("T1")
        .filter(builder.call(SqlStdOperatorTable.OR,
            builder.call(SqlStdOperatorTable.EQUALS, builder.field("a"), builder.literal("x")),
            builder.call(SqlStdOperatorTable.EQUALS, builder.field("b"), builder.literal(5))))
        .build();

    CanonicalPredicateExtractor.Output extracted = CanonicalPredicateExtractor.extract(tree);
    DnfRewriter.Output dnf = DnfRewriter.convert(extracted, tree.getCluster().getRexBuilder());

    assertEquals(dnf.sequentialScans.size(), 1);
    assertEquals(dnf.disjuncts.size(), 2, "OR condition should produce 2 disjuncts");
  }
}
