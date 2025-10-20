/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;

import com.linkedin.coral.common.catalog.CoralCatalog;
import com.linkedin.coral.common.catalog.Dataset;
import com.linkedin.coral.common.catalog.DatasetConverter;


/**
 * Interface for accessing Hive Metastore.
 * This interface extends {@link CoralCatalog} to provide unified Dataset access
 * while maintaining backward compatibility with existing Hive-specific methods.
 *
 * Implementations of this interface handle connections to Hive metastore
 * and provide access to database and table metadata.
 */
public interface HiveMetastoreClient extends CoralCatalog {

  /**
   * Retrieves all database names from the metastore.
   *
   * @return List of database names
   */
  List<String> getAllDatabases();

  /**
   * Retrieves database metadata by name.
   *
   * @param dbName Database name
   * @return Database object, or null if not found
   */
  Database getDatabase(String dbName);

  /**
   * Retrieves all table names in a database.
   *
   * @param dbName Database name
   * @return List of table names
   */
  List<String> getAllTables(String dbName);

  /**
   * Retrieves a table by database and table name.
   *
   * @param dbName Database name
   * @param tableName Table name
   * @return Hive Table object, or null if not found
   */
  Table getTable(String dbName, String tableName);

  /**
   * Retrieves a dataset by database and table name.
   * This method provides unified access to tables through the Dataset abstraction.
   *
   * Default implementation uses {@link #getTable(String, String)} and
   * {@link DatasetConverter#autoConvert(Table)} to provide Dataset access.
   *
   * @param dbName Database name
   * @param tableName Table name
   * @return Dataset object, or null if table not found
   */
  @Override
  default Dataset getDataset(String dbName, String tableName) {
    Table table = getTable(dbName, tableName);
    if (table == null) {
      return null;
    }
    return DatasetConverter.autoConvert(table);
  }

  /**
   * Retrieves all dataset names in a database.
   * Default implementation delegates to {@link #getAllTables(String)}.
   *
   * @param dbName Database name
   * @return List of dataset (table) names
   */
  @Override
  default List<String> getAllDatasets(String dbName) {
    return getAllTables(dbName);
  }

  // Note: getAllDatabases() already satisfies CoralCatalog.getAllDatabases()
}
