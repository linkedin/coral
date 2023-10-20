/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.linkedin.coral.common.functions.Function;
import com.linkedin.coral.common.functions.FunctionRegistry;


public class CompositeFunctionRegistry implements FunctionRegistry {
  private final List<FunctionRegistry> functionRegistryList;

  public CompositeFunctionRegistry(List<FunctionRegistry> functionRegistryList) {
    this.functionRegistryList = functionRegistryList;
  }

  @Override
  public Collection<Function> lookup(String functionName) {
    for (FunctionRegistry functionRegistry : functionRegistryList) {
      Collection<Function> functions = functionRegistry.lookup(functionName);
      if (functions != null && !functions.isEmpty()) {
        return functions;
      }
    }
    return ImmutableList.of();
  }
}
