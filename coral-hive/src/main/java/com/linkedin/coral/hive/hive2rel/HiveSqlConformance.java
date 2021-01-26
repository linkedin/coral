/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlDelegatingConformance;


public class HiveSqlConformance extends SqlDelegatingConformance {

  public static SqlConformance HIVE_SQL = new HiveSqlConformance();

  private HiveSqlConformance() {
    super(PRAGMATIC_2003);
  }

  @Override
  public boolean allowNiladicParentheses() {
    return true;
  }
}
