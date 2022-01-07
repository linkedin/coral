/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.coral.common.HiveMetastoreClient;

import static com.google.common.base.Preconditions.*;


public class HiveMetastoreClientProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(HiveMetastoreClient.class);

  public static HiveMetastoreClientProvider create(@Nonnull String hiveConfPath) {
    checkNotNull(hiveConfPath);
    HiveConf conf = new HiveConf();
    conf.addResource(new Path(hiveConfPath));
    return new HiveMetastoreClientProvider(conf);
  }

  public HiveMetastoreClientProvider(@Nonnull HiveConf hiveConf) {
    checkNotNull(hiveConf);

    SessionState.start(hiveConf);
    try {
      Hive.get(hiveConf);
    } catch (HiveException e) {
      throw new RuntimeException("Failed to initialize hive", e);
    }
  }

  public HiveMetastoreClient getMetastoreClient() {
    try {
      return new LocalHiveMetastoreClient(Hive.get().getMSC());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class LocalHiveMetastoreClient implements HiveMetastoreClient {
    private final IMetaStoreClient delegate;

    LocalHiveMetastoreClient(@Nonnull IMetaStoreClient msc) {
      checkNotNull(msc);
      this.delegate = msc;
    }

    @Override
    public List<String> getAllDatabases() {
      try {
        return delegate.getAllDatabases();
      } catch (TException e) {
        LOGGER.info("Failed to read databases", e);
        return ImmutableList.of();
      }
    }

    @Override
    public Database getDatabase(String dbName) {
      try {
        return delegate.getDatabase(dbName);
      } catch (NoSuchObjectException e) {
        LOGGER.info("Database {} not found", dbName);
        return null;
      } catch (TException e) {
        LOGGER.error("Failed to read database {}", dbName, e);
        return null;
      }
    }

    @Override
    public List<String> getAllTables(String dbName) {
      try {
        return delegate.getAllTables(dbName);
      } catch (TException e) {
        LOGGER.info("Failed to read table from db: {}", dbName, e);
        return ImmutableList.of();
      }
    }

    @Override
    public Table getTable(String dbName, String tableName) {
      try {
        return delegate.getTable(dbName, tableName);
      } catch (NoSuchObjectException e) {
        LOGGER.info("Db: {}, table: {} not found", dbName, tableName, e);
        return null;
      } catch (TException e) {
        LOGGER.error("Failed to read table information from DB: {}, table: {}", dbName, tableName);
        return null;
      }
    }
  }
}
