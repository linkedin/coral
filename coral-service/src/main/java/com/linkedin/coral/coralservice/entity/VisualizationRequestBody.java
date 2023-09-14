/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

public class VisualizationRequestBody {
  private String fromLanguage;
  private String query;

  private String rewriteType;

  public String getFromLanguage() {
    return fromLanguage;
  }

  public String getQuery() {
    return query;
  }

  public String getRewriteType() {
    return rewriteType;
  }

  public enum RewriteType {
    NONE("none"),
    INCREMENTAL("incremental"),
    DATAMASKING("datamasking");

    private final String type;

    RewriteType(String description) {
      this.type = description;
    }

    @Override
    public String toString() {
      return type;
    }
  }
}
