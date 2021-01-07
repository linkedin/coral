/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.List;

import org.apache.calcite.rex.RexCall;


/**
 * PigOperator translates SqlOperators to Pig Latin.
 */
public abstract class PigOperator {

  // RexCall that calls the PigOperator.
  protected final RexCall rexCall;

  // List-index based mapping from Calcite index reference to field name of the input.
  protected final List<String> inputFieldNames;

  public PigOperator(RexCall rexCall, List<String> inputFieldNames) {
    this.rexCall = rexCall;
    this.inputFieldNames = inputFieldNames;
  }

  /**
   * Generates the Pig Latin for the operator used in the given rexCall.
   *
   * @return Pig Latin
   */
  public abstract String unparse();

}
