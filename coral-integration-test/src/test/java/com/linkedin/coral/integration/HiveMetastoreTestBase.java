// Copyright 2019-2020 LinkedIn Corporation. All rights reserved.
// Licensed under the BSD-2 Clause license.
// See LICENSE in the project root for license information.
package com.linkedin.coral.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IHMSHandler;
import org.apache.hadoop.hive.metastore.RetryingHMSHandler;
import org.apache.hadoop.hive.metastore.TSetIpAddressProcessor;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * Base class for integration tests that require a standalone HiveMetastore 2.0.
 * This class sets up a single embedded Derby database and HMS for Hive tables.
 * For Iceberg tables, use SparkIcebergTestBase which adds TestHiveMetastore.
 */
public class HiveMetastoreTestBase {

  protected HiveMetaStoreClient metastoreClient;
  protected HiveConf hiveConf;
  protected Path warehouseDir;
  protected Path metastoreDbDir;
  protected String hiveMetastoreUri;
  private int hiveMetastorePort = 9085;
  
  // Thrift server components for in-memory HMS
  private TServer hiveMetastoreServer;
  private ExecutorService hiveExecutorService;
  private HiveMetaStore.HMSHandler hiveBaseHandler;

  @BeforeClass
  public void setupHiveMetastore() throws Exception {
    // Create temporary directories for Hive catalog
    warehouseDir = Files.createTempDirectory("hive-warehouse");
    metastoreDbDir = Files.createTempDirectory("hive-metastore-db");
    
    // Create HiveConf for Hive catalog
    hiveConf = createHiveConf("hive", metastoreDbDir, warehouseDir);

    // Start in-memory HiveMetaStore service (use port 0 for auto-assignment)
    hiveMetastorePort = startInMemoryMetastoreService(hiveConf);
    
    // Set the actual URI after server is started (with dynamic port)
    hiveMetastoreUri = "thrift://localhost:" + hiveMetastorePort;
    
    // Update config with actual URI
    hiveConf.set("hive.metastore.uris", hiveMetastoreUri);

    // Only create metastore client if not overridden by subclass
    if (shouldCreateMetastoreClient()) {
      createHiveMetastoreClient();
    }
  }
  
  /**
   * Start an in-memory HiveMetaStore Thrift service.
   * This starts a real HMS service listening on a dynamically assigned port, running within the test JVM.
   *
   * @param conf HiveConf for this metastore
   * @return the actual port number that was assigned
   */
  private int startInMemoryMetastoreService(HiveConf conf) throws Exception {
    // Create server socket with port 0 (auto-assign available port)
    TServerSocket socket = new TServerSocket(0);
    int port = socket.getServerSocket().getLocalPort();
    
    // Set metastore URI in config
    conf.set(HiveConf.ConfVars.METASTOREURIS.varname, "thrift://localhost:" + port);
    conf.set(HiveConf.ConfVars.METASTORE_TRY_DIRECT_SQL.varname, "false");
    
    // Create HMS handler
    hiveBaseHandler = new HiveMetaStore.HMSHandler("metastore", conf);
    IHMSHandler handler = RetryingHMSHandler.getProxy(conf, hiveBaseHandler, false);
    
    // Create Thrift server
    TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket)
        .processor(new TSetIpAddressProcessor<>(handler))
        .transportFactory(new TTransportFactory())
        .protocolFactory(new TBinaryProtocol.Factory())
        .minWorkerThreads(2)
        .maxWorkerThreads(2);
    
    hiveMetastoreServer = new TThreadPoolServer(args);
    
    // Start server in background thread
    hiveExecutorService = Executors.newSingleThreadExecutor();
    hiveExecutorService.submit(() -> hiveMetastoreServer.serve());
    
    // Wait for the service to start
    Thread.sleep(2000);
    
    System.out.println("Started in-memory HiveMetaStore on port " + port + " for Hive catalog");
    
    // Create default database using the started service
    createDefaultDatabase(conf);
    
    return port;
  }
  
  /**
   * Create default database in the metastore
   */
  private void createDefaultDatabase(HiveConf conf) {
    try {
      // Create a temporary client just for creating the default database
      HiveMetaStoreClient tempClient = new HiveMetaStoreClient(conf);
      try {
        // Check if default database already exists
        tempClient.getDatabase("default");
      } catch (Exception e) {
        // Default database doesn't exist, create it
        String warehousePath = conf.get("hive.metastore.warehouse.dir");
        Database defaultDb = new Database("default", "Default Hive database", warehousePath, null);
        tempClient.createDatabase(defaultDb);
        System.out.println("Created default database for Hive catalog");
      } finally {
        tempClient.close();
      }
    } catch (Exception e) {
      System.err.println("Failed to create default database for Hive catalog: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  /**
   * Create a HiveConf with embedded metastore configuration.
   *
   * @param catalogName Name identifier for this catalog (e.g., "hive")
   * @param metastoreDir Directory for Derby metastore database
   * @param warehouseDir Directory for warehouse
   * @return Configured HiveConf instance
   */
  protected HiveConf createHiveConf(String catalogName, Path metastoreDir, Path warehouseDir) {
    HiveConf conf = new HiveConf();

    // Each catalog gets its own Derby database (this ensures complete isolation)
    conf.set("javax.jdo.option.ConnectionURL",
        "jdbc:derby:;databaseName=" + metastoreDir.toAbsolutePath().toString() + "/metastore_db;create=true");
    conf.set("javax.jdo.option.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver");

    // Set warehouse directory for this catalog
    conf.set("hive.metastore.warehouse.dir", warehouseDir.toAbsolutePath().toUri().toString());

    // Derby/DataNucleus configuration
    conf.set("hive.metastore.schema.verification", "false");
    conf.set("datanucleus.schema.autoCreateAll", "true");
    conf.set("hive.metastore.schema.verification.record.version", "false");

    // Add catalog identifier for logging/debugging
    conf.set("hive.metastore.client.identifier", catalogName);

    return conf;
  }

  /**
   * Override this method to prevent metastore client creation in subclasses
   * that use their own metastore access (e.g., through Spark)
   */
  protected boolean shouldCreateMetastoreClient() {
    return true;
  }

  /**
   * Create the HiveMetaStoreClient for the Hive catalog.
   */
  protected void createHiveMetastoreClient() throws TException {
    try {
      metastoreClient = new HiveMetaStoreClient(hiveConf);
    } catch (MetaException e) {
      throw new RuntimeException("Failed to create HiveMetaStoreClient for Hive catalog", e);
    }
  }

  @AfterClass
  public void tearDownHiveMetastore() throws IOException {
    // Close Hive metastore client
    if (metastoreClient != null) {
      try {
        metastoreClient.close();
      } catch (Exception e) {
        System.err.println("Error closing Hive metastore client: " + e.getMessage());
      }
      metastoreClient = null;
    }
    
    // Stop Hive Thrift server
    if (hiveMetastoreServer != null) {
      hiveMetastoreServer.stop();
    }
    if (hiveExecutorService != null) {
      hiveExecutorService.shutdown();
    }
    if (hiveBaseHandler != null) {
      try {
        hiveBaseHandler.shutdown();
      } catch (Exception e) {
        System.err.println("Error shutting down Hive base handler: " + e.getMessage());
      }
    }

    // Shutdown Derby database system-wide
    try {
      java.sql.DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch (java.sql.SQLException e) {
      // Derby throws SQLException on successful shutdown
      // SQLState 08006 or XJ015 indicates successful shutdown
      if (e.getSQLState() != null && 
          (e.getSQLState().equals("XJ015") || e.getSQLState().equals("08006"))) {
        // Expected - Derby shut down successfully
      } else {
        System.err.println("Derby shutdown warning (SQLState " + e.getSQLState() + "): " + e.getMessage());
      }
    }

    // Wait for Derby to fully release locks
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Clean up temporary directories
    if (warehouseDir != null && Files.exists(warehouseDir)) {
      try {
        Files.walk(warehouseDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      } catch (Exception e) {
        System.err.println("Error deleting warehouse dir: " + e.getMessage());
      }
    }

    if (metastoreDbDir != null && Files.exists(metastoreDbDir)) {
      try {
        Files.walk(metastoreDbDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      } catch (Exception e) {
        System.err.println("Error deleting metastore DB dir: " + e.getMessage());
      }
    }
  }

  /**
   * Get the warehouse directory path
   */
  protected String getWarehouseDir() {
    return warehouseDir.toAbsolutePath().toUri().toString();
  }

  /**
   * Get the Hive metastore URI
   */
  protected String getHiveMetastoreUri() {
    return hiveMetastoreUri;
  }
}
