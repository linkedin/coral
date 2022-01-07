/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.functions;

import java.util.Map;

import com.google.common.collect.ImmutableMap;


/**
 * Trino Keywords Converter maintains the Trino Keywords and related utility functions
 */
public final class TrinoKeywordsConverter {

  private TrinoKeywordsConverter() {

  }

  /**
   * Map of Trino Reserved Keywords with the mark to distinguish if the word is reserved SQL:2016 keyword ONLY
   */
  public static final Map<String, Boolean> RESERVED_KEYWORDS = ImmutableMap.<String, Boolean> builder()
      .put("ALTER", Boolean.FALSE).put("AND", Boolean.FALSE).put("AS", Boolean.FALSE).put("BETWEEN", Boolean.FALSE)
      .put("BY", Boolean.FALSE).put("CASE", Boolean.FALSE).put("CAST", Boolean.FALSE).put("CONSTRAINT", Boolean.FALSE)
      .put("CREATE", Boolean.FALSE).put("CROSS", Boolean.FALSE).put("CUBE", Boolean.TRUE)
      .put("CURRENT_DATE", Boolean.FALSE).put("CURRENT_TIME", Boolean.FALSE).put("CURRENT_TIMESTAMP", Boolean.FALSE)
      .put("CURRENT_USER", Boolean.TRUE).put("DEALLOCATE", Boolean.FALSE).put("DELETE", Boolean.FALSE)
      .put("DESCRIBE", Boolean.FALSE).put("DISTINCT", Boolean.FALSE).put("DROP", Boolean.FALSE)
      .put("ELSE", Boolean.FALSE).put("END", Boolean.FALSE).put("ESCAPE", Boolean.FALSE).put("EXCEPT", Boolean.FALSE)
      .put("EXECUTE", Boolean.FALSE).put("EXISTS", Boolean.FALSE).put("EXTRACT", Boolean.FALSE)
      .put("FALSE", Boolean.FALSE).put("FOR", Boolean.FALSE).put("FROM", Boolean.FALSE).put("FULL", Boolean.FALSE)
      .put("GROUP", Boolean.FALSE).put("GROUPING", Boolean.TRUE).put("HAVING", Boolean.FALSE).put("IN", Boolean.FALSE)
      .put("INNER", Boolean.FALSE).put("INSERT", Boolean.FALSE).put("INTERSECT", Boolean.FALSE)
      .put("INTO", Boolean.FALSE).put("IS", Boolean.FALSE).put("JOIN", Boolean.FALSE).put("LEFT", Boolean.FALSE)
      .put("LIKE", Boolean.FALSE).put("LOCALTIME", Boolean.TRUE).put("LOCALTIMESTAMP", Boolean.TRUE)
      .put("NATURAL", Boolean.FALSE).put("NORMALIZE", Boolean.TRUE).put("NOT", Boolean.FALSE).put("NULL", Boolean.FALSE)
      .put("ON", Boolean.FALSE).put("OR", Boolean.FALSE).put("ORDER", Boolean.FALSE).put("OUTER", Boolean.FALSE)
      .put("PREPARE", Boolean.FALSE).put("RECURSIVE", Boolean.TRUE).put("RIGHT", Boolean.FALSE)
      .put("ROLLUP", Boolean.TRUE).put("SELECT", Boolean.FALSE).put("TABLE", Boolean.FALSE).put("THEN", Boolean.FALSE)
      .put("TRUE", Boolean.FALSE).put("UESCAPE", Boolean.TRUE).put("UNION", Boolean.FALSE).put("UNNEST", Boolean.TRUE)
      .put("USING", Boolean.FALSE).put("VALUES", Boolean.FALSE).put("WHEN", Boolean.FALSE).put("WHERE", Boolean.FALSE)
      .put("WITH", Boolean.FALSE).build();

  /**
   * Quote the value iif it is a reserved Trino keyword
   * Note: This method is not used in Coral anymore, but we don't remove it since our internal Trino is using it
   *
   * @param value input value
   * @return if the value is a reserved keyword, a double quote will be added as a return value
   */
  public static String quoteReservedKeyword(String value) {
    if (RESERVED_KEYWORDS.containsKey(value.toUpperCase())) {
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
