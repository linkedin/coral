/**
 * Copyright 2019-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import javax.annotation.Nullable;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.annotations.VisibleForTesting;
import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;


/**
 * This is the main class to generate the avro schema for a given Dali view
 *
 * The avro schema generation process is done in two steps:
 *  1) Use HiveToRelConverter to convert a Dali view to Calcite IR RelNode
 *  2) Use RelToAvroSchemaConverter to generate avro schema for Calcite IR RelNode
 *
 * Usage:
 *     ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
 *     Schema avroSchema = viewToAvroSchemaConverter.toAvroSchema("dbName", "viewOrTableName");
 */
public class ViewToAvroSchemaConverter {
  private final HiveToRelConverter hiveToRelConverter;
  private final RelToAvroSchemaConverter relToAvroSchemaConverter;
  // Exactly one of these is non-null per instance, mirroring the public factory split. The two
  // paths never mix: a CoralCatalog-backed converter never resolves through HiveMetastoreClient
  // and vice versa.
  @Nullable
  private final HiveMetastoreClient hiveMetastoreClient;
  @Nullable
  private final CoralCatalog coralCatalog;
  private static final Logger LOG = LoggerFactory.getLogger(ViewToAvroSchemaConverter.class);

  /**
   * Legacy constructor wired against {@link HiveMetastoreClient}. Reachable only via the
   * deprecated {@link #create(HiveMetastoreClient)} factory.
   */
  private ViewToAvroSchemaConverter(HiveMetastoreClient hiveMetastoreClient) {
    this.hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    this.relToAvroSchemaConverter = new RelToAvroSchemaConverter(hiveMetastoreClient);
    this.hiveMetastoreClient = hiveMetastoreClient;
    this.coralCatalog = null;
  }

  /**
   * CoralCatalog-backed constructor. The inner {@link HiveToRelConverter} and
   * {@link RelToAvroSchemaConverter} are wired against the same CoralCatalog, so view conversion
   * and table-scan resolution stay inside one abstraction end-to-end. Reachable only via
   * {@link #create(CoralCatalog)}.
   */
  private ViewToAvroSchemaConverter(CoralCatalog coralCatalog) {
    this.hiveToRelConverter = new HiveToRelConverter(coralCatalog);
    this.relToAvroSchemaConverter = new RelToAvroSchemaConverter(coralCatalog);
    this.hiveMetastoreClient = null;
    this.coralCatalog = coralCatalog;
  }

  /**
   * Legacy factory wired against {@link HiveMetastoreClient}.
   *
   * @deprecated use {@link #create(CoralCatalog)} instead.
   */
  @Deprecated
  public static ViewToAvroSchemaConverter create(HiveMetastoreClient hiveMetastoreClient) {
    Preconditions.checkNotNull(hiveMetastoreClient);
    return new ViewToAvroSchemaConverter(hiveMetastoreClient);
  }

  /**
   * Creates a CoralCatalog-backed converter. Base tables and table scans resolve through the
   * CoralCatalog uniformly across Hive- and Iceberg-backed tables. This and
   * {@link #create(HiveMetastoreClient)} are mutually exclusive entry points; instances built
   * by one factory never mix with the other.
   *
   * @param coralCatalog catalog used for table and view resolution; must not be null
   * @return a new converter wired to {@code coralCatalog}
   */
  public static ViewToAvroSchemaConverter create(CoralCatalog coralCatalog) {
    Preconditions.checkNotNull(coralCatalog);
    return new ViewToAvroSchemaConverter(coralCatalog);
  }

  /**
   * An API to generate the avro schema for a view
   *
   * @param dbName database name
   * @param tableOrViewName table or view name
   * @param strictMode if strictMode is set to True, we will not fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. In addition, original namespace in base tables will be preserved.
   *                   if strictMode is set to False, we will fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. A new set of namespace will be generated for
   *                        the resulting schema. The rule is as follows:
   *                                1. Top level namespace is dbName.tableOrViewName
   *                                2. Nested namespace is parentNamespace.parentFieldName
   *
   * @return avro schema for a given Dali view [dbName, viewName]
   */
  public Schema toAvroSchema(String dbName, String tableOrViewName, boolean strictMode) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Schema avroSchema = inferAvroSchema(dbName, tableOrViewName, strictMode, false);

    return avroSchema;
  }

  /**
   * An API to generate the avro schema for a view
   *
   * @param dbName database name
   * @param tableOrViewName table or view name
   * @param strictMode if strictMode is set to True, we will not fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. In addition, original namespace in base tables will be preserved.
   *                   if strictMode is set to False, we will fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. A new set of namespace will be generated for
   *                        the resulting schema. The rule is as follows:
   *                                1. Top level namespace is dbName.tableOrViewName
   *                                2. Nested namespace is parentNamespace.parentFieldName
   * @param forceLowercase if forceLowercase is set to True, we will return schema with lowercased field names
   *
   * @return avro schema for a given Dali view [dbName, viewName]
   */
  public Schema toAvroSchema(String dbName, String tableOrViewName, boolean strictMode, boolean forceLowercase) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Schema avroSchema = inferAvroSchema(dbName, tableOrViewName, strictMode, forceLowercase);

    return avroSchema;
  }

  /**
   * An API to generate the avro schema for a view
   *
   * @param dbName database name
   * @param tableOrViewName table or view name
   * @return avro schema for a given Dali view [dbName, viewName]
   */
  public Schema toAvroSchema(String dbName, String tableOrViewName) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Schema avroSchema = inferAvroSchema(dbName, tableOrViewName, false, false);

    return avroSchema;
  }

  /**
   * An API to generate the avro schema for a SQL string
   *
   * @param sql SQL string literal
   * @return avro schema for a given sql query
   */
  @VisibleForTesting
  public Schema toAvroSchema(String sql) {
    Preconditions.checkNotNull(sql);

    RelNode relNode = hiveToRelConverter.convertSql(sql);
    return relToAvroSchemaConverter.convert(relNode, false, false);
  }

  public Schema toAvroSchema(String sql, boolean strictMode, boolean forceLowercase) {
    Preconditions.checkNotNull(sql);

    RelNode relNode = hiveToRelConverter.convertSql(sql);
    return relToAvroSchemaConverter.convert(relNode, strictMode, forceLowercase);
  }

  /**
   * An API to generate the avro schema string for a view
   *
   * @param dbName database name
   * @param tableOrViewName table or view name
   * @param strictMode if strictMode is set to True, we will not fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. In addition, original namespace in base tables will be preserved.
   *                   if strictMode is set to False, we will fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. A new set of namespace will be generated for
   *                        the resulting schema. The rule is as follows:
   *                                1. Top level namespace is dbName.tableOrViewName
   *                                2. Nested namespace is parentNamespace.parentFieldName
   * @param isPrettyPrint whether to print the schema in pretty print mode
   *
   * @return avro schema String for a given Dali view [dbName, viewName]
   */
  public String toAvroSchemaString(String dbName, String tableOrViewName, boolean strictMode, boolean isPrettyPrint) {
    return toAvroSchema(dbName, tableOrViewName, strictMode).toString(isPrettyPrint);
  }

  /**
   * An API to generate the avro schema string for a view
   *
   * @param dbName database name
   * @param tableOrViewName table or view name
   * @param strictMode if strictMode is set to True, we will not fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. In addition, original namespace in base tables will be preserved.
   *                   if strictMode is set to False, we will fall back to Hive schema if avro.schema.literal
   *                        is missing in table properties. A new set of namespace will be generated for
   *                        the resulting schema. The rule is as follows:
   *                                1. Top level namespace is dbName.tableOrViewName
   *                                2. Nested namespace is parentNamespace.parentFieldName
   *
   * @return avro schema String for a given Dali view [dbName, viewName]
   */
  public String toAvroSchemaString(String dbName, String tableOrViewName, boolean strictMode) {
    return toAvroSchemaString(dbName, tableOrViewName, strictMode, false);
  }

  private Schema inferAvroSchema(String dbName, String tableOrViewName, boolean strictMode, boolean forceLowercase) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    if (coralCatalog != null) {
      return inferAvroSchemaUsingCoralCatalog(dbName, tableOrViewName, strictMode, forceLowercase);
    }
    return inferAvroSchemaUsingHiveMetastore(dbName, tableOrViewName, strictMode, forceLowercase);
  }

  /**
   * CoralCatalog-backed resolution. Tables and views are both surfaced through
   * {@link CoralCatalog#getTable}; views use the CoralCatalog-aware
   * {@link HiveToRelConverter} and {@link RelToAvroSchemaConverter}.
   */
  private Schema inferAvroSchemaUsingCoralCatalog(String dbName, String tableOrViewName, boolean strictMode,
      boolean forceLowercase) {
    CoralTable coralTable = coralCatalog.getTable(dbName, tableOrViewName);
    if (coralTable == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableOrViewName));
    }

    if (coralTable.tableType() == TableType.TABLE) {
      Schema schema = SchemaUtilities.getAvroSchemaForTable(coralTable, strictMode);
      return forceLowercase ? ToLowercaseSchemaVisitor.visit(schema) : schema;
    }

    RelNode relNode = hiveToRelConverter.convertView(dbName, tableOrViewName);
    Schema avroSchema = relToAvroSchemaConverter.convert(relNode, strictMode, forceLowercase);
    if (!strictMode) {
      avroSchema = SchemaUtilities.setupNameAndNamespace(avroSchema, tableOrViewName, dbName + "." + tableOrViewName);
    }
    if (forceLowercase) {
      avroSchema = ToLowercaseSchemaVisitor.visit(avroSchema);
    }
    return avroSchema;
  }

  /**
   * Legacy {@link HiveMetastoreClient}-backed resolution. Behavior is unchanged from before the
   * CoralCatalog API was introduced.
   */
  private Schema inferAvroSchemaUsingHiveMetastore(String dbName, String tableOrViewName, boolean strictMode,
      boolean forceLowercase) {
    Table tableOrView = hiveMetastoreClient.getTable(dbName, tableOrViewName);
    if (tableOrView == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableOrViewName));
    }

    if (!tableOrView.getTableType().equals("VIRTUAL_VIEW")) {
      // It's base table, just retrieve the avro schema from Hive metastore
      Schema schema = SchemaUtilities.getAvroSchemaForTable(tableOrView, strictMode);
      return forceLowercase ? ToLowercaseSchemaVisitor.visit(schema) : schema;
    }

    RelNode relNode = hiveToRelConverter.convertView(dbName, tableOrViewName);
    Schema avroSchema = relToAvroSchemaConverter.convert(relNode, strictMode, forceLowercase);
    // In flex mode, we assign a new set of namespace
    if (!strictMode) {
      avroSchema = SchemaUtilities.setupNameAndNamespace(avroSchema, tableOrViewName, dbName + "." + tableOrViewName);
    }
    if (forceLowercase) {
      avroSchema = ToLowercaseSchemaVisitor.visit(avroSchema);
    }
    return avroSchema;
  }
}
