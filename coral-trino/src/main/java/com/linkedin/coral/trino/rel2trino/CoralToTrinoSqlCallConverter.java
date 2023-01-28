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

import com.linkedin.coral.trino.rel2trino.utils.CoralToTrinoSqlCallTransformersUtil;


/**
 * This class extends the class of SqlShuttle and calls CalciteTrinoUDFOperatorTransformerUtil to get a list of SqlCallTransformers
 * to traverse the hierarchy and converts UDF operator in all SqlCalls if it is required
 */
public class CoralToTrinoSqlCallConverter extends SqlShuttle {
  private final Map<String, Boolean> configs;
  public CoralToTrinoSqlCallConverter(Map<String, Boolean> configs) {
    this.configs = configs;
  }

  @Override
  public SqlNode visit(SqlCall call) {
    SqlCall transformedCall = CoralToTrinoSqlCallTransformersUtil.getTransformers(configs).apply(call);
    return super.visit(transformedCall);
  }
}
