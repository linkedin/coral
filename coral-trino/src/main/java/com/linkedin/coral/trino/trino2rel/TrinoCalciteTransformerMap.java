/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.sql.type.ReturnTypes;

import static com.linkedin.coral.trino.trino2rel.TrinoCalciteTransformerMapUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;


public class TrinoCalciteTransformerMap {
  private TrinoCalciteTransformerMap() {
  }

  public static final Map<String, TrinoCalciteOperatorTransformer> TRANSFORMER_MAP = new HashMap<>();

  static {
    // TODO: keep adding Trino-Specific functions as needed

    createTransformerMapEntry(TRANSFORMER_MAP, createOperator("strpos", ReturnTypes.INTEGER, STRING_STRING), 2,
        "instr");
  }

  /**
   * Gets TrinoCalciteOperatorTransformer for a given Trino SQL Operator.
   *
   * @param trinoOpName Name of Trino SQL operator
   * @param numOperands Number of operands
   * @return {@link TrinoCalciteOperatorTransformer} object
   */
  public static TrinoCalciteOperatorTransformer getOperatorTransformer(String trinoOpName, int numOperands) {
    return TRANSFORMER_MAP.get(getKey(trinoOpName, numOperands));
  }
}
