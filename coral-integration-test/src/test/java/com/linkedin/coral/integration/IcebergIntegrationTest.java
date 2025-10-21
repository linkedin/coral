// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Sample integration test demonstrating Spark3 with Iceberg and HiveMetastore 2.0.
 */
public class IcebergIntegrationTest extends SparkIcebergTestBase {

  @Test
  public void testShowDatabases() {
    // Test showing databases in default Hive catalog
    Dataset<Row> hiveDatabases = spark.sql("SHOW DATABASES");
    System.out.println("Hive Catalog - Databases:");
    hiveDatabases.show();
    assertTrue(hiveDatabases.count() > 0, "Hive catalog should have at least one database (default)");

    // Verify default database exists in Hive catalog
    Dataset<Row> defaultDb = spark.sql("SHOW DATABASES LIKE 'default'");
    assertEquals(defaultDb.count(), 1, "Default database should exist in Hive catalog");

    // Test showing databases in Iceberg catalog
    Dataset<Row> icebergDatabases = spark.sql("SHOW DATABASES IN iceberg_catalog");
    System.out.println("Iceberg Catalog - Databases:");
    icebergDatabases.show();
    assertTrue(icebergDatabases.count() > 0, "Iceberg catalog should have at least one database (default)");

    // Verify default database exists in Iceberg catalog
    Dataset<Row> icebergDefaultDb = spark.sql("SHOW DATABASES IN iceberg_catalog LIKE 'default'");
    assertEquals(icebergDefaultDb.count(), 1, "Default database should exist in Iceberg catalog");
  }

  @Test
  public void testCreateIcebergTable() {
    // Create an Iceberg table using Spark3
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.test_table " + "(id BIGINT, name STRING, age INT) "
        + "USING iceberg");

    // Insert some data
    executeSql("INSERT INTO iceberg_catalog.default.test_table VALUES (1, 'Alice', 30), (2, 'Bob', 25)");

    // Query the data
    Dataset<Row> result = spark.sql("SELECT * FROM iceberg_catalog.default.test_table");
    long count = result.count();

    assertEquals(count, 2, "Should have 2 rows");

    // Verify data
    Row firstRow = result.first();
    assertNotNull(firstRow, "First row should not be null");
  }

  @Test
  public void testIcebergTableWithPartitions() {
    // Create a partitioned Iceberg table
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.partitioned_table " + "(id BIGINT, name STRING, year INT) "
        + "USING iceberg " + "PARTITIONED BY (year)");

    // Insert data
    executeSql("INSERT INTO iceberg_catalog.default.partitioned_table VALUES " + "(1, 'Alice', 2023), "
        + "(2, 'Bob', 2023), " + "(3, 'Charlie', 2024)");

    // Query specific partition
    Dataset<Row> result = spark.sql("SELECT * FROM iceberg_catalog.default.partitioned_table WHERE year = 2023");
    long count = result.count();

    assertEquals(count, 2, "Should have 2 rows for year 2023");
  }

  @Test
  public void testIcebergTimeTravel() {
    // Create an Iceberg table
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.time_travel_table " + "(id BIGINT, value STRING) "
        + "USING iceberg");

    // Insert initial data
    executeSql("INSERT INTO iceberg_catalog.default.time_travel_table VALUES (1, 'initial')");

    // Get snapshot ID
    Dataset<Row> snapshots = spark.sql("SELECT snapshot_id FROM iceberg_catalog.default.time_travel_table.snapshots");
    long firstSnapshotId = snapshots.first().getLong(0);

    // Update data
    executeSql("INSERT INTO iceberg_catalog.default.time_travel_table VALUES (2, 'updated')");

    // Query current state
    long currentCount = executeSqlAndGetCount("SELECT * FROM iceberg_catalog.default.time_travel_table");
    assertEquals(currentCount, 2, "Should have 2 rows in current state");

    // Query using time travel to first snapshot
    long timeravelCount =
        executeSqlAndGetCount("SELECT * FROM iceberg_catalog.default.time_travel_table VERSION AS OF " + firstSnapshotId);
    assertEquals(timeravelCount, 1, "Should have 1 row in first snapshot");
  }

  @Test
  public void testHiveMetastoreIntegration() {
    // Create a regular Hive table
    executeSql("CREATE TABLE IF NOT EXISTS default.hive_table " + "(id INT, name STRING) " + "USING parquet");

    // Insert data
    executeSql("INSERT INTO default.hive_table VALUES (1, 'Test')");

    // Query the data
    long count = executeSqlAndGetCount("SELECT * FROM default.hive_table");
    assertEquals(count, 1, "Should have 1 row");

    // Verify we can list tables
    Dataset<Row> tables = spark.sql("SHOW TABLES IN default");
    assertTrue(tables.count() > 0, "Should have at least one table in default database");
  }

  @Test
  public void testIcebergSchemaEvolution() {
    // Create an Iceberg table
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.schema_evolution_table " + "(id BIGINT, name STRING) "
        + "USING iceberg");

    // Insert data
    executeSql("INSERT INTO iceberg_catalog.default.schema_evolution_table VALUES (1, 'Alice')");

    // Add a new column
    executeSql("ALTER TABLE iceberg_catalog.default.schema_evolution_table ADD COLUMNS (email STRING)");

    // Insert data with new schema
    executeSql("INSERT INTO iceberg_catalog.default.schema_evolution_table VALUES (2, 'Bob', 'bob@example.com')");

    // Query all data (old and new schema)
    Dataset<Row> result = spark.sql("SELECT * FROM iceberg_catalog.default.schema_evolution_table");
    long count = result.count();

    assertEquals(count, 2, "Should have 2 rows");

    // Verify schema has 3 columns
    assertEquals(result.columns().length, 3, "Should have 3 columns after schema evolution");
  }
}

