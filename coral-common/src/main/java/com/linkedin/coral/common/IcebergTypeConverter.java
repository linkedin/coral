/**
 * Copyright 2017-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Type;
import org.apache.iceberg.types.Types;


/**
 * Converts Iceberg Schema and Types to Calcite RelDataType.
 * Preserves Iceberg type semantics including nullability and nested structures.
 * 
 * This converter provides native Iceberg schema support for Calcite, avoiding
 * lossy conversions through Hive type system.
 * 
 * Copied structure from TypeConverter for consistency.
 */
public class IcebergTypeConverter {

  private IcebergTypeConverter() {
    // Utility class
  }

  /**
   * Converts Iceberg Schema to Calcite RelDataType.
   * 
   * @param icebergSchema Iceberg table schema
   * @param tableName Table name for naming nested types
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the Iceberg schema
   */
  public static RelDataType convert(Schema icebergSchema, String tableName, RelDataTypeFactory typeFactory) {
    List<Types.NestedField> columns = icebergSchema.columns();
    List<RelDataType> fieldTypes = new ArrayList<>(columns.size());
    List<String> fieldNames = new ArrayList<>(columns.size());

    for (Types.NestedField field : columns) {
      fieldNames.add(field.name());

      // Convert field type using the main dispatcher
      RelDataType fieldType = convert(field.type(), typeFactory);

      // Handle nullability - Iceberg has explicit required/optional
      fieldType = typeFactory.createTypeWithNullability(fieldType, field.isOptional());

      fieldTypes.add(fieldType);
    }

    return typeFactory.createStructType(fieldTypes, fieldNames);
  }

  /**
   * Main dispatcher - converts Iceberg Type to Calcite RelDataType based on type category.
   * Similar to TypeConverter.convert(TypeInfo, RelDataTypeFactory).
   * 
   * @param icebergType Iceberg type
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the Iceberg type
   */
  public static RelDataType convert(Type icebergType, RelDataTypeFactory typeFactory) {
    Type.TypeID typeId = icebergType.typeId();

    switch (typeId) {
      case STRUCT:
        return convert((Types.StructType) icebergType, typeFactory);
      case LIST:
        return convert((Types.ListType) icebergType, typeFactory);
      case MAP:
        return convert((Types.MapType) icebergType, typeFactory);
      default:
        // Handle all primitive types
        return convertPrimitive(icebergType, typeFactory);
    }
  }

  /**
   * Converts Iceberg primitive types to Calcite RelDataType.
   * Handles all atomic types: BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, DATE, TIME, TIMESTAMP,
   * STRING, UUID, FIXED, BINARY, DECIMAL.
   * 
   * @param icebergType Iceberg primitive type
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the primitive type
   */
  public static RelDataType convertPrimitive(Type icebergType, RelDataTypeFactory typeFactory) {
    RelDataType convertedType = null;
    Type.TypeID typeId = icebergType.typeId();

    switch (typeId) {
      case BOOLEAN:
        convertedType = typeFactory.createSqlType(SqlTypeName.BOOLEAN);
        break;
      case INTEGER:
        convertedType = typeFactory.createSqlType(SqlTypeName.INTEGER);
        break;
      case LONG:
        convertedType = typeFactory.createSqlType(SqlTypeName.BIGINT);
        break;
      case FLOAT:
        convertedType = typeFactory.createSqlType(SqlTypeName.FLOAT);
        break;
      case DOUBLE:
        convertedType = typeFactory.createSqlType(SqlTypeName.DOUBLE);
        break;
      case DATE:
        convertedType = typeFactory.createSqlType(SqlTypeName.DATE);
        break;
      case TIME:
        convertedType = typeFactory.createSqlType(SqlTypeName.TIME);
        break;
      case TIMESTAMP:
        // Iceberg timestamp type - microsecond precision (6 digits)
        convertedType = typeFactory.createSqlType(SqlTypeName.TIMESTAMP, 6);
        break;
      case STRING:
        convertedType = typeFactory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);
        break;
      case UUID:
        // Represent UUID as CHAR(36) to preserve UUID semantics
        convertedType = typeFactory.createSqlType(SqlTypeName.CHAR, 36);
        break;
      case FIXED:
        Types.FixedType fixedType = (Types.FixedType) icebergType;
        convertedType = typeFactory.createSqlType(SqlTypeName.BINARY, fixedType.length());
        break;
      case BINARY:
        convertedType = typeFactory.createSqlType(SqlTypeName.VARBINARY, Integer.MAX_VALUE);
        break;
      case DECIMAL:
        Types.DecimalType decimalType = (Types.DecimalType) icebergType;
        convertedType = typeFactory.createSqlType(SqlTypeName.DECIMAL, decimalType.precision(), decimalType.scale());
        break;
      default:
        throw new UnsupportedOperationException(
            "Unsupported Iceberg primitive type: " + icebergType + " (TypeID: " + typeId + ")");
    }

    if (null == convertedType) {
      throw new RuntimeException("Unsupported Type : " + icebergType);
    }

    // Note: Unlike Hive's TypeConverter, we don't apply nullability here.
    // Nullability is handled at the field level based on Iceberg's required/optional flags.
    return convertedType;
  }

  /**
   * Converts Iceberg ListType to Calcite RelDataType.
   * 
   * @param listType Iceberg list type
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the list/array
   */
  public static RelDataType convert(Types.ListType listType, RelDataTypeFactory typeFactory) {
    // Recursively convert element type
    RelDataType elementType = convert(listType.elementType(), typeFactory);

    // Handle list element nullability - Iceberg has explicit element nullability
    elementType = typeFactory.createTypeWithNullability(elementType, listType.isElementOptional());

    return typeFactory.createArrayType(elementType, -1);
  }

  /**
   * Converts Iceberg MapType to Calcite RelDataType.
   * 
   * @param mapType Iceberg map type
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the map
   */
  public static RelDataType convert(Types.MapType mapType, RelDataTypeFactory typeFactory) {
    // Recursively convert key and value types
    RelDataType keyType = convert(mapType.keyType(), typeFactory);
    RelDataType valueType = convert(mapType.valueType(), typeFactory);

    // Iceberg map values can be required or optional
    valueType = typeFactory.createTypeWithNullability(valueType, mapType.isValueOptional());

    return typeFactory.createMapType(keyType, valueType);
  }

  /**
   * Converts Iceberg StructType to Calcite RelDataType.
   * 
   * @param structType Iceberg struct type
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the struct
   */
  public static RelDataType convert(Types.StructType structType, RelDataTypeFactory typeFactory) {
    List<Types.NestedField> fields = structType.fields();
    List<RelDataType> fieldTypes = new ArrayList<>(fields.size());
    List<String> fieldNames = new ArrayList<>(fields.size());

    for (Types.NestedField field : fields) {
      fieldNames.add(field.name());

      // Recursively convert field type using main dispatcher
      RelDataType fieldType = convert(field.type(), typeFactory);

      // Handle field nullability - Iceberg has explicit required/optional
      fieldType = typeFactory.createTypeWithNullability(fieldType, field.isOptional());

      fieldTypes.add(fieldType);
    }

    return typeFactory.createStructType(fieldTypes, fieldNames);
  }
}
