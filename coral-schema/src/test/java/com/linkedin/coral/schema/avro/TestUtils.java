/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;
import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;

import static org.apache.calcite.sql.type.OperandTypes.*;


public class TestUtils {
  private static Driver driver;
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  public static HiveMetastoreClient setup() throws HiveException, MetaException {
    HiveConf conf = getHiveConf();
    SessionState.start(conf);
    driver = new Driver(conf);
    HiveMetastoreClient metastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());

    initializeTables();
    initializeUdfs();

    return metastoreClient;
  }

  public static void executeCreateViewQuery(String dbName, String viewName, String sql) {
    executeQuery("DROP VIEW IF EXISTS " + dbName + "." + viewName);
    executeQuery(sql);
  }

  public static String loadSchema(String resource) {
    InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(resource);
    return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
  }

  public static void registerUdfs() {
    // add the following 3 test UDF to StaticHiveFunctionRegistry for testing purpose.
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF1", ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2", ReturnTypes.BOOLEAN,
        family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF3", ReturnTypes.INTEGER,
        family(SqlTypeFamily.INTEGER));
  }

  private static void initializeTables() {
    String baseComplexSchema = loadSchema("base-complex.avsc");
    String baseEnumSchema = loadSchema("base-enum.avsc");
    String baseLateralViewSchema = loadSchema("base-lateralview.avsc");
    String baseNullabilitySchema = loadSchema("base-nullability.avsc");
    String baseCasePreservation = loadSchema("base-casepreservation.avsc");

    executeCreateTableQuery("default", "basecomplex", baseComplexSchema);
    executeCreateTableQuery("default", "baseenum", baseEnumSchema);
    executeCreateTableQuery("default", "baselateralview", baseLateralViewSchema);
    executeCreateTableQuery("default", "basenullability", baseNullabilitySchema);
    executeCreateTableWithPartitionQuery("default", "basecasepreservation", baseCasePreservation);
  }

  private static void initializeUdfs() {
    List<String> viewsToCreateLessThanHundred = Arrays.asList(
        "foo_dali_udf",
        "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator",
        "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default",
        viewsToCreateLessThanHundred,
        "LessThanHundred",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF1");

    List<String> viewsToCreateGreaterThanHundred = Arrays.asList(
        "foo_dali_udf2",
        "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator",
        "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default",
        viewsToCreateGreaterThanHundred,
        "GreaterThanHundred",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF2");

    List<String> viewsToCreateFuncSquare = Arrays.asList(
        "foo_dali_udf3",
        "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator",
        "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default",
        viewsToCreateFuncSquare,
        "FuncSquare",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF3");
  }

  private static void executeCreateTableQuery(String dbName, String tableName, String schema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    executeQuery("CREATE EXTERNAL TABLE " + tableName + " "
        + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' "
        + "STORED AS "
        + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
        + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' "
        + "TBLPROPERTIES ('" + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static void executeCreateTableWithPartitionQuery(String dbName, String tableName, String schema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    executeQuery("CREATE EXTERNAL TABLE " + tableName + " "
        + "PARTITIONED BY (datepartition string) "
        + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' "
        + "STORED AS "
        + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
        + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' "
        + "TBLPROPERTIES ('" + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static void executeCreateFunctionQuery(String dbName, List<String> viewNames, String functionName, String functionClass) {
    for (String viewName : viewNames) {
      String expandedFunctionName = dbName + "_" + viewName + "_" + functionName;
      executeQuery("DROP FUNCTION IF EXISTS " + dbName + "." + expandedFunctionName);
      executeQuery("CREATE FUNCTION " + expandedFunctionName + " as '" + functionClass + "'");
    }
  }

  private static void executeQuery(String sql) {
    while(true){
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  private static HiveConf getHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-schema");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/schema");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/schema");
    return hiveConf;
  }
}
