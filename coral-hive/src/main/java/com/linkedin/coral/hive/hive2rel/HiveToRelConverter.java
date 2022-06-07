/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.HiveMetastoreClient;
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
