/**
 * Copyright 2017-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.util.Map;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;

import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;


public class TrinoSqlUDFConverter extends SqlShuttle {
  private static final String TO_DATE_OPERATOR_NAME = "to_date";
  private final Map<String, Boolean> configs;
  public TrinoSqlUDFConverter(Map<String, Boolean> configs) {
    this.configs = configs;
  }

  @Override
  public SqlNode visit(SqlCall call) {
    if (shouldTransformOperator(call.getOperator().getName())) {
      CalciteTrinoOperatorTransformers operatorTransformers = CalciteTrinoOperatorTransformers.getInstance();
      SqlCall transformedCall = operatorTransformers.apply(call);
      return super.visit(transformedCall);
    } else {
      return super.visit(call);
    }
  }

  private boolean shouldTransformOperator(String operatorName) {
    return !(TO_DATE_OPERATOR_NAME.equalsIgnoreCase(operatorName)
        && configs.getOrDefault(AVOID_TRANSFORM_TO_DATE_UDF, false));
  }
}
