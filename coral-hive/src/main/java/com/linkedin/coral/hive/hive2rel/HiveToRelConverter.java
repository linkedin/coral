/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;

import com.linkned.coral.common.HiveMetastoreClient;
import com.linkned.coral.common.RelContextProvider;
import com.linkned.coral.common.ToRelConverter;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.hive.metastore.api.Table;

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
public class HiveToRelConverter extends ToRelConverter {
  private final ParseTreeBuilder parseTreeBuilder;

  protected HiveToRelConverter(RelContextProvider relContextProvider) {
    super(relContextProvider);
    this.parseTreeBuilder = new ParseTreeBuilder();
  }

  /**
   * Initializes converter with hive configuration at provided path
   * @param mscClient HiveMetaStoreClient. Hive metastore client provides small subset
   *                  of methods provided by Hive's metastore client interface.
   * @return {@link HiveToRelConverter} object
   */
  public static HiveToRelConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    RelContextProvider relContextProvider = new com.linkedin.coral.hive.hive2rel.RelContextProvider(mscClient);
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
    RelContextProvider relContextProvider = new com.linkedin.coral.hive.hive2rel.RelContextProvider(localMetaStore);
    return new HiveToRelConverter(relContextProvider);
  }

  @Override
  public SqlNode toSqlNode(String sql) {
    return parseTreeBuilder.processSql(trimParenthesis(sql));
  }

  @Override
  protected SqlNode toSqlNode(String sql, Table hiveView) {
    return parseTreeBuilder.process(trimParenthesis(sql), hiveView);
  }

  @Override
  protected RelNode standardizeRel(RelNode relNode) {
    return new HiveRelConverter().convert(relNode);
  }

  private static String trimParenthesis(String value) {
    String str = value.trim();
    if (str.startsWith("(") && str.endsWith(")")) {
      return trimParenthesis(str.substring(1, str.length() - 1));
    }
    return str;
  }

}
