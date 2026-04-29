/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import com.linkedin.coral.common.catalog.IcebergTable;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergCalciteTableAdapter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class IcebergCalciteTableAdapter extends com.linkedin.coral.catalog.iceberg.IcebergCalciteTableAdapter {

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergCalciteTableAdapter#IcebergCalciteTableAdapter(com.linkedin.coral.catalog.iceberg.IcebergTable)} instead.
   */
  @Deprecated
  public IcebergCalciteTableAdapter(IcebergTable coralTable) {
    super(coralTable);
  }
}
