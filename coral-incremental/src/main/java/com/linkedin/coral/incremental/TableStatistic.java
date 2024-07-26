/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

import java.util.Map;


public class TableStatistic {
  // The number of rows in the table
  Double rowCount;
  // The number of distinct values in each column
  Map<String, Double> distinctCountByRow;
}
