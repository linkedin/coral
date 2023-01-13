/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel;

import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlDelegatingConformance;


public class SparkSqlConformance extends SqlDelegatingConformance {

  public static final SqlConformance SPARK_SQL = new SparkSqlConformance();

  private SparkSqlConformance() {
    super(SqlConformanceEnum.PRAGMATIC_2003);
  }
}
