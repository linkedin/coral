/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.suite;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.linkedin.coral.benchmark.comparison.ComparisonConfig;
import com.linkedin.coral.benchmark.data.RowSet;
import com.linkedin.coral.benchmark.spi.Dialect;
import com.linkedin.coral.benchmark.spi.DialectPlugin;
import com.linkedin.coral.benchmark.spi.EnginePlugin;
import com.linkedin.coral.benchmark.spi.VerificationLevel;
import com.linkedin.coral.common.catalog.CoralCatalog;


/**
 * Main orchestrator for cross-dialect translation benchmark tests.
 *
 * <p>A test suite is parameterized by source dialect, target dialect, verification level,
 * and the catalog/data/engines needed for that level. It reads all {@code .sql} files from
 * a query directory, translates each through the Coral IR pipeline, and verifies the result
 * at the configured level.
 *
 * <p>Usage (Level 1 - translation only):
 * <pre>{@code
 * TranslationTestSuite suite = TranslationTestSuite.builder()
 *     .source(Dialect.HIVE)
 *     .target(Dialect.TRINO)
 *     .catalog(catalog)
 *     .queryDir("queries/hive")
 *     .verificationLevel(VerificationLevel.TRANSLATION)
 *     .build();
 *
 * TestReport report = suite.run();
 * }</pre>
 *
 * <p>Usage (Level 2 - EXPLAIN):
 * <pre>{@code
 * TranslationTestSuite suite = TranslationTestSuite.builder()
 *     .source(Dialect.HIVE)
 *     .target(Dialect.TRINO)
 *     .catalog(catalog)
 *     .queryDir("queries/hive")
 *     .verificationLevel(VerificationLevel.EXPLAIN)
 *     .targetEngine(new TrinoEnginePlugin())
 *     .build();
 *
 * TestReport report = suite.run();
 * }</pre>
 *
 * <p>Usage (Level 3 - result set comparison):
 * <pre>{@code
 * TranslationTestSuite suite = TranslationTestSuite.builder()
 *     .source(Dialect.HIVE)
 *     .target(Dialect.TRINO)
 *     .catalog(catalog)
 *     .queryDir("queries/hive")
 *     .verificationLevel(VerificationLevel.RESULT_SET)
 *     .testData("db.users", userData)
 *     .testData("db.events", eventData)
 *     .sourceEngine(new SparkEnginePlugin())
 *     .targetEngine(new TrinoEnginePlugin())
 *     .comparisonConfig(ComparisonConfig.builder()
 *         .floatingPointEpsilon(1e-6)
 *         .build())
 *     .build();
 *
 * TestReport report = suite.run();
 * }</pre>
 */
public final class TranslationTestSuite {

  private final Dialect source;
  private final Dialect target;
  private final CoralCatalog catalog;
  private final String queryDir;
  private final VerificationLevel verificationLevel;
  private final DialectPlugin sourcePlugin;
  private final DialectPlugin targetPlugin;
  private final EnginePlugin sourceEngine;
  private final EnginePlugin targetEngine;
  private final Map<String, RowSet> testData;
  private final ComparisonConfig comparisonConfig;

  private TranslationTestSuite(Builder builder) {
    this.source = builder.source;
    this.target = builder.target;
    this.catalog = builder.catalog;
    this.queryDir = builder.queryDir;
    this.verificationLevel = builder.verificationLevel;
    this.sourcePlugin = builder.sourcePlugin;
    this.targetPlugin = builder.targetPlugin;
    this.sourceEngine = builder.sourceEngine;
    this.targetEngine = builder.targetEngine;
    this.testData = Collections.unmodifiableMap(new HashMap<>(builder.testData));
    this.comparisonConfig = builder.comparisonConfig;
  }

  /**
   * Runs the test suite: reads all .sql files from the query directory, translates each,
   * and verifies at the configured level.
   *
   * <p>For each query file, the pipeline is:
   * <ol>
   *   <li><b>TRANSLATION:</b> sourcePlugin.toRelNode(sql) then targetPlugin.toDialectSql(relNode)</li>
   *   <li><b>EXPLAIN:</b> (1) + targetEngine.explain(translatedSql)</li>
   *   <li><b>RESULT_SET:</b> (1) + (2) + sourceEngine.execute(sourceSql) vs
   *       targetEngine.execute(translatedSql), compared via ResultSetComparator</li>
   * </ol>
   *
   * <p>Engine lifecycle is managed automatically: start() is called before the first query,
   * and stop() is called after the last query, even if exceptions occur.
   *
   * @return the test report with per-query results and aggregate statistics
   */
  public TestReport run() {
    // Implementation will:
    // 1. Discover/load .sql files from queryDir
    // 2. Start engines if needed (EXPLAIN or RESULT_SET level)
    // 3. For RESULT_SET: create tables and load test data into both engines
    // 4. For each query: translate, verify at each level up to requested, collect result
    // 5. Stop engines
    // 6. Build and return TestReport
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Creates a new builder.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for {@link TranslationTestSuite}.
   *
   * <p>Required for all levels: source, target, catalog, queryDir, verificationLevel.
   * <p>Required for EXPLAIN: targetEngine (or both sourcePlugin and targetPlugin if not using ServiceLoader).
   * <p>Required for RESULT_SET: sourceEngine, targetEngine, testData.
   */
  public static final class Builder {
    private Dialect source;
    private Dialect target;
    private CoralCatalog catalog;
    private String queryDir;
    private VerificationLevel verificationLevel;
    private DialectPlugin sourcePlugin;
    private DialectPlugin targetPlugin;
    private EnginePlugin sourceEngine;
    private EnginePlugin targetEngine;
    private final Map<String, RowSet> testData = new HashMap<>();
    private ComparisonConfig comparisonConfig = ComparisonConfig.defaults();

    private Builder() {
    }

    /**
     * Sets the source dialect.
     *
     * @param source the dialect of the input queries
     * @return this builder
     */
    public Builder source(Dialect source) {
      this.source = Objects.requireNonNull(source);
      return this;
    }

    /**
     * Sets the target dialect.
     *
     * @param target the dialect to translate queries into
     * @return this builder
     */
    public Builder target(Dialect target) {
      this.target = Objects.requireNonNull(target);
      return this;
    }

    /**
     * Sets the catalog providing table metadata for query resolution.
     *
     * @param catalog the catalog (typically an {@link com.linkedin.coral.benchmark.catalog.InMemoryCatalog})
     * @return this builder
     */
    public Builder catalog(CoralCatalog catalog) {
      this.catalog = Objects.requireNonNull(catalog);
      return this;
    }

    /**
     * Sets the directory containing .sql query files.
     * Path is relative to the classpath (test resources).
     *
     * @param queryDir the directory path (e.g., "queries/hive")
     * @return this builder
     */
    public Builder queryDir(String queryDir) {
      this.queryDir = Objects.requireNonNull(queryDir);
      return this;
    }

    /**
     * Sets the verification level.
     *
     * @param level the level of verification to perform
     * @return this builder
     */
    public Builder verificationLevel(VerificationLevel level) {
      this.verificationLevel = Objects.requireNonNull(level);
      return this;
    }

    /**
     * Explicitly sets the source dialect plugin. If not set, the plugin is resolved
     * via {@link java.util.ServiceLoader} based on the source dialect.
     *
     * @param plugin the source dialect plugin
     * @return this builder
     */
    public Builder sourcePlugin(DialectPlugin plugin) {
      this.sourcePlugin = Objects.requireNonNull(plugin);
      return this;
    }

    /**
     * Explicitly sets the target dialect plugin. If not set, the plugin is resolved
     * via {@link java.util.ServiceLoader} based on the target dialect.
     *
     * @param plugin the target dialect plugin
     * @return this builder
     */
    public Builder targetPlugin(DialectPlugin plugin) {
      this.targetPlugin = Objects.requireNonNull(plugin);
      return this;
    }

    /**
     * Sets the engine for executing queries in the source dialect.
     * Required for {@link VerificationLevel#RESULT_SET}.
     *
     * @param engine the source engine
     * @return this builder
     */
    public Builder sourceEngine(EnginePlugin engine) {
      this.sourceEngine = Objects.requireNonNull(engine);
      return this;
    }

    /**
     * Sets the engine for executing queries in the target dialect.
     * Required for {@link VerificationLevel#EXPLAIN} and {@link VerificationLevel#RESULT_SET}.
     *
     * @param engine the target engine
     * @return this builder
     */
    public Builder targetEngine(EnginePlugin engine) {
      this.targetEngine = Objects.requireNonNull(engine);
      return this;
    }

    /**
     * Adds test data for a table. The key is the fully qualified table name
     * (e.g., "db.users"). Required for {@link VerificationLevel#RESULT_SET}.
     *
     * @param qualifiedTableName the fully qualified table name ("namespace.table")
     * @param data               the row data
     * @return this builder
     */
    public Builder testData(String qualifiedTableName, RowSet data) {
      Objects.requireNonNull(qualifiedTableName);
      Objects.requireNonNull(data);
      this.testData.put(qualifiedTableName, data);
      return this;
    }

    /**
     * Adds test data for multiple tables at once.
     *
     * @param testData a map from fully qualified table names to row data
     * @return this builder
     */
    public Builder testData(Map<String, RowSet> testData) {
      Objects.requireNonNull(testData);
      this.testData.putAll(testData);
      return this;
    }

    /**
     * Sets the comparison config for result-set comparison. Defaults to
     * {@link ComparisonConfig#defaults()} if not set.
     *
     * @param config the comparison config
     * @return this builder
     */
    public Builder comparisonConfig(ComparisonConfig config) {
      this.comparisonConfig = Objects.requireNonNull(config);
      return this;
    }

    /**
     * Builds the test suite, validating that all required configuration is present
     * for the requested verification level.
     *
     * @return a new TranslationTestSuite
     * @throws IllegalStateException if required configuration is missing
     */
    public TranslationTestSuite build() {
      Objects.requireNonNull(source, "Source dialect is required");
      Objects.requireNonNull(target, "Target dialect is required");
      Objects.requireNonNull(catalog, "Catalog is required");
      Objects.requireNonNull(queryDir, "Query directory is required");
      Objects.requireNonNull(verificationLevel, "Verification level is required");

      if (verificationLevel.ordinal() >= VerificationLevel.EXPLAIN.ordinal() && targetEngine == null) {
        throw new IllegalStateException("Target engine is required for verification level " + verificationLevel);
      }

      if (verificationLevel == VerificationLevel.RESULT_SET) {
        if (sourceEngine == null) {
          throw new IllegalStateException("Source engine is required for RESULT_SET verification");
        }
        if (testData.isEmpty()) {
          throw new IllegalStateException("Test data is required for RESULT_SET verification");
        }
      }

      return new TranslationTestSuite(this);
    }
  }
}
