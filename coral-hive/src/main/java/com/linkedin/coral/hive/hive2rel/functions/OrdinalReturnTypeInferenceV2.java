package com.linkedin.coral.hive.hive2rel.functions;

import org.apache.calcite.sql.type.OrdinalReturnTypeInference;


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
