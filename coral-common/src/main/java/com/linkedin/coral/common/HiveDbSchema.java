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
import com.linkedin.coral.common.catalog.Dataset;
import com.linkedin.coral.common.catalog.HiveDataset;
import com.linkedin.coral.common.catalog.IcebergDataset;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from catalog providing database and table names to Calcite {@link Schema}.
 * Uses CoralCatalog to provide unified access to different table formats
 * (Hive, Iceberg, etc.).
 */
public class HiveDbSchema implements Schema {

  public static final String DEFAULT_DB = "default";

  private final CoralCatalog catalog;
  private final String dbName;

  HiveDbSchema(@Nonnull CoralCatalog catalog, @Nonnull String dbName) {
    this.catalog = checkNotNull(catalog);
    this.dbName = checkNotNull(dbName);
  }

  /**
   * Constructor for backward compatibility with HiveMetastoreClient.
   */
  HiveDbSchema(@Nonnull HiveMetastoreClient msc, @Nonnull String dbName) {
    this((CoralCatalog) checkNotNull(msc), checkNotNull(dbName));
  }

  @Override
  public Table getTable(String name) {
    // Get unified Dataset from CoralCatalog
    Dataset dataset = catalog.getDataset(dbName, name);
    if (dataset == null) {
      return null;
    }

    // Dispatch based on Dataset implementation type
    if (dataset instanceof IcebergDataset) {
      return new IcebergTable((IcebergDataset) dataset);
    } else if (dataset instanceof HiveDataset) {
      HiveDataset hiveDataset = (HiveDataset) dataset;
      // Check if it's a view
      if (hiveDataset.tableType() == com.linkedin.coral.common.catalog.TableType.VIEW) {
        return new HiveViewTable(hiveDataset, ImmutableList.of(HiveSchema.ROOT_SCHEMA, dbName));
      } else {
        return new HiveTable(hiveDataset);
      }
    }

    // Unknown dataset type - return null
    return null;
  }

  @Override
  public Set<String> getTableNames() {
    return ImmutableSet.copyOf(catalog.getAllDatasets(dbName));
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
