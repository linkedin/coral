/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
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

import com.linkedin.coral.common.catalog.IcebergTable;
import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.common.types.CoralTypeToRelDataTypeConverter;
import com.linkedin.coral.common.types.StructType;


/**
 * Calcite adapter for Apache Iceberg tables, bridging Iceberg metadata to Calcite's ScannableTable interface.
 *
 * <p>This adapter provides native Iceberg schema to Calcite using two-stage conversion:
 * Iceberg → Coral → Calcite.
 *
 * <p>This class uses IcebergCoralTable to access Iceberg table metadata and converts
 * through the Coral type system for better abstraction and consistency with HiveCalciteTableAdapter.
 *
 * <p><b>Integration with ParseTreeBuilder and HiveFunctionResolver:</b> While this adapter itself
 * doesn't provide Dali UDF methods, Iceberg tables with UDF metadata still need to work with
 * {@code ParseTreeBuilder} and {@code HiveFunctionResolver}, which are currently tightly coupled to
 * {@link org.apache.hadoop.hive.metastore.api.Table}. The temporary workaround is
 * {@link com.linkedin.coral.common.catalog.IcebergHiveTableConverter}, which converts Iceberg tables
 * to Hive Table objects for UDF resolution.
 *
 * <p>This coupling is being addressed in <a href="https://github.com/linkedin/coral/issues/575">issue #575</a>,
 * which will refactor {@code ParseTreeBuilder} and {@code HiveFunctionResolver} to accept
 * {@link com.linkedin.coral.common.catalog.CoralTable} instead, enabling direct Iceberg support without conversion.
 *
 * @see com.linkedin.coral.common.catalog.IcebergHiveTableConverter
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class IcebergCalciteTableAdapter implements ScannableTable {

  private final IcebergTable coralTable;

  /**
   * Creates IcebergCalciteTableAdapter from IcebergCoralTable.
   *
   * @param coralTable IcebergCoralTable from catalog
   */
  public IcebergCalciteTableAdapter(IcebergTable coralTable) {
    Preconditions.checkNotNull(coralTable);
    this.coralTable = coralTable;
  }

  /**
   * Returns the row type (schema) for this Iceberg table.
   *
   * Uses two-stage conversion: Iceberg → Coral → Calcite.
   * This provides a unified type system abstraction across table formats.
   *
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the table schema
   */
  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    // Step 1: Iceberg → Coral
    StructType structType = (StructType) coralTable.getSchema();

    // Step 2: Coral → Calcite
    return CoralTypeToRelDataTypeConverter.convert(structType, typeFactory);
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
}
