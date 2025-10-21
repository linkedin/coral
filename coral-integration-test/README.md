# Coral Integration Test Module

This module provides integration tests for Coral with **Spark 3.1.1**, **Apache Iceberg**, and **HiveMetastore 2.0**.

## Architecture

The integration test infrastructure provides:

1. **Two Independent In-Memory HiveMetaStore Services**
   - Each runs as a Thrift server within the test JVM
   - Dynamically assigned ports to avoid conflicts
   - Separate Derby databases for complete isolation
   - No need for external HMS processes

2. **Dual Catalog Support**
   - **Hive Catalog (`spark_catalog`)**: Standard Hive tables with its own HMS instance
   - **Iceberg Catalog (`iceberg_catalog`)**: Iceberg tables with a separate HMS instance

3. **3-Part Namespace Support**
   - Fully qualified table names: `catalog.database.table`
   - Examples:
     - `spark_catalog.default.hive_table` (Hive table)
     - `iceberg_catalog.default.iceberg_table` (Iceberg table)

## Key Components

### HiveMetastoreTestBase

Base class that sets up two in-memory HiveMetaStore services:

```java
public class HiveMetastoreTestBase {
  // Creates two HMS Thrift servers with:
  // - Separate Derby databases
  // - Separate warehouse directories
  // - Different URIs (e.g., thrift://localhost:12345, thrift://localhost:12346)
  
  protected HiveConf hiveConf;           // For Hive catalog
  protected HiveConf icebergHiveConf;    // For Iceberg catalog
  protected String hiveMetastoreUri;     // Dynamic port
  protected String icebergMetastoreUri;  // Dynamic port
}
```

### SparkIcebergTestBase

Extends `HiveMetastoreTestBase` and configures SparkSession:

```java
public class SparkIcebergTestBase extends HiveMetastoreTestBase {
  protected SparkSession spark;
  
  // Configures Spark with:
  // - Iceberg extensions
  // - Hive catalog (spark_catalog) -> Hive HMS
  // - Iceberg catalog (iceberg_catalog) -> Iceberg HMS
}
```

## Usage Examples

### Creating and Querying Iceberg Tables

```java
@Test
public void testIcebergTable() {
  // Create Iceberg table with 3-part namespace
  spark.sql("CREATE TABLE iceberg_catalog.default.my_table " +
            "(id BIGINT, name STRING) USING iceberg");
  
  // Insert data
  spark.sql("INSERT INTO iceberg_catalog.default.my_table " +
            "VALUES (1, 'Alice'), (2, 'Bob')");
  
  // Query with 3-part namespace
  Dataset<Row> result = spark.sql(
    "SELECT * FROM iceberg_catalog.default.my_table WHERE id > 1"
  );
}
```

### Creating and Querying Hive Tables

```java
@Test
public void testHiveTable() {
  // Create Hive table with 3-part namespace
  spark.sql("CREATE TABLE spark_catalog.default.my_table " +
            "(id INT, name STRING) USING parquet");
  
  // Insert data
  spark.sql("INSERT INTO spark_catalog.default.my_table " +
            "VALUES (1, 'Test')");
  
  // Query
  Dataset<Row> result = spark.sql(
    "SELECT * FROM spark_catalog.default.my_table"
  );
}
```

### Switching Between Catalogs

```java
@Test
public void testCatalogSwitching() {
  // Option 1: Use 3-part namespace (recommended)
  spark.sql("SELECT * FROM iceberg_catalog.default.table1");
  spark.sql("SELECT * FROM spark_catalog.default.table2");
  
  // Option 2: Switch catalog context
  spark.sql("USE iceberg_catalog");
  spark.sql("SELECT * FROM default.table1");  // Uses iceberg_catalog
  
  spark.sql("USE spark_catalog");
  spark.sql("SELECT * FROM default.table2");  // Uses spark_catalog
}
```

## Configuration Details

### Spark Configuration for 3-Part Namespace

```java
SparkSession.builder()
  // Iceberg extensions for CREATE TABLE ... USING iceberg
  .config("spark.sql.extensions", 
          "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
  
  // Iceberg catalog configuration
  .config("spark.sql.catalog.iceberg_catalog", 
          "org.apache.iceberg.spark.SparkCatalog")
  .config("spark.sql.catalog.iceberg_catalog.type", "hive")
  .config("spark.sql.catalog.iceberg_catalog.uri", "thrift://localhost:12345")
  .config("spark.sql.catalog.iceberg_catalog.warehouse", "/path/to/warehouse")
  .config("spark.sql.catalog.iceberg_catalog.catalog-impl", 
          "org.apache.iceberg.hive.HiveCatalog")
  
  // Hive catalog configuration (default spark_catalog)
  .config("hive.metastore.uris", "thrift://localhost:12346")
  .config("spark.sql.catalogImplementation", "hive")
  .enableHiveSupport()
  .getOrCreate();
```

## Running Tests

```bash
# Run all integration tests
./gradlew :coral-integration-test:test

# Run specific test
./gradlew :coral-integration-test:test --tests IcebergIntegrationTest.testCreateIcebergTable

# Run with Java 8
export JAVA_HOME=/path/to/jdk1.8
./gradlew :coral-integration-test:test
```

## Key Features

1. **Automatic Database Creation**: Both HMS instances automatically create a `default` database
2. **Dynamic Port Assignment**: Ports are automatically assigned to avoid conflicts
3. **Isolated Environments**: Each catalog has completely separate metadata storage
4. **Sequential Test Execution**: Tests run sequentially to avoid Derby locking issues
5. **Clean Lifecycle Management**: Automatic setup and teardown of HMS services

## Technical Notes

### Why Two Separate HMS Instances?

- **Isolation**: Hive and Iceberg tables don't interfere with each other
- **Flexibility**: Each can have different configurations
- **Realistic Testing**: Mimics production setups with separate metastores

### Derby Database Isolation

Each HMS instance uses its own Derby database:
- Hive HMS: `/tmp/hive-metastore-db-xxx/metastore_db`
- Iceberg HMS: `/tmp/iceberg-metastore-db-yyy/metastore_db`

This prevents the Derby locking issues that occur when multiple components try to access the same database.

### In-Memory HMS vs Embedded HMS

- **Embedded HMS**: No network service, direct database access
- **In-Memory HMS**: Thrift server running in the test JVM on `localhost`

We use **in-memory HMS** with Thrift servers because:
1. It's more realistic (mimics production setups)
2. Allows separate HMS instances with different URIs
3. Spark's Iceberg catalog works better with URI-based HMS connections

## Troubleshooting

### Derby Locking Errors

If you see "Another instance of Derby may have already booted the database":
- Ensure `maxParallelForks = 1` in `build.gradle`
- Check that each HMS uses a different Derby database directory

### Table Not Found Errors

If you see "Table or view not found":
- Verify you're using `USING iceberg` clause for Iceberg tables
- Check that you're using the correct 3-part namespace
- Ensure the target HMS service is running (check logs for "Started in-memory HiveMetaStore on port XXX")

### Connection Refused Errors

If HMS connection fails:
- The in-memory HMS services may not have started properly
- Check thread startup logs
- Increase the sleep time after server startup (currently 2 seconds)

## Dependencies

- **Spark**: 3.1.1
- **Iceberg**: 1.3.1
- **Hive Metastore**: 2.0.1
- **Derby**: 10.14.2.0
- **Java**: 8 (required for compatibility)

## Future Enhancements

Potential improvements:
1. Add support for partitioned Iceberg tables
2. Add time-travel query tests
3. Add schema evolution tests
4. Add support for custom Iceberg table properties
5. Add tests for Coral SQL translation with Iceberg tables

