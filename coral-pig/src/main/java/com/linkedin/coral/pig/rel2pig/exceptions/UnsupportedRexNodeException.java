/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.exceptions;

import org.apache.calcite.rex.RexNode;


/**
 * UnsupportedRexNodeException indicates that a Calcite/SQL operator cannot be translated to Pig Latin.
 */
public class UnsupportedRexNodeException extends RuntimeException {

  private static final String UNSUPPORTED_REX_NODE_TEMPLATE =
      "The RexNode '%s' cannot be translated to Pig Latin in query: %s";

  public UnsupportedRexNodeException(RexNode rexNode) {
    super(String.format(UNSUPPORTED_REX_NODE_TEMPLATE, rexNode.getKind().toString(), rexNode.toString()));
  }

}
