/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * Represents a domain of integer values as a collection of intervals.
 * Supports single values, single intervals, and multiple disjoint intervals.
 *
 * Examples:
 * - Single value: [5, 5]
 * - Single interval: [10, 20]
 * - Multiple intervals: [1, 5] ∪ [10, 15] ∪ [20, 30]
 */
public class IntegerDomain extends Domain<Long, IntegerDomain> {
  private final List<Interval> intervals;
  private static final Random RANDOM = new Random();

  /**
   * Represents a closed interval [min, max].
   */
  public static class Interval {
    private final long min;
    private final long max;

    public Interval(long min, long max) {
      if (min > max) {
        throw new IllegalArgumentException("Invalid interval: min (" + min + ") > max (" + max + ")");
      }
      this.min = min;
      this.max = max;
    }

    public long getMin() {
      return min;
    }

    public long getMax() {
      return max;
    }

    public boolean contains(long value) {
      return value >= min && value <= max;
    }

    public long size() {
      if (max == Long.MAX_VALUE || min == Long.MIN_VALUE) {
        return Long.MAX_VALUE; // Unbounded
      }
      return max - min + 1;
    }

    public boolean overlaps(Interval other) {
      return this.min <= other.max && other.min <= this.max;
    }

    public boolean isAdjacent(Interval other) {
      return this.max + 1 == other.min || other.max + 1 == this.min;
    }

    public Interval merge(Interval other) {
      return new Interval(Math.min(this.min, other.min), Math.max(this.max, other.max));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      Interval interval = (Interval) o;
      return min == interval.min && max == interval.max;
    }

    @Override
    public int hashCode() {
      return Objects.hash(min, max);
    }

    @Override
    public String toString() {
      if (min == max) {
        return "{" + min + "}";
      }
      return "[" + min + ", " + max + "]";
    }
  }

  /**
   * Private constructor - use factory methods to create instances.
   */
  private IntegerDomain(List<Interval> intervals) {
    this.intervals = normalizeIntervals(intervals);
  }

  /**
   * Normalizes intervals by sorting, merging overlapping/adjacent intervals.
   */
  private List<Interval> normalizeIntervals(List<Interval> intervals) {
    if (intervals.isEmpty()) {
      return Collections.emptyList();
    }

    // Sort by min value
    List<Interval> sorted = new ArrayList<>(intervals);
    sorted.sort((a, b) -> Long.compare(a.min, b.min));

    List<Interval> merged = new ArrayList<>();
    Interval current = sorted.get(0);

    for (int i = 1; i < sorted.size(); i++) {
      Interval next = sorted.get(i);
      if (current.overlaps(next) || current.isAdjacent(next)) {
        current = current.merge(next);
      } else {
        merged.add(current);
        current = next;
      }
    }
    merged.add(current);

    return Collections.unmodifiableList(merged);
  }

  /**
   * Creates a domain from a single value.
   */
  public static IntegerDomain of(long value) {
    return new IntegerDomain(Collections.singletonList(new Interval(value, value)));
  }

  /**
   * Creates a domain from a single interval [min, max].
   */
  public static IntegerDomain of(long min, long max) {
    return new IntegerDomain(Collections.singletonList(new Interval(min, max)));
  }

  /**
   * Creates a domain from multiple intervals.
   */
  public static IntegerDomain of(List<Interval> intervals) {
    if (intervals == null || intervals.isEmpty()) {
      return empty();
    }
    return new IntegerDomain(new ArrayList<>(intervals));
  }

  /**
   * Creates an empty domain (no valid values).
   */
  public static IntegerDomain empty() {
    return new IntegerDomain(Collections.emptyList());
  }

  /**
   * Creates a domain containing all integers.
   */
  public static IntegerDomain all() {
    return new IntegerDomain(Collections.singletonList(new Interval(Long.MIN_VALUE, Long.MAX_VALUE)));
  }

  /**
   * Checks if this domain is empty (contains no values).
   */
  @Override
  public boolean isEmpty() {
    return intervals.isEmpty();
  }

  /**
   * Checks if this domain contains a specific value.
   */
  public boolean contains(long value) {
    for (Interval interval : intervals) {
      if (interval.contains(value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the list of intervals in this domain.
   */
  public List<Interval> getIntervals() {
    return intervals;
  }

  /**
   * Intersects this domain with another, producing a refined constraint.
   * The result contains only values that satisfy both constraints.
   */
  @Override
  public IntegerDomain intersect(IntegerDomain other) {
    if (this.isEmpty() || other.isEmpty()) {
      return empty();
    }

    List<Interval> result = new ArrayList<>();

    for (Interval thisInterval : this.intervals) {
      for (Interval otherInterval : other.intervals) {
        if (thisInterval.overlaps(otherInterval)) {
          long newMin = Math.max(thisInterval.min, otherInterval.min);
          long newMax = Math.min(thisInterval.max, otherInterval.max);
          result.add(new Interval(newMin, newMax));
        }
      }
    }

    return new IntegerDomain(result);
  }

  /**
   * Unions this domain with another, producing values that satisfy either constraint.
   */
  @Override
  public IntegerDomain union(IntegerDomain other) {
    if (this.isEmpty())
      return other;
    if (other.isEmpty())
      return this;

    List<Interval> combined = new ArrayList<>();
    combined.addAll(this.intervals);
    combined.addAll(other.intervals);

    return new IntegerDomain(combined);
  }

  /**
   * Adds a constant to all values in this domain.
   */
  public IntegerDomain add(long constant) {
    if (isEmpty()) {
      return empty();
    }

    List<Interval> shifted = new ArrayList<>();
    for (Interval interval : intervals) {
      // Handle overflow/underflow
      long newMin = interval.min == Long.MIN_VALUE ? Long.MIN_VALUE
          : (interval.min > 0 && constant > Long.MAX_VALUE - interval.min) ? Long.MAX_VALUE
              : (interval.min < 0 && constant < Long.MIN_VALUE - interval.min) ? Long.MIN_VALUE
                  : interval.min + constant;

      long newMax = interval.max == Long.MAX_VALUE ? Long.MAX_VALUE
          : (interval.max > 0 && constant > Long.MAX_VALUE - interval.max) ? Long.MAX_VALUE
              : (interval.max < 0 && constant < Long.MIN_VALUE - interval.max) ? Long.MIN_VALUE
                  : interval.max + constant;

      shifted.add(new Interval(newMin, newMax));
    }

    return new IntegerDomain(shifted);
  }

  /**
   * Multiplies all values in this domain by a constant.
   */
  public IntegerDomain multiply(long constant) {
    if (isEmpty()) {
      return empty();
    }

    if (constant == 0) {
      return of(0);
    }

    List<Interval> scaled = new ArrayList<>();
    for (Interval interval : intervals) {
      long newMin, newMax;

      if (constant > 0) {
        newMin = multiplyWithOverflow(interval.min, constant);
        newMax = multiplyWithOverflow(interval.max, constant);
      } else {
        // Negative constant flips the interval
        newMin = multiplyWithOverflow(interval.max, constant);
        newMax = multiplyWithOverflow(interval.min, constant);
      }

      scaled.add(new Interval(newMin, newMax));
    }

    return new IntegerDomain(scaled);
  }

  private long multiplyWithOverflow(long a, long b) {
    if (a == 0 || b == 0)
      return 0;
    if (a == Long.MIN_VALUE || b == Long.MIN_VALUE)
      return Long.MIN_VALUE;
    if (a == Long.MAX_VALUE || b == Long.MAX_VALUE)
      return Long.MAX_VALUE;

    try {
      return Math.multiplyExact(a, b);
    } catch (ArithmeticException e) {
      // Overflow
      if ((a > 0 && b > 0) || (a < 0 && b < 0)) {
        return Long.MAX_VALUE;
      } else {
        return Long.MIN_VALUE;
      }
    }
  }

  /**
   * Generates sample values from this domain.
   * Returns up to 'limit' values.
   */
  @Override
  public List<Long> sample(int limit) {
    if (isEmpty() || limit <= 0) {
      return Collections.emptyList();
    }

    List<Long> samples = new ArrayList<>();

    for (Interval interval : intervals) {
      if (samples.size() >= limit) {
        break;
      }

      long size = interval.size();
      int toSample = Math.min(limit - samples.size(), (int) Math.min(size, 100));

      if (size <= toSample) {
        // Sample all values in small intervals
        for (long v = interval.min; v <= interval.max && samples.size() < limit; v++) {
          samples.add(v);
        }
      } else {
        // Sample randomly from large intervals
        for (int i = 0; i < toSample; i++) {
          long value = sampleFromInterval(interval);
          samples.add(value);
        }
      }
    }

    return samples;
  }

  /**
   * Generates sample values from this domain (alias for compatibility).
   * @deprecated Use {@link #sample(int)} instead
   */
  @Deprecated
  public List<Long> sampleValues(int limit) {
    return sample(limit);
  }

  /**
   * Checks if this domain represents exactly one value.
   */
  @Override
  public boolean isSingleton() {
    if (intervals.size() != 1) {
      return false;
    }
    Interval interval = intervals.get(0);
    return interval.getMin() == interval.getMax();
  }

  private long sampleFromInterval(Interval interval) {
    if (interval.min == interval.max) {
      return interval.min;
    }

    long range = interval.max - interval.min;
    if (range < Integer.MAX_VALUE) {
      return interval.min + RANDOM.nextInt((int) range + 1);
    } else {
      // For large ranges, sample uniformly
      return interval.min + (long) (RANDOM.nextDouble() * range);
    }
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "IntegerDomain{∅}";
    }
    return "IntegerDomain" + intervals;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    IntegerDomain that = (IntegerDomain) o;
    return intervals.equals(that.intervals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intervals);
  }
}
