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
 * Converts a {@link RegexDomain} of canonical-decimal integer strings into the corresponding
 * {@link IntegerDomain}.
 *
 * <p>This converter intentionally answers only one question: <i>"Given a regex whose accepted
 * strings are all canonical decimal representations of integers, what is the integer set?"</i>.
 * Canonical decimal means the form produced by {@code String.valueOf(int)} / SQL
 * {@code CAST(integer AS VARCHAR)}: {@code "0"} or a non-zero leading digit followed by
 * additional digits. Strings like {@code "000"}, {@code "01"}, {@code "-0"} are non-canonical
 * and any regex that admits them is rejected.
 *
 * <p>Allowed:
 * <ul>
 *   <li>{@code /^2024$/} → {@code [2024, 2024]}</li>
 *   <li>{@code /^19[0-9]{2}$/} → {@code [1900, 1999]}</li>
 *   <li>{@code /^(10|11|12)$/} → {@code [10, 12]}</li>
 *   <li>{@code /^[0-9]$/} → {@code [0, 9]} (every accepted string is canonical)</li>
 * </ul>
 *
 * <p>Rejected (throws {@link NonConvertibleDomainException}):
 * <ul>
 *   <li>{@code /^[0-9]{3}$/} — admits {@code "000"}, {@code "001"}, … which are not canonical</li>
 *   <li>{@code /^009$/} — non-canonical literal</li>
 *   <li>{@code /^[0-9]+$/} — infinite language</li>
 *   <li>{@code /^abc$/} — not a subset of canonical integer strings</li>
 * </ul>
 *
 * <p>If a caller has a non-canonical regex but wants the canonical inverse, it must intersect
 * with the canonical-strings regex first; the converter will then accept the result.
 */
public class RegexToIntegerDomainConverter {

  private static final int MAX_ENUMERATION_SIZE = 5000;

  /**
   * Automaton accepting exactly the canonical decimal string form of every non-negative
   * integer ({@code "0"}, {@code "1"}, {@code "2"}, …, {@code "10"}, …). This is the precondition
   * the converter requires its inputs to satisfy.
   */
  private static final Automaton CANONICAL_INTEGER_STRINGS = new RegexDomain("^(0|[1-9][0-9]*)$").getAutomaton();

  /**
   * Checks if the given RegexDomain can be converted to IntegerDomain.
   *
   * @param regexDomain the regex domain to check
   * @return true if convertible, false otherwise
   */
  public boolean isConvertible(RegexDomain regexDomain) {
    Automaton a = regexDomain.getAutomaton();
    return !a.isEmpty() && a.isFinite() && a.subsetOf(CANONICAL_INTEGER_STRINGS);
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
    if (!a.subsetOf(CANONICAL_INTEGER_STRINGS)) {
      throw new NonConvertibleDomainException("Regex accepts strings outside canonical decimal form");
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
