/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.Collection;


/**
 * Class to resolve function names in SQL to Function.
 */
public abstract class FunctionResolver {

  protected final FunctionRegistry registry;

  protected FunctionResolver(FunctionRegistry registry) {
    this.registry = registry;
  }

  /**
   * Resolves function to concrete operator case-insensitively.
   * @param functionName function name to resolve
   * @return list of matching Functions or empty list if there is no match
   */
  public abstract Collection<Function> resolve(String functionName);
}
