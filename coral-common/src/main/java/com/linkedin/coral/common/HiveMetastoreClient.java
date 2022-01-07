/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;


public interface HiveMetastoreClient {

  List<String> getAllDatabases();

  Database getDatabase(String dbName);

  List<String> getAllTables(String dbName);

  Table getTable(String dbName, String tableName);
}
