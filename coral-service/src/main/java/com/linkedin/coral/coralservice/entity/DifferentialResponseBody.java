/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.ArrayList;
import java.util.List;


public class DifferentialResponseBody {
  private String modQuery;
  private List<String> modTblNames;

  public DifferentialResponseBody() {
    modQuery = "";
    modTblNames = new ArrayList<>();
  }

  public String getModQuery() {
    return modQuery;
  }

  public void setModQuery(String modQuery) {
    this.modQuery = modQuery;
  }

  public List<String> getModTblNames() {
    return modTblNames;
  }

  public void setModTblNames(List<String> modTblNames) {
    this.modTblNames = modTblNames;
  }

  public void addModTblName(String modTblName) {
    modTblNames.add(modTblName);
  }
}
