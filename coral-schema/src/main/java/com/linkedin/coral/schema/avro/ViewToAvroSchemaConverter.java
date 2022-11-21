/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.annotations.VisibleForTesting;
import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.common.HiveMetastoreClient;
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
  private final HiveMetastoreClient hiveMetastoreClient;
  private final RelToAvroSchemaConverter relToAvroSchemaConverter;
  private static final Logger LOG = LoggerFactory.getLogger(ViewToAvroSchemaConverter.class);

  /**
   * Constructor to create an instance of ViewToAvroSchemaConverter
   *
   * @param hiveMetastoreClient
   */
  private ViewToAvroSchemaConverter(HiveMetastoreClient hiveMetastoreClient) {
    this.hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    this.relToAvroSchemaConverter = new RelToAvroSchemaConverter(hiveMetastoreClient);
    this.hiveMetastoreClient = hiveMetastoreClient;
  }

  /**
   * Use this method to create a new instance of ViewToAvroSchemaConverter
   *
   * @param hiveMetastoreClient {@link HiveMetastoreClient} object
   * @return an instance of ViewToAvroSchemaConverter
   */
  public static ViewToAvroSchemaConverter create(HiveMetastoreClient hiveMetastoreClient) {
    return new ViewToAvroSchemaConverter(hiveMetastoreClient);
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

  @VisibleForTesting
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

    Table tableOrView = hiveMetastoreClient.getTable(dbName, tableOrViewName);
    if (tableOrView == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableOrViewName));
    }

    if (!tableOrView.getTableType().equals("VIRTUAL_VIEW")) {
      // It's base table, just retrieve the avro schema from Hive metastore
      Schema schema = SchemaUtilities.getAvroSchemaForTable(tableOrView, strictMode);
      return forceLowercase ? ToLowercaseSchemaVisitor.visit(schema) : schema;
    } else {
      RelNode relNode = hiveToRelConverter.convertView(dbName, tableOrViewName);
      Schema schema = relToAvroSchemaConverter.convert(relNode, strictMode, forceLowercase);

      Schema avroSchema = schema;

      // In flex mode, we assign a new set of namespace
      if (!strictMode) {
        avroSchema = SchemaUtilities.setupNameAndNamespace(schema, tableOrViewName, dbName + "." + tableOrViewName);
      }

      if (forceLowercase) {
        avroSchema = ToLowercaseSchemaVisitor.visit(avroSchema);
      }

      return avroSchema;
    }
  }
}
