package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Window;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RelToPrestoConverter extends RelToSqlConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(RelToPrestoConverter.class);

  public static final PrestoSqlDialect PRESTO_DIALECT = new PrestoSqlDialect();

  /**
   * Creates a RelToSqlConverter.
   */
  public RelToPrestoConverter() {
    super(PRESTO_DIALECT);
  }

  /**
   * Convert relational algebra to Presto SQL
   * @param relNode calcite relational algebra representation of SQL
   * @return SQL string
   */
  public String convert(RelNode relNode) {
    return convertToSqlNode(relNode)
        .toSqlString(PRESTO_DIALECT)
        .toString();
  }

  /**
   * Convert input relational algebra to calcite SqlNode
   * @param relNode relation algebra
   * @return calcite SqlNode representation for input
   */
  public SqlNode convertToSqlNode(RelNode relNode) {
    return visitChild(0, relNode)
        .asStatement();
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
