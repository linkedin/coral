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
  private List<String> modifiedTblNames;

  public IncrementalResponseBody() {
    modifiedQuery = "";
    modifiedTblNames = new ArrayList<>();
  }

  public String getModifiedQuery() {
    return modifiedQuery;
  }

  public void setModifiedQuery(String modifiedQuery) {
    this.modifiedQuery = modifiedQuery;
  }

  public List<String> getModifiedTblNames() {
    return modifiedTblNames;
  }

  public void setModifiedTblNames(List<String> modifiedTblNames) {
    this.modifiedTblNames = modifiedTblNames;
  }

  public void addModifiedTblName(String modifiedTblName) {
    modifiedTblNames.add(modifiedTblName);
  }
}
