/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;


/**
 * Interface for accessing Hive Metastore.
 * Implementations of this interface handle connections to Hive metastore
 * and provide access to database and table metadata.
 *
 * @deprecated Use {@link com.linkedin.coral.common.catalog.CoralCatalog} instead.
 *             CoralCatalog provides a unified interface supporting multiple table formats
 *             (Hive, Iceberg, etc.) while this interface is Hive-specific.
 *             Existing code using HiveMetastoreClient continues to work.
 */
@Deprecated
public interface HiveMetastoreClient {

  List<String> getAllDatabases();

  Database getDatabase(String dbName);

  List<String> getAllTables(String dbName);

  Table getTable(String dbName, String tableName);
}
