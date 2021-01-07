/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;

import org.apache.calcite.rel.core.TableScan;

import com.linkedin.coral.pig.rel2pig.PigLoadFunction;
import com.linkedin.coral.pig.rel2pig.TableToPigPathFunction;


/**
 * PigTableScan translates a Calcite TableScan into Pig Latin.
 */
public class PigTableScan {

  private static final String TABLE_SCAN_TEMPLATE = "%s = LOAD '%s' USING %s;";

  private PigTableScan() {

  }

  /**
   * Translates a Calcite LogicalProject into Pig Latin
   * @param tableScan The Calcite TableScan to be translated
   * @param outputRelation The variable that stores the loaded table output
   * @param pigLoadFunction The function that determines what LoadFunc to use for a given table in Pig Latin
   * @param tableToPigPathFunction The function that determines the Pig path for a given table in the Pig Latin
   * @return The Pig Latin for the tableScan in the form of:
   *           [outputRelation] = LOAD [tableToPigPathFunction.getPigPath(database, table)]
   *               USING [pigLoadFunction.getLoadFunction(database, table)] ;
   */
  public static String getScript(TableScan tableScan, String outputRelation, PigLoadFunction pigLoadFunction,
      TableToPigPathFunction tableToPigPathFunction) {
    List<String> qualifiedNames = tableScan.getTable().getQualifiedName();
    String database = qualifiedNames.get(1);
    String table = qualifiedNames.get(2);

    String loadFunction = pigLoadFunction.getLoadFunction(database, table);
    String pigPath = tableToPigPathFunction.getPigPath(database, table);

    return String.format(TABLE_SCAN_TEMPLATE, outputRelation, pigPath, loadFunction);
  }
}
