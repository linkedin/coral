/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
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
  private static final Logger LOG = LoggerFactory.getLogger(ViewToAvroSchemaConverter.class);

  /**
   * Constructor to create an instance of ViewToAvroSchemaConverter
   *
   * @param hiveMetastoreClient
   */
  private ViewToAvroSchemaConverter(HiveMetastoreClient hiveMetastoreClient) {
    this.hiveToRelConverter = HiveToRelConverter.create(hiveMetastoreClient);
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
   * This is the main API to generate the avro schema for a given Dali view
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

    Schema avroSchema = inferAvroSchema(dbName, tableOrViewName, strictMode);

    return avroSchema;
  }

  /**
   * This is the main API to generate the avro schema for a given Dali view
   *
   * @param dbName database name used to generate Avro schema
   * @param tableOrViewName table or view to generate Avro schema
   * @return avro schema for a given Dali view [dbName, viewName]
   */
  public Schema toAvroSchema(String dbName, String tableOrViewName) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Schema avroSchema = inferAvroSchema(dbName, tableOrViewName, false);

    return avroSchema;
  }

  private Schema inferAvroSchema(String dbName, String tableOrViewName, boolean strictMode) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Table tableOrView = hiveMetastoreClient.getTable(dbName, tableOrViewName);
    if (tableOrView == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableOrViewName));
    }

    if (!tableOrView.getTableType().equals("VIRTUAL_VIEW")) {
      // It's base table, just retrieve the avro schema from Hive metastore
      Schema tableSchema = SchemaUtilities.getAvroSchemaForTable(tableOrView, strictMode);

      return tableSchema;
    } else {
      RelNode relNode = hiveToRelConverter.convertView(dbName, tableOrViewName);
      RelToAvroSchemaConverter relToAvroSchemaConverter = new RelToAvroSchemaConverter(hiveMetastoreClient);

      Schema schema = relToAvroSchemaConverter.convert(relNode, strictMode);
      Schema avroSchema = schema;

      // In flex mode, we assign a new set of namespace
      if (!strictMode) {
        avroSchema = SchemaUtilities.setupNameAndNamespace(schema, tableOrViewName, dbName + "." + tableOrViewName);
      }

      return avroSchema;
    }
  }
}
