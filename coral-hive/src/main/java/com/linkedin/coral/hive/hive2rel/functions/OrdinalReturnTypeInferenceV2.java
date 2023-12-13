/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.sql.type.OrdinalReturnTypeInference;


/**
 * Custom implementation of {@link OrdinalReturnTypeInference} which allows inferring the return type
 * based on the ordinal of a given input argument and also exposes the ordinal.
 */
public class OrdinalReturnTypeInferenceV2 extends OrdinalReturnTypeInference {
  private final int ordinal;

  public OrdinalReturnTypeInferenceV2(int ordinal) {
    super(ordinal);
    this.ordinal = ordinal;
  }

  public int getOrdinal() {
    return ordinal;
  }
}
