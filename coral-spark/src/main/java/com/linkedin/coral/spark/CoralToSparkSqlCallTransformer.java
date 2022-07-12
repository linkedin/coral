/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlShuttle;


/**
 * CoralToSparkSqlCallTransformer rewrites the Coral SqlNode AST to replace Coral IR SqlCalls
 * with Spark compatible SqlCalls to obtain a Spark compatible SqlNode AST.
 *
 * It does so by visiting the Coral SqlNode AST in a pre-order traversal manner and
 * returning transformed SqlCalls wherever a transformation is required.
 */
public class CoralToSparkSqlCallTransformer extends SqlShuttle {
  public CoralToSparkSqlCallTransformer() {
  }

  @Override
  public SqlNode visit(final SqlCall call) {
    SqlCall transformedSqlCall = CoralToSparkSqlCallTransformationUtils.getTransformedSqlCall(call);
    return super.visit(transformedSqlCall);
  }
}
