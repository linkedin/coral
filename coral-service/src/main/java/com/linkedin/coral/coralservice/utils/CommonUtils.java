/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

public class CommonUtils {

  public static boolean isValidSourceLanguage(String sourceLanguage) {
    return sourceLanguage.equalsIgnoreCase("trino") || sourceLanguage.equalsIgnoreCase("hive")
        || sourceLanguage.equalsIgnoreCase("spark");
  }
}
