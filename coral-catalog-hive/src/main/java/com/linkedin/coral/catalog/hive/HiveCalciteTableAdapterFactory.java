/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.hive;

import java.util.List;

import org.apache.calcite.schema.Table;

import com.linkedin.coral.common.catalog.CoralCalciteTableAdapterFactory;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.TableType;


/**
 * SPI implementation of {@link CoralCalciteTableAdapterFactory} for Hive tables.
 *
 * <p>This factory creates the appropriate Calcite table adapter based on whether
 * the Hive table is a physical table or a view:
 * <ul>
 *   <li>Views → {@link HiveCalciteViewAdapter}</li>
 *   <li>Tables → {@link HiveCalciteTableAdapter}</li>
 * </ul>
 */
public class HiveCalciteTableAdapterFactory implements CoralCalciteTableAdapterFactory {

  @Override
  public boolean supports(CoralTable coralTable) {
    return coralTable instanceof HiveTable;
  }

  @Override
  public Table createAdapter(CoralTable coralTable, List<String> schemaPath) {
    HiveTable hiveTable = (HiveTable) coralTable;
    if (hiveTable.tableType() == TableType.VIEW) {
      return new HiveCalciteViewAdapter(hiveTable, schemaPath);
    } else {
      return new HiveCalciteTableAdapter(hiveTable);
    }
  }
}
