/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}
 */
public class HiveDbSchema implements Schema {

  public static final String DEFAULT_DB = "default";

  private final HiveMetastoreClient msc;
  private final String dbName;

  HiveDbSchema(@Nonnull HiveMetastoreClient msc, @Nonnull String dbName) {
    checkNotNull(msc);
    checkNotNull(dbName);
    this.msc = msc;
    this.dbName = dbName;
  }

  @Override
  public Table getTable(String name) {
    org.apache.hadoop.hive.metastore.api.Table table = msc.getTable(dbName, name);
    if (table == null) {
      return null;
    }
    org.apache.hadoop.hive.metastore.TableType tableType =
        Enum.valueOf(org.apache.hadoop.hive.metastore.TableType.class, table.getTableType());
    switch (tableType) {
      case VIRTUAL_VIEW:
        return new HiveViewTable(table, ImmutableList.of(HiveSchema.ROOT_SCHEMA, dbName));
      default:
        return new HiveTable(table);
    }
  }

  @Override
  public Set<String> getTableNames() {
    return ImmutableSet.copyOf(msc.getAllTables(dbName));
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
