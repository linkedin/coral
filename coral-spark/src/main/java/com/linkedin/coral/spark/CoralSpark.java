/**
 * Copyright 2018-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.ViewDependency;
import com.linkedin.coral.common.ViewDependencyTracker;
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
 *  4) SqlNode representation
 *
 * This class converts a IR RelNode to a Spark SQL by
 *  1) Transforming it to a Spark RelNode with Spark details [[IRRelToSparkRelTransformer]]
 *  2) Constructing a Spark SQL AST by using [[SparkRelToSparkSqlConverter]]
 *
 */
public class CoralSpark {

  private final List<String> baseTables;
  private final List<ViewDependency> viewDependencies;
  private final List<SparkUDFInfo> sparkUDFInfoList;
  private final HiveMetastoreClient hiveMetastoreClient;
  private final SqlNode sqlNode;
  private final String sparkSql;

  private CoralSpark(List<String> baseTables, List<ViewDependency> viewDependencies,
      List<SparkUDFInfo> sparkUDFInfoList, String sparkSql, HiveMetastoreClient hmsClient, SqlNode sqlNode) {
    this.baseTables = baseTables;
    this.viewDependencies = viewDependencies;
    this.sparkUDFInfoList = sparkUDFInfoList;
    this.sparkSql = sparkSql;
    this.hiveMetastoreClient = hmsClient;
    this.sqlNode = sqlNode;
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
   *  4) SqlNode representation
   *
   * @param irRelNode A IR RelNode for which CoralSpark will be constructed.
   * @param hmsClient client interface used to interact with the Hive Metastore service.
   *
   * @return [[CoralSpark]]
   */
  public static CoralSpark create(RelNode irRelNode, HiveMetastoreClient hmsClient) {
    // Capture view dependencies that were collected during convertView -> expandView chain
    List<ViewDependency> viewDeps = ViewDependencyTracker.get().getViewDependencies();

    SparkRelInfo sparkRelInfo = IRRelToSparkRelTransformer.transform(irRelNode);
    Set<SparkUDFInfo> sparkUDFInfos = sparkRelInfo.getSparkUDFInfos();
    RelNode sparkRelNode = sparkRelInfo.getSparkRelNode();
    SqlNode sparkSqlNode = constructSparkSqlNode(sparkRelNode, sparkUDFInfos, hmsClient);
    String sparkSQL = constructSparkSQL(sparkSqlNode);
    List<String> baseTables = constructBaseTables(sparkRelNode);
    return new CoralSpark(baseTables, viewDeps, ImmutableList.copyOf(sparkUDFInfos), sparkSQL, hmsClient, sparkSqlNode);
  }

  /**
   * Users use this function as the main API for getting CoralSpark instance.
   * This should be used when user need to align the Coral-spark translated SQL
   * with Coral-schema output schema
   *
   * @param irRelNode An IR RelNode for which CoralSpark will be constructed.
   * @param schema Coral schema that is represented by an Avro schema
   * @param hmsClient client interface used to interact with the Hive Metastore service.
   * @return [[CoralSpark]]
   */
  public static CoralSpark create(RelNode irRelNode, Schema schema, HiveMetastoreClient hmsClient) {
    List<String> aliases = schema.getFields().stream().map(Schema.Field::name).collect(Collectors.toList());
    return createWithAlias(irRelNode, aliases, hmsClient);
  }

  private static CoralSpark createWithAlias(RelNode irRelNode, List<String> aliases, HiveMetastoreClient hmsClient) {
    // Capture view dependencies that were collected during convertView -> expandView chain
    List<ViewDependency> viewDeps = ViewDependencyTracker.get().getViewDependencies();

    SparkRelInfo sparkRelInfo = IRRelToSparkRelTransformer.transform(irRelNode);
    Set<SparkUDFInfo> sparkUDFInfos = sparkRelInfo.getSparkUDFInfos();
    RelNode sparkRelNode = sparkRelInfo.getSparkRelNode();
    SqlNode sparkSqlNode = constructSparkSqlNode(sparkRelNode, sparkUDFInfos, hmsClient);
    // Use a second pass visit to add explicit alias names,
    // only do this when it's not a select star case,
    // since for select star we don't need to add any explicit aliases
    if (sparkSqlNode.getKind() == SqlKind.SELECT && !isSelectStar(sparkSqlNode)) {
      sparkSqlNode = sparkSqlNode.accept(new AddExplicitAlias(aliases));
    }
    String sparkSQL = constructSparkSQL(sparkSqlNode);
    List<String> baseTables = constructBaseTables(sparkRelNode);
    return new CoralSpark(baseTables, viewDeps, ImmutableList.copyOf(sparkUDFInfos), sparkSQL, hmsClient, sparkSqlNode);
  }

  private static SqlNode constructSparkSqlNode(RelNode sparkRelNode, Set<SparkUDFInfo> sparkUDFInfos,
      HiveMetastoreClient hmsClient) {
    CoralRelToSqlNodeConverter rel2sql = new CoralRelToSqlNodeConverter();
    SqlNode coralSqlNode = rel2sql.convert(sparkRelNode);

    SqlNode coralSqlNodeWithRelDataTypeDerivedConversions =
        coralSqlNode.accept(new DataTypeDerivedSqlCallConverter(hmsClient, coralSqlNode, sparkUDFInfos));

    SqlNode sparkSqlNode = coralSqlNodeWithRelDataTypeDerivedConversions
        .accept(new CoralSqlNodeToSparkSqlNodeConverter()).accept(new CoralToSparkSqlCallConverter(sparkUDFInfos));
    return sparkSqlNode.accept(new SparkSqlRewriter());
  }

  public static String constructSparkSQL(SqlNode sparkSqlNode) {
    return sparkSqlNode.toSqlString(SparkSqlDialect.INSTANCE).getSql();
  }

  private static boolean isSelectStar(SqlNode node) {
    if (node.getKind() == SqlKind.SELECT && ((SqlSelect) node).getSelectList().size() == 1
        && ((SqlSelect) node).getSelectList().get(0) instanceof SqlIdentifier) {
      SqlIdentifier identifier = (SqlIdentifier) ((SqlSelect) node).getSelectList().get(0);
      return identifier.isStar();
    }
    return false;
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
   * Getter for the view dependency chain collected during view expansion.
   * Each {@link ViewDependency} represents a view and its immediate dependencies
   * (which can be other views or base tables) in "catalog.database_name.table_name" format
   * (e.g. "hive.db.v1", "openhouse.db.t1").
   *
   * <p>For example, if view "hive.db.v1" depends on view "hive.db.v2" and table "openhouse.db.t1",
   * and "hive.db.v2" depends on tables "hive.db.t3" and "openhouse.db.t4", this returns:
   * <pre>
   * [ViewDependency("hive.db.v1", ["hive.db.v2", "openhouse.db.t1"]),
   *  ViewDependency("hive.db.v2", ["hive.db.t3", "openhouse.db.t4"])]
   * </pre>
   *
   * @return List of {@link ViewDependency} representing the view dependency chain,
   *         or an empty list if no views were involved.
   */
  public List<ViewDependency> getViewDependencies() {
    return viewDependencies;
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

  /**
   * Getter for the SqlNode representation
   *
   * @return SqlNode
   */
  public SqlNode getSqlNode() {
    return sqlNode;
  }
}
