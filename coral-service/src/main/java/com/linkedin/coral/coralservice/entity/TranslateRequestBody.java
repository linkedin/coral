/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

public class TranslateRequestBody {
  private String fromLanguage;
  private String toLanguage;
  private String query;

  public String getFromLanguage() {
    return fromLanguage;
  }

  public String getToLanguage() {
    return toLanguage;
  }

  public String getQuery() {
    return query;
  }
}
