/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.common.FuzzyUnionSqlRewriter;


/**
 * Class that implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}
 * interface to support expansion of Hive Views to relational algebra. The
 * returned {@link RelRoot} is realigned to the caller-provided
 * {@link RelDataType} by name (case-insensitive) to preserve the
 * {@link RelOptTable.ViewExpander} contract.
 */
public class HiveViewExpander implements RelOptTable.ViewExpander {

  private static final Logger LOG = LoggerFactory.getLogger(HiveViewExpander.class);

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
    return alignToRowType(root, rowType);
  }

  /**
   * Wrap {@code root} in a {@link LogicalProject} that re-orders its output
   * fields to match {@code expected} by name (case-insensitive). No-op when the
   * orderings already agree. If a name is missing from {@code root} or arities
   * differ, returns {@code root} unchanged and logs a warning.
   */
  @VisibleForTesting
  static RelRoot alignToRowType(RelRoot root, RelDataType expected) {
    final RelNode rel = root.rel;
    final RelDataType actual = rel.getRowType();
    if (expected.getFieldCount() != actual.getFieldCount()) {
      LOG.warn("Skipping row-type alignment: expected {} fields, expanded view produced {}. expected={}, actual={}",
          expected.getFieldCount(), actual.getFieldCount(), expected, actual);
      return root;
    }
    if (fieldNamesAlignedByOrder(expected, actual)) {
      return root;
    }
    final List<RexNode> projects = new ArrayList<>(expected.getFieldCount());
    final List<String> names = new ArrayList<>(expected.getFieldCount());
    final RexBuilder rexBuilder = rel.getCluster().getRexBuilder();
    for (RelDataTypeField expectedField : expected.getFieldList()) {
      // case-insensitive name lookup, no struct-field traversal
      final RelDataTypeField actualField = actual.getField(expectedField.getName(), false, false);
      if (actualField == null) {
        LOG.warn(
            "Skipping row-type alignment: expected field '{}' is absent from expanded view. expected={}, actual={}",
            expectedField.getName(), expected, actual);
        return root;
      }
      projects.add(rexBuilder.makeInputRef(actualField.getType(), actualField.getIndex()));
      names.add(expectedField.getName());
    }
    final RelNode aligned = LogicalProject.create(rel, projects, names);
    return RelRoot.of(aligned, root.kind);
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
