/**
 * Copyright 2022-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.ArrayList;
import java.util.List;


public class PIIPropagationResponseBody {

  private List<String> inputPIIFields;
  private List<String> outputPIIFields;

  public PIIPropagationResponseBody() {
    inputPIIFields = new ArrayList<>();
    outputPIIFields = new ArrayList<>();
  }

  public void setInputPIIFields(List<String> inputPIIFields) {
    this.inputPIIFields = inputPIIFields;
  }

  public void setOutputPIIFields(List<String> outputPIIFields) {
    this.outputPIIFields = outputPIIFields;
  }

  public List<String> getInputPIIFields() {
    return inputPIIFields;
  }

  public List<String> getOutputPIIFields() {
    return outputPIIFields;
  }
}
