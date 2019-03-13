package com.linkedin.coral.schema.avro;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import com.linkedin.coral.hive.hive2rel.test.HiveMscAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;


public class TestUtils {
  private static Driver driver;
  private static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  public static HiveMetastoreClient setup() throws HiveException, MetaException {
    HiveConf conf = getHiveConf();
    SessionState.start(conf);
    driver = new Driver(conf);
    HiveMetastoreClient metastoreClient = new HiveMscAdapter(Hive.get(conf).getMSC());

    initializeTables();

    return metastoreClient;
  }

  public static void executeCreateViewQuery(String dbName, String viewName, String sql) {
    executeQuery("DROP VIEW IF EXISTS " + dbName + "." + viewName);
    executeQuery(sql);
  }

  public static String loadSchema(String resource) {
    InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(resource);
    return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
  }

  private static void initializeTables() {
    String baseComplexSchema = loadSchema("base-complex.avsc");
    String baseEnumSchema = loadSchema("base-enum.avsc");

    executeCreateTableQuery("default", "basecomplex", baseComplexSchema);
    executeCreateTableQuery("default", "baseenum", baseEnumSchema);
  }



  private static void executeCreateTableQuery(String dbName, String tableName, String schema) {
    executeQuery("DROP TABLE IF EXISTS " + dbName + "." + tableName);
    executeQuery("CREATE EXTERNAL TABLE " + tableName + " "
        + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' "
        + "STORED AS "
        + "INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' "
        + "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' "
        + "TBLPROPERTIES ('" + AVRO_SCHEMA_LITERAL + "'='" + schema + "')");
  }

  private static void executeQuery(String sql){
    while(true){
      try {
        driver.run(sql);
      } catch (CommandNeedRetryException e) {
        continue;
      }
      break;
    }
  }

  private static HiveConf getHiveConf() {
    InputStream hiveConfStream = TestUtils.class.getClassLoader().getResourceAsStream("hive.xml");
    HiveConf hiveConf = new HiveConf();
    hiveConf.addResource(hiveConfStream);
    hiveConf.set("mapreduce.framework.name", "local-schema");
    hiveConf.set("_hive.hdfs.session.path", "/tmp/coral/schema");
    hiveConf.set("_hive.local.session.path", "/tmp/coral/schema");
    return hiveConf;
  }
}
