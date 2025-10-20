/**
 * Copyright 2017-2024 LinkedIn Corporation. All rights reserved.
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

      // Convert field type
      RelDataType fieldType = convertIcebergType(field.type(), field.name(), typeFactory);

      // Handle nullability - Iceberg has explicit required/optional
      fieldType = typeFactory.createTypeWithNullability(fieldType, field.isOptional());

      fieldTypes.add(fieldType);
    }

    return typeFactory.createStructType(fieldTypes, fieldNames);
  }

  /**
   * Converts Iceberg Type to Calcite RelDataType.
   * 
   * @param icebergType Iceberg type
   * @param fieldName Field name (for nested type naming)
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the Iceberg type
   */
  private static RelDataType convertIcebergType(Type icebergType, String fieldName, RelDataTypeFactory typeFactory) {

    Type.TypeID typeId = icebergType.typeId();

    switch (typeId) {
      case BOOLEAN:
        return typeFactory.createSqlType(SqlTypeName.BOOLEAN);

      case INTEGER:
        return typeFactory.createSqlType(SqlTypeName.INTEGER);

      case LONG:
        return typeFactory.createSqlType(SqlTypeName.BIGINT);

      case FLOAT:
        return typeFactory.createSqlType(SqlTypeName.FLOAT);

      case DOUBLE:
        return typeFactory.createSqlType(SqlTypeName.DOUBLE);

      case DATE:
        return typeFactory.createSqlType(SqlTypeName.DATE);

      case TIME:
        return typeFactory.createSqlType(SqlTypeName.TIME);

      case TIMESTAMP:
        // Iceberg has timestamptz (with timezone) and timestamp (without timezone)
        Types.TimestampType tsType = (Types.TimestampType) icebergType;
        return tsType.shouldAdjustToUTC() ? typeFactory.createSqlType(SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE)
            : typeFactory.createSqlType(SqlTypeName.TIMESTAMP);

      case STRING:
        return typeFactory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);

      case UUID:
        // Represent UUID as CHAR(36) to preserve UUID semantics
        return typeFactory.createSqlType(SqlTypeName.CHAR, 36);

      case FIXED:
        Types.FixedType fixedType = (Types.FixedType) icebergType;
        return typeFactory.createSqlType(SqlTypeName.BINARY, fixedType.length());

      case BINARY:
        return typeFactory.createSqlType(SqlTypeName.VARBINARY, Integer.MAX_VALUE);

      case DECIMAL:
        Types.DecimalType decimalType = (Types.DecimalType) icebergType;
        return typeFactory.createSqlType(SqlTypeName.DECIMAL, decimalType.precision(), decimalType.scale());

      case STRUCT:
        return convertIcebergStruct((Types.StructType) icebergType, fieldName, typeFactory);

      case LIST:
        Types.ListType listType = (Types.ListType) icebergType;
        RelDataType elementType =
            convertIcebergType(listType.elementType(), fieldName + "_element", typeFactory);
        // Handle list element nullability
        elementType = typeFactory.createTypeWithNullability(elementType, listType.isElementOptional());
        return typeFactory.createArrayType(elementType, -1);

      case MAP:
        Types.MapType mapType = (Types.MapType) icebergType;
        RelDataType keyType = convertIcebergType(mapType.keyType(), fieldName + "_key", typeFactory);
        RelDataType valueType = convertIcebergType(mapType.valueType(), fieldName + "_value", typeFactory);
        // Iceberg map values can be required or optional
        valueType = typeFactory.createTypeWithNullability(valueType, mapType.isValueOptional());
        return typeFactory.createMapType(keyType, valueType);

      default:
        throw new UnsupportedOperationException("Unsupported Iceberg type: " + icebergType + " (TypeID: " + typeId + ")");
    }
  }

  /**
   * Converts Iceberg StructType to Calcite RelDataType.
   * 
   * @param structType Iceberg struct type
   * @param structName Name for the struct (for nested type naming)
   * @param typeFactory Calcite type factory
   * @return RelDataType representing the struct
   */
  private static RelDataType convertIcebergStruct(Types.StructType structType, String structName,
      RelDataTypeFactory typeFactory) {

    List<Types.NestedField> fields = structType.fields();
    List<RelDataType> fieldTypes = new ArrayList<>(fields.size());
    List<String> fieldNames = new ArrayList<>(fields.size());

    for (Types.NestedField field : fields) {
      fieldNames.add(field.name());

      RelDataType fieldType = convertIcebergType(field.type(), structName + "_" + field.name(), typeFactory);

      // Handle field nullability
      fieldType = typeFactory.createTypeWithNullability(fieldType, field.isOptional());

      fieldTypes.add(fieldType);
    }

    return typeFactory.createStructType(fieldTypes, fieldNames);
  }
}

