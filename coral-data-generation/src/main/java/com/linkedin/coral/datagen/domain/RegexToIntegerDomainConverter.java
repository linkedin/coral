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
   * Uses BFS to find the shortest accepted string (which for digit-only strings
   * with no leading zeros gives the smallest number). Falls back to DFS for correctness
   * with leading zeros.
   */
  private long computeMin(Automaton a) {
    // For digit-only finite automata, enumerate up to a reasonable limit
    // to find the true numeric minimum
    Set<String> strings = a.getFiniteStrings(MAX_ENUMERATION_SIZE);
    if (strings != null) {
      long min = Long.MAX_VALUE;
      for (String s : strings) {
        min = Math.min(min, Long.parseLong(s));
      }
      return min;
    }
    // Fallback: DFS choosing shortest path with smallest digits
    return Long.parseLong(dfsMinString(a.getInitialState(), new HashMap<>()));
  }

  /**
   * Computes the maximum numeric value accepted by the automaton.
   * Uses DFS over the acyclic automaton choosing longest paths with largest digits.
   */
  private long computeMax(Automaton a) {
    Set<String> strings = a.getFiniteStrings(MAX_ENUMERATION_SIZE);
    if (strings != null) {
      long max = Long.MIN_VALUE;
      for (String s : strings) {
        max = Math.max(max, Long.parseLong(s));
      }
      return max;
    }
    // Fallback: DFS choosing longest path with largest digits
    return Long.parseLong(dfsMaxString(a.getInitialState(), new HashMap<>()));
  }

  private String dfsMinString(State state, Map<State, String> memo) {
    if (memo.containsKey(state)) {
      return memo.get(state);
    }
    String best = state.isAccept() ? "" : null;
    for (Transition t : state.getTransitions()) {
      String sub = dfsMinString(t.getDest(), memo);
      if (sub != null) {
        String candidate = t.getMin() + sub; // smallest char in range
        if (best == null || Long.parseLong(candidate) < Long.parseLong(best)) {
          best = candidate;
        }
      }
    }
    memo.put(state, best);
    return best;
  }

  private String dfsMaxString(State state, Map<State, String> memo) {
    if (memo.containsKey(state)) {
      return memo.get(state);
    }
    String best = state.isAccept() ? "" : null;
    for (Transition t : state.getTransitions()) {
      String sub = dfsMaxString(t.getDest(), memo);
      if (sub != null) {
        String candidate = t.getMax() + sub; // largest char in range
        if (best == null || Long.parseLong(candidate) > Long.parseLong(best)) {
          best = candidate;
        }
      }
    }
    memo.put(state, best);
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
