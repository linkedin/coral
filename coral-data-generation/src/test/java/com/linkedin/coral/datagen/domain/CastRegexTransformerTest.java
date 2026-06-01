/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.linkedin.coral.datagen.domain.transformer.CastRegexTransformer;

import static org.testng.Assert.*;


/**
 * Tests for CastRegexTransformer, focusing on cross-domain conversions.
 * 
 * Key scenarios:
 * - CAST(string AS integer) with IntegerDomain output → RegexDomain input
 * - CAST(integer AS string) with RegexDomain output → IntegerDomain input
 * - Same-type casts
 */
public class CastRegexTransformerTest {

  private CastRegexTransformer transformer;
  private RexBuilder rexBuilder;
  private RelDataTypeFactory typeFactory;

  @BeforeMethod
  public void setup() {
    transformer = new CastRegexTransformer();
    rexBuilder = TestHelper.createRexBuilder();
    typeFactory = rexBuilder.getTypeFactory();
  }

  // ==================== String to Integer Conversion Tests ====================

  @Test
  public void testCastStringToIntegerWithIntegerDomainOutput() {
    // CAST(x AS INTEGER) where output is IntegerDomain [100, 200]
    // Input should be RegexDomain matching "100", "101", ..., "200"

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(100, 200);

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Input should be RegexDomain
    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;

    // Should match numeric values in the range
    assertFalse(regexInput.isEmpty());
  }

  @Test
  public void testCastStringToIntegerSmallRange() {
    // CAST(x AS INTEGER) where output is [10, 12]
    // Input should be RegexDomain matching "10|11|12"

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(10, 12);

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;
    assertFalse(regexInput.isEmpty());
  }

  @Test
  public void testCastStringToIntegerWithRegexDomainOutput() {
    // CAST(x AS INTEGER) where output is RegexDomain "^[0-9]{3}$"
    // This represents integers 0-999 as strings

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Input should be RegexDomain (same or refined)
    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;
    assertFalse(regexInput.isEmpty());
  }

  // ==================== Integer to String Conversion Tests ====================

  @Test
  public void testCastIntegerToStringWithRegexDomainOutput() {
    // CAST(x AS VARCHAR) where output is RegexDomain "^[0-9]{3}$"
    // Input should be IntegerDomain [0, 999]

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Input should be IntegerDomain (converted from regex)
    assertTrue(inputDomain instanceof IntegerDomain);
    IntegerDomain intInput = (IntegerDomain) inputDomain;

    assertTrue(intInput.contains(0));
    assertTrue(intInput.contains(500));
    assertTrue(intInput.contains(999));
    assertFalse(intInput.contains(1000));
  }

  @Test
  public void testCastIntegerToStringSpecificValues() {
    // CAST(x AS VARCHAR) where output is RegexDomain "^(10|20|30)$"
    // Input should be IntegerDomain with values 10, 20, 30

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    RegexDomain outputDomain = new RegexDomain("^(10|20|30)$");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    assertTrue(inputDomain instanceof IntegerDomain);
    IntegerDomain intInput = (IntegerDomain) inputDomain;

    assertTrue(intInput.contains(10));
    assertTrue(intInput.contains(20));
    assertTrue(intInput.contains(30));
    assertFalse(intInput.contains(15));
    assertFalse(intInput.contains(40));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testCastIntegerToStringWithIntegerDomainOutput() {
    // CAST(x AS VARCHAR) with IntegerDomain output doesn't make sense

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(10, 20);

    transformer.refineInputDomain(castExpr, outputDomain);
  }

  // ==================== Same Type Casts ====================

  @Test
  public void testCastSameTypeInteger() {
    // CAST(x AS INTEGER) where x is already INTEGER

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(10, 20);

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Should return same domain
    assertEquals(inputDomain, outputDomain);
  }

  @Test
  public void testCastSameTypeString() {
    // CAST(x AS VARCHAR) where x is already VARCHAR

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Should return same domain
    assertEquals(inputDomain, outputDomain);
  }

  // ==================== Numeric Type Conversions ====================

  @Test
  public void testCastIntegerToBigInt() {
    // CAST(x AS BIGINT) where x is INTEGER

    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.BIGINT), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(1000, 2000);

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Should preserve IntegerDomain
    assertTrue(inputDomain instanceof IntegerDomain);
    assertEquals(inputDomain, outputDomain);
  }

  // ==================== Edge Cases ====================

  @Test
  public void testCastStringToIntegerEmptyDomain() {
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain = IntegerDomain.empty();

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    assertTrue(inputDomain instanceof RegexDomain);
    assertTrue(inputDomain.isEmpty());
  }

  @Test
  public void testCastStringToIntegerNonConvertibleRegex() {
    // Output regex contains non-numeric patterns
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    // This regex has wildcards, not convertible to IntegerDomain
    RegexDomain outputDomain = new RegexDomain("^.*$");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);

    // Should fall back to integer format constraint
    assertTrue(inputDomain instanceof RegexDomain);
    assertFalse(inputDomain.isEmpty());
  }

  // ==================== canHandle Tests ====================

  @Test
  public void testCanHandle() {
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    assertTrue(transformer.canHandle(castExpr));
  }

  @Test
  public void testCannotHandleNonCast() {
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);

    assertFalse(transformer.canHandle(inputRef));
  }

  @Test
  public void testIsVariableOperandPositionValid() {
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    assertTrue(transformer.isVariableOperandPositionValid(castExpr));
  }

  @Test
  public void testGetChildForVariable() {
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    RexNode child = transformer.getChildForVariable(castExpr);
    assertEquals(child, inputRef);
  }

  // ==================== Bug Bash Tests ====================

  @Test
  public void testCastStringToIntegerLargeRangePreservesConstraints() {
    // Bug bash item #3/#4: large ranges should produce precise regex, not "-?[0-9]+"
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain = IntegerDomain.of(100, 200);

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;

    // Must accept values in range
    assertTrue(regexInput.getAutomaton().run("100"), "Should accept 100");
    assertTrue(regexInput.getAutomaton().run("150"), "Should accept 150");
    assertTrue(regexInput.getAutomaton().run("200"), "Should accept 200");

    // Must reject values outside range — this is the key assertion
    assertFalse(regexInput.getAutomaton().run("99"), "Should reject 99");
    assertFalse(regexInput.getAutomaton().run("201"), "Should reject 201");
    assertFalse(regexInput.getAutomaton().run("0"), "Should reject 0");
    assertFalse(regexInput.getAutomaton().run("-5"), "Should reject -5");
    assertFalse(regexInput.getAutomaton().run("1000"), "Should reject 1000");
  }

  @Test
  public void testCastStringToIntegerMultiIntervalWithLargeRange() {
    // Bug bash item #3: break/return must not drop subsequent intervals
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);

    IntegerDomain outputDomain =
        IntegerDomain.of(Arrays.asList(new IntegerDomain.Interval(1, 5), new IntegerDomain.Interval(100, 200)));

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;

    // Small interval values
    for (int i = 1; i <= 5; i++) {
      assertTrue(regexInput.getAutomaton().run(String.valueOf(i)), "Should accept " + i);
    }
    // Large interval values
    assertTrue(regexInput.getAutomaton().run("100"), "Should accept 100");
    assertTrue(regexInput.getAutomaton().run("150"), "Should accept 150");
    assertTrue(regexInput.getAutomaton().run("200"), "Should accept 200");

    // Gap between intervals
    assertFalse(regexInput.getAutomaton().run("0"), "Should reject 0");
    assertFalse(regexInput.getAutomaton().run("6"), "Should reject 6");
    assertFalse(regexInput.getAutomaton().run("50"), "Should reject 50");
    assertFalse(regexInput.getAutomaton().run("99"), "Should reject 99");
    assertFalse(regexInput.getAutomaton().run("201"), "Should reject 201");
  }

  @Test
  public void testCastDateToStringAcceptsDay31() {
    // Bug bash item #2: day regex must include 31
    RexNode inputRef = rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.DATE), 0);
    RexNode castExpr = rexBuilder.makeCast(typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);

    RegexDomain outputDomain = RegexDomain.literal("2024-01-31");

    Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
    assertTrue(inputDomain instanceof RegexDomain);
    RegexDomain regexInput = (RegexDomain) inputDomain;

    assertFalse(regexInput.isEmpty(), "Day 31 should not produce empty domain");
    assertTrue(regexInput.getAutomaton().run("2024-01-31"), "Should accept 2024-01-31");
  }
}
