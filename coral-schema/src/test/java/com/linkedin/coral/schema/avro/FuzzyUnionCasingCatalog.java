/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.ArrayList;
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
 * In-memory metastore holding already existing views over base tables that have since evolved, a state a Hive
 * CREATE VIEW cannot produce. Hive columns are lowercase; the case-preserved Avro schema is only the
 * {@code avro.schema.literal} property. Lookups return copies, so a converter cannot mutate the fixture.
 */
class FuzzyUnionCasingCatalog implements HiveMetastoreClient {
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  private final Map<String, Map<String, Table>> databases = new LinkedHashMap<>();

  /** A non-Avro-SerDe table whose Avro schema, if any, is only the table property (the incident's path). */
  FuzzyUnionCasingCatalog addTable(String db, String name, String avroLiteral, String... cols) {
    Table table = table(db, name, "EXTERNAL_TABLE", "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe", cols);
    if (avroLiteral != null) {
      table.getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    }
    return put(table);
  }

  /** An AvroSerDe table: the literal is also a SerDe parameter. */
  FuzzyUnionCasingCatalog addAvroTable(String db, String name, String avroLiteral, String... cols) {
    Table table = table(db, name, "EXTERNAL_TABLE", "org.apache.hadoop.hive.serde2.avro.AvroSerDe", cols);
    table.getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    table.getSd().getSerdeInfo().getParameters().put(AVRO_SCHEMA_LITERAL, avroLiteral);
    return put(table);
  }

  /** An existing view; {@code cols} are its columns as declared when it was created. */
  FuzzyUnionCasingCatalog addView(String db, String name, String sql, String... cols) {
    Table view = table(db, name, "VIRTUAL_VIEW", null, cols);
    view.setViewOriginalText(sql);
    view.setViewExpandedText(sql);
    return put(view);
  }

  /** The same fixture through the {@link CoralCatalog} entry point. */
  CoralCatalog asCoralCatalog() {
    return new CoralCatalog() {
      @Override
      public CoralTable getTable(String namespace, String tableName) {
        Table table = FuzzyUnionCasingCatalog.this.getTable(namespace, tableName);
        return table == null ? null : new HiveTable(table);
      }

      @Override
      public boolean namespaceExists(String namespace) {
        return databases.containsKey(namespace);
      }

      @Override
      public List<String> getAllTables(String namespace) {
        return FuzzyUnionCasingCatalog.this.getAllTables(namespace);
      }

      @Override
      public List<String> getAllNamespaces() {
        return getAllDatabases();
      }
    };
  }

  @Override
  public List<String> getAllDatabases() {
    return new ArrayList<>(databases.keySet());
  }

  @Override
  public Database getDatabase(String dbName) {
    return databases.containsKey(dbName) ? new Database(dbName, null, null, new HashMap<>()) : null;
  }

  @Override
  public List<String> getAllTables(String dbName) {
    return new ArrayList<>(databases.getOrDefault(dbName, new LinkedHashMap<>()).keySet());
  }

  @Override
  public Table getTable(String dbName, String tableName) {
    Table table = databases.getOrDefault(dbName, new LinkedHashMap<>()).get(tableName.toLowerCase());
    return table == null ? null : new Table(table);
  }

  private FuzzyUnionCasingCatalog put(Table table) {
    if (databases.computeIfAbsent(table.getDbName(), k -> new LinkedHashMap<>()).put(table.getTableName(),
        table) != null) {
      throw new IllegalStateException("Duplicate fixture " + table.getTableName());
    }
    return this;
  }

  /** {@code cols} are {@code "name hiveType"} declarations. */
  private static Table table(String db, String name, String tableType, String serde, String... cols) {
    List<FieldSchema> columns = new ArrayList<>();
    for (String col : cols) {
      int space = col.indexOf(' ');
      columns.add(new FieldSchema(col.substring(0, space), col.substring(space + 1), null));
    }
    SerDeInfo serDeInfo = new SerDeInfo();
    serDeInfo.setSerializationLib(serde);
    serDeInfo.setParameters(new HashMap<>());
    StorageDescriptor sd = new StorageDescriptor();
    sd.setCols(columns);
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
}
