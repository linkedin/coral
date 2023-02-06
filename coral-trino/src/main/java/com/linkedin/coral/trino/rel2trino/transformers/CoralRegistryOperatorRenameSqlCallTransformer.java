/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import com.linkedin.coral.common.transformers.OperatorRenameSqlCallTransformer;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;


/**
 * This is a subclass of {@link OperatorRenameSqlCallTransformer} which transforms a Coral operator to a Trino operator
 * by renaming the operator in the Coral registry
 */
public class CoralRegistryOperatorRenameSqlCallTransformer extends OperatorRenameSqlCallTransformer {
  private static final StaticHiveFunctionRegistry HIVE_FUNCTION_REGISTRY = new StaticHiveFunctionRegistry();

  public CoralRegistryOperatorRenameSqlCallTransformer(String sourceOpName, int numOperands, String targetOpName) {
    super(HIVE_FUNCTION_REGISTRY.lookup(sourceOpName).iterator().next().getSqlOperator(), numOperands, targetOpName);
  }
}
