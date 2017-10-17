package com.linkedin.coral.hive.hive2rel;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;


/**
 * Class maintaining contextual information for Hive connection
 */
public class HiveContext {

  private final HiveConf conf;
  private final Driver driver;
  private final Context context;
  private final Hive hive;

  /**
   * Create Hive Context from hive site configuration file
   * @param hiveConfPath absolute file path for hive-site configuration file
   * @return HiveContext
   * @throws IOException
   * @throws HiveException
   */
  public static HiveContext create(String hiveConfPath) throws IOException, HiveException {
    HiveConf conf = new HiveConf();
    conf.addResource(new Path(hiveConfPath));
    return create(conf);
  }

  /**
   * Create HiveContext using provided HiveConf
   * @param conf HiveConf to create Hive context
   * @return HiveContext
   * @throws IOException
   * @throws HiveException
   */
  public static HiveContext create(HiveConf conf) throws IOException, HiveException {
    SessionState.start(conf);
    Context context = new Context(conf);
    Driver driver = new Driver(conf);
    return new HiveContext(conf, driver, context);
  }

  private HiveContext(HiveConf conf, Driver driver, Context context) throws HiveException {
    this.conf = conf;
    this.driver = driver;
    this.context = context;
    this.hive = Hive.get(this.conf);
  }

  /**
   * Returns handle to Hive
   * @return Hive
   */
  public Hive getHive() {
    return hive;
  }

  /**
   * Get Hive configuration
   * @return hive configuration
   */
  public HiveConf getConf() {
    return conf;
  }

  /**
   * Hive driver
   * @return hive driver
   */
  public Driver getDriver() {
    return driver;
  }

  /**
   * Returns hive context
   * @return hive context
   */
  public Context getContext() {
    return context;
  }
}
