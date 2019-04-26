package com.linkedin.coral.spark;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import com.linkedin.coral.hive.hive2rel.test.HiveMscAdapter;
import java.io.InputStream;
import org.apache.calcite.rel.RelNode;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;

public class TestUtils {

  static HiveToRelConverter hiveToRelConverter;

  static void run(Driver driver, String sql){
    while(true){
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  public static void initializeViews() throws HiveException, MetaException {
    HiveConf conf = loadResourceHiveConf();
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    HiveMetastoreClient hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = HiveToRelConverter.create(hiveMetastoreClient);
    run(driver, "CREATE TABLE IF NOT EXISTS foo(a int, b varchar(30), c double)");
    run(driver, "CREATE TABLE IF NOT EXISTS bar(x int, y double)");
    run(driver, "CREATE TABLE IF NOT EXISTS complex(a int, b string, c array<double>, s struct<name:string, age:int>, m map<int, string>, sarr array<struct<name:string, age:int>>)");
    run(driver, "CREATE FUNCTION default_foo_dali_udf_LessThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF'");
    run(driver, "CREATE FUNCTION default_foo_dali_udf2_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver, "CREATE FUNCTION default_foo_dali_udf3_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver, "CREATE FUNCTION default_foo_dali_udf3_FuncSquare as 'com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare'");
    run(driver, "CREATE FUNCTION default_foo_dali_udf4_GreaterThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF2'");
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_view",
        "AS",
        "SELECT b AS bcol, sum(c) AS sum_c",
        "FROM foo",
        "GROUP BY b"
    ));
    run(driver,String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_bar_view",
        "AS",
        "SELECT foo_view.bcol, bar.x",
        "FROM foo_view JOIN bar",
        "ON bar.y = foo_view.sum_c"
    ));
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_dali_udf",
        "tblproperties('functions' = 'LessThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.0')",
        "AS",
        "SELECT default_foo_dali_udf_LessThanHundred(a)",
        "FROM foo"
    ));
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_dali_udf2",
        "tblproperties('functions' = 'GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.0')",
        "AS",
        "SELECT default_foo_dali_udf2_GreaterThanHundred(a)",
        "FROM foo"
    ));
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_dali_udf3",
        "tblproperties('functions' = ",
        "'FuncSquare:com.linkedin.coral.hive.hive2rel.CoralTestUdfSquare GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
        "              'dependencies' = 'ivy://com.linkedin:udf:1.1 ivy://com.linkedin:udf:1.0')",
        "AS",
        "SELECT default_foo_dali_udf3_FuncSquare(a), default_foo_dali_udf3_GreaterThanHundred(a) ",
        "FROM foo"
    ));
    // foo_dali_udf4 is same as foo_dali_udf2, except we add extra space in dependencies parameter
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS foo_dali_udf4",
        "tblproperties('functions' = 'GreaterThanHundred:com.linkedin.coral.hive.hive2rel.CoralTestUDF2',",
        "              'dependencies' = '  ivy://com.linkedin:udf:1.0  ')",
        "AS",
        "SELECT default_foo_dali_udf4_GreaterThanHundred(a)",
        "FROM foo"
    ));
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS named_struct_view",
        "AS",
        "SELECT named_struct('abc', 123, 'def', 'xyz') AS named_struc",
        "FROM bar"
    ));

    // Views and tables used in FuzzyUnionViewTest
    run(driver, String.join("\n", "",
        "CREATE DATABASE IF NOT EXISTS fuzzy_union"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableA(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view",
        "AS",
        "SELECT * from fuzzy_union.tableA",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableA"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_more_than_two_tables",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableA",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableA",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableA"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_alias",
        "AS",
        "SELECT *",
        "FROM",
        "(SELECT * from fuzzy_union.tableA)",
        "as viewFirst",
        "union all",
        "SELECT *",
        "FROM (SELECT * from fuzzy_union.tableA)",
        "as viewSecond"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableB(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableC(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_single_branch_evolved",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableB",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableC"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableC CHANGE COLUMN b b struct<b1:string, b2:int>"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableD(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableE(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_same",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableD",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableE"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableD CHANGE COLUMN b b struct<b1:string, b2:int>"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableE CHANGE COLUMN b b struct<b1:string, b2:int>"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableF(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableG(a int, b struct<b1:string>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_different",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableF",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableG"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_more_than_two_branches_evolved",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableF",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableG",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableF"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableF CHANGE COLUMN b b struct<b1:string, b3:string>"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableG CHANGE COLUMN b b struct<b1:string, b2:int>"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableH(a int, b map<string, struct<b1:string>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableI(a int, b map<string, struct<b1:string>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_map_with_struct_value_evolved",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableH",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableI"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableH CHANGE COLUMN b b map<string, struct<b1:string, b2:int>>"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableJ(a int, b array<struct<b1:string>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableK(a int, b array<struct<b1:string>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_array_with_struct_value_evolved",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableJ",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableK"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableJ CHANGE COLUMN b b array<struct<b1:string, b2:int>>"
    ));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableL(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS fuzzy_union.tableM(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)"
    ));
    run(driver, String.join("\n", "",
        "CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_deeply_nested_struct_evolved",
        "AS",
        "SELECT *",
        "from fuzzy_union.tableL",
        "union all",
        "SELECT *",
        "from fuzzy_union.tableM"
    ));
    run(driver, String.join("\n", "",
        "ALTER TABLE fuzzy_union.tableL CHANGE COLUMN b b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string, b6:string>>>"
    ));

  }

  public static RelNode toRelNode(String db, String view) {
    return hiveToRelConverter.convertView(db, view);
  }

  public static RelNode toRelNode(String sql) {
    return hiveToRelConverter.convertSql(sql);
  }

  private static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-spark");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/spark");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/spark");
    return hiveConf;
  }

}
