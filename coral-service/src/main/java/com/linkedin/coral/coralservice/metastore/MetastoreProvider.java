package com.linkedin.coral.coralservice.metastore;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.coralservice.utils.CoralUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.naming.ConfigurationException;

import com.google.common.base.Strings;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.security.UserGroupInformation;

import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


/**
 * Utility class to get Hive metastore client for local hive or grid based hive using kerberos
 */
@SuppressWarnings("WeakerAccess")
public class MetastoreProvider {

  public static final String HIVE_METASTORE_URIS = "hive.metastore.uris";
  public static final String HIVE_METASTORE_AUTHENTICATION = "hive.metastore.authentication";
  public static final String HIVE_METASTORE_CLIENT_PRINCIPAL = "hive.metastore.client.principal";
  public static final String HIVE_METASTORE_CLIENT_KEYTAB = "hive.metastore.client.keytab";
  public static final String HIVE_METASTORE_SERVICE_PRINCIPAL = "hive.metastore.service.principal";
  public static final String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";

  // following properties are to create local hive metastore
  public static final String HIVE_DIR = "hive.dir";

  public static final String DEFAULT_METASTORE_URI = "thrift://localhost:7552";
  public static final String DEFAULT_METASTORE_AUTHENTICATION = "SIMPLE";
  public static final String KERBEROS_AUTHENTICATION = "kerberos";
  public static final String DEFAULT_TMP_HIVE_DIR;

  static {
    DEFAULT_TMP_HIVE_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "coral").toString();
  }

  public static HiveMetastoreClient getMetastoreClient() throws Exception {
    final InputStream hiveConfStream =
        CoralUtils.class.getClassLoader().getResourceAsStream("hive.properties");
    final Properties props = new Properties();
    props.load(hiveConfStream);
    return new HiveMscAdapter(MetastoreProvider.getGridMetastoreClient(props));
  }

  /**
   * Get metastore client for the hive metastore on Grid. Supports kerberos based
   * authentication. Using kerberos requires user to get kerberos ticket and setup
   * keytab externally.
   * @param props hive configuration to use for connecting to the metastore
   * @return metastore client
   * @throws IOException indicates failure to read keytab file
   * @throws MetaException indicates failure to instantiate metastore client
   * @throws ConfigurationException on missing required configuration parameter
   */
  public static IMetaStoreClient getGridMetastoreClient(Properties props)
      throws IOException, MetaException, ConfigurationException {

    HiveConf conf = new HiveConf();
    conf.setVar(METASTOREURIS, props.getProperty(HIVE_METASTORE_URIS, DEFAULT_METASTORE_URI));

    if (props.getProperty(HIVE_METASTORE_AUTHENTICATION, DEFAULT_METASTORE_AUTHENTICATION)
        .equalsIgnoreCase(KERBEROS_AUTHENTICATION)) {

      String clientPrincipal = props.getProperty(HIVE_METASTORE_CLIENT_PRINCIPAL);
      if (Strings.isNullOrEmpty(clientPrincipal)) {
        throw new ConfigurationException(String.format("%s is required", HIVE_METASTORE_CLIENT_PRINCIPAL));
      }
      String clientKeytab = props.getProperty(HIVE_METASTORE_CLIENT_KEYTAB);
      if (Strings.isNullOrEmpty(clientKeytab)) {
        throw new ConfigurationException(String.format("%s is required", HIVE_METASTORE_CLIENT_KEYTAB));
      }
      String servicePrincipal = props.getProperty(HIVE_METASTORE_SERVICE_PRINCIPAL);
      if (Strings.isNullOrEmpty(servicePrincipal)) {
        throw new ConfigurationException(String.format("%s is required", HIVE_METASTORE_SERVICE_PRINCIPAL));
      }
      // leave this commented...handy debugging option rather than searching
      // System.setProperty("sun.security.krb5.debug", "true");
      conf.setVar(METASTORE_USE_THRIFT_SASL, "true");
      conf.setVar(METASTORE_KERBEROS_PRINCIPAL, servicePrincipal);
      conf.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS_AUTHENTICATION);

      UserGroupInformation.setConfiguration(conf);
      UserGroupInformation.loginUserFromKeytab(clientPrincipal, clientKeytab);
    }
    return new HiveMetaStoreClient(conf);
  }

  /**
   * Get hive metastore client for local hive. Uses locally setup database
   * and uses embedded derby to connect.
   * @param props properties to connect to the local store
   * @return Local hive metastore client
   * @throws HiveException on failure to instantiate local hive
   * @throws MetaException on failure to create local hive metastore
   */
  public static IMetaStoreClient getLocalMetastoreClient(Properties props) throws HiveException, MetaException {
    HiveConf conf = new HiveConf();
    String tmpDir = props.getProperty(HIVE_DIR, DEFAULT_TMP_HIVE_DIR);
    conf.setVar(SCRATCHDIR, getPath(tmpDir, "hive-scratch-dir"));
    conf.setVar(METASTOREWAREHOUSE, getPath(tmpDir, "warehouse"));
    conf.setVar(METASTORE_CONNECTION_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
    conf.setVar(METASTORECONNECTURLKEY, "jdbc:derby:;databaseName=" + getPath(tmpDir, "metastore.db") + ";create=true");
    SessionState.start(conf);
    return Hive.get(conf).getMSC();
  }

  private static String getPath(String parent, String child) {
    return Paths.get(parent, child).toString();
  }
}