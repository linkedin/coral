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

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}. This class represents the "root" schema
 * that holds all hive databases as subschema and no tables.
 * 
 * Can use either CoralCatalog for unified access to different table formats
 * or HiveMetastoreClient for Hive-specific access.
 */
public class HiveSchema implements Schema {

  public static final String ROOT_SCHEMA = "hive";
  public static final String DEFAULT_DB = "default";

  private final CoralCatalog coralCatalog;
  private final HiveMetastoreClient msc;

  /**
   * Create HiveSchema using CoralCatalog to read catalog information.
   * 
   * @param coralCatalog Coral catalog providing unified access to tables
   */
  public HiveSchema(@Nonnull CoralCatalog coralCatalog) {
    this.coralCatalog = checkNotNull(coralCatalog);
    this.msc = null;
  }

  /**
   * Create HiveSchema using HiveMetastoreClient (backward compatibility).
   * 
   * @param msc Hive metastore client
   */
  public HiveSchema(@Nonnull HiveMetastoreClient msc) {
    this.msc = checkNotNull(msc);
    this.coralCatalog = null;
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
    // Check if database exists
    if (coralCatalog != null) {
      if (!coralCatalog.namespaceExists(name)) {
        return null;
      }
      return new HiveDbSchema(coralCatalog, null, name);
    } else {
      if (msc.getDatabase(name) == null) {
        return null;
      }
      return new HiveDbSchema(null, msc, name);
    }
  }

  @Override
  public Set<String> getSubSchemaNames() {
    if (coralCatalog != null) {
      return ImmutableSet.copyOf(coralCatalog.getAllNamespaces());
    } else {
      return ImmutableSet.copyOf(msc.getAllDatabases());
    }
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
