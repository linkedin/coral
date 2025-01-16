/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

public class WhereClauseResponseBody {
  private String whereClauseQuery;
  public WhereClauseResponseBody() {
    whereClauseQuery = "";
  }

  public String getQuery() {
    return whereClauseQuery;
  }

  public void setQuery(String query) {
    this.whereClauseQuery = query;
  }
}
