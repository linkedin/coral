/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.Programs;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;

import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static com.linkedin.coral.trino.rel2trino.TestTable.*;


public class TestUtils {
  public static final String CORAL_TRINO_TEST_DIR = "coral.trino.test.dir";

  private static HiveMscAdapter hiveMetastoreClient;
  static HiveToRelConverter hiveToRelConverter;

  public static FrameworkConfig createFrameworkConfig(TestTable... tables) {
    SchemaPlus rootSchema = Frameworks.createRootSchema(true);
    Arrays.asList(tables).forEach(t -> rootSchema.add(t.getTableName(), t));

    SqlParser.Config parserConfig =
        SqlParser.configBuilder().setCaseSensitive(false).setConformance(SqlConformanceEnum.DEFAULT).build();

    return Frameworks.newConfigBuilder().parserConfig(parserConfig).defaultSchema(rootSchema)
        .traitDefs((List<RelTraitDef>) null).programs(Programs.ofRules(Programs.RULE_SET)).build();
  }

  static RelNode toRel(String sql, FrameworkConfig config) {
    Planner planner = Frameworks.getPlanner(config);
    try {
      SqlNode sn = planner.parse(sql);
      SqlNode validate = planner.validate(sn);
      RelRoot rel = planner.rel(validate);
      //RelNode relNode = rel.project();
      return rel.project();
      //return Calcite2TrinoUDFConverter.convertRel(relNode);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Utility to conveniently format sql string just like Calcite formatting.
   * This is used to conveniently generate expected string without having
   * to type all formatting each time.
   * WARNING: This is not generic method but utility method for common cases
   * for testing. There are bugs. Use at your own risk or, better yet, fix it
   * @param sql
   */
  public static String formatSql(String sql) {
    String s = upcaseKeywords(sql);
    s = quoteColumns(s);
    s = addLineBreaks(s);
    return s;
  }

  private static final ImmutableList<String> SQL_KEYWORDS;
  private static final Pattern KW_PATTERN;
  static {
    String kw = "SELECT FROM WHERE AS IN GROUP BY HAVING ORDER ASC DSC JOIN"
        + " INNER OUTER CROSS UNNEST LEFT RIGHT SUM COUNT MAX MIN AVG CAST IN EXCEPT IS NULL NOT OVER AND OR ON";
    SQL_KEYWORDS = ImmutableList.copyOf(kw.toLowerCase().split("\\s+"));
    KW_PATTERN = Pattern.compile("\\b(" + String.join("|", SQL_KEYWORDS) + ")");
  }

  private static String addLineBreaks(String s) {
    ImmutableList<String> lineBreakKeywords =
        ImmutableList.copyOf("SELECT FROM WHERE GROUP HAVING ORDER OVER IN EXCEPT UNION INTERSECT".split("\\s+"));
    Pattern pattern = Pattern.compile("\\s" + String.join("\\b|\\s", lineBreakKeywords) + "\\b");
    Matcher m = pattern.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "\n" + m.group().trim());
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String quoteColumns(String sql) {
    Iterable<String> concat = Iterables.concat(TABLE_ONE.getColumnNames(), TABLE_TWO.getColumnNames(),
        TABLE_THREE.getColumnNames(), TABLE_FOUR.getColumnNames(), ImmutableList.of(TABLE_ONE.getTableName(),
            TABLE_TWO.getTableName(), TABLE_THREE.getTableName(), TABLE_FOUR.getTableName()));
    Pattern colPattern = Pattern.compile(String.join("|", concat));
    return quoteColumns(sql, colPattern);
  }

  public String quoteColumns(String sql, List<String> columns) {
    return quoteColumns(sql, Pattern.compile(String.join("|", columns)));
  }

  public static String quoteColumns(String sql, Pattern pattern) {
    String s = quoteAliases(sql);
    Matcher m = pattern.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "\"" + m.group() + "\"");
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String quoteAliases(String input) {
    Pattern pattern = Pattern.compile("AS (\\w+)");
    Matcher m = pattern.matcher(input);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String replacement = null;
      String alias = m.group(1);
      if (alias.equalsIgnoreCase("integer") || alias.equalsIgnoreCase("double") || alias.equalsIgnoreCase("boolean")
          || alias.equalsIgnoreCase("varchar") || alias.equalsIgnoreCase("real") || alias.equalsIgnoreCase("varbinary")
          || alias.equalsIgnoreCase("timestamp")) {
        replacement = "AS " + alias.toUpperCase();
      } else {
        replacement = "AS \"" + m.group(1).toUpperCase() + "\"";
      }
      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String upcaseKeywords(String sql) {
    Matcher matcher = KW_PATTERN.matcher(sql);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, matcher.group().toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  static void run(Driver driver, String sql) {
    while (true) {
      try {
        CommandProcessorResponse result = driver.run(sql);
        if (result.getException() != null) {
          throw new RuntimeException("Execution failed for: " + sql, result.getException());
        }
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  static void turnOffRelSimplification() {
    // Rel simplification can statically interpret boolean conditions in
    // OR, AND, CASE clauses and simplify those. This has two problems:
    // 1. Our type system is not perfect replication of Hive so this can be incorrect
    // 2. Converted expression is harder to validate for correctness(because it appears different from input)
    Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
  }

  public static void initializeViews(HiveConf conf) throws HiveException, MetaException, IOException {
    String testDir = conf.get(CORAL_TRINO_TEST_DIR);
    System.out.println("Test Workspace: " + testDir);
    FileUtils.deleteDirectory(new File(testDir));
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = new HiveToRelConverter(hiveMetastoreClient);

    // Views and tables used in HiveToTrinoConverterTest
    run(driver, "CREATE DATABASE IF NOT EXISTS test");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableA(a int, b struct<b1:string>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view AS \n"
        + "SELECT * from test.tableA union all SELECT * from test.tableA");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_with_more_than_two_tables AS \n"
        + "SELECT * from test.tableA union all SELECT * from test.tableA union all SELECT * from test.tableA");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_with_alias AS \n"
        + "SELECT * FROM (SELECT * from test.tableA) as viewFirst union all SELECT * FROM (SELECT * from test.tableA) as viewSecond");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableB(a int, b struct<b1:string>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableC(a int, b struct<b1:string>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_single_branch_evolved AS \n"
        + "SELECT * from test.tableB union all SELECT * from test.tableC");
    run(driver, "ALTER TABLE test.tableC CHANGE COLUMN b b struct<b1:string, b2:int>");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableD(a int, b struct<b1:string>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableE(a int, b struct<b1:string>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_double_branch_evolved_same AS \n"
        + "SELECT * from test.tableD union all SELECT * from test.tableE");
    run(driver, "ALTER TABLE test.tableD CHANGE COLUMN b b struct<b1:string, b2:int>");
    run(driver, "ALTER TABLE test.tableE CHANGE COLUMN b b struct<b1:string, b2:int>");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableF(a int, b struct<b1:string>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableG(a int, b struct<b1:string>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_double_branch_evolved_different AS \n"
        + "SELECT * from test.tableF union all SELECT * from test.tableG");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_more_than_two_branches_evolved AS \n"
        + "SELECT * from test.tableF union all SELECT * from test.tableG union all SELECT * from test.tableF");
    run(driver, "ALTER TABLE test.tableF CHANGE COLUMN b b struct<b1:string, b3:string>");
    run(driver, "ALTER TABLE test.tableG CHANGE COLUMN b b struct<b1:string, b2:int>");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableH(a int, b map<string, struct<b1:string>>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableI(a int, b map<string, struct<b1:string>>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_map_with_struct_value_evolved AS \n"
        + "SELECT * from test.tableH union all SELECT * from test.tableI");
    run(driver, "ALTER TABLE test.tableH CHANGE COLUMN b b map<string, struct<b1:string, b2:int>>");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableJ(a int, b array<struct<b1:string>>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableK(a int, b array<struct<b1:string>>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_array_with_struct_value_evolved AS \n"
        + "SELECT * from test.tableJ union all SELECT * from test.tableK");
    run(driver, "ALTER TABLE test.tableJ CHANGE COLUMN b b array<struct<b1:string, b2:int>>");

    run(driver,
        "CREATE TABLE IF NOT EXISTS test.tableL(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)");
    run(driver,
        "CREATE TABLE IF NOT EXISTS test.tableM(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_deeply_nested_struct_evolved AS \n"
        + "SELECT * from test.tableL union all SELECT * from test.tableM");
    run(driver,
        "ALTER TABLE test.tableL CHANGE COLUMN b b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string, b6:string>>>");

    run(driver,
        "CREATE TABLE IF NOT EXISTS test.tableN(a int, b struct<b1:string, m1:map<string, struct<b1:string, a1:array<struct<b1:string>>>>>)");
    run(driver,
        "CREATE TABLE IF NOT EXISTS test.tableO(a int, b struct<b1:string, m1:map<string, struct<b1:string, a1:array<struct<b1:string>>>>>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.fuzzy_union_view_deeply_nested_complex_struct_evolved AS \n"
        + "SELECT * from test.tableN union all SELECT * from test.tableO");
    run(driver,
        "ALTER TABLE test.tableN CHANGE COLUMN b b struct<b1:string, m1:map<string, struct<b1:string, a1:array<struct<b0:string, b1:string>>>>>");

    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.tableP(a int, b struct<b1:string>)"));
    run(driver, String.join("\n", "", "CREATE TABLE IF NOT EXISTS test.tableQ(a int, b struct<b1:string>)"));
    run(driver,
        String.join("\n", "", "CREATE VIEW IF NOT EXISTS test.union_view_same_schema_evolution_with_different_ordering",
            "AS", "SELECT *", "from test.tableP", "union all", "SELECT *", "from test.tableQ"));
    run(driver,
        String.join("\n", "", "ALTER TABLE test.tableP CHANGE COLUMN b b struct<b2:double, b1:string, b0:int>"));
    run(driver, String.join("\n", "", "ALTER TABLE test.tableQ CHANGE COLUMN b b struct<b0:int, b1:string, b2:int>"));

    run(driver, "CREATE TABLE test.table_with_string_array(a int, b array<string>)");
    run(driver,
        "CREATE VIEW test.view_with_explode_string_array AS SELECT a, c FROM test.table_with_string_array LATERAL VIEW EXPLODE(b) t AS c");
    run(driver,
        "CREATE VIEW test.view_with_outer_explode_string_array AS SELECT a, c FROM test.table_with_string_array LATERAL VIEW OUTER EXPLODE(b) t AS c");

    run(driver, "CREATE TABLE test.table_with_struct_array(a int, b array<struct<sa: int, sb: string>>)");
    run(driver,
        "CREATE VIEW test.view_with_explode_struct_array AS SELECT a, c FROM test.table_with_struct_array LATERAL VIEW EXPLODE(b) t AS c");
    run(driver,
        "CREATE VIEW test.view_with_outer_explode_struct_array AS SELECT a, c FROM test.table_with_struct_array LATERAL VIEW OUTER EXPLODE(b) t AS c");

    run(driver,
        "CREATE VIEW test.view_with_date_and_interval AS SELECT CAST('2021-08-30' AS DATE) + INTERVAL '3' DAY FROM test.tableA");
    run(driver,
        "CREATE VIEW test.view_with_timestamp_and_interval AS SELECT CAST('2021-08-30' AS TIMESTAMP) + INTERVAL '-3 01:02:03' DAY TO SECOND FROM test.tableA");
    run(driver,
        "CREATE VIEW test.view_with_timestamp_and_interval_2 AS SELECT CAST('2021-08-30' AS TIMESTAMP) + INTERVAL '-1-6' YEAR TO MONTH FROM test.tableA");

    run(driver, "CREATE TABLE test.table_with_map(a int, b map<string, string>)");
    run(driver,
        "CREATE VIEW test.view_with_explode_map AS SELECT a, c, d FROM test.table_with_map LATERAL VIEW EXPLODE(b) t AS c, d");
    run(driver,
        "CREATE VIEW test.view_with_outer_explode_map AS SELECT a, c, d FROM test.table_with_map LATERAL VIEW OUTER EXPLODE(b) t AS c, d");

    run(driver, "CREATE VIEW IF NOT EXISTS test.current_date_and_timestamp_view AS \n"
        + "SELECT CURRENT_TIMESTAMP, trim(cast(CURRENT_TIMESTAMP as string)) as ct, CURRENT_DATE, CURRENT_DATE as cd, a from test.tableA");
    run(driver,
        "CREATE VIEW IF NOT EXISTS test.date_function_view AS \n" + "SELECT date('2021-01-02') as a from test.tableA");
    run(driver, "CREATE VIEW IF NOT EXISTS test.lateral_view_json_tuple_view AS \n"
        + "SELECT a, d, e, f FROM test.tableA LATERAL VIEW json_tuple(b.b1, 'trino', 'always', 'rocks') jt AS d, e, f");
    run(driver, "CREATE VIEW IF NOT EXISTS test.lateral_view_json_tuple_view_qualified AS \n"
        + "SELECT `t`.`a`, `jt`.`d`, `jt`.`e`, `jt`.`f` FROM `test`.`tableA` AS `t` LATERAL VIEW json_tuple(`t`.`b`.`b1`, \"trino\", \"always\", \"rocks\") `jt` AS `d`, `e`, `f`");
    run(driver, "CREATE VIEW IF NOT EXISTS test.get_json_object_view AS \n"
        + "SELECT get_json_object(b.b1, '$.name') FROM test.tableA");

    run(driver, "CREATE VIEW IF NOT EXISTS test.map_array_view AS \n"
        + "SELECT MAP('key1', 'value1', 'key2', 'value2') AS simple_map_col, "
        + "MAP('key1', MAP('a', 'b', 'c', 'd'), 'key2', MAP('a', 'b', 'c', 'd')) AS nested_map_col FROM test.tableA");

    run(driver,
        "CREATE TABLE test.table_from_utc_timestamp (a_tinyint tinyint, a_smallint smallint, "
            + "a_integer int, a_bigint bigint, a_float float, a_double double, "
            + "a_decimal_three decimal(10,3), a_decimal_zero decimal(10,0), a_timestamp timestamp, " + "a_date date)");
    run(driver,
        "CREATE VIEW test.view_from_utc_timestamp AS SELECT from_utc_timestamp(a_tinyint, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_smallint, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_integer, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_bigint, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_float, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_double, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_decimal_three, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_decimal_zero, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_timestamp, 'America/Los_Angeles'), "
            + "from_utc_timestamp(a_date, 'America/Los_Angeles')" + "FROM test.table_from_utc_timestamp");

    run(driver, "CREATE VIEW IF NOT EXISTS test.date_calculation_view AS \n"
        + "SELECT to_date(substr('2021-08-20', 1, 10)), to_date('2021-08-20'), " + "to_date('2021-08-20 00:00:00'), "
        + "date_add('2021-08-20', 1), " + "date_add('2021-08-20 00:00:00', 1), " + "date_sub('2021-08-20', 1), "
        + "date_sub('2021-08-20 00:00:00', 1), " + "datediff('2021-08-20', '2021-08-21'), "
        + "datediff('2021-08-20', '2021-08-19'), " + "datediff('2021-08-20 00:00:00', '2021-08-19 23:59:59')"
        + "FROM test.tableA");

    run(driver, "CREATE VIEW IF NOT EXISTS test.pmod_view AS \n" + "SELECT pmod(-9, 4) FROM test.tableA");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableR(a int, b string, c int)");
    run(driver,
        "CREATE VIEW IF NOT EXISTS test.nullscollationd_view AS \n" + "SELECT a,b,c FROM test.tableR ORDER  BY b DESC");

    run(driver, "CREATE VIEW IF NOT EXISTS test.t_dot_star_view AS \n"
        + "SELECT ta.*, tb.b as tbb FROM test.tableA as ta JOIN test.tableA as tb ON ta.a = tb.a");

    run(driver, "CREATE TABLE IF NOT EXISTS test.table_ints_strings( a int, b int, c string, d string)");

    run(driver, "CREATE VIEW IF NOT EXISTS test.greatest_view AS \n"
        + "SELECT greatest(t.a, t.b) as g_int, greatest(t.c, t.d) as g_string FROM test.table_ints_strings t");

    run(driver, "CREATE VIEW IF NOT EXISTS test.least_view AS \n"
        + "SELECT least(t.a, t.b) as g_int, least(t.c, t.d) as g_string FROM test.table_ints_strings t");

    run(driver, "CREATE VIEW IF NOT EXISTS test.cast_decimal_view AS \n"
        + "SELECT CAST(t.a as DECIMAL(6,2)) as casted_decimal FROM test.table_ints_strings t");

    run(driver, "CREATE TABLE IF NOT EXISTS test.tableS (structCol struct<a:int>)");
    run(driver, "CREATE TABLE IF NOT EXISTS test.tableT (structCol struct<a:int>)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.viewA AS SELECT structCol as struct_col FROM test.tableS");
    run(driver, "CREATE VIEW IF NOT EXISTS test.viewB AS SELECT structCol as struct_col FROM test.tableT");
    run(driver,
        "CREATE VIEW IF NOT EXISTS test.view_with_transform_column_name_reset AS SELECT struct_col AS structCol FROM (SELECT * FROM test.viewA UNION ALL SELECT * FROM test.viewB) X");
    run(driver, "ALTER TABLE test.tableT CHANGE COLUMN structCol structCol struct<a:int, b:string>");

    run(driver, "CREATE TABLE test.duplicate_column_name_a (some_id string)");
    run(driver, "CREATE TABLE test.duplicate_column_name_b (some_id string)");
    run(driver, "CREATE VIEW IF NOT EXISTS test.view_namesake_column_names AS \n"
        + "SELECT a.some_id FROM test.duplicate_column_name_a a LEFT JOIN ( SELECT trim(some_id) AS SOME_ID FROM test.duplicate_column_name_b) b ON a.some_id = b.some_id WHERE a.some_id != ''");

    run(driver, "CREATE TABLE test.table_with_binary_column (b binary)");
  }

  public static RelNode convertView(String db, String view) {
    return new HiveToRelConverter(hiveMetastoreClient).convertView(db, view);
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(CORAL_TRINO_TEST_DIR,
        System.getProperty("java.io.tmpdir") + "/coral/trino/" + UUID.randomUUID().toString());
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-trino");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/trino");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/trino");
    return hiveConf;
  }
}
