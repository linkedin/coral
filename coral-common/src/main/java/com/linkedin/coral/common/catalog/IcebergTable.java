/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import org.apache.iceberg.Table;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergTable} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class IcebergTable extends com.linkedin.coral.catalog.iceberg.IcebergTable {

  /**
   * Creates a new IcebergTable wrapping the given Iceberg table.
   *
   * @param table Iceberg Table object (must not be null)
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergTable#IcebergTable(Table)} instead.
   */
  @Deprecated
  public IcebergTable(Table table) {
    super(table);
  }
}
