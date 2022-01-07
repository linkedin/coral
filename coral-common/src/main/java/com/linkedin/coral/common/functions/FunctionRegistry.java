/**
 * Copyright 2018-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.functions;

import java.util.Collection;


/**
 * Public interface to access available hive UDFs
 */
public interface FunctionRegistry {
  /**
   * Returns a list of functions matching given name case-insensitively. This returns empty list if the
   * function name is not found
   *
   * @param functionName function name to match
   * @return collection of HiveFunctions with given function name
   * or empty list if there is no match
   */
  Collection<Function> lookup(String functionName);
}
