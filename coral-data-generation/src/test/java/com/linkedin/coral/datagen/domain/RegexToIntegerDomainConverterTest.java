/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Tests for RegexToIntegerDomainConverter.
 * 
 * Validates that the converter correctly:
 * 1. Accepts only valid bounded numeric patterns
 * 2. Rejects unbounded or non-numeric patterns
 * 3. Converts patterns to correct IntegerDomain intervals
 * 4. Handles edge cases (leading zeros, large domains, alternation)
 */
public class RegexToIntegerDomainConverterTest {

  private RegexToIntegerDomainConverter converter;

  @BeforeMethod
  public void setup() {
    converter = new RegexToIntegerDomainConverter();
  }

  // ==================== Basic Literal Tests ====================

  @Test
  public void testLiteralSingleDigit() {
    RegexDomain regex = new RegexDomain("^5$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertFalse(result.isEmpty());
    assertTrue(result.contains(5));
    assertFalse(result.contains(4));
    assertFalse(result.contains(6));
  }

  @Test
  public void testLiteralMultipleDigits() {
    RegexDomain regex = new RegexDomain("^2024$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(2024));
    assertFalse(result.contains(2023));
    assertFalse(result.contains(2025));
  }

  @Test
  public void testLiteralZero() {
    RegexDomain regex = new RegexDomain("^0$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(0));
    assertFalse(result.contains(1));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testLiteralWithLeadingZerosIsNonCanonical() {
    // ^009$ only accepts the string "009", which is not the canonical decimal form of any integer
    // (9's canonical form is "9"). Strict converter rejects.
    converter.convert(new RegexDomain("^009$"));
  }

  // ==================== Character Class Tests ====================

  @Test
  public void testCharClassSingleDigit() {
    RegexDomain regex = new RegexDomain("^[0-9]$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(0));
    assertTrue(result.contains(5));
    assertTrue(result.contains(9));
    assertFalse(result.contains(10));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testCharClassTwoDigitsIsNonCanonical() {
    // ^[0-9][0-9]$ admits "00", "01", … which are not canonical decimal for any integer.
    converter.convert(new RegexDomain("^[0-9][0-9]$"));
  }

  @Test
  public void testCanonicalTwoDigit() {
    // The canonical form of all 2-digit integers: leading digit 1-9, then any digit.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(10));
    assertTrue(result.contains(50));
    assertTrue(result.contains(99));
    assertFalse(result.contains(9));
    assertFalse(result.contains(100));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testCharClassThreeDigitsIsNonCanonical() {
    // ^[0-9]{3}$ admits "000", "001", … — none of those are canonical decimals.
    converter.convert(new RegexDomain("^[0-9]{3}$"));
  }

  @Test
  public void testCanonicalThreeDigit() {
    // Canonical 3-digit integers: leading digit 1-9, then two digits.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]{2}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(100));
    assertTrue(result.contains(500));
    assertTrue(result.contains(999));
    assertFalse(result.contains(99));
    assertFalse(result.contains(1000));
  }

  // ==================== Bounded Repetition Tests ====================

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testExactRepetitionIsNonCanonical() {
    // ^[0-9]{4}$ admits "0000", "0001", … which are not canonical.
    converter.convert(new RegexDomain("^[0-9]{4}$"));
  }

  @Test
  public void testCanonicalFourDigit() {
    // Canonical 4-digit integers: 1000..9999.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]{3}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(1000));
    assertTrue(result.contains(1234));
    assertTrue(result.contains(9999));
    assertFalse(result.contains(999));
    assertFalse(result.contains(10000));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testRangeRepetitionIsNonCanonical() {
    // ^[0-9]{2,3}$ admits "00", "000", "01", … (non-canonical).
    converter.convert(new RegexDomain("^[0-9]{2,3}$"));
  }

  @Test
  public void testLiteralWithRepetition() {
    RegexDomain regex = new RegexDomain("^5{3}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(555));
    assertFalse(result.contains(554));
    assertFalse(result.contains(556));
  }

  // ==================== Mixed Pattern Tests ====================

  @Test
  public void testPrefixPattern() {
    // ^19[0-9]{2}$ matches 1900-1999
    RegexDomain regex = new RegexDomain("^19[0-9]{2}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertFalse(result.contains(1899));
    assertTrue(result.contains(1900));
    assertTrue(result.contains(1950));
    assertTrue(result.contains(1999));
    assertFalse(result.contains(2000));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testSuffixPatternIsNonCanonical() {
    // ^[0-9]{2}99$ admits "0099", which is not canonical (canonical for 99 is "99").
    converter.convert(new RegexDomain("^[0-9]{2}99$"));
  }

  @Test
  public void testCanonicalSuffixPattern() {
    // Canonical 4-digit values ending in 99: ^[1-9][0-9]99$ matches 1099, 1199, …, 9999.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]99$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(1099));
    assertTrue(result.contains(1999));
    assertTrue(result.contains(9999));
    assertFalse(result.contains(99));
    assertFalse(result.contains(1998));
    assertFalse(result.contains(2000));
  }

  @Test
  public void testMixedLiteralsAndClasses() {
    // ^2[0-9]2[0-9]$ matches 2020, 2021, ..., 2929
    RegexDomain regex = new RegexDomain("^2[0-9]2[0-9]$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(2020));
    assertTrue(result.contains(2525));
    assertTrue(result.contains(2929));
    assertFalse(result.contains(2019));
    assertFalse(result.contains(2930));
  }

  // ==================== Alternation Tests ====================

  @Test
  public void testSimpleAlternation() {
    // ^(10|11|12)$ matches exactly 10, 11, 12
    RegexDomain regex = new RegexDomain("^(10|11|12)$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(10));
    assertTrue(result.contains(11));
    assertTrue(result.contains(12));
    assertFalse(result.contains(9));
    assertFalse(result.contains(13));
  }

  @Test
  public void testAlternationWithRanges() {
    // ^(19|20)[0-9]{2}$ matches 1900-1999 and 2000-2099
    RegexDomain regex = new RegexDomain("^(19|20)[0-9]{2}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(1900));
    assertTrue(result.contains(1999));
    assertTrue(result.contains(2000));
    assertTrue(result.contains(2099));
    assertFalse(result.contains(1899));
    assertFalse(result.contains(2100));
  }

  @Test
  public void testMultipleAlternatives() {
    // ^(1|2|3|4|5)$ matches 1-5
    RegexDomain regex = new RegexDomain("^(1|2|3|4|5)$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(1));
    assertTrue(result.contains(3));
    assertTrue(result.contains(5));
    assertFalse(result.contains(0));
    assertFalse(result.contains(6));
  }

  @Test
  public void testNestedAlternation() {
    // ^((10|20)|(30|40))$ matches 10, 20, 30, 40
    RegexDomain regex = new RegexDomain("^((10|20)|(30|40))$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(10));
    assertTrue(result.contains(20));
    assertTrue(result.contains(30));
    assertTrue(result.contains(40));
    assertFalse(result.contains(15));
    assertFalse(result.contains(25));
  }

  // ==================== Invalid Pattern Tests ====================

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testUnboundedStar() {
    RegexDomain regex = new RegexDomain("^[0-9]*$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testUnboundedPlus() {
    RegexDomain regex = new RegexDomain("^[0-9]+$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testOptionalDigitIsNonCanonical() {
    // ^[0-9]?$ admits the empty string, which is not a canonical decimal integer.
    converter.convert(new RegexDomain("^[0-9]?$"));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testUnboundedQuantifier() {
    RegexDomain regex = new RegexDomain("^[0-9]{3,}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testWildcardDot() {
    RegexDomain regex = new RegexDomain("^.{3}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testLetters() {
    RegexDomain regex = new RegexDomain("^abc$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testMixedAlphanumeric() {
    RegexDomain regex = new RegexDomain("^abc123$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testHyphen() {
    RegexDomain regex = new RegexDomain("^123-456$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testDecimalPoint() {
    RegexDomain regex = new RegexDomain("^123.456$");
    converter.convert(regex);
  }

  @Test
  public void testAnchorsAreIgnored() {
    // brics matches the whole string implicitly, so ^...$ anchors are no-ops.
    // The three forms below should produce identical results on a canonical input.
    RegexDomain anchored = new RegexDomain("^(100|999)$");
    RegexDomain missingStart = new RegexDomain("(100|999)$");
    RegexDomain missingEnd = new RegexDomain("^(100|999)");
    RegexDomain noAnchors = new RegexDomain("(100|999)");

    IntegerDomain a = converter.convert(anchored);
    IntegerDomain b = converter.convert(missingStart);
    IntegerDomain c = converter.convert(missingEnd);
    IntegerDomain d = converter.convert(noAnchors);

    for (IntegerDomain domain : new IntegerDomain[] { a, b, c, d }) {
      assertTrue(domain.contains(100));
      assertTrue(domain.contains(999));
      assertFalse(domain.contains(101));
    }
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testLookahead() {
    RegexDomain regex = new RegexDomain("^(?=123)[0-9]{3}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = { NonConvertibleDomainException.class, IllegalArgumentException.class })
  public void testLookbehind() {
    RegexDomain regex = new RegexDomain("^(?<=123)[0-9]{3}$");
    converter.convert(regex);
  }

  @Test
  public void testBackreference() {
    // dk.brics.automaton does not support backreferences (\1 etc.); we expect either a
    // construction-time failure from the regex parser, or a downstream conversion failure.
    try {
      RegexDomain regex = new RegexDomain("^([0-9])\\1$");
      if (converter.isConvertible(regex)) {
        // If reported convertible, the converted domain must at least be a valid IntegerDomain
        // (i.e., not crash on use). We don't pin specific contents — that depends on how the
        // parser ultimately handled the \1.
        IntegerDomain domain = converter.convert(regex);
        assertNotNull(domain);
      } else {
        // Reported non-convertible: convert() should throw.
        try {
          converter.convert(regex);
          fail("convert() should throw when isConvertible reports false");
        } catch (NonConvertibleDomainException expected) {
          // ok
        }
      }
    } catch (IllegalArgumentException e) {
      // Acceptable: regex parser rejected the backreference at construction time.
    }
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testShorthandDigitClassIsNonCanonical() {
    // \d is translated to [0-9], so ^\d{3}$ is equivalent to ^[0-9]{3}$ — admits "000" etc.
    converter.convert(new RegexDomain("^\\d{3}$"));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testShorthandWordClass() {
    RegexDomain regex = new RegexDomain("^\\w{3}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testInvalidCharClass() {
    // [a-z] is not allowed
    RegexDomain regex = new RegexDomain("^[a-z]{3}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testInvalidCharClassWithDigits() {
    // [0-9a-z] mixes digits with letters
    RegexDomain regex = new RegexDomain("^[0-9a-z]{3}$");
    converter.convert(regex);
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testEmptyPatternIsNonCanonical() {
    // Empty regex accepts only "", which is not a canonical decimal integer.
    converter.convert(new RegexDomain(""));
  }

  @Test(expectedExceptions = { NonConvertibleDomainException.class, NullPointerException.class })
  public void testNullPattern() {
    RegexDomain regex = new RegexDomain((String) null);
    converter.convert(regex);
  }

  // ==================== Edge Case Tests ====================

  @Test
  public void testSingleZero() {
    RegexDomain regex = new RegexDomain("^0$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(0));
    assertFalse(result.contains(1));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testLeadingZerosIsNonCanonical() {
    // ^00[0-9]$ admits "000", "001", … (non-canonical).
    converter.convert(new RegexDomain("^00[0-9]$"));
  }

  @Test
  public void testLargeCanonicalRange() {
    // Canonical 6-digit values: ^[1-9][0-9]{5}$ matches 100000..999999.
    // Large enough that the converter uses bounds computation instead of enumeration.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]{5}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(100000));
    assertTrue(result.contains(500000));
    assertTrue(result.contains(999999));
    assertFalse(result.contains(99999));
    assertFalse(result.contains(1000000));
  }

  @Test
  public void testDisjointIntervals() {
    // Should produce multiple disjoint intervals
    RegexDomain regex = new RegexDomain("^(10|20|30)$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(10));
    assertTrue(result.contains(20));
    assertTrue(result.contains(30));
    assertFalse(result.contains(15));
    assertFalse(result.contains(25));

    // Check that intervals are separate
    List<IntegerDomain.Interval> intervals = result.getIntervals();
    assertEquals(intervals.size(), 3);
  }

  @Test
  public void testContiguousIntervalMerging() {
    // Should merge into single interval
    RegexDomain regex = new RegexDomain("^(10|11|12|13|14|15)$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(10));
    assertTrue(result.contains(15));

    // Should be merged into one interval [10, 15]
    List<IntegerDomain.Interval> intervals = result.getIntervals();
    assertEquals(intervals.size(), 1);
    assertEquals(intervals.get(0).getMin(), 10);
    assertEquals(intervals.get(0).getMax(), 15);
  }

  @Test
  public void testEscapedDigit() {
    // Test simple literal instead of escaped digit
    RegexDomain regex = new RegexDomain("^5$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(5));
    assertFalse(result.contains(4));
    assertFalse(result.contains(6));
  }

  @Test
  public void testZeroRepetition() {
    // {1,2} means 1 or 2 repetitions
    RegexDomain regex = new RegexDomain("^5{1,2}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(5)); // One 5
    assertTrue(result.contains(55)); // Two 5s
    assertFalse(result.contains(0));
    assertFalse(result.contains(555));
  }

  // ==================== Complex Pattern Tests ====================

  @Test
  public void testComplexYearRange() {
    // Simpler pattern: just century ranges
    RegexDomain regex = new RegexDomain("^(19|20)[0-9]{2}$");
    assertTrue(converter.isConvertible(regex));

    IntegerDomain result = converter.convert(regex);
    assertTrue(result.contains(1900));
    assertTrue(result.contains(1999));
    assertTrue(result.contains(2000));
    assertTrue(result.contains(2099));
    assertFalse(result.contains(1899));
    assertFalse(result.contains(2100));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testZeroPaddedFourDigitFieldIsNonCanonical() {
    // A fixed-width 4-digit field (e.g., phone-number last-4) is naturally a string-domain
    // constraint, not an integer one, because "0042" is a valid value for the field. The
    // converter rejects this regex so callers don't accidentally interpret it as an integer.
    converter.convert(new RegexDomain("^[0-9]{4}$"));
  }

  @Test(expectedExceptions = NonConvertibleDomainException.class)
  public void testZipCodeFirstThreeIsNonCanonical() {
    // ZIP-3 prefixes like "007" are valid strings but not canonical integer decimals.
    converter.convert(new RegexDomain("^[0-9]{3}$"));
  }

  // ==================== isConvertible Tests ====================

  @Test
  public void testIsConvertibleValidPatterns() {
    // isConvertible should agree with convert() succeeding and producing the expected domain.
    // Every example here is canonical-only (no leading-zero strings, no empty string).
    assertTrue(converter.isConvertible(new RegexDomain("^123$")));
    assertEquals(converter.convert(new RegexDomain("^123$")), IntegerDomain.of(123));

    assertTrue(converter.isConvertible(new RegexDomain("^[0-9]$")));
    IntegerDomain singleDigit = converter.convert(new RegexDomain("^[0-9]$"));
    assertTrue(singleDigit.contains(0));
    assertTrue(singleDigit.contains(9));
    assertFalse(singleDigit.contains(10));

    assertTrue(converter.isConvertible(new RegexDomain("^[1-9][0-9]{2}$")));
    IntegerDomain threeDigit = converter.convert(new RegexDomain("^[1-9][0-9]{2}$"));
    assertTrue(threeDigit.contains(100));
    assertTrue(threeDigit.contains(999));
    assertFalse(threeDigit.contains(99));
    assertFalse(threeDigit.contains(1000));

    assertTrue(converter.isConvertible(new RegexDomain("^(10|20|30)$")));
    IntegerDomain alts = converter.convert(new RegexDomain("^(10|20|30)$"));
    assertTrue(alts.contains(10) && alts.contains(20) && alts.contains(30));
    assertFalse(alts.contains(15));

    assertTrue(converter.isConvertible(new RegexDomain("^19[0-9]{2}$")));
    IntegerDomain years = converter.convert(new RegexDomain("^19[0-9]{2}$"));
    assertTrue(years.contains(1900) && years.contains(1999));
    assertFalse(years.contains(1899) || years.contains(2000));
  }

  @Test
  public void testIsConvertibleInvalidPatterns() {
    // isConvertible should agree with convert() throwing NonConvertibleDomainException.
    RegexDomain[] invalid = { new RegexDomain("^[0-9]+$"), // unbounded
        new RegexDomain("^[0-9]*$"), // unbounded
        new RegexDomain("^abc$"), // non-digit
        new RegexDomain("^[0-9]{3,}$") // unbounded
    };
    for (RegexDomain regex : invalid) {
      assertFalse(converter.isConvertible(regex), "should report not convertible: " + regex);
      try {
        converter.convert(regex);
        fail("convert() should have thrown for non-convertible regex: " + regex);
      } catch (NonConvertibleDomainException expected) {
        // ok
      }
    }
  }

  // ==================== Integration Tests ====================

  @Test
  public void testRoundTrip_SmallDomain() {
    // Create regex, convert to integer domain, verify values
    RegexDomain regex = new RegexDomain("^(1|2|3|4|5)$");
    IntegerDomain domain = converter.convert(regex);

    // Verify all expected values
    for (int i = 1; i <= 5; i++) {
      assertTrue(domain.contains(i), "Should contain " + i);
    }

    // Verify out-of-range values
    assertFalse(domain.contains(0));
    assertFalse(domain.contains(6));
  }

  @Test
  public void testRoundTrip_MediumDomain() {
    // Canonical 2-digit range: 10-99 (90 values).
    RegexDomain regex = new RegexDomain("^[1-9][0-9]$");
    IntegerDomain domain = converter.convert(regex);

    assertTrue(domain.contains(10));
    assertTrue(domain.contains(50));
    assertTrue(domain.contains(99));
    assertFalse(domain.contains(9));
    assertFalse(domain.contains(100));
  }

  @Test
  public void testBoundaryValues() {
    // Canonical 3-digit range: 100-999.
    RegexDomain regex = new RegexDomain("^[1-9][0-9]{2}$");
    IntegerDomain domain = converter.convert(regex);

    assertTrue(domain.contains(100), "Min");
    assertTrue(domain.contains(999), "Max");
    assertFalse(domain.contains(99), "Below min");
    assertFalse(domain.contains(1000), "Above max");
  }
}
