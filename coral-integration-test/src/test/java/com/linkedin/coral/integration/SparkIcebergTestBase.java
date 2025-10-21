// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import java.io.IOException;

import org.apache.spark.sql.SparkSession;
import org.apache.thrift.TException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * Base class for integration tests that require Spark3 with Iceberg and HiveMetastore 2.0.
 * This class extends HiveMetastoreTestBase and adds Spark3 session setup.
 */
public class SparkIcebergTestBase extends HiveMetastoreTestBase {

  protected SparkSession spark;

  /**
   * Don't create a separate HiveMetaStoreClient - Spark will manage its own
   */
  @Override
  protected boolean shouldCreateMetastoreClient() {
    return false;
  }

  @BeforeClass
  public void setupSpark() throws Exception {
    // Setup Hive Metastore configurations (creates two separate HiveConf instances with different URIs)
    super.setupHiveMetastore();

    // Create SparkSession with TWO separate embedded metastores
    // Each catalog uses its own URI and HiveConf with separate Derby databases
    spark = SparkSession.builder().appName("CoralIntegrationTest").master("local[2]")
        .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")

        // Iceberg catalog with its OWN URI and embedded metastore
        .config("spark.sql.catalog.iceberg_catalog", "org.apache.iceberg.spark.SparkCatalog")
        .config("spark.sql.catalog.iceberg_catalog.type", "hive")
        .config("spark.sql.catalog.iceberg_catalog.uri", getIcebergMetastoreUri())  // Iceberg-specific URI
        .config("spark.sql.catalog.iceberg_catalog.warehouse", getIcebergWarehouseDir())
        // Use Iceberg-specific HiveConf settings (separate Derby database)
        .config("spark.sql.catalog.iceberg_catalog.javax.jdo.option.ConnectionURL",
            icebergHiveConf.get("javax.jdo.option.ConnectionURL"))
        .config("spark.sql.catalog.iceberg_catalog.javax.jdo.option.ConnectionDriverName",
            icebergHiveConf.get("javax.jdo.option.ConnectionDriverName"))
        .config("spark.sql.catalog.iceberg_catalog.datanucleus.schema.autoCreateAll", "true")
        .config("spark.sql.catalog.iceberg_catalog.hive.metastore.schema.verification", "false")

        // Default Hive catalog with its OWN URI and embedded metastore
        .config("spark.sql.warehouse.dir", getWarehouseDir())
        .config("hive.metastore.warehouse.dir", getWarehouseDir())
        .config("hive.metastore.uris", getHiveMetastoreUri())  // Hive-specific URI
        // Use Hive-specific HiveConf settings (separate Derby database)
        .config("javax.jdo.option.ConnectionURL", hiveConf.get("javax.jdo.option.ConnectionURL"))
        .config("javax.jdo.option.ConnectionDriverName", hiveConf.get("javax.jdo.option.ConnectionDriverName"))
        .config("datanucleus.schema.autoCreateAll", "true")
        .config("hive.metastore.schema.verification", "false")

        // Spark configuration
        .config("spark.sql.catalogImplementation", "hive")
        .config("spark.sql.shuffle.partitions", "4")
        .config("spark.ui.enabled", "false")  // Disable UI for tests
        .enableHiveSupport().getOrCreate();

    // Set log level to WARN to reduce noise
    spark.sparkContext().setLogLevel("WARN");
  }

  @AfterClass
  public void tearDownSpark() throws IOException {
    // Stop Spark session first
    if (spark != null) {
      spark.stop();
      spark = null;
    }

    // Give Spark time to fully release Derby
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Cleanup Hive Metastore (includes Derby shutdown)
    super.tearDownHiveMetastore();
  }

  /**
   * Get the SparkSession for this test.
   *
   * @return SparkSession instance
   */
  protected SparkSession getSparkSession() {
    return spark;
  }

  /**
   * Execute a SQL query and return the count of results.
   *
   * @param sql SQL query to execute
   * @return Count of results
   */
  protected long executeSqlAndGetCount(String sql) {
    return spark.sql(sql).count();
  }

  /**
   * Execute a SQL query.
   *
   * @param sql SQL query to execute
   */
  protected void executeSql(String sql) {
    spark.sql(sql);
  }
}

