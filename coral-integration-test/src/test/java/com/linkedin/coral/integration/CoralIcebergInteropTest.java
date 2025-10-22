// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import coral.shading.io.trino.sql.tree.Statement;
import org.apache.calcite.rel.RelNode;
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

    String db = "default";
    String view = "hive_view";
    String table = "test_iceberg_table";

    executeSql("CREATE TABLE IF NOT EXISTS iceberg_catalog." + db + "." + table + " "
        + "(id BIGINT, name STRING, age INT, salary DOUBLE, hire_date TIMESTAMP) "
        + "USING iceberg");

    executeSql("INSERT INTO iceberg_catalog." + db + "." + table + " " +
        "SELECT 1L, 'Alice', 30, 75000.0, current_timestamp() UNION ALL " +
        "SELECT 2L, 'Bob', 25, 65000.0, current_timestamp() UNION ALL " +
        "SELECT 3L, 'Charlie', 35, 85000.0, current_timestamp()");

    // Coral does not support 3-part namespace for the referenced table, so switching the default catalog
    // for during view creation.
    executeSql("USE iceberg_catalog");
    executeSql("CREATE OR REPLACE VIEW spark_catalog." + db + "." + view + " " +
        "SELECT * FROM default.test_iceberg_table WHERE age > 25");
    executeSql("USE spark_catalog");

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

    // Test timestamp precision comparison between Iceberg and Coral
    assertTimestampTypeMatches(table, relNode, "hire_date");

   // Drop the view after test
    executeSql("DROP VIEW IF EXISTS spark_catalog." + db + "." + view);
    executeSql("DROP TABLE IF EXISTS iceberg_catalog." + db + "." + table);
  }
}
