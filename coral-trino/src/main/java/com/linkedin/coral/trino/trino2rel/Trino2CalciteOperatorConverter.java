/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Locale;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;


/**
 * Rewrites the SqlNode tree to replace Trino SQL operators with Calcite Operators to obtain a Calcite-compatible plan.
 */
public class Trino2CalciteOperatorConverter extends SqlShuttle {
  public Trino2CalciteOperatorConverter() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    final String operatorName = call.getOperator().getName();

    final TrinoCalciteOperatorTransformer transformer =
        TrinoCalciteTransformerMap.getOperatorTransformer(operatorName.toLowerCase(Locale.ROOT), call.operandCount());

    if (transformer == null) {
      return super.visit(call);
    }

    return super.visit((SqlCall) transformer.transformCall(call.getOperandList()));
  }
}
