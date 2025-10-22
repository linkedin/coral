// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.iceberg.hive.TestHiveMetastore;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.schema.avro.ViewToAvroSchemaConverter;
import com.linkedin.coral.spark.CoralSpark;


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

  /**
   * Create a HiveMetastoreClient wrapper for Coral that delegates to both HMS clients.
   * This wrapper checks both the Derby-based HMS (rawHmsClient) and Iceberg's TestHiveMetastore.
   *
   * @return HiveMetastoreClient for Coral
   */
  protected HiveMetastoreClient createCoralHiveMetastoreClient() throws Exception {
    HiveMetaStoreClient rawHmsClient = new HiveMetaStoreClient(hiveConf);
    HiveMetaStoreClient icebergHmsClient = new HiveMetaStoreClient(icebergHiveConf);

    return new HiveMetastoreClient() {
      @Override
      public List<String> getAllDatabases() {
          try {
              return rawHmsClient.getAllDatabases();
          } catch (Exception e) {
              throw new RuntimeException("Failed to retrieve all databases", e);
          }
      }

      @Override
      public Database getDatabase(String dbName) {
        try {
          return rawHmsClient.getDatabase(dbName);
        } catch (Exception e) {
          throw new RuntimeException("Failed to get database: " + dbName, e);
        }
      }

      @Override
      public List<String> getAllTables(String dbName) {
        Set<String> allTables = new HashSet<>();

        // Get tables from rawHmsClient (Derby-based HMS for Hive tables)
        try {
          List<String> rawTables = rawHmsClient.getAllTables(dbName);
          if (rawTables != null) {
            allTables.addAll(rawTables);
          }
        } catch (Exception e) {
          // Ignore if database doesn't exist in rawHmsClient
        }

        // Get tables from icebergHmsClient (TestHiveMetastore for Iceberg tables)
        try {
          List<String> icebergTables = icebergHmsClient.getAllTables(dbName);
          if (icebergTables != null) {
            allTables.addAll(icebergTables);
          }
        } catch (Exception e) {
          // Ignore if database doesn't exist in icebergHmsClient
        }

        return new ArrayList<>(allTables);
      }

      @Override
      public org.apache.hadoop.hive.metastore.api.Table getTable(String dbName, String tableName) {
        try {
          // Try rawHmsClient first (Derby-based HMS for Hive tables)
          return rawHmsClient.getTable(dbName, tableName);
        } catch (Exception e) {
          // Table not found in rawHmsClient, try icebergHmsClient
        }

        try {
          // Try icebergHmsClient (TestHiveMetastore for Iceberg tables)
          return icebergHmsClient.getTable(dbName, tableName);
        } catch (Exception e) {
          throw new RuntimeException("Failed to get table from both HMS instances: " + dbName + "." + tableName, e);
        }
      }
    };
  }

  /**
   * Validate that the translated Spark SQL can be parsed by Spark's SQL parser.
   *
   * @param spark SparkSession instance
   * @param coralSparkTranslation CoralSpark translation object
   */
  protected boolean validateSparkSql(SparkSession spark, CoralSpark coralSparkTranslation) {
    String sql = coralSparkTranslation.getSparkSql();
    try {
      spark.sessionState().sqlParser().parsePlan(sql);
      return true;
    } catch (ParseException e) {
      throw new RuntimeException("Validation failed, failed to parse the translated spark sql: ", e);
    }
    return false;
  }

  /**
   * Get Coral Spark translation for a given view.
   *
   * @param db Database name
   * @param table Table/view name
   * @param hiveMetastoreClient HiveMetastoreClient for Coral
   * @return CoralSpark translation object
   */
  protected CoralSpark getCoralSparkTranslation(String db, String table, HiveMetastoreClient hiveMetastoreClient) {
    final HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    final RelNode rel = hiveToRelConverter.convertView(db, table);
    Schema coralSchema = ViewToAvroSchemaConverter.create(hiveMetastoreClient).toAvroSchema(db, table);
    return CoralSpark.create(rel, coralSchema, hiveMetastoreClient);
  }
}
