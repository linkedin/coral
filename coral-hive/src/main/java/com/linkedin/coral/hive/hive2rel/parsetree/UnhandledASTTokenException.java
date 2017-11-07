package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;


public class UnhandledASTTokenException extends RuntimeException {

  private final ASTNode node;

  public UnhandledASTTokenException(ASTNode node) {
    super(String.format("Unhandled Hive AST token %s, tree: %s", node.getText(), node.dump()));
    this.node = node;
  }
}
