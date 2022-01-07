/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.validate.SqlNameMatchers;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Util;

import com.linkedin.coral.com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Public class to convert SQL dialects to Calcite relational algebra.
 * This class should serve as the main entry point for clients to convert
 * SQL queries.
 */
public abstract class ToRelConverter {

  private final HiveMetastoreClient hiveMetastoreClient;
  private final FrameworkConfig config;
  private final SqlRexConvertletTable convertletTable = getConvertletTable();
  private CalciteCatalogReader catalogReader;
  private RelBuilder relBuilder;

  protected abstract SqlRexConvertletTable getConvertletTable();

  protected abstract SqlValidator getSqlValidator();

  protected abstract SqlOperatorTable getOperatorTable();

  protected abstract SqlToRelConverter getSqlToRelConverter();

  protected abstract SqlNode toSqlNode(String sql, org.apache.hadoop.hive.metastore.api.Table hiveView);

  /**
   * Apply series of transforms to convert Hive relnode to
   * standardized intermediate representation.
   * @param relNode calcite relnode representing hive query
   * @return standard representation of input query as relnode
   */
  protected abstract RelNode standardizeRel(RelNode relNode);

  protected ToRelConverter(@Nonnull HiveMetastoreClient hiveMetastoreClient) {
    checkNotNull(hiveMetastoreClient);
    this.hiveMetastoreClient = hiveMetastoreClient;
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, new HiveSchema(hiveMetastoreClient));
    // this is to ensure that jdbc:calcite driver is correctly registered
    // before initializing framework (which needs it)
    // We don't want each engine to register the driver. It may not also load correctly
    // if the service uses its own service loader (see Trino)
    new Driver();
    config = Frameworks.newConfigBuilder().convertletTable(convertletTable).defaultSchema(schemaPlus)
        .typeSystem(new HiveTypeSystem()).traitDefs((List<RelTraitDef>) null).operatorTable(getOperatorTable())
        .programs(Programs.ofRules(Programs.RULE_SET)).build();

  }

  protected ToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
    this.hiveMetastoreClient = null;
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, new LocalMetastoreHiveSchema(localMetaStore));
    // this is to ensure that jdbc:calcite driver is correctly registered
    // before initializing framework (which needs it)
    // We don't want each engine to register the driver. It may not also load correctly
    // if the service uses its own service loader (see Trino)
    new Driver();
    config = Frameworks.newConfigBuilder().convertletTable(convertletTable).defaultSchema(schemaPlus)
        .typeSystem(new HiveTypeSystem()).traitDefs((List<RelTraitDef>) null).operatorTable(getOperatorTable())
        .programs(Programs.ofRules(Programs.RULE_SET)).build();

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
    return toRel(toSqlNode(sql));
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
    SqlNode sqlNode = processView(hiveDbName, hiveViewName);
    sqlNode.accept(new FuzzyUnionSqlRewriter(hiveViewName, this));
    return toRel(sqlNode);
  }

  // TODO change back to protected once the relevant tests move to the common package
  @VisibleForTesting
  public SqlNode toSqlNode(String sql) {
    return toSqlNode(sql, null);
  }

  /**
   * Creates a parse tree for a hive table/view.
   * @param dbName database name
   * @param tableName table/view name
   * @return Calcite SqlNode representing parse tree that calcite framework can understand
   */
  @VisibleForTesting
  public SqlNode processView(String dbName, String tableName) {
    org.apache.hadoop.hive.metastore.api.Table table = hiveMetastoreClient.getTable(dbName, tableName);
    if (table == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableName));
    }
    String stringViewExpandedText = null;
    if (table.getTableType().equals("VIRTUAL_VIEW")) {
      stringViewExpandedText = table.getViewExpandedText();
    } else {
      // It is a table, not a view.
      stringViewExpandedText = "SELECT * FROM " + dbName + "." + tableName;
    }
    return toSqlNode(stringViewExpandedText, table);
  }

  @VisibleForTesting
  protected RelNode toRel(SqlNode sqlNode) {
    RelRoot root = getSqlToRelConverter().convertQuery(sqlNode, true, true);
    return standardizeRel(root.rel);
  }

  /**
   * Gets {@link RelBuilder} object for generating relational algebra.
   *
   * @return the rel builder
   */
  protected RelBuilder getRelBuilder() {
    // Turn off Rel simplification. Rel simplification can statically interpret boolean conditions in
    // OR, AND, CASE clauses and simplify those. This has two problems:
    // 1. Our type system is not perfect replication of Hive so this can be incorrect
    // 2. Converted expression is harder to validate for correctness(because it appears different from input)
    if (relBuilder == null) {
      Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
      relBuilder = RelBuilder.create(config);
    }
    return relBuilder;
  }

  /**
   * This class allows CalciteCatalogReader to have multiple schemaPaths, for example:
   * ["hive", "default"], ["hive"], and []
   */
  static class MultiSchemaPathCalciteCatalogReader extends CalciteCatalogReader {

    public MultiSchemaPathCalciteCatalogReader(CalciteSchema rootSchema, List<List<String>> schemaPathList,
        RelDataTypeFactory typeFactory, CalciteConnectionConfig config) {
      super(rootSchema, SqlNameMatchers.withCaseSensitive(config != null && config.caseSensitive()),
          Util.immutableCopy(schemaPathList), typeFactory, config);
    }
  }

  /**
   * Gets calcite catalog reader.
   *
   * @return the calcite catalog reader
   */
  protected CalciteCatalogReader getCalciteCatalogReader() {
    CalciteConnectionConfig connectionConfig;
    if (config.getContext() != null) {
      connectionConfig = config.getContext().unwrap(CalciteConnectionConfig.class);
    } else {
      Properties properties = new Properties();
      properties.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), String.valueOf(false));
      connectionConfig = new CalciteConnectionConfigImpl(properties);
    }
    if (catalogReader == null) {
      catalogReader = new MultiSchemaPathCalciteCatalogReader(config.getDefaultSchema().unwrap(CalciteSchema.class),
          ImmutableList.of(ImmutableList.of(HiveSchema.ROOT_SCHEMA, HiveSchema.DEFAULT_DB),
              ImmutableList.of(HiveSchema.ROOT_SCHEMA), ImmutableList.of()),
          getRelBuilder().getTypeFactory(), connectionConfig);
    }
    return catalogReader;
  }
}
