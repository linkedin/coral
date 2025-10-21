// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.TException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * Base class for integration tests that require a standalone HiveMetastore 2.0.
 * This class sets up an embedded Derby database and HiveMetaStoreClient.
 */
public class HiveMetastoreTestBase {

  protected HiveMetaStoreClient metastoreClient;
  protected HiveConf hiveConf;
  protected Path warehouseDir;
  protected Path metastoreDbDir;

  @BeforeClass
  public void setupHiveMetastore() throws IOException, TException {
    // Create temporary directories for warehouse and metastore database
    warehouseDir = Files.createTempDirectory("hive-warehouse");
    metastoreDbDir = Files.createTempDirectory("hive-metastore-db");

    // Configure HiveConf for standalone metastore
    hiveConf = new HiveConf();
    hiveConf.set("hive.metastore.uris", "");  // Empty to use embedded metastore
    hiveConf.set("javax.jdo.option.ConnectionURL",
        "jdbc:derby:;databaseName=" + metastoreDbDir.toString() + "/metastore_db;create=true");
    hiveConf.set("javax.jdo.option.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver");
    hiveConf.set("hive.metastore.warehouse.dir", warehouseDir.toString());
    hiveConf.set("hive.metastore.schema.verification", "false");
    hiveConf.set("datanucleus.schema.autoCreateAll", "true");
    hiveConf.set("hive.metastore.schema.verification.record.version", "false");

    // Create metastore client
    try {
      metastoreClient = new HiveMetaStoreClient(hiveConf);
    } catch (MetaException e) {
      throw new RuntimeException("Failed to create HiveMetaStoreClient", e);
    }

    // Create default database if it doesn't exist
    try {
      metastoreClient.getDatabase("default");
    } catch (Exception e) {
      Database db = new Database();
      db.setName("default");
      db.setDescription("Default database");
      db.setLocationUri(warehouseDir.toString() + "/default.db");
      metastoreClient.createDatabase(db);
    }
  }

  @AfterClass
  public void tearDownHiveMetastore() throws IOException {
    // Close metastore client
    if (metastoreClient != null) {
      metastoreClient.close();
    }

    // Clean up temporary directories
    if (warehouseDir != null && Files.exists(warehouseDir)) {
      Files.walk(warehouseDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    if (metastoreDbDir != null && Files.exists(metastoreDbDir)) {
      Files.walk(metastoreDbDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
  }

  /**
   * Get the HiveConf configured for this test.
   *
   * @return HiveConf instance
   */
  protected HiveConf getHiveConf() {
    return hiveConf;
  }

  /**
   * Get the HiveMetaStoreClient for this test.
   *
   * @return HiveMetaStoreClient instance
   */
  protected HiveMetaStoreClient getMetastoreClient() {
    return metastoreClient;
  }

  /**
   * Get the warehouse directory path.
   *
   * @return Path to warehouse directory
   */
  protected String getWarehouseDir() {
    return warehouseDir.toString();
  }
}

