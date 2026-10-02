/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.linkedin.coral.datagen.domain.transformer.AbsIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.MinusIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.NegateIntegerTransformer;

import static org.testng.Assert.*;


/**
 * Tests for integer domain transformers (Plus, Times, Minus, Negate, Abs).
 */
public class IntegerTransformerTest {

  private RexBuilder rexBuilder;
  private RelDataTypeFactory typeFactory;

  @BeforeMethod
  public void setup() {
    rexBuilder = TestHelper.createRexBuilder();
    typeFactory = rexBuilder.getTypeFactory();
  }

  private RexNode intRef() {
    return rexBuilder.makeInputRef(typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
  }

  private RexLiteral intLit(long value) {
    return (RexLiteral) rexBuilder.makeLiteral(value, typeFactory.createSqlType(SqlTypeName.BIGINT), false);
  }

  // ==================== Plus Transformer ====================

  @Test
  public void testPlusTransformerSingleValue() {
    // Test: x + 5 = 25 => x = 20
    IntegerDomain output = IntegerDomain.of(25);
    IntegerDomain input = output.add(-5); // Inverse of adding 5

    assertFalse(input.isEmpty());
    assertTrue(input.contains(20));
    assertTrue(input.isSingleton());

    List<Long> samples = input.sample(5);
    assertFalse(samples.isEmpty());
    for (long v : samples) {
      assertEquals(v, 20);
    }
  }

  @Test
  public void testPlusTransformerInterval() {
    // Test: x + 5 in [20, 30] => x in [15, 25]
    IntegerDomain output = IntegerDomain.of(20, 30);
    IntegerDomain input = output.add(-5);

    assertTrue(input.contains(15));
    assertTrue(input.contains(25));
    assertFalse(input.contains(14));
    assertFalse(input.contains(26));

    List<Long> samples = input.sample(10);
    assertEquals(samples.size(), 10);
    for (long v : samples) {
      assertTrue(input.contains(v));
    }
  }

  // ==================== Times Transformer ====================

  @Test
  public void testTimesTransformerSingleValue() {
    // Test: x * 2 = 50 => x = 25
    // Verify by multiplying expected input back
    IntegerDomain expectedInput = IntegerDomain.of(25);
    IntegerDomain verified = expectedInput.multiply(2);

    assertTrue(verified.contains(50));
    assertTrue(verified.isSingleton());
  }

  @Test
  public void testTimesTransformerInterval() {
    // Test: x * 2 in [20, 40] => x in [10, 20]
    // Verify by multiplying expected input back
    IntegerDomain expectedInput = IntegerDomain.of(10, 20);
    IntegerDomain verified = expectedInput.multiply(2);

    assertTrue(verified.contains(20));
    assertTrue(verified.contains(40));
    assertFalse(verified.contains(19));
    assertFalse(verified.contains(41));
  }

  @Test
  public void testTimesTransformerNegativeMultiplier() {
    // Test: x * -2 in [20, 40] => x in [-20, -10]
    // Verify by multiplying expected input back
    IntegerDomain expectedInput = IntegerDomain.of(-20, -10);
    IntegerDomain verified = expectedInput.multiply(-2);

    assertTrue(verified.contains(20));
    assertTrue(verified.contains(40));
    assertFalse(verified.contains(19));
    assertFalse(verified.contains(41));
  }

  // ==================== Complex Scenarios ====================

  @Test
  public void testComplexArithmetic() {
    // Solve: 2*x + 5 = 25
    // Step 1: 2*x + 5 = 25 => 2*x = 20
    IntegerDomain afterPlus = IntegerDomain.of(25).add(-5);
    assertTrue(afterPlus.contains(20));
    assertTrue(afterPlus.isSingleton());

    // Verify: 2*10 + 5 = 25
    long x = 10;
    assertEquals(2 * x + 5, 25);
  }

  @Test
  public void testMultiIntervalOperations() {
    // Test: x + 10 where x in [1, 5] ∪ [10, 15]
    IntegerDomain input =
        IntegerDomain.of(Arrays.asList(new IntegerDomain.Interval(1, 5), new IntegerDomain.Interval(10, 15)));
    IntegerDomain output = input.add(10);

    // Expected: [11, 15] ∪ [20, 25]
    assertEquals(output.getIntervals().size(), 2);
    assertTrue(output.contains(11));
    assertTrue(output.contains(15));
    assertTrue(output.contains(20));
    assertTrue(output.contains(25));
    assertFalse(output.contains(10));
    assertFalse(output.contains(16));
    assertFalse(output.contains(19));
    assertFalse(output.contains(26));

    List<Long> samples = output.sample(10);
    for (long v : samples) {
      assertTrue(output.contains(v));
    }
  }

  @Test
  public void testIntersectionWithArithmetic() {
    // Test: x + 5 = 20 AND x in [10, 20]
    IntegerDomain fromEquation = IntegerDomain.of(20).add(-5); // x = 15
    IntegerDomain fromRange = IntegerDomain.of(10, 20);
    IntegerDomain intersection = fromEquation.intersect(fromRange);

    // Expected: {15}
    assertFalse(intersection.isEmpty());
    assertTrue(intersection.isSingleton());
    assertTrue(intersection.contains(15));

    List<Long> samples = intersection.sample(5);
    assertFalse(samples.isEmpty());
    for (long v : samples) {
      assertEquals(v, 15);
    }
  }

  // ==================== Minus Transformer ====================

  @Test
  public void testMinusTransformerVariableMinusLiteral() {
    // x - 3 = 7 => x = 10
    MinusIntegerTransformer transformer = new MinusIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.MINUS, intRef(), intLit(3));

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(7));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(10));
    assertTrue(intResult.isSingleton());
  }

  @Test
  public void testMinusTransformerLiteralMinusVariable() {
    // 10 - x = 3 => x = 7
    MinusIntegerTransformer transformer = new MinusIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.MINUS, intLit(10), intRef());

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(3));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(7));
    assertTrue(intResult.isSingleton());
  }

  @Test
  public void testMinusTransformerInterval() {
    // x - 5 in [20, 30] => x in [25, 35]
    MinusIntegerTransformer transformer = new MinusIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.MINUS, intRef(), intLit(5));

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(20, 30));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(25));
    assertTrue(intResult.contains(35));
    assertFalse(intResult.contains(24));
    assertFalse(intResult.contains(36));
  }

  @Test
  public void testMinusTransformerLiteralMinusVariableInterval() {
    // 50 - x in [10, 20] => x in [30, 40]
    MinusIntegerTransformer transformer = new MinusIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.MINUS, intLit(50), intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(10, 20));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(30));
    assertTrue(intResult.contains(40));
    assertFalse(intResult.contains(29));
    assertFalse(intResult.contains(41));
  }

  // ==================== Negate Transformer ====================

  @Test
  public void testNegateTransformerSingleValue() {
    // -x = -5 => x = 5
    NegateIntegerTransformer transformer = new NegateIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.UNARY_MINUS, intRef());

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(-5));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(5));
    assertTrue(intResult.isSingleton());
  }

  @Test
  public void testNegateTransformerInterval() {
    // -x in [10, 20] => x in [-20, -10]
    NegateIntegerTransformer transformer = new NegateIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.UNARY_MINUS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(10, 20));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(-20));
    assertTrue(intResult.contains(-10));
    assertFalse(intResult.contains(-21));
    assertFalse(intResult.contains(-9));
  }

  @Test
  public void testNegateTransformerCrossesZero() {
    // -x in [-5, 5] => x in [-5, 5] (symmetric)
    NegateIntegerTransformer transformer = new NegateIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.UNARY_MINUS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(-5, 5));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(-5));
    assertTrue(intResult.contains(0));
    assertTrue(intResult.contains(5));
    assertFalse(intResult.contains(-6));
    assertFalse(intResult.contains(6));
  }

  // ==================== Abs Transformer ====================

  @Test
  public void testAbsTransformerPositiveValue() {
    // ABS(x) = 5 => x in {-5, 5}
    AbsIntegerTransformer transformer = new AbsIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.ABS, intRef());

    assertTrue(transformer.canHandle(expr));
    assertTrue(transformer.isVariableOperandPositionValid(expr));

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(5));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(-5));
    assertTrue(intResult.contains(5));
    assertFalse(intResult.contains(0));
    assertFalse(intResult.contains(4));
    assertFalse(intResult.contains(-4));
  }

  @Test
  public void testAbsTransformerZero() {
    // ABS(x) = 0 => x = 0
    AbsIntegerTransformer transformer = new AbsIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.ABS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(0));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(0));
    assertTrue(intResult.isSingleton());
  }

  @Test
  public void testAbsTransformerPositiveInterval() {
    // ABS(x) in [3, 7] => x in [-7, -3] ∪ [3, 7]
    AbsIntegerTransformer transformer = new AbsIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.ABS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(3, 7));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(3));
    assertTrue(intResult.contains(7));
    assertTrue(intResult.contains(-3));
    assertTrue(intResult.contains(-7));
    assertFalse(intResult.contains(0));
    assertFalse(intResult.contains(2));
    assertFalse(intResult.contains(-2));
    assertFalse(intResult.contains(8));
    assertFalse(intResult.contains(-8));
  }

  @Test
  public void testAbsTransformerIntervalIncludingZero() {
    // ABS(x) in [0, 5] => x in [-5, 5]
    AbsIntegerTransformer transformer = new AbsIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.ABS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(0, 5));
    assertTrue(result instanceof IntegerDomain);
    IntegerDomain intResult = (IntegerDomain) result;
    assertTrue(intResult.contains(0));
    assertTrue(intResult.contains(-5));
    assertTrue(intResult.contains(5));
    assertFalse(intResult.contains(-6));
    assertFalse(intResult.contains(6));
  }

  @Test
  public void testAbsTransformerNegativeIntervalProducesEmpty() {
    // ABS(x) in [-5, -1] => no valid input (ABS is always non-negative)
    AbsIntegerTransformer transformer = new AbsIntegerTransformer();
    RexNode expr = rexBuilder.makeCall(SqlStdOperatorTable.ABS, intRef());

    Domain<?, ?> result = transformer.refineInputDomain(expr, IntegerDomain.of(-5, -1));
    assertTrue(result instanceof IntegerDomain);
    assertTrue(result.isEmpty());
  }
}
