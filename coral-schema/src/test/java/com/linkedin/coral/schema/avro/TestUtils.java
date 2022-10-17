/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.common.functions.FunctionReturnTypes;
import com.linkedin.coral.hive.hive2rel.functions.StaticHiveFunctionRegistry;

import static org.apache.calcite.sql.type.OperandTypes.*;


public class TestUtils {
  public static final String CORAL_SCHEMA_TEST_DIR = "coral.schema.test.dir";

  private static Driver driver;
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  public static HiveMetastoreClient setup(HiveConf conf) throws HiveException, MetaException, IOException {
    String testDir = conf.get(CORAL_SCHEMA_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
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
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF1",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF2",
        ReturnTypes.BOOLEAN, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDF3",
        ReturnTypes.INTEGER, family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedTableFunction("com.linkedin.coral.hive.hive2rel.CoralTestUDTF",
        ImmutableList.of("col1"), ImmutableList.of(SqlTypeName.INTEGER), family(SqlTypeFamily.INTEGER));
    StaticHiveFunctionRegistry.createAddUserDefinedFunction(
        "com.linkedin.coral.hive.hive2rel.CoralTestUDFReturnStruct", FunctionReturnTypes
            .rowOf(ImmutableList.of("isEven", "number"), ImmutableList.of(SqlTypeName.BOOLEAN, SqlTypeName.INTEGER)),
        family(SqlTypeFamily.INTEGER));
  }

  private static void initializeTables() {
    String baseComplexSchema = loadSchema("base-complex.avsc");
    String baseComplexUnionCompatible = loadSchema("base-complex-union-compatible.avsc");
    String baseEnumSchema = loadSchema("base-enum.avsc");
    String baseBytes = loadSchema("base-bytes.avsc");
    String baseFixed = loadSchema("base-fixed.avsc");
    String baseLateralViewSchema = loadSchema("base-lateralview.avsc");
    String baseNullabilitySchema = loadSchema("base-nullability.avsc");
    String baseCasePreservation = loadSchema("base-casepreservation.avsc");
    String baseComplexFieldSchema = loadSchema("base-complex-fieldschema");
    String baseNestedComplexSchema = loadSchema("base-nested-complex.avsc");
    String baseNullTypeFieldSchema = loadSchema("base-null-type-field.avsc");
    String baseTimestampTypeFieldSchema = loadSchema("base-timestamp-type-field.avsc");
    String baseComplexUnionTypeSchema = loadSchema("base-complex-union-type.avsc");
    String baseNestedUnionSchema = loadSchema("base-nested-union.avsc");
    String baseComplexLowercase = loadSchema("base-complex-lowercase.avsc");
    String baseComplexNullableWithDefaults = loadSchema("base-complex-nullable-with-defaults.avsc");
    String basePrimitive = loadSchema("base-primitive.avsc");

    executeCreateTableQuery("default", "basecomplex", baseComplexSchema);
    executeCreateTableQuery("default", "basecomplexunioncompatible", baseComplexUnionCompatible);
    executeCreateTableQuery("default", "baseenum", baseEnumSchema);
    executeCreateTableQuery("default", "basebytes", baseBytes);
    executeCreateTableQuery("default", "basefixed", baseFixed);
    executeCreateTableQuery("default", "baselateralview", baseLateralViewSchema);
    executeCreateTableQuery("default", "basenullability", baseNullabilitySchema);
    executeCreateTableQuery("default", "basenulltypefield", baseNullTypeFieldSchema);
    executeCreateTableQuery("default", "basetimestamptypefield", baseTimestampTypeFieldSchema);
    executeCreateTableQuery("default", "basecomplexuniontype", baseComplexUnionTypeSchema);
    executeCreateTableQuery("default", "basenestedunion", baseNestedUnionSchema);
    executeCreateTableQuery("default", "basecomplexlowercase", baseComplexLowercase);
    executeCreateTableQuery("default", "baseprimitive", basePrimitive);
    executeCreateTableWithPartitionQuery("default", "basecasepreservation", baseCasePreservation);
    executeCreateTableWithPartitionFieldSchemaQuery("default", "basecomplexfieldschema", baseComplexFieldSchema);
    executeCreateTableWithPartitionQuery("default", "basenestedcomplex", baseNestedComplexSchema);
    executeCreateTableWithPartitionQuery("default", "basecomplexnullablewithdefaults", baseComplexNullableWithDefaults);

    String baseComplexSchemaWithDoc = loadSchema("docTestResources/base-complex-with-doc.avsc");
    String baseEnumSchemaWithDoc = loadSchema("docTestResources/base-enum-with-doc.avsc");
    String baseLateralViewSchemaWithDoc = loadSchema("docTestResources/base-lateralview-with-doc.avsc");
    executeCreateTableQuery("default", "basecomplexwithdoc", baseComplexSchemaWithDoc);
    executeCreateTableQuery("default", "baseenumwithdoc", baseEnumSchemaWithDoc);
    executeCreateTableQuery("default", "baselateralviewwithdoc", baseLateralViewSchemaWithDoc);

    // Creates a table with deep nested structs
    executeQuery("DROP TABLE IF EXISTS basedeepnestedcomplex");
    executeQuery("CREATE TABLE IF NOT EXISTS basedeepnestedcomplex("
        + "struct_col_1 struct<struct_col_2:struct<struct_col_3:struct<int_field_1:int>>>, "
        + "array_col_1 array<struct<array_col_2:array<struct<int_field_2:int>>>>, "
        + "map_col_1 map<string, struct<map_col_2:map<string, struct<int_field_3:int>>>>, "
        + "struct_col_4 struct<map_col_3: map<string, struct<struct_col_5:struct<int_field_4:int>>>, "
        + "array_col_3: array<struct<struct_col_6:struct<int_field_5:int>>>>)");

    executeQuery("DROP TABLE IF EXISTS basedecimal");
    executeQuery("CREATE TABLE IF NOT EXISTS basedecimal(decimal_col decimal(2,1))");
  }

  private static void initializeUdfs() {
    List<String> viewsToCreateLessThanHundred = Arrays.asList("foo_dali_udf", "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator", "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default", viewsToCreateLessThanHundred, "LessThanHundred",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF1");

    List<String> viewsToCreateGreaterThanHundred = Arrays.asList("foo_dali_udf2", "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator", "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default", viewsToCreateGreaterThanHundred, "GreaterThanHundred",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF2");

    List<String> viewsToCreateFuncSquare = Arrays.asList("foo_dali_udf3", "foo_dali_multiple_udfs",
        "foo_dali_udf_with_operator", "foo_dali_udf_nullability");
    executeCreateFunctionQuery("default", viewsToCreateFuncSquare, "FuncSquare",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDF3");

    executeCreateFunctionQuery("default", Collections.singletonList("foo_lateral_udtf"), "CountOfRow",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDTF");

    executeCreateFunctionQuery("default", Collections.singletonList("foo_udf_return_struct"), "FuncIsEven",
        "com.linkedin.coral.hive.hive2rel.CoralTestUDFReturnStruct");
  }

  private static void executeCreateTableQuery(String dbName, String tableName, String schema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    executeQuery(
        "CREATE EXTERNAL TABLE " + tableName + " " + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' "
            + "STORED AS " + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
            + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' " + "TBLPROPERTIES ('"
            + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static void executeCreateTableWithPartitionQuery(String dbName, String tableName, String schema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    executeQuery("CREATE EXTERNAL TABLE " + tableName + " " + "PARTITIONED BY (datepartition string) "
        + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' " + "STORED AS "
        + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
        + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' " + "TBLPROPERTIES ('"
        + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static void executeCreateTableWithPartitionFieldSchemaQuery(String dbName, String tableName,
      String fieldSchema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    // Format is not specified as Avro because Avro 1.10 doesn't allow creating tables with field schema only.
    // Without specifying the format, inputFormat is "org.apache.hadoop.mapred.TextInputFormat"
    // and outputFormat is "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat".
    executeQuery(
        "CREATE EXTERNAL TABLE " + tableName + " (" + fieldSchema + ") " + "PARTITIONED BY (datepartition string)");
  }

  private static void executeCreateFunctionQuery(String dbName, List<String> viewNames, String functionName,
      String functionClass) {
    for (String viewName : viewNames) {
      String expandedFunctionName = dbName + "_" + viewName + "_" + functionName;
      executeQuery("DROP FUNCTION IF EXISTS " + dbName + "." + expandedFunctionName);
      executeQuery("CREATE FUNCTION " + expandedFunctionName + " as '" + functionClass + "'");
    }
  }

  private static void executeQuery(String sql) {
    while (true) {
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  public static HiveConf getHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_SCHEMA_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/schema/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-schema");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/schema");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/schema");
    return hiveConf;
  }
}
