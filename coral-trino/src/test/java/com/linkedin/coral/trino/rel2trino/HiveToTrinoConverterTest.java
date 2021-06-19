/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.calcite.rel.RelNode;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static org.assertj.core.api.Assertions.assertThat;


public class HiveToTrinoConverterTest {

  Path metastoreDbDirectory;

  @BeforeTest
  public void beforeClass() throws Exception {
    metastoreDbDirectory = Files.createTempFile("coral-trino", "metastore.db");
    Files.delete(metastoreDbDirectory); // it will be re-created
    TestUtils.initializeViews(metastoreDbDirectory);
  }

  @AfterClass(alwaysRun = true)
  public void afterClass() throws Exception {
    deleteRecursively(metastoreDbDirectory, ALLOW_INSECURE);
  }

  @Test(dataProvider = "viewTestCases")
  public void testViews(String database, String view, String expectedSql) {
    RelNode relNode = TestUtils.convertView(database, view);
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertThat(expandedSql).isEqualTo(expectedSql);
  }

  @DataProvider(name = "viewTestCases")
  public Object[][] viewTestCasesProvider() {
    return new Object[][] {

        { "test", "fuzzy_union_view", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t1\"" },

        { "test", "fuzzy_union_view_with_more_than_two_tables", "SELECT \"a\", \"b\"\nFROM (SELECT *\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\")\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t3\"" },

        { "test", "fuzzy_union_view_with_alias", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t1\"" },

        { "test", "fuzzy_union_view_single_branch_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tableb\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablec\") AS \"t1\"" },

        { "test", "fuzzy_union_view_double_branch_evolved_same", "SELECT \"a\", \"b\"\n" + "FROM (SELECT \"a\", \"b\"\n"
            + "FROM \"test\".\"tabled\"\n" + "UNION ALL\n" + "SELECT \"a\", \"b\"\n"
            + "FROM \"test\".\"tablee\") AS \"t1\"" },

        { "test", "fuzzy_union_view_double_branch_evolved_different", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tableg\") AS \"t1\"" },

        { "test", "fuzzy_union_view_more_than_two_branches_evolved", "SELECT \"a\", \"b\"\nFROM (SELECT *\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tableg\")\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\") AS \"t3\"" },

        { "test", "fuzzy_union_view_map_with_struct_value_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", TRANSFORM_VALUES(b, (k, v) -> cast(row(v.b1) as row(b1 varchar))) AS \"b\"\nFROM \"test\".\"tableh\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablei\") AS \"t1\"" },

        { "test", "fuzzy_union_view_array_with_struct_value_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", TRANSFORM(b, x -> cast(row(x.b1) as row(b1 varchar))) AS \"b\"\nFROM \"test\".\"tablej\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablek\") AS \"t1\"" },

        { "test", "fuzzy_union_view_deeply_nested_struct_evolved", "" + "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1, cast(row(b.b2.b3, cast(row(b.b2.b4.b5) as row(b5 varchar))) as row(b3 varchar, b4 row(b5 varchar)))) as row(b1 varchar, b2 row(b3 varchar, b4 row(b5 varchar)))) AS \"b\"\nFROM \"test\".\"tablel\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\n" + "FROM \"test\".\"tablem\") AS \"t1\"" },

        { "test", "fuzzy_union_view_deeply_nested_complex_struct_evolved", "" + "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1, transform_values(b.m1, (k, v) -> cast(row(v.b1, transform(v.a1, x -> cast(row(x.b1) as row(b1 varchar)))) as row(b1 varchar, a1 array(row(b1 varchar)))))) as row(b1 varchar, m1 map(varchar, row(b1 varchar, a1 array(row(b1 varchar)))))) AS \"b\"\nFROM \"test\".\"tablen\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\n" + "FROM \"test\".\"tableo\") AS \"t1\"" },

        { "test", "union_view_same_schema_evolution_with_different_ordering", "" + "SELECT \"a\", \"b\"\n"
            + "FROM (SELECT \"a\", \"b\"\n" + "FROM \"test\".\"tablep\"\n" + "UNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b2, b.b1, b.b0) as row(b2 double, b1 varchar, b0 integer)) AS \"b\"\n"
            + "FROM \"test\".\"tableq\") AS \"t1\"" },

        { "test", "view_with_explode_string_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t1\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_string_array\" AS \"$cor0\"\n"
            + "CROSS JOIN LATERAL (SELECT \"c\"\nFROM UNNEST(\"$cor0\".\"b\") AS \"t0\" (\"c\")) AS \"t1\"" },

        { "test", "view_with_outer_explode_string_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t1\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_string_array\" AS \"$cor0\"\n"
            + "CROSS JOIN LATERAL (SELECT \"c\"\nFROM UNNEST(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", ARRAY[NULL])) AS \"t0\" (\"c\")) AS \"t1\"" },

        { "test", "view_with_explode_struct_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t1\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
            + "CROSS JOIN LATERAL (SELECT \"c\"\nFROM UNNEST(TRANSFORM(\"$cor0\".\"b\", x -> ROW(x))) AS \"t0\" (\"c\")) AS \"t1\"" },

        { "test", "view_with_outer_explode_struct_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t1\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
            + "CROSS JOIN LATERAL (SELECT \"c\"\nFROM UNNEST(TRANSFORM(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", ARRAY[NULL]), x -> ROW(x))) AS \"t0\" (\"c\")) AS \"t1\"" },

        { "test", "current_date_and_timestamp_view", "SELECT CURRENT_TIMESTAMP, TRIM(CAST(CURRENT_TIMESTAMP AS VARCHAR(65535))) AS \"ct\", CURRENT_DATE, CURRENT_DATE AS \"cd\", \"a\"\nFROM \"test\".\"tablea\"" },

        { "test", "lateral_view_json_tuple_view", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"d\" AS \"d\", \"t0\".\"e\" AS \"e\", \"t0\".\"f\" AS \"f\"\n"
            + "FROM \"test\".\"tablea\" AS \"$cor0\"\nCROSS JOIN LATERAL (SELECT "
            + "\"if\"(\"REGEXP_LIKE\"('trino', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'trino' || '\"]') AS VARCHAR(65535)), NULL) AS \"d\", "
            + "\"if\"(\"REGEXP_LIKE\"('always', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'always' || '\"]') AS VARCHAR(65535)), NULL) AS \"e\", "
            + "\"if\"(\"REGEXP_LIKE\"('rocks', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'rocks' || '\"]') AS VARCHAR(65535)), NULL) AS \"f\"\n"
            + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"t0\"" },

        { "test", "lateral_view_json_tuple_view_qualified", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"d\" AS \"d\", \"t0\".\"e\" AS \"e\", \"t0\".\"f\" AS \"f\"\n"
            + "FROM \"test\".\"tablea\" AS \"$cor0\"\nCROSS JOIN LATERAL (SELECT "
            + "\"if\"(\"REGEXP_LIKE\"('trino', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'trino' || '\"]') AS VARCHAR(65535)), NULL) AS \"d\", "
            + "\"if\"(\"REGEXP_LIKE\"('always', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'always' || '\"]') AS VARCHAR(65535)), NULL) AS \"e\", "
            + "\"if\"(\"REGEXP_LIKE\"('rocks', '^[^\\\"]*$'), CAST(\"json_extract\"(\"$cor0\".\"b\".\"b1\", '$[\"' || 'rocks' || '\"]') AS VARCHAR(65535)), NULL) AS \"f\"\n"
            + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"t0\"" },

        { "test", "get_json_object_view", "SELECT \"json_extract\"(\"b\".\"b1\", '$.name')\nFROM \"test\".\"tablea\"" },

        { "test", "pmod_view", "SELECT MOD(MOD(- 9, 4) + 4, 4)\nFROM \"test\".\"tablea\"" } };
  }
}
