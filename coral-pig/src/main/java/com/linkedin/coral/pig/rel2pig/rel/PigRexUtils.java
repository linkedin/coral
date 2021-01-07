/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.pig.rel2pig.rel;

import java.util.List;

import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlPostfixOperator;
import org.apache.calcite.sql.SqlPrefixOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.fun.SqlCaseOperator;
import org.apache.calcite.sql.fun.SqlCastFunction;
import org.apache.calcite.util.NlsString;

import com.linkedin.coral.pig.rel2pig.exceptions.UnsupportedRexNodeException;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigBinaryOperator;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigCaseOperator;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigCastFunction;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigFunction;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigOperator;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigPostfixOperator;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigPrefixOperator;
import com.linkedin.coral.pig.rel2pig.rel.operators.PigSpecialOperator;


/**
 * PigRexUtils provides utilities to translate SQL expressions represented as
 * Calcite RexNode into Pig Latin.
 */
public class PigRexUtils {

  private PigRexUtils() {

  }

  /**
   * Transforms a SQL expression represented as a RexNode to equivalent Pig Latin
   *
   * @param rexNode RexNode SQL expression to be transformed
   * @param inputFieldNames Column name accessors for input references
   * @return Pig Latin equivalent of given rexNode
   */
  public static String convertRexNodeToPigExpression(RexNode rexNode, List<String> inputFieldNames) {
    if (rexNode instanceof RexInputRef) {
      return convertRexInputRef((RexInputRef) rexNode, inputFieldNames);
    } else if (rexNode instanceof RexCall) {
      return convertRexCall((RexCall) rexNode, inputFieldNames);
    } else if (rexNode instanceof RexFieldAccess) {
      return convertRexFieldAccess((RexFieldAccess) rexNode, inputFieldNames);
    } else if (rexNode instanceof RexLiteral) {
      return convertRexLiteral((RexLiteral) rexNode);
    }

    throw new UnsupportedRexNodeException(rexNode);
  }

  /**
   * Resolves the Pig Latin accessor name of an input reference given by a RexInputRef
   *
   * @param rexInputRef Input reference to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin accessor name of the given rexInputRef
   */
  private static String convertRexInputRef(RexInputRef rexInputRef, List<String> inputFieldNames) {
    if (rexInputRef.getIndex() >= inputFieldNames.size()) {
      throw new IllegalArgumentException(String.format(
          "RexInputRef failed to access field at index %d with RexInputRef column name mapping of size %d",
          rexInputRef.getIndex(), inputFieldNames.size()));
    }
    return inputFieldNames.get(rexInputRef.getIndex());
  }

  /**
   * Resolves the Pig Latin literal for a RexLiteral
   *
   * @param rexLiteral RexLiteral to be resolved
   * @return Pig Latin literal of the given rexLiteral
   */
  private static String convertRexLiteral(RexLiteral rexLiteral) {
    Comparable value = rexLiteral.getValue();
    switch (rexLiteral.getTypeName()) {
      case CHAR:
        // We need a special case for NlsString because it adds its charset information to its value.
        if (rexLiteral.getValue() instanceof NlsString) {
          value = ((NlsString) value).getValue();
        }
        return String.format("'%s'", value);
      default:
        return String.valueOf(value);
    }
  }

  /**
   * Resolves the Pig Latin expression for a struct field access given by a RexCall.
   *
   * @param rexFieldAccess RexFieldAccess to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin expression of the given rexCall
   */
  private static String convertRexFieldAccess(RexFieldAccess rexFieldAccess, List<String> inputFieldNames) {
    final String parentFieldName = convertRexNodeToPigExpression(rexFieldAccess.getReferenceExpr(), inputFieldNames);
    final String nestedFieldName = rexFieldAccess.getField().getName();
    return String.join(".", parentFieldName, nestedFieldName);
  }

  /**
   * Resolves the Pig Latin expression for a SQL expression given by a RexCall.
   *
   * @param rexCall RexCall to be resolved
   * @param inputFieldNames Mapping from list index to accessor name
   * @return Pig Latin expression of the given rexCall
   */
  private static String convertRexCall(RexCall rexCall, List<String> inputFieldNames) {
    // TODO(ralam): Add more supported RexCall functions.
    PigOperator pigOperator = null;

    if (rexCall.getOperator() instanceof SqlSpecialOperator) {
      pigOperator = new PigSpecialOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlBinaryOperator) {
      pigOperator = new PigBinaryOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlPrefixOperator) {
      pigOperator = new PigPrefixOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlCaseOperator) {
      pigOperator = new PigCaseOperator(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlCastFunction) {
      pigOperator = new PigCastFunction(rexCall, inputFieldNames);
    } else if (rexCall.getOperator() instanceof SqlPostfixOperator) {
      pigOperator = new PigPostfixOperator(rexCall, inputFieldNames);
    } else {
      pigOperator = new PigFunction(rexCall, inputFieldNames);
    }

    return pigOperator.unparse();
  }

}
