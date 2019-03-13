package com.linkedin.coral.schema.avro;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import org.apache.avro.Schema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ViewToAvroSchemaConverterTests {
  private HiveMetastoreClient hiveMetastoreClient;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException {
    hiveMetastoreClient = TestUtils.setup();
  }

  @Test
  public void testBaseTable() {
    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "basecomplex");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("base-complex.avsc"));
  }

  @Test
  public void testSelectStar() {
    String viewSql = "CREATE VIEW v AS SELECT * FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectStar-expected.avsc"));
  }

  @Test
  public void testFilter() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id AS Id_View_Col, bc.Array_Col AS Array_View_Col "
        + "FROM basecomplex bc "
        + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testFilter-expected.avsc"));
  }

  @Test
  public void testSelectWithLiterals() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id AS Id_View_Col, 100 AS Additional_Int, 200, bc.Array_Col AS Array_View_Col "
        + "FROM basecomplex bc "
        + "WHERE bc.Id > 0 AND bc.Struct_Col IS NOT NULL";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    // TODO: need to improve default name for literal later
    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectWithLiterals-expected.avsc"));
  }


  @Test
  public void testAggregate() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Id AS Id_View_Col, COUNT(bc.Map_Col), 100 AS Additional_Int, bc.Struct_Col AS Struct_View_Col "
        + "FROM basecomplex bc "
        + "WHERE bc.Id > 0 AND bc.Map_Col IS NOT NULL AND bc.Struct_Col IS NOT NULL "
        + "GROUP BY bc.Id, bc.Struct_Col";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    // TODO: need to improve default name for aggregation later
    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testAggregate-expected.avsc"));
  }

  @Test
  public void testSubQueryFrom() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT Id, Map_Col "
        + "FROM "
        + "( "
        + "SELECT Id, Map_Col "
        + "FROM basecomplex "
        + "WHERE Id > 0 AND Struct_Col IS NOT NULL "
        + ") t";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSubQueryFrom-expected.avsc"));
  }

  @Test
  public void testSelectEnum() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT bc.Enum_Top_Col "
        + "FROM baseenum bc";
    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testSelectEnum-expected.avsc"));
  }

  @Test
  public void testSelectSameLiterals() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT 1, 1 "
        + "FROM basecomplex";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    // TODO: compare with expected schema
  }

  @Test
  public void testUnion() {
    String viewSql = "CREATE VIEW v AS "
        + "SELECT b1.Id AS Id_View_Col, b1.Struct_Col AS Struct_View_Col "
        + "FROM basecomplex b1 "
        + "UNION ALL "
        + "SELECT b2.Id AS Id_View_Col, b2.Struct_Col AS Struct_View_Col "
        + "FROM basecomplex b2 "
        + "UNION ALL "
        + "SELECT b3.Id AS Id_View_Col, b3.Struct_Col AS Struct_View_Col "
        + "FROM basecomplex b3";

    TestUtils.executeCreateViewQuery("default", "v", viewSql);

    ViewToAvroSchemaConverter viewToAvroSchemaConverter = ViewToAvroSchemaConverter.create(hiveMetastoreClient);
    Schema actualSchema = viewToAvroSchemaConverter.toAvroSchema("default", "v");

    Assert.assertEquals(actualSchema.toString(true),
        TestUtils.loadSchema("testUnion-expected.avsc"));
  }

  @Test
  public void testUdf() {
    // TODO: implement this test

  }

  @Test
  public void testLateralView() {
    // TODO: implement this test
  }

  @Test
  public void testSubQueryWhere() {
    // TODO: implement this test
  }

  @Test
  public void testJoin() {
    // TODO: implement this test
  }

  // TODO: add more unit tests
}
