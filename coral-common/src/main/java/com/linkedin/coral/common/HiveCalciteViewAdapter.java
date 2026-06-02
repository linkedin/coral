/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.com.google.common.base.Throwables;
import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.catalog.HiveTable;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 *
 * <p>This shim intentionally extends {@link HiveCalciteTableAdapter} (rather than
 * {@code com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter}) so that the historical
 * {@code view instanceof com.linkedin.coral.common.HiveCalciteTableAdapter} relationship is
 * preserved for existing callers. The view-specific {@link #toRel} behavior is replicated here;
 * both this class and {@link HiveCalciteTableAdapter} are removed together at cleanup, leaving the
 * canonical {@code catalog.hive} hierarchy untouched.
 */
@Deprecated
public class HiveCalciteViewAdapter extends HiveCalciteTableAdapter implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter#HiveCalciteViewAdapter(Table, List)} instead.
   */
  @Deprecated
  public HiveCalciteViewAdapter(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
    this.schemaPath = schemaPath;
  }

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter#HiveCalciteViewAdapter(com.linkedin.coral.catalog.hive.HiveTable, List)} instead.
   */
  @Deprecated
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
