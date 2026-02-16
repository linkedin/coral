/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}. This class represents the "root" schema
 * that holds all hive databases as subschema and no tables.
 *
 * <p><b>LEGACY API:</b> This class exists for backward compatibility with existing code
 * that uses {@link HiveMetastoreClient} directly. For new code, prefer {@link CoralRootSchema}
 * which integrates with the unified {@link com.linkedin.coral.common.catalog.CoralCatalog} API
 * and supports multiple table formats (Hive, Iceberg, etc.) without Hive-specific coupling.
 *
 * <p><b>Migration Path:</b>
 * <ul>
 *   <li><b>Legacy:</b> {@link HiveSchema} (this class) → {@link HiveDbSchema} → {@link HiveCalciteTableAdapter}</li>
 *   <li><b>Modern:</b> {@link CoralRootSchema} → {@link CoralDatabaseSchema} → Format-specific adapters</li>
 * </ul>
 *
 * <p><b>Future:</b> As part of <a href="https://github.com/linkedin/coral/issues/575">issue #575</a>,
 * this class will be evaluated for deprecation/cleanup once all clients migrate to {@link CoralRootSchema}
 * and the {@link com.linkedin.coral.common.catalog.CoralCatalog} API.
 *
 * @see CoralRootSchema Modern replacement with CoralCatalog integration
 * @see HiveDbSchema Legacy database-level schema
 * @see <a href="https://github.com/linkedin/coral/issues/575">Issue #575: Refactor ParseTreeBuilder to Use CoralTable</a>
 */
public class HiveSchema implements Schema {

  public static final String ROOT_SCHEMA = "hive";
  public static final String DEFAULT_DB = "default";

  private final HiveMetastoreClient msc;

  /**
   * Create HiveSchema using input metastore client to read hive catalog
   * @param msc Hive metastore client
   */
  public HiveSchema(@Nonnull HiveMetastoreClient msc) {
    this.msc = checkNotNull(msc);
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
    Database database = msc.getDatabase(name);
    return (database == null) ? null : new HiveDbSchema(msc, database.getName());
  }

  @Override
  public Set<String> getSubSchemaNames() {
    List<String> dbNames = msc.getAllDatabases();
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
