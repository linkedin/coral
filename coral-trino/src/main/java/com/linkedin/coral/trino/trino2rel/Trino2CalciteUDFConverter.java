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


public class Trino2CalciteUDFConverter {
  private Trino2CalciteUDFConverter() {
  }

  /**
   * Replaces Calcite SQL operators with Trino UDF to obtain the Trino-compatible Calcite plan.
   *
   * @param sqlNode Original Trino node
   * @return Calcite-compatible node
   */
  public static SqlNode convertSqlNode(SqlNode sqlNode) {
    SqlShuttle converter = new SqlShuttle() {

      @Override
      public SqlNode visit(final SqlCall call) {
        final String operatorName = call.getOperator().getName();

        final TrinoCalciteUDFTransformer transformer =
            TrinoCalciteUDFMap.getUDFTransformer(operatorName.toLowerCase(Locale.ROOT), call.operandCount());

        if (transformer == null) {
          return super.visit(call);
        }

        return super.visit((SqlCall) transformer.transformCall(call.getOperandList()));
      }

    };

    return sqlNode.accept(converter);
  }

}
