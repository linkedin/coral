# Coral Spark Catalog

A Spark 3.5 [CatalogExtension](https://spark.apache.org/docs/3.5.6/api/java/org/apache/spark/sql/connector/catalog/ViewCatalog.html) that intercepts view resolution to translate view definitions into Spark SQL via Coral's translation pipeline. Views can be defined in any SQL dialect supported by Coral (e.g., HiveQL, Trino SQL, Spark SQL). This enables Spark to transparently query Hive Metastore views without manual SQL rewriting. Table, namespace, and function operations are delegated to the underlying session catalog.

## How It Works

When Spark resolves a view, `CoralSparkViewCatalog` intercepts the request and:

1. Fetches the view definition from Hive Metastore
2. Converts the view to Coral IR (e.g., using `HiveToRelConverter` for HiveQL views)
3. Translates the Coral IR to Spark SQL using `CoralSpark`
4. Derives the view schema via Avro using `ViewToAvroSchemaConverter`
5. Automatically registers any UDFs the view depends on (Hive, Spark, and Transport UDFs)
6. Returns a `View` object that Spark can execute natively

All non-view operations (tables, namespaces, functions) pass through to the session catalog unchanged.

## Configuration

Register `CoralSparkViewCatalog` as the Spark session catalog:

```java
SparkSession spark = SparkSession.builder()
    .config("spark.sql.catalog.spark_catalog", CoralSparkViewCatalog.class.getName())
    .enableHiveSupport()
    .getOrCreate();
```

The catalog discovers the Hive Metastore connection through standard Hive configuration (`hive-site.xml` or `HiveConf` properties).

## UDF Registration

`CoralSparkViewCatalog` automatically registers UDFs that views depend on. Three UDF types are supported:

- **Hive Custom UDFs**: Registered via the Spark `SessionCatalog` with the UDF class name
- **Spark UDFs**: Detected by reflection (classes with a `Seq<Expression>` constructor) and registered via the Spark `FunctionRegistry`
- **Transport UDFs**: Registered by invoking the static `register` method on the UDF class

UDF JAR dependencies are loaded automatically from the artifact URLs provided by `CoralSpark`.

## Dependencies

This module depends on the following Coral modules:

- `coral-hive`: Converts Hive view definitions to Coral IR
- `coral-spark`: Translates Coral IR to Spark SQL and provides UDF metadata
- `coral-schema`: Derives Avro schemas from view definitions for schema extraction

Spark 3.5 (`spark-sql` and `spark-avro`) is a compile-only dependency, expected to be provided by the runtime environment.

## Tests

Tests use an embedded Hive Metastore backed by an in-memory Derby database with a local Spark session configured with `CoralSparkViewCatalog`. The test suite covers:

- View loading with joins, subqueries, aggregations, lateral views, and complex types (arrays, maps, structs)
- Column name casing preservation
- Custom UDF registration and execution
- Trino-to-Spark translation via the catalog pipeline
