/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import com.google.common.base.Preconditions;

import org.apache.calcite.DataContext;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.iceberg.Table;

import com.linkedin.coral.common.catalog.IcebergCoralTable;
import com.linkedin.coral.common.catalog.TableType;


/**
 * Calcite Table implementation for Apache Iceberg tables.
 * Provides native Iceberg schema to Calcite instead of going through Hive metastore representation.
 *
 * This class uses IcebergCoralTable to access Iceberg table metadata and IcebergTypeConverter
 * to convert Iceberg schema to Calcite's RelDataType, preserving Iceberg type semantics.
 */
public class IcebergTable implements ScannableTable {

  private final IcebergCoralTable coralTable;

  /**
   * Creates IcebergTable from IcebergCoralTable.
   *
   * @param coralTable IcebergCoralTable from catalog
   */
  public IcebergTable(IcebergCoralTable coralTable) {
    Preconditions.checkNotNull(coralTable);
    this.coralTable = coralTable;
    if (coralTable.getIcebergTable() == null) {
      throw new IllegalArgumentException("IcebergCoralTable must have an Iceberg Table");
    }
  }

  /**
   * Returns the Calcite RelDataType for this Iceberg table.
   * Uses IcebergTypeConverter to convert native Iceberg schema to Calcite types.
   */
  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    return IcebergTypeConverter.convert(coralTable.getIcebergTable().schema(), coralTable.name(), typeFactory);
  }

  @Override
  public Statistic getStatistic() {
    // Future enhancement: Could use Iceberg statistics here
    // Iceberg provides rich statistics: row count, file count, size, etc.
    return Statistics.UNKNOWN;
  }

  @Override
  public Schema.TableType getJdbcTableType() {
    return coralTable.tableType() == TableType.VIEW ? Schema.TableType.VIEW : Schema.TableType.TABLE;
  }

  @Override
  public boolean isRolledUp(String column) {
    return false;
  }

  @Override
  public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent,
      CalciteConnectionConfig config) {
    return true;
  }

  @Override
  public Enumerable<Object[]> scan(DataContext root) {
    throw new RuntimeException("Calcite runtime execution is not supported");
  }

  /**
   * Returns the underlying Iceberg Table for advanced operations.
   *
   * @return org.apache.iceberg.Table instance
   */
  public Table getIcebergTable() {
    return coralTable.getIcebergTable();
  }
}
