package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.hadoop.hive.metastore.api.Database;

import static com.google.common.base.Preconditions.*;


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
    checkNotNull(msc);
    this.msc = msc;
  }

  /**
   * This always returns null as root hive schema does not have tables.
   * @param name
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

  /**
   * Always return true for now since we are building a library.
   * Roundtrip to metastore is not that costly
   * @param lastCheck
   * @param now
   * @return always returns true indicating that catalog can change any time
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
