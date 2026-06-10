/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
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
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.util.Util;

import com.linkedin.coral.common.FuzzyUnionSqlRewriter;


/**
 * Class that implements {@link org.apache.calcite.plan.RelOptTable.ViewExpander}
 * interface to support expansion of views to relational algebra.
 */
public class CoralViewExpander implements RelOptTable.ViewExpander {

  private final HiveToRelConverter hiveToRelConverter;
  /**
   * Instantiates a new view expander.
   *
   * @param hiveToRelConverter Hive to Rel converter
   */
  public CoralViewExpander(@Nonnull HiveToRelConverter hiveToRelConverter) {
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
    return hiveToRelConverter.getSqlToRelConverter().convertQuery(sqlNode, true, true);
  }
}
