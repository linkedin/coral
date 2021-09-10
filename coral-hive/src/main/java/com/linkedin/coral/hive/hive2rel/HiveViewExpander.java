/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.util.Util;


/**
 * Class that implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}
 * interface to support expansion of Hive Views to relational algebra.
 */
public class HiveViewExpander implements RelOptTable.ViewExpander {

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

    return RelRoot.of(hiveToRelConverter.convertView(dbName, tableName), SqlKind.SELECT);
  }
}
