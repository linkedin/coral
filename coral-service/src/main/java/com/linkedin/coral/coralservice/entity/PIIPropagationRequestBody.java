/**
 * Copyright 2022-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.List;


public class PIIPropagationRequestBody {

  private String query;
  private List<String> inputPIIFields;
  private String language;

  public String getQuery() {
    return query;
  }

  public List<String> getInputPIIFields() {
    return inputPIIFields;
  }

  public String getLanguage() {
    return language;
  }

}
