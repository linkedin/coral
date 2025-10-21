/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.iceberg.Table;
import org.apache.iceberg.avro.AvroSchemaUtil;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link Dataset} interface for Apache Iceberg tables.
 * This class wraps an Iceberg Table object and provides a unified
 * Dataset API for accessing table metadata.
 *
 * IcebergDataset provides native access to Iceberg table schemas,
 * properties, and metadata.
 */
public class IcebergDataset implements Dataset {

  private final Table table;
  private final String database;
  private final String tableName;

  /**
   * Creates a new IcebergDataset wrapping the given Iceberg table.
   *
   * @param table Iceberg Table object (must not be null)
   * @param database Database name
   * @param tableName Table name
   */
  public IcebergDataset(Table table, String database, String tableName) {
    this.table = checkNotNull(table, "Iceberg table cannot be null");
    this.database = checkNotNull(database, "Database name cannot be null");
    this.tableName = checkNotNull(tableName, "Table name cannot be null");
  }

  /**
   * Returns the fully qualified table name in the format "database.table".
   *
   * @return Fully qualified table name
   */
  @Override
  public String name() {
    return database + "." + tableName;
  }

  /**
   * Returns the Avro schema representation of this Iceberg table.
   * The schema is converted from Iceberg's native schema format
   * to Avro using Iceberg's built-in conversion utilities.
   *
   * @return Avro Schema representation of the table
   */
  @Override
  public Schema avroSchema() {
    try {
      // Convert Iceberg schema to Avro schema
      org.apache.iceberg.Schema icebergSchema = table.schema();
      return AvroSchemaUtil.convert(icebergSchema, tableName);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert Iceberg schema to Avro for table: " + name(), e);
    }
  }

  /**
   * Returns the table properties from Iceberg table metadata.
   * This includes properties set on the Iceberg table.
   *
   * @return Map of table properties
   */
  @Override
  public Map<String, String> properties() {
    if (table.properties() != null) {
      return new HashMap<>(table.properties());
    }
    return new HashMap<>();
  }

  /**
   * Returns the table type.
   * Iceberg tables are always considered physical tables (TABLE type).
   *
   * @return TableType.TABLE
   */
  @Override
  public TableType tableType() {
    return TableType.TABLE;
  }

  /**
   * Returns the underlying Iceberg Table object.
   * Used by Calcite integration layer (IcebergTable).
   *
   * @return Iceberg Table object
   */
  public org.apache.iceberg.Table getIcebergTable() {
    return table;
  }
}
