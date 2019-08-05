package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;


public class PrestoSqlDialect extends SqlDialect {

  public static final PrestoSqlDialect INSTANCE = new PrestoSqlDialect(
      emptyContext()
          .withDatabaseProduct(DatabaseProduct.UNKNOWN)
          .withDatabaseProductName("Presto")
          .withIdentifierQuoteString("\"")
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

  public boolean requireCastOnString() {
    return true;
  }
}
