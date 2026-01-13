/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;


/**
 * Tests for IntegerDomain class.
 */
public class IntegerDomainTest {

  @Test
  public void testSingleValue() {
    System.out.println("\n=== Single Value Test ===");
    IntegerDomain domain = IntegerDomain.of(42);
    System.out.println("Domain: " + domain);
    System.out.println("Is empty: " + domain.isEmpty());
    System.out.println("Contains 42: " + domain.contains(42));
    System.out.println("Contains 43: " + domain.contains(43));
    System.out.println("Samples: " + domain.sampleValues(5));
  }

  @Test
  public void testSingleInterval() {
    System.out.println("\n=== Single Interval Test ===");
    IntegerDomain domain = IntegerDomain.of(10, 20);
    System.out.println("Domain: " + domain);
    System.out.println("Contains 10: " + domain.contains(10));
    System.out.println("Contains 15: " + domain.contains(15));
    System.out.println("Contains 20: " + domain.contains(20));
    System.out.println("Contains 21: " + domain.contains(21));
    System.out.println("Samples: " + domain.sampleValues(5));
  }

  @Test
  public void testMultipleIntervals() {
    System.out.println("\n=== Multiple Intervals Test ===");
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 5),
        new IntegerDomain.Interval(10, 15), new IntegerDomain.Interval(20, 30));
    IntegerDomain domain = IntegerDomain.of(intervals);
    System.out.println("Domain: " + domain);
    System.out.println("Contains 3: " + domain.contains(3));
    System.out.println("Contains 7: " + domain.contains(7));
    System.out.println("Contains 12: " + domain.contains(12));
    System.out.println("Contains 25: " + domain.contains(25));
    System.out.println("Samples: " + domain.sampleValues(10));
  }

  @Test
  public void testIntersection() {
    System.out.println("\n=== Intersection Test ===");
    IntegerDomain domain1 = IntegerDomain.of(1, 20);
    IntegerDomain domain2 = IntegerDomain.of(10, 30);
    IntegerDomain intersection = domain1.intersect(domain2);
    System.out.println("Domain 1: " + domain1);
    System.out.println("Domain 2: " + domain2);
    System.out.println("Intersection: " + intersection);
    System.out.println("Samples: " + intersection.sampleValues(5));
  }

  @Test
  public void testUnion() {
    System.out.println("\n=== Union Test ===");
    IntegerDomain domain1 = IntegerDomain.of(1, 10);
    IntegerDomain domain2 = IntegerDomain.of(20, 30);
    IntegerDomain union = domain1.union(domain2);
    System.out.println("Domain 1: " + domain1);
    System.out.println("Domain 2: " + domain2);
    System.out.println("Union: " + union);
    System.out.println("Samples: " + union.sampleValues(10));
  }

  @Test
  public void testAddConstant() {
    System.out.println("\n=== Add Constant Test ===");
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain shifted = domain.add(5);
    System.out.println("Original domain: " + domain);
    System.out.println("After adding 5: " + shifted);
    System.out.println("Samples: " + shifted.sampleValues(5));
  }

  @Test
  public void testMultiplyConstant() {
    System.out.println("\n=== Multiply Constant Test ===");
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain scaled = domain.multiply(2);
    System.out.println("Original domain: " + domain);
    System.out.println("After multiplying by 2: " + scaled);
    System.out.println("Samples: " + scaled.sampleValues(5));
  }

  @Test
  public void testNegativeMultiply() {
    System.out.println("\n=== Negative Multiply Test ===");
    IntegerDomain domain = IntegerDomain.of(10, 20);
    IntegerDomain scaled = domain.multiply(-1);
    System.out.println("Original domain: " + domain);
    System.out.println("After multiplying by -1: " + scaled);
    System.out.println("Samples: " + scaled.sampleValues(5));
  }

  @Test
  public void testOverlappingIntervalsMerge() {
    System.out.println("\n=== Overlapping Intervals Merge Test ===");
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 10),
        new IntegerDomain.Interval(5, 15), new IntegerDomain.Interval(20, 30));
    IntegerDomain domain = IntegerDomain.of(intervals);
    System.out.println("Input intervals: [1, 10], [5, 15], [20, 30]");
    System.out.println("Merged domain: " + domain);
    System.out.println("Samples: " + domain.sampleValues(10));
  }

  @Test
  public void testAdjacentIntervalsMerge() {
    System.out.println("\n=== Adjacent Intervals Merge Test ===");
    List<IntegerDomain.Interval> intervals = Arrays.asList(new IntegerDomain.Interval(1, 10),
        new IntegerDomain.Interval(11, 20), new IntegerDomain.Interval(30, 40));
    IntegerDomain domain = IntegerDomain.of(intervals);
    System.out.println("Input intervals: [1, 10], [11, 20], [30, 40]");
    System.out.println("Merged domain: " + domain);
    System.out.println("Samples: " + domain.sampleValues(10));
  }

  @Test
  public void testEmptyDomain() {
    System.out.println("\n=== Empty Domain Test ===");
    IntegerDomain empty = IntegerDomain.empty();
    System.out.println("Empty domain: " + empty);
    System.out.println("Is empty: " + empty.isEmpty());
    System.out.println("Samples: " + empty.sampleValues(5));
  }

  @Test
  public void testIntersectionEmpty() {
    System.out.println("\n=== Intersection Empty Test ===");
    IntegerDomain domain1 = IntegerDomain.of(1, 10);
    IntegerDomain domain2 = IntegerDomain.of(20, 30);
    IntegerDomain intersection = domain1.intersect(domain2);
    System.out.println("Domain 1: " + domain1);
    System.out.println("Domain 2: " + domain2);
    System.out.println("Intersection: " + intersection);
    System.out.println("Is empty: " + intersection.isEmpty());
  }

  @Test
  public void testComplexArithmetic() {
    System.out.println("\n=== Complex Arithmetic Test ===");
    // Solve: 2*x + 5 = 25, where x in [0, 100]
    // => 2*x = 20
    // => x = 10
    IntegerDomain output = IntegerDomain.of(25);
    IntegerDomain afterSubtract = output.add(-5); // x = 20
    IntegerDomain solution = afterSubtract.multiply(1).intersect(IntegerDomain.of(0, 100));

    System.out.println("Equation: 2*x + 5 = 25");
    System.out.println("Output domain: " + output);
    System.out.println("After subtracting 5: " + afterSubtract);
    System.out.println("Solution (x must be in [0, 100]): " + solution);

    // Verify
    if (!solution.isEmpty()) {
      long x = solution.sampleValues(1).get(0);
      System.out.println("Sample x: " + x);
      System.out.println("Verification: 2*" + x + " + 5 = " + (2 * x + 5));
    }
  }

  @Test
  public void testMultiIntervalIntersection() {
    System.out.println("\n=== Multi-Interval Intersection Test ===");
    List<IntegerDomain.Interval> intervals1 =
        Arrays.asList(new IntegerDomain.Interval(1, 20), new IntegerDomain.Interval(30, 50));
    List<IntegerDomain.Interval> intervals2 =
        Arrays.asList(new IntegerDomain.Interval(10, 35), new IntegerDomain.Interval(45, 60));

    IntegerDomain domain1 = IntegerDomain.of(intervals1);
    IntegerDomain domain2 = IntegerDomain.of(intervals2);
    IntegerDomain intersection = domain1.intersect(domain2);

    System.out.println("Domain 1: " + domain1);
    System.out.println("Domain 2: " + domain2);
    System.out.println("Intersection: " + intersection);
    System.out.println("Expected: [10, 20] ∪ [30, 35] ∪ [45, 50]");
    System.out.println("Samples: " + intersection.sampleValues(15));
  }
}
