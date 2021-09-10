/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkned.coral.common;

import javax.annotation.Nonnull;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Public class to convert Hive SQL to Calcite relational algebra.
 * This class should serve as the main entry point for clients to convert
 * Hive queries.
 */
/*
 * We provide this class as a public interface by providing a thin wrapper
 * around HiveSqlToRelConverter. Directly using HiveSqlToRelConverter will
 * expose public methods from SqlToRelConverter. Use of SqlToRelConverter
 * is likely to change in the future if we want more control over the
 * conversion process. This class abstracts that out.
 */
public abstract class ToRelConverter {

  protected final RelContextProvider relContextProvider;

  protected ToRelConverter(RelContextProvider relContextProvider) {
    checkNotNull(relContextProvider);
    this.relContextProvider = relContextProvider;
  }

  // TODO change back to protected once tests move to the common package
  public abstract SqlNode toSqlNode(String sql);

  protected abstract SqlNode toSqlNode(String sql, org.apache.hadoop.hive.metastore.api.Table hiveView);

  /**
   * Converts input Hive SQL query to Calcite {@link RelNode}.
   *
   * This method resolves all the database, table and field names using the catalog
   * information provided by hive configuration during initialization. The input
   * sql parameter should not refer to dali functions since those can not be resolved.
   * The sql can, however, refer to dali views whose definitions include dali functions.
   *
   * @param sql Hive sql string to convert to Calcite RelNode
   * @return Calcite RelNode representation of input hive sql
   */
  public RelNode convertSql(String sql) {
    return toRel(toSqlNode(sql));
  }

  /**
   * Apply series of transforms to convert Hive relnode to
   * standardized intermediate representation. What is "standard"
   * is vague right now but we try to be closer to ANSI standard.
   * TODO: define standard intermediate representation
   * @param relNode calcite relnode representing hive query
   * @return standard representation of input query as relnode
   */
  protected abstract RelNode standardizeRel(RelNode relNode);

  /**
   * Similar to {@link #convertSql(String)} but converts hive view definition stored
   * in the hive metastore to corresponding {@link RelNode} implementation.
   * This sets up the initial context for resolving Dali function names using table parameters.
   * @param hiveDbName hive database name
   * @param hiveViewName hive view name whose definition to convert.  Table name is allowed.
   * @return Calcite {@link RelNode} representation of hive view definition
   */
  public RelNode convertView(String hiveDbName, String hiveViewName) {
    SqlNode sqlNode = processView(hiveDbName, hiveViewName);
    Table view = relContextProvider.getHiveSchema().getSubSchema(hiveDbName).getTable(hiveViewName);
    if (view != null) {
      sqlNode.accept(new FuzzyUnionSqlRewriter(hiveViewName, relContextProvider));
    }
    return toRel(sqlNode);
  }

  /**
   * Creates a parse tree for a hive view using the expanded view text from hive metastore.
   * This table name is required for handling dali function name resolution.
   * @param hiveView hive table handle to read expanded text from.  Table name is also allowed.
   * @return Calcite SqlNode representing parse tree that calcite framework can understand
   */
  public SqlNode processViewOrTable(@Nonnull org.apache.hadoop.hive.metastore.api.Table hiveView) {
    checkNotNull(hiveView);
    String stringViewExpandedText = null;
    if (hiveView.getTableType().equals("VIRTUAL_VIEW")) {
      stringViewExpandedText = hiveView.getViewExpandedText();
    } else {
      // It is a table, not a view.
      stringViewExpandedText = "SELECT * FROM " + hiveView.getDbName() + "." + hiveView.getTableName();
    }

    return toSqlNode(stringViewExpandedText, hiveView);
  }

  /**
   * Gets the hive table handle for db and table and calls {@link #processViewOrTable(org.apache.hadoop.hive.metastore.api.Table)}
   *
   * @param dbName database name
   * @param tableName table name
   * @return {@link SqlNode} object
   */
  public SqlNode processView(String dbName, String tableName) {
    org.apache.hadoop.hive.metastore.api.Table table = getMscOrThrow().getTable(dbName, tableName);
    if (table == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableName));
    }
    return processViewOrTable(table);
  }

  private HiveMetastoreClient getMscOrThrow() {
    HiveMetastoreClient hiveMetastoreClient = relContextProvider.getHiveMetastoreClient();
    if (hiveMetastoreClient == null) {
      throw new RuntimeException("Hive metastore client is required to access table");
    } else {
      return hiveMetastoreClient;
    }
  }

  @VisibleForTesting
  // TODO: Change back to package protected once tests are moved to the same package
  public RelNode toRel(SqlNode sqlNode) {
    RelRoot root = relContextProvider.getSqlToRelConverter().convertQuery(sqlNode, true, true);
    return standardizeRel(root.rel);
  }
}
