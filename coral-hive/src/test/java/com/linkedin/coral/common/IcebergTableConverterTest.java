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

import com.linkedin.coral.common.catalog.Dataset;
import com.linkedin.coral.common.catalog.IcebergDataset;
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
    IcebergDataset icebergDataset = new IcebergDataset(mockIcebergTable, TEST_DB, TEST_TABLE);

    testCatalog.addDataset(TEST_DB, TEST_TABLE, icebergDataset);

    // Create converter with test catalog
    converter = new HiveToRelConverter(testCatalog);
  }

  /**
   * Creates a mock Iceberg table with a timestamp(6) column using Mockito.
   * Schema: (id BIGINT, event_time TIMESTAMP, name STRING)
   */
  private org.apache.iceberg.Table createMockIcebergTable() {
    // Create Iceberg schema with timestamp column
    Schema icebergSchema = new Schema(Types.NestedField.required(1, "id", Types.LongType.get()),
        Types.NestedField.required(2, "event_time", Types.TimestampType.withoutZone()),
        Types.NestedField.optional(3, "name", Types.StringType.get()));

    // Mock Iceberg Table using Mockito
    org.apache.iceberg.Table mockTable = mock(org.apache.iceberg.Table.class);
    when(mockTable.schema()).thenReturn(icebergSchema);
    when(mockTable.properties()).thenReturn(Collections.emptyMap());

    return mockTable;
  }

  @Test
  public void testIcebergTableWithTimestampPrecision() {
    // Convert SQL query that references the Iceberg table
    String sql = String.format("SELECT * FROM %s.%s", TEST_DB, TEST_TABLE);

    // Get RelNode from the converter
    RelNode relNode = converter.convertSql(sql);

    assertNotNull(relNode, "RelNode should not be null");

    // Verify the RelNode structure
    RelDataType rowType = relNode.getRowType();
    assertNotNull(rowType, "Row type should not be null");

    // Verify we have 3 columns
    List<RelDataTypeField> fields = rowType.getFieldList();
    assertEquals(fields.size(), 3, "Should have 3 columns");

    // Print field names and types (equivalent to Scala viewrel.getRowType.getFieldList.asScala.foreach)
    System.out.println("\nField types in RelNode:");
    for (RelDataTypeField f : fields) {
      System.out.println(f.getName() + ": " + f.getType());
    }

    // Verify column names
    assertEquals(fields.get(0).getName(), "id");
    assertEquals(fields.get(1).getName(), "event_time");
    assertEquals(fields.get(2).getName(), "name");

    // Verify column types
    assertEquals(fields.get(0).getType().getSqlTypeName(), SqlTypeName.BIGINT, "id should be BIGINT");
    assertEquals(fields.get(1).getType().getSqlTypeName(), SqlTypeName.TIMESTAMP, "event_time should be TIMESTAMP");
    assertEquals(fields.get(2).getType().getSqlTypeName(), SqlTypeName.VARCHAR, "name should be VARCHAR");

    // Verify timestamp precision is 6 (microseconds)
    RelDataType timestampType = fields.get(1).getType();
    assertEquals(timestampType.getPrecision(), 6, "Timestamp should have precision 6");

    System.out.println("\nRelNode structure:");
    System.out.println(org.apache.calcite.plan.RelOptUtil.toString(relNode));
  }

  @Test
  public void testIcebergTableProjection() {
    // Test projection with timestamp column
    String sql = String.format("SELECT event_time, id FROM %s.%s WHERE id > 100", TEST_DB, TEST_TABLE);

    RelNode relNode = converter.convertSql(sql);
    assertNotNull(relNode);

    // Verify projected columns
    RelDataType rowType = relNode.getRowType();
    List<RelDataTypeField> fields = rowType.getFieldList();

    // Print field names and types
    System.out.println("\nProjected field types in RelNode:");
    for (RelDataTypeField f : fields) {
      System.out.println(f.getName() + ": " + f.getType());
    }

    assertEquals(fields.size(), 2, "Should have 2 projected columns");
    assertEquals(fields.get(0).getName(), "event_time");
    assertEquals(fields.get(1).getName(), "id");

    // Verify timestamp precision is preserved in projection
    assertEquals(fields.get(0).getType().getSqlTypeName(), SqlTypeName.TIMESTAMP);
    assertEquals(fields.get(0).getType().getPrecision(), 6, "Timestamp precision should be preserved in projection");
  }

  @Test
  public void testIcebergDatasetConversion() {
    // Direct test of IcebergDataset to verify schema conversion
    Dataset dataset = testCatalog.getDataset(TEST_DB, TEST_TABLE);

    assertNotNull(dataset, "Dataset should not be null");
    assertTrue(dataset instanceof IcebergDataset, "Dataset should be IcebergDataset");
    assertEquals(dataset.name(), TEST_DB + "." + TEST_TABLE);
    assertEquals(dataset.tableType(), com.linkedin.coral.common.catalog.TableType.TABLE);

    // Verify Avro schema can be generated
    org.apache.avro.Schema avroSchema = dataset.avroSchema();
    assertNotNull(avroSchema, "Avro schema should not be null");
    assertEquals(avroSchema.getType(), org.apache.avro.Schema.Type.RECORD);

    // Verify fields in Avro schema
    List<org.apache.avro.Schema.Field> avroFields = avroSchema.getFields();
    assertEquals(avroFields.size(), 3, "Should have 3 fields in Avro schema");

    // Find timestamp field
    org.apache.avro.Schema.Field timestampField =
        avroFields.stream().filter(f -> f.name().equals("event_time")).findFirst().orElse(null);

    assertNotNull(timestampField, "Timestamp field should exist in Avro schema");
  }

  /**
   * Simple test catalog implementation for CoralCatalog.
   * Only implements CoralCatalog interface, not HiveMetastoreClient.
   */
  private static class TestCoralCatalog implements com.linkedin.coral.common.catalog.CoralCatalog {
    private final Map<String, Map<String, Dataset>> databases = new HashMap<>();

    public void addDataset(String dbName, String tableName, Dataset dataset) {
      databases.computeIfAbsent(dbName, k -> new HashMap<>()).put(tableName, dataset);
    }

    @Override
    public Dataset getDataset(String dbName, String tableName) {
      Map<String, Dataset> tables = databases.get(dbName);
      return tables != null ? tables.get(tableName) : null;
    }

    @Override
    public List<String> getAllDatasets(String dbName) {
      Map<String, Dataset> tables = databases.get(dbName);
      return tables != null ? Collections.unmodifiableList(new java.util.ArrayList<>(tables.keySet()))
          : Collections.emptyList();
    }

    @Override
    public List<String> getAllDatabases() {
      return Collections.unmodifiableList(new java.util.ArrayList<>(databases.keySet()));
    }

    @Override
    public boolean namespaceExists(String dbName) {
      return databases.containsKey(dbName);
    }
  }

}
