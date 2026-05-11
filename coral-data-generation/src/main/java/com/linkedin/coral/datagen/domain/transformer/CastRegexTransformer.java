/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain.transformer;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeName;

import com.linkedin.coral.datagen.domain.Domain;
import com.linkedin.coral.datagen.domain.DomainTransformer;
import com.linkedin.coral.datagen.domain.IntegerDomain;
import com.linkedin.coral.datagen.domain.IntegerRangeAutomaton;
import com.linkedin.coral.datagen.domain.NonConvertibleDomainException;
import com.linkedin.coral.datagen.domain.RegexDomain;
import com.linkedin.coral.datagen.domain.RegexToIntegerDomainConverter;

import dk.brics.automaton.Automaton;


/**
 * Transformer for CAST operations supporting cross-domain conversions.
 *
 * Handles type conversions by transforming constraints across type boundaries.
 * Supports:
 * - String to Integer: Uses RegexToIntegerDomainConverter when output is IntegerDomain
 * - Integer to String: Converts IntegerDomain to RegexDomain when output is RegexDomain
 * - Same-type casts: Preserves domain
 */
public class CastRegexTransformer implements DomainTransformer {

  private final RegexToIntegerDomainConverter regexToIntegerConverter;

  public CastRegexTransformer() {
    this.regexToIntegerConverter = new RegexToIntegerDomainConverter();
  }

  @Override
  public boolean canHandle(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    return call.getKind() == SqlKind.CAST;
  }

  @Override
  public boolean isVariableOperandPositionValid(RexNode expr) {
    RexCall call = (RexCall) expr;
    RexNode operand = call.getOperands().get(0);
    return operand instanceof RexInputRef || operand instanceof RexCall;
  }

  @Override
  public RexNode getChildForVariable(RexNode expr) {
    RexCall call = (RexCall) expr;
    return call.getOperands().get(0);
  }

  @Override
  public Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    RexCall call = (RexCall) expr;
    RelDataType targetType = call.getType();
    RexNode operand = call.getOperands().get(0);
    RelDataType sourceType = operand.getType();

    SqlTypeName targetTypeName = targetType.getSqlTypeName();
    SqlTypeName sourceTypeName = sourceType.getSqlTypeName();

    // If types are the same, no conversion needed
    if (sourceTypeName == targetTypeName) {
      return outputDomain;
    }

    // ========== CAST(string AS integer) with IntegerDomain output ==========
    if (isStringType(sourceTypeName) && isIntegerType(targetTypeName) && outputDomain instanceof IntegerDomain) {
      // Output is IntegerDomain, input must be RegexDomain
      // We need to produce a RegexDomain that, when cast to integer, gives the IntegerDomain
      IntegerDomain outputIntDomain = (IntegerDomain) outputDomain;

      // Convert IntegerDomain to RegexDomain
      // For each interval, create a regex pattern that matches those numbers
      return convertIntegerDomainToRegex(outputIntDomain);
    }

    // ========== CAST(string AS integer) with RegexDomain output ==========
    if (isStringType(sourceTypeName) && isIntegerType(targetTypeName) && outputDomain instanceof RegexDomain) {
      // Output is RegexDomain (representing integer as string)
      // Input must be RegexDomain (representing integer as string)
      RegexDomain outputRegex = (RegexDomain) outputDomain;

      // Try to convert to IntegerDomain and back to get normalized regex
      try {
        if (regexToIntegerConverter.isConvertible(outputRegex)) {
          IntegerDomain intDomain = regexToIntegerConverter.convert(outputRegex);
          return convertIntegerDomainToRegex(intDomain);
        }
      } catch (NonConvertibleDomainException e) {
        // Fall through to default handling
      }

      // Intersect with valid integer format
      String integerFormatRegex = "^-?[0-9]+$";
      RegexDomain integerFormatDomain = new RegexDomain(integerFormatRegex);
      return outputRegex.intersect(integerFormatDomain);
    }

    // ========== CAST(integer AS string) with RegexDomain output ==========
    if (isIntegerType(sourceTypeName) && isStringType(targetTypeName) && outputDomain instanceof RegexDomain) {
      RegexDomain outputRegex = (RegexDomain) outputDomain;

      // Try to convert RegexDomain to IntegerDomain
      try {
        if (regexToIntegerConverter.isConvertible(outputRegex)) {
          IntegerDomain intDomain = regexToIntegerConverter.convert(outputRegex);
          // Return the IntegerDomain as input constraint
          return intDomain;
        }
      } catch (NonConvertibleDomainException e) {
        // Fall through to default handling
      }

      // Intersect with valid integer format
      String integerFormatRegex = "^-?[0-9]+$";
      RegexDomain integerFormatDomain = new RegexDomain(integerFormatRegex);
      return outputRegex.intersect(integerFormatDomain);
    }

    // ========== CAST(integer AS string) with IntegerDomain output ==========
    if (isIntegerType(sourceTypeName) && isStringType(targetTypeName) && outputDomain instanceof IntegerDomain) {
      // This doesn't make sense - output should be RegexDomain for string type
      throw new IllegalArgumentException("CAST to string type should have RegexDomain output, not IntegerDomain");
    }

    // ========== Handle RegexDomain for other conversions ==========
    if (outputDomain instanceof RegexDomain) {
      RegexDomain outputRegex = (RegexDomain) outputDomain;

      // Handle casting TO string from other types
      if (isStringType(targetTypeName) && !isStringType(sourceTypeName)) {
        if (isDateType(sourceTypeName)) {
          // Date to String
          String dateFormatRegex = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
          RegexDomain dateFormatDomain = new RegexDomain(dateFormatRegex);
          return outputRegex.intersect(dateFormatDomain);
        } else {
          throw new UnsupportedOperationException("Cast from " + sourceTypeName + " to string not yet supported");
        }
      }

      // Handle casting FROM string to other types
      if (isStringType(sourceTypeName) && !isStringType(targetTypeName)) {
        if (isDateType(targetTypeName)) {
          return outputRegex;
        } else {
          throw new UnsupportedOperationException("Cast from string to " + targetTypeName + " not yet supported");
        }
      }

      // Numeric type conversions
      if (isNumericType(sourceTypeName) && isNumericType(targetTypeName)) {
        return outputRegex;
      }

      return outputRegex;
    }

    // ========== Handle IntegerDomain for numeric conversions ==========
    if (outputDomain instanceof IntegerDomain) {
      IntegerDomain outputIntDomain = (IntegerDomain) outputDomain;

      // Numeric to numeric
      if (isNumericType(sourceTypeName) && isNumericType(targetTypeName)) {
        return outputIntDomain;
      }

      throw new UnsupportedOperationException(
          "Cast from " + sourceTypeName + " to " + targetTypeName + " with IntegerDomain not supported");
    }

    // Default: return output domain
    return outputDomain;
  }

  /**
   * Converts IntegerDomain to RegexDomain.
   * Creates an automaton that accepts exactly the string representations of the integer values.
   * Uses digit-by-digit automaton construction for precise range matching.
   */
  private RegexDomain convertIntegerDomainToRegex(IntegerDomain intDomain) {
    if (intDomain.isEmpty()) {
      return RegexDomain.empty();
    }

    List<Automaton> parts = new ArrayList<>();

    for (IntegerDomain.Interval interval : intDomain.getIntervals()) {
      long min = interval.getMin();
      long max = interval.getMax();

      if (min == max) {
        parts.add(Automaton.makeString(String.valueOf(min)));
      } else {
        parts.add(IntegerRangeAutomaton.build(min, max));
      }
    }

    return new RegexDomain(Automaton.union(parts));
  }

  private boolean isStringType(SqlTypeName type) {
    return type == SqlTypeName.CHAR || type == SqlTypeName.VARCHAR;
  }

  private boolean isNumericType(SqlTypeName type) {
    return isIntegerType(type) || isFloatingPointType(type);
  }

  private boolean isIntegerType(SqlTypeName type) {
    return type == SqlTypeName.TINYINT || type == SqlTypeName.SMALLINT || type == SqlTypeName.INTEGER
        || type == SqlTypeName.BIGINT;
  }

  private boolean isFloatingPointType(SqlTypeName type) {
    return type == SqlTypeName.FLOAT || type == SqlTypeName.REAL || type == SqlTypeName.DOUBLE
        || type == SqlTypeName.DECIMAL;
  }

  private boolean isDateType(SqlTypeName type) {
    return type == SqlTypeName.DATE || type == SqlTypeName.TIME || type == SqlTypeName.TIMESTAMP
        || type == SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
  }

}
