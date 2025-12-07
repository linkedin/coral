package com.linkedin.coral.datagen.domain;

import java.util.List;

/**
 * Abstract base class representing a domain of values with constraint propagation.
 * 
 * A domain represents a set of valid values that satisfy certain constraints.
 * Domains support:
 * - Emptiness checking (detecting contradictions)
 * - Intersection (refining constraints)
 * - Union (combining constraints)
 * - Sampling (generating example values)
 *
 * @param <T> the type of values in this domain
 * @param <D> the concrete domain type (for fluent API)
 */
public abstract class Domain<T, D extends Domain<T, D>> {

    /**
     * Checks if this domain is empty (contains no valid values).
     * An empty domain represents a contradiction in constraints.
     *
     * @return true if no values satisfy the domain constraints
     */
    public abstract boolean isEmpty();

    /**
     * Intersects this domain with another, producing a refined constraint.
     * The result contains only values that satisfy BOTH constraints.
     *
     * Example:
     * - Integer: [1, 10] ∩ [5, 15] = [5, 10]
     * - String: "a.*" ∩ ".*b" = "a.*b"
     *
     * @param other the other domain to intersect with
     * @return a new domain representing the intersection
     */
    public abstract D intersect(D other);

    /**
     * Unions this domain with another, producing a combined constraint.
     * The result contains values that satisfy EITHER constraint.
     *
     * Example:
     * - Integer: [1, 5] ∪ [10, 15] = [1, 5] ∪ [10, 15]
     * - String: "abc" ∪ "xyz" = "(abc|xyz)"
     *
     * @param other the other domain to union with
     * @return a new domain representing the union
     */
    public abstract D union(D other);

    /**
     * Generates sample values from this domain.
     * Useful for testing and generating example data that satisfies constraints.
     *
     * @param limit the maximum number of samples to generate
     * @return a list of sample values (may be less than limit if domain is small)
     */
    public abstract List<T> sample(int limit);

    /**
     * Checks if this domain represents a single specific value.
     * Used to optimize constraint propagation when domain is fully resolved.
     *
     * @return true if this domain contains exactly one value
     */
    public abstract boolean isSingleton();

    /**
     * Returns a string representation of this domain for debugging.
     * 
     * @return a human-readable representation of the domain constraints
     */
    @Override
    public abstract String toString();

    /**
     * Checks equality with another domain.
     * Two domains are equal if they represent the same set of values.
     *
     * @param obj the object to compare with
     * @return true if domains are equal
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns a hash code for this domain.
     *
     * @return the hash code
     */
    @Override
    public abstract int hashCode();
}
