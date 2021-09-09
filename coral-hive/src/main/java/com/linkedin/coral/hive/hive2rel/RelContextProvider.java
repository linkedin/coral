/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.linkned.coral.common.Function;
import com.linkned.coral.common.FunctionRegistry;
import com.linkned.coral.common.HiveMetastoreClient;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static com.linkedin.coral.hive.hive2rel.HiveSqlConformance.HIVE_SQL;


/**
 * Calcite needs different objects that are not trivial to create. This class
 * simplifies creation of objects, required by Calcite, easy. These objects
 * are created only once and shared across each call to corresponding getter.
 */
// TODO: Replace this with Google injection framework
public class RelContextProvider {
  private final FrameworkConfig config;
  private final HiveMetastoreClient hiveMetastoreClient;
  private RelBuilder relBuilder;
  private CalciteCatalogReader catalogReader;
  private HiveSqlValidator sqlValidator;
  private final HiveConvertletTable convertletTable = new HiveConvertletTable();
  private Driver driver;
  // maintain a mutable copy of Hive function registry in order to save some UDF information
  // resolved at run time.  For example, dependencies information.
  private HiveFunctionRegistry registry;
  private ConcurrentHashMap<String, HiveFunction> dynamicRegistry;
  private HiveSchema schema;
  private LocalMetastoreHiveSchema localMetastoreSchema;

  /**
   * Instantiates a new Rel context provider.
   *
   * @param hiveMetastoreClient Hive metastore client to construct Calcite schema
   */
  public RelContextProvider(@Nonnull HiveMetastoreClient hiveMetastoreClient) {
    super(hiveMetastoreClient);
  }

  public RelContextProvider(Map<String, Map<String, List<String>>> localMetaStore) {
    super(localMetaStore);
  }

  @Override
  public FunctionRegistry getFunctionRegistry() {
    return new StaticHiveFunctionRegistry();
  }

  /**
   * Gets {@link RelBuilder} object for generating relational algebra.
   *
   * @return the rel builder
   */
  public RelBuilder getRelBuilder() {
    if (relBuilder == null) {
      // Turn off Rel simplification. Rel simplification can statically interpret boolean conditions in
      // OR, AND, CASE clauses and simplify those. This has two problems:
      // 1. Our type system is not perfect replication of Hive so this can be incorrect
      // 2. Converted expression is harder to validate for correctness(because it appears different from input)
      Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
      relBuilder = HiveRelBuilder.create(config);
    }
    return relBuilder;
  }

  @Override
  public RelDataTypeSystemImpl getTypeSystem() {
    return new HiveTypeSystem();
  }

  @Override
  protected SqlRexConvertletTable getConvertletTable() {
    return new HiveConvertletTable();
  }

  /**
   * Gets hive sql validator.
   *
   * @return the hive sql validator
   */
  HiveSqlValidator getHiveSqlValidator() {
    if (sqlValidator == null) {
      sqlValidator = new HiveSqlValidator(config.getOperatorTable(), getCalciteCatalogReader(),
          ((JavaTypeFactory) relBuilder.getTypeFactory()), HIVE_SQL);
      sqlValidator.setDefaultNullCollation(NullCollation.LOW);
    }
    return sqlValidator;
  }

  /**
   * Gets rel opt cluster.
   *
   * @return the rel opt cluster
   */
  RelOptCluster getRelOptCluster() {
    // Create a new one every time so that RelOptCluster.nextCorrel starts from 0 again.
    // Need to ensure deterministic names for correlations for testing purposes.
    /** see {@link org.apache.calcite.plan.RelOptCluster} private field: nextCorrel */
    return RelOptCluster.create(new VolcanoPlanner(), getRelBuilder().getRexBuilder());
  }

  HiveViewExpander getViewExpander() {
    // we don't need to cache this...Okay to re-create each time
    return new HiveViewExpander(this);
  }

  /**
   * Gets sql to rel converter.
   *
   * @return the sql to rel converter
   */
  SqlToRelConverter getSqlToRelConverter() {
    // Create a new one every time so that RelOptCluster.nextCorrel starts from 0 again.
    // Need to ensure deterministic names for correlations for testing purposes.
    /** see {@link org.apache.calcite.plan.RelOptCluster} private field: nextCorrel */
    return new HiveSqlToRelConverter(getViewExpander(), getHiveSqlValidator(), getCalciteCatalogReader(),
        getRelOptCluster(), convertletTable,
        SqlToRelConverter.configBuilder().withRelBuilderFactory(HiveRelBuilder.LOGICAL_BUILDER).build());
  }
}
