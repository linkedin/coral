package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;


/**
 * Adaptor class from Hive {@link org.apache.hadoop.hive.metastore.api.Table} representation to
 * Calcite {@link org.apache.calcite.schema.Table}
 */
public class HiveTable implements org.apache.calcite.schema.Table {

  private final org.apache.hadoop.hive.metastore.api.Table hiveTable;

  /**
   * Constructor to create bridge from hive table to calcite table
   * @param hiveTable Hive table
   */
  public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
    Preconditions.checkNotNull(hiveTable);
    this.hiveTable = hiveTable;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    List<FieldSchema> cols = hiveTable.getSd().getCols();
    List<RelDataType> fieldTypes = new ArrayList<>(cols.size());
    List<String> fieldNames = new ArrayList<>(cols.size());
    cols.forEach(col -> {
      TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(col.getType());
      RelDataType relType = TypeConverter.convert(typeInfo, typeFactory);
      fieldNames.add(col.getName());
      fieldTypes.add(relType);
    });
    return typeFactory.createStructType(fieldTypes, fieldNames);
  }

  @Override
  public Statistic getStatistic() {
    return Statistics.UNKNOWN;
  }

  @Override
  public Schema.TableType getJdbcTableType() {
    TableType tableType = Enum.valueOf(TableType.class, hiveTable.getTableType());
    switch (tableType) {
      case VIRTUAL_VIEW:
        return Schema.TableType.VIEW;
      case MANAGED_TABLE:
        return Schema.TableType.TABLE;
      case INDEX_TABLE:
        return Schema.TableType.INDEX;
        default:
          throw new RuntimeException("Unknown table type: " + hiveTable.getTableType());
    }
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
}
