/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;

import com.linkedin.coral.spark.containers.SparkRelInfo;
import com.linkedin.coral.spark.containers.SparkUDFInfo;
import com.linkedin.coral.spark.dialect.SparkSqlDialect;
import com.linkedin.coral.transformers.CoralRelToSqlNodeConverter;


/**
 * This is the main API class for Coral-Spark.
 *
 * Use `process` to get an instance of  CoralSparkInfo, which contains
 *  1) Spark SQL
 *  2) Base tables
 *  3) Spark UDF information objects, ie. List of {@link SparkUDFInfo}
 *
 * This class converts a IR RelNode to a Spark SQL by
 *  1) Transforming it to a Spark RelNode with Spark details [[IRRelToSparkRelTransformer]]
 *  2) Constructing a Spark SQL AST by using [[SparkRelToSparkSqlConverter]]
 *
 */
public class CoralSpark {

  private final List<String> baseTables;
  private final List<SparkUDFInfo> sparkUDFInfoList;
  private final String sparkSql;

  private CoralSpark(List<String> baseTables, List<SparkUDFInfo> sparkUDFInfoList, String sparkSql) {
    this.baseTables = baseTables;
    this.sparkUDFInfoList = sparkUDFInfoList;
    this.sparkSql = sparkSql;
  }

  /**
   * Users use this function as the main API for getting CoralSpark instance.
   *
   * Internally IR RelNode is converted to Spark RelNode, and Spark RelNode to Spark SQL.
   *
   * It returns an instance of CoralSpark which contains
   *  1) Spark SQL
   *  2) Base tables
   *  3) Spark UDF information objects, ie. List of {@link SparkUDFInfo}
   *
   * @param irRelNode A IR RelNode for which CoralSpark will be constructed.
   *
   * @return [[CoralSparkInfo]]
   */
  public static CoralSpark create(RelNode irRelNode) {
    SparkRelInfo sparkRelInfo = IRRelToSparkRelTransformer.transform(irRelNode);
    RelNode sparkRelNode = sparkRelInfo.getSparkRelNode();
    String sparkSQL = constructSparkSQL(sparkRelNode);
    List<String> baseTables = constructBaseTables(sparkRelNode);
    List<SparkUDFInfo> sparkUDFInfos = sparkRelInfo.getSparkUDFInfoList();
    return new CoralSpark(baseTables, sparkUDFInfos, sparkSQL);
  }

  /**
   * Users use this function as the main API for getting CoralSpark instance.
   * This should be used when user need to align the Coral-spark translated SQL
   * with Coral-schema output schema
   *
   * @param irRelNode An IR RelNode for which CoralSpark will be constructed.
   * @param schema Coral schema that is represented by an Avro schema
   * @return [[CoralSparkInfo]]
   */
  public static CoralSpark create(RelNode irRelNode, Schema schema) {
    List<String> aliases = schema.getFields().stream().map(Schema.Field::name).collect(Collectors.toList());
    return createWithAlias(irRelNode, aliases);
  }

  private static CoralSpark createWithAlias(RelNode irRelNode, List<String> aliases) {
    SparkRelInfo sparkRelInfo = IRRelToSparkRelTransformer.transform(irRelNode);
    RelNode sparkRelNode = sparkRelInfo.getSparkRelNode();
    String sparkSQL = constructSparkSQLWithExplicitAlias(sparkRelNode, aliases);
    List<String> baseTables = constructBaseTables(sparkRelNode);
    List<SparkUDFInfo> sparkUDFInfos = sparkRelInfo.getSparkUDFInfoList();
    return new CoralSpark(baseTables, sparkUDFInfos, sparkSQL);
  }

  /**
   * This function returns a completely expanded SQL statement in Spark SQL Dialect.
   *
   * A SQL statement is 'completely expanded' if it doesn't depend
   * on (or selects from) Hive views, but instead, just on base tables.
   * This function internally calls [[CoralRelToSqlNodeConverter]] module to
   * convert CoralSpark to SparkSQL.
   *
   * Converts Spark RelNode to Spark SQL
   *
   * @param sparkRelNode A Spark compatible RelNode
   *
   * @return SQL String in Spark SQL dialect which is 'completely expanded'
   */
  private static String constructSparkSQL(RelNode sparkRelNode) {
    CoralRelToSqlNodeConverter rel2sql = new CoralRelToSqlNodeConverter();
    SqlNode coralSqlNode = rel2sql.convert(sparkRelNode);
    SqlNode sparkSqlNode = coralSqlNode.accept(new CoralSqlNodeToSparkSqlNodeConverter());
    SqlNode rewrittenSparkSqlNode = sparkSqlNode.accept(new SparkSqlRewriter());
    return rewrittenSparkSqlNode.toSqlString(SparkSqlDialect.INSTANCE).getSql();
  }

  private static String constructSparkSQLWithExplicitAlias(RelNode sparkRelNode, List<String> aliases) {
    CoralRelToSqlNodeConverter rel2sql = new CoralRelToSqlNodeConverter();
    // Create temporary objects r and rewritten to make debugging easier
    SqlNode coralSqlNode = rel2sql.convert(sparkRelNode);
    SqlNode sparkSqlNode = coralSqlNode.accept(new CoralSqlNodeToSparkSqlNodeConverter());

    SqlNode rewritten = sparkSqlNode.accept(new SparkSqlRewriter());
    // Use a second pass visit to add explicit alias names,
    // only do this when it's not a select star case,
    // since for select star we don't need to add any explicit aliases
    if (rewritten.getKind() == SqlKind.SELECT && ((SqlSelect) rewritten).getSelectList() != null) {
      rewritten = rewritten.accept(new AddExplicitAlias(aliases));
    }
    return rewritten.toSqlString(SparkSqlDialect.INSTANCE).getSql();
  }

  /**
   * This function returns the list of base table names, in the format
   * "database_name.table_name".
   *
   * A 'base table' of a view, is the Hive table on which view is dependent on.
   *
   * @param relNode A RelNode (may or may not be Spark compatible)
   * @return List of {@link String} representing base tables on which a view
   * depends on.
   */
  private static List<String> constructBaseTables(RelNode relNode) {
    return RelOptUtil.findAllTables(relNode).stream().map(RelOptTable::getQualifiedName)
        .map(x -> String.join(".", x.get(1), x.get(2))).collect(Collectors.toList());

  }

  /**
   * Getter for list of base tables in the format "database_name.table_name".
   *
   * @return List of base table strings.
   */
  public List<String> getBaseTables() {
    return baseTables;
  }

  /**
   * Getter for Spark UDF information list:
   * Additional information required to use an UDF (for details, read [[SparkUDFInfo]])
   *
   * @return List of {@link SparkUDFInfo} : List of Spark UDF information
   */
  public List<SparkUDFInfo> getSparkUDFInfoList() {
    return sparkUDFInfoList;
  }

  /**
   * Getter for Spark SQL which is 'completely expanded'
   *
   * A SQL statement is 'completely expanded' if it doesn't depend
   * on (or selects from) Hive views, but instead, just on base tables.
   *
   * @return SQL string in HiveQL dialect which is 'completely expanded'
   */
  public String getSparkSql() {
    return sparkSql;
  }
}
