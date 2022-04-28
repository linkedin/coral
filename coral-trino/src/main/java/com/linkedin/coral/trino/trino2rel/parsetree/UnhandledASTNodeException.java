/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.trino2rel.parsetree;

import coral.shading.io.trino.sql.tree.Node;


public class UnhandledASTNodeException extends RuntimeException {
  public UnhandledASTNodeException(Node node, String message) {
    super(String.format(message, node.toString(), getLine(node), getColumn(node)));
  }

  private static int getLine(Node node) {
    return node.getLocation().isPresent() ? node.getLocation().get().getLineNumber() : -1;
  }

  private static int getColumn(Node node) {
    return node.getLocation().isPresent() ? node.getLocation().get().getColumnNumber() : -1;
  }
}
