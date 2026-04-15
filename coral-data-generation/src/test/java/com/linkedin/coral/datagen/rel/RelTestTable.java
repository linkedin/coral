/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.rel;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * Simple test table for building Calcite RelNode trees programmatically.
 */
class RelTestTable implements Table {
  private final String name;
  private final List<String> fieldNames;
  private final List<SqlTypeName> fieldTypes;

  RelTestTable(String name, String[] fieldNames, SqlTypeName[] fieldTypes) {
    this.name = name;
    this.fieldNames = new ArrayList<>();
    this.fieldTypes = new ArrayList<>();
    for (int i = 0; i < fieldNames.length; i++) {
      this.fieldNames.add(fieldNames[i]);
      this.fieldTypes.add(fieldTypes[i]);
    }
  }

  String getName() {
    return name;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    List<RelDataType> types = new ArrayList<>();
    for (SqlTypeName typeName : fieldTypes) {
      types.add(typeFactory.createSqlType(typeName));
    }
    return typeFactory.createStructType(types, fieldNames);
  }

  @Override
  public Statistic getStatistic() {
    return Statistics.UNKNOWN;
  }

  @Override
  public Schema.TableType getJdbcTableType() {
    return Schema.TableType.TABLE;
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
}
