/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlDelegatingConformance;


public class TrinoSqlConformance extends SqlDelegatingConformance {

  public static final SqlConformance TRINO_SQL = new TrinoSqlConformance();

  private TrinoSqlConformance() {
    super(SqlConformanceEnum.PRAGMATIC_2003);
  }
}
