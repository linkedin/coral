/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.Map;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.google.common.base.Preconditions.*;


public class HiveToTrinoConverter {

  private final HiveToRelConverter hiveToRelConverter;
  private final RelToTrinoConverter relToTrinoConverter;

  public static HiveToTrinoConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(mscClient);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    return new HiveToTrinoConverter(hiveToRelConverter, relToTrinoConverter);
  }

  public static HiveToTrinoConverter create(HiveMetastoreClient mscClient, Map<String, Boolean> configs) {
    checkNotNull(mscClient);
    checkNotNull(configs);
    HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(mscClient);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter(configs);
    return new HiveToTrinoConverter(hiveToRelConverter, relToTrinoConverter);
  }

  private HiveToTrinoConverter(HiveToRelConverter hiveToRelConverter, RelToTrinoConverter relToTrinoConverter) {
    this.hiveToRelConverter = hiveToRelConverter;
    this.relToTrinoConverter = relToTrinoConverter;
  }

  /**
   * Converts input HiveQL to Trino's SQL
   *
   * @param hiveSql hive sql query string
   * @return Trino-compatible SQL string representing input hiveSql
   */
  public String toTrinoSql(String hiveSql) {
    RelNode rel = hiveToRelConverter.convertSql(hiveSql);
    return relToTrinoConverter.convert(rel);
  }

  /**
   * Converts input view definition to Trino SQL
   * @param dbName hive DB name
   * @param viewName hive view base name
   * @return Trino-compatible SQL matching input view definition
   */
  public String toTrinoSql(String dbName, String viewName) {
    RelNode rel = hiveToRelConverter.convertView(dbName, viewName);
    return relToTrinoConverter.convert(rel);
  }
}
