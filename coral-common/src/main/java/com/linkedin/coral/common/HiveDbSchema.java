/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
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
import com.linkedin.coral.common.catalog.HiveCoralTable;
import com.linkedin.coral.common.catalog.IcebergCoralTable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.linkedin.coral.common.catalog.TableType.VIEW;


/**
 * Adaptor from catalog providing database and table names to Calcite {@link Schema}.
 * Can use either CoralCatalog for unified access or HiveMetastoreClient for Hive-specific access.
 */
public class HiveDbSchema implements Schema {

  public static final String DEFAULT_DB = "default";

  private final CoralCatalog coralCatalog;
  private final HiveMetastoreClient msc;
  private final String dbName;

  /**
   * Constructor for HiveDbSchema. Exactly one of coralCatalog or msc must be non-null.
   * 
   * @param coralCatalog Coral catalog for unified access (can be null if msc is provided)
   * @param msc Hive metastore client for Hive-specific access (can be null if coralCatalog is provided)
   * @param dbName Database name (must not be null)
   */
  HiveDbSchema(CoralCatalog coralCatalog, HiveMetastoreClient msc, @Nonnull String dbName) {
    this.coralCatalog = coralCatalog;
    this.msc = msc;
    this.dbName = checkNotNull(dbName);
  }

  @Override
  public Table getTable(String name) {
    if (coralCatalog != null) {
      // Use CoralCatalog for unified table access
      CoralTable coralTable = coralCatalog.getTable(dbName, name);
      if (coralTable == null) {
        return null;
      }

      // Dispatch based on CoralTable implementation type
      if (coralTable instanceof IcebergCoralTable) {
        return new IcebergTable((IcebergCoralTable) coralTable);
      } else if (coralTable instanceof HiveCoralTable) {
        HiveCoralTable hiveCoralTable = (HiveCoralTable) coralTable;
        // Check if it's a view
        if (hiveCoralTable.tableType() == VIEW) {
          return new HiveViewTable(hiveCoralTable, ImmutableList.of(HiveSchema.ROOT_SCHEMA, dbName));
        } else {
          return new HiveTable(hiveCoralTable);
        }
      }
      return null;
    } else {
      // Use HiveMetastoreClient for Hive-specific access
      org.apache.hadoop.hive.metastore.api.Table hiveTable = msc.getTable(dbName, name);
      if (hiveTable == null) {
        return null;
      }

      // Wrap in HiveCoralTable and dispatch
      HiveCoralTable hiveCoralTable = new HiveCoralTable(hiveTable);
      if (hiveCoralTable.tableType() == VIEW) {
        return new HiveViewTable(hiveCoralTable, ImmutableList.of(HiveSchema.ROOT_SCHEMA, dbName));
      } else {
        return new HiveTable(hiveCoralTable);
      }
    }
  }

  @Override
  public Set<String> getTableNames() {
    if (coralCatalog != null) {
      return ImmutableSet.copyOf(coralCatalog.getAllTables(dbName));
    } else {
      return ImmutableSet.copyOf(msc.getAllTables(dbName));
    }
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
   * A Hive DB does not have subschema
   * @param name Subschema name
   * @return Calcite schema
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
