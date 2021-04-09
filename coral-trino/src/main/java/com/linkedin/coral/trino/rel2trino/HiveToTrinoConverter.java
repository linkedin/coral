/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.rel.RelNode;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.google.common.base.Preconditions.*;


public class HiveToTrinoConverter {

  private final HiveToRelConverter hiveToRelConverter;
  private final RelToTrinoConverter relToPrestoConverter;

  public static HiveToTrinoConverter create(HiveMetastoreClient mscClient) {
    checkNotNull(mscClient);
    HiveToRelConverter hiveToRelConverter = HiveToRelConverter.create(mscClient);
    RelToTrinoConverter relToPrestoConverter = new RelToTrinoConverter();
    return new HiveToTrinoConverter(hiveToRelConverter, relToPrestoConverter);
  }

  private HiveToTrinoConverter(HiveToRelConverter hiveToRelConverter, RelToTrinoConverter relToPrestoConverter) {
    this.hiveToRelConverter = hiveToRelConverter;
    this.relToPrestoConverter = relToPrestoConverter;
  }

  /**
   * Converts input HiveQL to Presto SQL
   *
   * @param hiveSql hive sql query string
   * @return presto sql string representing input hiveSql
   */
  public String toPrestoSql(String hiveSql) {
    RelNode rel = hiveToRelConverter.convertSql(hiveSql);
    return relToPrestoConverter.convert(rel);
  }

  /**
   * Converts input view definition to Presto SQL
   * @param dbName hive DB name
   * @param viewName hive view base name
   * @return Presto SQL matching input view definition
   */
  public String toPrestoSql(String dbName, String viewName) {
    RelNode rel = hiveToRelConverter.convertView(dbName, viewName);
    return relToPrestoConverter.convert(rel);
  }
}
