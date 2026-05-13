/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.core.Values;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;


/**
 * Unit tests for {@link HiveViewExpander#alignToRowType}. These exercise the
 * alignment logic in isolation -- no Hive metastore or end-to-end view expansion
 * required.
 */
public class HiveViewExpanderTest {

  private RelDataTypeFactory typeFactory;
  private RelOptCluster cluster;

  @BeforeClass
  public void setUp() {
    typeFactory = new JavaTypeFactoryImpl();
    cluster = RelOptCluster.create(new VolcanoPlanner(), new RexBuilder(typeFactory));
  }

  private RelDataType rowType(Object... nameTypePairs) {
    if (nameTypePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Expected (name, type) pairs");
    }
    RelDataTypeFactory.Builder builder = typeFactory.builder();
    for (int i = 0; i < nameTypePairs.length; i += 2) {
      builder.add((String) nameTypePairs[i], (SqlTypeName) nameTypePairs[i + 1]);
    }
    return builder.build();
  }

  private RelRoot rootWithRowType(RelDataType type) {
    RelNode rel = LogicalValues.createEmpty(cluster, type);
    return RelRoot.of(rel, SqlKind.SELECT);
  }

  @Test
  public void testAlignedByOrderReturnsSameRoot() {
    RelDataType type = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);
    RelRoot root = rootWithRowType(type);

    RelRoot result = HiveViewExpander.alignToRowType(root, type);

    assertSame(result, root, "Already-aligned root should be returned unchanged");
  }

  @Test
  public void testCaseDifferencesOnlyReturnsSameRoot() {
    RelRoot root = rootWithRowType(rowType("colA", SqlTypeName.INTEGER, "colB", SqlTypeName.VARCHAR));
    RelDataType expected = rowType("cola", SqlTypeName.INTEGER, "colb", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertSame(result, root, "Names differing only in case should be treated as aligned (case-insensitive)");
  }

  @Test
  public void testReorderedFieldsWrapInProject() {
    RelRoot root = rootWithRowType(rowType("b", SqlTypeName.VARCHAR, "a", SqlTypeName.INTEGER));
    RelDataType expected = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertTrue(result.rel instanceof LogicalProject,
        "Reordered fields should result in a LogicalProject wrapper, got " + result.rel.getClass());
    LogicalProject project = (LogicalProject) result.rel;
    assertTrue(project.getInput() instanceof Values, "Wrapped Project should sit directly on the input");

    List<RelDataTypeField> outFields = result.rel.getRowType().getFieldList();
    assertEquals(outFields.size(), 2);
    assertEquals(outFields.get(0).getName(), "a");
    assertEquals(outFields.get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    assertEquals(outFields.get(1).getName(), "b");
    assertEquals(outFields.get(1).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
  }

  /**
   * Models the production failure mode this method exists to prevent: an
   * upstream view body that lists {@code idHash} (the longer name) ahead of
   * its sibling {@code id} (the shorter name, a prefix of the other)
   * introduces column aliases whose ordering disagrees with the downstream
   * consumer's expected ordering. Without realignment, the downstream's
   * positional access lands {@code id} on the {@code idHash} position and
   * vice versa, silently swapping every value read from those columns.
   */
  @Test
  public void testPrefixSiblingsReorderedCorrectly() {
    RelRoot root = rootWithRowType(rowType("idHash", SqlTypeName.VARCHAR, "id", SqlTypeName.INTEGER));
    RelDataType expected = rowType("id", SqlTypeName.INTEGER, "idHash", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertTrue(result.rel instanceof LogicalProject);
    LogicalProject project = (LogicalProject) result.rel;

    // Output field order matches expected: id (INTEGER) first, idHash (VARCHAR) second.
    List<RelDataTypeField> outFields = result.rel.getRowType().getFieldList();
    assertEquals(outFields.get(0).getName(), "id");
    assertEquals(outFields.get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    assertEquals(outFields.get(1).getName(), "idHash");
    assertEquals(outFields.get(1).getType().getSqlTypeName(), SqlTypeName.VARCHAR);

    // And the underlying input refs cross-link to the correct source positions:
    // expected[0]=id -> input position 1; expected[1]=idHash -> input position 0.
    assertTrue(project.getProjects().get(0) instanceof RexInputRef);
    assertTrue(project.getProjects().get(1) instanceof RexInputRef);
    assertEquals(((RexInputRef) project.getProjects().get(0)).getIndex(), 1);
    assertEquals(((RexInputRef) project.getProjects().get(1)).getIndex(), 0);
  }

  /**
   * Realignment over multiple prefix-name pairs at once. Each pair in the input
   * row type lists the longer name first ({@code idHash} before {@code id},
   * {@code emailVerified} before {@code email}, {@code countryCode} before
   * {@code country}); the caller's expected row type lists them the other way
   * around. Mirrors the realistic failure shape where an upstream view body's
   * case-sensitive alphabetical sort places longer prefix-extensions ahead of
   * their shorter siblings, and a downstream consumer expects the
   * lowercase-alphabetical (prefix-first) order. Each pair must be realigned
   * independently and correctly.
   */
  @Test
  public void testMultiplePrefixSiblingsAllReorderedCorrectly() {
    RelRoot root = rootWithRowType(
        rowType("idHash", SqlTypeName.VARCHAR, "id", SqlTypeName.INTEGER, "emailVerified", SqlTypeName.BOOLEAN, "email",
            SqlTypeName.VARCHAR, "countryCode", SqlTypeName.VARCHAR, "country", SqlTypeName.VARCHAR));
    RelDataType expected =
        rowType("id", SqlTypeName.INTEGER, "idHash", SqlTypeName.VARCHAR, "email", SqlTypeName.VARCHAR, "emailVerified",
            SqlTypeName.BOOLEAN, "country", SqlTypeName.VARCHAR, "countryCode", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertTrue(result.rel instanceof LogicalProject);
    LogicalProject project = (LogicalProject) result.rel;

    // Output names and types follow the expected (prefix-first) ordering.
    List<RelDataTypeField> outFields = result.rel.getRowType().getFieldList();
    assertEquals(outFields.size(), 6);
    assertEquals(outFields.get(0).getName(), "id");
    assertEquals(outFields.get(0).getType().getSqlTypeName(), SqlTypeName.INTEGER);
    assertEquals(outFields.get(1).getName(), "idHash");
    assertEquals(outFields.get(1).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(outFields.get(2).getName(), "email");
    assertEquals(outFields.get(2).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(outFields.get(3).getName(), "emailVerified");
    assertEquals(outFields.get(3).getType().getSqlTypeName(), SqlTypeName.BOOLEAN);
    assertEquals(outFields.get(4).getName(), "country");
    assertEquals(outFields.get(4).getType().getSqlTypeName(), SqlTypeName.VARCHAR);
    assertEquals(outFields.get(5).getName(), "countryCode");
    assertEquals(outFields.get(5).getType().getSqlTypeName(), SqlTypeName.VARCHAR);

    // Each expected position points at the correct source position in the input row type.
    // Input order was: [idHash=0, id=1, emailVerified=2, email=3, countryCode=4, country=5].
    int[] expectedSourceIndices = { 1, 0, 3, 2, 5, 4 };
    for (int i = 0; i < expectedSourceIndices.length; i++) {
      assertTrue(project.getProjects().get(i) instanceof RexInputRef, "Project " + i + " should be a RexInputRef");
      assertEquals(((RexInputRef) project.getProjects().get(i)).getIndex(), expectedSourceIndices[i],
          "Expected position " + i + " (" + outFields.get(i).getName() + ") should source from input position "
              + expectedSourceIndices[i]);
    }
  }

  @Test
  public void testMissingFieldReturnsOriginalRoot() {
    RelRoot root = rootWithRowType(rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR));
    // `c` is not in the input -- alignment should bail rather than fabricate.
    RelDataType expected = rowType("a", SqlTypeName.INTEGER, "c", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertSame(result, root, "Missing field should trigger safe fallback to the original root");
  }

  @Test
  public void testDifferentArityReturnsOriginalRoot() {
    RelRoot root =
        rootWithRowType(rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR, "c", SqlTypeName.DOUBLE));
    RelDataType expected = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);

    RelRoot result = HiveViewExpander.alignToRowType(root, expected);

    assertSame(result, root, "Different arity should trigger safe fallback rather than silent column drop");
    assertFalse(result.rel instanceof LogicalProject,
        "Different-arity case should not wrap in a Project (would be lossy)");
  }
}
