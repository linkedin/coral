package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.sql.SqlDialect;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class PrestoSqlDialect extends SqlDialect {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrestoSqlDialect.class);

  public PrestoSqlDialect() {
    super(DatabaseProduct.UNKNOWN, "Presto", "\"", NullCollation.LAST);
  }

}
