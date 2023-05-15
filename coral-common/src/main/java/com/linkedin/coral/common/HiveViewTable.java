/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
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

import static org.apache.calcite.sql.type.SqlTypeName.*;


/**
 * A TranslatableTable (ViewTable) version of HiveTable that supports
 * recursive expansion of view definitions
 */
public class HiveViewTable extends HiveTable implements TranslatableTable {
  private final List<String> schemaPath;

  /**
   * Constructor to create bridge from hive table to calcite table
   *
   * @param hiveTable Hive table
   * @param schemaPath Calcite schema path
   */
  public HiveViewTable(Table hiveTable, List<String> schemaPath) {
    super(hiveTable);
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
