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

import org.apache.hadoop.conf.Configuration;
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
 * This class sets up an embedded Derby database and HiveMetaStoreClient.
 */
public class HiveMetastoreTestBase {

  protected HiveMetaStoreClient metastoreClient;
  protected HiveMetaStoreClient icebergMetastoreClient;
  protected HiveConf hiveConf;
  protected HiveConf icebergHiveConf;
  protected Path warehouseDir;
  protected Path metastoreDbDir;
  protected Path icebergWarehouseDir;
  protected Path icebergMetastoreDbDir;
  protected String hiveMetastoreUri;
  protected String icebergMetastoreUri;
  private int hiveMetastorePort = 9085;
  private int icebergMetastorePort = 9087;
  
  // Thrift server components for in-memory HMS
  private TServer hiveMetastoreServer;
  private TServer icebergMetastoreServer;
  private ExecutorService hiveExecutorService;
  private ExecutorService icebergExecutorService;
  private HiveMetaStore.HMSHandler hiveBaseHandler;
  private HiveMetaStore.HMSHandler icebergBaseHandler;

  @BeforeClass
  public void setupHiveMetastore() throws Exception {
    // Create temporary directories for Hive catalog
    warehouseDir = Files.createTempDirectory("hive-warehouse");
    metastoreDbDir = Files.createTempDirectory("hive-metastore-db");
    
    // Create temporary directories for Iceberg catalog
    icebergWarehouseDir = Files.createTempDirectory("iceberg-warehouse");
    icebergMetastoreDbDir = Files.createTempDirectory("iceberg-metastore-db");
    
    // Create separate HiveConf for Hive catalog
    hiveConf = createHiveConf("hive", metastoreDbDir, warehouseDir);
    
    // Create separate HiveConf for Iceberg catalog
    icebergHiveConf = createHiveConf("iceberg", icebergMetastoreDbDir, icebergWarehouseDir);

    // Start in-memory HiveMetaStore services (use port 0 for auto-assignment)
    hiveMetastorePort = startInMemoryMetastoreService(hiveConf, true);
    icebergMetastorePort = startInMemoryMetastoreService(icebergHiveConf, false);
    
    // Set the actual URIs after servers are started (with dynamic ports)
    hiveMetastoreUri = "thrift://localhost:" + hiveMetastorePort;
    icebergMetastoreUri = "thrift://localhost:" + icebergMetastorePort;
    
    // Update configs with actual URIs
    hiveConf.set("hive.metastore.uris", hiveMetastoreUri);
    icebergHiveConf.set("hive.metastore.uris", icebergMetastoreUri);

    // Only create metastore clients if not overridden by subclass
    if (shouldCreateMetastoreClient()) {
      createHiveMetastoreClient();
      createIcebergMetastoreClient();
    }
  }
  
  /**
   * Start an in-memory HiveMetaStore Thrift service.
   * This starts a real HMS service listening on a dynamically assigned port, running within the test JVM.
   *
   * @param conf HiveConf for this metastore
   * @param isHiveCatalog true if this is for Hive catalog, false for Iceberg catalog
   * @return the actual port number that was assigned
   */
  private int startInMemoryMetastoreService(HiveConf conf, boolean isHiveCatalog) throws Exception {
    // Create server socket with port 0 (auto-assign available port)
    TServerSocket socket = new TServerSocket(0);
    int port = socket.getServerSocket().getLocalPort();
    
    // Set metastore URI in config
    conf.set(HiveConf.ConfVars.METASTOREURIS.varname, "thrift://localhost:" + port);
    conf.set(HiveConf.ConfVars.METASTORE_TRY_DIRECT_SQL.varname, "false");
    
    // Create HMS handler
    HiveMetaStore.HMSHandler baseHandler = new HiveMetaStore.HMSHandler("metastore", conf);
    IHMSHandler handler = RetryingHMSHandler.getProxy(conf, baseHandler, false);
    
    // Create Thrift server
    TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket)
        .processor(new TSetIpAddressProcessor<>(handler))
        .transportFactory(new TTransportFactory())
        .protocolFactory(new TBinaryProtocol.Factory())
        .minWorkerThreads(2)
        .maxWorkerThreads(2);
    
    TServer server = new TThreadPoolServer(args);
    
    // Start server in background thread
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.serve());
    
    // Store references for cleanup
    if (isHiveCatalog) {
      hiveMetastoreServer = server;
      hiveExecutorService = executorService;
      hiveBaseHandler = baseHandler;
    } else {
      icebergMetastoreServer = server;
      icebergExecutorService = executorService;
      icebergBaseHandler = baseHandler;
    }
    
    // Wait for the service to start
    Thread.sleep(2000);
    
    System.out.println("Started in-memory HiveMetaStore on port " + port + " for " + (isHiveCatalog ? "Hive" : "Iceberg") + " catalog");
    
    // Create default database using the started service
    createDefaultDatabase(conf, isHiveCatalog);
    
    return port;
  }
  
  /**
   * Create default database in the metastore.
   */
  private void createDefaultDatabase(HiveConf conf, boolean isHiveCatalog) {
    try {
      HiveMetaStoreClient client = new HiveMetaStoreClient(conf);
      try {
        client.getDatabase("default");
        System.out.println("Default database already exists for " + (isHiveCatalog ? "Hive" : "Iceberg") + " catalog");
      } catch (Exception e) {
        // Database doesn't exist, create it
        Database db = new Database();
        db.setName("default");
        db.setDescription("Default " + (isHiveCatalog ? "Hive" : "Iceberg") + " database");
        db.setLocationUri((isHiveCatalog ? warehouseDir : icebergWarehouseDir).toAbsolutePath().toUri().toString() + "/default.db");
        client.createDatabase(db);
        System.out.println("Created default database for " + (isHiveCatalog ? "Hive" : "Iceberg") + " catalog");
      }
      client.close();
    } catch (Exception e) {
      System.err.println("Failed to create default database for " + (isHiveCatalog ? "Hive" : "Iceberg") + " catalog: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  /**
   * Create a HiveConf with embedded metastore configuration.
   *
   * @param catalogName Name identifier for this catalog (e.g., "hive", "iceberg")
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

    // Create default database if it doesn't exist
    try {
      metastoreClient.getDatabase("default");
    } catch (Exception e) {
      Database db = new Database();
      db.setName("default");
      db.setDescription("Default Hive database");
      db.setLocationUri(warehouseDir.toAbsolutePath().toUri().toString() + "/default.db");
      metastoreClient.createDatabase(db);
    }
  }

  /**
   * Create the HiveMetaStoreClient for the Iceberg catalog.
   */
  protected void createIcebergMetastoreClient() throws TException {
    try {
      icebergMetastoreClient = new HiveMetaStoreClient(icebergHiveConf);
    } catch (MetaException e) {
      throw new RuntimeException("Failed to create HiveMetaStoreClient for Iceberg catalog", e);
    }

    // Create default database if it doesn't exist
    try {
      icebergMetastoreClient.getDatabase("default");
    } catch (Exception e) {
      Database db = new Database();
      db.setName("default");
      db.setDescription("Default Iceberg database");
      db.setLocationUri(icebergWarehouseDir.toAbsolutePath().toUri().toString() + "/default.db");
      icebergMetastoreClient.createDatabase(db);
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
    
    // Close Iceberg metastore client
    if (icebergMetastoreClient != null) {
      try {
        icebergMetastoreClient.close();
      } catch (Exception e) {
        System.err.println("Error closing Iceberg metastore client: " + e.getMessage());
      }
      icebergMetastoreClient = null;
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
    
    // Stop Iceberg Thrift server
    if (icebergMetastoreServer != null) {
      icebergMetastoreServer.stop();
    }
    if (icebergExecutorService != null) {
      icebergExecutorService.shutdown();
    }
    if (icebergBaseHandler != null) {
      try {
        icebergBaseHandler.shutdown();
      } catch (Exception e) {
        System.err.println("Error shutting down Iceberg base handler: " + e.getMessage());
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
        System.err.println("Error deleting metastore db dir: " + e.getMessage());
      }
    }
    
    if (icebergWarehouseDir != null && Files.exists(icebergWarehouseDir)) {
      try {
        Files.walk(icebergWarehouseDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      } catch (Exception e) {
        System.err.println("Error deleting iceberg warehouse dir: " + e.getMessage());
      }
    }
    
    if (icebergMetastoreDbDir != null && Files.exists(icebergMetastoreDbDir)) {
      try {
        Files.walk(icebergMetastoreDbDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      } catch (Exception e) {
        System.err.println("Error deleting iceberg metastore db dir: " + e.getMessage());
      }
    }
  }

  /**
   * Get the HiveConf configured for the Hive catalog.
   *
   * @return HiveConf instance for Hive catalog
   */
  protected HiveConf getHiveConf() {
    return hiveConf;
  }
  
  /**
   * Get the HiveConf configured for the Iceberg catalog.
   *
   * @return HiveConf instance for Iceberg catalog
   */
  protected HiveConf getIcebergHiveConf() {
    return icebergHiveConf;
  }

  /**
   * Get the Hive metastore URI.
   *
   * @return Metastore URI for Hive catalog (empty string for embedded mode)
   */
  protected String getHiveMetastoreUri() {
    return hiveMetastoreUri;
  }

  /**
   * Get the Iceberg metastore URI.
   *
   * @return Metastore URI for Iceberg catalog (empty string for embedded mode)
   */
  protected String getIcebergMetastoreUri() {
    return icebergMetastoreUri;
  }

  /**
   * Get the shared metastore URI that all catalogs should use.
   * 
   * @deprecated Use getHiveMetastoreUri() or getIcebergMetastoreUri() for specific catalogs
   * @return Metastore URI (empty string for embedded mode)
   */
  @Deprecated
  protected String getMetastoreUri() {
    return hiveMetastoreUri;
  }

  /**
   * Get the HiveMetaStoreClient for the Hive catalog.
   *
   * @return HiveMetaStoreClient instance for Hive catalog
   */
  protected HiveMetaStoreClient getMetastoreClient() {
    return metastoreClient;
  }
  
  /**
   * Get the HiveMetaStoreClient for the Iceberg catalog.
   *
   * @return HiveMetaStoreClient instance for Iceberg catalog
   */
  protected HiveMetaStoreClient getIcebergMetastoreClient() {
    return icebergMetastoreClient;
  }

  /**
   * Get the warehouse directory path as absolute URI.
   *
   * @return Absolute URI to warehouse directory
   */
  protected String getWarehouseDir() {
    return warehouseDir.toAbsolutePath().toUri().toString();
  }
  
  /**
   * Get the Iceberg warehouse directory path as absolute URI.
   *
   * @return Absolute URI to Iceberg warehouse directory
   */
  protected String getIcebergWarehouseDir() {
    return icebergWarehouseDir.toAbsolutePath().toUri().toString();
  }
}

