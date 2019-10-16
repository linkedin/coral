package com.linkedin.coral.pig.rel2pig.rel;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlPrefixOperator;
import org.apache.calcite.sql.SqlSpecialOperator;


/**
 * PigRexUtils provides utilities to translate SQL expressions represented as
 * Calcite RexNode into Pig Latin.
 */
public class PigRexUtils {

  private PigRexUtils() {

  }

  /**
   * Transforms a SQL expression represented as a RexNode to equivalent Pig Latin
   * @param rexNode RexNode SQL expression to be transformed
   * @param inputFieldNames Column name accessors for input references
   * @return Pig Latin equivalent of given rexNode
   */
  public static String convertRexNodePigExpression(RexNode rexNode, List<String> inputFieldNames) {
    if (rexNode instanceof RexInputRef) {
      return convertRexInputRef((RexInputRef) rexNode, inputFieldNames);
    } else if (rexNode instanceof RexCall) {
      return convertRexCall((RexCall) rexNode, inputFieldNames);
    } else if (rexNode instanceof RexLiteral) {
      return convertRexLiteral((RexLiteral) rexNode);
    }
    return rexNode.toString();
  }

  /**
   * Resolves the Pig Latin accessor name of an input reference given by a RexInputRef
   * @param rexInputRef Input reference to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin accessor name of the given rexInputRef
   */
  private static String convertRexInputRef(RexInputRef rexInputRef, List<String> inputFieldNames) {
    if (rexInputRef.getIndex() >= inputFieldNames.size()) {
      //TODO(ralam): Create better exception messages
      throw new RuntimeException(String.format(
          "RexInputRef failed to access field at index %d with RexInputRef column name mapping of size %d",
          rexInputRef.getIndex(), inputFieldNames.size()));
    }
    return inputFieldNames.get(rexInputRef.getIndex());
  }

  /**
   * Resolves the Pig Latin literal for a RexLiteral
   * @param rexLiteral RexLiteral to be resolved
   * @return Pig Latin literal of the given rexLiteral
   */
  private static String convertRexLiteral(RexLiteral rexLiteral) {
    switch (rexLiteral.getTypeName()) {
      case CHAR:
        return String.format("'%s'", rexLiteral.toString());
      default:
        return rexLiteral.toString();
    }
  }

  /**
   * Resolves the Pig Latin expression for a SQL expression given by a RexCall.
   * @param rexCall RexCall to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin expression of the given rexCall
   */
  private static String convertRexCall(RexCall rexCall, List<String> inputFieldNames) {
    // TODO(ralam): Add more supported RexCall functions.
    // TODO(ralam): CORAL-70 Refactor this code and create PigOperators for each SqlOperator with an unparse method.
    if (rexCall.getOperator() instanceof SqlSpecialOperator) {
      return convertSqlSpecialOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlBinaryOperator) {
      return convertSqlBinaryOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlPrefixOperator) {
      return convertSqlPrefixOperator(rexCall, inputFieldNames);
    }

    // TODO(ralam): Finish implementing RexCall resolution. Throw an unsupported exception in the meantime.
    throw new UnsupportedOperationException(
        String.format("Unsupported operator: %s", rexCall.getOperator().getName()));
  }

  /**
   * The Pig Latin for a Calcite SqlPrefixOperator call
   * @param rexCall RexCall to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin equivalent of the given sqlPrefixOperator applied to the given operands
   */
  private static String convertSqlPrefixOperator(RexCall rexCall, List<String> inputFieldNames) {
    // TODO(ralam): Do not generalize operand calls; we are likely to have special cases
    String operand = convertRexNodePigExpression(rexCall.getOperands().get(0), inputFieldNames);
    switch (rexCall.getOperator().getKind()) {
      case NOT:
      default:
        return String.format("%s %s", rexCall.getOperator().getName(), operand);
    }
  }

  /**
   * The Pig Latin for a Calcite SqlBinaryOperator call
   * @param rexCall RexCall to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin equivalent of the given sqlBinaryOperator applied to the given operands
   */
  private static String convertSqlBinaryOperator(RexCall rexCall, List<String> inputFieldNames) {
    String operator = rexCall.getOperator().getName();

    switch (rexCall.getOperator().getKind()) {
      case EQUALS:
        operator = "==";
        break;
      case NOT_EQUALS:
        operator = "!=";
        break;
      default:
    }

    String leftOperand = convertRexNodePigExpression(rexCall.getOperands().get(0), inputFieldNames);
    String rightOperand = convertRexNodePigExpression(rexCall.getOperands().get(1), inputFieldNames);

    return String.format("(%s %s %s)", leftOperand, operator, rightOperand);
  }

  /**
   * The Pig Latin for Calcite SqlSpecialOperator calls
   * @param rexCall RexCall to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin of the special operator call for the given inputs
   */
  private static String convertSqlSpecialOperator(RexCall rexCall, List<String> inputFieldNames) {
    // TODO(ralam): Change this function to do a map-lookup from SQLSpecialOperator function name to Pig Latin.
    if (rexCall.getOperator().getName().equalsIgnoreCase("in")) {
      return convertHiveInOperatorCall(rexCall.getOperands(), inputFieldNames);
    }

    throw new UnsupportedOperationException(String.format(
        "Unsupported operator: %s", rexCall.getOperator().getName()));
  }

  /**
   * Translates Hive In operator calls to Pig Latin.
   * This is necessary because we do not use the Calcite IN operator in coral-hive.
   * Instead we define a Hive IN operator with special input semantics.
   * @param operands The operands of a Hive IN operator call
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin of the Hive In operator call for the given inputs
   */
  private static String convertHiveInOperatorCall(List<RexNode> operands, List<String> inputFieldNames) {
    List<String> inArrayReferencesList = new ArrayList<>();
    for (RexNode input : operands) {
      inArrayReferencesList.add(convertRexNodePigExpression(input, inputFieldNames));
    }

    String inArrayInput = inArrayReferencesList.remove(0);
    String inArrayReferences = String.join(", ", inArrayReferencesList);

    return String.format("%s IN (%s)", inArrayInput, inArrayReferences);
  }
}
