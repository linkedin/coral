/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.presto.rel2presto;

import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class HiveToPrestoConverterTest {

  RelToPrestoConverter relToPrestoConverter;

  @BeforeTest
  public void beforeClass() throws HiveException, MetaException {
    TestUtils.initializeViews();
    relToPrestoConverter = new RelToPrestoConverter();
  }

  @Test(dataProvider = "viewTestCases")
  public void testViews(String database, String view, String expectedSql) {
    RelNode relNode = TestUtils.convertView(database, view);
    String expandedSql = relToPrestoConverter.convert(relNode);
    assertEquals(expandedSql, expectedSql);
  }

  @DataProvider(name = "viewTestCases")
  public Object[][] viewTestCasesProvider() {
    return new Object[][] { { "test", "fuzzy_union_view", "SELECT \"a\", \"b\"\nFROM ("
        + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
        + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t1\"" },

        { "test", "fuzzy_union_view_with_more_than_two_tables", "SELECT \"a\", \"b\"\nFROM (SELECT *\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\")\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t7\"" },

        { "test", "fuzzy_union_view_with_alias", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablea\") AS \"t11\"" },

        { "test", "fuzzy_union_view_single_branch_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tableb\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablec\") AS \"t15\"" },

        { "test", "fuzzy_union_view_double_branch_evolved_same", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tabled\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablee\") AS \"t19\"" },

        { "test", "fuzzy_union_view_double_branch_evolved_different", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tableg\") AS \"t23\"" },

        { "test", "fuzzy_union_view_more_than_two_branches_evolved", "SELECT \"a\", \"b\"\nFROM (SELECT *\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\"\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tableg\")\nUNION ALL\n"
            + "SELECT \"a\", CAST(row(b.b1) as row(b1 varchar)) AS \"b\"\nFROM \"test\".\"tablef\") AS \"t29\"" },

        { "test", "fuzzy_union_view_map_with_struct_value_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", TRANSFORM_VALUES(b, (k, v) -> cast(row(v.b1) as row(b1 varchar))) AS \"b\"\nFROM \"test\".\"tableh\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablei\") AS \"t33\"" },

        { "test", "fuzzy_union_view_array_with_struct_value_evolved", "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", TRANSFORM(b, x -> cast(row(x.b1) as row(b1 varchar))) AS \"b\"\nFROM \"test\".\"tablej\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\nFROM \"test\".\"tablek\") AS \"t37\"" },

        { "test", "fuzzy_union_view_deeply_nested_struct_evolved", "" + "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1, cast(row(b.b2.b3, cast(row(b.b2.b4.b5) as row(b5 varchar))) as row(b3 varchar, b4 row(b5 varchar)))) as row(b1 varchar, b2 row(b3 varchar, b4 row(b5 varchar)))) AS \"b\"\nFROM \"test\".\"tablel\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\n" + "FROM \"test\".\"tablem\") AS \"t41\"" },

        { "test", "fuzzy_union_view_deeply_nested_complex_struct_evolved", "" + "SELECT \"a\", \"b\"\nFROM ("
            + "SELECT \"a\", CAST(row(b.b1, transform_values(b.m1, (k, v) -> cast(row(v.b1, transform(v.a1, x -> cast(row(x.b1) as row(b1 varchar)))) as row(b1 varchar, a1 array(row(b1 varchar)))))) as row(b1 varchar, m1 map(varchar, row(b1 varchar, a1 array(row(b1 varchar)))))) AS \"b\"\nFROM \"test\".\"tablen\"\nUNION ALL\n"
            + "SELECT \"a\", \"b\"\n" + "FROM \"test\".\"tableo\") AS \"t45\"" },

        { "test", "current_date_and_timestamp_view", "SELECT CURRENT_TIMESTAMP, TRIM(CAST(CURRENT_TIMESTAMP AS VARCHAR(65535))) AS \"ct\", CURRENT_DATE, CURRENT_DATE AS \"cd\", \"a\"\nFROM \"test\".\"tablea\"" },

        { "test", "get_json_object_view", "SELECT \"json_extract\"(\"b\".\"b1\", '$.name')\nFROM \"test\".\"tablea\"" } };
  }
}
