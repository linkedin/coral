# Coral Benchmark: Cross-Dialect Integration Testing Framework

## 1. Purpose

A new module (`coral-benchmark`) that tests Coral translations end-to-end: from any supported source dialect to any supported target dialect. The framework verifies both **syntactic correctness** (the translated query is valid in the target dialect) and **semantic correctness** (the translated query produces equivalent results on real engine execution).

## 2. Design Principles

- **Grounded in existing APIs.** The framework builds on `CoralCatalog`, `CoralTable`, and the Coral type system (`CoralDataType`, `CoralTypeKind`). It does not invent parallel abstractions for catalog or types.
- **In-memory by default.** Tests run against an in-memory `CoralCatalog` implementation with no external metastore dependency.
- **Dialect-agnostic core, dialect-specific plugins.** The core framework knows nothing about Hive, Spark, or Trino. Each dialect contributes an implementation of a small SPI that the core orchestrates.
- **Incremental verification levels.** Users choose the level of verification appropriate to their needs, from pure IR round-trip checks up to full result-set comparison on live engines.

## 3. Core Concepts

The framework is organized around a single orchestrator (`TranslationTestSuite`) that
drives a corpus of SQL queries through pluggable translation and execution stages,
comparing results via a configurable comparator. The major components and their
relationships:

```
                       +-----------------------------+
                       |   TranslationTestSuite      |
                       |       (orchestrator)        |
                       +--+--------+--------+--------+
                          |        |        |
                    uses  |        |        |  uses
                          v        v        v
                +-----------+ +-------+ +---------------------+
                |  Catalog  | |  SPI  | | ResultSetComparator |
                | (schemas) | | impls | |   (Level 3 only)    |
                +-----------+ +---+---+ +---------------------+
                                  |
                          +-------+--------+
                          v                v
                  +---------------+ +--------------+
                  | DialectPlugin | | EnginePlugin |
                  |  (translate)  | |  (execute)   |
                  +---------------+ +--------------+
```

The orchestrator iterates over `.sql` files from the configured query directory,
runs each through the appropriate plugins for the requested verification level, and
emits a `QueryTestResult` per query plus an aggregate `TestReport`.

### 3.1 Catalog Setup

Tests declare their table schemas using the Coral type system and register them in an in-memory catalog.

**In-memory catalog.** A concrete `InMemoryCatalog implements CoralCatalog` that holds tables in a `Map<namespace, Map<tableName, CoralTable>>`. Provides a builder API for test ergonomics:

```java
InMemoryCatalog catalog = InMemoryCatalog.builder()
    .createNamespace("db")
    .addTable("db", "users", StructType.of(Arrays.asList(
        StructField.of("id", PrimitiveType.of(CoralTypeKind.INT, true)),
        StructField.of("name", PrimitiveType.of(CoralTypeKind.STRING, true)),
        StructField.of("created", TimestampType.of(3, true)),
        StructField.of("tags", ArrayType.of(PrimitiveType.of(CoralTypeKind.STRING, true), true))
    ), true))
    .addTable("db", "events", StructType.of(Arrays.asList(
        StructField.of("user_id", PrimitiveType.of(CoralTypeKind.INT, true)),
        StructField.of("event_type", PrimitiveType.of(CoralTypeKind.STRING, true)),
        StructField.of("payload", MapType.of(
            PrimitiveType.of(CoralTypeKind.STRING, true),
            PrimitiveType.of(CoralTypeKind.STRING, true), true)),
        StructField.of("ts", TimestampType.of(3, true))
    ), true))
    .build();
```

All column types are expressed through the existing Coral type hierarchy (`PrimitiveType`, `StructType`, `ArrayType`, `MapType`, `DecimalType`, `TimestampType`, etc.). No raw strings for types.

### 3.2 Query Corpus

Queries are plain SQL SELECT statements stored as individual `.sql` files organized by source dialect:

```
coral-benchmark/src/test/resources/
  queries/
    hive/
      group_by_count.sql
      join_with_filter.sql
      nested_struct_access.sql
    trino/
      group_by_count.sql
      ...
    spark/
      ...
```

Each file contains a single SELECT statement written in the source dialect's syntax. File names are descriptive of the query pattern being tested. The same logical query may appear under multiple dialect directories, written in each dialect's native syntax.

### 3.3 Dialect SPI

Each dialect plugs into the framework via a *provider/plugin* pair. The provider is the
ServiceLoader-discoverable entry point (no-arg constructor); the plugin is the
fully-constructed translator bound to a catalog. This split keeps the plugin immutable
(catalog as a `final` field, no two-phase `init`) while still supporting standard SPI
discovery.

```java
public interface DialectPluginProvider {

    /** Identifier for the dialect this provider produces plugins for. */
    Dialect dialect();

    /** Construct a plugin bound to the given catalog. */
    DialectPlugin create(CoralCatalog catalog);
}

public interface DialectPlugin {

    /** Identifier for this dialect (e.g., HIVE, SPARK, TRINO). */
    Dialect dialect();

    /** Parse a SQL string in this dialect and produce a Coral IR RelNode. */
    RelNode toRelNode(String sql);

    /** Convert a Coral IR RelNode to a SQL string in this dialect. */
    String toDialectSql(RelNode relNode);
}
```

Provider implementations are typically three lines, e.g.
`new HiveDialectPlugin(catalog)`. Plugins themselves wrap the existing converters:
- **Hive**: `HiveToRelConverter` / `CoralRelToSqlNodeConverter` (Hive dialect)
- **Trino**: `TrinoToRelConverter` / `RelToTrinoConverter`
- **Spark**: `HiveToRelConverter` (Spark SQL parses as Hive) / `CoralSpark`

Providers are discovered by `ServiceLoader` (via
`META-INF/services/com.linkedin.coral.benchmark.spi.DialectPluginProvider`) or registered
explicitly on the suite builder. The framework calls `provider.create(catalog)` once per
suite to materialize the plugin.

### 3.4 Engine SPI (for execution-level verification)

For tests that go beyond syntactic validation and actually execute queries, each engine provides:

```java
public interface EnginePlugin {

    /** Which dialect this engine natively executes. */
    Dialect dialect();

    /** Start the engine (may be a no-op for remote engines). */
    void start();

    /** Create the given table schema in the engine's catalog, ready for data loading. */
    void createTable(String namespace, String tableName, CoralDataType schema);

    /** Load row data into a previously created table. */
    void loadData(String namespace, String tableName, RowSet data);

    /** Run EXPLAIN on a query and return success/failure. Validates syntax + planning. */
    ExplainResult explain(String sql);

    /** Execute a query and return its result set. */
    ResultSet execute(String sql);

    /** Tear down the engine. */
    void stop();
}
```

Engine implementations are expected for embedded/local versions of:
- **Spark**: embedded SparkSession
- **Trino**: Trino test harness / in-memory connector
- **Hive**: embedded HiveServer2 or Tez local mode

The `EnginePlugin` is optional. Tests that only verify translation correctness at the IR level do not need engine plugins.

### 3.5 Test Data

A `RowSet` abstraction carries typed tabular data for loading into engines:

```java
RowSet userData = RowSet.builder(usersSchema)   // usersSchema is the StructType from catalog setup
    .addRow(1, "alice", Timestamp.valueOf("2024-01-15 10:00:00"), Arrays.asList("admin", "user"))
    .addRow(2, "bob",   Timestamp.valueOf("2024-03-20 14:30:00"), Arrays.asList("user"))
    .build();
```

Values are Java objects matching the Coral type mapping (INT -> Integer, STRING -> String, ARRAY -> List, MAP -> Map, STRUCT -> Object[], etc.).

## 4. Verification Levels

The framework supports three escalating levels of verification. Each level subsumes the
ones before it: passing Level 3 implies Level 2 also passed, which implies Level 1.

```
  Level 1 (TRANSLATION):

      Source SQL --[toRelNode]--> IR --[toDialectSql]--> Target SQL


  Level 2 (EXPLAIN):  Level 1, plus:

      Target SQL --[targetEngine.explain]--> ExplainResult


  Level 3 (RESULT_SET):  Level 2, plus:

      sourceEngine.execute(Source SQL) ---> ResultSet A
                                                         \
                                                          ResultSetComparator
                                                         /
      targetEngine.execute(Target SQL) ---> ResultSet B
```

Each step up the ladder requires more setup:

| Level       | Engines required | Test data required |
| ----------- | ---------------- | ------------------ |
| TRANSLATION | none             | no                 |
| EXPLAIN     | target only      | no                 |
| RESULT_SET  | source + target  | yes                |

### Level 1: Translation (IR round-trip)

Translates a query from a source dialect to the target dialect through Coral IR. Verifies that the translation pipeline completes without error and produces non-empty SQL.

```
Source SQL --[toRelNode]--> RelNode --[toDialectSql]--> Target SQL
```

**What it catches:** Parser failures, unsupported SQL constructs, operator mapping gaps, type conversion errors.

### Level 2: Syntactic Validation (EXPLAIN)

Takes the translated SQL and runs it through the target engine's EXPLAIN. This validates that the target engine can parse and plan the query against the declared schema.

```
Target SQL --[engine.explain]--> ExplainResult (success/failure + plan)
```

**What it catches:** Dialect-specific syntax errors the Coral converter missed, schema mismatches, unresolved functions.

### Level 3: Semantic Validation (result-set comparison)

Loads test data into both source and target engines, executes the original query on the source engine and the translated query on the target engine, then compares result sets.

```
Source Engine: execute(sourceSQL, sourceData) --> ResultSet A
Target Engine: execute(targetSQL, targetData) --> ResultSet B
compare(A, B) --> equivalent?
```

**What it catches:** Subtle semantic differences in function behavior, NULL handling, type coercion, ordering, and precision across engines.

## 5. Test Suite Construction

A `TranslationTestSuite` is parameterized by source dialect, target dialect, and verification level:

```java
TranslationTestSuite suite = TranslationTestSuite.builder()
    .source(Dialect.HIVE)
    .target(Dialect.TRINO)
    .catalog(catalog)                          // InMemoryCatalog from Section 3.1
    .queryDir("queries/hive")                  // directory of .sql files
    .verificationLevel(VerificationLevel.EXPLAIN)
    .build();

suite.run();  // returns TestReport with per-query pass/fail + details
```

For result-set comparison:

```java
TranslationTestSuite suite = TranslationTestSuite.builder()
    .source(Dialect.HIVE)
    .target(Dialect.TRINO)
    .catalog(catalog)
    .queryDir("queries/hive")
    .verificationLevel(VerificationLevel.RESULT_SET)
    .testData(Map.of(
        "db.users", userData,
        "db.events", eventData
    ))
    .sourceEngine(new SparkEnginePlugin())     // Spark executes Hive SQL natively
    .targetEngine(new TrinoEnginePlugin())
    .build();
```

The suite iterates over all `.sql` files in the query directory and runs each through the configured pipeline.

## 6. Comparison Semantics

Result-set comparison must handle real-world engine differences:

- **Row ordering:** Unordered by default (compare as sets). Ordered comparison only when the query has an explicit ORDER BY.
- **Floating-point tolerance:** Configurable epsilon for FLOAT/DOUBLE comparisons.
- **NULL equivalence:** Two NULLs in the same position are treated as equal for comparison purposes.
- **Timestamp precision:** Normalize to the lower precision of the two engines before comparison.
- **Type widening:** Allow safe promotions (e.g., INT vs BIGINT) in the comparison, flag unsafe mismatches.

## 7. Reporting

`TestReport` provides structured output:

- Per-query: status (PASS/FAIL/SKIP/ERROR), source SQL, translated SQL, and failure details (diff of result sets, exception stack traces, EXPLAIN output).
- Aggregate: pass rate by dialect pair, failure categories (translation error, explain failure, result mismatch), regression tracking across runs.

## 8. Module Structure

This section describes only the framework code that ships in this module. Concrete
`DialectPlugin` and `EnginePlugin` implementations are deferred to follow-up changes;
their hosting module is intentionally left undecided here so it can be chosen against
real implementations rather than against speculation.

```
coral-benchmark/
  src/main/java/com/linkedin/coral/benchmark/
    catalog/
      InMemoryCatalog.java          # CoralCatalog impl for tests
      InMemoryTable.java            # CoralTable impl for tests
    spi/
      Dialect.java                  # Enum: HIVE, SPARK, TRINO, ...
      DialectPluginProvider.java    # Translation SPI - ServiceLoader entry point
      DialectPlugin.java            # Translation SPI - catalog-bound plugin
      EnginePlugin.java             # Execution SPI
      VerificationLevel.java        # Enum: TRANSLATION, EXPLAIN, RESULT_SET
    data/
      ExplainResult.java            # Result of running EXPLAIN
      ResultSet.java                # Query result container
      RowSet.java                   # Typed tabular test data
    comparison/
      ComparisonConfig.java         # Tolerances, ordering, type widening
      ComparisonResult.java         # Outcome of comparing two result sets
      ResultSetComparator.java      # Configurable comparison logic
    suite/
      QueryTestResult.java          # Per-query test outcome
      TestReport.java               # Aggregate results
      TranslationTestSuite.java     # Main orchestrator
  src/test/resources/
    queries/
      hive/
      trino/
      spark/
```

## 9. Dependencies

- `coral-common` (CoralCatalog, CoralTable, CoralDataType, type hierarchy)

## 10. Non-Goals (for initial version)

- **DDL/DML translation testing.** Only SELECT queries are in scope.
- **Performance benchmarking.** This is a correctness framework, not a latency benchmark.
- **Production metastore integration.** Tests use in-memory catalogs only.
- **View resolution.** Queries reference base tables, not views-on-views. View expansion is tested separately in existing module tests.
