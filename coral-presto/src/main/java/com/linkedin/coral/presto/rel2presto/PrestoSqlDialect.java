package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;


public class PrestoSqlDialect extends SqlDialect {
  private static final String IDENTIFIER_QUOTE_STRING = "\"";

  public static final PrestoSqlDialect INSTANCE = new PrestoSqlDialect(
      emptyContext()
          .withDatabaseProduct(DatabaseProduct.UNKNOWN)
          .withDatabaseProductName("Presto")
          .withIdentifierQuoteString(IDENTIFIER_QUOTE_STRING)
          .withNullCollation(NullCollation.LAST));

  private PrestoSqlDialect(Context context) {
    super(context);
  }

  @Override
  public boolean supportsCharSet() {
    return false;
  }

  public void unparseOffsetFetch(SqlWriter writer, SqlNode offset,
      SqlNode fetch) {
    unparseFetchUsingLimit(writer, offset, fetch);
  }

  @Override
  public String quoteIdentifier(String name) {
    // Assume that quote string is not allowed in Presto SQL identifiers
    if (name.contains(IDENTIFIER_QUOTE_STRING)) {
      // This mean the identifiers within the name were quoted before.
      return name;
    }
    return IDENTIFIER_QUOTE_STRING + name + IDENTIFIER_QUOTE_STRING;
  }

  public boolean requireCastOnString() {
    return true;
  }
}
