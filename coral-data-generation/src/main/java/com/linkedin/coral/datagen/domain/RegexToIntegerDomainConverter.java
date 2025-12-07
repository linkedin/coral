/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Converts RegexDomain patterns to IntegerDomain when the regex represents
 * only bounded numeric patterns.
 * 
 * Conversion is allowed only if the regex satisfies:
 * 1. Only digits [0-9], grouping (), alternation |, and bounded repetition {n} or {n,m}
 * 2. Anchors ^ and $ must fully enclose the expression
 * 3. No unbounded repetition (*, +, ?, {n,}, .*, .+)
 * 4. No non-digit literals (-, ., /, letters, spaces, symbols)
 * 5. Represented numeric space must be finite and bounded
 * 6. No lookaheads, lookbehinds, backreferences, or shorthand classes
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
    try {
      String pattern = regexDomain.getRegex();
      validateAndParse(pattern);
      return true;
    } catch (NonConvertibleDomainException e) {
      return false;
    }
  }

  /**
   * Converts the given RegexDomain to IntegerDomain.
   * 
   * @param regexDomain the regex domain to convert
   * @return the equivalent IntegerDomain
   * @throws NonConvertibleDomainException if the regex cannot be converted
   */
  public IntegerDomain convert(RegexDomain regexDomain) {
    String pattern = regexDomain.getRegex();
    RegexNode ast = validateAndParse(pattern);

    // Estimate the size of the domain
    long estimatedSize = estimateSize(ast);

    if (estimatedSize <= MAX_ENUMERATION_SIZE) {
      // Small domain: enumerate all values
      Set<Long> values = enumerateValues(ast);
      return createIntegerDomainFromValues(values);
    } else {
      // Large domain: compute min/max bounds
      return createIntegerDomainFromBounds(ast);
    }
  }

  /**
   * Validates the regex pattern and parses it into an AST.
   * 
   * @param pattern the regex pattern
   * @return the parsed AST
   * @throws NonConvertibleDomainException if the pattern is invalid
   */
  private RegexNode validateAndParse(String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      throw new NonConvertibleDomainException("Empty pattern");
    }

    // Check for anchors
    if (!pattern.startsWith("^")) {
      throw new NonConvertibleDomainException("Pattern must start with ^");
    }
    if (!pattern.endsWith("$")) {
      throw new NonConvertibleDomainException("Pattern must end with $");
    }

    // Remove anchors for parsing
    String core = pattern.substring(1, pattern.length() - 1);

    // Check for invalid constructs
    checkForInvalidConstructs(core);

    // Parse the core pattern
    return parseExpression(core, 0).node;
  }

  /**
   * Checks for invalid constructs in the pattern.
   */
  private void checkForInvalidConstructs(String pattern) {
    // Check for unbounded repetition
    if (pattern.matches(".*[*+?].*")) {
      throw new NonConvertibleDomainException("Unbounded repetition not allowed: *, +, ?");
    }

    // Check for {n,} unbounded quantifier
    if (pattern.matches(".*\\{\\d+,\\}.*")) {
      throw new NonConvertibleDomainException("Unbounded repetition not allowed: {n,}");
    }

    // Check for dot (.) - but not inside quantifiers like {2,3}
    // Remove quantifiers first, then check for dots
    String patternWithoutQuantifiers = pattern.replaceAll("\\{\\d+,?\\d*\\}", "");
    if (patternWithoutQuantifiers.contains(".")) {
      throw new NonConvertibleDomainException("Wildcard . not allowed");
    }

    // Check for lookahead/lookbehind  
    if (pattern.contains("(?=") || pattern.contains("(?!") || pattern.contains("(?<=") || pattern.contains("(?<!")) {
      throw new NonConvertibleDomainException("Lookahead/lookbehind not allowed");
    }

    // Check for non-capturing groups and other special constructs
    if (pattern.contains("(?:") || pattern.contains("(?<")) {
      throw new NonConvertibleDomainException("Special group constructs not allowed");
    }

    // Check for backreferences (but allow escaped digits like \\5)
    // Backreferences are \1, \2, etc. which in the string would be "\\1", "\\2"
    // We need to distinguish from escaped digits which are also "\\5"
    // Since we allow escaped digits, we'll check this during parsing instead
    // Pattern for actual backreferences in group context would need more sophisticated parsing

    // Check for shorthand classes
    if (pattern.matches(".*\\\\[dwsWSD].*")) {
      throw new NonConvertibleDomainException("Shorthand classes not allowed");
    }

    // Check for non-digit literals (excluding special chars like [], {}, (), |)
    // Remove quantifiers first (which can contain commas)
    String patternToCheck = pattern.replaceAll("\\{\\d+,?\\d*\\}", "") // Remove quantifiers
        .replaceAll("\\[0-9\\]", "") // Remove [0-9]
        .replaceAll("[\\[\\]{}()|]", "") // Remove structural chars
        .replaceAll("\\\\\\d", ""); // Remove escaped digits
    Pattern invalidChars = Pattern.compile("[a-zA-Z\\-_./:;,@#$%&!=<>~`'\"\\s]");
    if (invalidChars.matcher(patternToCheck).find()) {
      throw new NonConvertibleDomainException("Non-digit characters not allowed");
    }
  }

  /**
   * Parses a regex expression into an AST.
   */
  private ParseResult parseExpression(String pattern, int start) {
    if (start >= pattern.length()) {
      return new ParseResult(new SequenceNode(Collections.emptyList()), start);
    }

    List<RegexNode> sequence = new ArrayList<>();
    int pos = start;

    while (pos < pattern.length()) {
      char ch = pattern.charAt(pos);

      if (ch == ')' || ch == '|') {
        // End of this sequence
        break;
      } else if (ch == '(') {
        // Group or alternation
        ParseResult groupResult = parseGroup(pattern, pos);
        sequence.add(groupResult.node);
        pos = groupResult.nextPos;
      } else if (ch == '[') {
        // Character class
        ParseResult classResult = parseCharClass(pattern, pos);
        sequence.add(classResult.node);
        pos = classResult.nextPos;
      } else if (ch >= '0' && ch <= '9') {
        // Literal digit
        sequence.add(new LiteralNode(String.valueOf(ch)));
        pos++;
      } else if (ch == '\\') {
        // Escaped character
        if (pos + 1 < pattern.length()) {
          char next = pattern.charAt(pos + 1);
          if (next >= '0' && next <= '9') {
            sequence.add(new LiteralNode(String.valueOf(next)));
            pos += 2;
          } else {
            throw new NonConvertibleDomainException("Invalid escape: \\" + next);
          }
        } else {
          throw new NonConvertibleDomainException("Incomplete escape at end");
        }
      } else {
        throw new NonConvertibleDomainException("Unexpected character: " + ch);
      }

      // Check for quantifier
      if (pos < pattern.length() && pattern.charAt(pos) == '{') {
        ParseResult quantResult = parseQuantifier(pattern, pos, sequence.get(sequence.size() - 1));
        sequence.set(sequence.size() - 1, quantResult.node);
        pos = quantResult.nextPos;
      }
    }

    return new ParseResult(new SequenceNode(sequence), pos);
  }

  /**
   * Parses a group (possibly with alternation).
   */
  private ParseResult parseGroup(String pattern, int start) {
    if (pattern.charAt(start) != '(') {
      throw new NonConvertibleDomainException("Expected ( at position " + start);
    }

    int pos = start + 1;
    List<RegexNode> alternatives = new ArrayList<>();

    while (pos < pattern.length()) {
      ParseResult exprResult = parseExpression(pattern, pos);
      alternatives.add(exprResult.node);
      pos = exprResult.nextPos;

      if (pos < pattern.length() && pattern.charAt(pos) == '|') {
        pos++; // Skip |
      } else if (pos < pattern.length() && pattern.charAt(pos) == ')') {
        pos++; // Skip )
        break;
      } else if (pos >= pattern.length()) {
        throw new NonConvertibleDomainException("Unclosed group");
      }
    }

    if (alternatives.size() == 1) {
      return new ParseResult(alternatives.get(0), pos);
    } else {
      return new ParseResult(new AlternationNode(alternatives), pos);
    }
  }

  /**
   * Parses a character class like [0-9].
   */
  private ParseResult parseCharClass(String pattern, int start) {
    if (pattern.charAt(start) != '[') {
      throw new NonConvertibleDomainException("Expected [ at position " + start);
    }

    int end = pattern.indexOf(']', start);
    if (end == -1) {
      throw new NonConvertibleDomainException("Unclosed character class");
    }

    String classContent = pattern.substring(start + 1, end);

    // Only allow [0-9]
    if (!classContent.equals("0-9")) {
      throw new NonConvertibleDomainException("Only [0-9] character class allowed, got: [" + classContent + "]");
    }

    return new ParseResult(new CharClassNode(), end + 1);
  }

  /**
   * Parses a quantifier like {n} or {n,m}.
   */
  private ParseResult parseQuantifier(String pattern, int start, RegexNode target) {
    if (pattern.charAt(start) != '{') {
      throw new NonConvertibleDomainException("Expected { at position " + start);
    }

    int end = pattern.indexOf('}', start);
    if (end == -1) {
      throw new NonConvertibleDomainException("Unclosed quantifier");
    }

    String quantContent = pattern.substring(start + 1, end);

    if (quantContent.contains(",")) {
      String[] parts = quantContent.split(",");
      int min = Integer.parseInt(parts[0].trim());
      int max = Integer.parseInt(parts[1].trim());
      return new ParseResult(new RepetitionNode(target, min, max), end + 1);
    } else {
      int count = Integer.parseInt(quantContent.trim());
      return new ParseResult(new RepetitionNode(target, count, count), end + 1);
    }
  }

  /**
   * Estimates the size of the domain represented by the AST.
   */
  private long estimateSize(RegexNode node) {
    if (node instanceof LiteralNode) {
      return 1;
    } else if (node instanceof CharClassNode) {
      return 10; // [0-9]
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      long size = 1;
      for (RegexNode child : seq.children) {
        size *= estimateSize(child);
        if (size > MAX_ENUMERATION_SIZE) {
          return size; // Early exit
        }
      }
      return size;
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      long size = 0;
      for (RegexNode child : alt.alternatives) {
        size += estimateSize(child);
        if (size > MAX_ENUMERATION_SIZE) {
          return size; // Early exit
        }
      }
      return size;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      long childSize = estimateSize(rep.child);
      long totalSize = 0;
      for (int i = rep.min; i <= rep.max; i++) {
        long iterSize = 1;
        for (int j = 0; j < i; j++) {
          iterSize *= childSize;
          if (iterSize > MAX_ENUMERATION_SIZE) {
            return iterSize; // Early exit
          }
        }
        totalSize += iterSize;
        if (totalSize > MAX_ENUMERATION_SIZE) {
          return totalSize; // Early exit
        }
      }
      return totalSize;
    }
    return 1;
  }

  /**
   * Enumerates all values represented by the AST.
   */
  private Set<Long> enumerateValues(RegexNode node) {
    Set<String> strings = enumerateStrings(node);
    Set<Long> values = new TreeSet<>();

    for (String s : strings) {
      try {
        values.add(Long.parseLong(s));
      } catch (NumberFormatException e) {
        // Skip invalid numbers
      }
    }

    return values;
  }

  /**
   * Enumerates all string representations.
   */
  private Set<String> enumerateStrings(RegexNode node) {
    if (node instanceof LiteralNode) {
      return Collections.singleton(((LiteralNode) node).value);
    } else if (node instanceof CharClassNode) {
      Set<String> result = new HashSet<>();
      for (int i = 0; i <= 9; i++) {
        result.add(String.valueOf(i));
      }
      return result;
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      Set<String> result = new HashSet<>();
      result.add("");

      for (RegexNode child : seq.children) {
        Set<String> childStrings = enumerateStrings(child);
        Set<String> newResult = new HashSet<>();
        for (String prefix : result) {
          for (String suffix : childStrings) {
            newResult.add(prefix + suffix);
          }
        }
        result = newResult;
      }
      return result;
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      Set<String> result = new HashSet<>();
      for (RegexNode child : alt.alternatives) {
        result.addAll(enumerateStrings(child));
      }
      return result;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      Set<String> childStrings = enumerateStrings(rep.child);
      Set<String> result = new HashSet<>();

      for (int count = rep.min; count <= rep.max; count++) {
        if (count == 0) {
          result.add("");
        } else {
          Set<String> current = new HashSet<>(childStrings);
          for (int i = 1; i < count; i++) {
            Set<String> next = new HashSet<>();
            for (String s1 : current) {
              for (String s2 : childStrings) {
                next.add(s1 + s2);
              }
            }
            current = next;
          }
          result.addAll(current);
        }
      }
      return result;
    }
    return Collections.emptySet();
  }

  /**
   * Creates IntegerDomain from enumerated values.
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

  /**
   * Creates IntegerDomain from computed bounds.
   */
  private IntegerDomain createIntegerDomainFromBounds(RegexNode node) {
    long min = computeMin(node);
    long max = computeMax(node);
    return IntegerDomain.of(Collections.singletonList(new IntegerDomain.Interval(min, max)));
  }

  /**
   * Computes the minimum value.
   */
  private long computeMin(RegexNode node) {
    if (node instanceof LiteralNode) {
      return Long.parseLong(((LiteralNode) node).value);
    } else if (node instanceof CharClassNode) {
      return 0;
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      StringBuilder sb = new StringBuilder();
      for (RegexNode child : seq.children) {
        sb.append(computeMinString(child));
      }
      return Long.parseLong(sb.toString());
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      long min = Long.MAX_VALUE;
      for (RegexNode child : alt.alternatives) {
        min = Math.min(min, computeMin(child));
      }
      return min;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      if (rep.min == 0) {
        return 0;
      }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rep.min; i++) {
        sb.append(computeMinString(rep.child));
      }
      return Long.parseLong(sb.toString());
    }
    return 0;
  }

  /**
   * Computes the maximum value.
   */
  private long computeMax(RegexNode node) {
    if (node instanceof LiteralNode) {
      return Long.parseLong(((LiteralNode) node).value);
    } else if (node instanceof CharClassNode) {
      return 9;
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      StringBuilder sb = new StringBuilder();
      for (RegexNode child : seq.children) {
        sb.append(computeMaxString(child));
      }
      return Long.parseLong(sb.toString());
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      long max = Long.MIN_VALUE;
      for (RegexNode child : alt.alternatives) {
        max = Math.max(max, computeMax(child));
      }
      return max;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rep.max; i++) {
        sb.append(computeMaxString(rep.child));
      }
      return Long.parseLong(sb.toString());
    }
    return 0;
  }

  private String computeMinString(RegexNode node) {
    if (node instanceof LiteralNode) {
      return ((LiteralNode) node).value;
    } else if (node instanceof CharClassNode) {
      return "0";
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      StringBuilder sb = new StringBuilder();
      for (RegexNode child : seq.children) {
        sb.append(computeMinString(child));
      }
      return sb.toString();
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      String min = null;
      long minVal = Long.MAX_VALUE;
      for (RegexNode child : alt.alternatives) {
        String s = computeMinString(child);
        long val = Long.parseLong(s);
        if (val < minVal) {
          minVal = val;
          min = s;
        }
      }
      return min;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      if (rep.min == 0) {
        return "";
      }
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rep.min; i++) {
        sb.append(computeMinString(rep.child));
      }
      return sb.toString();
    }
    return "";
  }

  private String computeMaxString(RegexNode node) {
    if (node instanceof LiteralNode) {
      return ((LiteralNode) node).value;
    } else if (node instanceof CharClassNode) {
      return "9";
    } else if (node instanceof SequenceNode) {
      SequenceNode seq = (SequenceNode) node;
      StringBuilder sb = new StringBuilder();
      for (RegexNode child : seq.children) {
        sb.append(computeMaxString(child));
      }
      return sb.toString();
    } else if (node instanceof AlternationNode) {
      AlternationNode alt = (AlternationNode) node;
      String max = null;
      long maxVal = Long.MIN_VALUE;
      for (RegexNode child : alt.alternatives) {
        String s = computeMaxString(child);
        long val = Long.parseLong(s);
        if (val > maxVal) {
          maxVal = val;
          max = s;
        }
      }
      return max;
    } else if (node instanceof RepetitionNode) {
      RepetitionNode rep = (RepetitionNode) node;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rep.max; i++) {
        sb.append(computeMaxString(rep.child));
      }
      return sb.toString();
    }
    return "";
  }

  // AST Node classes
  private interface RegexNode {
  }

  private static class LiteralNode implements RegexNode {
    final String value;
    LiteralNode(String value) {
      this.value = value;
    }
  }

  private static class CharClassNode implements RegexNode {
  }

  private static class SequenceNode implements RegexNode {
    final List<RegexNode> children;
    SequenceNode(List<RegexNode> children) {
      this.children = children;
    }
  }

  private static class AlternationNode implements RegexNode {
    final List<RegexNode> alternatives;
    AlternationNode(List<RegexNode> alternatives) {
      this.alternatives = alternatives;
    }
  }

  private static class RepetitionNode implements RegexNode {
    final RegexNode child;
    final int min;
    final int max;
    RepetitionNode(RegexNode child, int min, int max) {
      this.child = child;
      this.min = min;
      this.max = max;
    }
  }

  private static class ParseResult {
    final RegexNode node;
    final int nextPos;
    ParseResult(RegexNode node, int nextPos) {
      this.node = node;
      this.nextPos = nextPos;
    }
  }
}
