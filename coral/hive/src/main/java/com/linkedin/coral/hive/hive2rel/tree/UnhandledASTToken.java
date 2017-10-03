package com.linkedin.coral.hive.hive2rel.tree;

import org.apache.hadoop.hive.ql.parse.ASTNode;


public class UnhandledASTToken extends RuntimeException {

  private final ASTNode node;

  public UnhandledASTToken(ASTNode node) {
    super(String.format("Unhandled Hive AST token %s, tree: %s", node.getText(), node.dump()));
    this.node = node;
  }
}
