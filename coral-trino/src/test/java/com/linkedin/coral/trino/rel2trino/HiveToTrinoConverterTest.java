/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.collect.ImmutableMap;

import org.apache.calcite.rel.RelNode;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static com.linkedin.coral.trino.rel2trino.CoralTrinoConfigKeys.*;
import static com.linkedin.coral.trino.rel2trino.TestUtils.hiveToRelConverter;
import static org.testng.Assert.assertEquals;


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
    assertEquals(expandedSql, expectedSql);
  }

  @DataProvider(name = "viewTestCases")
  public Object[][] viewTestCasesProvider() {
    return new Object[][] {

        { "test", "t_dot_star_view", "SELECT \"tablea\".\"a\" AS \"a\", \"tablea\".\"b\" AS \"b\", \"tablea0\".\"b\" AS \"tbb\"\n"
            + "FROM \"test\".\"tablea\"\n"
            + "INNER JOIN \"test\".\"tablea\" AS \"tablea0\" ON \"tablea\".\"a\" = \"tablea0\".\"a\"" },

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

        { "test", "view_with_explode_string_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_string_array\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(\"$cor0\".\"b\") AS \"t0\" (\"c\")" },

        { "test", "view_with_outer_explode_string_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_string_array\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", ARRAY[NULL])) AS \"t0\" (\"c\")" },

        { "test", "view_with_explode_struct_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(TRANSFORM(\"$cor0\".\"b\", x -> ROW(x))) AS \"t0\" (\"c\")" },

        { "test", "view_with_outer_explode_struct_array", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
            + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(TRANSFORM(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", ARRAY[NULL]), x -> ROW(x))) AS \"t0\" (\"c\")" },

        { "test", "view_with_explode_map", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\", \"t0\".\"d\" AS \"d\"\n"
            + "FROM \"test\".\"table_with_map\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(\"$cor0\".\"b\") AS \"t0\" (\"c\", \"d\")" },

        { "test", "view_with_outer_explode_map", "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\", \"t0\".\"d\" AS \"d\"\n"
            + "FROM \"test\".\"table_with_map\" AS \"$cor0\"\n"
            + "CROSS JOIN UNNEST(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", MAP (ARRAY[NULL], ARRAY[NULL]))) AS \"t0\" (\"c\", \"d\")" },

        { "test", "map_array_view", "SELECT MAP (ARRAY['key1', 'key2'], ARRAY['value1', 'value2']) AS \"simple_map_col\", "
            + "MAP (ARRAY['key1', 'key2'], ARRAY[MAP (ARRAY['a', 'c'], ARRAY['b', 'd']), MAP (ARRAY['a', 'c'], ARRAY['b', 'd'])]) AS \"nested_map_col\"\nFROM \"test\".\"tablea\"" },

        { "test", "current_date_and_timestamp_view", "SELECT CURRENT_TIMESTAMP, TRIM(CAST(CURRENT_TIMESTAMP AS VARCHAR(65535))) AS \"ct\", CURRENT_DATE, CURRENT_DATE AS \"cd\", \"a\"\nFROM \"test\".\"tablea\"" },

        { "test", "date_function_view", "SELECT \"date\"('2021-01-02') AS \"a\"\n" + "FROM \"test\".\"tablea\"" },

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

        { "test", "view_from_utc_timestamp", "SELECT "
            + "CAST(\"at_timezone\"(\"from_unixtime_nanos\"(CAST(\"a_tinyint\" AS BIGINT) * 1000000), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime_nanos\"(CAST(\"a_smallint\" AS BIGINT) * 1000000), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime_nanos\"(CAST(\"a_integer\" AS BIGINT) * 1000000), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime_nanos\"(CAST(\"a_bigint\" AS BIGINT) * 1000000), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(CAST(\"a_float\" AS DOUBLE)), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(CAST(\"a_double\" AS DOUBLE)), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(CAST(\"a_decimal_three\" AS DOUBLE)), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(CAST(\"a_decimal_zero\" AS DOUBLE)), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(\"to_unixtime\"(\"with_timezone\"(\"a_timestamp\", 'UTC'))), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3)), "
            + "CAST(\"at_timezone\"(\"from_unixtime\"(\"to_unixtime\"(\"with_timezone\"(\"a_date\", 'UTC'))), \"$canonicalize_hive_timezone_id\"('America/Los_Angeles')) AS TIMESTAMP(3))\n"
            + "FROM \"test\".\"table_from_utc_timestamp\"" },

        { "test", "date_calculation_view", "SELECT \"date\"(CAST(\"SUBSTR\"('2021-08-20', 1, 10) AS TIMESTAMP)), \"date\"(CAST('2021-08-20' AS TIMESTAMP)), \"date\"(CAST('2021-08-20 00:00:00' AS TIMESTAMP)), \"date_add\"('day', 1, \"date\"(CAST('2021-08-20' AS TIMESTAMP))), \"date_add\"('day', 1, \"date\"(CAST('2021-08-20 00:00:00' AS TIMESTAMP))), \"date_add\"('day', 1 * -1, \"date\"(CAST('2021-08-20' AS TIMESTAMP))), \"date_add\"('day', 1 * -1, \"date\"(CAST('2021-08-20 00:00:00' AS TIMESTAMP))), \"date_diff\"('day', \"date\"(CAST('2021-08-21' AS TIMESTAMP)), \"date\"(CAST('2021-08-20' AS TIMESTAMP))), \"date_diff\"('day', \"date\"(CAST('2021-08-19' AS TIMESTAMP)), \"date\"(CAST('2021-08-20' AS TIMESTAMP))), \"date_diff\"('day', \"date\"(CAST('2021-08-19 23:59:59' AS TIMESTAMP)), \"date\"(CAST('2021-08-20 00:00:00' AS TIMESTAMP)))\n"
            + "FROM \"test\".\"tablea\"" },

        { "test", "pmod_view", "SELECT MOD(MOD(- 9, 4) + 4, 4)\nFROM \"test\".\"tablea\"" },

        { "test", "nullscollationd_view", "SELECT \"a\", \"b\", \"c\"\nFROM \"test\".\"tabler\"\nORDER BY \"b\" DESC" },

        { "test", "view_with_date_and_interval", "SELECT (CAST('2021-08-30' AS DATE) + INTERVAL '3' DAY)\nFROM \"test\".\"tablea\"" },

        { "test", "view_with_timestamp_and_interval", "SELECT (CAST('2021-08-30' AS TIMESTAMP) + INTERVAL -'3 01:02:03' DAY TO SECOND)\nFROM \"test\".\"tablea\"" },

        { "test", "view_with_timestamp_and_interval_2", "SELECT (CAST('2021-08-30' AS TIMESTAMP) + INTERVAL -'1-6' YEAR TO MONTH)\nFROM \"test\".\"tablea\"" },

        { "test", "greatest_view", "SELECT \"greatest\"(\"a\", \"b\") AS \"g_int\", \"greatest\"(\"c\", \"d\") AS \"g_string\"\n"
            + "FROM \"test\".\"table_ints_strings\"" },

        { "test", "least_view", "SELECT \"least\"(\"a\", \"b\") AS \"g_int\", \"least\"(\"c\", \"d\") AS \"g_string\"\n"
            + "FROM \"test\".\"table_ints_strings\"" }, };
  }

  @Test
  public void testLateralViewArray() {
    RelNode relNode = hiveToRelConverter
        .convertSql("SELECT col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias AS col");
    String targetSql = "SELECT \"t2\".\"col\" AS \"col\"\n" + "FROM (SELECT ARRAY['a1', 'a2'] AS \"a\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"a\") AS \"t2\" (\"col\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  /**
   * Test the generation of COMMA JOIN in Presto from Lateral views that are uncorrelated.
   */
  @Test
  public void testLateralViewArray2() {
    RelNode relNode = hiveToRelConverter
        .convertSql("SELECT arr.alias FROM test.tableA tmp LATERAL VIEW EXPLODE(ARRAY('a', 'b')) arr as alias");

    String targetSql = "SELECT \"t0\".\"alias\" AS \"alias\"\n" + "FROM \"test\".\"tablea\",\n"
        + "UNNEST(ARRAY['a', 'b']) AS \"t0\" (\"alias\")";
    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testLateralViewArrayWithoutColumns() {
    RelNode relNode = hiveToRelConverter
        .convertSql("SELECT col FROM (SELECT ARRAY('a1', 'a2') as a) tmp LATERAL VIEW EXPLODE(a) a_alias");
    String targetSql = "SELECT \"t2\".\"col\" AS \"col\"\n" + "FROM (SELECT ARRAY['a1', 'a2'] AS \"a\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"a\") AS \"t2\" (\"col\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testLateralViewMap() {
    RelNode relNode = hiveToRelConverter.convertSql(
        "SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias AS key, value");
    String targetSql = "SELECT \"t2\".\"key\" AS \"key\", \"t2\".\"value\" AS \"value\"\n"
        + "FROM (SELECT MAP (ARRAY['key1'], ARRAY['value1']) AS \"m\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"m\") AS \"t2\" (\"key\", \"value\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testLateralViewMapWithoutAlias() {
    RelNode relNode = hiveToRelConverter
        .convertSql("SELECT key, value FROM (SELECT MAP('key1', 'value1') as m) tmp LATERAL VIEW EXPLODE(m) m_alias");
    String targetSql = "SELECT \"t2\".\"KEY\" AS \"key\", \"t2\".\"VALUE\" AS \"value\"\n"
        + "FROM (SELECT MAP (ARRAY['key1'], ARRAY['value1']) AS \"m\"\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")) AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"m\") AS \"t2\" (\"KEY\", \"VALUE\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }
  
  

  @Test
  public void testLegacyUnnestArrayOfStruct() {
    RelNode relNode = hiveToRelConverter.convertView("test", "view_with_explode_struct_array");
    String targetSql = "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
        + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"$cor0\".\"b\") AS \"t0\" (\"c\")";

    RelToTrinoConverter relToTrinoConverter =
        new RelToTrinoConverter(ImmutableMap.of(SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT, true));
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testLegacyOuterUnnestArrayOfStruct() {
    RelNode relNode = hiveToRelConverter.convertView("test", "view_with_outer_explode_struct_array");
    String targetSql = "SELECT \"$cor0\".\"a\" AS \"a\", \"t0\".\"c\" AS \"c\"\n"
        + "FROM \"test\".\"table_with_struct_array\" AS \"$cor0\"\n"
        + "CROSS JOIN UNNEST(\"if\"(\"$cor0\".\"b\" IS NOT NULL AND CARDINALITY(\"$cor0\".\"b\") > 0, \"$cor0\".\"b\", ARRAY[NULL])) AS \"t0\" (\"c\")";

    RelToTrinoConverter relToTrinoConverter =
        new RelToTrinoConverter(ImmutableMap.of(SUPPORT_LEGACY_UNNEST_ARRAY_OF_STRUCT, true));
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testAvoidTransformToDate() {
    RelNode relNode = hiveToRelConverter
        .convertSql("SELECT to_date(substr('2021-08-20', 1, 10)), to_date('2021-08-20')" + "FROM test.tableA");
    String targetSql =
        "SELECT \"to_date\"(\"SUBSTR\"('2021-08-20', 1, 10)), \"to_date\"('2021-08-20')\n" + "FROM \"test\".\"tablea\"";

    RelToTrinoConverter relToTrinoConverter =
        new RelToTrinoConverter(ImmutableMap.of(AVOID_TRANSFORM_TO_DATE_UDF, true));
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }
  
  @Test
  public void testFromUnixTimeOneParameter() {
    RelNode relNode = hiveToRelConverter.convertSql("SELECT from_unixtime(10000)");
    String targetSql = "SELECT \"format_datetime\"(\"from_unixtime\"(10000), 'yyyy-MM-dd HH:mm:ss')\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }

  @Test
  public void testFromUnixTimeTwoParameters() {
    RelNode relNode = hiveToRelConverter.convertSql("SELECT from_unixtime(10000, 'yyyy-MM-dd')");
    String targetSql = "SELECT \"format_datetime\"(\"from_unixtime\"(10000), 'yyyy-MM-dd')\n"
        + "FROM (VALUES  (0)) AS \"t\" (\"ZERO\")";

    RelToTrinoConverter relToTrinoConverter = new RelToTrinoConverter();
    String expandedSql = relToTrinoConverter.convert(relNode);
    assertEquals(expandedSql, targetSql);
  }
}

