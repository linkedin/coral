/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import com.linkedin.coral.common.catalog.HiveTable;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteTableAdapter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class HiveCalciteTableAdapter extends com.linkedin.coral.catalog.hive.HiveCalciteTableAdapter {

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteTableAdapter#HiveCalciteTableAdapter(org.apache.hadoop.hive.metastore.api.Table)} instead.
   */
  @Deprecated
  public HiveCalciteTableAdapter(org.apache.hadoop.hive.metastore.api.Table hiveTable) {
    super(hiveTable);
  }

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveCalciteTableAdapter#HiveCalciteTableAdapter(com.linkedin.coral.catalog.hive.HiveTable)} instead.
   */
  @Deprecated
  public HiveCalciteTableAdapter(HiveTable coralTable) {
    super(coralTable.getHiveTable());
  }
}
