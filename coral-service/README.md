# Coral-as-a-Service

**Coral-as-a-Service** or simply, **Coral Service** is a REST API allowing users to make SQL translations using Coral. The service can be spun up and connected to the production Hive Metastore Client or connected a temporary local metastore (this comes with an additional endpoints).

## API Reference

| Endpoint | Query Params |Example | Method | Semantics
|--|--|--|--|--|
| /translate |  <ul><li><strong>query:</strong> Query you want to have translated</li><li><strong>fromLanguage:</strong> input SQL language of query</li><li><strong> toLanguage:</strong> target SQL language you want query translated to</li></ul> | /translate?query=select%20strpos%28%27foobar%27%2C%20%27b%27%29%20as%20pos&fromLanguage=trino&toLanguage=spark </br> **Note:** 'select%20strpos%28%27foobar%27%2C%20%27b%27%29%20as%20pos' is URL decoded as 'select strpos('foobar', 'b') as pos' | GET |   READ - returns the translated query|
| /create  | <ul><li><strong>createQuery:</strong> SQL query to create a DB/table/view</li> | /create?createQuery=CREATE%20TABLE%20IF%20NOT%20EXISTS%20default.employees(e_id%20int%2C%20name%20string) **Note:** 'CREATE%20TABLE%20IF%20NOT%20EXISTS%20default.employees(e_id%20int%2C%20name%20string)' is URL decoded as 'CREATE TABLE IF NOT EXISTS default.employees(e_id int, name string)' | POST | CREATE - creates the metadata of the DB/table/view in the local metastore (**note:** this endpoint is only available if users choose to use Coral Service with local metastore) |

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
To run Coral Service using the **production metastore** (Hive Metastore Client):
4. a) Add authenticated `daliview.headless.keytab` to `coral-service/src/main/resources`
4. b) Appropriately replace all instances of `SET_ME` in `coral-service/src/main/resources/hive.properties`
4. c) Run
```
./gradlew bootRun
```

To run Coral Service using the temporary **local metastore**:
4. a) Run
```
./gradlew bootRun --args='--spring.profiles.active=localMetastore'
```

## Currently Supported Translation Flows
1. Trino to Spark
2. Hive to Trino
3. Hive to Spark