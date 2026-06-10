/**
 * Copyright 2021-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlDelegatingConformance;


public class CoralSqlConformance extends SqlDelegatingConformance {

  public static final SqlConformance CORAL_SQL = new CoralSqlConformance();

  /**
   * @deprecated Use {@link #CORAL_SQL} instead.
   */
  @Deprecated
  public static final SqlConformance HIVE_SQL = CORAL_SQL;

  protected CoralSqlConformance() {
    super(SqlConformanceEnum.PRAGMATIC_2003);
  }

  @Override
  public boolean allowNiladicParentheses() {
    return true;
  }

  @Override
  public boolean isSortByAlias() {
    return true;
  }

  @Override
  public boolean isHavingAlias() {
    return true;
  }
}
