/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;
import org.apache.hadoop.hive.metastore.api.Database;

import com.linkedin.coral.common.catalog.CoralCatalog;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}. This class represents the "root" schema
 * that holds all hive databases as subschema and no tables.
 * 
 * Uses CoralCatalog for unified access to different table formats
 * (Hive, Iceberg, etc.).
 */
public class HiveSchema implements Schema {

  public static final String ROOT_SCHEMA = "hive";
  public static final String DEFAULT_DB = "default";

  private final CoralCatalog catalog;

  /**
   * Create HiveSchema using CoralCatalog to read catalog information.
   * 
   * @param catalog Coral catalog providing unified access to tables
   */
  public HiveSchema(@Nonnull CoralCatalog catalog) {
    this.catalog = checkNotNull(catalog);
  }
  
  /**
   * Create HiveSchema using HiveMetastoreClient (backward compatibility).
   * 
   * @param msc Hive metastore client
   */
  public HiveSchema(@Nonnull HiveMetastoreClient msc) {
    this((CoralCatalog) checkNotNull(msc));
  }

  /**
   * This always returns null as root hive schema does not have tables.
   * @param name Table name
   * @return get calcite table representation
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

  @Override
  public Schema getSubSchema(String name) {
    // Check if database exists by checking if it has any datasets
    List<String> datasets = catalog.getAllDatasets(name);
    if (datasets == null || datasets.isEmpty()) {
      // Could be empty database, check via HiveMetastoreClient if available
      if (catalog instanceof HiveMetastoreClient) {
        Database database = ((HiveMetastoreClient) catalog).getDatabase(name);
        if (database == null) {
          return null;
        }
      }
    }
    return new HiveDbSchema(catalog, name);
  }

  @Override
  public Set<String> getSubSchemaNames() {
    List<String> dbNames = catalog.getAllDatabases();
    return ImmutableSet.copyOf(dbNames);
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
