/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.benchmark.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.StructType;


/**
 * An in-memory implementation of {@link CoralCatalog} for benchmark tests.
 *
 * <p>Holds namespaces and tables in memory with no external metastore dependency.
 * Tables are registered using the Coral type system ({@link StructType},
 * {@link com.linkedin.coral.common.types.PrimitiveType}, etc.).
 *
 * <p>Usage:
 * <pre>{@code
 * InMemoryCatalog catalog = InMemoryCatalog.builder()
 *     .createNamespace("db")
 *     .addTable("db", "users", StructType.of(
 *         Arrays.asList(
 *             StructField.of("id", PrimitiveType.of(CoralTypeKind.INT, true)),
 *             StructField.of("name", PrimitiveType.of(CoralTypeKind.STRING, true))
 *         ), true))
 *     .build();
 * }</pre>
 */
public final class InMemoryCatalog implements CoralCatalog {

  private final Map<String, Map<String, CoralTable>> namespaces;

  private InMemoryCatalog(Map<String, Map<String, CoralTable>> namespaces) {
    // Deep-copy to make the catalog immutable after construction
    Map<String, Map<String, CoralTable>> copy = new LinkedHashMap<>();
    for (Map.Entry<String, Map<String, CoralTable>> entry : namespaces.entrySet()) {
      copy.put(entry.getKey(), Collections.unmodifiableMap(new LinkedHashMap<>(entry.getValue())));
    }
    this.namespaces = Collections.unmodifiableMap(copy);
  }

  @Override
  public CoralTable getTable(String namespace, String tableName) {
    Map<String, CoralTable> tables = namespaces.get(namespace);
    return tables != null ? tables.get(tableName) : null;
  }

  @Override
  public boolean namespaceExists(String namespace) {
    return namespaces.containsKey(namespace);
  }

  @Override
  public List<String> getAllTables(String namespace) {
    Map<String, CoralTable> tables = namespaces.get(namespace);
    return tables != null
        ? Collections.unmodifiableList(new ArrayList<>(tables.keySet()))
        : Collections.emptyList();
  }

  @Override
  public List<String> getAllNamespaces() {
    return Collections.unmodifiableList(new ArrayList<>(namespaces.keySet()));
  }

  /**
   * Creates a new builder for constructing an InMemoryCatalog.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for constructing an {@link InMemoryCatalog}.
   *
   * <p>Namespaces are created explicitly via {@link #createNamespace(String)}.
   * Tables are added to existing namespaces via {@link #addTable(String, String, StructType)}
   * or {@link #addTable(String, String, StructType, Map)}.
   */
  public static final class Builder {

    private final Map<String, Map<String, CoralTable>> namespaces = new LinkedHashMap<>();

    private Builder() {
    }

    /**
     * Creates a namespace (database) in the catalog.
     *
     * @param namespace the namespace name
     * @return this builder
     * @throws IllegalArgumentException if the namespace already exists
     */
    public Builder createNamespace(String namespace) {
      Objects.requireNonNull(namespace, "Namespace cannot be null");
      if (namespaces.containsKey(namespace)) {
        throw new IllegalArgumentException("Namespace already exists: " + namespace);
      }
      namespaces.put(namespace, new LinkedHashMap<>());
      return this;
    }

    /**
     * Adds a table with the given schema and no extra properties.
     *
     * @param namespace the namespace (must already exist via {@link #createNamespace})
     * @param tableName the table name
     * @param schema    the table schema
     * @return this builder
     * @throws IllegalArgumentException if the namespace does not exist or the table already exists
     */
    public Builder addTable(String namespace, String tableName, StructType schema) {
      return addTable(namespace, tableName, schema, Collections.emptyMap());
    }

    /**
     * Adds a table with the given schema and properties.
     *
     * @param namespace  the namespace (must already exist via {@link #createNamespace})
     * @param tableName  the table name
     * @param schema     the table schema
     * @param properties table properties
     * @return this builder
     * @throws IllegalArgumentException if the namespace does not exist or the table already exists
     */
    public Builder addTable(String namespace, String tableName, StructType schema, Map<String, String> properties) {
      Objects.requireNonNull(namespace, "Namespace cannot be null");
      Objects.requireNonNull(tableName, "Table name cannot be null");
      Objects.requireNonNull(schema, "Schema cannot be null");
      Objects.requireNonNull(properties, "Properties cannot be null");

      Map<String, CoralTable> tables = namespaces.get(namespace);
      if (tables == null) {
        throw new IllegalArgumentException("Namespace does not exist: " + namespace
            + ". Call createNamespace() first.");
      }
      if (tables.containsKey(tableName)) {
        throw new IllegalArgumentException("Table already exists: " + namespace + "." + tableName);
      }

      tables.put(tableName, new InMemoryTable(namespace, tableName, schema, properties));
      return this;
    }

    /**
     * Builds the catalog. The resulting catalog is immutable.
     *
     * @return a new InMemoryCatalog
     */
    public InMemoryCatalog build() {
      return new InMemoryCatalog(namespaces);
    }
  }
}
