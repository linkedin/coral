/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
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
import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.HiveCoralTable;
import com.linkedin.coral.common.catalog.IcebergCoralTable;
import com.linkedin.coral.common.catalog.IcebergHiveTableConverter;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Public class to convert SQL dialects to Calcite relational algebra.
 * This class should serve as the main entry point for clients to convert
 * SQL queries.
 * 
 * Supports both {@link com.linkedin.coral.common.catalog.CoralCatalog} (for unified
 * multi-format access to Hive/Iceberg tables) and {@link HiveMetastoreClient}
 * (for backward compatibility with Hive-only workflows).
 */
public abstract class ToRelConverter {

  private final CoralCatalog coralCatalog;
  private final HiveMetastoreClient msc;
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
   * Constructor for backward compatibility with HiveMetastoreClient.
   * 
   * @param hiveMetastoreClient Hive metastore client
   */
  protected ToRelConverter(@Nonnull HiveMetastoreClient hiveMetastoreClient) {
    checkNotNull(hiveMetastoreClient);
    this.msc = hiveMetastoreClient;
    this.coralCatalog = null;
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

  /**
   * Constructor accepting CoralCatalog for unified catalog access.
   * 
   * @param coralCatalog Coral catalog providing access to table metadata
   */
  protected ToRelConverter(@Nonnull CoralCatalog coralCatalog) {
    checkNotNull(coralCatalog);
    this.coralCatalog = coralCatalog;
    this.msc = null;
    SchemaPlus schemaPlus = Frameworks.createRootSchema(false);
    schemaPlus.add(HiveSchema.ROOT_SCHEMA, new HiveSchema(coralCatalog));
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
   * Constructor for local metastore (testing/development).
   * 
   * @param localMetaStore Local metastore map
   */
  protected ToRelConverter(Map<String, Map<String, List<String>>> localMetaStore) {
    this.coralCatalog = null;
    this.msc = null;
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
    if (coralCatalog != null) {
      return processViewWithCatalog(dbName, tableName);
    } else if (msc != null) {
      return processViewWithMsc(dbName, tableName);
    } else {
      throw new RuntimeException("Cannot process view without catalog or msc: " + dbName + "." + tableName);
    }
  }

  /**
   * Processes a table/view using CoralCatalog (supports Hive and Iceberg tables).
   */
  private SqlNode processViewWithCatalog(String dbName, String tableName) {
    CoralTable coralTable = coralCatalog.getTable(dbName, tableName);
    if (coralTable == null) {
      throw new RuntimeException("Table/view not found: " + dbName + "." + tableName);
    }

    String stringViewExpandedText;
    org.apache.hadoop.hive.metastore.api.Table table;

    if (coralTable instanceof HiveCoralTable) {
      // Hive coral table: can be TABLE or VIEW
      HiveCoralTable hiveCoralTable = (HiveCoralTable) coralTable;
      table = hiveCoralTable.getHiveTable();

      if (table.getTableType().equals("VIRTUAL_VIEW")) {
        // It's a view - use expanded view text
        stringViewExpandedText = table.getViewExpandedText();
      } else {
        // It's a Hive table
        stringViewExpandedText = "SELECT * FROM " + dbName + "." + tableName;
      }
    } else if (coralTable instanceof IcebergCoralTable) {
      // Iceberg coral table: always a table (Iceberg doesn't have views)
      IcebergCoralTable icebergCoralTable = (IcebergCoralTable) coralTable;

      // Convert Iceberg coral table to minimal Hive Table for backward compatibility
      // This is needed because downstream code (ParseTreeBuilder, HiveFunctionResolver)
      // expects a Hive Table object for Dali UDF resolution
      table = IcebergHiveTableConverter.toHiveTable(icebergCoralTable);
      stringViewExpandedText = "SELECT * FROM " + dbName + "." + tableName;
    } else {
      throw new RuntimeException("Unsupported coral table type for: " + dbName + "." + tableName);
    }

    return toSqlNode(stringViewExpandedText, table);
  }

  /**
   * Processes a table/view using HiveMetastoreClient (backward compatible, Hive-only path).
   */
  private SqlNode processViewWithMsc(String dbName, String tableName) {
    org.apache.hadoop.hive.metastore.api.Table hiveTable = msc.getTable(dbName, tableName);
    if (hiveTable == null) {
      throw new RuntimeException("Table/view not found: " + dbName + "." + tableName);
    }

    String stringViewExpandedText;
    if (hiveTable.getTableType().equals("VIRTUAL_VIEW")) {
      // It's a view - use expanded view text
      stringViewExpandedText = hiveTable.getViewExpandedText();
    } else {
      // It's a Hive table
      stringViewExpandedText = "SELECT * FROM " + dbName + "." + tableName;
    }

    return toSqlNode(stringViewExpandedText, hiveTable);
  }

  @VisibleForTesting
  protected RelNode toRel(SqlNode sqlNode) {
    RelRoot root = getSqlToRelConverter().convertQuery(sqlNode, true, true);
    return root.rel;
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
      relBuilder = HiveRelBuilder.create(config);
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
