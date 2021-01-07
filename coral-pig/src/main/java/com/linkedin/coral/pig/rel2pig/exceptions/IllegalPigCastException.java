/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.exceptions;

import com.linkedin.coral.pig.rel2pig.rel.operators.PigCastFunction;


/**
 * IllegalPigCastException indicates that a Calcite/SQL cast is invalid in Pig semantics.
 *
 * For example:
 *   In SQL, a binary can be casted to a varchar(string) by doing:
 *     CAST([binary] AS varchar)
 *
 *   In Pig, binary is equivalent to bytearray and varchar is equivalent to chararray.
 *   Casting from bytearray to chararray is not valid in Pig, so an IllegalPigCastException will be thrown.
 *
 * The full matrix of CAST semantics for Pig can be found here:
 *   https://pig.apache.org/docs/r0.15.0/basic.html#cast
 */
public class IllegalPigCastException extends RuntimeException {

  private static final String ILLEGAL_PIG_CAST_TEMPLATE = "CAST operation from '%s' to '%s' is not valid in Pig Latin.";

  public IllegalPigCastException(PigCastFunction.PigType fromType, PigCastFunction.PigType toType) {
    super(String.format(ILLEGAL_PIG_CAST_TEMPLATE, fromType.getName(), toType.getName()));
  }

}
