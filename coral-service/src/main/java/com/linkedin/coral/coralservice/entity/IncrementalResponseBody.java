/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.ArrayList;
import java.util.List;


public class IncrementalResponseBody {
  private String incrementalQuery;
  private List<String> incrementalTableNames;

  public IncrementalResponseBody() {
    incrementalQuery = "";
    incrementalTableNames = new ArrayList<>();
  }

  public String getIncrementalQuery() {
    return incrementalQuery;
  }

  public void setIncrementalQuery(String incrementalQuery) {
    this.incrementalQuery = incrementalQuery;
  }

  public List<String> getIncrementalTableNames() {
    return incrementalTableNames;
  }

  public void setIncrementalTableNames(List<String> incrementalTableNames) {
    this.incrementalTableNames = incrementalTableNames;
  }

  public void addIncrementalTableName(String incrementalTableName) {
    incrementalTableNames.add(incrementalTableName);
  }
}
