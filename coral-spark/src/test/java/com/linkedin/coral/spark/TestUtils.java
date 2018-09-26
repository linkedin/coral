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
        "tblproperties('functions' = 'LessThanHundred com.linkedin.coral.hive.hive2rel.CoralTestUDF',",
        "              'dependencies' = 'com.linkedin:udf:1.0')",
        "AS",
        "SELECT default_foo_dali_udf_LessThanHundred(a)",
        "FROM foo"
    ));
    run(driver, String.join("\n","",
        "CREATE VIEW IF NOT EXISTS named_struct_view",
        "AS",
        "SELECT named_struct('abc', 123, 'def', 'xyz') AS named_struc",
        "FROM bar"
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
