# Coral Data Generation: Symbolic Domain Inference for SQL

## 1. Introduction

### The Core Idea: Solving for Input Constraints

Consider how you solve an algebraic equation:

```
Given: 2x + 5 = 25
Solve for x by inverting each operation:
  2x = 25 - 5  (inverse of addition)
  x = 20 / 2   (inverse of multiplication)
  x = 10
```

This system applies the same principle to SQL expressions. Instead of evaluating SQL forward (input → output), it **inverts SQL expressions symbolically** to derive constraints on input data that satisfy output predicates.

**Example 1: Simple String Transformation**

```sql
WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'
```

Traditional approach: Generate random strings, apply LOWER(SUBSTRING(...)), check if result equals 'abc'.

**Symbolic inversion approach:**
1. Start with output constraint: `'abc'` (exact match)
2. Invert LOWER: `'abc'` → `[aA][bB][cC]` (case-insensitive)
3. Invert SUBSTRING(x, 1, 3): must start with `[aA][bB][cC]`
4. **Result:** `name` must match `^[aA][bB][cC].*$`

Generate: `"Abc"`, `"ABC123"`, `"abcdef"` — all valid by construction.

---

**Example 2: Cross-Domain Arithmetic Constraint**

```sql
WHERE CAST(age * 2 AS STRING) = '50'
```

**Symbolic inversion:**
1. Output constraint: `'50'` (string literal)
2. Convert string domain to integer: `RegexDomain("^50$")` → `IntegerDomain([50])`
3. Invert multiplication by 2: `[50]` → `[25]`
4. **Result:** `age` must be `25`

---

**Example 3: Nested Position-Dependent Extraction**

```sql
WHERE SUBSTRING(SUBSTRING(name, 5, 15), 1, 3) = 'xyz'
```

**Symbolic inversion:**
1. Output: `'xyz'` at positions 1-3 of inner substring
2. Inner substring starts at position 5 of original
3. **Result:** Original string must have `'xyz'` at positions 5-7
4. Constraint: `^.{4}xyz.*$`

---

**Example 4: Contradiction Detection**

```sql
WHERE SUBSTRING(name, 1, 4) = '2000' AND SUBSTRING(name, 1, 4) = '1999'
```

The system detects this is **unsatisfiable** by intersecting domains:
- First constraint: `^2000.*$`
- Second constraint: `^1999.*$`
- Intersection: **empty** (contradiction)

No test data generated — the query cannot produce results.

---

### Why This Matters

**Efficient Test Data Generation**
- Generate valid inputs directly without rejection sampling
- Handle complex nested expressions with position-dependent constraints
- Detect contradictions early (empty result sets)

**Extensible to Multi-Table Analysis**
- Domain propagation across joins via constraint intersection
- Fixed-point computation for recursive constraints
- Multi-variable inference through simultaneous equation solving

**Symbolic, Not Stochastic**
- No random guessing; every generated value satisfies constraints
- Deterministic coverage of constraint space
- Minimal examples for unit testing

---

## 2. Conceptual Model: SQL as Invertible Expressions

### Mathematical Foundation

Every SQL function or operator can be viewed as a **transformation** with a (potentially partial) **inverse**:

| Operation | Forward (f) | Inverse (f⁻¹) | Domain Type |
|-----------|-------------|---------------|-------------|
| LOWER(x) = 'abc' | x → lowercase(x) | 'abc' → [aA][bB][cC] | RegexDomain |
| SUBSTRING(x,1,4) = '2000' | x → x[1:4] | '2000' → ^2000.* | RegexDomain |
| x + 5 = 25 | x → x + 5 | 25 → 25 - 5 = 20 | IntegerDomain |
| x * 2 = 50 | x → x * 2 | 50 → 50 / 2 = 25 | IntegerDomain |
| CAST(x AS INT) = 42 | x → int(x) | 42 → "^42$" | Cross-domain |

### Domain-Based Constraint Representation

A **domain** is a symbolic description of all possible values satisfying a constraint:

- **RegexDomain**: Automaton-backed representation of string constraints
  - Supports intersection (∩), union (∪), emptiness checking
  - Examples: `^[aA][bB][cC].*$`, `^.{4}2000.*$`, `^(Alice|Bob)$`

- **IntegerDomain**: Interval-based numeric constraints
  - Represented as disjoint intervals: `[10, 20] ∪ [50, 60]`
  - Closed under arithmetic operations: `domain.add(5)`, `domain.multiply(2)`

- **Cross-Domain Conversions**: CAST operations bridge domains
  - `RegexDomain("^50$")` ⇄ `IntegerDomain([50])`
  - Enables arithmetic on string-typed columns, string operations on numeric results

### Symbolic Inversion as Constraint Solving

Given a predicate like `f(g(h(x))) = constant`, the system:

1. **Starts from the output**: Creates a domain representing `constant`
2. **Inverts each layer**: Applies inverse transformations inward
   - `f⁻¹(constant)` → intermediate domain
   - `g⁻¹(intermediate)` → refined domain
   - `h⁻¹(refined)` → **input domain constraint**
3. **Intersects multiple constraints**: If predicate is `f(x) = 'A' AND g(x) = 'B'`
   - Derive domain₁ from first constraint
   - Derive domain₂ from second constraint
   - Result: `domain₁ ∩ domain₂`
4. **Detects contradictions**: Empty intersection → unsatisfiable query

### Extensibility to Multi-Variable Systems

The architecture naturally extends to multi-table, multi-variable inference:

**Single Table, Single Variable** (current implementation examples):
```sql
WHERE LOWER(name) = 'alice' AND SUBSTRING(name, 1, 2) = 'Al'
→ name ∈ RegexDomain("^[aA][lL].*$") ∩ RegexDomain("^Al.*$")
```

**Multi-Variable Through Join Propagation** (architectural capability):
```sql
WHERE users.id = orders.user_id AND LOWER(users.name) = 'alice'
→ Propagate domain constraints across equijoin:
   users.id ∈ domain₁
   orders.user_id ∈ domain₁  (same domain via equality)
   users.name ∈ domain₂
```

**Fixed-Point Iteration for Recursive Constraints** (design support):
- Iteratively refine domains until no changes occur
- Handles cyclic dependencies in complex join graphs
- Detects convergence or reports non-convergence

The system architecture separates **expression inversion** (transformers) from **constraint propagation** (domain intersection), enabling modular extension to arbitrarily complex query patterns.

---

## 3. Relational Layer: Preprocessing Pipeline

Before symbolic inversion can occur, SQL expressions must be normalized into a **canonical form** amenable to analysis. This preprocessing transforms arbitrary relational algebra trees into a structure where predicates are isolated, normalized, and indexed consistently.

### Pipeline Overview

```
Raw RelNode (Calcite logical plan)
    ↓
[1] Project Pull-Up (Fixed-Point Normalization)
    ↓
[2] Canonical Predicate Extraction
    ↓
[3] DNF Conversion (Disjunctive Normal Form)
    ↓
[4] Global Index Rewriting
    ↓
Normalized Predicates Ready for Symbolic Inversion
```

---

### Step 1: Project Pull-Up (`ProjectPullUpController`)

**Purpose**: Normalize query structure by moving all `Project` (SELECT list) nodes to the top of the tree, ensuring predicates reference base table columns directly.

**Problem Solved**:
Raw SQL often embeds projections at arbitrary tree levels:
```
Filter(LOWER(expr) = 'abc')
  ↳ Project(expr: SUBSTRING(name, 1, 5))
     ↳ TableScan(T)
```

Here, `LOWER(expr)` references a projected expression, not the base column `name`.

**Transformation**:
```
Project(LOWER(SUBSTRING(name, 1, 5)))
  ↳ Filter(LOWER(SUBSTRING(name, 1, 5)) = 'abc')
     ↳ TableScan(T)
```

Now the predicate references `name` directly, and the projection is transparent.

**Implementation**:
- **Fixed-point iteration**: Repeatedly applies pull-up rules until no further changes occur
- **Handles nested patterns**: Filter→Project, Join→Project, complex nestings
- **Convergence guarantee**: Monotonic reduction in tree depth of projects

**Key Class**: `ProjectPullUpController.applyUntilFixedPoint(RelNode)`

---

### Step 2: Canonical Predicate Extraction (`CanonicalPredicateExtractor`)

**Purpose**: Isolate all predicates (WHERE, JOIN ON) from the relational tree and rewrite field references to use a **global index** across all scanned tables.

**Problem Solved**:
Predicates in different parts of the tree reference fields using **local indices** relative to their immediate input:
```
Join(left: T1, right: T2, condition: T1.id = T2.id)
  → left.id is index 0, right.id is index 0 in local contexts
```

We need a **single, consistent indexing scheme** for all fields.

**Transformation**:
1. **Extract sequential scans**: Collect all base table scans in left-to-right order
   - Example: `[T1, T2, T3]` → T1 fields are indices 0-n₁, T2 fields are n₁+1 to n₁+n₂, etc.

2. **Rewrite RexInputRef indices**: 
   - Original: `RexInputRef(2)` in context of T2 (local index)
   - Global: `RexInputRef(7)` if T2 starts at global offset 5 and has 3 fields before this one

3. **Collect all predicates**: Extract from Filter nodes, Join conditions, etc.

**Output**:
```java
Output {
  List<RelNode> sequentialScans;  // [T1, T2, T3]
  List<RexNode> canonicalPredicates;  // [predicate1, predicate2, ...]
}
```

All predicates now use the same coordinate system.

**Key Class**: `CanonicalPredicateExtractor.extract(RelNode)`

---

### Step 3: DNF Conversion (`DnfRewriter`)

**Purpose**: Convert predicates into **Disjunctive Normal Form** (DNF) to enable independent constraint solving for each disjunct.

**Why DNF?**
- Each top-level disjunct (OR term) represents an **independent scenario**
- Domain inference can solve each scenario separately
- Final result is the **union** of solutions from each disjunct

**Example**:
```sql
WHERE (name = 'Alice' AND age > 30) OR (name = 'Bob' AND age < 25)
```

**DNF Form**:
```
Disjunct 1: (name = 'Alice' ∧ age > 30)
Disjunct 2: (name = 'Bob' ∧ age < 25)
```

For each disjunct:
1. Derive domain for `name` and `age` from conjuncts
2. Intersect constraints within each disjunct
3. Union results across disjuncts

**Implementation**:
- Uses Calcite's `RexUtil.toDnf()` for transformation
- Extracts top-level OR terms using `RelOptUtil.disjunctions()`
- Preserves sequential scan ordering from previous step

**Key Class**: `DnfRewriter.convert(CanonicalPredicateExtractor.Output, RexBuilder)`

---

### Step 4: Global Index Rewriting (Implicit)

After DNF conversion, all predicates reference fields using **global indices** established in Step 2. This enables:

- **Consistent variable identification**: `RexInputRef(5)` always means the same column across all predicates
- **Cross-predicate constraint intersection**: Multiple predicates on the same variable can be merged
- **Multi-table analysis**: Field references span multiple tables with unambiguous semantics

---

### Design Rationale

These preprocessing steps transform SQL from an **opaque procedural plan** into a **symbolic constraint system**:

| Before Preprocessing | After Preprocessing |
|---------------------|---------------------|
| Arbitrary tree structure | Normalized, flat predicate list |
| Local field indices | Global, consistent indices |
| Complex nested predicates | DNF: independent OR terms |
| Expressions buried in projections | Direct references to base columns |

This enables **symbolic inversion**: each predicate is now a tractable constraint on base columns, ready for domain-based solving.

The pipeline is **modular and extensible**:
- Additional normalization rules can be added (e.g., subquery flattening)
- Alternative normal forms can be substituted (CNF for different solving strategies)
- More complex SQL constructs (aggregates, window functions) can be incorporated with new extraction rules

---

## 4. Domain System

### Conceptual Foundation

A **domain** is the algebraic substrate for symbolic constraint solving. It represents:
- **Semantically**: The set of all possible values satisfying a constraint
- **Operationally**: A closed algebraic structure supporting:
  - **Intersection** (∩): Refining constraints
  - **Union** (∪): Combining alternatives
  - **Emptiness checking**: Detecting contradictions
  - **Sampling**: Generating concrete values

Domains form a **lattice** under intersection, enabling constraint propagation through expression trees.

### Abstract Domain Interface

```java
public abstract class Domain<T, D extends Domain<T, D>> {
    boolean isEmpty();              // Contradiction detection
    D intersect(D other);           // Constraint refinement
    D union(D other);               // Disjunctive alternatives
    List<T> sample(int limit);      // Concrete value generation
    boolean isSingleton();          // Fully resolved constraint
}
```

All domain implementations adhere to this contract, ensuring uniform treatment by the solver.

---

### RegexDomain: Automaton-Backed String Constraints

**Purpose**: Represents string constraints as regular expressions with full automaton support for intersection and emptiness checking.

**Representation**:
- Backed by `dk.brics.automaton` library
- Stores both regex pattern and compiled DFA
- Supports complex patterns: character classes, anchors, quantifiers, alternation

**Core Operations**:

1. **Intersection** (conjunction of constraints):
```java
RegexDomain d1 = new RegexDomain("^[aA].*$");  // Starts with 'a' or 'A'
RegexDomain d2 = new RegexDomain(".*[bB]c$");  // Ends with 'bc' or 'Bc'
RegexDomain result = d1.intersect(d2);         // ^[aA].*[bB]c$
```

2. **Union** (disjunction):
```java
RegexDomain d1 = RegexDomain.literal("Alice");
RegexDomain d2 = RegexDomain.literal("Bob");
RegexDomain result = d1.union(d2);  // Accepts "Alice" or "Bob"
```

3. **Emptiness Checking** (contradiction detection):
```java
RegexDomain d1 = new RegexDomain("^2000.*$");
RegexDomain d2 = new RegexDomain("^1999.*$");
RegexDomain result = d1.intersect(d2);
assert result.isEmpty();  // No string can start with both 2000 and 1999
```

4. **Sampling** (DFS-based generation):
```java
RegexDomain domain = new RegexDomain("^[aA][bB][cC].*$");
List<String> samples = domain.sample(5);
// Possible output: ["Abc", "ABC", "abc", "AbC", "aBc"]
```

**Key Features**:
- **Literal detection**: Optimizes case-insensitive handling for simple strings
- **Anchored patterns**: Precise position-dependent constraints (e.g., prefix, suffix)
- **Efficient intersection**: Uses automaton product construction
- **Deterministic sampling**: DFS traversal of automaton state space

**Example Usage**:
```java
// Representing SUBSTRING(name, 1, 4) = '2000'
RegexDomain outputConstraint = RegexDomain.literal("2000");
RegexDomain inputConstraint = new RegexDomain("^2000.*$");
```

---

### IntegerDomain: Interval-Based Numeric Constraints

**Purpose**: Represents numeric constraints as collections of disjoint intervals, closed under arithmetic operations.

**Representation**:
- Stored as normalized list of `[min, max]` intervals
- Automatically merges overlapping/adjacent intervals
- Supports both finite and unbounded ranges

**Core Operations**:

1. **Interval Arithmetic**:
```java
IntegerDomain domain = IntegerDomain.of(10, 20);  // [10, 20]
IntegerDomain shifted = domain.add(5);            // [15, 25]
IntegerDomain scaled = domain.multiply(2);        // [20, 40]
```

2. **Intersection**:
```java
IntegerDomain d1 = IntegerDomain.of(10, 30);
IntegerDomain d2 = IntegerDomain.of(20, 40);
IntegerDomain result = d1.intersect(d2);  // [20, 30]
```

3. **Union** (disjoint intervals):
```java
IntegerDomain d1 = IntegerDomain.of(1, 5);
IntegerDomain d2 = IntegerDomain.of(10, 15);
IntegerDomain result = d1.union(d2);  // [1, 5] ∪ [10, 15]
```

4. **Inversion of Arithmetic**:
```java
// Solving: x + 5 = 25
IntegerDomain output = IntegerDomain.of(25);
IntegerDomain input = output.add(-5);  // [20]

// Solving: x * 2 ∈ [40, 60]
IntegerDomain output = IntegerDomain.of(40, 60);
// (Requires division, handled by TimesRegexTransformer)
```

**Key Features**:
- **Overflow handling**: Saturates at Long.MIN_VALUE / Long.MAX_VALUE
- **Singleton detection**: Optimizes for fully resolved constraints
- **Normalization**: Automatically merges intervals to minimal representation
- **Efficient sampling**: Uniform sampling for large intervals, exhaustive for small

**Example Usage**:
```java
// Representing: age * 2 + 5 = 25
IntegerDomain output = IntegerDomain.of(25);
IntegerDomain afterPlus = output.add(-5);   // [20]
// TimesRegexTransformer handles division to get [10]
```

---

### Domain Extensibility

The domain abstraction is not limited to strings and integers. The architecture supports arbitrary domain types:

**Potential Extensions**:
- **DateDomain**: ISO-8601 date constraints with interval arithmetic
- **TimestampDomain**: Temporal ranges with timezone handling
- **DecimalDomain**: Fixed-precision numeric constraints
- **EnumDomain**: Finite sets of symbolic values
- **StructDomain**: Nested record constraints (for complex types)
- **UnionDomain**: Polymorphic type constraints

Each new domain type implements the `Domain<T, D>` interface and integrates seamlessly with the solver via transformers.

**Cross-Domain Conversions**:
The `CastRegexTransformer` demonstrates bidirectional conversion:
- `RegexDomain("^50$")` → `IntegerDomain([50])`
- `IntegerDomain([25])` → `RegexDomain("^25$")`

This pattern extends to any domain pair with a well-defined conversion function.

---

## 5. Domain Transformers: Symbolic Inversion Functions

### Transformer Architecture

A **transformer** encapsulates the **inverse** of a SQL function or operator. It answers the question:

> "Given that `f(x) = y` and we know `y` must satisfy domain `D_out`, what domain `D_in` must `x` satisfy?"

**Transformer Interface**:
```java
public interface DomainTransformer {
    boolean canHandle(RexNode expr);
    boolean isVariableOperandPositionValid(RexNode expr);
    RexNode getChildForVariable(RexNode expr);
    Domain<?, ?> refineInputDomain(RexNode expr, Domain<?, ?> outputDomain);
}
```

**Key Responsibilities**:
1. **Pattern matching**: Identify applicable SQL operations (`canHandle`)
2. **Validation**: Ensure expression structure is invertible (`isVariableOperandPositionValid`)
3. **Traversal**: Navigate to child expression containing the variable (`getChildForVariable`)
4. **Inversion**: Apply inverse transformation to refine input domain (`refineInputDomain`)

---

### Implemented Transformers

#### 1. SubstringRegexTransformer

**Inverts**: `SUBSTRING(x, start, length) = pattern`

**Logic**:
- Output constraint at positions `[start, start+length)` must match `pattern`
- Input constraint: Embed pattern at the correct position with wildcards elsewhere

**Example**:
```java
// SUBSTRING(name, 5, 3) = 'xyz'
// Output: RegexDomain("^xyz$")
// Input:  RegexDomain("^.{4}xyz.*$")  // Skip 4 chars, then 'xyz', then anything
```

**Special Cases**:
- Nested substrings: Compose position offsets
- Literal length validation: Detect contradiction if output length ≠ declared length

---

#### 2. LowerRegexTransformer

**Inverts**: `LOWER(x) = pattern`

**Logic**:
- If output is a literal lowercase string, make it case-insensitive
- Convert each letter `c` to character class `[c,C]`

**Example**:
```java
// LOWER(name) = 'abc'
// Output: RegexDomain("^abc$")
// Input:  RegexDomain("^[aA][bB][cC]$")
```

**Implementation**:
```java
for (char c : literalValue.toCharArray()) {
    if (Character.isLetter(c)) {
        caseInsensitive.append('[')
                       .append(Character.toLowerCase(c))
                       .append(Character.toUpperCase(c))
                       .append(']');
    }
}
```

---

#### 3. CastRegexTransformer (Cross-Domain)

**Inverts**: `CAST(x AS type) = value`

**Supports Multiple Conversion Paths**:

**Path 1: String → Integer (Forward)**
```java
// CAST(name AS INT) = 42
// Output: IntegerDomain([42])
// Input:  RegexDomain("^42$")  (string representation of 42)
```

**Path 2: Integer → String (Reverse)**
```java
// CAST(age AS STRING) = '50'
// Output: RegexDomain("^50$")
// Input:  IntegerDomain([50])  (numeric value)
```

**Path 3: Date → String**
```java
// CAST(birthdate AS STRING) starts with '2000'
// Output: RegexDomain("^2000.*$")
// Input:  Intersect with date format: "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|30)$"
```

**Cross-Domain Conversion**:
- Uses `RegexToIntegerDomainConverter` for regex ↔ numeric conversion
- Detects incompatible patterns (e.g., non-numeric regex when casting to int)
- Returns empty domain for contradictions

---

#### 4. PlusRegexTransformer (Arithmetic)

**Inverts**: `x + c = value` where `c` is a constant

**Logic**:
- Inverse of addition is subtraction
- `x = value - c`

**Example**:
```java
// age + 5 = 25
// Output: IntegerDomain([25])
// Input:  IntegerDomain([20])  (via output.add(-5))
```

**Interval Support**:
```java
// age + 10 ∈ [30, 50]
// Output: IntegerDomain([30, 50])
// Input:  IntegerDomain([20, 40])
```

---

#### 5. TimesRegexTransformer (Arithmetic)

**Inverts**: `x * c = value` where `c` is a non-zero constant

**Logic**:
- Inverse of multiplication is division
- `x = value / c`
- Handles negative constants (interval reversal)

**Example**:
```java
// age * 2 = 50
// Output: IntegerDomain([50])
// Input:  IntegerDomain([25])
```

**Implementation Note**:
```java
// For exact division:
if (outputValue % multiplier == 0) {
    return IntegerDomain.of(outputValue / multiplier);
} else {
    return IntegerDomain.empty();  // Contradiction
}
```

---

### Transformer Composition

The power of transformers emerges from **composition**: inverting nested expressions by applying transformers in reverse order.

**Example: Nested Expression**
```sql
WHERE CAST(age * 2 AS STRING) = '50'
```

**Inversion Sequence**:
1. Start with `RegexDomain("^50$")`
2. Invert CAST (string→int): `IntegerDomain([50])`
3. Invert TIMES: `IntegerDomain([25])`
4. Result: `age = 25`

**Solver Code** (conceptual):
```java
Domain<?> current = RegexDomain.literal("50");
current = castTransformer.refineInputDomain(castExpr, current);   // → IntegerDomain([50])
current = timesTransformer.refineInputDomain(timesExpr, current); // → IntegerDomain([25])
```

---

### Transformer Extensibility

New transformers can be added for any SQL function with a well-defined inverse:

**String Functions**:
- `UPPER(x) = pattern` → Case-insensitive (similar to LOWER)
- `CONCAT(x, 'suffix') = pattern` → Prefix constraint
- `TRIM(x) = pattern` → Add surrounding whitespace pattern
- `REGEXP_REPLACE(x, pattern, replacement)` → Complex regex inversion

**Arithmetic Functions**:
- `ABS(x) = value` → Union of positive and negative solutions
- `MOD(x, m) = r` → Modular arithmetic constraint
- `POWER(x, n) = value` → Nth root extraction

**Date Functions**:
- `YEAR(x) = 2024` → Date range constraint
- `DATE_ADD(x, INTERVAL n DAY) = date` → Date subtraction

**Aggregate Functions** (future, requires multi-row reasoning):
- `SUM(x) = value` over groups
- `COUNT(x) = n` with cardinality constraints

Each transformer is **independent and pluggable**, registered in the `DomainInferenceProgram` constructor.

---

## 6. Solver Architecture: DomainInferenceProgram

### Overview

`DomainInferenceProgram` orchestrates the **top-down symbolic inversion** process, walking expression trees from predicates inward to variables while refining domains at each step.

**Conceptual Algorithm**:
```
function deriveInputDomain(expr, outputDomain):
    if expr is a variable (RexInputRef):
        return outputDomain  // Base case: propagated constraint
    
    for each transformer:
        if transformer.canHandle(expr):
            refinedDomain = transformer.refineInputDomain(expr, outputDomain)
            
            if refinedDomain.isEmpty():
                return EMPTY  // Contradiction detected
            
            childExpr = transformer.getChildForVariable(expr)
            return deriveInputDomain(childExpr, refinedDomain)  // Recurse
    
    throw "No applicable transformer"
```

---

### Execution Flow

**Example Query**:
```sql
WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'
```

**Expression Tree**:
```
EQUALS
├─ LOWER
│  └─ SUBSTRING
│     ├─ RexInputRef(0)  ← name
│     ├─ Literal(1)
│     └─ Literal(3)
└─ Literal('abc')
```

**Solver Execution**:

**Step 1: Initialize**
```java
RexNode predicate = parseSQL("LOWER(SUBSTRING(name, 1, 3)) = 'abc'");
RexNode lhs = predicate.operands[0];  // LOWER(SUBSTRING(...))
RexNode rhs = predicate.operands[1];  // 'abc'

Domain<?> outputDomain = RegexDomain.literal("abc");
```

**Step 2: Invert LOWER**
```java
// expr = LOWER(SUBSTRING(name, 1, 3))
// outputDomain = RegexDomain("^abc$")

LowerRegexTransformer.refineInputDomain(expr, outputDomain)
→ Returns: RegexDomain("^[aA][bB][cC]$")

childExpr = SUBSTRING(name, 1, 3)
```

**Step 3: Invert SUBSTRING**
```java
// expr = SUBSTRING(name, 1, 3)
// outputDomain = RegexDomain("^[aA][bB][cC]$")

SubstringRegexTransformer.refineInputDomain(expr, outputDomain)
→ Returns: RegexDomain("^[aA][bB][cC].*$")

childExpr = RexInputRef(0)  // name
```

**Step 4: Base Case**
```java
// expr = RexInputRef(0)
// outputDomain = RegexDomain("^[aA][bB][cC].*$")

→ Returns: outputDomain (this is the final constraint on 'name')
```

**Result**: `name ∈ RegexDomain("^[aA][bB][cC].*$")`

---

### Intersection of Multiple Constraints

When multiple predicates constrain the same variable, domains are intersected:

**Example**:
```sql
WHERE SUBSTRING(name, 1, 4) = '2000' AND SUBSTRING(name, 1, 2) = '20'
```

**Solving**:
1. **First constraint**: `SUBSTRING(name, 1, 4) = '2000'`
   - Result: `RegexDomain("^2000.*$")`

2. **Second constraint**: `SUBSTRING(name, 1, 2) = '20'`
   - Result: `RegexDomain("^20.*$")`

3. **Intersection**:
```java
domain1 = RegexDomain("^2000.*$");
domain2 = RegexDomain("^20.*$");
finalDomain = domain1.intersect(domain2);
→ RegexDomain("^2000.*$")  (more specific constraint)
```

**Contradiction Example**:
```sql
WHERE SUBSTRING(name, 1, 4) = '2000' AND SUBSTRING(name, 1, 4) = '1999'
```

```java
domain1 = RegexDomain("^2000.*$");
domain2 = RegexDomain("^1999.*$");
finalDomain = domain1.intersect(domain2);
→ isEmpty() == true  // No string can satisfy both constraints
```

---

### Multi-Variable and Multi-Table Extensibility

The architecture is designed for extension to multi-variable systems:

#### Fixed-Point Iteration (Design Pattern)

For queries with interdependent constraints:
```sql
WHERE users.age = orders.quantity * 2
  AND orders.quantity = users.age / 2
```

**Solving Strategy**:
1. Initialize domains: `users.age = all()`, `orders.quantity = all()`
2. **Iteration 1**:
   - From first constraint: `users.age ∈ [0, ∞)` (derived from `quantity * 2`)
   - From second constraint: `orders.quantity ∈ [0, ∞)` (derived from `age / 2`)
3. **Iteration 2**:
   - Refine `users.age` based on new `quantity` domain
   - Refine `quantity` based on new `age` domain
4. **Convergence**: Stop when domains no longer change

**Implementation** (conceptual extension):
```java
Map<Variable, Domain<?>> domains = initializeAll();

while (!converged) {
    Map<Variable, Domain<?>> newDomains = {};
    
    for (Constraint c : constraints) {
        Domain<?> refined = solveConstraint(c, domains);
        newDomains.merge(c.variable, refined, Domain::intersect);
    }
    
    if (newDomains.equals(domains)) break;  // Fixed point
    domains = newDomains;
}
```

#### Join Propagation

For equijoin constraints:
```sql
FROM T1 JOIN T2 ON T1.id = T2.id
WHERE LOWER(T1.name) = 'alice'
```

**Propagation**:
1. Solve for `T1.name`: `RegexDomain("^[aA][lL][iI][cC][eE]$")`
2. Equijoin constraint: `T1.id = T2.id`
   - If additional constraints exist on `T1.id`, propagate to `T2.id`
3. **Union across tables**: Sample from both tables consistently

---

### Key Design Properties

**Modularity**: Transformers are independent; adding new SQL functions requires no changes to the solver

**Correctness**: Symbolic inversion guarantees all generated values satisfy constraints

**Early Termination**: Empty domain detection avoids wasted computation on unsatisfiable queries

**Extensibility**: The algorithm naturally extends to:
- Multi-variable systems (via domain propagation)
- Multi-table queries (via join constraint propagation)
- Recursive constraints (via fixed-point iteration)

---

## 7. End-to-End Example: Full SQL Pipeline

Let's trace a complete query through the entire system.

### Query
```sql
SELECT * FROM users 
WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc' 
  AND age * 2 + 5 > 50
```

---

### Phase 1: Relational Preprocessing

**Step 1: Parse SQL → Calcite RelNode**
```
LogicalProject(name, age)
  LogicalFilter(AND(
    EQUALS(LOWER(SUBSTRING($0, 1, 3)), 'abc'),
    GREATER_THAN(PLUS(TIMES($1, 2), 5), 50)
  ))
    LogicalTableScan(table=[users])
```

**Step 2: Project Pull-Up**
```java
RelNode normalized = ProjectPullUpController.applyUntilFixedPoint(root);
```
Result: Projects moved to top, filters access base columns directly.

**Step 3: Canonical Predicate Extraction**
```java
CanonicalPredicateExtractor.Output extracted = 
    CanonicalPredicateExtractor.extract(normalized);
```
Output:
- `sequentialScans`: `[TableScan(users)]`
- `canonicalPredicates`: 
  - `EQUALS(LOWER(SUBSTRING(RexInputRef(0), 1, 3)), 'abc')`
  - `GREATER_THAN(PLUS(TIMES(RexInputRef(1), 2), 5), 50)`

**Step 4: DNF Conversion**
```java
DnfRewriter.Output dnf = DnfRewriter.convert(extracted, rexBuilder);
```
Output (single disjunct, since no OR):
- `disjuncts[0]`: `AND(pred1, pred2)`

---

### Phase 2: Symbolic Inversion

**Disjunct**: `AND(EQUALS(...), GREATER_THAN(...))`

We split conjuncts and solve independently, then intersect domains.

#### Constraint 1: `LOWER(SUBSTRING(name, 1, 3)) = 'abc'`

**LHS Expression**: `LOWER(SUBSTRING(RexInputRef(0), 1, 3))`
**RHS**: `'abc'`

**Initialize Output Domain**:
```java
Domain<?> outputDomain = RegexDomain.literal("abc");
// → RegexDomain("^abc$")
```

**Inversion**:
```java
Domain<?> domain = program.deriveInputDomain(lhs, outputDomain);
```

**Trace**:
1. **LOWER** inversion:
   - Input: `RegexDomain("^abc$")`
   - Output: `RegexDomain("^[aA][bB][cC]$")`

2. **SUBSTRING** inversion:
   - Input: `RegexDomain("^[aA][bB][cC]$")`
   - Output: `RegexDomain("^[aA][bB][cC].*$")`

3. **RexInputRef(0)** (base case):
   - Return: `RegexDomain("^[aA][bB][cC].*$")`

**Result**: `name ∈ RegexDomain("^[aA][bB][cC].*$")`

---

#### Constraint 2: `age * 2 + 5 > 50`

**Simplification**: Treat `>50` as `≥51`
**LHS Expression**: `PLUS(TIMES(RexInputRef(1), 2), 5)`
**RHS**: `IntegerDomain([51, Long.MAX_VALUE])`

**Inversion**:
```java
Domain<?> domain = program.deriveInputDomain(lhs, IntegerDomain.of(51, Long.MAX_VALUE));
```

**Trace**:
1. **PLUS** inversion:
   - Input: `IntegerDomain([51, ∞))`
   - Subtract 5: `IntegerDomain([46, ∞))`

2. **TIMES** inversion:
   - Input: `IntegerDomain([46, ∞))`
   - Divide by 2: `IntegerDomain([23, ∞))`  (rounding up for integer division)

3. **RexInputRef(1)** (base case):
   - Return: `IntegerDomain([23, ∞))`

**Result**: `age ∈ IntegerDomain([23, ∞))`

---

### Phase 3: Result Synthesis

**Final Domains**:
- `name ∈ RegexDomain("^[aA][bB][cC].*$")`
- `age ∈ IntegerDomain([23, ∞))`

**Sample Generation**:
```java
List<String> nameExamples = nameDomain.sample(5);
// → ["Abc", "ABC", "abc123", "AbC_test", "aBc"]

List<Long> ageExamples = ageDomain.sample(5);
// → [23, 24, 25, 30, 50]
```

**Test Data**:
```
| name      | age |
|-----------|-----|
| Abc       | 23  |
| ABC       | 30  |
| abc123    | 25  |
| AbC_test  | 50  |
| aBc       | 100 |
```

All rows satisfy both predicates by construction.

---

## 8. Extensibility & Future Directions

The system is designed as a **general-purpose SQL constraint solver** with the current implementation serving as proof of concept.

### Type System Expansion

**Additional Domain Types**:
- **DecimalDomain**: Fixed-precision arithmetic with rounding constraints
- **DateDomain**: Temporal intervals with calendar-aware arithmetic
- **TimestampDomain**: Microsecond-precision temporal constraints
- **BooleanDomain**: Three-valued logic (TRUE, FALSE, NULL)
- **BinaryDomain**: Byte array constraints with pattern matching

Each domain integrates via the `Domain<T, D>` interface.

---

### Advanced Transformers

**String Operations**:
- **CONCAT inversion**: Split patterns at known boundaries
- **REGEXP_EXTRACT**: Capture group-based constraint derivation
- **JSON_EXTRACT_PATH**: Nested structure constraint propagation

**Numeric Operations**:
- **Logarithmic/Exponential**: Non-linear inversion with approximation
- **Trigonometric**: Inverse trig functions with period handling
- **Statistical**: PERCENTILE, STDDEV constraints via distribution modeling

**Temporal Operations**:
- **Date arithmetic**: INTERVAL-based offset inversion
- **Timezone conversion**: Cross-timezone constraint propagation
- **Windowing**: Temporal range constraint derivation

---

### Multi-Table Inference

**Join Graph Analysis**:
- **Equijoin propagation**: Unify domains across `=` constraints
- **Foreign key inference**: Leverage schema metadata for constraint tightening
- **Multi-way joins**: Fixed-point iteration across join graphs

**Example**:
```sql
SELECT u.name, o.amount
FROM users u JOIN orders o ON u.id = o.user_id
WHERE LOWER(u.name) = 'alice' AND o.amount > 100
```

**Solving Strategy**:
1. Derive `u.name` constraint: `RegexDomain("^[aA][lL][iI][cC][eE]$")`
2. Derive `o.amount` constraint: `IntegerDomain([101, ∞))`
3. **Propagate via join**: If `u.id` has additional constraints, apply to `o.user_id`
4. Generate consistent cross-table samples

---

### Aggregate and Window Function Support

**Cardinality Constraints**:
- **COUNT(x) = n**: Ensure generated dataset has exactly `n` non-null values
- **SUM(x) = total**: Distribute total across multiple rows
- **AVG(x) = mean**: Generate values with specific mean

**Window Functions**:
- **ROW_NUMBER() = k**: Position-specific constraint
- **RANK() OVER (...)**: Ordering constraint inference
- **LAG/LEAD**: Inter-row dependency tracking

---

### Optimization and Performance

**Transformer Caching**:
- Memoize inversion results for repeated subexpressions
- Build expression DAGs to share computation

**Incremental Solving**:
- Propagate only changed constraints in iterative refinement
- Lazy evaluation of domains until sampling is requested

**Parallel Disjunct Solving**:
- Each DNF disjunct can be solved independently
- Parallelize across disjuncts and merge results

---

### Integration Points

**Schema-Aware Inference**:
- Leverage table constraints (PRIMARY KEY, UNIQUE, CHECK)
- Use column statistics for more realistic sampling

**Query Optimizer Integration**:
- Detect unsatisfiable queries at planning time
- Estimate cardinality from domain sizes
- Guide index selection based on constraint selectivity

**Test Data Generation Frameworks**:
- Generate minimal reproducing examples for query bugs
- Create edge-case datasets for regression testing
- Populate test databases with schema-compliant data

---

## 9. Reference Section

### Core Classes

#### Domain Layer

**`Domain<T, D>`** (`com.linkedin.coral.datagen.domain.Domain`)
- Abstract base class for all domain types
- Methods: `isEmpty()`, `intersect()`, `union()`, `sample()`, `isSingleton()`

**`RegexDomain`** (`com.linkedin.coral.datagen.domain.RegexDomain`)
- Automaton-backed string constraint representation
- Constructor: `new RegexDomain(String regex)`
- Factory: `RegexDomain.literal(String)`, `RegexDomain.empty()`

**`IntegerDomain`** (`com.linkedin.coral.datagen.domain.IntegerDomain`)
- Interval-based numeric constraint representation
- Factory: `IntegerDomain.of(long)`, `IntegerDomain.of(long, long)`, `IntegerDomain.empty()`
- Arithmetic: `add(long)`, `multiply(long)`

---

#### Transformer Layer

**`DomainTransformer`** (`com.linkedin.coral.datagen.domain.DomainTransformer`)
- Interface for symbolic inversion functions
- Methods:
  - `canHandle(RexNode)`: Pattern matching
  - `isVariableOperandPositionValid(RexNode)`: Validation
  - `getChildForVariable(RexNode)`: Traversal
  - `refineInputDomain(RexNode, Domain<?, ?>)`: Inversion

**Implemented Transformers**:
- `SubstringRegexTransformer`: Inverts `SUBSTRING(x, start, len)`
- `LowerRegexTransformer`: Inverts `LOWER(x)`
- `CastRegexTransformer`: Inverts `CAST(x AS type)` with cross-domain support
- `PlusRegexTransformer`: Inverts `x + constant`
- `TimesRegexTransformer`: Inverts `x * constant`

---

#### Solver Layer

**`DomainInferenceProgram`** (`com.linkedin.coral.datagen.domain.DomainInferenceProgram`)
- Orchestrates top-down symbolic inversion
- Constructor: `new DomainInferenceProgram(List<DomainTransformer>)`
- Methods:
  - `deriveInputDomain(RexNode, Domain<?, ?>)`: Generic solver
  - `deriveInputRegex(RexNode, RegexDomain)`: Type-specific convenience method
  - `deriveInputInteger(RexNode, IntegerDomain)`: Type-specific convenience method

---

#### Relational Preprocessing

**`ProjectPullUpController`** (`com.linkedin.coral.datagen.rel.ProjectPullUpController`)
- Fixed-point normalization of relational trees
- Method: `applyUntilFixedPoint(RelNode)`

**`CanonicalPredicateExtractor`** (`com.linkedin.coral.datagen.rel.CanonicalPredicateExtractor`)
- Extracts predicates with global index rewriting
- Method: `extract(RelNode)` → `Output{sequentialScans, canonicalPredicates}`

**`DnfRewriter`** (`com.linkedin.coral.datagen.rel.DnfRewriter`)
- Converts predicates to Disjunctive Normal Form
- Method: `convert(CanonicalPredicateExtractor.Output, RexBuilder)` → `Output{sequentialScans, disjuncts}`

---

### Usage Example

```java
// Setup
HiveToRelConverter converter = new HiveToRelConverter(mscAdapter);
DomainInferenceProgram program = new DomainInferenceProgram(
    Arrays.asList(
        new LowerRegexTransformer(),
        new SubstringRegexTransformer(),
        new CastRegexTransformer(),
        new PlusRegexTransformer(),
        new TimesRegexTransformer()
    )
);

// Parse SQL
String sql = "SELECT * FROM users WHERE LOWER(SUBSTRING(name, 1, 3)) = 'abc'";
RelNode relNode = converter.convertSql(sql);

// Normalize
RelNode normalized = ProjectPullUpController.applyUntilFixedPoint(relNode);

// Extract predicates
CanonicalPredicateExtractor.Output extracted = 
    CanonicalPredicateExtractor.extract(normalized);

// Convert to DNF
RexBuilder rexBuilder = relNode.getCluster().getRexBuilder();
DnfRewriter.Output dnf = DnfRewriter.convert(extracted, rexBuilder);

// Solve each disjunct
for (RexNode disjunct : dnf.disjuncts) {
    RexCall call = (RexCall) disjunct;
    RexNode lhs = call.getOperands().get(0);
    RexNode rhs = call.getOperands().get(1);
    
    // Create output domain from RHS
    RegexDomain outputDomain = RegexDomain.literal(rhsValue);
    
    // Derive input domain
    RegexDomain inputDomain = program.deriveInputRegex(lhs, outputDomain);
    
    // Generate samples
    List<String> samples = inputDomain.sample(10);
    System.out.println("Generated values: " + samples);
}
```

---

### Build and Test

**Build**:
```bash
./gradlew :coral-data-generation:build
```

**Run Tests**:
```bash
./gradlew :coral-data-generation:test
```

**Dependencies**:
- Apache Calcite (relational algebra)
- dk.brics.automaton (regex operations)
- Hive Metastore (schema access)

---

### Contributing

**Adding a New Domain Type**:
1. Extend `Domain<T, D>` with your value type
2. Implement `isEmpty()`, `intersect()`, `union()`, `sample()`, `isSingleton()`
3. Add conversion methods if cross-domain operations are needed

**Adding a New Transformer**:
1. Implement `DomainTransformer` interface
2. Define `canHandle()` to match your SQL operator
3. Implement `refineInputDomain()` with inversion logic
4. Register transformer in `DomainInferenceProgram` constructor
5. Add test cases in `RegexDomainInferenceProgramTest`

**Extending Relational Preprocessing**:
1. Add new rewrite rules to `ProjectPullUpRewriter` for complex SQL constructs
2. Extend `CanonicalPredicateExtractor` for new predicate types (e.g., EXISTS, IN)
3. Ensure global index consistency is maintained

---

### License

Coral Data Generation is licensed under the BSD-2 Clause license. See `LICENSE` file for details.

---

**For questions or contributions, refer to the main [Coral repository](https://github.com/linkedin/coral).**
