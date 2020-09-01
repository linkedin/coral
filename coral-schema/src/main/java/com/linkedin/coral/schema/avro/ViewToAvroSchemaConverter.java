/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.Table;


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
   * @param dbName database name used to generate Avro schema
   * @param tableOrViewName table or view to generate Avro schema
   * @return avro schema for a given Dali view [dbName, viewName]
   */
  public Schema toAvroSchema(String dbName, String tableOrViewName) {
    Preconditions.checkNotNull(dbName);
    Preconditions.checkNotNull(tableOrViewName);

    Table tableOrView = hiveMetastoreClient.getTable(dbName, tableOrViewName);
    if (tableOrView == null) {
      throw new RuntimeException(String.format("Unknown table %s.%s", dbName, tableOrViewName));
    }

    if (!tableOrView.getTableType().equals("VIRTUAL_VIEW")) {
      // It's base table, just retrieve the avro schema from Hive metastore
      Schema tableSchema = SchemaUtilities.getCasePreservedSchemaFromTblProperties(tableOrView);
      if (tableSchema == null) {
        throw new RuntimeException("Cannot determine avro schema for table " + dbName + "." + tableOrViewName);
      }

      return tableSchema;
    } else {
      RelNode relNode = hiveToRelConverter.convertView(dbName, tableOrViewName);
      RelToAvroSchemaConverter relToAvroSchemaConverter = new RelToAvroSchemaConverter(hiveMetastoreClient);

      Schema schema = relToAvroSchemaConverter.convert(relNode);

      // handle schema name and namespace
      Schema avroSchema = SchemaUtilities.setupNameAndNamespace(
          schema,
          tableOrViewName,
          dbName + "." + tableOrViewName);

      return avroSchema;
    }
  }
}
