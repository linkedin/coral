/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;


public class UnhandledASTTokenException extends RuntimeException {

  private final ASTNode node;

  public UnhandledASTTokenException(ASTNode node) {
    super(String.format("Unhandled Hive AST token %d %s, tree: %s", node.getType(), node.getText(), node.dump()));
    this.node = node;
  }

  public ASTNode getNode() {
    return node;
  }
}
