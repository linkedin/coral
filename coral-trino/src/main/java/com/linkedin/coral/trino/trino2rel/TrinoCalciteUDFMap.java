/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.sql.type.ReturnTypes;

import com.linkedin.coral.trino.rel2trino.UDFTransformer;

import static com.linkedin.coral.trino.trino2rel.TrinoCalciteUDFMapUtils.*;
import static org.apache.calcite.sql.type.OperandTypes.*;


public class TrinoCalciteUDFMap {
  private TrinoCalciteUDFMap() {
  }

  private static final Map<String, TrinoCalciteUDFTransformer> TRANSFORMER_MAP = new HashMap<>();

  static {
    createUDFMapEntry(TRANSFORMER_MAP, createUDF("strpos", ReturnTypes.INTEGER, STRING_STRING), 2, "instr");
  }

  /**
   * Gets UDFTransformer for a given Trino SQL Operator.
   *
   * @param trinoOpName Name of Trino SQL operator
   * @param numOperands Number of operands
   * @return {@link UDFTransformer} object
   */
  public static TrinoCalciteUDFTransformer getUDFTransformer(String trinoOpName, int numOperands) {
    return TRANSFORMER_MAP.get(getKey(trinoOpName, numOperands));
  }

  /**
   * @return copy of internal transformer map
   */
  public static Map<String, TrinoCalciteUDFTransformer> getTransformerMap() {
    return TRANSFORMER_MAP;
  }

}
