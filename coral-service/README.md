
# Coral-as-a-Service

**Coral-as-a-Service** or simply, **Coral Service** is a service that exposes REST APIs that allow users to interact with Coral without necessarily coming from a compute engine. Currently, the service supports an API for query translation between different dialects and another for interacting with a local Hive Metastore to create example databases, tables, and views so they can be referenced in the translation API. The service can be used in two modes: remote Hive Metastore mode, and local Hive Metastore mode. The remote mode uses an existing (already deployed) Hive Metastore to resolve tables and views, while the local one creates an empty embedded Hive Metastore so users can add their own table and view definitions.

## API Reference
Please note that endpoints marked with * are only available in local Hive Metastore mode.

| Endpoint | Query Params |Example | Method | Semantics  
|--|--|--|--|--|  
| /translate |  <ul><li><strong>query:</strong> Query you want to have translated</li><li><strong>fromLanguage:</strong> input SQL language of query</li><li><strong> toLanguage:</strong> target SQL language you want query translated to</li></ul> | /translate?query=select%20strpos%28%27foobar%27%2C%20%27b%27%29%20as%20pos&fromLanguage=trino&toLanguage=spark </br> **Note:** 'select%20strpos%28%27foobar%27%2C%20%27b%27%29%20as%20pos' is URL decoded as `select strpos('foobar', 'b') as pos` | GET |   READ - returns the translated query|  
| /create * | <ul><li><strong>createQuery:</strong> SQL query to create a DB/table/view</li> | /create?createQuery=CREATE%20TABLE%20IF%20NOT%20EXISTS%20default.employees(e_id%20int%2C%20name%20string) **Note:** 'CREATE%20TABLE%20IF%20NOT%20EXISTS%20default.employees(e_id%20int%2C%20name%20string)' is URL decoded as `CREATE TABLE IF NOT EXISTS default.employees(e_id int, name string)` | POST | CREATE - creates the metadata of the DB/table/view in the local metastore (**note:** this endpoint is only available if users choose to use Coral Service with local metastore) |

## Instructions to Use
1. Clone [Coral repo](https://github.com/linkedin/coral)
```  
git clone https://github.com/linkedin/coral.git  
```  
2. From the root directory of Coral, access the coral-service module
```  
cd coral-service  
```  
3. Build
```  
./gradlew clean build  
```  
To run Coral Service using the **local metastore**:
4.   Run
```  
./gradlew bootRun --args='--spring.profiles.active=localMetastore'  
```  
Example workflow using local metastore:
Note - Use an [online URL encoder](https://www.urlencoder.org/) to encode your "create" SQL queries
5. Create a database called `db1` in local metastore using the /create endpoint
```
createQuery (non URL encoded): CREATE DATABASE IF NOT EXISTS db1

curl -X POST "http://localhost:8080/create?createQuery=CREATE%20DATABASE%20IF%20NOT%20EXISTS%20db1"
```
6. Create a table called `airport` within `test` in local metastore using the /create endpoint
```
createQuery (non URL encoded): CREATE TABLE IF NOT EXISTS db1.airport(name string, country string, area_code int, code string, datepartition string)

curl -X POST "http://localhost:8080/create?createQuery=CREATE%20TABLE%20IF%20NOT%20EXISTS%20db1.airport%28name%20string%2C%20country%20string%2C%20area_code%20int%2C%20code%20string%2C%20datepartition%20string%29"
```

7. Query columns of newly existing `db1.airport`
```
query (non URL encoded): select * from db1.airport

curl "http://localhost:8080/translate?query=select%20%2A%20from%20db1.airport&fromLanguage=hive&toLanguage=trino"
```

To run Coral Service using the **remote metastore** (Hive Metastore Client):
7.  Add your kerberos client keytab file to `coral-service/src/main/resources`
8.  Appropriately replace all instances of `SET_ME` in `coral-service/src/main/resources/hive.properties`
9. Run
```  
./gradlew bootRun  
```  

## Currently Supported Translation Flows
1. Trino to Spark  
   Note: During Trino to Spark translations, views referenced in queries are treated like they are defined in HiveQL and hence cannot be used when translating a view from Trino. Currently, only referencing base tables are supported in Trino queries. This translation path is currently in a POC stage and may need further improvements.
2. Hive to Trino
3. Hive to Spark
