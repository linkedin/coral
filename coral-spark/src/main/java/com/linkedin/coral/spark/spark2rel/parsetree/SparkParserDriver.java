/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel.parsetree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.calcite.sql.SqlNode;
import org.apache.spark.sql.catalyst.parser.SqlBaseParser;
import org.apache.spark.sql.execution.SparkSqlParser;


public class SparkParserDriver {
  private static final SparkSqlParser SPARK_SQL_PARSER = new SparkSqlParser();

  /**
   * Use the SparkSqlParser to parse the command and return the Calcite SqlNode.
   *
   * @param command Spark SQL
   * @return {@link SqlNode} as response
   */
  public static SqlNode parse(String command) {
    ParserRuleContext context = SPARK_SQL_PARSER.parse(command, new SparkSqlAstBuilder());
    SparkSqlAstVisitor visitor = new SparkSqlAstVisitor();
    return visitor.visitSingleStatement((SqlBaseParser.SingleStatementContext) context);
  }
}
