/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.spark.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IHMSHandler;
import org.apache.hadoop.hive.metastore.RetryingHMSHandler;
import org.apache.hadoop.hive.metastore.TSetIpAddressProcessor;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportFactory;

import static java.nio.file.Files.*;
import static java.nio.file.attribute.PosixFilePermissions.*;


public class TestHiveMetastore {

  private File hiveLocalDir;
  private HiveConf hiveConf;
  private ExecutorService executorService;
  private HiveMetaStoreClient hiveMetaStoreClient;
  private TServer server;

  TestHiveMetastore() {
    start();
  }

  public void start() {
    try {
      hiveLocalDir = createTempDirectory("hive", asFileAttribute(fromString("rwxrwxrwx"))).toFile();
      File derbyLogFile = new File(hiveLocalDir, "derby.log");
      System.setProperty("derby.stream.error.file", derbyLogFile.getAbsolutePath());
      setupMetastoreDB("jdbc:derby:" + getDerbyPath() + ";create=true");

      TServerSocket socket = new TServerSocket(0);
      int port = socket.getServerSocket().getLocalPort();
      File hiveSiteXml = new File(hiveLocalDir, "hive-site.xml");
      createHiveSiteXml(hiveSiteXml, port);
      addURLToClassLoader(hiveLocalDir.toURI().toURL());
      hiveConf = newHiveConf(port);
      server = newThriftServer(socket, hiveConf);
      executorService = Executors.newSingleThreadExecutor();
      executorService.submit(() -> server.serve());
      hiveMetaStoreClient = getMetastoreClient();
    } catch (Exception e) {
      throw new RuntimeException("Cannot start TestHiveMetastore", e);
    }
  }

  public void stop() {
    if (server != null) {
      server.stop();
    }
    if (executorService != null) {
      executorService.shutdown();
    }
    if (hiveLocalDir != null) {
      hiveLocalDir.delete();
    }
  }

  public HiveMetaStoreClient getMetastoreClient() throws MetaException {
    return new HiveMetaStoreClient(hiveConf);
  }

  public void createDatabase(String dbName) {
    try {
      hiveMetaStoreClient.createDatabase(new Database(dbName, null, getDatabasePath(dbName), new HashMap<>()));
    } catch (Exception e) {
      throw new RuntimeException("Cannot create database: " + dbName, e);
    }
  }

  public String getDatabasePath(String dbName) {
    File dbDir = new File(hiveLocalDir, dbName + ".db");
    return dbDir.getPath();
  }

  private TServer newThriftServer(TServerSocket socket, HiveConf conf) throws Exception {
    HiveConf serverConf = new HiveConf(conf);
    serverConf.set(HiveConf.ConfVars.METASTORECONNECTURLKEY.varname, "jdbc:derby:" + getDerbyPath() + ";create=true");
    HiveMetaStore.HMSHandler baseHandler = new HiveMetaStore.HMSHandler("new db based metaserver", serverConf);
    IHMSHandler handler = RetryingHMSHandler.getProxy(serverConf, baseHandler, false);

    TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket).processor(new TSetIpAddressProcessor<>(handler))
        .transportFactory(new TTransportFactory()).protocolFactory(new TBinaryProtocol.Factory()).minWorkerThreads(3)
        .maxWorkerThreads(5);

    return new TThreadPoolServer(args);
  }

  private HiveConf newHiveConf(int port) {
    HiveConf newHiveConf = new HiveConf(new Configuration(), TestHiveMetastore.class);
    newHiveConf.set(HiveConf.ConfVars.METASTOREURIS.varname, "thrift://localhost:" + port);
    newHiveConf.set(HiveConf.ConfVars.METASTOREWAREHOUSE.varname, "file:" + hiveLocalDir.getAbsolutePath());
    return newHiveConf;
  }

  private void setupMetastoreDB(String dbURL) throws SQLException, IOException {
    Connection connection = DriverManager.getConnection(dbURL);
    ScriptRunner scriptRunner = new ScriptRunner(connection, true, true);

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("hive-schema.derby.sql");
    try (Reader reader = new InputStreamReader(inputStream)) {
      scriptRunner.runScript(reader);
    }
  }

  private String getDerbyPath() {
    File metastoreDB = new File(hiveLocalDir, "metastore_db");
    return metastoreDB.getPath();
  }

  private void createHiveSiteXml(File target, int port) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<configuration>");
    sb.append(getXmlForProperty(HiveConf.ConfVars.METASTOREURIS.varname, "thrift://localhost:" + port));
    sb.append(
        getXmlForProperty(HiveConf.ConfVars.METASTOREWAREHOUSE.varname, "file:" + hiveLocalDir.getAbsolutePath()));
    sb.append("</configuration>");
    Files.write(target.toPath(), Arrays.asList(sb.toString()));
  }

  private String getXmlForProperty(String name, String value) {
    return String.format("<property><name>%s</name><value>%s</value></property>", name, value);
  }

  private void addURLToClassLoader(URL url) {
    ClassLoader newClassLoader = new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
    Thread.currentThread().setContextClassLoader(newClassLoader);
  }

  public HiveConf hiveConf() {
    return hiveConf;
  }
}
