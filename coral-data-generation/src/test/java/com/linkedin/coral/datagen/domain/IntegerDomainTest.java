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
 * Tests for IntegerDomain class.
 */
public class IntegerDomainTest {

  @Test
  public void testSingleValue() {
    IntegerDomain domain = IntegerDomain.of(42);
    assertFalse(domain.isEmpty());
    assertTrue(domain.contains(42));
    assertFalse(domain.contains(43));
    assertTrue(domain.isSingleton());

    List<Long> samples = domain.sampleValues(5);
    assertFalse(samples.isEmpty());
    for (long v : samples) {
      assertTrue(domain.contains(v));
    }
  }

  @Test
  public void testSingleInterval() {
    IntegerDomain domain = IntegerDomain.of(10, 20);
    assertTrue(domain.contains(10));
    assertTrue(domain.contains(15));
    assertTrue(domain.contains(20));
    assertFalse(domain.contains(9));
    assertFalse(domain.contains(21));
    assertFalse(domain.isSingleton());

    List<Long> samples = domain.sampleValues(5);
    assertEquals(samples.size(), 5);
    for (long v : samples) {
      assertTrue(domain.contains(v));
    }
  }

  @Test
  public void testMultipleIntervals() {
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 5),
        new IntegerDomain.Interval(10, 15), new IntegerDomain.Interval(20, 30));
    IntegerDomain domain = IntegerDomain.of(intervals);
    assertTrue(domain.contains(3));
    assertFalse(domain.contains(7));
    assertTrue(domain.contains(12));
    assertTrue(domain.contains(25));
    assertFalse(domain.contains(16));

    List<Long> samples = domain.sampleValues(10);
    assertFalse(samples.isEmpty());
    for (long v : samples) {
      assertTrue(domain.contains(v));
    }
  }

  @Test
  public void testIntersection() {
    IntegerDomain domain1 = IntegerDomain.of(1, 20);
    IntegerDomain domain2 = IntegerDomain.of(10, 30);
    IntegerDomain intersection = domain1.intersect(domain2);

    assertFalse(intersection.isEmpty());
    assertTrue(intersection.contains(10));
    assertTrue(intersection.contains(20));
    assertFalse(intersection.contains(9));
    assertFalse(intersection.contains(21));

    List<Long> samples = intersection.sampleValues(5);
    for (long v : samples) {
      assertTrue(domain1.contains(v));
      assertTrue(domain2.contains(v));
    }
  }

  @Test
  public void testUnion() {
    IntegerDomain domain1 = IntegerDomain.of(1, 10);
    IntegerDomain domain2 = IntegerDomain.of(20, 30);
    IntegerDomain union = domain1.union(domain2);

    assertFalse(union.isEmpty());
    assertTrue(union.contains(5));
    assertTrue(union.contains(25));
    assertFalse(union.contains(15));

    List<Long> samples = union.sampleValues(10);
    for (long v : samples) {
      assertTrue(union.contains(v));
    }
  }

  @Test
  public void testAddConstant() {
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain shifted = domain.add(5);

    assertTrue(shifted.contains(15));
    assertTrue(shifted.contains(25));
    assertFalse(shifted.contains(14));
    assertFalse(shifted.contains(26));

    List<Long> samples = shifted.sampleValues(5);
    for (long v : samples) {
      assertTrue(shifted.contains(v));
    }
  }

  @Test
  public void testMultiplyConstant() {
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain scaled = domain.multiply(2);

    assertTrue(scaled.contains(20));
    assertTrue(scaled.contains(40));
    assertFalse(scaled.contains(19));
    assertFalse(scaled.contains(41));

    List<Long> samples = scaled.sampleValues(5);
    for (long v : samples) {
      assertTrue(scaled.contains(v));
    }
  }

  @Test
  public void testNegativeMultiply() {
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain scaled = domain.multiply(-1);

    assertTrue(scaled.contains(-10));
    assertTrue(scaled.contains(-20));
    assertTrue(scaled.contains(-15));
    assertFalse(scaled.contains(-9));
    assertFalse(scaled.contains(-21));
    assertFalse(scaled.contains(10));
  }

  @Test
  public void testOverlappingIntervalsMerge() {
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 10),
        new IntegerDomain.Interval(5, 15), new IntegerDomain.Interval(20, 30));
    IntegerDomain domain = IntegerDomain.of(intervals);

    // [1,10] and [5,15] should merge into [1,15]
    assertTrue(domain.contains(1));
    assertTrue(domain.contains(12));
    assertTrue(domain.contains(15));
    assertFalse(domain.contains(16));
    assertTrue(domain.contains(20));
    assertTrue(domain.contains(30));
    assertEquals(domain.getIntervals().size(), 2);
    assertEquals(domain.getIntervals().get(0), new IntegerDomain.Interval(1, 15));
    assertEquals(domain.getIntervals().get(1), new IntegerDomain.Interval(20, 30));
  }

  @Test
  public void testAdjacentIntervalsMerge() {
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 10),
        new IntegerDomain.Interval(11, 20), new IntegerDomain.Interval(30, 40));
    IntegerDomain domain = IntegerDomain.of(intervals);

    // [1,10] and [11,20] should merge into [1,20]
    assertEquals(domain.getIntervals().size(), 2);
    assertEquals(domain.getIntervals().get(0), new IntegerDomain.Interval(1, 20));
    assertEquals(domain.getIntervals().get(1), new IntegerDomain.Interval(30, 40));
    assertTrue(domain.contains(15));
    assertFalse(domain.contains(25));
  }

  @Test
  public void testEmptyDomain() {
    IntegerDomain empty = IntegerDomain.empty();
    assertTrue(empty.isEmpty());
    assertTrue(empty.sampleValues(5).isEmpty());
  }

  @Test
  public void testIntersectionEmpty() {
    IntegerDomain domain1 = IntegerDomain.of(1, 10);
    IntegerDomain domain2 = IntegerDomain.of(20, 30);
    IntegerDomain intersection = domain1.intersect(domain2);
    assertTrue(intersection.isEmpty());
    assertTrue(intersection.sampleValues(5).isEmpty());
  }

  @Test
  public void testComplexArithmetic() {
    // Solve: 2*x + 5 = 25, where x in [0, 100]
    // => 2*x = 20
    // => x = 10
    IntegerDomain output = IntegerDomain.of(25);
    IntegerDomain afterSubtract = output.add(-5); // result = 20
    IntegerDomain solution = afterSubtract.multiply(1).intersect(IntegerDomain.of(0, 100));

    assertFalse(solution.isEmpty());
    assertTrue(solution.contains(20));

    long x = solution.sampleValues(1).get(0);
    assertEquals(x, 20);
  }

  @Test
  public void testMultiIntervalIntersection() {
    List<IntegerDomain.Interval> intervals1 =
        Arrays.asList(new IntegerDomain.Interval(1, 20), new IntegerDomain.Interval(30, 50));
    List<IntegerDomain.Interval> intervals2 =
        Arrays.asList(new IntegerDomain.Interval(10, 35), new IntegerDomain.Interval(45, 60));

    IntegerDomain domain1 = IntegerDomain.of(intervals1);
    IntegerDomain domain2 = IntegerDomain.of(intervals2);
    IntegerDomain intersection = domain1.intersect(domain2);

    // Expected: [10, 20] ∪ [30, 35] ∪ [45, 50]
    assertFalse(intersection.isEmpty());
    assertEquals(intersection.getIntervals().size(), 3);
    assertEquals(intersection.getIntervals().get(0), new IntegerDomain.Interval(10, 20));
    assertEquals(intersection.getIntervals().get(1), new IntegerDomain.Interval(30, 35));
    assertEquals(intersection.getIntervals().get(2), new IntegerDomain.Interval(45, 50));

    assertTrue(intersection.contains(15));
    assertTrue(intersection.contains(32));
    assertTrue(intersection.contains(47));
    assertFalse(intersection.contains(9));
    assertFalse(intersection.contains(25));
    assertFalse(intersection.contains(40));
  }
}
