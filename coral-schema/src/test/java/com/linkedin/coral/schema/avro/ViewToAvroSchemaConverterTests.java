/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import org.apache.avro.Schema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;


public class ViewToAvroSchemaConverterTests {
  private HiveMetastoreClient hiveMetastoreClient;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException {
    hiveMetastoreClient = TestUtils.setup();
    TestUtils.registerUdfs();
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
  public void testInnerJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test
  public void testLeftOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "LEFT OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test
  public void testRightOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "RIGHT OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));

  }

  @Test
  public void testFullOuterJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc.id, bc.struct_col, be.enum_top_col " + "FROM basecomplex bc "
        + "FULL OUTER JOIN baseenum be ON bc.id = be.id " + "WHERE bc.id > 0 AND bc.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testJoin-expected.avsc"));
  }

  @Test
  public void testSelfJoin() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bc1.id, bc1.struct_col, bc2.array_col " + "FROM basecomplex bc1 "
        + "JOIN basecomplex bc2 ON bc1.id = bc2.id " + "WHERE bc1.id > 0 AND bc1.struct_col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelfJoin-expected.avsc"));
  }

  // TODO: handle complex type (Array[Struct] in lateral view:  LIHADOOP-46695)
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

  // Currently coral-hive does not support lateral view on map type and
  // it throws IllegalStateException while converting it to RelNode
  @Test(expectedExceptions = IllegalStateException.class)
  public void testLateralViewMap() {
    String viewSql = "CREATE VIEW v AS " + "SELECT bl.Id AS Id_View_Col, t.Col1, t.Col2 " + "FROM baselateralview bl "
        + "LATERAL VIEW explode(bl.Map_Col_String) t as Col1, Col2";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    viewToAvroSchemaConverter.toAvroSchema("default", "v");
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
  public void testSelectStarFromNestComplex() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basenestedcomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectStarFromNestComplex-expected.avsc"));
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

    Assert.assertEquals(actualSchema.toString(true), TestUtils.loadSchema("testSelectNull-expected.avsc"));
  }

  // TODO: add more unit tests
}
