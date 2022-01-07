/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;

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
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;


/**
 * This class is a replacement for {@link HiveTable} to work with localMetastore in coral-spark-plan module
 *
 * Adaptor class from Hive {@link org.apache.hadoop.hive.metastore.api.Table} representation to
 * Calcite {@link ScannableTable}
 *
 * Implementing this as a ScannableTable, instead of Table, is hacky approach to make calcite
 * correctly generate relational algebra. This will have to go away gradually.
 */
public class LocalMetastoreHiveTable implements ScannableTable {

  private final String tableName;
  private final List<String> columnInfo;

  /**
   * Overwritten constructor which can help to use local metastore
   * @param tableName name of the table
   * @param columnInfo list of column information of table, contains name and type of the column, like "name:string"
   */
  public LocalMetastoreHiveTable(String tableName, List<String> columnInfo) {
    this.tableName = tableName;
    this.columnInfo = columnInfo;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    if (tableName != null && columnInfo != null) {
      final List<RelDataType> fieldTypes = new ArrayList<>(columnInfo.size());
      final List<String> fieldNames = new ArrayList<>(columnInfo.size());
      columnInfo.forEach(col -> {
        String[] info = col.split("\\|");
        String colName = info[0];
        String colType = info[1];
        final TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(colType);
        final RelDataType relType = TypeConverter.convert(typeInfo, typeFactory);
        if (!fieldNames.contains(colName)) {
          fieldNames.add(colName);
          fieldTypes.add(relType);
        }
      });

      return typeFactory.createStructType(fieldTypes, fieldNames);
    }
    return null;
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
  public boolean isRolledUp(String s) {
    return false;
  }

  @Override
  public boolean rolledUpColumnValidInsideAgg(String s, SqlCall sqlCall, SqlNode sqlNode,
      CalciteConnectionConfig calciteConnectionConfig) {
    return true;
  }

  @Override
  public Enumerable<Object[]> scan(DataContext dataContext) {
    throw new RuntimeException("Calcite runtime is not supported");
  }
}
