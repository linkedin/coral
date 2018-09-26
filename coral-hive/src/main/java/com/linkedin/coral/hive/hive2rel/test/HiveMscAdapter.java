package com.linkedin.coral.hive.hive2rel.test;

import com.google.common.collect.ImmutableList;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import java.util.List;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;


public class HiveMscAdapter implements HiveMetastoreClient {

  private final IMetaStoreClient delegate;

  public HiveMscAdapter(IMetaStoreClient msc) {
    this.delegate = msc;
  }

  @Override
  public List<String> getAllDatabases() {
    try {
      return delegate.getAllDatabases();
    } catch (TException e) {
      return ImmutableList.of();
    }
  }

  @Override
  public Database getDatabase(String dbName) {
    try {
      return delegate.getDatabase(dbName);
    } catch (TException e) {
      return null;
    }
  }

  @Override
  public List<String> getAllTables(String dbName) {
    try {
      return delegate.getAllTables(dbName);
    } catch (TException e) {
      return ImmutableList.of();
    }
  }

  @Override
  public Table getTable(String dbName, String tableName) {
    try {
      return delegate.getTable(dbName, tableName);
    } catch (TException e) {
      return null;
    }
  }
}
