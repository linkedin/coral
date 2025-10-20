/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.catalog;

import java.util.List;


/**
 * Top-level catalog interface for accessing datasets in Coral.
 * This interface provides a unified API for querying table metadata
 * across different table formats (Hive, Iceberg, etc.).
 *
 * CoralCatalog abstracts away the differences between various table formats
 * and provides a consistent way to access dataset information through
 * the {@link Dataset} interface.
 *
 * Implementations of this interface handle the details of connecting to
 * metadata stores and converting format-specific table representations
 * into the unified Dataset abstraction.
 */
public interface CoralCatalog {

  /**
   * Retrieves a dataset by database and table name.
   * This method returns a unified Dataset abstraction that works
   * across different table formats (Hive, Iceberg, etc.).
   *
   * @param dbName Database name
   * @param tableName Table name
   * @return Dataset object representing the table, or null if not found
   */
  Dataset getDataset(String dbName, String tableName);

  /**
   * Retrieves all dataset (table/view) names in a database.
   *
   * @param dbName Database name
   * @return List of dataset names in the database, empty list if database doesn't exist
   */
  List<String> getAllDatasets(String dbName);

  /**
   * Retrieves all database names accessible through this catalog.
   *
   * @return List of database names, empty list if no databases are accessible
   */
  List<String> getAllDatabases();
}

