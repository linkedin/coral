/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import org.testng.annotations.Test;

import dk.brics.automaton.Automaton;

import static org.testng.Assert.*;


/**
 * Tests for {@link IntegerRangeAutomaton}.
 * Verifies that the constructed automaton accepts exactly the decimal string
 * representations of integers in the given range.
 */
public class IntegerRangeAutomatonTest {

  // ==================== Single values ====================

  @Test
  public void testSingleValue() {
    Automaton a = IntegerRangeAutomaton.build(42, 42);
    assertTrue(a.run("42"));
    assertFalse(a.run("41"));
    assertFalse(a.run("43"));
  }

  @Test
  public void testZero() {
    Automaton a = IntegerRangeAutomaton.build(0, 0);
    assertTrue(a.run("0"));
    assertFalse(a.run("1"));
    assertFalse(a.run("-1"));
    assertFalse(a.run("00"));
  }

  @Test
  public void testNegativeSingle() {
    Automaton a = IntegerRangeAutomaton.build(-7, -7);
    assertTrue(a.run("-7"));
    assertFalse(a.run("7"));
    assertFalse(a.run("-8"));
  }

  // ==================== Small ranges ====================

  @Test
  public void testSmallRange() {
    Automaton a = IntegerRangeAutomaton.build(3, 7);
    for (int i = 3; i <= 7; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("2"));
    assertFalse(a.run("8"));
    assertFalse(a.run("0"));
  }

  @Test
  public void testRangeIncludingZero() {
    Automaton a = IntegerRangeAutomaton.build(0, 5);
    for (int i = 0; i <= 5; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("-1"));
    assertFalse(a.run("6"));
  }

  // ==================== Cross digit-count boundaries ====================

  @Test
  public void testCrossDigitBoundary_SingleToDouble() {
    // [7, 12] spans 1-digit (7-9) and 2-digit (10-12) numbers
    Automaton a = IntegerRangeAutomaton.build(7, 12);
    for (int i = 7; i <= 12; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("6"));
    assertFalse(a.run("13"));
  }

  @Test
  public void testCrossDigitBoundary_DoubleToTriple() {
    // [95, 105] spans 2-digit and 3-digit numbers
    Automaton a = IntegerRangeAutomaton.build(95, 105);
    for (int i = 95; i <= 105; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("94"));
    assertFalse(a.run("106"));
  }

  @Test
  public void testCrossMultipleDigitBoundaries() {
    // [5, 1005] spans 1, 2, 3, and 4-digit numbers
    Automaton a = IntegerRangeAutomaton.build(5, 1005);
    assertTrue(a.run("5"));
    assertTrue(a.run("9"));
    assertTrue(a.run("10"));
    assertTrue(a.run("99"));
    assertTrue(a.run("100"));
    assertTrue(a.run("999"));
    assertTrue(a.run("1000"));
    assertTrue(a.run("1005"));
    assertFalse(a.run("4"));
    assertFalse(a.run("1006"));
  }

  // ==================== Large ranges (the main use case) ====================

  @Test
  public void testLargeRange_100_200() {
    Automaton a = IntegerRangeAutomaton.build(100, 200);
    assertTrue(a.run("100"));
    assertTrue(a.run("150"));
    assertTrue(a.run("200"));
    assertFalse(a.run("99"));
    assertFalse(a.run("201"));
    assertFalse(a.run("50"));
    assertFalse(a.run("-100"));
  }

  @Test
  public void testLargeRange_1000_9999() {
    Automaton a = IntegerRangeAutomaton.build(1000, 9999);
    assertTrue(a.run("1000"));
    assertTrue(a.run("5555"));
    assertTrue(a.run("9999"));
    assertFalse(a.run("999"));
    assertFalse(a.run("10000"));
    assertFalse(a.run("0"));
  }

  @Test
  public void testPreciseRange_37_523() {
    // The example from the algorithm discussion
    Automaton a = IntegerRangeAutomaton.build(37, 523);
    assertTrue(a.run("37"));
    assertTrue(a.run("99"));
    assertTrue(a.run("100"));
    assertTrue(a.run("523"));
    assertFalse(a.run("36"));
    assertFalse(a.run("524"));
  }

  // ==================== Negative ranges ====================

  @Test
  public void testAllNegativeRange() {
    Automaton a = IntegerRangeAutomaton.build(-10, -3);
    for (int i = -10; i <= -3; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("-11"));
    assertFalse(a.run("-2"));
    assertFalse(a.run("5"));
    assertFalse(a.run("0"));
  }

  @Test
  public void testMixedNegativePositive() {
    Automaton a = IntegerRangeAutomaton.build(-5, 5);
    for (int i = -5; i <= 5; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("-6"));
    assertFalse(a.run("6"));
  }

  @Test
  public void testMixedLargeNegativePositive() {
    Automaton a = IntegerRangeAutomaton.build(-100, 200);
    assertTrue(a.run("-100"));
    assertTrue(a.run("-1"));
    assertTrue(a.run("0"));
    assertTrue(a.run("1"));
    assertTrue(a.run("200"));
    assertFalse(a.run("-101"));
    assertFalse(a.run("201"));
  }

  // ==================== Edge cases ====================

  @Test
  public void testEmptyRange() {
    Automaton a = IntegerRangeAutomaton.build(5, 3);
    assertTrue(a.isEmpty(), "Range [5,3] should produce empty automaton");
  }

  @Test
  public void testFullSingleDigit() {
    Automaton a = IntegerRangeAutomaton.build(0, 9);
    for (int i = 0; i <= 9; i++) {
      assertTrue(a.run(String.valueOf(i)), "Should accept " + i);
    }
    assertFalse(a.run("10"));
    assertFalse(a.run("-1"));
  }

  @Test
  public void testRejectsLeadingZeros() {
    Automaton a = IntegerRangeAutomaton.build(1, 100);
    assertFalse(a.run("01"), "Should reject leading zeros");
    assertFalse(a.run("001"), "Should reject leading zeros");
    assertFalse(a.run("099"), "Should reject leading zeros");
  }

  @Test
  public void testLargeValues() {
    // Test with values near Integer.MAX_VALUE
    Automaton a = IntegerRangeAutomaton.build(2147483600L, 2147483700L);
    assertTrue(a.run("2147483600"));
    assertTrue(a.run("2147483647")); // Integer.MAX_VALUE
    assertTrue(a.run("2147483700"));
    assertFalse(a.run("2147483599"));
    assertFalse(a.run("2147483701"));
  }

  // ==================== Integration: CastRegexTransformer uses this ====================

  @Test
  public void testMultiIntervalViaIntegerDomain() {
    // Simulate [1,5] U [100,200] — CastRegexTransformer builds one automaton per interval
    Automaton small = IntegerRangeAutomaton.build(1, 5);
    Automaton large = IntegerRangeAutomaton.build(100, 200);
    Automaton combined = small.union(large);

    // Small interval values
    for (int i = 1; i <= 5; i++) {
      assertTrue(combined.run(String.valueOf(i)), "Should accept " + i);
    }
    // Large interval values
    assertTrue(combined.run("100"));
    assertTrue(combined.run("150"));
    assertTrue(combined.run("200"));
    // Gap between intervals
    assertFalse(combined.run("0"));
    assertFalse(combined.run("6"));
    assertFalse(combined.run("50"));
    assertFalse(combined.run("99"));
    assertFalse(combined.run("201"));
  }
}
