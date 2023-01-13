/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveRelBuilder;
import com.linkedin.coral.common.ToRelConverter;
import com.linkedin.coral.hive.hive2rel.DaliOperatorTable;
import com.linkedin.coral.hive.hive2rel.HiveConvertletTable;
import com.linkedin.coral.hive.hive2rel.HiveSqlValidator;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.spark.spark2rel.parsetree.SparkParserDriver;

import static com.linkedin.coral.spark.spark2rel.SparkSqlConformance.SPARK_SQL;


/*
 * We provide this class as a public interface by providing a thin wrapper
 * around SparkSqlToRelConverter. Directly using SparkSqlToRelConverter will
 * expose public methods from SqlToRelConverter. Use of SqlToRelConverter
 * is likely to change in the future if we want more control over the
 * conversion process. This class abstracts that out.
 */
public class SparkToRelConverter extends ToRelConverter {
  private final HiveFunctionResolver functionResolver =
      new HiveFunctionResolver(new StaticHiveFunctionRegistry(), new ConcurrentHashMap<>());
  private final
  // The validator must be reused
  SqlValidator sqlValidator = new HiveSqlValidator(getOperatorTable(), getCalciteCatalogReader(),
      ((JavaTypeFactory) getRelBuilder().getTypeFactory()), SPARK_SQL);

  public SparkToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
    super(hiveMetastoreClient);
  }

  public SparkToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
    super(localMetaStore);
  }

  @Override
  protected SqlRexConvertletTable getConvertletTable() {
    return new HiveConvertletTable();
  }

  @Override
  protected SqlValidator getSqlValidator() {
    return sqlValidator;
  }

  @Override
  protected SqlOperatorTable getOperatorTable() {
    return ChainedSqlOperatorTable.of(SqlStdOperatorTable.instance(), new DaliOperatorTable(functionResolver));
  }

  @Override
  protected SqlToRelConverter getSqlToRelConverter() {
    return new SparkSqlToRelConverter(new SparkViewExpander(this), getSqlValidator(), getCalciteCatalogReader(),
        RelOptCluster.create(new VolcanoPlanner(), getRelBuilder().getRexBuilder()), getConvertletTable(),
        SqlToRelConverter.configBuilder().withRelBuilderFactory(HiveRelBuilder.LOGICAL_BUILDER).build());
  }

  @Override
  protected SqlNode toSqlNode(String sql, Table sparkView) {
    String trimmedSql = trimParenthesis(sql.toUpperCase());
    SqlNode parsedSqlNode = SparkParserDriver.parse(trimmedSql);
    SqlNode convertedSqlNode = parsedSqlNode.accept(new Spark2CoralOperatorConverter());
    return convertedSqlNode;
  }

  private static String trimParenthesis(String value) {
    String str = value.trim();
    if (str.startsWith("(") && str.endsWith(")")) {
      return trimParenthesis(str.substring(1, str.length() - 1));
    }
    return str;
  }

}
