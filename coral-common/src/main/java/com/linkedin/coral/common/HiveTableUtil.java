/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.linkedin.coral.common.catalog.TableType;


/**
 * Utility class for working with Hive tables in the context of Coral.
 * Provides helper methods to extract schema, properties, and metadata
 * from Hive table objects.
 *
 * <p><b>Schema Conversion Strategy:</b></p>
 * <p>This class uses a two-tier approach for obtaining Avro schemas from Hive tables:</p>
 * <ol>
 *   <li><b>avro.schema.literal property:</b> If the Hive table has an 'avro.schema.literal' property,
 *       this is the authoritative schema definition. This property is set by Avro-backed Hive tables
 *       (tables using AvroSerDe) and contains the complete Avro schema as a JSON string. This is
 *       preferred because it preserves the exact schema with all Avro-specific features (unions,
 *       logical types, etc.) that were used when the table was created.</li>
 *   <li><b>Hive schema conversion:</b> If no avro.schema.literal is found, the class falls back to
 *       converting Hive column types to Avro. This conversion is basic and handles common types
 *       but may not capture all nuances of complex types. For production use with complex types,
 *       consider using coral-schema module's SchemaUtilities.</li>
 * </ol>
 *
 * <p>Note: For advanced schema operations with full type support and proper handling of
 * complex types, use coral-schema module's SchemaUtilities and ViewToAvroSchemaConverter.</p>
 */
public class HiveTableUtil {

  private HiveTableUtil() {
    // Utility class, prevent instantiation
  }

  /**
   * Converts Hive table schema to Avro Schema.
   * This is a simplified conversion that handles common types.
   *
   * Note: For production use with complex types and full Avro schema support,
   * consider using ViewToAvroSchemaConverter from coral-schema module.
   *
   * @param table Hive table object
   * @return Avro Schema representation of the table
   */
  public static Schema getAvroSchema(Table table) {
    if (table == null) {
      return null;
    }

    // Check if Avro schema is available in table properties
    String avroSchemaStr = table.getParameters() != null ? table.getParameters().get("avro.schema.literal") : null;
    if (avroSchemaStr != null && !avroSchemaStr.isEmpty()) {
      return new Schema.Parser().parse(avroSchemaStr);
    }

    // Convert from Hive schema
    return convertHiveSchemaToAvro(table);
  }

  /**
   * Converts Hive table schema to Avro schema using basic type mappings.
   *
   * @param table Hive table
   * @return Avro Schema
   */
  private static Schema convertHiveSchemaToAvro(Table table) {
    String tableName = table.getTableName();
    String namespace = table.getDbName();

    List<Schema.Field> fields = new ArrayList<>();

    // Process regular columns
    if (table.getSd() != null && table.getSd().getCols() != null) {
      for (FieldSchema hiveField : table.getSd().getCols()) {
        Schema.Field field = convertFieldSchemaToAvroField(hiveField);
        if (field != null) {
          fields.add(field);
        }
      }
    }

    // Process partition columns
    if (table.getPartitionKeys() != null) {
      for (FieldSchema hiveField : table.getPartitionKeys()) {
        Schema.Field field = convertFieldSchemaToAvroField(hiveField);
        if (field != null) {
          fields.add(field);
        }
      }
    }

    // Create record schema using old Avro API
    Schema recordSchema = Schema.createRecord(tableName, null, namespace, false);
    recordSchema.setFields(fields);
    return recordSchema;
  }

  /**
   * Converts a Hive FieldSchema to an Avro Field.
   *
   * @param hiveField Hive field schema
   * @return Avro Field
   */
  private static Schema.Field convertFieldSchemaToAvroField(FieldSchema hiveField) {
    String fieldName = hiveField.getName().toLowerCase();
    String hiveType = hiveField.getType();

    try {
      TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(hiveType);
      Schema avroFieldSchema = convertTypeInfoToAvroSchema(typeInfo);

      // Make field nullable by default using List
      List<Schema> unionTypes = new ArrayList<>();
      unionTypes.add(Schema.create(Schema.Type.NULL));
      unionTypes.add(avroFieldSchema);
      Schema nullableSchema = Schema.createUnion(unionTypes);

      // Use null as default value directly (not a constant)
      return new Schema.Field(fieldName, nullableSchema, null, null);
    } catch (Exception e) {
      // If conversion fails, use string type as fallback
      Schema stringSchema = Schema.create(Schema.Type.STRING);
      List<Schema> unionTypes = new ArrayList<>();
      unionTypes.add(Schema.create(Schema.Type.NULL));
      unionTypes.add(stringSchema);
      Schema nullableStringSchema = Schema.createUnion(unionTypes);
      return new Schema.Field(fieldName, nullableStringSchema, null, null);
    }
  }

  /**
   * Converts Hive TypeInfo to Avro Schema (basic types only).
   * Complex types like STRUCT, ARRAY, MAP use simplified conversions.
   *
   * @param typeInfo Hive TypeInfo
   * @return Avro Schema
   */
  private static Schema convertTypeInfoToAvroSchema(TypeInfo typeInfo) {
    String typeName = typeInfo.getTypeName().toLowerCase();

    // Handle primitive types
    if (typeName.equals("string") || typeName.startsWith("varchar") || typeName.startsWith("char")) {
      return Schema.create(Schema.Type.STRING);
    } else if (typeName.equals("int") || typeName.equals("integer")) {
      return Schema.create(Schema.Type.INT);
    } else if (typeName.equals("bigint") || typeName.equals("long")) {
      return Schema.create(Schema.Type.LONG);
    } else if (typeName.equals("float")) {
      return Schema.create(Schema.Type.FLOAT);
    } else if (typeName.equals("double")) {
      return Schema.create(Schema.Type.DOUBLE);
    } else if (typeName.equals("boolean")) {
      return Schema.create(Schema.Type.BOOLEAN);
    } else if (typeName.equals("binary")) {
      return Schema.create(Schema.Type.BYTES);
    } else if (typeName.startsWith("decimal")) {
      // Simplified: represent decimal as string
      return Schema.create(Schema.Type.STRING);
    } else if (typeName.equals("date") || typeName.equals("timestamp")) {
      // Simplified: represent date/timestamp as string
      return Schema.create(Schema.Type.STRING);
    } else if (typeName.startsWith("array")) {
      // Simplified: array of strings
      return Schema.createArray(Schema.create(Schema.Type.STRING));
    } else if (typeName.startsWith("map")) {
      // Simplified: map with string values
      return Schema.createMap(Schema.create(Schema.Type.STRING));
    } else if (typeName.startsWith("struct")) {
      // Simplified: represent struct as string
      return Schema.create(Schema.Type.STRING);
    } else {
      // Default fallback
      return Schema.create(Schema.Type.STRING);
    }
  }

  /**
   * Extracts table properties from Hive table.
   *
   * @param table Hive table object
   * @return Map of table properties, or empty map if none exist
   */
  public static Map<String, String> properties(Table table) {
    if (table == null || table.getParameters() == null) {
      return Collections.emptyMap();
    }
    return table.getParameters();
  }

  /**
   * Converts Hive table type string to Coral TableType enum.
   *
   * @param hiveTableType Hive table type string
   * @return TableType enum value
   */
  public static TableType tableType(String hiveTableType) {
    return TableType.fromHiveTableType(hiveTableType);
  }
}

