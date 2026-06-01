/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.ArrayList;
import java.util.List;

import dk.brics.automaton.Automaton;


/**
 * Builds a minimal automaton that accepts exactly the decimal string representations
 * of integers in a given range [lo, hi].
 *
 * <p>Uses a digit-by-digit recursive construction with two boolean flags tracking
 * whether the current prefix is still constrained by the lower/upper bound.
 * brics minimizes the result.</p>
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code build(37, 523)} accepts "37", "38", ..., "523"</li>
 *   <li>{@code build(-5, 10)} accepts "-5", "-4", ..., "0", ..., "10"</li>
 *   <li>{@code build(100, 200)} accepts "100", "101", ..., "200"</li>
 * </ul>
 */
public final class IntegerRangeAutomaton {

  private IntegerRangeAutomaton() {
  }

  /**
   * Builds an automaton accepting exactly the string representations of integers in [lo, hi].
   */
  public static Automaton build(long lo, long hi) {
    if (lo > hi) {
      return Automaton.makeEmpty();
    }
    if (lo == hi) {
      return Automaton.makeString(String.valueOf(lo));
    }

    // Split negative and non-negative ranges
    if (lo < 0 && hi < 0) {
      // All negative: "-" followed by reversed positive range [|hi|, |lo|]
      return Automaton.makeChar('-').concatenate(buildNonNegativeRange(-hi, -lo));
    }
    if (lo < 0) {
      // Mixed: union of negative part and non-negative part
      Automaton negPart = Automaton.makeChar('-').concatenate(buildNonNegativeRange(1, -lo));
      Automaton posPart = buildNonNegativeRange(0, hi);
      Automaton result = negPart.union(posPart);
      result.minimize();
      return result;
    }

    // Both non-negative
    return buildNonNegativeRange(lo, hi);
  }

  /**
   * Builds automaton for non-negative integers in [lo, hi], grouping by digit count.
   */
  private static Automaton buildNonNegativeRange(long lo, long hi) {
    Automaton result = null;

    // Handle zero separately since it has 1 digit but pow10(0)=1
    if (lo == 0) {
      result = Automaton.makeString("0");
      lo = 1;
      if (lo > hi) {
        return result;
      }
    }

    // Group by digit count: [37,523] → [37,99] ∪ [100,523]
    int loLen = digitCount(lo);
    int hiLen = digitCount(hi);

    for (int len = loLen; len <= hiLen; len++) {
      long groupLo = (len == loLen) ? lo : pow10(len - 1);
      long groupHi = (len == hiLen) ? hi : pow10(len) - 1;

      char[] loDigits = String.valueOf(groupLo).toCharArray();
      char[] hiDigits = String.valueOf(groupHi).toCharArray();

      Automaton group = buildFixedLength(loDigits, hiDigits, 0, true, true);
      result = (result == null) ? group : result.union(group);
    }

    result.minimize();
    return result;
  }

  /**
   * Core recursive builder for fixed-length digit strings in [lo, hi].
   *
   * @param lo lower bound digits
   * @param hi upper bound digits
   * @param pos current digit position (0 = most significant)
   * @param boundedBelow true if all digits so far matched lo's prefix exactly
   * @param boundedAbove true if all digits so far matched hi's prefix exactly
   */
  private static Automaton buildFixedLength(char[] lo, char[] hi, int pos, boolean boundedBelow, boolean boundedAbove) {
    int n = lo.length;

    // No more constraints — any remaining digits are valid
    if (!boundedBelow && !boundedAbove) {
      int remaining = n - pos;
      return remaining == 0 ? Automaton.makeEmptyString()
          : Automaton.makeCharRange('0', '9').repeat(remaining, remaining);
    }

    // All digits consumed — accept
    if (pos == n) {
      return Automaton.makeEmptyString();
    }

    char minD = boundedBelow ? lo[pos] : '0';
    char maxD = boundedAbove ? hi[pos] : '9';

    // Same digit: emit it, recurse with same constraint flags
    if (minD == maxD) {
      return Automaton.makeChar(minD).concatenate(buildFixedLength(lo, hi, pos + 1, boundedBelow, boundedAbove));
    }

    List<Automaton> parts = new ArrayList<>();

    // Bottom edge: digit = minD, still bounded below, freed above
    parts.add(Automaton.makeChar(minD).concatenate(buildFixedLength(lo, hi, pos + 1, boundedBelow, false)));

    // Middle band: digits strictly between minD and maxD, freed from both bounds
    if (maxD - minD > 1) {
      Automaton mid = Automaton.makeCharRange((char) (minD + 1), (char) (maxD - 1));
      int remaining = n - pos - 1;
      Automaton tail =
          remaining == 0 ? Automaton.makeEmptyString() : Automaton.makeCharRange('0', '9').repeat(remaining, remaining);
      parts.add(mid.concatenate(tail));
    }

    // Top edge: digit = maxD, freed below, still bounded above
    parts.add(Automaton.makeChar(maxD).concatenate(buildFixedLength(lo, hi, pos + 1, false, boundedAbove)));

    Automaton result = parts.get(0);
    for (int i = 1; i < parts.size(); i++) {
      result = result.union(parts.get(i));
    }
    return result;
  }

  private static int digitCount(long n) {
    if (n == 0) {
      return 1;
    }
    return String.valueOf(n).length();
  }

  private static long pow10(int n) {
    long result = 1;
    for (int i = 0; i < n; i++) {
      result *= 10;
    }
    return result;
  }
}
