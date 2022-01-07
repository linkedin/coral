/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel.operators;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.type.MapSqlType;

import com.linkedin.coral.common.functions.UnknownSqlFunctionException;
import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexCallException;
import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;


/**
 * PigSpecialOperator translates SqlSpecialOperators to Pig Latin.
 */
public class PigSpecialOperator extends PigOperator {

  public PigSpecialOperator(RexCall rexCall, List<String> inputFieldNames) {
    super(rexCall, inputFieldNames);
  }

  @Override
  public String unparse() {
    // TODO(ralam): Change this function to do a map-lookup from SQLSpecialOperator function name to Pig Latin.
    final String operatorName = rexCall.getOperator().getName();
    if (operatorName.equalsIgnoreCase("in")) {
      return convertHiveInOperatorCall();
    } else if (operatorName.equalsIgnoreCase("item")) {
      return convertItemOperatorCall();
    }

    throw new UnknownSqlFunctionException(rexCall.getOperator().getName() + "_pig");
  }

  /**
   * Translates Hive In operator calls to Pig Latin.
   * This is necessary because we do not use the Calcite IN operator in coral-hive.
   * Instead we define a Hive IN operator with special input semantics.
   *
   * @return Pig Latin of the Hive In operator call for the given inputs
   */
  private String convertHiveInOperatorCall() {
    final List<String> inArrayReferencesList = rexCall.getOperands().stream()
        .map(operand -> PigRexUtils.convertRexNodeToPigExpression(operand, inputFieldNames))
        .collect(Collectors.toList());

    // The Hive In operator defined by coral-hive has the following semantics:
    //   - operands[0] is the checked to be 'in' an array
    //   - operands[1..n] is the array that we want to see if operand[0] is in.
    final String inArrayInput = inArrayReferencesList.remove(0);
    final String inArrayReferences = String.join(", ", inArrayReferencesList);

    return String.format("%s IN (%s)", inArrayInput, inArrayReferences);
  }

  /**
   * Translates ITEM operator calls to Pig Latin.
   *
   * @return Pig Latin of an ITEM operator call, which is implemented by:
   *           - a map access for some given key
   */
  private String convertItemOperatorCall() {
    final RexNode columnReference = rexCall.getOperands().get(0);
    String itemOperatorCall;
    if (columnReference.getType() instanceof MapSqlType) {
      itemOperatorCall = convertMapOperatorCall();
    } else {
      throw new UnsupportedRexCallException(String.format("SqlItemOperator is not supported for column of type '%s'",
          columnReference.getType().getSqlTypeName().toString()));
    }
    return itemOperatorCall;
  }

  /**
   * Translates MAP accesses for some given key to Pig Latin.
   *
   * @return Pig Latin of a map access for the given key
   */
  private String convertMapOperatorCall() {
    final String key = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(1), inputFieldNames);
    final String map = PigRexUtils.convertRexNodeToPigExpression(rexCall.getOperands().get(0), inputFieldNames);

    return String.format("%s#%s", map, key);
  }

}
