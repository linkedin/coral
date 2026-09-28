/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.*;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Adaptor from a local metastore catalog providing database and table names
 * to Calcite {@link Schema}. This class represents the "root" schema
 * that holds all databases as subschema and no tables.
 *
 * <p>This class is a replacement for {@link HiveSchema} to work with localMetastore in coral-spark-plan module.
 */
public class LocalMetastoreSchema implements Schema {

  private final Map<String, Map<String, List<String>>> localMetastore;

  /**
   * Create LocalMetastoreSchema using input metastore map to read catalog
   * @param localMetastore map like a Hive metastore client
   */
  public LocalMetastoreSchema(Map<String, Map<String, List<String>>> localMetastore) {
    this.localMetastore = checkNotNull(localMetastore);
  }

  /**
   * This always returns null as root schema does not have tables.
   * @param name name of the table
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
    if (localMetastore.containsKey(name)) {
      return new LocalMetastoreDbSchema(localMetastore, name);
    }
    return null;
  }

  @Override
  public Set<String> getSubSchemaNames() {
    return ImmutableSet.copyOf(localMetastore.keySet());
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

  /**
   * @return Hive metastore client, because we don't have MSC in this class, return null for now
   */
  public HiveMetastoreClient getHiveMetastoreClient() {
    return null;
  }
}
