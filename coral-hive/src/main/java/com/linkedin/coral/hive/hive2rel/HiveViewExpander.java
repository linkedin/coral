/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.common.FuzzyUnionSqlRewriter;


/**
 * Class that implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}
 * interface to support expansion of Hive Views to relational algebra. Logs a
 * warning when the expanded body's row type disagrees with the caller-provided
 * {@link RelDataType} so silent positional column swaps surface in logs.
 */
public class HiveViewExpander implements RelOptTable.ViewExpander {

  // Non-final so unit tests can swap in a mock via reflection. See HiveViewExpanderTest.
  private static Logger LOG = LoggerFactory.getLogger(HiveViewExpander.class);

  private final HiveToRelConverter hiveToRelConverter;
  /**
   * Instantiates a new Hive view expander.
   *
   * @param hiveToRelConverter Hive to Rel converter
   */
  public HiveViewExpander(@Nonnull HiveToRelConverter hiveToRelConverter) {
    this.hiveToRelConverter = hiveToRelConverter;
  }

  @Override
  public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath, List<String> viewPath) {
    Preconditions.checkNotNull(viewPath);
    Preconditions.checkState(!viewPath.isEmpty());

    String dbName = Util.last(schemaPath);
    String tableName = viewPath.get(0);

    SqlNode sqlNode = hiveToRelConverter.processView(dbName, tableName)
        .accept(new FuzzyUnionSqlRewriter(tableName, hiveToRelConverter));
    RelRoot root = hiveToRelConverter.getSqlToRelConverter().convertQuery(sqlNode, true, true);
    warnIfRowTypeMisaligned(root, rowType);
    // TODO: tighten this from a warning to an IllegalStateException once we
    // are confident no live Hive view emits an expanded body whose row type
    // disagrees with the HMS-recorded row type. This is currently a
    // transitional safeguard while view producers migrate to projections
    // that do not trigger the case-sensitive resolver mismatch.
    return root;
  }

  /**
   * Logs a warning when {@code root}'s row type disagrees with {@code expected}
   * by field count or by case-insensitive field name order. No-op when they
   * already agree.
   */
  @VisibleForTesting
  static void warnIfRowTypeMisaligned(RelRoot root, RelDataType expected) {
    final RelDataType actual = root.rel.getRowType();
    if (fieldNamesAlignedByOrder(expected, actual)) {
      return;
    }
    LOG.warn("Expanded view row type does not match caller-provided row type. expected={}, actual={}", expected,
        actual);
  }

  private static boolean fieldNamesAlignedByOrder(RelDataType a, RelDataType b) {
    if (a.getFieldCount() != b.getFieldCount()) {
      return false;
    }
    final List<RelDataTypeField> af = a.getFieldList();
    final List<RelDataTypeField> bf = b.getFieldList();
    for (int i = 0; i < af.size(); i++) {
      if (!af.get(i).getName().equalsIgnoreCase(bf.get(i).getName())) {
        return false;
      }
    }
    return true;
  }
}
