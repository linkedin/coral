package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Window;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RelToPrestoConverter extends RelToSqlConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(RelToPrestoConverter.class);

  public static final PrestoSqlDialect PRESTO_DIALECT = new PrestoSqlDialect();

  /** Creates a RelToSqlConverter.
   */
  public RelToPrestoConverter() {
    super(PRESTO_DIALECT);
  }

  /**
   * @see #dispatch(RelNode)
   * @param window Relnode representing window clause
   * @return result of translation to sql
   */
  public Result visit(Window window) {
    return null;
  }
}
