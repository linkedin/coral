/**
 * Copyright 2018-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Types;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.coral.common.catalog.CoralTable;
import com.linkedin.coral.common.catalog.IcebergCoralTable;
import com.linkedin.coral.hive.hive2rel.HiveToRelConverter;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;


/**
 * Test case for Iceberg table with timestamp precision handling.
 * Verifies that Iceberg timestamp(6) columns are correctly converted to RelNode.
 */
public class IcebergTableConverterTest {

  private HiveToRelConverter converter;
  private TestCoralCatalog testCatalog;
  private static final String TEST_DB = "test_db";
  private static final String TEST_TABLE = "test_iceberg_table";

  @BeforeClass
  public void setup() {
    // Create test catalog with Iceberg table
    testCatalog = new TestCoralCatalog();

    // Create mock Iceberg table with timestamp(6) column using Mockito
    org.apache.iceberg.Table mockIcebergTable = createMockIcebergTable();
    IcebergCoralTable icebergCoralTable = new IcebergCoralTable(mockIcebergTable);

    testCatalog.addTable(TEST_DB, TEST_TABLE, icebergCoralTable);

    // Create converter with test catalog
    converter = new HiveToRelConverter(testCatalog);
  }

  /**
   * Creates a mock Iceberg table with a timestamp(6) column using Mockito.
   * Schema: (id BIGINT, event_time TIMESTAMP, name STRING)
   */
  private org.apache.iceberg.Table createMockIcebergTable() {
    // Define Iceberg schema with timestamp column
    // TimestampType.withoutZone() represents microsecond precision (6 digits)
    Schema icebergSchema = new Schema(Types.NestedField.required(1, "id", Types.LongType.get()),
        Types.NestedField.required(2, "event_time", Types.TimestampType.withoutZone()),
        Types.NestedField.optional(3, "name", Types.StringType.get()));

    // Mock the Iceberg Table interface using Mockito
    org.apache.iceberg.Table mockTable = mock(org.apache.iceberg.Table.class);

    // Stub necessary methods
    when(mockTable.schema()).thenReturn(icebergSchema);
    when(mockTable.properties()).thenReturn(Collections.emptyMap());
    when(mockTable.name()).thenReturn(TEST_DB + "." + TEST_TABLE);

    return mockTable;
  }

  /**
   * Test that an Iceberg table with TIMESTAMP column is correctly converted to RelNode
   * with precision 6 (microseconds).
   */
  @Test
  public void testIcebergTableWithTimestampPrecision() {
    // Convert SQL query to RelNode
    String sql = String.format("SELECT * FROM %s.%s", TEST_DB, TEST_TABLE);
    RelNode relNode = converter.convertSql(sql);

    // Print RelNode structure for debugging
    System.out.println("\nField types in RelNode:");
    for (RelDataTypeField f : relNode.getRowType().getFieldList()) {
      System.out.println(f.getName() + ": " + f.getType());
    }

    System.out.println("\nRelNode structure:");
    System.out.println(org.apache.calcite.plan.RelOptUtil.toString(relNode));

    // Verify RelNode structure
    assertNotNull(relNode, "RelNode should not be null");

    // Check that we have 3 columns: id, event_time, name
    List<RelDataTypeField> fields = relNode.getRowType().getFieldList();
    assertEquals(fields.size(), 3, "Should have 3 columns");

    // Verify column names
    assertEquals(fields.get(0).getName(), "id");
    assertEquals(fields.get(1).getName(), "event_time");
    assertEquals(fields.get(2).getName(), "name");

    // Verify column types
    assertEquals(fields.get(0).getType().getSqlTypeName(), SqlTypeName.BIGINT);
    assertEquals(fields.get(2).getType().getSqlTypeName(), SqlTypeName.VARCHAR);

    // CRITICAL: Verify that TIMESTAMP has precision 6 (microseconds)
    RelDataType timestampType = fields.get(1).getType();
    assertEquals(timestampType.getSqlTypeName(), SqlTypeName.TIMESTAMP, "event_time should be TIMESTAMP");
    assertEquals(timestampType.getPrecision(), 6, "Timestamp should have precision 6 (microseconds)");
  }

  /**
   * Test IcebergCoralTable conversion and metadata access.
   */
  @Test
  public void testIcebergCoralTableConversion() {
    // Direct test of IcebergCoralTable to verify metadata
    CoralTable coralTable = testCatalog.getTable(TEST_DB, TEST_TABLE);

    assertNotNull(coralTable, "CoralTable should not be null");
    assertTrue(coralTable instanceof IcebergCoralTable, "CoralTable should be IcebergCoralTable");

    // Verify table metadata
    assertEquals(coralTable.name(), TEST_DB + "." + TEST_TABLE);
    assertEquals(coralTable.tableType(), com.linkedin.coral.common.catalog.TableType.TABLE);

    // Verify properties
    Map<String, String> properties = coralTable.properties();
    assertNotNull(properties, "Properties should not be null");
  }

  /**
   * Test catalog implementation that stores Iceberg tables.
   * Only implements CoralCatalog (not HiveMetastoreClient).
   */
  private static class TestCoralCatalog implements com.linkedin.coral.common.catalog.CoralCatalog {
    private final Map<String, Map<String, CoralTable>> namespaces = new HashMap<>();

    public void addTable(String namespaceName, String tableName, CoralTable coralTable) {
      namespaces.computeIfAbsent(namespaceName, k -> new HashMap<>()).put(tableName, coralTable);
    }

    @Override
    public CoralTable getTable(String namespaceName, String tableName) {
      Map<String, CoralTable> tables = namespaces.get(namespaceName);
      return tables != null ? tables.get(tableName) : null;
    }

    @Override
    public List<String> getAllTables(String namespaceName) {
      Map<String, CoralTable> tables = namespaces.get(namespaceName);
      return tables != null ? Collections.unmodifiableList(new java.util.ArrayList<>(tables.keySet()))
          : Collections.emptyList();
    }

    @Override
    public boolean namespaceExists(String namespaceName) {
      return namespaces.containsKey(namespaceName);
    }

    @Override
    public List<String> getAllNamespaces() {
      return Collections.unmodifiableList(new java.util.ArrayList<>(namespaces.keySet()));
    }
  }
}
