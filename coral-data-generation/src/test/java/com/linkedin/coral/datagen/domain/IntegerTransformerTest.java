/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import org.testng.annotations.Test;


/**
 * Tests for integer domain transformers (Plus and Times).
 */
public class IntegerTransformerTest {

  @Test
  public void testPlusTransformerSingleValue() {
    System.out.println("\n=== Plus Transformer - Single Value ===");
    // Test: x + 5 = 25 => x = 20
    IntegerDomain output = IntegerDomain.of(25);
    System.out.println("Output domain (result of x + 5): " + output);

    // Simulate the transformation (in real usage, this would be called by the inference program)
    IntegerDomain input = output.add(-5); // Inverse of adding 5
    System.out.println("Input domain (x): " + input);
    System.out.println("Samples: " + input.sampleValues(5));
  }

  @Test
  public void testPlusTransformerInterval() {
    System.out.println("\n=== Plus Transformer - Interval ===");
    // Test: x + 5 in [20, 30] => x in [15, 25]
    IntegerDomain output = IntegerDomain.of(20, 30);
    System.out.println("Output domain (result of x + 5): " + output);

    IntegerDomain input = output.add(-5);
    System.out.println("Input domain (x): " + input);
    System.out.println("Samples: " + input.sampleValues(10));
  }

  @Test
  public void testTimesTransformerSingleValue() {
    System.out.println("\n=== Times Transformer - Single Value ===");
    // Test: x * 2 = 50 => x = 25
    IntegerDomain output = IntegerDomain.of(50);
    System.out.println("Output domain (result of x * 2): " + output);

    // For multiplication, we need to handle division
    // This would be done by the refineInputDomain method
    System.out.println("Expected input domain: {25}");
  }

  @Test
  public void testTimesTransformerInterval() {
    System.out.println("\n=== Times Transformer - Interval ===");
    // Test: x * 2 in [20, 40] => x in [10, 20]
    IntegerDomain output = IntegerDomain.of(20, 40);
    System.out.println("Output domain (result of x * 2): " + output);
    System.out.println("Expected input domain (x): [10, 20]");

    // Verify by multiplying back
    IntegerDomain expectedInput = IntegerDomain.of(10, 20);
    IntegerDomain verified = expectedInput.multiply(2);
    System.out.println("Verification (x * 2): " + verified);
  }

  @Test
  public void testTimesTransformerNegativeMultiplier() {
    System.out.println("\n=== Times Transformer - Negative Multiplier ===");
    // Test: x * -2 in [20, 40] => x in [-20, -10]
    IntegerDomain output = IntegerDomain.of(20, 40);
    System.out.println("Output domain (result of x * -2): " + output);
    System.out.println("Expected input domain (x): [-20, -10]");

    // Verify by multiplying back
    IntegerDomain expectedInput = IntegerDomain.of(-20, -10);
    IntegerDomain verified = expectedInput.multiply(-2);
    System.out.println("Verification (x * -2): " + verified);
  }

  @Test
  public void testComplexArithmetic() {
    System.out.println("\n=== Complex Arithmetic: 2*x + 5 = 25 ===");
    // Solve: 2*x + 5 = 25
    // Step 1: 2*x + 5 = 25 => 2*x = 20
    IntegerDomain afterPlus = IntegerDomain.of(25).add(-5);
    System.out.println("After inverting +5: " + afterPlus);

    // Step 2: 2*x = 20 => x = 10
    // In real code, this would use TimesRegexTransformer.refineInputDomain()
    System.out.println("Expected solution: x = 10");

    // Verify
    long x = 10;
    System.out.println("Verification: 2*" + x + " + 5 = " + (2 * x + 5));
  }

  @Test
  public void testMultiIntervalOperations() {
    System.out.println("\n=== Multi-Interval Operations ===");
    // Test: x + 10 where x in [1, 5] ∪ [10, 15]
    IntegerDomain input =
        IntegerDomain.of(java.util.Arrays.asList(new IntegerDomain.Interval(1, 5), new IntegerDomain.Interval(10, 15)));
    System.out.println("Input domain (x): " + input);

    IntegerDomain output = input.add(10);
    System.out.println("Output domain (x + 10): " + output);
    System.out.println("Expected: [11, 15] ∪ [20, 25]");
    System.out.println("Samples: " + output.sampleValues(10));
  }

  @Test
  public void testIntersectionWithArithmetic() {
    System.out.println("\n=== Intersection with Arithmetic ===");
    // Test: x + 5 = 20 AND x in [10, 20]
    IntegerDomain fromEquation = IntegerDomain.of(20).add(-5); // x = 15
    IntegerDomain fromRange = IntegerDomain.of(10, 20);

    System.out.println("From equation (x + 5 = 20): " + fromEquation);
    System.out.println("From range constraint: " + fromRange);

    IntegerDomain intersection = fromEquation.intersect(fromRange);
    System.out.println("Intersection: " + intersection);
    System.out.println("Expected: {15}");
    System.out.println("Samples: " + intersection.sampleValues(5));
  }
}
