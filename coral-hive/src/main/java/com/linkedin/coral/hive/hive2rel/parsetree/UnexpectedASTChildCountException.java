/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

import com.linkedin.coral.hive.hive2rel.parsetree.parser.ASTNode;


public class UnexpectedASTChildCountException extends RuntimeException {

  private final ASTNode node;

  public UnexpectedASTChildCountException(ASTNode node, int type, int expected, int actual) {
    super(String.format("Expected %d but got %d children of type %d under Hive AST token %s, tree: %s", expected,
        actual, type, node.getText(), node.dump()));
    this.node = node;
  }

  public ASTNode getNode() {
    return node;
  }
}
