package com.linkedin.coral.hive.hive2rel;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;


public class TestUtils {

  static TestHive hive;

  public static class TestHive {
    private final HiveConf conf;
    List<DB> databases;

    public TestHive(HiveConf conf) {
      this.conf = conf;
    }

    public HiveConf getConf() {
      return conf;
    }

    public static class DB {
      DB(String name, Iterable<String> tables) {
        this.name = name;
        this.tables = ImmutableList.copyOf(tables);
      }
      String name;
      List<String> tables;
    }

    public List<String> getDbNames() {
      return databases.stream()
          .map(db -> db.name)
          .collect(Collectors.toList());
    }

    public List<String> getTables(String db) {
      return databases.stream()
          .filter(d -> d.name == db)
          .findFirst()
          .orElseThrow(() -> new RuntimeException("DB " + db + " not found"))
          .tables;
    }

    public IMetaStoreClient getMetastoreClient() throws HiveException, MetaException {
      return Hive.get(conf).getMSC();
    }
  }

  public static TestHive setupDefaultHive() throws IOException {
    if (hive != null) {
      return hive;
    }
    System.out.println(System.getProperty("java.io.tmpdir"));
    HiveConf conf = loadResourceHiveConf();
    TestHive testHive = new TestHive(conf);
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    try {
      driver.run("CREATE DATABASE IF NOT EXISTS test");
      driver.run("CREATE TABLE IF NOT EXISTS test.tableOne(a int, b varchar(30), c double, d timestamp)");
      driver.run("CREATE TABLE IF NOT EXISTS test.tableTwo(x int, y double)");

      driver.run("CREATE DATABASE IF NOT EXISTS fuzzy_union");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableA(a int, b struct<b1:string>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view AS SELECT * from fuzzy_union.tableA union all SELECT * from fuzzy_union.tableA");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_more_than_two_tables AS SELECT * from fuzzy_union.tableA union all SELECT * from fuzzy_union.tableA union all SELECT * from fuzzy_union.tableA");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_with_alias AS SELECT * FROM (SELECT * from fuzzy_union.tableA) as viewFirst union all SELECT * FROM (SELECT * from fuzzy_union.tableA) as viewSecond");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableB(a int, b struct<b1:string>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableC(a int, b struct<b1:string>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_single_branch_evolved AS SELECT * from fuzzy_union.tableB union all SELECT * from fuzzy_union.tableC");
      driver.run("ALTER TABLE fuzzy_union.tableC CHANGE COLUMN b b struct<b1:string, b2:int>");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableD(a int, b struct<b1:string>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableE(a int, b struct<b1:string>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_same AS SELECT * from fuzzy_union.tableD union all SELECT * from fuzzy_union.tableE");
      driver.run("ALTER TABLE fuzzy_union.tableD CHANGE COLUMN b b struct<b1:string, b2:int>");
      driver.run("ALTER TABLE fuzzy_union.tableE CHANGE COLUMN b b struct<b1:string, b2:int>");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableF(a int, b struct<b1:string>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableG(a int, b struct<b1:string>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_double_branch_evolved_different AS SELECT * from fuzzy_union.tableF union all SELECT * from fuzzy_union.tableG");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_more_than_two_branches_evolved AS SELECT * from fuzzy_union.tableF union all SELECT * from fuzzy_union.tableG union all SELECT * from fuzzy_union.tableF");
      driver.run("ALTER TABLE fuzzy_union.tableF CHANGE COLUMN b b struct<b1:string, b3:string>");
      driver.run("ALTER TABLE fuzzy_union.tableG CHANGE COLUMN b b struct<b1:string, b2:int>");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableH(a int, b map<string, struct<b1:string>>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableI(a int, b map<string, struct<b1:string>>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_map_with_struct_value_evolved AS SELECT * from fuzzy_union.tableH union all SELECT * from fuzzy_union.tableI");
      driver.run("ALTER TABLE fuzzy_union.tableH CHANGE COLUMN b b map<string, struct<b1:string, b2:int>>");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableJ(a int, b array<struct<b1:string>>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableK(a int, b array<struct<b1:string>>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_array_with_struct_value_evolved AS SELECT * from fuzzy_union.tableJ union all SELECT * from fuzzy_union.tableK");
      driver.run("ALTER TABLE fuzzy_union.tableJ CHANGE COLUMN b b array<struct<b1:string, b2:int>>");

      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableL(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)");
      driver.run("CREATE TABLE IF NOT EXISTS fuzzy_union.tableM(a int, b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string>>>)");
      driver.run("CREATE VIEW IF NOT EXISTS fuzzy_union.union_view_deeply_nested_struct_evolved AS SELECT * from fuzzy_union.tableL union all SELECT * from fuzzy_union.tableM");
      driver.run("ALTER TABLE fuzzy_union.tableL CHANGE COLUMN b b struct<b1:string, b2:struct<b3:string, b4:struct<b5:string, b6:string>>>");

      driver.run("CREATE TABLE IF NOT EXISTS foo(a int, b varchar(30), c double)");
      driver.run("CREATE TABLE IF NOT EXISTS bar(x int, y double)");
      driver.run("CREATE VIEW IF NOT EXISTS foo_view AS SELECT b as bcol, sum(c) as sum_c from foo group by b");
      driver.run(
          "CREATE TABLE IF NOT EXISTS complex(a int, b string, c array<double>, s struct<name:string, age:int>, m map<string, string>, sarr array<struct<name:string, age:int>>)");
      CommandProcessorResponse response = driver.run("create function test_tableOneView_LessThanHundred as 'com.linkedin.coral.hive.hive2rel.CoralTestUDF'");
      response = driver.run("CREATE VIEW IF NOT EXISTS test.tableOneView as SELECT test_tableOneView_LessThanHundred(a) from test.tableOne");
      if (response.getResponseCode() != 0) {
        throw new RuntimeException("Failed to setup view");
      }
      testHive.databases = ImmutableList.of(
          new TestHive.DB("test", ImmutableList.of("tableOne", "tableTwo", "tableOneView")),
          new TestHive.DB("default", ImmutableList.of("foo", "bar", "complex", "foo_view")),
          new TestHive.DB("fuzzy_union", ImmutableList.of("tableA", "tableB", "tableC",
              "union_view", "union_view_with_more_than_two_tables", "union_view_with_alias",
              "union_view_single_branch_evolved", "union_view_double_branch_evolved_different",
              "union_view_map_with_struct_value_evolved", "union_view_array_with_struct_value_evolved",
              "union_view_deeply_nested_struct_evolved", "union_view_more_than_two_branches_evolved"))
      );

      // add some Dali functions to table properties
      IMetaStoreClient msc = testHive.getMetastoreClient();
      Table fooViewTable = msc.getTable("default", "foo_view");
      setOrUpdateDaliFunction(fooViewTable, "IsTestMemberId", "com.linkedin.dali.udf.istestmemberid.hive.IsTestMemberId");
      msc.alter_table("default", "foo_view", fooViewTable);
      Table tableOneView = msc.getTable("test", "tableOneView");
      setOrUpdateDaliFunction(tableOneView, "LessThanHundred", "com.linkedin.coral.hive.hive2rel.CoralTestUDF");
      msc.alter_table("test", "tableOneView", tableOneView);
      hive = testHive;
      return hive;
    } catch (Exception e) {
      throw new RuntimeException("Failed to setup database", e);
    }
  }

  // package private
  /**
   * Caller must explicitly make changes persistent by calling alter_table method on
   * metastore client to make changes persistent.
   */
  static void setOrUpdateDaliFunction(Table table, String functionName, String functionClass) {
    table.setOwner("daliview");
    Map<String, String> parameters = table.getParameters();
    String[] split = table.getParameters().getOrDefault("functions", new String())
        .split(" |:");
    Map<String, String> functionMap = new HashMap<>();
    for (int i = 0; i < split.length - 1; i += 2) {
      functionMap.put(split[i], split[i+1]);
    }
    functionMap.put(functionName, functionClass);
    String serializedFunctions = functionMap.entrySet().stream()
        .map(x -> x.getKey() + ":" + x.getValue())
        .reduce((x, y) -> x + " " + y)
        .get();
    parameters.put("functions", serializedFunctions);
    table.setParameters(parameters);
  }

  public static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral");
    hiveConf.set("_hive.local.session.path", "/tmp/coral");
    return hiveConf;
  }
}
