/**
 * Copyright 2023-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

public class WhereClauseRequestBody {
  private String query;
  private String tableName;
  private String columnName;
  private String columnValue;

  public String getQuery() {
    return query;
  }

  public String getTableName() {
    return tableName;
  }

  public String getColumnName() {
    return columnName;
  }

  public String getColumnValue() {
    return columnValue;
  }
}
