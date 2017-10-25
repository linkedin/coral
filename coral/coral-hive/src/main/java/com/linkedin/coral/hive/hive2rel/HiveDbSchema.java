package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import static com.google.common.base.Preconditions.*;


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
    return table == null ? null : new HiveTable(table);
  }

  @Override
  public Set<String> getTableNames() {
    return ImmutableSet.copyOf(msc.getAllTables(dbName));
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
