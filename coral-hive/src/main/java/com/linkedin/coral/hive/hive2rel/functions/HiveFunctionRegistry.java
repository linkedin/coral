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
   * @return collection of HiveFunctions with given function name
   * or empty list if there is no match
   */
  Collection<HiveFunction> lookup(String functionName);
}
