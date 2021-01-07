/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.exceptions;

import org.apache.calcite.rex.RexCall;


/**
 * UnsupportedRexCallException indicates that a function/method/UDF cannot be translated to Pig Latin.
 */
public class UnsupportedRexCallException extends RuntimeException {

  private static final String UNSUPPORTED_REX_CALL_TEMPLATE =
      "The operator '%s' with type '%s' cannot be translated to Pig Latin in query: %s";

  public UnsupportedRexCallException(RexCall rexCall) {
    super(String.format(UNSUPPORTED_REX_CALL_TEMPLATE, rexCall.getOperator().getName(),
        rexCall.getOperator().getClass().getCanonicalName(), rexCall.toString()));
  }

  public UnsupportedRexCallException(String e) {
    super(e);
  }

}
