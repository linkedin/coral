/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools;

import com.facebook.presto.sql.parser.ParsingOptions;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.presto.rel2presto.HiveToPrestoConverter;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.*;


/**
 * Validates that a DaliView can be translated to Presto.
 */
public class PrestoValidator implements LanguageValidator {

  @Override
  public String getCamelName() {
    return "prestoSql";
  }

  @Override
  public String getStandardName() {
    return "PrestoSQL";
  }

  @Override
  public void convertAndValidate(String db, String table, HiveMetastoreClient hiveMetastoreClient,
      PrintWriter outputWriter) {
    final HiveToPrestoConverter converter = HiveToPrestoConverter.create(hiveMetastoreClient);
    final String prestoSql = toPrestoSql(db, table, converter);
    validatePrestoSql(prestoSql);
    if (outputWriter != null) {
      outputWriter.println(db + "." + table + ":");
      outputWriter.println(prestoSql);
      outputWriter.flush();
    }
  }

  private String toPrestoSql(String db, String table, HiveToPrestoConverter converter) {
    checkNotNull(table);
    return converter.toPrestoSql(db, table);
  }

  private void validatePrestoSql(String sql) {
    final SqlParser parser = new SqlParser();
    final Statement statement = parser.createStatement(sql, new ParsingOptions());
    if (statement == null) {
      throw new RuntimeException("Failed to parse presto sql: " + sql);
    }
  }

}
