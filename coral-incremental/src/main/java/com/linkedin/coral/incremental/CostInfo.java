/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.incremental;

public class CostInfo {
  // TODO: we may also need to add TableName field.
  Double cost;
  Double row;

  public CostInfo(Double cost, Double row) {
    this.cost = cost;
    this.row = row;
  }
}
