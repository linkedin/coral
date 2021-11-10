/**
 * Copyright 2019-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;

import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.FuzzyUnionSqlRewriter;
import com.linkedin.coral.common.ToRelConverterTestUtils;

import static com.linkedin.coral.common.ToRelConverterTestUtils.*;
import static org.testng.Assert.*;


public class FuzzyUnionTest {

  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    ToRelConverterTestUtils.setup();
  }

  private SqlNode getFuzzyUnionView(String databaseName, String viewName) {
    SqlNode node = viewToSqlNode(databaseName, viewName);
    node.accept(new FuzzyUnionSqlRewriter(viewName, converter));
    return node;
  }

  @Test
  public void testNoSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithMultipleTables() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_more_than_two_tables";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM (SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n"
        + "UNION ALL\n" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablea\") AS \"t\"\n" + "UNION ALL\n"
        + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithAlias() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_alias";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testSingleBranchSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_single_branch_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tableb\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tablec\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchSameSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_same";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tabled\"\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablee\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchDifferentSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_different";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablef\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tableg\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMoreThanTwoBranchesSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_more_than_two_branches_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM (SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablef\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableg\") AS \"t\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tablef\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMapWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_map_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql =
        "" + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tableh\"\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablei\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testArrayWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_array_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql =
        "" + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tablej\"\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablek\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDeeplyNestedStructSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_deeply_nested_struct_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql =
        "" + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tablel\"\n"
            + "UNION ALL\n" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablem\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testSameSchemaEvolutionWithDifferentOrdering() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_same_schema_evolution_with_different_ordering";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "" + "SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tablen\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM \"hive\".\"fuzzy_union\".\"tableo\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testUnionViewWithBaseTableChange() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_base_table_change";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n" + "FROM (SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablep\"\n" + "UNION ALL\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableq\") AS \"t0\"\n" + "UNION ALL\n" + "SELECT *\n" + "FROM (SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tabler\"\n" + "UNION ALL\n" + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tables\") AS \"t\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testFuzzyUnionInFromClause() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_in_from_clause";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT \"a\"\n" + "FROM (SELECT *\n" + "FROM \"hive\".\"fuzzy_union\".\"tableb\"\n"
        + "UNION ALL\n" + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablec\") AS \"t0\"\n" + "UNION ALL\n" + "SELECT \"a\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableb\"";

    converter.getSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }
}
