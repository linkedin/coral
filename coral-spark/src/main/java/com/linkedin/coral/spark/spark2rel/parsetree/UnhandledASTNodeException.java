/**
 * Copyright 2023-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.spark2rel.parsetree;

import org.antlr.v4.runtime.ParserRuleContext;


public class UnhandledASTNodeException extends RuntimeException {
  public UnhandledASTNodeException(ParserRuleContext context, String message) {
    super(String.format(message, context.getClass().getSimpleName(), getLine(context), getColumn(context)));
  }

  private static int getLine(ParserRuleContext context) {
    return context.start.getLine();
  }

  private static int getColumn(ParserRuleContext context) {
    return context.start.getStartIndex();
  }
}
