// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.iceberg.hive.TestHiveMetastore;
import org.apache.spark.sql.SparkSession;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * Base class for integration tests that require Spark3 with Iceberg and HiveMetastore.
 * This class uses:
 * - Iceberg's TestHiveMetastore for iceberg_catalog (optimized for Iceberg)
 * - Derby-based HMS from HiveMetastoreTestBase for spark_catalog (regular Hive tables)
 */
public class SparkIcebergTestBase extends HiveMetastoreTestBase {

  protected SparkSession spark;
  protected static TestHiveMetastore icebergTestHms;
  protected HiveConf icebergHiveConf;

  /**
   * Don't create a separate HiveMetaStoreClient - Spark will manage its own
   */
  @Override
  protected boolean shouldCreateMetastoreClient() {
    return false;
  }
  
  /**
   * Create default database in Iceberg's TestHiveMetastore
   */
  private void createIcebergDefaultDatabase() throws Exception {
    org.apache.hadoop.hive.metastore.HiveMetaStoreClient client = 
        new org.apache.hadoop.hive.metastore.HiveMetaStoreClient(icebergHiveConf);
    try {
      // Check if default database already exists
      try {
        client.getDatabase("default");
        System.out.println("Default database already exists in Iceberg HMS");
      } catch (Exception e) {
        // Default database doesn't exist, create it
        String warehousePath = icebergHiveConf.getVar(HiveConf.ConfVars.METASTOREWAREHOUSE);
        org.apache.hadoop.hive.metastore.api.Database defaultDb = 
            new org.apache.hadoop.hive.metastore.api.Database("default", "Default database for Iceberg", warehousePath, null);
        client.createDatabase(defaultDb);
        System.out.println("Created default database in Iceberg HMS at: " + warehousePath);
      }
    } finally {
      client.close();
    }
  }

  @BeforeClass
  public void setupSpark() throws Exception {
    // Setup Derby-based HMS for Hive catalog (from parent class)
    super.setupHiveMetastore();

    // Setup Iceberg's TestHiveMetastore for Iceberg catalog
    icebergTestHms = new TestHiveMetastore();
    icebergTestHms.start();
    icebergHiveConf = icebergTestHms.hiveConf();
    
    // Create default database in Iceberg's HMS if it doesn't exist
    createIcebergDefaultDatabase();

    // Create SparkSession with TWO separate HMS instances
    // - Iceberg catalog uses TestHiveMetastore (Iceberg's test utility)
    // - Hive catalog uses Derby-based HMS (from HiveMetastoreTestBase)
    spark = SparkSession.builder()
        .appName("CoralIntegrationTest")
        .master("local[2]")
        .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
        
        // Iceberg catalog using TestHiveMetastore
        .config("spark.sql.catalog.iceberg_catalog", "org.apache.iceberg.spark.SparkCatalog")
        .config("spark.sql.catalog.iceberg_catalog.type", "hive")
        .config("spark.sql.catalog.iceberg_catalog.uri", icebergHiveConf.getVar(HiveConf.ConfVars.METASTOREURIS))
        .config("spark.sql.catalog.iceberg_catalog.warehouse", icebergHiveConf.getVar(HiveConf.ConfVars.METASTOREWAREHOUSE))
        
        // Default Hive catalog using Derby-based HMS
        .config("spark.sql.catalogImplementation", "hive")
        .config("hive.metastore.uris", getHiveMetastoreUri())
        .config("spark.sql.warehouse.dir", getWarehouseDir())
        .config("hive.metastore.warehouse.dir", getWarehouseDir())
        
        // Spark configuration
        .config("spark.sql.shuffle.partitions", "4")
        .config("spark.ui.enabled", "false")
        .enableHiveSupport()
        .getOrCreate();

    // Set log level to WARN to reduce noise
    spark.sparkContext().setLogLevel("WARN");
  }

  @AfterClass
  public void tearDownSpark() throws Exception {
    // Stop Spark session first
    if (spark != null) {
      spark.stop();
      spark = null;
    }

    // Give Spark time to release resources
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Stop Iceberg's TestHiveMetastore
    if (icebergTestHms != null) {
      icebergTestHms.stop();
      icebergTestHms = null;
    }

    // Cleanup Derby-based HMS (from parent class)
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
   * For DDL statements, we need to collect() to force execution.
   *
   * @param sql SQL query to execute
   */
  protected void executeSql(String sql) {
    spark.sql(sql).collect();
  }
}
