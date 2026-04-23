/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.catalog.HiveTable;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class HiveCalciteViewAdapter extends com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter {

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter#HiveCalciteViewAdapter(Table, List)} instead.
   */
  @Deprecated
  public HiveCalciteViewAdapter(Table hiveTable, List<String> schemaPath) {
    super(hiveTable, schemaPath);
  }

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteViewAdapter#HiveCalciteViewAdapter(com.linkedin.coral.catalog.hive.HiveTable, List)} instead.
   */
  @Deprecated
  public HiveCalciteViewAdapter(HiveTable coralTable, List<String> schemaPath) {
    super(coralTable.getHiveTable(), schemaPath);
  }
}
