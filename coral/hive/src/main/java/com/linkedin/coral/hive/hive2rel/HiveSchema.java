package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;


/**
 * Adaptor from Hive catalog providing database and table names
 * to Calcite {@link Schema}. This class represents the "root" schema
 * that holds all hive databases as subschema and no tables.
 */
public class HiveSchema implements Schema {

  private final Hive hive;
  public static final String ROOT_SCHEMA = "hive";
  public static final String DEFAULT_DB = "default";

  /**
   * Create HiveSchema from hive-site configuration
   * @param conf Hive configuration to connect to metastore
   * @return HiveSchema
   * @throws HiveException
   */
  public static HiveSchema create(HiveConf conf) throws HiveException {
    Hive hive = Hive.get(conf);
    return create(hive);
  }

  /**
   * HiveSchema from input {@link Hive} object providing connection to metastore
   * @param hive
   * @return
   */
  public static HiveSchema create(Hive hive) {
    return new HiveSchema(hive);
  }

  private HiveSchema(Hive hive) {
    this.hive = hive;
  }

  /**
   * This always returns null as root hive schema does not have tables.
   * @param name
   * @return
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
    try {
      Database database = hive.getDatabase(name);
      return (database == null) ? null : new HiveDbSchema(hive, database.getName());
    } catch (HiveException e) {
      // TODO: throw or return null ??
      throw new RuntimeException("Database " + name + " does not exist", e);
    }
  }

  @Override
  public Set<String> getSubSchemaNames() {
    try {
      List<String> dbNames = hive.getAllDatabases();
      return ImmutableSet.copyOf(dbNames);
    } catch (HiveException e) {
      // TODO: throw or log and return empty set ??
      throw new RuntimeException("Failed to load database names from Hive catalog", e);
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
