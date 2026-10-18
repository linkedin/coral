/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.*;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;


/**
 * Converts RegexDomain patterns to IntegerDomain when the regex represents
 * only bounded numeric patterns.
 *
 * Conversion is allowed only if the automaton:
 * 1. Accepts a finite language
 * 2. All transitions are over digit characters [0-9] only
 *
 * Examples:
 * - {@code /^2024$/} converts to {@code [2024, 2024]}
 * - {@code /^[0-9]{3}$/} converts to {@code [0, 999]}
 * - {@code /^19[0-9]{2}$/} converts to {@code [1900, 1999]}
 * - {@code /^(10|11|12)$/} converts to {@code [10, 10], [11, 11], [12, 12]}
 */
public class RegexToIntegerDomainConverter {

  private static final int MAX_ENUMERATION_SIZE = 5000;

  /**
   * Checks if the given RegexDomain can be converted to IntegerDomain.
   *
   * @param regexDomain the regex domain to check
   * @return true if convertible, false otherwise
   */
  public boolean isConvertible(RegexDomain regexDomain) {
    Automaton a = regexDomain.getAutomaton();
    return !a.isEmpty() && a.isFinite() && isDigitOnly(a);
  }

  /**
   * Converts the given RegexDomain to IntegerDomain.
   *
   * @param regexDomain the regex domain to convert
   * @return the equivalent IntegerDomain
   * @throws NonConvertibleDomainException if the regex cannot be converted
   */
  public IntegerDomain convert(RegexDomain regexDomain) {
    Automaton a = regexDomain.getAutomaton();

    if (a.isEmpty()) {
      throw new NonConvertibleDomainException("Empty automaton");
    }
    if (!a.isFinite()) {
      throw new NonConvertibleDomainException("Infinite language");
    }
    if (!isDigitOnly(a)) {
      throw new NonConvertibleDomainException("Non-digit characters in automaton");
    }

    // Try enumeration first
    Set<String> strings = a.getFiniteStrings(MAX_ENUMERATION_SIZE);
    if (strings != null) {
      // Small domain: enumerate all values
      Set<Long> values = new TreeSet<>();
      for (String s : strings) {
        if (!s.isEmpty()) {
          values.add(Long.parseLong(s));
        }
      }
      return createIntegerDomainFromValues(values);
    } else {
      // Large domain: compute min/max bounds
      long min = computeMin(a);
      long max = computeMax(a);
      return IntegerDomain.of(Collections.singletonList(new IntegerDomain.Interval(min, max)));
    }
  }

  /**
   * Checks that all transitions in the automaton are over digit characters only.
   */
  private boolean isDigitOnly(Automaton a) {
    for (State s : a.getStates()) {
      for (Transition t : s.getTransitions()) {
        if (t.getMin() < '0' || t.getMax() > '9') {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Computes the minimum numeric value accepted by the automaton.
   *
   * <p>Accepted strings may have different lengths and leading zeros, so the smallest number is not
   * necessarily on the shortest path. For a fixed length, however, lexicographic order of digit
   * strings matches numeric order. The minimum is therefore the smallest value among the
   * lexicographically smallest accepted string of each length.
   */
  private long computeMin(Automaton a) {
    return computeBound(a, true);
  }

  /**
   * Computes the maximum numeric value accepted by the automaton. See {@link #computeMin(Automaton)}.
   */
  private long computeMax(Automaton a) {
    return computeBound(a, false);
  }

  private long computeBound(Automaton a, boolean min) {
    // In a finite language no accepted path repeats a state, so lengths are bounded by the state count.
    int maxLength = a.getNumberOfStates();
    Map<State, Map<Integer, Optional<String>>> memo = new HashMap<>();
    Long bound = null;
    for (int length = 1; length <= maxLength; length++) {
      String s = extremeString(a.getInitialState(), length, min, memo);
      if (s != null) {
        long value = Long.parseLong(s);
        if (bound == null || (min ? value < bound : value > bound)) {
          bound = value;
        }
      }
    }
    return bound;
  }

  /**
   * Returns the lexicographically smallest (or largest) string of exactly {@code length} digits
   * accepted from {@code state}, or null if there is none.
   */
  private String extremeString(State state, int length, boolean smallest,
      Map<State, Map<Integer, Optional<String>>> memo) {
    if (length == 0) {
      return state.isAccept() ? "" : null;
    }
    Map<Integer, Optional<String>> byLength = memo.computeIfAbsent(state, k -> new HashMap<>());
    Optional<String> cached = byLength.get(length);
    if (cached != null) {
      return cached.orElse(null);
    }
    String best = null;
    for (Transition t : state.getTransitions()) {
      String sub = extremeString(t.getDest(), length - 1, smallest, memo);
      if (sub != null) {
        String candidate = (smallest ? t.getMin() : t.getMax()) + sub;
        if (best == null || (smallest ? candidate.compareTo(best) < 0 : candidate.compareTo(best) > 0)) {
          best = candidate;
        }
      }
    }
    byLength.put(length, Optional.ofNullable(best));
    return best;
  }

  /**
   * Creates IntegerDomain from enumerated values by merging contiguous values into intervals.
   */
  private IntegerDomain createIntegerDomainFromValues(Set<Long> values) {
    if (values.isEmpty()) {
      return IntegerDomain.empty();
    }

    List<Long> sortedValues = new ArrayList<>(values);
    Collections.sort(sortedValues);

    List<IntegerDomain.Interval> intervals = new ArrayList<>();
    long rangeStart = sortedValues.get(0);
    long rangeEnd = sortedValues.get(0);

    for (int i = 1; i < sortedValues.size(); i++) {
      long val = sortedValues.get(i);
      if (val == rangeEnd + 1) {
        rangeEnd = val;
      } else {
        intervals.add(new IntegerDomain.Interval(rangeStart, rangeEnd));
        rangeStart = val;
        rangeEnd = val;
      }
    }
    intervals.add(new IntegerDomain.Interval(rangeStart, rangeEnd));

    return IntegerDomain.of(intervals);
  }
}
