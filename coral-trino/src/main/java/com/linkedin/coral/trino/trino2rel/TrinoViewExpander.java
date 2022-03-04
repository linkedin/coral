/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

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
 * Class that implements {@link RelOptTable.ViewExpander}
 * interface to support expansion of Trino Views to relational algebra.
 */
public class TrinoViewExpander implements RelOptTable.ViewExpander {

  private final TrinoToRelConverter trinoToRelConverter;
  /**
   * Instantiates a new Trino view expander.
   *
   * @param trinoToRelConverter Trino to Rel converter
   */
  public TrinoViewExpander(@Nonnull TrinoToRelConverter trinoToRelConverter) {
    this.trinoToRelConverter = trinoToRelConverter;
  }

  @Override
  public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath, List<String> viewPath) {
    Preconditions.checkNotNull(viewPath);
    Preconditions.checkState(!viewPath.isEmpty());

    String dbName = Util.last(schemaPath);
    String tableName = viewPath.get(0);

    SqlNode sqlNode = trinoToRelConverter.processView(dbName, tableName)
        .accept(new FuzzyUnionSqlRewriter(tableName, trinoToRelConverter));
    return trinoToRelConverter.getSqlToRelConverter().convertQuery(sqlNode, true, true);
  }
}
