/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.util.Collection;


/**
 * Public interface to access available hive UDFs
 */
public interface HiveFunctionRegistry {
  /**
   * Returns a list of functions matching given name This returns empty list if the
   * function name is not found
   *
   * @param functionName function name to match
   * @param isCaseSensitive whether to perform case-sensitive match for function name
   * @return collection of HiveFunctions with given function name
   * or empty list if there is no match
   */
  Collection<HiveFunction> lookup(String functionName, boolean isCaseSensitive);
}
