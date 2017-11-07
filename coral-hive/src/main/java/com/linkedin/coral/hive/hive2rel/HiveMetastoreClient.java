package com.linkedin.coral.hive.hive2rel;

import java.util.List;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;


public interface HiveMetastoreClient {

  List<String> getAllDatabases();

  Database getDatabase(String dbName);

  List<String> getAllTables(String dbName);

  Table getTable(String dbName, String tableName);
}
