/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;


public class TrinoSqlDialect extends SqlDialect {
  private static final String IDENTIFIER_QUOTE_STRING = "\"";

  public static final TrinoSqlDialect INSTANCE =
      new TrinoSqlDialect(emptyContext().withDatabaseProduct(DatabaseProduct.UNKNOWN).withDatabaseProductName("Trino")
          .withIdentifierQuoteString(IDENTIFIER_QUOTE_STRING).withNullCollation(NullCollation.LAST));

  private TrinoSqlDialect(Context context) {
    super(context);
  }

  @Override
  public boolean supportsCharSet() {
    return false;
  }

  public void unparseOffsetFetch(SqlWriter writer, SqlNode offset, SqlNode fetch) {
    unparseFetchUsingLimit(writer, offset, fetch);
  }

  @Override
  public String quoteIdentifier(String name) {
    // Assume that quote string is not allowed in Trino SQL identifiers
    if (name.contains(IDENTIFIER_QUOTE_STRING)) {
      // This mean the identifiers within the name were quoted before.
      return name;
    }
    return IDENTIFIER_QUOTE_STRING + name + IDENTIFIER_QUOTE_STRING;
  }

  @Override
  public void unparseIdentifier(SqlWriter writer, SqlIdentifier identifier) {
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.IDENTIFIER);
    for (int i = 0; i < identifier.names.size(); i++) {
      writer.sep(".");
      final String name = identifier.names.get(i);
      final SqlParserPos pos = identifier.getComponentParserPosition(i);
      if (name.equals("")) {
        writer.print("*");
      } else {
        writer.identifier(name, pos.isQuoted());
      }
    }
    writer.endList(frame);
  }

  public boolean requireCastOnString() {
    return true;
  }
}
