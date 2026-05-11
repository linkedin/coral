/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;


/**
 * Represents a domain as a regular expression constraint.
 * Uses dk.brics.automaton for intersection and emptiness checking.
 */
public class RegexDomain extends Domain<String, RegexDomain> {
  private static final char[][] ALPHABET_RANGES = { { '0', '9' }, // digits
      { 'A', 'Z' }, // uppercase
      { 'a', 'z' }, // lowercase
      { ' ', '/' }, // space to slash
      { ':', '@' }, // colon to @ (common punctuation)
      { '[', '`' }, // [ to backtick
      { '{', '~' } // { to tilde
  };
  private final Automaton automaton;

  /**
   * Creates a RegexDomain from a regex pattern string.
   * The pattern uses dk.brics.automaton syntax (no ^ or $ anchors needed;
   * matching is always against the full string).
   */
  public RegexDomain(String regex) {
    this.automaton = parseRegex(regex);
  }

  /**
   * Creates a RegexDomain from an existing automaton.
   */
  public RegexDomain(Automaton automaton) {
    this.automaton = automaton;
  }

  /**
   * Returns the underlying automaton.
   */
  public Automaton getAutomaton() {
    return automaton;
  }

  /**
   * Checks if this domain is empty (no strings satisfy the constraint).
   */
  @Override
  public boolean isEmpty() {
    return automaton.isEmpty();
  }

  /**
   * Checks if this domain accepts exactly one string.
   */
  public boolean isLiteral() {
    Set<String> s = automaton.getFiniteStrings(2);
    return s != null && s.size() == 1;
  }

  /**
   * Returns the single string accepted by this domain.
   * @throws IllegalStateException if the domain is not a literal
   */
  public String getLiteralValue() {
    Set<String> s = automaton.getFiniteStrings(1);
    if (s == null || s.size() != 1) {
      throw new IllegalStateException("Not a literal domain");
    }
    return s.iterator().next();
  }

  /**
   * Intersects this domain with another, producing a refined constraint.
   * The result accepts only strings that satisfy both constraints.
   *
   * @param other the other domain to intersect with
   * @return a new RegexDomain representing the intersection
   */
  @Override
  public RegexDomain intersect(RegexDomain other) {
    Automaton intersection = this.automaton.intersection(other.automaton);
    return new RegexDomain(intersection);
  }

  /**
   * Unions this domain with another, producing a combined constraint.
   * The result accepts strings that satisfy EITHER constraint.
   *
   * @param other the other domain to union with
   * @return a new RegexDomain representing the union
   */
  @Override
  public RegexDomain union(RegexDomain other) {
    Automaton union = this.automaton.union(other.automaton);
    return new RegexDomain(union);
  }

  /**
   * Generates sample strings from this domain.
   *
   * @param limit the maximum number of samples to generate
   * @return a list of sample strings (up to 100 characters each)
   */
  private static final int DEFAULT_MAX_SAMPLE_LENGTH = 100;

  @Override
  public List<String> sample(int limit) {
    List<String> samples = sampleStrings(limit, DEFAULT_MAX_SAMPLE_LENGTH);
    if (samples.isEmpty() && !automaton.isEmpty()) {
      throw new IllegalStateException("Non-empty domain yielded zero samples (all valid strings may exceed max length "
          + DEFAULT_MAX_SAMPLE_LENGTH + ")");
    }
    return samples;
  }

  /**
   * Checks if this domain represents exactly one string.
   */
  @Override
  public boolean isSingleton() {
    return isLiteral();
  }

  /**
   * Generates sample strings that satisfy this regex domain using DFS traversal.
   * Returns the shortest valid strings up to the specified limit.
   *
   * @param limit the maximum number of sample strings to generate
   * @param maxLength the maximum length of each generated string
   * @return a list of sample strings satisfying the regex constraint
   */
  public List<String> sampleStrings(int limit, int maxLength) {
    List<String> samples = new ArrayList<>();
    if (limit <= 0)
      return samples;

    State initial = automaton.getInitialState();
    dfsCollect(initial, "", samples, limit, maxLength, new HashSet<>());
    return samples;
  }

  private static void dfsCollect(State state, String prefix, List<String> samples, int limit, int maxLength,
      Set<StateWithPrefix> seen) {

    if (samples.size() >= limit || prefix.length() > maxLength)
      return;

    // Accept shortest valid strings immediately — no need to go deeper
    if (state.isAccept()) {
      samples.add(prefix);
      if (samples.size() >= limit)
        return;
      return;
    }

    StateWithPrefix key = new StateWithPrefix(state, prefix);
    if (!seen.add(key))
      return;

    for (Transition t : state.getTransitions()) {
      for (char c : enumerateAllowedChars(t)) {
        dfsCollect(t.getDest(), prefix + c, samples, limit, maxLength, seen);
        if (samples.size() >= limit)
          return;
      }
    }
  }

  private static List<Character> enumerateAllowedChars(Transition t) {
    List<Character> result = new ArrayList<>();
    for (char[] range : ALPHABET_RANGES) {
      char start = (char) Math.max(t.getMin(), range[0]);
      char end = (char) Math.min(t.getMax(), range[1]);
      for (char c = start; c <= end; c++)
        result.add(c);
    }
    return result;
  }

  private static class StateWithPrefix {
    final State state;
    final String prefix;

    StateWithPrefix(State s, String p) {
      state = s;
      prefix = p;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof StateWithPrefix))
        return false;
      StateWithPrefix o = (StateWithPrefix) obj;
      return state == o.state && prefix.equals(o.prefix);
    }

    public int hashCode() {
      return Objects.hash(state, prefix);
    }
  }

  /**
   * Creates a domain that accepts no strings (empty/contradiction).
   */
  public static RegexDomain empty() {
    return new RegexDomain(Automaton.makeEmpty());
  }

  /**
   * Creates a domain from a literal string.
   */
  public static RegexDomain literal(String value) {
    return new RegexDomain(Automaton.makeString(value));
  }

  @Override
  public String toString() {
    if (isLiteral()) {
      return "RegexDomain{\"" + getLiteralValue() + "\"}";
    }
    return "RegexDomain{automaton}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    RegexDomain that = (RegexDomain) o;
    return this.automaton.equals(that.automaton);
  }

  @Override
  public int hashCode() {
    return automaton.hashCode();
  }

  /**
   * Parses a regex string into an automaton.
   * Strips ^ and $ anchors (brics matches whole string implicitly)
   * and translates common Java regex shorthands.
   */
  private static Automaton parseRegex(String regex) {
    String bricsRegex = regex;
    if (bricsRegex.startsWith("^")) {
      bricsRegex = bricsRegex.substring(1);
    }
    if (bricsRegex.endsWith("$")) {
      bricsRegex = bricsRegex.substring(0, bricsRegex.length() - 1);
    }
    bricsRegex = bricsRegex.replace("\\d", "[0-9]");
    bricsRegex = bricsRegex.replace("\\w", "[a-zA-Z0-9_]");
    bricsRegex = bricsRegex.replace("\\s", "[ \\t\\n\\r]");
    return new RegExp(bricsRegex).toAutomaton();
  }
}
