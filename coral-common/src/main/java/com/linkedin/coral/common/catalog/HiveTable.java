/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.linkedin.coral.common.HiveToCoralTypeConverter;
import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;

import static com.google.common.base.Preconditions.*;


/**
 * Implementation of {@link CoralTable} interface for Hive tables.
 * This class wraps a Hive metastore Table object and provides
 * a unified CoralTable API for accessing table metadata.
 */
public class HiveTable implements CoralTable {

  private final Table table;

  /**
   * Creates a new HiveCoralTable wrapping the given Hive table.
   *
   * @param table Hive metastore Table object (must not be null)
   */
  public HiveTable(Table table) {
    this.table = checkNotNull(table, "Hive table cannot be null");
  }

  /**
   * Returns the fully qualified table name in the format "database.table".
   *
   * @return Fully qualified table name
   */
  @Override
  public String name() {
    return table.getDbName() + "." + table.getTableName();
  }

  /**
   * Returns the table properties/parameters.
   * This includes Hive table properties, SerDe properties,
   * and any custom properties set on the table.
   *
   * @return Map of table properties
   */
  @Override
  public Map<String, String> properties() {
    return table.getParameters() != null ? table.getParameters() : Collections.emptyMap();
  }

  /**
   * Returns the table type (TABLE or VIEW).
   * Hive table types like MANAGED_TABLE, EXTERNAL_TABLE map to TABLE.
   * VIRTUAL_VIEW and MATERIALIZED_VIEW map to VIEW.
   *
   * @return TableType enum value
   */
  @Override
  public TableType tableType() {
    String hiveTableType = table.getTableType();
    if (hiveTableType != null && hiveTableType.toUpperCase().contains("VIEW")) {
      return TableType.VIEW;
    }
    return TableType.TABLE;
  }

  /**
   * INTERNAL API
   * @deprecated This method is for internal use only and will be removed in a future release.
   * Do not depend on this API.
   *
   * @return Hive metastore Table object
   */
  public org.apache.hadoop.hive.metastore.api.Table getHiveTable() {
    return table;
  }

  /**
   * Returns the table schema in Coral type system.
   * This includes both regular columns (from StorageDescriptor) and partition columns.
   * Converts Hive TypeInfo to Coral types using HiveToCoralTypeConverter.
   *
   * @return StructType representing the full table schema (columns + partitions)
   */
  @Override
  public CoralDataType getSchema() {
    final List<FieldSchema> cols = table.getSd() != null ? table.getSd().getCols() : Collections.emptyList();
    final List<StructField> fields = new ArrayList<>();
    final List<String> fieldNames = new ArrayList<>();

    final Iterable<FieldSchema> allCols = Iterables.concat(cols, table.getPartitionKeys());

    for (FieldSchema col : allCols) {
      final String colName = col.getName();

      // Skip duplicate columns (partition keys might overlap with regular columns)
      if (!fieldNames.contains(colName)) {
        // Convert Hive type string to TypeInfo, then to CoralDataType
        final TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(col.getType());
        final CoralDataType coralType = HiveToCoralTypeConverter.convert(typeInfo);

        fields.add(StructField.of(colName, coralType));
        fieldNames.add(colName);
      }
    }

    // Return struct type representing the table schema
    // Table-level struct is nullable (Hive convention)
    return StructType.of(fields, true);
  }
}
