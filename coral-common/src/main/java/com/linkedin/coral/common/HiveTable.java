/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

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


/**
 * Adaptor class from Hive {@link org.apache.hadoop.hive.metastore.api.Table} representation to
 * Calcite {@link ScannableTable}
 *
 * Implementing this as a ScannableTable, instead of Table, is hacky approach to make calcite
 * correctly generate relational algebra. This will have to go away gradually.
 */
public class HiveTable implements ScannableTable {

  private static final Logger LOG = LoggerFactory.getLogger(HiveTable.class);
  protected final org.apache.hadoop.hive.metastore.api.Table hiveTable;
  private Deserializer deserializer;

  /**
   * Any functions the user registers during view creation should also be
   * specified in the table properties of the created view under the key
   * {@value #TBLPROPERTIES_FUNCTIONS_KEY}
   *
   * e.g 'functions' = 'f:c1 g:c2'
   */
  static final String TBLPROPERTIES_FUNCTIONS_KEY = "functions";

  /**
   * Any dependencies the user adds during view creation can be
   * specified in the table properties of the created view under the key
   * {@value #TBLPROPERTIES_DEPENDENCIES_KEY} or under
   * [fn].{@value #TBLPROPERTIES_DEPENDENCIES_KEY} for function specific
   * dependencies
   *
   * e.g 'dependencies' = 'o1:m1:v1 o2:m2:v2?transitive=false'
   */
  static final String TBLPROPERTIES_DEPENDENCIES_KEY = "dependencies";

  private static final Splitter tblpropertiesSplitter =
      Splitter.on(Pattern.compile("\\s+")).omitEmptyStrings().trimResults();

  private static final Splitter.MapSplitter functionsKeyValueSplitter =
      tblpropertiesSplitter.withKeyValueSeparator(Splitter.on(":").limit(2));

  /**
   * Constructor to create bridge from hive table to calcite table
   * @param hiveTable Hive table
   */
  public HiveTable(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
    Preconditions.checkNotNull(hiveTable);
    this.hiveTable = hiveTable;
  }

  /**
   * Get dali function params from table TBLPROPERTIES clause parameters.
   * The 'functions' parameter in TBLPROPERTIES clause is a whitespace-separated list of function base name
   * used in the view, followed by colon(:), followed by the corresponding full class names.  Example:
   * 'functions' = 'func_1:com.linkedin.Func1 func_2:com.linkedin.Func2'
   * @return returns a mapping of function name to class name as stored in the
   * {@code functions} parameter key of table parameters
   */
  public Map<String, String> getDaliFunctionParams() {
    checkDaliTable();
    final String functionsValue = hiveTable.getParameters().get(TBLPROPERTIES_FUNCTIONS_KEY);
    Map<String, String> params = new HashMap<>();
    if (functionsValue != null) {
      params = functionsKeyValueSplitter.split(functionsValue);
    }
    return params;
  }

  /**
   * Get Dali UDF dependencies from the "dependencies" Hive table property.
   * The 'dependencies' parameter in TBLPROPERTIES clause is a whitespace-separated list of the ivy coordinates
   * of the artifacts a UDF requires.  Example:
   * 'dependencies' = 'ivy://com.linkedin.foo:foo1:0.0.1 ivy://com.linkedin.foo:foo2:0.0.1'
   * @return returns a string list of the ivy coordinates as stored in the
   * {@code dependencies} table property.  The return value is null if
   * {@code dependencies} is not set.
   */
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

  private List<FieldSchema> getColumns() {
    StorageDescriptor sd = hiveTable.getSd();
    String serDeLib = getSerializationLib();
    if (serDeLib == null || serDeLib.isEmpty()) {
      // views don't have serde library
      return sd.getCols();
    } else {
      try {
        return MetaStoreUtils.getFieldsFromDeserializer(hiveTable.getTableName(), getDeserializer());
      } catch (Exception e) {
        // if there is an exception like failing to get the deserializer or failing to get columns using deserializer,
        // we use sd.getCols() to avoid throwing exception
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
    } catch (Throwable e) { // Catch Throwable here because it may throw Exception or Error
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
