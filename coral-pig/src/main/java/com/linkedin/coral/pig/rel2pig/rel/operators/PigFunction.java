/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.List;

import org.apache.calcite.rex.RexCall;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexCallException;
import com.linkedin.coral.pig.rel2pig.rel.CalcitePigOperatorMap;
import com.linkedin.coral.pig.rel2pig.rel.functions.Function;


/**
 * PigFunction translates SqlUserDefinedFunctions/SqlFunctions to Pig Latin.
 */
public class PigFunction extends PigOperator {

  public PigFunction(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  @Override
  public String unparse() {
    // Perform a case-sensitive lookup first
    final Function caseSensitivePigUDF = CalcitePigOperatorMap.lookup(rexCall.getOperator().getName(), true);
    if (caseSensitivePigUDF != null) {
      return caseSensitivePigUDF.unparse(rexCall, inputFieldNames);
    }
    // If there is no case-sensitive UDF, perform a case-insensitive lookup.
    final Function pigUDF = CalcitePigOperatorMap.lookup(rexCall.getOperator().getName());
    if (pigUDF == null) {
      throw new UnsupportedRexCallException(rexCall);
    }
    return pigUDF.unparse(rexCall, inputFieldNames);
  }

}
