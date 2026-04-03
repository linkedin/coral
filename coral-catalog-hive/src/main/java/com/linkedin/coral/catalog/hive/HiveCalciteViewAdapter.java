/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.hive;

import java.util.List;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.com.google.common.base.Throwables;
import com.linkedin.coral.com.google.common.collect.ImmutableList;


/**
 * Calcite adapter for Hive views, extending HiveCalciteTableAdapter with TranslatableTable support
 * for recursive expansion of view definitions.
 *
 * @see HiveCalciteTableAdapter
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class HiveCalciteViewAdapter extends HiveCalciteTableAdapter implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * Constructor to create bridge from hive table to calcite table.
   *
   * @param hiveTable Hive table
   * @param schemaPath Calcite schema path
   */
  public HiveCalciteViewAdapter(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
    this.schemaPath = schemaPath;
  }

  /**
   * Constructor accepting HiveTable for unified catalog integration.
   *
   * @param coralTable HiveTable from catalog
   * @param schemaPath Calcite schema path
   */
  public HiveCalciteViewAdapter(HiveTable coralTable, List<String> schemaPath) {
    super(coralTable);
    this.schemaPath = schemaPath;
  }

  @Override
  public RelNode toRel(RelOptTable.ToRelContext relContext, RelOptTable relOptTable) {
    try {
      RelRoot root = relContext.expandView(relOptTable.getRowType(), hiveTable.getViewExpandedText(), schemaPath,
          ImmutableList.of(hiveTable.getTableName()));
      return root.rel;
    } catch (Exception e) {
      Throwables.propagateIfInstanceOf(e, RuntimeException.class);
      throw new RuntimeException("Error while parsing view definition", e);
    }
  }
}
