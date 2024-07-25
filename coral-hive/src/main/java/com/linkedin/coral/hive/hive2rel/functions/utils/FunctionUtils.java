/**
 * Copyright 2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions.utils;

public class FunctionUtils {
  public static final String CORAL_VERSIONED_UDF_PREFIX = "coral_udf_version_(\\d+|x)_(\\d+|x)_(\\d+|x)";

  /**
   * Checks if the given class name has a versioning prefix.
   * A class name is considered versioned if the prefix before the first dot
   * follows {@link FunctionUtils#CORAL_VERSIONED_UDF_PREFIX} format
   */
  public static boolean isVersioningUDF(String className) {
    if (className != null && !className.isEmpty()) {
      int firstDotIndex = className.indexOf('.');
      if (firstDotIndex != -1) {
        String prefix = className.substring(0, firstDotIndex);
        return prefix.matches(CORAL_VERSIONED_UDF_PREFIX);
      }
    }
    return false;
  }

  /**
   * Removes the versioning prefix from a given UDF class name if it is present.
   * A class name is considered versioned if the prefix before the first dot
   * follows {@link FunctionUtils#CORAL_VERSIONED_UDF_PREFIX} format
   */
  public static String removeVersioningPrefix(String className) {
    if (isVersioningUDF(className)) {
      return className.substring(className.indexOf('.') + 1);
    } else {
      return className;
    }
  }
}
