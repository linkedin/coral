/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Tests demonstrating the unified Domain API across IntegerDomain and RegexDomain.
 */
public class DomainUnificationTest {

  // ==================== Per-Domain API ====================

  @Test
  public void testIntegerDomainUnifiedApi() {
    IntegerDomain domain1 = IntegerDomain.of(1, 10);
    IntegerDomain domain2 = IntegerDomain.of(5, 15);

    assertFalse(domain1.isEmpty());

    // Test intersect
    IntegerDomain intersection = domain1.intersect(domain2);
    assertFalse(intersection.isEmpty());
    assertTrue(intersection.contains(5));
    assertTrue(intersection.contains(10));
    assertFalse(intersection.contains(4));
    assertFalse(intersection.contains(11));

    // Test union
    IntegerDomain union = domain1.union(domain2);
    assertTrue(union.contains(1));
    assertTrue(union.contains(15));

    // Test sample (unified API)
    List<Long> samples = intersection.sample(5);
    assertEquals(samples.size(), 5);
    for (long v : samples) {
      assertTrue(intersection.contains(v));
    }

    // Test isSingleton
    IntegerDomain singleton = IntegerDomain.of(42);
    assertTrue(singleton.isSingleton());
    assertFalse(domain1.isSingleton());
  }

  @Test
  public void testRegexDomainUnifiedApi() {
    RegexDomain domain1 = new RegexDomain("a.*"); // starts with 'a'
    RegexDomain domain2 = new RegexDomain(".*b"); // ends with 'b'

    assertFalse(domain1.isEmpty());
    assertFalse(domain2.isEmpty());

    // Test intersect
    RegexDomain intersection = domain1.intersect(domain2);
    assertFalse(intersection.isEmpty());

    // Test union
    RegexDomain union = domain1.union(domain2);
    assertFalse(union.isEmpty());

    // Test sample (unified API)
    List<String> samples = intersection.sample(5);
    assertFalse(samples.isEmpty());

    // Test isSingleton
    RegexDomain singleton = RegexDomain.literal("hello");
    assertTrue(singleton.isSingleton());
    assertFalse(domain1.isSingleton());
  }

  // ==================== Empty and Contradiction ====================

  @Test
  public void testEmptyDomains() {
    // Integer empty domain
    IntegerDomain intEmpty = IntegerDomain.empty();
    assertTrue(intEmpty.isEmpty());
    assertTrue(intEmpty.sample(5).isEmpty());

    // Regex empty domain
    RegexDomain regexEmpty = RegexDomain.empty();
    assertTrue(regexEmpty.isEmpty());
    assertTrue(regexEmpty.sample(5).isEmpty());
  }

  @Test
  public void testIntersectionBecomesEmpty() {
    // Integer: disjoint intervals
    IntegerDomain int1 = IntegerDomain.of(1, 10);
    IntegerDomain int2 = IntegerDomain.of(20, 30);
    IntegerDomain intIntersection = int1.intersect(int2);
    assertTrue(intIntersection.isEmpty());

    // Regex: contradictory patterns
    RegexDomain regex1 = new RegexDomain("a+"); // one or more 'a'
    RegexDomain regex2 = new RegexDomain("b+"); // one or more 'b'
    RegexDomain regexIntersection = regex1.intersect(regex2);
    assertTrue(regexIntersection.isEmpty());
  }

  // ==================== Polymorphic and Chained Usage ====================

  @Test
  public void testPolymorphicUsage() {
    Domain<Long, IntegerDomain> intDomain = IntegerDomain.of(1, 10);
    Domain<String, RegexDomain> strDomain = new RegexDomain("[0-9]+");

    assertFalse(intDomain.isEmpty());
    assertFalse(strDomain.isEmpty());

    List<Long> intSamples = intDomain.sample(3);
    assertEquals(intSamples.size(), 3);

    List<String> strSamples = strDomain.sample(3);
    assertFalse(strSamples.isEmpty());
  }

  @Test
  public void testChainedOperations() {
    // Integer: chain intersections
    IntegerDomain result =
        IntegerDomain.of(1, 100).intersect(IntegerDomain.of(10, 50)).intersect(IntegerDomain.of(20, 30));

    assertFalse(result.isEmpty());
    assertTrue(result.contains(20));
    assertTrue(result.contains(30));
    assertFalse(result.contains(19));
    assertFalse(result.contains(31));

    List<Long> samples = result.sample(5);
    assertEquals(samples.size(), 5);
    for (long v : samples) {
      assertTrue(result.contains(v));
    }

    // Regex: chain operations
    RegexDomain regexResult =
        new RegexDomain(".*").intersect(new RegexDomain("[a-z]+")).intersect(new RegexDomain("a.*"));
    assertFalse(regexResult.isEmpty());

    List<String> regexSamples = regexResult.sample(5);
    assertFalse(regexSamples.isEmpty());
    for (String s : regexSamples) {
      assertTrue(s.startsWith("a"), "Sample should start with 'a': " + s);
      assertTrue(s.matches("[a-z]+"), "Sample should be lowercase letters: " + s);
    }
  }

  @Test
  public void testSingletonDetection() {
    // Integer singletons
    IntegerDomain intSingle = IntegerDomain.of(42);
    IntegerDomain intRange = IntegerDomain.of(1, 10);
    assertTrue(intSingle.isSingleton());
    assertFalse(intRange.isSingleton());

    // Regex singletons
    RegexDomain regexSingle = RegexDomain.literal("hello");
    RegexDomain regexPattern = new RegexDomain("hel+o");
    assertTrue(regexSingle.isSingleton());
    assertFalse(regexPattern.isSingleton());
  }
}
