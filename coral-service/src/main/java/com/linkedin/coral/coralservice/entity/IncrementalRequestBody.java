/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.List;


public class IncrementalRequestBody {
  private String query;
  private List<String> tableNames;
  private String language;

  public String getQuery() {
    return query;
  }

  public List<String> getTableNames() {
    return tableNames;
  }

  public String getLanguage() {
    return language;
  }
}
