/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.HashMap;
import java.util.Map;

import org.apache.iceberg.Table;

import com.linkedin.coral.common.IcebergToCoralTypeConverter;
import com.linkedin.coral.common.types.CoralDataType;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link CoralTable} interface for Apache Iceberg tables.
 * This class wraps an Iceberg Table object and provides a unified
 * CoralTable API for accessing table metadata.
 */
public class IcebergTable implements CoralTable {

  private final Table table;

  /**
   * Creates a new IcebergCoralTable wrapping the given Iceberg table.
   *
   * @param table Iceberg Table object (must not be null)
   */
  public IcebergTable(Table table) {
    this.table = checkNotNull(table, "Iceberg table cannot be null");
  }

  /**
   * Returns the fully qualified table name from Iceberg table.
   * Uses table.name() which returns the full table identifier.
   *
   * @return Fully qualified table name
   */
  @Override
  public String name() {
    return table.name();
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
   * INTERNAL API
   * @deprecated This method is for internal use only and will be removed in a future release.
   * Do not depend on this API.
   *
   * @return Iceberg Table object
   */
  public org.apache.iceberg.Table getIcebergTable() {
    return table;
  }

  /**
   * Returns the table schema in Coral type system.
   * Converts Iceberg schema to Coral types using IcebergToCoralTypeConverter.
   *
   * @return StructType representing the Iceberg table schema
   */
  @Override
  public CoralDataType getSchema() {
    return IcebergToCoralTypeConverter.convert(table.schema());
  }
}
