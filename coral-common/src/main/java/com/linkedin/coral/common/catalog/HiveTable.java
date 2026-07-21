/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import org.apache.hadoop.hive.metastore.api.Table;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveTable} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class HiveTable extends com.linkedin.coral.catalog.hive.HiveTable {

  /**
   * Creates a new HiveTable wrapping the given Hive table.
   *
   * @param table Hive metastore Table object (must not be null)
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveTable#HiveTable(Table)} instead.
   */
  @Deprecated
  public HiveTable(Table table) {
    super(table);
  }
}
