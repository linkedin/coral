/**
 * Copyright 2018-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

public class Utils {
  private Utils() {
  }

  /**
   * Removes quotes (single or double) if the input is quoted
   * @param id input string
   * @return input with quotes removed
   */
  public static String stripQuotes(String id) {
    if ((id.startsWith("'") && id.endsWith("'")) || (id.startsWith("\"") && id.endsWith("\""))) {
      return id.substring(1, id.length() - 1);
    }
    return id;
  }
}
