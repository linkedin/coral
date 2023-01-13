/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel.parsetree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.spark.sql.catalyst.parser.AstBuilder;
import org.apache.spark.sql.catalyst.parser.SqlBaseParser;

import scala.Function1;


public class SparkSqlAstBuilder extends AstBuilder implements Function1<SqlBaseParser, ParserRuleContext> {
  @Override
  public ParserRuleContext apply(SqlBaseParser v1) {
    return v1.singleStatement();
  }
}
