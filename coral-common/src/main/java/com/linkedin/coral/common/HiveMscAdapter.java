/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adapter implementation of {@link HiveMetastoreClient} that wraps
 * Hadoop's {@link IMetaStoreClient}.
 *
 * @deprecated Use {@link com.linkedin.coral.common.catalog.CoralCatalog} instead.
 *             This class is Hive-specific. For multi-format support (Hive, Iceberg),
 *             implement CoralCatalog directly. Existing code continues to work.
 */
@Deprecated
public class HiveMscAdapter implements HiveMetastoreClient {

  private final static Logger LOG = LoggerFactory.getLogger(HiveMscAdapter.class);

  private final IMetaStoreClient delegate;

  public HiveMscAdapter(IMetaStoreClient msc) {
    this.delegate = msc;
  }

  @Override
  public List<String> getAllDatabases() {
    try {
      return delegate.getAllDatabases();
    } catch (TException e) {
      LOG.error("Failed to get all databases.", e);
      return ImmutableList.of();
    }
  }

  @Override
  public Database getDatabase(String dbName) {
    try {
      return delegate.getDatabase(dbName);
    } catch (TException e) {
      LOG.error(String.format("Failed to get database %s.", dbName), e);
      return null;
    }
  }

  @Override
  public List<String> getAllTables(String dbName) {
    try {
      return delegate.getAllTables(dbName);
    } catch (TException e) {
      LOG.error(String.format("Failed to get all tables from database %s.", dbName), e);
      return ImmutableList.of();
    }
  }

  @Override
  public Table getTable(String dbName, String tableName) {
    try {
      return delegate.getTable(dbName, tableName);
    } catch (TException e) {
      LOG.error(String.format("Failed to get table %s.%s.", dbName, tableName), e);
      return null;
    }
  }
}
