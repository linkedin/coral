/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.functions;

import java.util.List;

import org.apache.calcite.rex.RexCall;


/**
 * Represents an operator in Pig Latin.
 * TODO(ralam): Rename this class to Operator after migrating all operators to use CalcitePigOperatorMap.
 */
public abstract class Function {

  /**
   * Generates the Pig Latin for the operator used in the given rexCall and inputFieldNames.
   *
   * @param rexCall RexCall for the given operator.
   * @param inputFieldNames List-index based mapping from Calcite index reference to field name of the input.
   * @return Pig Latin expression for the rexCall
   */
  public abstract String unparse(RexCall rexCall, List<String> inputFieldNames);

}
