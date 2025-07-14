/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.linkedin.relocated.org.apache.calcite.adapter.java.JavaTypeFactory;
import com.linkedin.relocated.org.apache.calcite.plan.RelOptCluster;
import com.linkedin.relocated.org.apache.calcite.plan.volcano.VolcanoPlanner;
import com.linkedin.relocated.org.apache.calcite.sql.SqlNode;
import com.linkedin.relocated.org.apache.calcite.sql.SqlOperatorTable;
import com.linkedin.relocated.org.apache.calcite.sql.fun.SqlStdOperatorTable;
import com.linkedin.relocated.org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import com.linkedin.relocated.org.apache.calcite.sql.validate.SqlValidator;
import com.linkedin.relocated.org.apache.calcite.sql2rel.SqlRexConvertletTable;
import com.linkedin.relocated.org.apache.calcite.sql2rel.SqlToRelConverter;

import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.FuzzyUnionSqlRewriter;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveRelBuilder;
import com.linkedin.coral.common.ToRelConverter;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.parsetree.ParseTreeBuilder;

import static com.linkedin.coral.hive.hive2rel.HiveSqlConformance.HIVE_SQL;


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
  private SqlToRelConverter sqlToRelConverter;
  private final HiveFunctionResolver functionResolver =
      new HiveFunctionResolver(new StaticHiveFunctionRegistry(), new ConcurrentHashMap<>());
  private final
  // The validator must be reused
  SqlValidator sqlValidator = new HiveSqlValidator(getOperatorTable(), getCalciteCatalogReader(),
      ((JavaTypeFactory) getRelBuilder().getTypeFactory()), HIVE_SQL);

  public HiveToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
    super(hiveMetastoreClient);
    this.parseTreeBuilder = new ParseTreeBuilder(functionResolver);
  }

  public HiveToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
    super(localMetaStore);
    this.parseTreeBuilder = new ParseTreeBuilder(functionResolver);
  }

  public HiveFunctionResolver getFunctionResolver() {
    return functionResolver;
  }

  @Override
  protected SqlRexConvertletTable getConvertletTable() {
    return new CoralConvertletTable();
  }

  @Override
  public SqlValidator getSqlValidator() {
    return sqlValidator;
  }

  @Override
  protected SqlOperatorTable getOperatorTable() {
    return ChainedSqlOperatorTable.of(SqlStdOperatorTable.instance(), new DaliOperatorTable(functionResolver));
  }

  @Override
  protected SqlToRelConverter getSqlToRelConverter() {
    if (sqlToRelConverter == null) {
      sqlToRelConverter =
          new HiveSqlToRelConverter(new HiveViewExpander(this), getSqlValidator(), getCalciteCatalogReader(),
              RelOptCluster.create(new VolcanoPlanner(), new HiveRexBuilder(getRelBuilder().getTypeFactory())),
              getConvertletTable(),
              SqlToRelConverter.configBuilder().withRelBuilderFactory(HiveRelBuilder.LOGICAL_BUILDER).build());
    }
    return sqlToRelConverter;
  }

  @Override
  public SqlNode toSqlNode(String sql, Table hiveView) {
    final SqlNode sqlNode = parseTreeBuilder.process(trimParenthesis(sql), hiveView);
    if (hiveView != null) {
      sqlNode.accept(new FuzzyUnionSqlRewriter(hiveView.getTableName(), this));
    }
    return sqlNode.accept(new HiveSqlNodeToCoralSqlNodeConverter(getSqlValidator(), sqlNode));
  }

  private static String trimParenthesis(String value) {
    String str = value.trim();
    if (str.startsWith("(") && str.endsWith(")")) {
      return trimParenthesis(str.substring(1, str.length() - 1));
    }
    return str;
  }

}
