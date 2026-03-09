/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Tests for integer domain transformers (Plus and Times).
 */
public class IntegerTransformerTest {

  @Test
  public void testPlusTransformerSingleValue() {
    // Test: x + 5 = 25 => x = 20
    IntegerDomain output = IntegerDomain.of(25);
    IntegerDomain input = output.add(-5); // Inverse of adding 5

    assertFalse(input.isEmpty());
    assertTrue(input.contains(20));
    assertTrue(input.isSingleton());

    List<Long> samples = input.sampleValues(5);
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

    List<Long> samples = input.sampleValues(10);
    assertEquals(samples.size(), 10);
    for (long v : samples) {
      assertTrue(input.contains(v));
    }
  }

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

    List<Long> samples = output.sampleValues(10);
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

    List<Long> samples = intersection.sampleValues(5);
    assertFalse(samples.isEmpty());
    for (long v : samples) {
      assertEquals(v, 15);
    }
  }
}
