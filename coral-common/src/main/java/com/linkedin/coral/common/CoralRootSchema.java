/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;

import com.linkedin.coral.common.catalog.CoralCatalog;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Coral's root-level Calcite adapter for {@link CoralCatalog} integration.
 * 
 * <p>This is the modern iteration of Coral's Calcite schema adapter that bridges
 * {@link CoralCatalog} to Calcite's {@link Schema} interface. It represents the "root" schema
 * that holds all databases as subschemas and contains no tables directly.
 * 
 * <p><b>Multi-format Support:</b> This class supports multiple table formats (Hive, Iceberg, etc.)
 * through the unified {@link CoralCatalog} interface, enabling format-agnostic SQL query processing.
 * 
 * <p><b>Schema Name:</b> The schema name remains "hive" for backward compatibility with existing
 * SQL queries and qualified table names, even though this class supports multiple formats beyond Hive.
 * 
 * <p><b>Relationship to HiveSchema:</b>
 * <ul>
 *   <li>{@link CoralRootSchema} (this class) - Modern CoralCatalog-based integration (new code)</li>
 *   <li>{@link HiveSchema} - Legacy dual-mode adapter supporting both CoralCatalog and HiveMetastoreClient (backward compatibility)</li>
 * </ul>
 * 
 * <p><b>Usage:</b> This class is instantiated by {@link ToRelConverter} when constructed with a
 * {@link CoralCatalog} parameter.
 * 
 * @see CoralDatabaseSchema Database-level adapter (contains tables)
 * @see HiveSchema Legacy adapter with HiveMetastoreClient support
 * @see CoralCatalog Unified catalog interface
 */
public class CoralRootSchema implements Schema {

  /**
   * Schema name used in Calcite (e.g., "hive"."database"."table").
   * Remains "hive" for backward compatibility despite multi-format support.
   */
  public static final String ROOT_SCHEMA = "hive";

  /**
   * Default database name.
   */
  public static final String DEFAULT_DB = "default";

  private final CoralCatalog coralCatalog;

  /**
   * Creates a CoralRootSchema using CoralCatalog for unified multi-format table access.
   *
   * @param coralCatalog Coral catalog providing unified access to Hive, Iceberg, and other table formats
   */
  public CoralRootSchema(@Nonnull CoralCatalog coralCatalog) {
    this.coralCatalog = checkNotNull(coralCatalog, "coralCatalog cannot be null");
  }

  /**
   * This always returns null as root schema does not have tables.
   * Tables are contained in database-level subschemas.
   * 
   * @param name Table name
   * @return null (root schema has no tables)
   */
  @Override
  public Table getTable(String name) {
    return null;
  }

  @Override
  public Set<String> getTableNames() {
    return ImmutableSet.of();
  }

  @Override
  public RelProtoDataType getType(String s) {
    return null;
  }

  @Override
  public Set<String> getTypeNames() {
    return null;
  }

  @Override
  public Collection<Function> getFunctions(String name) {
    return ImmutableList.of();
  }

  @Override
  public Set<String> getFunctionNames() {
    return ImmutableSet.of();
  }

  /**
   * Returns a database-level subschema if the database exists.
   * 
   * @param name Database/namespace name
   * @return CoralDatabaseSchema for the database, or null if database doesn't exist
   */
  @Override
  public Schema getSubSchema(String name) {
    if (!coralCatalog.namespaceExists(name)) {
      return null;
    }
    return new CoralDatabaseSchema(coralCatalog, name);
  }

  /**
   * Returns all database/namespace names in the catalog.
   * 
   * @return Set of database names
   */
  @Override
  public Set<String> getSubSchemaNames() {
    return ImmutableSet.copyOf(coralCatalog.getAllNamespaces());
  }

  @Override
  public Expression getExpression(SchemaPlus parentSchema, String name) {
    return null;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  // TODO: This needs to be snapshot of current state of catalog
  @Override
  public Schema snapshot(SchemaVersion schemaVersion) {
    return this;
  }
}
