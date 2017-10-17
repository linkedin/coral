package com.linkedin.coral.hive.hive2rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.hadoop.hive.ql.optimizer.calcite.CalciteSemanticException;
import org.apache.hadoop.hive.ql.optimizer.calcite.translator.TypeConverter;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;


/**
 * Adaptor class from Hive {@link org.apache.hadoop.hive.ql.metadata.Table} representation to
 * Calcite {@link Table}
 */
public class HiveTable implements Table {

  private final org.apache.hadoop.hive.ql.metadata.Table hiveTable;

  /**
   * Constructor to create bridge from hive table to calcite table
   * @param hiveTable Hive table
   */
  public HiveTable(org.apache.hadoop.hive.ql.metadata.Table hiveTable) {
    this.hiveTable = hiveTable;
  }

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    StructObjectInspector rowOI;
    try {
      rowOI = (StructObjectInspector) hiveTable.getDeserializer().getObjectInspector();
    } catch (SerDeException e) {
      throw new RuntimeException("Failed to deserialize hive table type", e);
    }
    List<? extends StructField> fields = rowOI.getAllStructFieldRefs();

    List<RelDataType> fieldTypes = new ArrayList<>(fields.size());
    List<String> fieldNames = new ArrayList<>(fields.size());
    fields.forEach(f -> {
      TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromObjectInspector(f.getFieldObjectInspector());
      try {
        RelDataType relType = TypeConverter.convert(typeInfo, typeFactory);
        fieldNames.add(f.getFieldName());
        fieldTypes.add(relType);
      } catch (CalciteSemanticException e) {
        throw new RuntimeException("Failed to convert type " + typeInfo.getTypeName() + " to RelDataType", e);
      }
    });
    return typeFactory.createStructType(fieldTypes, fieldNames);
  }

  @Override
  public Statistic getStatistic() {
    return Statistics.UNKNOWN;
  }

  @Override
  public Schema.TableType getJdbcTableType() {
    switch (hiveTable.getTableType()) {
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
}
