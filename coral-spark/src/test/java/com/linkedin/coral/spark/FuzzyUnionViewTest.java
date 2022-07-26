/**
 * Copyright 2019-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark;

import java.io.File;
import java.io.IOException;

import org.apache.calcite.rel.RelNode;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class FuzzyUnionViewTest {

  private HiveConf conf;

  @BeforeClass
  public void beforeClass() throws HiveException, MetaException, IOException {
    conf = TestUtils.loadResourceHiveConf();
    TestUtils.initializeViews(conf);
  }

  @AfterTest
  public void afterClass() throws IOException {
    FileUtils.deleteDirectory(new File(conf.get(TestUtils.CORAL_SPARK_TEST_DIR)));
  }

  @Test
  public void testNoSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql =
        "" + "SELECT *\n" + "FROM fuzzy_union.tablea\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablea";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithMultipleTables() {
    String database = "fuzzy_union";
    String view = "union_view_with_more_than_two_tables";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "" + "SELECT *\n" + "FROM (SELECT *\n" + "FROM fuzzy_union.tablea\n" + "UNION ALL\n"
        + "SELECT *\n" + "FROM fuzzy_union.tablea)\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablea";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithAlias() {
    String database = "fuzzy_union";
    String view = "union_view_with_alias";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql =
        "" + "SELECT *\n" + "FROM fuzzy_union.tablea\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablea";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testSingleBranchSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_single_branch_evolved";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "SELECT *\n" + "FROM fuzzy_union.tableb\n" + "UNION ALL\n"
        + "SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tablec";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchSameSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_same";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    // TODO(ralam): This unit test may be inconsistent with what we want.
    // If we want to have our schemas fixed to the top level schema, we would need to perform fuzzy-unions on all
    // queries.
    // This query currently does not have any generic_projections despite the top level view schema being inconsistent
    // because the schemas of the branches evolved the same way.
    // This unit test illustrates this behaviour; however, we can re-evaluate our desired behaviour later on.
    String expectedSql =
        "SELECT *\n" + "FROM fuzzy_union.tabled\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablee";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchDifferentSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_different";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tablef\n"
        + "UNION ALL\n" + "SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tableg";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMoreThanTwoBranchesSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_more_than_two_branches_evolved";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql =
        "SELECT *\n" + "FROM (SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tablef\n"
            + "UNION ALL\n" + "SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tableg)\n"
            + "UNION ALL\n" + "SELECT a, generic_project(b, 'struct<b1:string>') b\n" + "FROM fuzzy_union.tablef";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMapWithStructValueSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_map_with_struct_value_evolved";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "SELECT a, generic_project(b, 'map<string,struct<b1:string>>') b\n"
        + "FROM fuzzy_union.tableh\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablei";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testArrayWithStructValueSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_array_with_struct_value_evolved";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "SELECT a, generic_project(b, 'array<struct<b1:string>>') b\n" + "FROM fuzzy_union.tablej\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablek";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDeeplyNestedStructSchemaEvolution() {
    String database = "fuzzy_union";
    String view = "union_view_deeply_nested_struct_evolved";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql =
        "SELECT a, generic_project(b, 'struct<b1:string,b2:struct<b3:string,b4:struct<b5:string>>>') b\n"
            + "FROM fuzzy_union.tablel\n" + "UNION ALL\n" + "SELECT *\n" + "FROM fuzzy_union.tablem";

    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testSameSchemaEvolutionWithDifferentOrdering() {
    String database = "fuzzy_union";
    String view = "union_view_same_schema_evolution_with_different_ordering";
    RelNode relNode = TestUtils.toRelNode(database, view);
    CoralSpark coralSpark = CoralSpark.create(relNode);
    String expandedSql = coralSpark.getSparkSql();

    String expectedSql = "SELECT *\n" + "FROM fuzzy_union.tablen\n" + "UNION ALL\n"
        + "SELECT a, generic_project(b, 'struct<b2:double,b1:string,b0:int>') b\n" + "FROM fuzzy_union.tableo";

    assertEquals(expandedSql, expectedSql);
  }
}
