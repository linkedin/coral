package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.thrift.TException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.linkedin.coral.hive.hive2rel.ToRelConverter.*;
import static org.testng.Assert.*;

public class FuzzyUnionTest {

  @BeforeClass
  public static void beforeClass() throws HiveException, MetaException, IOException {
    ToRelConverter.setup();
  }

  private SqlNode getFuzzyUnionView(String database, String view) throws TException {
    SqlNode node = viewToSqlNode(database, view);
    Table table = getMsc().getTable(database, view);
    node.accept(new FuzzyUnionSqlRewriter(table, getRelContextProvider()));
    return node;
  }

  @Test
  public void testNoSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithMultipleTables() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_more_than_two_tables";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT *\n"
        + "FROM (SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n"
        + "UNION\n" + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\")\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithAlias() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_alias";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablea\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testSingleBranchSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_single_branch_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableb\"\n"
        + "UNION\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablec\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchSameSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_same";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tabled\"\n"
        + "UNION\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablee\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDoubleBranchDifferentSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_different";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablef\"\n"
        + "UNION\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableg\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMoreThanTwoBranchesSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_more_than_two_branches_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT *\n"
        + "FROM (SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablef\"\n"
        + "UNION\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableg\")\n"
        + "UNION\n"
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablef\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testMapWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_map_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tableh\"\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablei\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testArrayWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_array_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablej\"\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablek\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }

  @Test
  public void testDeeplyNestedStructSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_deeply_nested_struct_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = ""
        + "SELECT \"a\", \"generic_project\"(\"b\", 'b') AS \"b\"\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablel\"\n"
        + "UNION\n"
        + "SELECT *\n"
        + "FROM \"hive\".\"fuzzy_union\".\"tablem\"";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    String expandedSql = nodeToStr(node);
    assertEquals(expandedSql, expectedSql);
  }
}
