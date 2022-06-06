/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel;

import java.util.Collection;

import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;
import com.linkedin.coral.common.functions.FunctionResolver;


/**
 * Class to resolve Trino function names in SQL to Function.
 */
public class TrinoFunctionResolver extends FunctionResolver {
  protected TrinoFunctionResolver(FunctionRegistry registry) {
    super(registry);
  }

  /**
   * Resolves function to concrete operator case-insensitively.
   * @param functionName function name to resolve
   * @return list of matching Functions or empty list if there is no match
   */
  @Override
  public Collection<Function> resolve(String functionName) {
    return registry.lookup(functionName);
  }
}
