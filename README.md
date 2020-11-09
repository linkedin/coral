# Coral
<p align="center">
 <img src="docs/coral-logo.png" width="400" title="Coral Logo">
</p>

**Coral** is a library for analyzing, processing, and rewriting views defined in the Hive Metastore, and sharing them
across multiple execution engines. It performs SQL translations to enable views expressed in HiveQL (and potentially
other languages) to be accessible in engines such as [Presto](https://prestosql.io/),
[Apache Spark](https://spark.apache.org/), [Apache Pig](https://pig.apache.org/), 
or Java Streaming API with [Apache Beam](https://beam.apache.org/).
Coral not only translates view definitions between different SQL/non-SQL dialects, but also rewrites expressions to
produce semantically equivalent ones, taking into account the semantics of the target language or engine.
For example, it automatically composes new built-in expressions that are equivalent to each built-in expression in the
 source view definition. Additionally, it integrates with [Transport UDFs](https://github.com/linkedin/transport)
to enable translating and executing user-defined functions (UDFs) across Hive, Presto, Spark, and Pig. Coral is under
active development. Currently, we are looking into expanding the set of input view language APIs beyond HiveQL,
and implementing query rewrite algorithms for data governance and query optimization.

## Modules
**Coral** consists of following modules:
- Coral-Hive: Converts definitions of Hive views with UDFs to equivalent view logical plan.
- Coral-Presto: Converts view logical plan to Presto SQL.
- Coral-Spark: Converts view logical plan to Spark SQL.
- Coral-Pig: Converts view logical plan to Pig-latin.
- Coral-Schema: Derives Avro schema of view using view logical plan and input Avro schemas of base tables.
- Coral-Spark-Plan: Converts Spark plan strings to equivalent logical plan (in progress).
- Coral-Beam: Convert view logical plan to Beam Java API streaming code
- Coral-Beam-runtime: runtime module to support Coral-Beam

## How to Build
Clone the repository:
```bash
git clone https://github.com/linkedin/coral.git
```
Build:
```bash
./gradlew build
```

## Contributing
The project is under active development and we welcome contributions of different forms.
Please see the [Contribution Agreement](CONTRIBUTING.md).

## Resources
- [Coral & Transport UDFs: Building Blocks of a Postmodern Data Warehouse](https://www.slideshare.net/walaa_eldin_moustafa/coral-transport-udfs-building-blocks-of-a-postmodern-data-warehouse-229545076), Tech-talk, Facebook HQ, 2/28/2020.
