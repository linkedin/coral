/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.exceptions;

import org.apache.calcite.rel.RelNode;


/**
 * UnsupportedRelNodeException indicates that a Calcite RelNode cannot be translated to Pig Latin.
 */
public class UnsupportedRelNodeException extends RuntimeException {

  private static final String UNSUPPORTED_REL_NODE_TEMPLATE =
      "Calcite RelNode '%s' cannot be translated to Pig Latin in query: %s";

  public UnsupportedRelNodeException(RelNode relNode) {
    super(String.format(UNSUPPORTED_REL_NODE_TEMPLATE, relNode.getClass().getCanonicalName(), relNode.toString()));
  }

}
