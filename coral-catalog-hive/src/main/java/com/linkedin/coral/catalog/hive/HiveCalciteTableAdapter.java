/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.hive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.apache.calcite.DataContext;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.MetaStoreUtils;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.CoralTypeToRelDataTypeConverter;
import com.linkedin.coral.common.types.StructField;
import com.linkedin.coral.common.types.StructType;


/**
 * Calcite adapter for Hive tables, bridging Hive metadata to Calcite's ScannableTable interface.
 *
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class HiveCalciteTableAdapter implements ScannableTable {

  private static final Logger LOG = LoggerFactory.getLogger(HiveCalciteTableAdapter.class);
  protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;
  private Deserializer deserializer;

  static final String TBLPROPERTIES_FUNCTIONS_KEY = "functions";
  static final String TBLPROPERTIES_DEPENDENCIES_KEY = "dependencies";

  private static final Splitter tblpropertiesSplitter =
      Splitter.on(Pattern.compile("\\s+")).omitEmptyStrings().trimResults();

  private static final Splitter.MapSplitter functionsKeyValueSplitter =
      tblpropertiesSplitter.withKeyValueSeparator(Splitter.on(":").limit(2));

  /**
   * Constructor to create bridge from hive table to calcite table.
   * @param hiveTable Hive table
   */
  public HiveCalciteTableAdapter(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
    Preconditions.checkNotNull(hiveTable);
    this.hiveTable = hiveTable;
  }

  /**
   * Constructor accepting HiveTable for unified catalog integration.
   * @param coralTable HiveTable from catalog
   */
  public HiveCalciteTableAdapter(HiveTable coralTable) {
    Preconditions.checkNotNull(coralTable);
    this.hiveTable = coralTable.getHiveTable();
  }

  public Map<String, String> getDaliFunctionParams() {
    checkDaliTable();
    final String functionsValue = hiveTable.getParameters().get(TBLPROPERTIES_FUNCTIONS_KEY);
    Map<String, String> params = new HashMap<>();
    if (functionsValue != null) {
      params = functionsKeyValueSplitter.split(functionsValue);
    }
    return params;
  }

  public List<String> getDaliUdfDependencies() {
    checkDaliTable();
    final String propertyValue = hiveTable.getParameters().get(TBLPROPERTIES_DEPENDENCIES_KEY);
    if (propertyValue != null) {
      return tblpropertiesSplitter.splitToList(propertyValue).stream()
          .map(s -> s.toLowerCase().startsWith("ivy://") ? s : "ivy://" + s).collect(Collectors.toList());
    }
    return ImmutableList.of();
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
    RelDataType hiveType = getRowTypeFromHiveType(typeFactory);

    try {
      RelDataType coralType = getRowTypeFromCoralType(typeFactory);

      if (!RelOptUtil.areRowTypesEqual(hiveType, coralType, false)) {
        LOG.warn("Hive and Coral type conversion mismatch for table {}.{}. Hive: {}, Coral: {}", hiveTable.getDbName(),
            hiveTable.getTableName(), hiveType, coralType);
      }
    } catch (Exception e) {
      LOG.warn("Coral type validation failed for table {}.{}. Proceeding with Hive type. Error: {}",
          hiveTable.getDbName(), hiveTable.getTableName(), e.getMessage(), e);
    }

    return hiveType;
  }

  private RelDataType getRowTypeFromCoralType(RelDataTypeFactory typeFactory) {
    StructType structType = (StructType) getCoralSchema();
    return CoralTypeToRelDataTypeConverter.convert(structType, typeFactory);
  }

  private RelDataType getRowTypeFromHiveType(RelDataTypeFactory typeFactory) {
    final List<FieldSchema> cols = getColumns();
    final List<RelDataType> fieldTypes = new ArrayList<>(cols.size());
    final List<String> fieldNames = new ArrayList<>(cols.size());
    final Iterable<FieldSchema> allCols = Iterables.concat(cols, hiveTable.getPartitionKeys());

    allCols.forEach(col -> {
      final TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(col.getType());
      final RelDataType relType = TypeConverter.convert(typeInfo, typeFactory);
      final String colName = col.getName();
      if (!fieldNames.contains(colName)) {
        fieldNames.add(colName);
        fieldTypes.add(relType);
      }
    });

    return typeFactory.createStructType(fieldTypes, fieldNames);
  }

  public CoralDataType getCoralSchema() {
    final List<FieldSchema> cols = getColumns();
    final List<StructField> fields = new ArrayList<>();
    final List<String> fieldNames = new ArrayList<>();

    final Iterable<FieldSchema> allCols = Iterables.concat(cols, hiveTable.getPartitionKeys());

    for (FieldSchema col : allCols) {
      final String colName = col.getName();

      if (!fieldNames.contains(colName)) {
        final TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(col.getType());
        final CoralDataType coralType = HiveToCoralTypeConverter.convert(typeInfo);

        fields.add(StructField.of(colName, coralType));
        fieldNames.add(colName);
      }
    }

    return StructType.of(fields, true);
  }

  private List<FieldSchema> getColumns() {
    StorageDescriptor sd = hiveTable.getSd();
    String serDeLib = getSerializationLib();
    if (serDeLib == null || serDeLib.isEmpty()) {
      return sd.getCols();
    } else {
      try {
        return MetaStoreUtils.getFieldsFromDeserializer(hiveTable.getTableName(), getDeserializer());
      } catch (Exception e) {
        LOG.warn("Failed to get columns using deserializer: {}", e.getMessage());
        return sd.getCols();
      }
    }
  }

  private String getSerializationLib() {
    return hiveTable.getSd().getSerdeInfo().getSerializationLib();
  }

  private Deserializer getDeserializer() {
    if (deserializer == null) {
      deserializer = getDeserializerFromMetaStore();
    }
    return deserializer;
  }

  private Deserializer getDeserializerFromMetaStore() {
    try {
      return MetaStoreUtils.getDeserializer(new Configuration(false), hiveTable, false);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
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
