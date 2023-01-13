/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

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
 * interface to support expansion of Spark Views to relational algebra.
 */
public class SparkViewExpander implements RelOptTable.ViewExpander {

  private final SparkToRelConverter sparkToRelConverter;
  /**
   * Instantiates a new Spark view expander.
   *
   * @param sparkToRelConverter Spark to Rel converter
   */
  public SparkViewExpander(@Nonnull SparkToRelConverter sparkToRelConverter) {
    this.sparkToRelConverter = sparkToRelConverter;
  }

  @Override
  public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath, List<String> viewPath) {
    Preconditions.checkNotNull(viewPath);
    Preconditions.checkState(!viewPath.isEmpty());

    String dbName = Util.last(schemaPath);
    String tableName = viewPath.get(0);

    SqlNode sqlNode = sparkToRelConverter.processView(dbName, tableName)
        .accept(new FuzzyUnionSqlRewriter(tableName, sparkToRelConverter));
    return sparkToRelConverter.getSqlToRelConverter().convertQuery(sqlNode, true, true);
  }
}
