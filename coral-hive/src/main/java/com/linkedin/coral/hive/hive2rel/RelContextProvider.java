/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.util.ChainedSqlOperatorTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RelBuilder;

import com.linkedin.coral.hive.hive2rel.functions.HiveFunction;
import com.linkedin.coral.hive.hive2rel.functions.HiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.parsetree.ParseTreeBuilder;

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
  private RelOptCluster cluster;
  private SqlToRelConverter relConverter;
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
    Preconditions.checkNotNull(hiveMetastoreClient);
    this.hiveMetastoreClient = hiveMetastoreClient;
    this.schema = new HiveSchema(hiveMetastoreClient);
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, schema);
    this.registry = new StaticHiveFunctionRegistry();
    this.dynamicRegistry = new ConcurrentHashMap<>();
    // this is to ensure that jdbc:calcite driver is correctly registered
    // before initializing framework (which needs it)
    // We don't want each engine to register the driver. It may not also load correctly
    // if the service uses its own service loader (see Presto)
    driver = new Driver();
    config = Frameworks.newConfigBuilder().convertletTable(convertletTable).defaultSchema(schemaPlus)
        .typeSystem(new HiveTypeSystem()).traitDefs((List<RelTraitDef>) null)
        .operatorTable(ChainedSqlOperatorTable.of(SqlStdOperatorTable.instance(),
            new DaliOperatorTable(this.registry, this.dynamicRegistry)))
        .programs(Programs.ofRules(Programs.RULE_SET)).build();
  }

  /**
   * Instantiates a new Rel context provider.
   *
   * @param localMetaStore in-memory version of Hive metastore client used to  construct Calcite schema
   */
  public RelContextProvider(Map<String, Map<String, List<String>>> localMetaStore) {
    this.hiveMetastoreClient = null;
    this.localMetastoreSchema = new LocalMetastoreHiveSchema(localMetaStore);
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, localMetastoreSchema);
    this.registry = new StaticHiveFunctionRegistry();
    this.dynamicRegistry = new ConcurrentHashMap<>();
    // this is to ensure that jdbc:calcite driver is correctly registered
    // before initializing framework (which needs it)
    // We don't want each engine to register the driver. It may not also load correctly
    // if the service uses its own service loader (see Presto)
    driver = new Driver();
    config = Frameworks.newConfigBuilder().convertletTable(convertletTable).defaultSchema(schemaPlus)
        .typeSystem(new HiveTypeSystem()).traitDefs((List<RelTraitDef>) null)
        .operatorTable(ChainedSqlOperatorTable.of(SqlStdOperatorTable.instance(),
            new DaliOperatorTable(this.registry, this.dynamicRegistry)))
        .programs(Programs.ofRules(Programs.RULE_SET)).build();
  }

  /**
   * Gets the local copy of HiveFunctionRegistry for current query.
   *
   * @return HiveFunctionRegistry map
   */
  public HiveFunctionRegistry getHiveFunctionRegistry() {
    return this.registry;
  }

  public ConcurrentHashMap<String, HiveFunction> getDynamicHiveFunctionRegistry() {
    return this.dynamicRegistry;
  }

  /**
   * Gets {@link FrameworkConfig} for creation of various objects
   * from Calcite object model
   *
   * @return FrameworkConfig object
   */
  public FrameworkConfig getConfig() {
    return config;
  }

  ParseTreeBuilder.Config getParseTreeBuilderConfig() {
    return new ParseTreeBuilder.Config().setCatalogName(HiveSchema.ROOT_SCHEMA).setDefaultDB(HiveDbSchema.DEFAULT_DB);
  }

  HiveMetastoreClient getHiveMetastoreClient() {
    return hiveMetastoreClient;
  }

  Schema getHiveSchema() {
    return (schema != null) ? this.schema : this.localMetastoreSchema;
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
      relBuilder = RelBuilder.create(config);
    }
    return relBuilder;
  }

  /**
   * Gets calcite catalog reader.
   *
   * @return the calcite catalog reader
   */
  CalciteCatalogReader getCalciteCatalogReader() {
    CalciteConnectionConfig connectionConfig;
    if (config.getContext() != null) {
      connectionConfig = config.getContext().unwrap(CalciteConnectionConfig.class);
    } else {
      Properties properties = new Properties();
      properties.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), String.valueOf(false));
      connectionConfig = new CalciteConnectionConfigImpl(properties);
    }
    if (catalogReader == null) {
      catalogReader = new CalciteCatalogReader(config.getDefaultSchema().unwrap(CalciteSchema.class),
          ImmutableList.of(HiveSchema.ROOT_SCHEMA), getRelBuilder().getTypeFactory(), connectionConfig);
    }
    return catalogReader;
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
    }
    return sqlValidator;
  }

  /**
   * Gets rel opt cluster.
   *
   * @return the rel opt cluster
   */
  RelOptCluster getRelOptCluster() {
    if (cluster == null) {
      cluster = RelOptCluster.create(new VolcanoPlanner(), getRelBuilder().getRexBuilder());
    }
    return cluster;
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
    if (relConverter == null) {
      relConverter = new HiveSqlToRelConverter(getViewExpander(), getHiveSqlValidator(), getCalciteCatalogReader(),
          getRelOptCluster(), convertletTable, SqlToRelConverter.configBuilder().build());
    }
    return relConverter;
  }
}
