package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.InvalidTableException;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}
 */
public class HiveDbSchema implements Schema {

  public static final String DEFAULT_DB = "default";

  private final Hive hive;
  private final String dbName;

  public static HiveDbSchema create(Hive hive, String dbName) {
    return new HiveDbSchema(hive, dbName);
  }

  HiveDbSchema(Hive hive, String dbName) {
    this.hive = hive;
    this.dbName = dbName;
  }

  @Override
  public Table getTable(String name) {
    try {
      org.apache.hadoop.hive.ql.metadata.Table hiveTable = hive.getTable(dbName, name);
      return new HiveTable(hiveTable);
    } catch (InvalidTableException e) {
      return null;
    } catch (HiveException e) {
      throw new RuntimeException("Hive table " + name + " does not exist", e);
    }
  }

  @Override
  public Set<String> getTableNames() {
    try {
      return ImmutableSet.copyOf(hive.getAllTables(dbName));
    } catch (HiveException e) {
      throw new RuntimeException("Failed to read table names from Hive metastore", e);
    }
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
   * @param name
   * @return
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

  /**
   * Always return true for now since we are building a library.
   * Roundtrip to metastore is not that costly
   * @param lastCheck
   * @param now
   * @return
   */
  @Override
  public boolean contentsHaveChangedSince(long lastCheck, long now) {
    return true;
  }

  // TODO: This needs to be snapshot of current state of catalog
  @Override
  public Schema snapshot(long now) {
    return this;
  }
}
