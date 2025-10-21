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

    // Test showing databases in Iceberg catalog using USE and SHOW
    spark.sql("USE iceberg_catalog");
    Dataset<Row> icebergDatabases = spark.sql("SHOW DATABASES");
    System.out.println("Iceberg Catalog - Databases:");
    icebergDatabases.show();
    assertTrue(icebergDatabases.count() > 0, "Iceberg catalog should have at least one database (default)");

    // Verify default database exists in Iceberg catalog
    Dataset<Row> icebergDefaultDb = spark.sql("SHOW DATABASES LIKE 'default'");
    assertEquals(icebergDefaultDb.count(), 1, "Default database should exist in Iceberg catalog");

    // Switch back to default catalog
    spark.sql("USE spark_catalog");
  }

  @Test
  public void testCreateIcebergTable() {
    // Test 3-part namespace: catalog.database.table
    // Create an Iceberg table using fully qualified name
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.test_table "
        + "(id BIGINT, name STRING, age INT, tscol TIMESTAMP) "
        + "USING iceberg");

    // Insert some data using INSERT INTO ... SELECT pattern (avoids catalog resolution issue with VALUES)
    executeSql("INSERT INTO iceberg_catalog.default.test_table " +
        "SELECT 1L, 'Alice', 30, current_timestamp() UNION ALL " +
        "SELECT 2L, 'Bob', 25, current_timestamp()");

    // Query the data using 3-part namespace
    Dataset<Row> result = spark.sql("SELECT * FROM iceberg_catalog.default.test_table");
    long count = result.count();

    assertEquals(count, 2, "Should have 2 rows");

    // Verify data
    Row firstRow = result.first();
    assertNotNull(firstRow, "First row should not be null");

    // Also verify we can query without switching catalogs
    Dataset<Row> result2 = spark.sql("SELECT id, name FROM iceberg_catalog.default.test_table WHERE age > 25");
    assertEquals(result2.count(), 1, "Should have 1 row with age > 25");
  }

  @Test
  public void testCreateHiveTable() {
    // Test 3-part namespace: catalog.database.table
    // Create an Iceberg table using fully qualified name
    executeSql("CREATE TABLE IF NOT EXISTS spark_catalog.default.test_table "
        + "(id BIGINT, name STRING, age INT, tscol TIMESTAMP) ");

    // Insert some data using INSERT INTO ... SELECT pattern (avoids catalog resolution issue with VALUES)
    executeSql("INSERT INTO spark_catalog.default.test_table " +
        "SELECT 1L, 'Alice', 30, current_timestamp() UNION ALL " +
        "SELECT 2L, 'Bob', 25, current_timestamp()");

    // Query the data using 3-part namespace
    Dataset<Row> result = spark.sql("SELECT * FROM spark_catalog.default.test_table");
    long count = result.count();

    assertEquals(count, 2, "Should have 2 rows");

    // Verify data
    Row firstRow = result.first();
    assertNotNull(firstRow, "First row should not be null");

    // Also verify we can query without switching catalogs
    Dataset<Row> result2 = spark.sql("SELECT id, name FROM spark_catalog.default.test_table WHERE age > 25");
    assertEquals(result2.count(), 1, "Should have 1 row with age > 25");
  }

}

