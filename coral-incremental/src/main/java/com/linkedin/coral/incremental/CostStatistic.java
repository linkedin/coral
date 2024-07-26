/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

public enum CostStatistic {
  COST("cost"),
  ROW_COUNT("rowCount");

  private final String statistic;

  CostStatistic(String statistic) {
    this.statistic = statistic;
  }

  @Override
  public String toString() {
    return statistic;
  }
}
