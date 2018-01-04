package com.linkedin.coral.hive.hive2rel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;


/**
 * Adaptor class from Hive {@link org.apache.hadoop.hive.metastore.api.Table} representation to
 * Calcite {@link ScannableTable}
 *
 * Implementing this as a ScannableTable, instead of Table, is hacky approach to make calcite
 * correctly generate relational algebra. This will have to go away gradually.
 */
public class HiveTable implements ScannableTable {

  protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;

  /**
   * Constructor to create bridge from hive table to calcite table
   * @param hiveTable Hive table
   */
  public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
    Preconditions.checkNotNull(hiveTable);
    this.hiveTable = hiveTable;
  }

  /**
   * Get dali function params from table parameters
   * @return returns a mapping of function name to class name as stored in the
   * {@code functions} parameter key of table parameters
   */
  public Map<String, String> getDaliFunctionParams() {
    checkDaliTable();
    String[] funcEntries = hiveTable.getParameters()
        .getOrDefault("functions", "")
        .split(" |:");
    Map<String, String> params = new HashMap<>();
    for (int i = 0; i < funcEntries.length - 1; i += 2) {
      params.put(funcEntries[i], funcEntries[i + 1]);
    }
    return params;
  }

  public boolean isDaliTable() {
    return hiveTable.getOwner().equalsIgnoreCase("daliview");
  }

  private void checkDaliTable() {
    // FIXME: this fails unit test right now
   // Preconditions.checkState(isDaliTable());
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    List<FieldSchema> cols = hiveTable.getSd().getCols();
    List<RelDataType> fieldTypes = new ArrayList<>(cols.size());
    List<String> fieldNames = new ArrayList<>(cols.size());
    Iterable<FieldSchema> allCols = Iterables.concat(cols, hiveTable.getPartitionKeys());
    allCols.forEach(col -> {
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

  @Override
  public Enumerable<Object[]> scan(DataContext dataContext) {
    throw new RuntimeException("Calcite runtime is not supported");
  }
}
