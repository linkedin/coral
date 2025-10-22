// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.avro.Schema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.iceberg.Table;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.hive.HiveCatalog;
import org.apache.iceberg.hive.TestHiveMetastore;
import org.apache.iceberg.types.Type;
import org.apache.iceberg.types.Types;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.schema.avro.ViewToAvroSchemaConverter;
import com.linkedin.coral.spark.CoralSpark;
import com.linkedin.coral.trino.rel2trino.RelToTrinoConverter;

import coral.shading.io.trino.sql.parser.ParsingOptions;
import coral.shading.io.trino.sql.parser.SqlParser;
import coral.shading.io.trino.sql.tree.Statement;

import static org.testng.Assert.*;


/**
 * Base class for integration tests that require Spark3 with Iceberg and HiveMetastore.
 * This class uses:
 * - Iceberg's TestHiveMetastore for iceberg_catalog (optimized for Iceberg)
 * - Derby-based HMS from HiveMetastoreTestBase for spark_catalog (regular Hive tables)
 */
public class IcebergTestBase extends HiveMetastoreTestBase {

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
    final RelNode rel = getRelNode(db, table, hiveMetastoreClient);
    Schema coralSchema = ViewToAvroSchemaConverter.create(hiveMetastoreClient).toAvroSchema(db, table);
    return CoralSpark.create(rel, coralSchema, hiveMetastoreClient);
  }

  protected static RelNode getRelNode(String db, String table, HiveMetastoreClient hiveMetastoreClient) {
    final HiveToRelConverter hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);
    final RelNode rel = hiveToRelConverter.convertView(db, table);
    return rel;
  }

  /**
   * Validate Trino SQL syntax by parsing with Trino's SqlParser.
   * This ensures the SQL is syntactically valid according to Trino's grammar.
   *
   * @param trinoSql Trino SQL string
   * @return true if SQL is syntactically valid
   * @throws RuntimeException if SQL is invalid
   */
  protected Statement validateTrinoSql(String trinoSql) {
    try {
      SqlParser trinoParser = new SqlParser();
      Statement statement = trinoParser.createStatement(trinoSql,
          new ParsingOptions(ParsingOptions.DecimalLiteralTreatment.AS_DECIMAL));
      return statement;
    } catch (Exception e) {
      throw new RuntimeException("Validation failed, failed to parse the translated Trino SQL: " + trinoSql, e);
    }
  }

  /**
   * Get Coral Trino translation for a given view.
   *
   * @param db Database name
   * @param table Table/view name
   * @param hiveMetastoreClient HiveMetastoreClient for Coral
   * @return Trino SQL string
   */
  protected String getCoralTrinoTranslation(String db, String table, HiveMetastoreClient hiveMetastoreClient) {
    final RelNode rel = getRelNode(db, table, hiveMetastoreClient);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter(hiveMetastoreClient);
    return relToTrinoConverter.convert(rel);
  }

  /**
   * Helper method to load an Iceberg table using HiveCatalog.
   *
   * @param database Database name
   * @param tableName Table name
   * @return Iceberg Table object
   */
  protected Table loadIcebergTable(String database, String tableName) {
    HiveCatalog icebergCatalog = new HiveCatalog();
    icebergCatalog.setConf(icebergHiveConf);
    icebergCatalog.initialize("iceberg_catalog", java.util.Collections.emptyMap());
    TableIdentifier tableId = TableIdentifier.of(database, tableName);
    return icebergCatalog.loadTable(tableId);
  }

  /**
   * Helper method to get a field's type from an Iceberg table.
   *
   * @param table Iceberg Table object
   * @param fieldName Name of the field to retrieve
   * @return Iceberg Type for the specified field
   */
  protected Type getIcebergFieldType(Table table, String fieldName) {
    Types.NestedField field = table.schema().findField(fieldName);
    assertNotNull(field, fieldName + " field should exist in Iceberg table");
    return field.type();
  }

  /**
   * Helper method to get a field's RelDataType from Coral's RelNode.
   *
   * @param relNode Coral RelNode
   * @param fieldName Name of the field to retrieve
   * @return RelDataType for the specified field
   */
  protected RelDataType getCoralFieldType(RelNode relNode, String fieldName) {
    RelDataType fieldType = relNode.getRowType().getFieldList().stream()
        .filter(field -> field.getName().equals(fieldName))
        .map(field -> field.getType())
        .findFirst()
        .orElse(null);
    assertNotNull(fieldType, fieldName + " field should exist in Coral translation");
    return fieldType;
  }

  /**
   * Helper method to get timestamp precision from Iceberg.
   * Iceberg timestamps are always stored with microsecond precision (6).
   *
   * @param icebergType Iceberg Type (should be TimestampType)
   * @return Precision value (always 6 for timestamps)
   */
  protected int getIcebergTimestampPrecision(Type icebergType) {
    assertTrue(icebergType instanceof Types.TimestampType,
        "Type should be TimestampType in Iceberg");
    // Iceberg timestamps are always stored as microseconds (precision 6)
    // The TimestampType class doesn't expose a precision() method because
    // precision is fixed by the Iceberg specification.
    return 6;
  }

  /**
   * Helper method to test timestamp precision comparison between Iceberg and Coral.
   *
   * @param icebergTableName Iceberg table name
   * @param relNode Coral RelNode
   * @param fieldName Name of the timestamp field to test
   */
  protected void assertTimestampTypeMatches(String icebergTableName, RelNode relNode, String fieldName) {
    // Get timestamp type from Coral's RelDataType
    RelDataType coralFieldType = getCoralFieldType(relNode, fieldName);
    assertEquals(coralFieldType.getSqlTypeName(), SqlTypeName.TIMESTAMP,
        fieldName + " should be TIMESTAMP type in Coral");

    int coralPrecision = coralFieldType.getPrecision();
    System.out.println("Coral timestamp precision for " + fieldName + ": " + coralPrecision);

    // Get timestamp type from Iceberg table
    Table icebergTable = loadIcebergTable("default", icebergTableName);
    Type icebergFieldType = getIcebergFieldType(icebergTable, fieldName);
    int icebergPrecision = getIcebergTimestampPrecision(icebergFieldType);
    System.out.println("Iceberg timestamp precision for " + fieldName + ": " + icebergPrecision);

    // Compare the precisions between Iceberg and Coral
    // Note: This test documents the current behavior. If Coral is not preserving precision,
    // the assertion will fail and should be fixed.
    assertEquals(coralPrecision, icebergPrecision,
        "Coral should preserve the timestamp precision from Iceberg table (microsecond precision = 6)");
  }
}
