package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
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

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`\n"
        + "UNION\n"
        + "SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`"
        + ") AS `_u1`";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithMultipleTables() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_more_than_two_tables";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`\n"
        + "UNION\n"
        + "SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`\n"
        + "UNION\n"
        + "SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`"
        + ") AS `_u1`";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testNoSchemaEvolutionWithAlias() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_with_alias";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `viewfirst`.`a`, `viewfirst`.`b`\n"
        + "FROM (SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`) AS `viewFirst`\n"
        + "UNION\n"
        + "SELECT `viewsecond`.`a`, `viewsecond`.`b`\n"
        + "FROM (SELECT `tablea`.`a`, `tablea`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableA`) AS `viewSecond`"
        + ") AS `_u1`";

    getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testSingleBranchSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_single_branch_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `tableb`.`a`, `tableb`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableB`\n"
        + "UNION\n"
        + "SELECT `union_view_single_branch_evolved`.`a`, `fuzzy_union`.`union_view_single_branch_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablec`.`a`, `tablec`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableC`) AS `union_view_single_branch_evolved`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the genric project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testDoubleBranchSameSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_same";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_double_branch_evolved_same`.`a`, `fuzzy_union`.`union_view_double_branch_evolved_same`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tabled`.`a`, `tabled`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableD`) AS `union_view_double_branch_evolved_same`\n"
        + "UNION\n"
        + "SELECT `union_view_double_branch_evolved_same`.`a`, `fuzzy_union`.`union_view_double_branch_evolved_same`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablee`.`a`, `tablee`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableE`) AS `union_view_double_branch_evolved_same`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testDoubleBranchDifferentSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_double_branch_evolved_different";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_double_branch_evolved_different`.`a`, `fuzzy_union`.`union_view_double_branch_evolved_different`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablef`.`a`, `tablef`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableF`) AS `union_view_double_branch_evolved_different`\n"
        + "UNION\n"
        + "SELECT `union_view_double_branch_evolved_different`.`a`, `fuzzy_union`.`union_view_double_branch_evolved_different`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tableg`.`a`, `tableg`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableG`) AS `union_view_double_branch_evolved_different`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testMoreThanTwoBranchesSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_more_than_two_branches_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_more_than_two_branches_evolved`.`a`, `fuzzy_union`.`union_view_more_than_two_branches_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablef`.`a`, `tablef`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableF`) AS `union_view_more_than_two_branches_evolved`\n"
        + "UNION\n"
        + "SELECT `union_view_more_than_two_branches_evolved`.`a`, `fuzzy_union`.`union_view_more_than_two_branches_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tableg`.`a`, `tableg`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableG`) AS `union_view_more_than_two_branches_evolved`\n"
        + "UNION\n"
        + "SELECT `union_view_more_than_two_branches_evolved`.`a`, `fuzzy_union`.`union_view_more_than_two_branches_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablef`.`a`, `tablef`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableF`) AS `union_view_more_than_two_branches_evolved`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testMapWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_map_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_map_with_struct_value_evolved`.`a`, `fuzzy_union`.`union_view_map_with_struct_value_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tableh`.`a`, `tableh`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableH`) AS `union_view_map_with_struct_value_evolved`\n"
        + "UNION\n"
        + "SELECT `tablei`.`a`, `tablei`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableI`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testArrayWithStructValueSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_array_with_struct_value_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_array_with_struct_value_evolved`.`a`, `fuzzy_union`.`union_view_array_with_struct_value_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablej`.`a`, `tablej`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableJ`) AS `union_view_array_with_struct_value_evolved`\n"
        + "UNION\n"
        + "SELECT `tablek`.`a`, `tablek`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableK`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

  @Test
  public void testDeeplyNestedStructSchemaEvolution() throws TException {
    String database = "fuzzy_union";
    String view = "union_view_deeply_nested_struct_evolved";
    SqlNode node = getFuzzyUnionView(database, view);

    String expectedSql = "SELECT *\n"
        + "FROM ("
        + "SELECT `union_view_deeply_nested_struct_evolved`.`a`, `fuzzy_union`.`union_view_deeply_nested_struct_evolved`.`b`.`genericPROJECT`\n"
        + "FROM (SELECT `tablel`.`a`, `tablel`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableL`) AS `union_view_deeply_nested_struct_evolved`\n"
        + "UNION\n"
        + "SELECT `tablem`.`a`, `tablem`.`b`\n"
        + "FROM `hive`.`fuzzy_union`.`tableM`"
        + ") AS `_u1`";

    // TODO(ralam): Add validation when the generic project UDF is added.
    // getRelContextProvider().getHiveSqlValidator().validate(node);
    assertEquals(node.toString(), expectedSql);
  }

}
