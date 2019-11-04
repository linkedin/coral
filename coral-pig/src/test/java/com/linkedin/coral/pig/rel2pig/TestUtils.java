package com.linkedin.coral.pig.rel2pig;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.HiveMscAdapter;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;
import java.io.InputStream;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.runtime.Hook;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;


/**
 * Provides utility functions used in unit tests
 */
public class TestUtils {

  static final String TEST_JSON_FILE_DIR = "src/test/resources/data";
  static HiveToRelConverter hiveToRelConverter;

  private TestUtils() {
  }

  /**
   * Initializes the tables used in Coral-Pig unit tests
   * @throws HiveException
   * @throws MetaException
   */
  public static void initializeViews() throws HiveException, MetaException {
    HiveConf conf = loadResourceHiveConf();
    SessionState.start(conf);
    Driver driver = new Driver(conf);
    HiveMetastoreClient hiveMetastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());
    hiveToRelConverter = HiveToRelConverter.create(hiveMetastoreClient);

    // Views and tables used in unit tests
    run(driver, String.join("\n", "",
        "CREATE DATABASE IF NOT EXISTS pig"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tableA(a int, b int, c int)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tableB(a int, b int)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tableLeft(a int, b int, c int)"));

    run(driver, String.join("\n", "",
        "CREATE TABLE IF NOT EXISTS pig.tableRight(d int, e int)"));
  }

  /**
   * Turns off optimizations used in the transformation of Calcite SQL to Relational Algebra
   */
  public static void turnOffRelSimplification() {
    // Rel simplification can statically interpret boolean conditions in
    // OR, AND, CASE clauses and simplify those. This has two problems:
    // 1. Our type system is not perfect replication of Hive so this can be incorrect
    // 2. Converted expression is harder to validate for correctness(because it appears different from input)
    Hook.REL_BUILDER_SIMPLIFY.add(Hook.propertyJ(false));
  }

  /**
   * Translates the view definition of a SQL query to Pig Latin
   * @param sql A SQL query.
   * @param outputRelation The variable that the output of the table will be stored in
   * @return Ordered list of Pig Latin statements to read the records produced by the view definition of the
   *         given database.table stored in outputRelation
   */
  public static String[] sqlToPigLatin(String sql, String outputRelation) {
    RelNode relNode = hiveToRelConverter.convertSql(sql);

    PigLoadFunction pigLoadFunction = (String db, String t) -> {

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableA")) {
        return "JsonLoader('a:int, b:int, c:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableB")) {
        return "JsonLoader('a:int, b:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableLeft")) {
        return "JsonLoader('a:int, b:int, c:int')";
      }

      if (db.equalsIgnoreCase("pig") && t.equalsIgnoreCase("tableRight")) {
        return "JsonLoader('d:int, e:int')";
      }

      return "JsonLoader()";
    };

    TableToPigPathFunction tableToPigPathFunction =
        (String db, String t) -> String.format("%s/%s/%s.json", TEST_JSON_FILE_DIR, db, t);

    RelToPigLatinConverter converter = new RelToPigLatinConverter(pigLoadFunction, tableToPigPathFunction);
    String pigScript = converter.convert(relNode, outputRelation);
    return pigScript.split("\n");
  }

  private static void run(Driver driver, String sql) {
    while (true) {
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  private static HiveConf loadResourceHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-pig");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/pig");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/pig");
    return hiveConf;
  }
}
