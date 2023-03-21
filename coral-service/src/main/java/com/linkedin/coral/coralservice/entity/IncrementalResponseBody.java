/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.ArrayList;
import java.util.List;


public class IncrementalResponseBody {
  private String modifiedQuery;
  private List<String> modifiedTableNames;

  public IncrementalResponseBody() {
    modifiedQuery = "";
    modifiedTableNames = new ArrayList<>();
  }

  public String getModifiedQuery() {
    return modifiedQuery;
  }

  public void setModifiedQuery(String modifiedQuery) {
    this.modifiedQuery = modifiedQuery;
  }

  public List<String> getModifiedTableNames() {
    return modifiedTableNames;
  }

  public void setModifiedTableNames(List<String> modifiedTableNames) {
    this.modifiedTableNames = modifiedTableNames;
  }

  public void addModifiedTableName(String modifiedTableName) {
    modifiedTableNames.add(modifiedTableName);
  }
}
