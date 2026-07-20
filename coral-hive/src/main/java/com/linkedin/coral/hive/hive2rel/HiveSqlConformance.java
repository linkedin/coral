/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import org.apache.calcite.sql.validate.SqlConformance;


/**
 * @deprecated Use {@link CoralSqlConformance} instead.
 */
@Deprecated
public class HiveSqlConformance extends CoralSqlConformance {

  public static final SqlConformance HIVE_SQL = CoralSqlConformance.CORAL_SQL;

  private HiveSqlConformance() {
    super();
  }
}
