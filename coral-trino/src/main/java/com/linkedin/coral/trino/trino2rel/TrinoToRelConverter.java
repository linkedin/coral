/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

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
import com.linkedin.coral.hive.hive2rel.DaliOperatorTable;
import com.linkedin.coral.hive.hive2rel.HiveConvertletTable;
import com.linkedin.coral.hive.hive2rel.HiveRelBuilder;
import com.linkedin.coral.hive.hive2rel.HiveSqlValidator;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionResolver;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.trino.trino2rel.parsetree.ParseTreeBuilder;
import com.linkedin.coral.trino.trino2rel.parsetree.ParserVisitorContext;
import com.linkedin.coral.trino.trino2rel.parsetree.TrinoParserDriver;

import static com.linkedin.coral.trino.trino2rel.TrinoSqlConformance.*;


/*
 * We provide this class as a public interface by providing a thin wrapper
 * around TrinoSqlToRelConverter. Directly using TrinoSqlToRelConverter will
 * expose public methods from SqlToRelConverter. Use of SqlToRelConverter
 * is likely to change in the future if we want more control over the
 * conversion process. This class abstracts that out.
 */
public class TrinoToRelConverter extends ToRelConverter {
  private final ParseTreeBuilder parseTreeBuilder = new ParseTreeBuilder();
  private final ParserVisitorContext parserVisitorContext = new ParserVisitorContext();
  private final HiveFunctionResolver functionResolver =
      new HiveFunctionResolver(new StaticHiveFunctionRegistry(), new ConcurrentHashMap<>());
  private final
  // The validator must be reused
  SqlValidator sqlValidator = new HiveSqlValidator(getOperatorTable(), getCalciteCatalogReader(),
      ((JavaTypeFactory) getRelBuilder().getTypeFactory()), TRINO_SQL);

  public TrinoToRelConverter(HiveMetastoreClient hiveMetastoreClient) {
    super(hiveMetastoreClient);
  }

  public TrinoToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
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
    return new TrinoSqlToRelConverter(new TrinoViewExpander(this), getSqlValidator(), getCalciteCatalogReader(),
        RelOptCluster.create(new VolcanoPlanner(), getRelBuilder().getRexBuilder()), getConvertletTable(),
        SqlToRelConverter.configBuilder().withRelBuilderFactory(HiveRelBuilder.LOGICAL_BUILDER).build());
  }

  @Override
  protected SqlNode toSqlNode(String sql, Table trinoView) {
    String trimmedSql = trimParenthesis(sql.toUpperCase());
    SqlNode parsedSqlNode = TrinoParserDriver.parse(trimmedSql).accept(parseTreeBuilder, parserVisitorContext);
    SqlNode convertedSqlNode = parsedSqlNode.accept(new Trino2CoralOperatorConverter());
    return convertedSqlNode;
  }

  @Override
  protected RelNode standardizeRel(RelNode relNode) {
    return relNode;
  }

  private static String trimParenthesis(String value) {
    String str = value.trim();
    if (str.startsWith("(") && str.endsWith(")")) {
      return trimParenthesis(str.substring(1, str.length() - 1));
    }
    return str;
  }

}
