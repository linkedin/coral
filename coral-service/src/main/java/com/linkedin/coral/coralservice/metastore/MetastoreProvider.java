/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.metastore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;

import com.google.common.base.Strings;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.security.UserGroupInformation;

import com.linkedin.coral.common.HiveMetastoreClient;
import com.linkedin.coral.common.HiveMscAdapter;
import com.linkedin.coral.coralservice.utils.CoralProvider;

import static org.apache.hadoop.hive.conf.HiveConf.ConfVars.*;


/**
 * Utility class to get Hive metastore client for local hive or remote based hive using kerberos
 */
@SuppressWarnings("WeakerAccess")
public class MetastoreProvider {

  private static final String HIVE_METASTORE_URIS = "hive.metastore.uris";
  private static final String HIVE_METASTORE_AUTHENTICATION = "hive.metastore.authentication";
  private static final String HIVE_METASTORE_CLIENT_PRINCIPAL = "hive.metastore.client.principal";
  private static final String HIVE_METASTORE_CLIENT_KEYTAB = "hive.metastore.client.keytab";
  private static final String HIVE_METASTORE_SERVICE_PRINCIPAL = "hive.metastore.service.principal";
  private static final String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";

  private static final String DEFAULT_METASTORE_URI = "thrift://localhost:7552";
  private static final String DEFAULT_METASTORE_AUTHENTICATION = "SIMPLE";
  private static final String KERBEROS_AUTHENTICATION = "kerberos";

  public static HiveMetastoreClient getMetastoreClient() throws Exception {
    final InputStream hiveConfStream = CoralProvider.class.getClassLoader().getResourceAsStream("hive.properties");
    final Properties props = new Properties();
    props.load(hiveConfStream);
    return new HiveMscAdapter(MetastoreProvider.getRemoteMetastoreClient(props));
  }

  /**
   * Get metastore client for the remote hive metastore. Supports kerberos based
   * authentication. Using kerberos requires user to get kerberos ticket and setup
   * keytab externally.
   * @param props hive configuration to use for connecting to the metastore
   * @return metastore client
   * @throws IOException indicates failure to read keytab file
   * @throws MetaException indicates failure to instantiate metastore client
   * @throws ConfigurationException on missing required configuration parameter
   */
  private static IMetaStoreClient getRemoteMetastoreClient(Properties props)
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
      conf.setVar(METASTORE_USE_THRIFT_SASL, "true");
      conf.setVar(METASTORE_KERBEROS_PRINCIPAL, servicePrincipal);
      conf.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS_AUTHENTICATION);

      UserGroupInformation.setConfiguration(conf);
      UserGroupInformation.loginUserFromKeytab(clientPrincipal, clientKeytab);
    }
    return new HiveMetaStoreClient(conf);
  }
}
