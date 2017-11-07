package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlDialect;


public class PrestoSqlDialect extends SqlDialect {

  public PrestoSqlDialect() {
    super(DatabaseProduct.UNKNOWN, "Presto", "\"", NullCollation.LAST);
  }

}
