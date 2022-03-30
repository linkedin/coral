/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;


/**
 * Trino Keywords Converter maintains the Trino Keywords and related utility functions
 */
public final class TrinoKeywordsConverter {

  private TrinoKeywordsConverter() {

  }

  private static final Set<String> RESERVED_KEYWORDS = ImmutableSet.of("ALTER", "AND", "AS", "BETWEEN", "BY", "CASE",
      "CAST", "CONSTRAINT", "CREATE", "CROSS", "CUBE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
      "CURRENT_USER", "DEALLOCATE", "DELETE", "DESCRIBE", "DISTINCT", "DROP", "ELSE", "END", "ESCAPE", "EXCEPT",
      "EXECUTE", "EXISTS", "EXTRACT", "FALSE", "FOR", "FROM", "FULL", "GROUP", "GROUPING", "HAVING", "IN", "INNER",
      "INSERT", "INTERSECT", "INTO", "IS", "JOIN", "LEFT", "LIKE", "LOCALTIME", "LOCALTIMESTAMP", "NATURAL",
      "NORMALIZE", "NOT", "NULL", "ON", "OR", "ORDER", "OUTER", "PREPARE", "RECURSIVE", "RIGHT", "ROLLUP", "SELECT",
      "TABLE", "THEN", "TRUE", "UESCAPE", "UNION", "UNNEST", "USING", "VALUES", "WHEN", "WHERE", "WITH");

  /**
   * Quote the value iif it is a reserved Trino keyword
   * Note: This method is not used in Coral anymore, but we don't remove it since our internal Trino is using it
   *
   * @param value input value
   * @return if the value is a reserved keyword, a double quote will be added as a return value
   */
  public static String quoteReservedKeyword(String value) {
    if (RESERVED_KEYWORDS.contains(value.toUpperCase())) {
      return quoteWordIfNotQuoted(value);
    } else {
      return value;
    }
  }

  /**
   * Add quote if the value is not quoted
   */
  public static String quoteWordIfNotQuoted(String value) {
    return value.startsWith("\"") && value.endsWith("\"") ? value : ("\"" + value + "\"");
  }
}
