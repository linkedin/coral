/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlNode;

import com.linkedin.coral.com.google.common.annotations.VisibleForTesting;
import com.linkedin.coral.hive.hive2rel.parsetree.ParseTreeBuilder;

import static com.google.common.base.Preconditions.*;


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
public class HiveToRelConverter {

  private final RelContextProvider relContextProvider;

  /**
   * Initializes converter with hive configuration at provided path
   * @param mscClient HiveMetaStoreClient. Hive metastore client provides small subset
   *                  of methods provided by Hive's metastore client interface.
   * @return {@link HiveToRelConverter} object
   */
  public static HiveToRelConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    RelContextProvider relContextProvider = new RelContextProvider(mscClient);
    return new HiveToRelConverter(relContextProvider);
  }

  /**
   * Initializes converter with local metastore instead of retrieving metadata using HiveMetastoreClient,
   * this initializer is for SparkPlanToIRRelConverter in coral-spark-plan module
   * @param localMetaStore Map containing the required metadata (database name, table name, column name and type)
   *                       needed by SparkPlanToIRRelConverter in coral-spark-plan module
   * @return {@link HiveToRelConverter}
   */
  public static HiveToRelConverter create(Map<String, Map<String, List<String>>> localMetaStore) {
    checkNotNull(localMetaStore);
    RelContextProvider relContextProvider = new RelContextProvider(localMetaStore);
    return new HiveToRelConverter(relContextProvider);
  }

  private HiveToRelConverter(RelContextProvider relContextProvider) {
    checkNotNull(relContextProvider);
    this.relContextProvider = relContextProvider;
  }

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
    SqlNode sqlNode = getTreeBuilder().processSql(sql);
    return toRel(sqlNode);
  }

  /**
   * Apply series of transforms to convert Hive relnode to
   * standardized intermediate representation. What is "standard"
   * is vague right now but we try to be closer to ANSI standard.
   * TODO: define standard intermediate representation
   * @param relNode calcite relnode representing hive query
   * @return standard representation of input query as relnode
   */
  private RelNode standardizeRel(RelNode relNode) {
    return new HiveRelConverter().convert(relNode);
  }

  /**
   * Similar to {@link #convertSql(String)} but converts hive view definition stored
   * in the hive metastore to corresponding {@link RelNode} implementation.
   * This sets up the initial context for resolving Dali function names using table parameters.
   * @param hiveDbName hive database name
   * @param hiveViewName hive view name whose definition to convert.  Table name is allowed.
   * @return Calcite {@link RelNode} representation of hive view definition
   */
  public RelNode convertView(String hiveDbName, String hiveViewName) {
    SqlNode sqlNode = getTreeBuilder().processView(hiveDbName, hiveViewName);
    Table view = relContextProvider.getHiveSchema().getSubSchema(hiveDbName).getTable(hiveViewName);
    if (view != null) {
      sqlNode.accept(new FuzzyUnionSqlRewriter(view, hiveViewName, relContextProvider));
    }
    return toRel(sqlNode);
  }

  @VisibleForTesting
  ParseTreeBuilder getTreeBuilder() {
    if (relContextProvider.getHiveSchema() == null) {
      return new ParseTreeBuilder(null, relContextProvider.getParseTreeBuilderConfig(),
          relContextProvider.getHiveFunctionRegistry(), relContextProvider.getDynamicHiveFunctionRegistry());
    }
    return new ParseTreeBuilder(relContextProvider.getHiveMetastoreClient(),
        relContextProvider.getParseTreeBuilderConfig(), relContextProvider.getHiveFunctionRegistry(),
        relContextProvider.getDynamicHiveFunctionRegistry());
  }

  @VisibleForTesting
  RelNode toRel(SqlNode sqlNode) {
    RelRoot root = relContextProvider.getSqlToRelConverter().convertQuery(sqlNode, true, true);
    return standardizeRel(root.rel);
  }
}
