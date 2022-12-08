/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.HiveMetastoreClient;


public class ViewToAvroSchemaConverterTests {
  private HiveMetastoreClient hiveMetastoreClient;
  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.getHiveConf();
    hiveMetastoreClient = TestUtils.setup(conf);
    TestUtils.registerUdfs();
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_SCHEMA_TEST_DIR)));
  }

  @Test
  public void testBaseTable() {
    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "basecomplex");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("base-complex.avsc"));
  }

  @Test
  public void testSelectStar() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStar-expected.avsc"));
  }

  @Test
  public void testSchemaWithNullableFieldsAndDefaults() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplexnullablewithdefaults";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStarWithNullsAndDefaults.avsc"));
  }

  @Test
  public void testFilter() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, bc.Array_Col AS Array_View_Col "
        + "FROM basecomplex bc " + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testFilter-expected.avsc"));
  }

  @Test
  public void testSelectWithLiterals() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, 100 AS Additional_Int, 200, bc.Array_Col AS Array_View_Col "
            + "FROM basecomplex bc " + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectWithLiterals-expected.avsc"));
  }

  @Test
  public void testSelectWithMultipleLiterals() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT 200, 1, bc.Id AS Id_View_Col, 100 AS Additional_Int, 200, 1, 1, 1, bc.Array_Col "
            + "FROM basecomplex bc";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectWithMultipleLiterals-expected.avsc"));
  }

  @Test
  public void testAggregate() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id AS Id_View_Col, COUNT(bc.Map_Col), 1 + 1, 100 AS Additional_Int, bc.Struct_Col AS Struct_View_Col "
        + "FROM basecomplex bc " + "WHERE bc.Id > 0 AND bc.Map_Col IS NOT NULL AND bc.Struct_Col IS NOT NULL "
        + "GROUP BY bc.Id, bc.Struct_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testAggregate-expected.avsc"));
  }

  @Test
  public void testAggregateRename() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, COUNT(*) AS Count_Col "
        + "FROM basecomplex bc " + "WHERE bc.Id > 0 " + "GROUP BY bc.Id";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testAggregateRename-expected.avsc"));
  }

  @Test
  public void testMultipleAggregates() {
    String viewSql = "CREATE VIEW v AS SELECT MAX(bc.Id) AS Max_Id_Col, MIN(bc.Id) AS Min_Id_Col, "
        + "AVG(bc.Id) AS Avg_Id_Col, SUM(bc.Id) AS Sum_Id_Col FROM basecomplex bc GROUP BY bc.Array_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testMultipleAggregates-expected.avsc"));
  }

  @Test
  public void testRexCallAggregate() {
    String viewSql = "CREATE VIEW v AS " + "SELECT 22*COUNT(bc.Id) AS Temp " + "FROM basecomplex bc";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testRexCallAggregate-expected.avsc"));
  }

  @Test
  public void testRexCallAggregateMultiple() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id, 22*COUNT(bc.Array_Col) AS Array_Count, 33*COUNT(bc.Map_Col) AS Map_Count, COUNT(bc.Array_Col), COUNT(bc.Map_Col) "
        + "FROM basecomplex bc " + "WHERE bc.Id > 0 " + "GROUP BY bc.Id";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testRexCallAggregateMultiple-expected.avsc"));
  }

  @Test
  public void testSubQueryFrom() {
    String viewSql = "CREATE VIEW v AS " + "SELECT Id, Map_Col " + "FROM " + "( " + "SELECT Id, Map_Col "
        + "FROM basecomplex " + "WHERE Id > 0 AND Struct_Col IS NOT NULL " + ") t";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSubQueryFrom-expected.avsc"));
  }

  @Test
  public void testSelectEnum() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Enum_Top_Col " + "FROM baseenum bc";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectEnum-expected.avsc"));
  }

  @Test
  public void testUnion() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT b1.Id AS Id_View_Col, b1.Struct_Col AS Struct_View_Col " + "FROM basecomplex b1 "
            + "UNION ALL " + "SELECT b2.Id AS Id_View_Col, b2.Struct_Col AS Struct_View_Col " + "FROM basecomplex b2 "
            + "UNION ALL " + "SELECT b3.Id AS Id_View_Col, b3.Struct_Col AS Struct_View_Col " + "FROM basecomplex b3";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUnion-expected.avsc"));
  }

  @Test
  public void testUdfLessThanHundred() {
    String viewSql = "CREATE VIEW foo_dali_udf "
        + "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF1', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0') " + "AS "
        + "SELECT Id AS Id_View_Col, default_foo_dali_udf_LessThanHundred(Id) AS Id_View_LessThanHundred_Col "
        + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "foo_dali_udf", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_udf");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUdfLessThanHundred-expected.avsc"));
  }

  @Test
  public void testUdfGreaterThanHundred() {
    String viewSql = "CREATE VIEW foo_dali_udf2 "
        + "tblproperties('functions' = 'GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0') " + "AS "
        + "SELECT Id AS Id_Viewc_Col, default_foo_dali_udf2_GreaterThanHundred(Id) AS Id_View_GreaterThanHundred_Col "
        + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "foo_dali_udf2", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_udf2");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUdfGreaterThanHundred-expected.avsc"));
  }

  @Test
  public void testUdfSquare() {
    String viewSql = "CREATE VIEW foo_dali_udf3 "
        + "tblproperties('functions' = 'FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUDF3', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0') " + "AS "
        + "SELECT Id AS Id_Viewc_Col, default_foo_dali_udf3_FuncSquare(Id) AS Id_View_FuncSquare_Col "
        + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "foo_dali_udf3", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_udf3");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUdfSquare-expected.avsc"));
  }

  @Test
  public void testMultipleUdfs() {
    String viewSql = "CREATE VIEW foo_dali_multiple_udfs "
        + "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF1 GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2 FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUDF3', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0') "
        + "AS " + "SELECT Id AS Id_Viewc_Col, "
        + "default_foo_dali_multiple_udfs_LessThanHundred(Id) AS Id_View_LessThanHundred_Col,"
        + "default_foo_dali_multiple_udfs_GreaterThanHundred(Id) AS Id_View_GreaterThanHundred_Col, "
        + "default_foo_dali_multiple_udfs_FuncSquare(Id) AS Id_View_FuncSquare_Col " + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "foo_dali_multiple_udfs", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_multiple_udfs");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testMultipleUdfs-expected.avsc"));
  }

  @Test
  public void testUdfWithOperator() {
    String viewSql = "CREATE VIEW foo_dali_udf_with_operator "
        + "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF1 GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2 FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUDF3', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0') "
        + "AS " + "SELECT Id AS Id_Viewc_Col, " + "default_foo_dali_udf_with_operator_LessThanHundred(Id),"
        + "default_foo_dali_udf_with_operator_GreaterThanHundred(Id), "
        + "default_foo_dali_udf_with_operator_FuncSquare(Id), " + "1 + 1, " + "Id + 1, "
        + "default_foo_dali_udf_with_operator_FuncSquare(Id) + 1, "
        + "default_foo_dali_udf_with_operator_FuncSquare(Id + 1) + 1, "
        + "Id + default_foo_dali_udf_with_operator_FuncSquare(Id) " + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "foo_dali_udf_with_operator", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_udf_with_operator");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUdfWithOperator-expected.avsc"));
  }

  @Test
  public void testLateralView() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, t.Array_Lateral_View_Col "
        + "FROM basecomplex bc " + "LATERAL VIEW explode(bc.Array_Col) t as Array_Lateral_View_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testLateralView-expected.avsc"));
  }

  @Test
  public void testLateralViewOuter() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, t.Array_Lateral_View_Col "
        + "FROM basecomplex bc " + "LATERAL VIEW OUTER explode(bc.Array_Col) t as Array_Lateral_View_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testLateralViewOuter-expected.avsc"));
  }

  @Test
  public void testMultipleLateralView() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, t1.Array_Lateral_View_Col_1, t2.Array_Lateral_View_Col_2 "
            + "FROM basecomplex bc " + "LATERAL VIEW explode(bc.Array_Col) t1 as Array_Lateral_View_Col_1 "
            + "LATERAL VIEW explode(bc.Array_Col) t2 as Array_Lateral_View_Col_2";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testMultipleLateralView-expected.avsc"));
  }

  @Test
  public void testMultipleLateralViewDifferentArrayType() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bl.Id AS Id_View_Col, t1.Array_Lateral_View_String_Col, t2.Array_Lateral_View_Double_Col "
        + "FROM baselateralview bl " + "LATERAL VIEW explode(bl.Array_Col_String) t1 as Array_Lateral_View_String_Col "
        + "LATERAL VIEW explode(bl.Array_Col_Double) t2 as Array_Lateral_View_Double_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testMultipleLateralViewDifferentArrayType-expected.avsc"));
  }

  @Test
  public void testLateralUDTF() {
    String viewSql = "CREATE VIEW foo_lateral_udtf "
        + "tblproperties('functions' = 'CountOfRow:com.linkedin.coral.hive.hive2rel.CoralTestUDTF') " + "AS "
        + "SELECT bc.Id AS Id_View_Col, t.col1 as Col1 " + "FROM basecomplex bc "
        + "LATERAL VIEW default_foo_lateral_udtf_CountOfRow(bc.Id) t";

    TestUtils.executeCreateViewQuery("default", "foo_lateral_udtf", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_lateral_udtf");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testLateralUDTF-expected.avsc"));
  }

  // The following tests for join are disabled because Avro 1.10 doesn't allow 2 schema fields share the same name.
  // We need to modify the logic of `RelToAvroSchemaConverter.visit(LogicalJoin)` before enabling these tests
  @Test(enabled = false)
  public void testInnerJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test(enabled = false)
  public void testLeftOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "LEFT OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test(enabled = false)
  public void testRightOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "RIGHT OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test(enabled = false)
  public void testFullOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "FULL OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));
  }

  @Test(enabled = false)
  public void testSelfJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc1.id, bc1.struct_col, bc2.array_col " + "FROM basecomplex bc1 "
        + "JOIN basecomplex bc2 ON bc1.id = bc2.id " + "WHERE bc1.id > 0 AND bc1.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelfJoin-expected.avsc"));
  }

  @Test(enabled = false)
  public void testFullOuterJoinWithDoc() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplexwithdoc bc "
            + "FULL OUTER JOIN baseenumwithdoc be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testJoin-expected-with-doc.avsc"));
  }

  // TODO: handle complex type (Array[Struct] in lateral view)
  @Test(enabled = false)
  public void testLateralViewArrayWithComplexType() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bl.Id AS Id_View_Col, bl.Array_Col_Struct AS Array_Struct_View_Col, "
        + "t.Array_Col_Struct_Flatten " + "FROM baselateralview bl "
        + "LATERAL VIEW explode(bl.Array_Col_Struct) t as Array_Col_Struct_Flatten";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testLateralViewArrayWithComplexType-expected.avsc"));
  }

  @Test
  public void testLateralViewMap() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bl.Id AS Id_View_Col, t.col1, t.col2 " + "FROM baselateralview bl "
        + "LATERAL VIEW explode(bl.Map_Col_String) t as col1, col2";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testLateralViewMap-expected.avsc"));
  }

  @Test
  public void testNullabilityBasic() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bn.Id, " + "bn.Array_Col, " + "bn.Map_Col, " + "bn.Int_Field_1, "
        + "bn.Int_Field_2, " + "bn.Double_Field_1, " + "bn.Double_Field_2, " + "bn.Bool_Field_1, " + "bn.Bool_Field_2, "
        + "bn.String_Field_1, " + "bn.String_Field_2, " + "bn.Struct_Col " + "FROM basenullability bn";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testNullabilityBasic-expected.avsc"));
  }

  @Test
  public void testNullabilitySqlOperator() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bn.Id + bn.Int_Field_1, " + "bn.Id + bn.Int_Field_2, "
        + "bn.Int_Field_1 + bn.Int_Field_1, " + "bn.Int_Field_1 + 1, " + "bn.Int_Field_2 + 1, "
        + "bn.Double_Field_1 + bn.Double_Field_1, " + "bn.Double_Field_2 + bn.Double_Field_2, "
        + "bn.Double_Field_1 + bn.Double_Field_2, " + "NOT bn.Bool_Field_1, " + "NOT bn.Bool_Field_2 "
        + "FROM basenullability bn";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testNullabilitySqlOperator-expected.avsc"));
  }

  @Test
  public void testNullabilityUdf() {
    String viewSql = "CREATE VIEW foo_dali_udf_nullability "
        + "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF1 GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2 FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUDF3', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0') "
        + "AS " + "SELECT default_foo_dali_udf_nullability_LessThanHundred(Int_Field_1), "
        + "default_foo_dali_udf_nullability_LessThanHundred(Int_Field_2), "
        + "default_foo_dali_udf_nullability_GreaterThanHundred(Int_Field_1), "
        + "default_foo_dali_udf_nullability_GreaterThanHundred(Int_Field_2), "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_1), "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_2), "
        + "default_foo_dali_udf_nullability_LessThanHundred(Int_Field_1) AND default_foo_dali_udf_nullability_LessThanHundred(Int_Field_2), "
        + "default_foo_dali_udf_nullability_GreaterThanHundred(Int_Field_1) OR default_foo_dali_udf_nullability_GreaterThanHundred(Int_Field_2), "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_1) + Id, "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_2) + Id, " + "CONCAT(String_Field_1,String_Field_1), "
        + "CONCAT(String_Field_2,String_Field_2), " + "CONCAT(String_Field_1,String_Field_2), "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_1) + 1, "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_2 + 1) + 1, "
        + "default_foo_dali_udf_nullability_FuncSquare(default_foo_dali_udf_nullability_FuncSquare(default_foo_dali_udf_nullability_FuncSquare(Int_Field_2))) + bn.Id, "
        + "default_foo_dali_udf_nullability_FuncSquare(default_foo_dali_udf_nullability_FuncSquare(default_foo_dali_udf_nullability_FuncSquare(Int_Field_2)) + 1) + bn.Id, "
        + "default_foo_dali_udf_nullability_FuncSquare(Int_Field_2 + Id) + Id, "
        + "Id + default_foo_dali_udf_nullability_FuncSquare(Id) " + "FROM basenullability bn";

    TestUtils.executeCreateViewQuery("default", "foo_dali_udf_nullability", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_udf_nullability");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testNullabilityUdf-expected.avsc"));
  }

  @Test
  public void testNullabliltyExtractUnionUDF() {
    String sql = "select extract_union(unionCol) as c1 from basenestedunion";
    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);

    Schema actual = viewToAvroSchemaConverter.toAvroSchema(sql);

    Assert.assertEquals(actual.toString(true), TestUtils.loadSchema("testNullabilityExtractUnionUDF-expected.avsc"));
  }

  @Test(enabled = false)
  public void testRenameToLowercase() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS id, bc.Array_Col AS array_col " + "FROM basecomplex bc "
        + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testRenameToLowercase-expected.avsc"));
  }

  @Test
  public void testSelectStarWithPartition() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecasepreservation";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStarWithPartition.avsc"));
  }

  @Test
  public void testSelectPartitionColumn() {
    String viewSql = "CREATE VIEW v AS SELECT KEY, VALUE, datepartition FROM basecasepreservation";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStarWithPartition.avsc"));
  }

  @Test
  public void testUnionSelectStarFromPartitionTable() {
    String viewSql = "CREATE VIEW v AS " + "SELECT * FROM basecasepreservation " + "UNION ALL "
        + "SELECT * FROM basecasepreservation";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStarWithPartition.avsc"));
  }

  @Test
  public void testUnionPreserveNamespace() {
    String viewSql = "CREATE VIEW v AS " + "SELECT * FROM basecasepreservation " + "UNION ALL "
        + "SELECT * FROM basecasepreservation";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", true);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUnionPreserveNamespace.avsc"));
  }

  @Test
  public void testUnionNotPreserveNamespace() {
    String viewSql = "CREATE VIEW v AS " + "SELECT * FROM basecasepreservation " + "UNION ALL "
        + "SELECT * FROM basecasepreservation";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectStarWithPartition.avsc"));
  }

  @Test
  public void testBaseTableWithFieldSchema() {
    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "basecomplexfieldschema");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testBaseTableWithFieldSchema-expected.avsc"));
  }

  @Test
  public void testSelectStarFromTableWithFieldSchema() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplexfieldschema";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectStarFromTableWithFieldSchema-expected.avsc"));
  }

  @Test
  public void testBaseTableWithPartition() {
    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "basecasepreservation");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testBaseTableWithPartiton-expected.avsc"));
  }

  @Test
  public void testCompatibleUnion() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT * FROM basecomplex " + "UNION ALL " + "SELECT * FROM basecomplexunioncompatible";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testCompatibleUnion-expected.avsc"));
  }

  @Test
  public void testUnionForceLowercase() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT * FROM basecomplex " + "UNION ALL " + "SELECT * FROM basecomplexlowercase";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false, true);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUnionForceLowercase-expected.avsc"));
  }

  @Test
  public void testSelectStarFromNestComplex() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basenestedcomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectStarFromNestComplex-expected.avsc"));
  }

  @Test
  public void testSelectNestedStructFieldFromNestComplex() {
    String viewSql =
        "CREATE VIEW v AS SELECT array_col[0].Int_Field Int_Field, map_col['x'].Int_Field2 Int_Field2, struct_col.inner_struct_col.Int_Field3 Int_Field3 FROM basenestedcomplex";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectNestedStructFieldFromNestComplex-expected.avsc"));
  }

  @Test
  public void testSelectDeepNestStructFieldFromDeepNestComplex() {
    String viewSql = "CREATE VIEW v AS SELECT struct_col_1.struct_col_2.struct_col_3.int_field_1 Int_Field_1, "
        + "array_col_1[0].array_col_2[0].int_field_2 Int_Field_2, map_col_1['x'].map_col_2['y'].int_field_3 Int_Field_3, "
        + "struct_col_4.map_col_3['x'].struct_col_5.int_field_4 Int_Field_4, "
        + "struct_col_4.array_col_3[0].struct_col_6.int_field_5 Int_Field_5 FROM basedeepnestedcomplex";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectDeepNestedStructFieldFromDeepNestComplex-expected.avsc"));
  }

  @Test
  public void testProjectStructInnerField() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id AS Id_View_Col, Struct_Col.Bool_Field AS Struct_Inner_Bool_Col, Struct_Col.Int_Field AS Struct_Inner_Int_Col, Struct_Col.Bigint_Field AS Struct_Inner_Bigint_Col "
        + "FROM basecomplex bc " + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testProjectStructInnerField-expected.avsc"));
  }

  @Test
  public void testSubQueryWhere() {
    // TODO: implement this test
  }

  @Test
  public void testSelectNullUnionNullField() {
    String viewSql =
        "CREATE VIEW v AS SELECT NULL Null_Field FROM basecomplex UNION ALL SELECT Null_Field FROM basenulltypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectNullUnionNullField-expected.avsc"));
  }

  @Test
  public void testNullUnionNotNullableField() {
    String viewSql = "CREATE VIEW v AS SELECT NULL Field FROM basecomplex UNION ALL SELECT Id Field FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testNullUnionNotNullableField-expected.avsc"));
  }

  @Test
  public void testNotNullableFieldUnionNull() {
    String viewSql = "CREATE VIEW v AS SELECT Id Field FROM basecomplex UNION ALL SELECT NULL Field FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testNotNullableFieldUnionNull-expected.avsc"));
  }

  @Test
  public void testNullUnionNullableField() {
    String viewSql =
        "CREATE VIEW v AS SELECT NULL Field FROM basenulltypefield UNION ALL SELECT Nullable_Field Field FROM basenulltypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testNullUnionNullableField-expected.avsc"));
  }

  @Test
  public void testNullableFieldUnionNull() {
    String viewSql =
        "CREATE VIEW v AS SELECT Nullable_Field Field FROM basenulltypefield UNION ALL SELECT NULL Field FROM basenulltypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testNullableFieldUnionNull-expected.avsc"));
  }

  @Test
  public void testNotNullableFieldUnionNullableField() {
    String viewSql =
        "CREATE VIEW v AS SELECT Not_Nullable_Field Field FROM basenulltypefield UNION ALL SELECT Nullable_Field Field FROM basenulltypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testNotNullableFieldUnionNullableField-expected.avsc"));
  }

  @Test
  public void testNullableFieldUnionNotNullableField() {
    String viewSql =
        "CREATE VIEW v AS SELECT Nullable_Field Field FROM basenulltypefield UNION ALL SELECT Not_Nullable_Field Field FROM basenulltypefield";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false);

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testNullableFieldUnionNotNullableField-expected.avsc"));
  }

  /*
   * TODO : Discuss how to deal with expressions.
   */
  @Test
  public void testSelectWithLiteralsWithDoc() {
    String viewSql =
        "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, 100 AS Additional_Int, 200, bc.Array_Col AS Array_View_Col "
            + "FROM basecomplexwithdoc bc " + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testSelectWithLiterals-expected-with-doc.avsc"));
  }

  /*
   * TODO: enhance documentation for aggregate cols.
   */
  @Test
  public void testAggregateRenameWithDoc() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.Id AS Id_View_Col, COUNT(*) AS Count_Col "
        + "FROM basecomplexwithdoc bc " + "WHERE bc.Id > 0 " + "GROUP BY bc.Id";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testAggregateRename-expected-with-doc.avsc"));
  }

  /*
   * TODO: enhance handling of rex calls and modify this test case.
   */
  @Test
  public void testRexCallAggregateWithDoc() {
    String viewSql = "CREATE VIEW v AS " + "SELECT 22*COUNT(bc.Id) AS Temp " + "FROM basecomplexwithdoc bc";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testRexCallAggregate-expected-with-doc.avsc"));
  }

  /*
   * TODO: Documentation of UDFs should be handled as part of enhancements of documentation for RexCall
   */
  @Test
  public void testMultipleUdfsWithDoc() {
    String viewSql = "CREATE VIEW foo_dali_multiple_udfs "
        + "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF1 GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2 FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUDF3', "
        + "              'dependencies' = 'ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0 ivy://com.linkedin:udf:1.0') "
        + "AS " + "SELECT Id AS Id_Viewc_Col, "
        + "default_foo_dali_multiple_udfs_LessThanHundred(Id) AS Id_View_LessThanHundred_Col,"
        + "default_foo_dali_multiple_udfs_GreaterThanHundred(Id) AS Id_View_GreaterThanHundred_Col, "
        + "default_foo_dali_multiple_udfs_FuncSquare(Id) AS Id_View_FuncSquare_Col " + "FROM basecomplexwithdoc";

    TestUtils.executeCreateViewQuery("default", "foo_dali_multiple_udfs", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_dali_multiple_udfs");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testMultipleUdfs-expected-with-doc.avsc"));
  }

  /*
   * TODO: test case need to be enhanced for when a lateral is used to generate a column.
   */
  @Test
  public void testMultipleLateralViewDifferentArrayTypeWithDoc() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bl.Id AS Id_View_Col, t1.Array_Lateral_View_String_Col, t2.Array_Lateral_View_Double_Col "
        + "FROM baselateralviewwithdoc bl "
        + "LATERAL VIEW explode(bl.Array_Col_String) t1 as Array_Lateral_View_String_Col "
        + "LATERAL VIEW explode(bl.Array_Col_Double) t2 as Array_Lateral_View_Double_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("docTestResources/testMultipleLateralViewDifferentArrayType-expected-with-doc.avsc"));
  }

  @Test
  public void testDecimalType() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basedecimal";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testDecimalType-expected.avsc"));
  }

  @Test
  public void testEnumUnionEnum() {
    String viewSql = "CREATE VIEW v AS SELECT b1.Enum_Second_Col AS c1 FROM baseenum b1"
        + " UNION ALL SELECT b2.Enum_Third_Col AS c1 FROM baseenum b2";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testEnumUnionEnum-expected.avsc"));
  }

  @Test
  public void testEnumUnionString() {
    String viewSql = "CREATE VIEW v AS SELECT b1.Enum_Top_Col AS c1 FROM baseenum b1"
        + " UNION ALL SELECT b2.Struct_Col.String_Field AS c1 FROM basecomplex b2";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testEnumUnionString-expected.avsc"));
  }

  @Test
  public void testComplexUnionType() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplexuniontype";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testComplexUnionType-expected.avsc"));
  }

  @Test
  public void testUnionFixedAndBytes() {
    String viewSql = "CREATE VIEW v AS SELECT b1.Fixed_field AS c1 FROM basefixed b1" + " UNION ALL "
        + "SELECT b2.Bytes_field AS c1 FROM basebytes b2";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testUnionFixedAndBytes-expected.avsc"));
  }

  @Test
  public void testProjectUdfReturnedStruct() {
    String viewSql = "CREATE VIEW foo_udf_return_struct "
        + "tblproperties('functions' = 'FuncIsEven:com.linkedin.coral.hive.hive2rel.CoralTestUDFReturnStruct') " + "AS "
        + "SELECT bc.Id AS Id_View_Col, default_foo_udf_return_struct_FuncIsEven(bc.Id) AS Id_View_FuncIsEven_Col,  "
        + "default_foo_udf_return_struct_FuncIsEven(bc.Id).isEven AS View_IsEven_Col, "
        + "default_foo_udf_return_struct_FuncIsEven(bc.Id).number AS View_Number_Col " + "FROM basecomplex bc";

    TestUtils.executeCreateViewQuery("default", "foo_udf_return_struct", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "foo_udf_return_struct");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testProjectUdfReturnedStruct-expected.avsc"));
  }

  @Test
  public void testLowercaseSchema() {
    String viewSql = "CREATE VIEW v AS SELECT id as Id FROM basecomplex";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v", false, true);

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testLowercaseSchema-expected.avsc"));
  }

  @Test
  public void testCaseCallWithNullBranchAndComplexDataTypeBranch() {
    String viewSql =
        "CREATE VIEW v AS SELECT CASE WHEN TRUE THEN NULL ELSE split(struct_col.string_field, ' ') END AS col1 FROM basecomplex";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testCaseCallWithNullBranchAndComplexDataTypeBranch-expected.avsc"));
  }

  @Test
  public void testUnionIntAndLongPromoteToLong() {
    String viewSql =
        "CREATE VIEW v AS SELECT t1.int_col AS f1 FROM baseprimitive t1 UNION ALL SELECT t2.long_col AS f1 FROM baseprimitive t2";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testUnionIntAndLongPromoteToLong-expected.avsc"));
  }

  @Test
  public void testUnionIntAndDoublePromoteToDouble() {
    String viewSql =
        "CREATE VIEW v AS SELECT t1.int_col AS f1 FROM baseprimitive t1 UNION ALL SELECT t2.double_col AS f1 FROM baseprimitive t2";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testUnionIntAndDoublePromoteToDouble-expected.avsc"));
  }

  @Test
  public void testUnionFloatAndDoublePromoteToDouble() {
    String viewSql =
        "CREATE VIEW v AS SELECT t1.double_col AS f1 FROM baseprimitive t1 UNION ALL SELECT t2.float_col AS f1 FROM baseprimitive t2";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testUnionFloatAndDoublePromoteToDouble-expected.avsc"));
  }

  // TODO: add more unit tests
}
