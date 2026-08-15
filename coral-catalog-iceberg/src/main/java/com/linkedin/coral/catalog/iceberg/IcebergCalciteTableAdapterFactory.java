/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.iceberg;

import java.util.List;

import org.apache.calcite.schema.Table;

import com.linkedin.coral.common.catalog.CoralCalciteTableAdapterFactory;
import com.linkedin.coral.common.catalog.CoralTable;


/**
 * SPI implementation of {@link CoralCalciteTableAdapterFactory} for Iceberg tables.
 *
 * <p>This factory creates {@link IcebergCalciteTableAdapter} for Iceberg tables.
 */
public class IcebergCalciteTableAdapterFactory implements CoralCalciteTableAdapterFactory {

  @Override
  public boolean supports(CoralTable coralTable) {
    return coralTable instanceof IcebergTable;
  }

  @Override
  public Table createAdapter(CoralTable coralTable, List<String> schemaPath) {
    return new IcebergCalciteTableAdapter((IcebergTable) coralTable);
  }
}
