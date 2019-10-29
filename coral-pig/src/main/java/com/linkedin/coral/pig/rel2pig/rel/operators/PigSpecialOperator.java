package com.linkedin.coral.pig.rel2pig.rel.operators;

import com.linkedin.coral.pig.rel2pig.rel.PigRexUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.rex.RexCall;


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
    if (rexCall.getOperator().getName().equalsIgnoreCase("in")) {
      return convertHiveInOperatorCall();
    }

    throw new UnsupportedOperationException(String.format(
        "Unsupported operator: %s", rexCall.getOperator().getName()));
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

}
