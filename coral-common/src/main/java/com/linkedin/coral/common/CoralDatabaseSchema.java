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
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.HiveTable;
import com.linkedin.coral.common.catalog.IcebergTable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.linkedin.coral.common.catalog.TableType.VIEW;


/**
 * Coral's database-level Calcite adapter for {@link CoralCatalog} integration.
 *
 * <p>This is the modern iteration of Coral's database-level schema adapter that bridges
 * {@link CoralCatalog} to Calcite's {@link Schema} interface. It represents a specific database/namespace
 * and dispatches table lookups to the appropriate format-specific implementations.
 *
 * <p><b>Multi-format Dispatch:</b> This class automatically dispatches to the correct table implementation
 * based on the underlying table format:
 * <ul>
 *   <li>{@link IcebergCalciteTableAdapter} for Iceberg tables ({@link IcebergTable})</li>
 *   <li>{@link HiveCalciteTableAdapter} for Hive tables ({@link HiveTable})</li>
 *   <li>{@link HiveCalciteViewAdapter} for Hive views ({@link HiveTable} with VIEW type)</li>
 * </ul>
 *
 * <p><b>Relationship to HiveDbSchema:</b>
 * <ul>
 *   <li>{@link CoralDatabaseSchema} (this class) - Modern CoralCatalog-based integration (new code)</li>
 *   <li>{@link HiveDbSchema} - Legacy dual-mode adapter supporting both CoralCatalog and HiveMetastoreClient (backward compatibility)</li>
 * </ul>
 *
 * <p><b>Usage:</b> This class is instantiated by {@link CoralRootSchema} when a database subschema is requested.
 *
 * @see CoralRootSchema Root-level adapter (contains databases)
 * @see HiveDbSchema Legacy adapter with HiveMetastoreClient support
 * @see CoralCatalog Unified catalog interface
 */
public class CoralDatabaseSchema implements Schema {

  /**
   * Default database name.
   */
  public static final String DEFAULT_DB = "default";

  private final CoralCatalog coralCatalog;
  private final String dbName;

  /**
   * Creates a CoralDatabaseSchema for a specific database using CoralCatalog.
   *
   * @param coralCatalog Coral catalog providing unified access to tables
   * @param dbName Database name (must not be null)
   */
  CoralDatabaseSchema(@Nonnull CoralCatalog coralCatalog, @Nonnull String dbName) {
    this.coralCatalog = checkNotNull(coralCatalog, "coralCatalog cannot be null");
    this.dbName = checkNotNull(dbName, "dbName cannot be null");
  }

  /**
   * Returns a Calcite Table for the specified table name.
   *
   * <p>This method performs format-aware dispatch:
   * <ul>
   *   <li>Iceberg tables → {@link IcebergCalciteTableAdapter}</li>
   *   <li>Hive views → {@link HiveCalciteViewAdapter}</li>
   *   <li>Hive tables → {@link HiveCalciteTableAdapter}</li>
   * </ul>
   *
   * @param name Table name
   * @return Calcite Table implementation, or null if table doesn't exist
   */
  @Override
  public Table getTable(String name) {
    CoralTable coralTable = coralCatalog.getTable(dbName, name);
    if (coralTable == null) {
      return null;
    }

    // Dispatch based on CoralTable implementation type
    if (coralTable instanceof IcebergTable) {
      return new IcebergCalciteTableAdapter((IcebergTable) coralTable);
    } else if (coralTable instanceof HiveTable) {
      HiveTable hiveTable = (HiveTable) coralTable;
      // Check if it's a view
      if (hiveTable.tableType() == VIEW) {
        return new HiveCalciteViewAdapter(hiveTable, ImmutableList.of(CoralRootSchema.ROOT_SCHEMA, dbName));
      } else {
        return new HiveCalciteTableAdapter(hiveTable);
      }
    }

    return null;
  }

  /**
   * Returns all table names in this database.
   *
   * @return Set of table names
   */
  @Override
  public Set<String> getTableNames() {
    return ImmutableSet.copyOf(coralCatalog.getAllTables(dbName));
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
   * A database does not have subschemas.
   *
   * @param name Subschema name
   * @return null (databases don't have subschemas)
   */
  @Override
  public Schema getSubSchema(String name) {
    return null;
  }

  @Override
  public Set<String> getSubSchemaNames() {
    return ImmutableSet.of();
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
