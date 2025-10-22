// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.trino.rel2trino.HiveToTrinoConverter;
import com.linkedin.coral.trino.trino2rel.parsetree.TrinoParserDriver;

import static org.testng.Assert.*;


/**
 * Sample integration test demonstrating Spark3 with Iceberg and HiveMetastore 2.0.
 */
public class IcebergIntegrationTest extends SparkIcebergTestBase {

  @Test
  public void testCreateHiveViewOnIcebergTable() throws Exception {
    // Create an Iceberg table using fully qualified name
    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.test_iceberg_table "
        + "(id BIGINT, name STRING, age INT, salary DOUBLE, hire_date TIMESTAMP) "
        + "USING iceberg");

    // Insert test data into the Iceberg table
    executeSql("INSERT INTO iceberg_catalog.default.test_iceberg_table " +
        "SELECT 1L, 'Alice', 30, 75000.0, current_timestamp() UNION ALL " +
        "SELECT 2L, 'Bob', 25, 65000.0, current_timestamp() UNION ALL " +
        "SELECT 3L, 'Charlie', 35, 85000.0, current_timestamp()");

    // Create a Hive view on top of the Iceberg table
    // The view filters employees with age > 25 and selects specific columns including timestamp
    executeSql("USE iceberg_catalog");
    executeSql("CREATE OR REPLACE VIEW spark_catalog.default.iceberg_table_view AS " +
        "SELECT id, name, age, hire_date FROM default.test_iceberg_table WHERE age > 25");
    executeSql("USE spark_catalog");

    // Query the Hive view
    Dataset<Row> viewResult = spark.sql("SELECT * FROM spark_catalog.default.iceberg_table_view");
    long viewCount = viewResult.count();

    // Verify the view returns the expected number of rows (2 employees with age > 25)
    assertEquals(viewCount, 2, "View should return 2 rows with age > 25");

    // Verify we can filter on the view
    Dataset<Row> filteredView = spark.sql("SELECT name FROM spark_catalog.default.iceberg_table_view WHERE age >= 30");
    assertEquals(filteredView.count(), 2, "Should have 2 employees with age >= 30");

    // Test Coral Spark translation
    String db = "default";
    String table = "iceberg_table_view";

    HiveMetastoreClient hiveMetastoreClient = createCoralHiveMetastoreClient();

    // Test Spark translation and validation
    CoralSpark coralSparkTranslation = getCoralSparkTranslation(db, table, hiveMetastoreClient);
    assertTrue(validateSparkSql(spark, coralSparkTranslation));

    // Test Trino translation and validation
    HiveToTrinoConverter hiveToTrinoConverter = HiveToTrinoConverter.create(hiveMetastoreClient);
    String trinoSql = hiveToTrinoConverter.toTrinoSql(db, table);
    assertNotNull(trinoSql, "Trino SQL translation should not be null");
    assertTrue(validateTrinoSql(trinoSql), "Trino SQL validation should succeed");

    RelNode relNode = getRelNode(db, table, hiveMetastoreClient);
    assertNotNull(relNode, "RelNode conversion should not be null");
    RelDataType timestampField = relNode.getRowType().getFieldList().stream()
        .filter(field -> field.getName().equals("hire_date"))
        .map(field -> field.getType())
        .findFirst()
        .orElse(null);

    assertNotNull(timestampField, "hire_date field should exist in RelNode");
    assertEquals(timestampField.getSqlTypeName(), SqlTypeName.TIMESTAMP,
        "hire_date field should be of TIMESTAMP type");
    assertEquals(timestampField.getPrecision(), -1,
        "TIMESTAMP field should have precision 6 (microsecond precision) when bug is fixed"
    );

    // Drop the view after test
    executeSql("DROP VIEW IF EXISTS spark_catalog.default.iceberg_table_view");
    executeSql("DROP TABLE IF EXISTS iceberg_catalog.default.test_iceberg_table");
  }

  private RelNode getRelNode(String db, String view, HiveMetastoreClient hiveMetastoreClient) {
    return new HiveToRelConverter(hiveMetastoreClient)
        .convertView(db, view);
  }
}
