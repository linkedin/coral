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

  @BeforeClass
  public void setupSpark() throws IOException, TException {
    // Setup Hive Metastore first
    super.setupHiveMetastore();

    // Create SparkSession configured with Iceberg and Hive Metastore
    // Use default Hive catalog for Hive tables, and add Iceberg catalog for Iceberg tables
    spark = SparkSession.builder().appName("CoralIntegrationTest").master("local[2]")
        .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
        .config("spark.sql.catalog.iceberg_catalog", "org.apache.iceberg.spark.SparkCatalog")
        .config("spark.sql.catalog.iceberg_catalog.type", "hive")
        .config("spark.sql.catalog.iceberg_catalog.warehouse", getWarehouseDir())
        .config("spark.sql.warehouse.dir", getWarehouseDir())
        .config("hive.metastore.warehouse.dir", getWarehouseDir())
        .config("hive.metastore.uris", "thrift://localhost:9083")  // Connect to standalone metastore
        .config("javax.jdo.option.ConnectionURL", hiveConf.get("javax.jdo.option.ConnectionURL"))
        .config("javax.jdo.option.ConnectionDriverName", hiveConf.get("javax.jdo.option.ConnectionDriverName"))
        // Use Hive catalog implementation for default catalog
        .config("spark.sql.catalogImplementation", "hive")
        .config("spark.sql.shuffle.partitions", "4")
        .config("spark.ui.enabled", "false")  // Disable UI for tests
        .enableHiveSupport().getOrCreate();

    // Set log level to WARN to reduce noise
    spark.sparkContext().setLogLevel("WARN");
  }

  @AfterClass
  public void tearDownSpark() throws IOException {
    // Stop Spark session
    if (spark != null) {
      spark.stop();
    }

    // Cleanup Hive Metastore
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

