/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.iceberg;

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

import com.linkedin.coral.common.catalog.TableType;
import com.linkedin.coral.common.types.CoralTypeToRelDataTypeConverter;
import com.linkedin.coral.common.types.StructType;


/**
 * Calcite adapter for Apache Iceberg tables, bridging Iceberg metadata to Calcite's ScannableTable interface.
 *
 * <p>Uses two-stage conversion: Iceberg -> Coral -> Calcite.
 *
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class IcebergCalciteTableAdapter implements ScannableTable {

  private final IcebergTable coralTable;

  /**
   * Creates IcebergCalciteTableAdapter from IcebergTable.
   *
   * @param coralTable IcebergTable from catalog
   */
  public IcebergCalciteTableAdapter(IcebergTable coralTable) {
    Preconditions.checkNotNull(coralTable);
    this.coralTable = coralTable;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    StructType structType = (StructType) coralTable.getSchema();
    return CoralTypeToRelDataTypeConverter.convert(structType, typeFactory);
  }

  @Override
  public Statistic getStatistic() {
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
