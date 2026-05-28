/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.lang.reflect.Field;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertSame;


/**
 * Unit tests for {@link HiveViewExpander#warnIfRowTypeMisaligned}. The method
 * is the diagnostic that surfaces a mismatch between the caller-provided row
 * type and the expanded view body's row type -- a state that previously caused
 * silent positional column swaps in downstream view-on-view consumers. Behavior
 * is "log a warning and leave the {@code RelRoot} untouched"; tests pin both
 * the warn emission and the no-rewrite contract.
 */
public class HiveViewExpanderTest {

  private RelDataTypeFactory typeFactory;
  private RelOptCluster cluster;
  private Logger originalLogger;
  private Logger mockLogger;

  @BeforeClass
  public void setUp() {
    typeFactory = new JavaTypeFactoryImpl();
    cluster = RelOptCluster.create(new VolcanoPlanner(), new RexBuilder(typeFactory));
  }

  @BeforeMethod
  public void installMockLogger() throws Exception {
    mockLogger = mock(Logger.class);
    Field logField = HiveViewExpander.class.getDeclaredField("LOG");
    logField.setAccessible(true);
    originalLogger = (Logger) logField.get(null);
    logField.set(null, mockLogger);
  }

  @AfterMethod
  public void restoreLogger() throws Exception {
    Field logField = HiveViewExpander.class.getDeclaredField("LOG");
    logField.setAccessible(true);
    logField.set(null, originalLogger);
  }

  private RelDataType rowType(Object... nameTypePairs) {
    if (nameTypePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Expected (name, type) pairs");
    }
    RelDataTypeFactory.Builder builder = typeFactory.builder();
    for (int i = 0; i < nameTypePairs.length; i += 2) {
      String name = (String) nameTypePairs[i];
      Object type = nameTypePairs[i + 1];
      if (type instanceof SqlTypeName) {
        builder.add(name, (SqlTypeName) type);
      } else if (type instanceof RelDataType) {
        builder.add(name, (RelDataType) type);
      } else {
        throw new IllegalArgumentException("Expected SqlTypeName or RelDataType for field " + name);
      }
    }
    return builder.build();
  }

  private RelDataType arrayOf(RelDataType element) {
    return typeFactory.createArrayType(element, -1);
  }

  private RelRoot rootWithRowType(RelDataType type) {
    RelNode rel = LogicalValues.createEmpty(cluster, type);
    return RelRoot.of(rel, SqlKind.SELECT);
  }

  @Test
  public void testAlignedByOrderEmitsNoWarning() {
    RelDataType type = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);
    RelRoot root = rootWithRowType(type);

    HiveViewExpander.warnIfRowTypeMisaligned(root, type);

    verify(mockLogger, never()).warn(anyString(), any(), any());
  }

  @Test
  public void testCaseDifferencesOnlyEmitNoWarning() {
    RelRoot root = rootWithRowType(rowType("colA", SqlTypeName.INTEGER, "colB", SqlTypeName.VARCHAR));
    RelDataType expected = rowType("cola", SqlTypeName.INTEGER, "colb", SqlTypeName.VARCHAR);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, never()).warn(anyString(), any(), any());
  }

  /**
   * Models the production failure mode this method exists to flag: an upstream
   * view body that lists {@code idHash} (the longer name) ahead of its sibling
   * {@code id} (the shorter name, a prefix of the other) introduces column
   * aliases whose ordering disagrees with the downstream consumer's expected
   * ordering. We no longer auto-realign -- Kyoto (kyoto_table_management#305)
   * fixed the upstream ordering -- but we still want to know if a view trips it.
   */
  @Test
  public void testReorderedFieldsEmitWarning() {
    RelRoot root = rootWithRowType(rowType("idHash", SqlTypeName.VARCHAR, "id", SqlTypeName.INTEGER));
    RelDataType expected = rowType("id", SqlTypeName.INTEGER, "idHash", SqlTypeName.VARCHAR);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, times(1)).warn(anyString(), any(), any());
  }

  @Test
  public void testDifferentArityEmitsWarning() {
    RelRoot root =
        rootWithRowType(rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR, "c", SqlTypeName.DOUBLE));
    RelDataType expected = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, times(1)).warn(anyString(), any(), any());
  }

  @Test
  public void testNestedStructAlignedEmitsNoWarning() {
    RelDataType address = rowType("street", SqlTypeName.VARCHAR, "city", SqlTypeName.VARCHAR);
    RelDataType row = rowType("id", SqlTypeName.INTEGER, "address", address);
    RelRoot root = rootWithRowType(row);

    HiveViewExpander.warnIfRowTypeMisaligned(root, row);

    verify(mockLogger, never()).warn(anyString(), any(), any());
  }

  @Test
  public void testNestedStructFieldsReorderedEmitWarning() {
    // Top-level field names agree ("address"), but the inner struct fields
    // are in opposite order between caller and expanded body. The previous
    // top-level-only check would miss this; the recursive check catches it.
    RelDataType expectedAddress = rowType("street", SqlTypeName.VARCHAR, "city", SqlTypeName.VARCHAR);
    RelDataType actualAddress = rowType("city", SqlTypeName.VARCHAR, "street", SqlTypeName.VARCHAR);
    RelDataType expected = rowType("id", SqlTypeName.INTEGER, "address", expectedAddress);
    RelDataType actual = rowType("id", SqlTypeName.INTEGER, "address", actualAddress);
    RelRoot root = rootWithRowType(actual);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, times(1)).warn(anyString(), any(), any());
  }

  @Test
  public void testArrayOfStructFieldsReorderedEmitWarning() {
    // ARRAY<STRUCT<street, city>> vs ARRAY<STRUCT<city, street>> -- nested
    // reorder is one level past the array. Same silent-swap risk; should warn.
    RelDataType expectedElem = rowType("street", SqlTypeName.VARCHAR, "city", SqlTypeName.VARCHAR);
    RelDataType actualElem = rowType("city", SqlTypeName.VARCHAR, "street", SqlTypeName.VARCHAR);
    RelDataType expected = rowType("id", SqlTypeName.INTEGER, "addresses", arrayOf(expectedElem));
    RelDataType actual = rowType("id", SqlTypeName.INTEGER, "addresses", arrayOf(actualElem));
    RelRoot root = rootWithRowType(actual);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, times(1)).warn(anyString(), any(), any());
  }

  @Test
  public void testDeeplyNestedStructReorderedEmitWarning() {
    // STRUCT<addr STRUCT<geo STRUCT<lat, lon>>> vs same outer shape but the
    // innermost struct fields are in opposite order. Pins the recursion.
    RelDataType expectedGeo = rowType("lat", SqlTypeName.DOUBLE, "lon", SqlTypeName.DOUBLE);
    RelDataType actualGeo = rowType("lon", SqlTypeName.DOUBLE, "lat", SqlTypeName.DOUBLE);
    RelDataType expectedAddr = rowType("street", SqlTypeName.VARCHAR, "geo", expectedGeo);
    RelDataType actualAddr = rowType("street", SqlTypeName.VARCHAR, "geo", actualGeo);
    RelDataType expected = rowType("addr", expectedAddr);
    RelDataType actual = rowType("addr", actualAddr);
    RelRoot root = rootWithRowType(actual);

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    verify(mockLogger, times(1)).warn(anyString(), any(), any());
  }

  @Test
  public void testWarningPathLeavesRootUnchanged() {
    RelRoot root = rootWithRowType(rowType("b", SqlTypeName.VARCHAR, "a", SqlTypeName.INTEGER));
    RelDataType expected = rowType("a", SqlTypeName.INTEGER, "b", SqlTypeName.VARCHAR);
    RelNode originalRel = root.rel;

    HiveViewExpander.warnIfRowTypeMisaligned(root, expected);

    // The helper returns void; we're pinning the contract that it does not
    // mutate the RelRoot in place either (no LogicalProject wrap from earlier
    // versions of this code).
    assertSame(root.rel, originalRel);
  }
}
