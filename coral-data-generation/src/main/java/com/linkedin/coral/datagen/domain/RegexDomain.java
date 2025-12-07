package com.linkedin.coral.datagen.domain;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;


/**
 * Represents a domain as a regular expression constraint.
 * Uses dk.brics.automaton for intersection and emptiness checking.
 */
public class RegexDomain extends Domain<String, RegexDomain> {
    private static final char[][] ALPHABET_RANGES = {
        {'0', '9'},  // digits
        {'A', 'Z'},  // uppercase
        {'a', 'z'},  // lowercase
        {' ', '/'},  // space to slash
        {':', '@'},  // colon to @ (common punctuation)
        {'[', '`'},  // [ to backtick
        {'{', '~'}   // { to tilde
    };
    private final String regex;
    private final Automaton automaton;

    /**
     * Creates a RegexDomain from a regex pattern.
     */
    public RegexDomain(String regex) {
        this.regex = regex;
        this.automaton = new RegExp(regex).toAutomaton();
    }

    /**
     * Creates a RegexDomain from an existing automaton.
     */
    private RegexDomain(Automaton automaton) {
        this.regex = automaton.toString();
        this.automaton = automaton;
    }

    /**
     * Returns the regex pattern.
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Checks if this domain is empty (no strings satisfy the constraint).
     */
    @Override
    public boolean isEmpty() {
        return automaton.isEmpty();
    }

    /**
     * Checks if this regex represents a literal string (no regex operators except anchors).
     * Anchored literals like "^50$" are still considered literals.
     */
    public boolean isLiteral() {
        // Remove anchors first, then check for special regex characters
        String withoutAnchors = regex;
        if (withoutAnchors.startsWith("^")) {
            withoutAnchors = withoutAnchors.substring(1);
        }
        if (withoutAnchors.endsWith("$")) {
            withoutAnchors = withoutAnchors.substring(0, withoutAnchors.length() - 1);
        }
        // Check if remaining pattern contains special regex characters
        return !withoutAnchors.matches(".*[.*+?{}\\[\\]()^$|\\\\].*");
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
    @Override
    public List<String> sample(int limit) {
        return sampleStrings(limit, 100);
    }

    /**
     * Checks if this domain represents exactly one string.
     */
    @Override
    public boolean isSingleton() {
        return automaton.getSingleton() != null;
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
        if (limit <= 0) return samples;

        State initial = automaton.getInitialState();
        dfsCollect(initial, "", samples, limit, maxLength, new HashSet<>());
        return samples;
    }

    private static void dfsCollect(State state, String prefix,
        List<String> samples, int limit,
        int maxLength, Set<StateWithPrefix> seen) {

        if (samples.size() >= limit || prefix.length() > maxLength) return;

        // Accept shortest valid strings immediately — no need to go deeper
        if (state.isAccept()) {
            samples.add(prefix);
            if (samples.size() >= limit) return;
            return;
        }

        StateWithPrefix key = new StateWithPrefix(state, prefix);
        if (!seen.add(key)) return;

        for (Transition t : state.getTransitions()) {
            for (char c : enumerateAllowedChars(t)) {
                dfsCollect(t.getDest(), prefix + c, samples, limit, maxLength, seen);
                if (samples.size() >= limit) return;
            }
        }
    }
    private static List<Character> enumerateAllowedChars(Transition t) {
        List<Character> result = new ArrayList<>();
        for (char[] range : ALPHABET_RANGES) {
            char start = (char) Math.max(t.getMin(), range[0]);
            char end = (char) Math.min(t.getMax(), range[1]);
            for (char c = start; c <= end; c++) result.add(c);
        }
        return result;
    }

    private static class StateWithPrefix {
        final State state;
        final String prefix;

        StateWithPrefix(State s, String p) { state = s; prefix = p; }

        public boolean equals(Object obj) {
            if (!(obj instanceof StateWithPrefix)) return false;
            StateWithPrefix o = (StateWithPrefix) obj;
            return state == o.state && prefix.equals(o.prefix);
        }

        public int hashCode() {
            return Objects.hash(state, prefix);
        }
    }
    private static class Node {
        State state;
        String value;
        Node(State state, String value) {
            this.state = state;
            this.value = value;
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
     * The pattern is fully anchored to match the exact string.
     */
    public static RegexDomain literal(String value) {
        // Escape regex special characters
        String escaped = value.replaceAll("([.+*?^$()\\[\\]{}|\\\\])", "\\\\$1");
        // Add anchors to match exact string
        return new RegexDomain("^" + escaped + "$");
    }

    @Override
    public String toString() {
        return "RegexDomain{" + regex + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexDomain that = (RegexDomain) o;
        return regex.equals(that.regex);
    }

    @Override
    public int hashCode() {
        return regex.hashCode();
    }
}
