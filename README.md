# Coral

**Coral** performs SQL translations to enable views expressed in HiveQL (and potentially other languages) be accessible in engines such as [Presto](https://prestodb.io/), [Apache Spark](https://spark.apache.org/), and [Apache Pig](https://pig.apache.org/).

## Why Coral?
**Coral** provides several benefits:
- Ensures the consistency and portability of business logic.
- Enables native evaluation of datasets in various compute engines for better optimization.
- Integrates natively with other open source projects such as [Transport UDF](https://github.com/linkedin/transport) for UDF portability.
- Enables users with a repository of Hive views to immediately use those views in other engines.

## Modules
**Coral** consists of following modules:
- Coral-Hive: To convert Hive views with UDFs to standard intermediate representation (Coral IR) based on view logical plans.
- Coral-Presto: To convert Coral IR to Presto SQL.
- Coral-Spark: To convert Coral IR to Spark SQL.
- Coral-Pig: To convert Coral IR to Pig scripts.
- Coral-Schema: Derive avro schema of view using its Coral IR and input schemas of base tables.

In the future, Coral will be extended to allow converting other SQL dialects (than HiveQL), or non-SQL views to Coral IR, allowing all target engines to execute view logic not necessarily expressed in SQL.

## How to Build
Clone the repository:
```bash
git clone https://github.com/linkedin/coral.git
```
Build:
```bash
./gradlew build
```

## How to Use
How Coral integrates with various big data engine [TODO]

## Contributing
Coral is under active development and we welcome contributions from the community.

Please take a look at the [Contribution Agreement](CONTRIBUTING.md) if you'd like to contribute.

## Resources
- Tech-talk: [Coral & Transport UDFs: Building Blocks of a Postmodern Data Warehouse](https://www.slideshare.net/walaa_eldin_moustafa/coral-transport-udfs-building-blocks-of-a-postmodern-data-warehouse-229545076)
