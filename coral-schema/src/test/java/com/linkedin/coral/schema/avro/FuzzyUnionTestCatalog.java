/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.HiveTable;


/**
 * Small in-memory metastore for fuzzy-UNION schema tests.
 *
 * <p>A real metastore cannot express the post-evolution state these tests need: a stored view whose base tables have
 * since gained fields. This fixture registers tables and existing {@code VIRTUAL_VIEW}s directly, so
 * {@code HiveToRelConverter.convertView} sees exactly the stored SQL and table metadata supplied by each test.
 *
 * <p>Every lookup returns a defensive copy, so a converter cannot mutate the registered fixture.
 */
class FuzzyUnionTestCatalog implements HiveMetastoreClient {
  static final String AVRO_SERDE = "org.apache.hadoop.hive.serde2.avro.AvroSerDe";
  static final String LAZY_SIMPLE_SERDE = "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe";
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  private final Map<String, Map<String, Table>> databases = new LinkedHashMap<>();

  /**
   * Registers a table whose Hive columns are lowercase Hive types and whose case-preserved Avro schema is only
   * available as the {@code avro.schema.literal} table property (the ORC/OpenHouse-style incident path).
   */
  FuzzyUnionTestCatalog addHiveTableWithAvroLiteral(String db, String name, String avroLiteral, String... cols) {
    Table table = baseTable(db, name, "EXTERNAL_TABLE", LAZY_SIMPLE_SERDE, columns(cols));
    table.getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    return put(table);
  }

  /**
   * Registers an AvroSerDe table. Hive reads its columns from the Avro schema, which is also the table's
   * {@code avro.schema.literal}; {@code cols} are the equivalent lowercase Hive columns.
   */
  FuzzyUnionTestCatalog addAvroSerdeTable(String db, String name, String avroLiteral, String... cols) {
    Table table = baseTable(db, name, "EXTERNAL_TABLE", AVRO_SERDE, columns(cols));
    table.getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    table.getSd().getSerdeInfo().getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    return put(table);
  }

  /**
   * Registers an already existing view. {@code cols} are the view's declared columns at creation time, which may be
   * stale relative to its evolved base tables.
   */
  FuzzyUnionTestCatalog addView(String db, String name, String expandedSql, Map<String, String> properties,
      String... cols) {
    Table view = baseTable(db, name, "VIRTUAL_VIEW", null, columns(cols));
    view.setViewOriginalText(expandedSql);
    view.setViewExpandedText(expandedSql);
    view.getParameters().putAll(properties);
    return put(view);
  }

  FuzzyUnionTestCatalog addView(String db, String name, String expandedSql, String... cols) {
    return addView(db, name, expandedSql, Collections.emptyMap(), cols);
  }

  /** Returns the registered Avro literal of a table, read straight from the fixture. */
  String avroLiteral(String db, String name) {
    return requireTable(db, name).getParameters().get(AVRO_SCHEMA_LITERAL);
  }

  /** A {@link CoralCatalog} over the same fixture that never goes through {@link HiveMetastoreClient}. */
  CoralCatalog asCoralCatalog() {
    return new CoralCatalog() {
      @Override
      public CoralTable getTable(String namespace, String tableName) {
        Table table = lookup(namespace, tableName);
        return table == null ? null : new HiveTable(table);
      }

      @Override
      public boolean namespaceExists(String namespace) {
        return databases.containsKey(namespace);
      }

      @Override
      public List<String> getAllTables(String namespace) {
        return tableNames(namespace);
      }

      @Override
      public List<String> getAllNamespaces() {
        return new ArrayList<>(databases.keySet());
      }
    };
  }

  @Override
  public List<String> getAllDatabases() {
    return new ArrayList<>(databases.keySet());
  }

  @Override
  public Database getDatabase(String dbName) {
    if (!databases.containsKey(dbName)) {
      return null;
    }
    return new Database(dbName, null, null, new HashMap<>());
  }

  @Override
  public List<String> getAllTables(String dbName) {
    return tableNames(dbName);
  }

  @Override
  public Table getTable(String dbName, String tableName) {
    return lookup(dbName, tableName);
  }

  private List<String> tableNames(String dbName) {
    Map<String, Table> tables = databases.get(dbName);
    return tables == null ? Collections.emptyList() : new ArrayList<>(tables.keySet());
  }

  private Table lookup(String dbName, String tableName) {
    Map<String, Table> tables = databases.get(dbName);
    if (tables == null) {
      return null;
    }
    Table table = tables.get(tableName.toLowerCase());
    return table == null ? null : new Table(table);
  }

  private Table requireTable(String dbName, String tableName) {
    Table table = lookup(dbName, tableName);
    if (table == null) {
      throw new IllegalStateException("Missing test fixture " + dbName + "." + tableName);
    }
    return table;
  }

  private FuzzyUnionTestCatalog put(Table table) {
    Map<String, Table> tables = databases.computeIfAbsent(table.getDbName(), k -> new LinkedHashMap<>());
    if (tables.put(table.getTableName(), table) != null) {
      throw new IllegalStateException("Duplicate test fixture " + table.getDbName() + "." + table.getTableName());
    }
    return this;
  }

  private static Table baseTable(String db, String name, String tableType, String serde, List<FieldSchema> cols) {
    SerDeInfo serDeInfo = new SerDeInfo();
    serDeInfo.setSerializationLib(serde);
    serDeInfo.setParameters(new HashMap<>());

    StorageDescriptor sd = new StorageDescriptor();
    sd.setCols(cols);
    sd.setSerdeInfo(serDeInfo);

    Table table = new Table();
    table.setDbName(db);
    table.setTableName(name.toLowerCase());
    table.setTableType(tableType);
    table.setSd(sd);
    table.setPartitionKeys(new ArrayList<>());
    table.setParameters(new HashMap<>());
    return table;
  }

  /** Parses {@code "name type"} column declarations, e.g. {@code "requestheader struct<pagekey:string>"}. */
  private static List<FieldSchema> columns(String... cols) {
    List<FieldSchema> result = new ArrayList<>();
    for (String col : cols) {
      int space = col.indexOf(' ');
      result.add(new FieldSchema(col.substring(0, space), col.substring(space + 1).trim(), null));
    }
    return result;
  }
}
