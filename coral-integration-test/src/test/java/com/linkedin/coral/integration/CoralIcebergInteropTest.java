// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import coral.shading.io.trino.sql.tree.Statement;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.spark.CoralSpark;

import static org.testng.Assert.*;


/**
 * Sample integration test demonstrating Spark3 with Iceberg and HiveMetastore 2.0.
 */
public class CoralIcebergInteropTest extends IcebergTestBase {

  @Test
  public void testValidateHiveViewOnIcebergTable() throws Exception {

    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog.default.test_iceberg_table "
        + "(id BIGINT, name STRING, age INT, salary DOUBLE, hire_date TIMESTAMP) "
        + "USING iceberg");

    executeSql("INSERT INTO iceberg_catalog.default.test_iceberg_table " +
        "SELECT 1L, 'Alice', 30, 75000.0, current_timestamp() UNION ALL " +
        "SELECT 2L, 'Bob', 25, 65000.0, current_timestamp() UNION ALL " +
        "SELECT 3L, 'Charlie', 35, 85000.0, current_timestamp()");

    // Coral does not support 3-part namespace for the referenced table, so switching the default catalog
    // for during view creation.
    executeSql("USE iceberg_catalog");
    executeSql("CREATE OR REPLACE VIEW spark_catalog.default.iceberg_table_view AS " +
        "SELECT * FROM default.test_iceberg_table WHERE age > 25");
    executeSql("USE spark_catalog");

    String db = "default";
    String view = "iceberg_table_view";

    HiveMetastoreClient coralHiveMetastoreClient = createCoralHiveMetastoreClient();

    // Test Coral Trino translation for View on Iceberg table
    String trinoSql = getCoralTrinoTranslation(db, view, coralHiveMetastoreClient);
    Statement trinoStatement = validateTrinoSql(trinoSql);
    assertNotNull(trinoStatement, "Trino SQL should parse successfully");
    System.out.println("\nTrino syntax validation passed\n" + trinoSql + "\n");

    // Test Coral Spark translation for View on Iceberg table
    CoralSpark coralSparkTranslation = getCoralSparkTranslation(db, view, coralHiveMetastoreClient);
    assertTrue(validateSparkSql(spark, coralSparkTranslation), "Spark SQL should be valid");
    System.out.println("\nSpark syntax validation passed\n" + coralSparkTranslation.getSparkSql() + "\n");

    // Get the RelNode for the view and validate types for interoperability check.
    RelNode relNode = getRelNode(db, view, coralHiveMetastoreClient);

    // Find the timestamp field (hire_date) -> Buggy because Coral is not preserving precision for TIMESTAMP type.
    RelDataType timestampField = relNode.getRowType().getFieldList().stream()
        .filter(field -> field.getName().equals("hire_date"))
        .map(field -> field.getType())
        .findFirst()
        .orElse(null);
    assertNotNull(timestampField, "hire_date timestamp field should exist in the view");
    assertEquals(timestampField.getSqlTypeName(), SqlTypeName.TIMESTAMP,
        "Field should be TIMESTAMP type");
    assertEquals(timestampField.getPrecision(), -1,
        "TIMESTAMP field should have precision 6 (microsecond precision) when bug is fixed");

   // Drop the view after test
    executeSql("DROP VIEW IF EXISTS spark_catalog.default.iceberg_table_view");
    executeSql("DROP TABLE IF EXISTS iceberg_catalog.default.test_iceberg_table");
  }
}
