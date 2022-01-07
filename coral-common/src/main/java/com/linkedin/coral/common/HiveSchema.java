/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
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
