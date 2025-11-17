/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;


/**
 * Converter that transforms Coral data types to Calcite RelDataType objects.
 * This enables the Coral type system to integrate seamlessly with Calcite-based
 * query planning and execution engines.
 */
public final class CoralTypeToRelDataTypeConverter {

  private CoralTypeToRelDataTypeConverter() {
    // Utility class - prevent instantiation
  }

  /**
   * Converts a Coral data type to a Calcite RelDataType.
   * @param type the Coral data type to convert
   * @return the corresponding Calcite RelDataType
   */
  public static RelDataType convert(CoralDataType type, RelDataTypeFactory factory) {
    RelDataType relType;

    if (type instanceof PrimitiveType) {
      relType = convertPrimitive((PrimitiveType) type, factory);
    } else if (type instanceof TimestampType) {
      TimestampType ts = (TimestampType) type;
      // Handle unspecified precision (Hive compatibility)
      if (ts.hasPrecision()) {
        relType = factory.createSqlType(SqlTypeName.TIMESTAMP, ts.getPrecision());
      } else {
        // No precision specified - matches TypeConverter behavior
        relType = factory.createSqlType(SqlTypeName.TIMESTAMP);
      }
    } else if (type instanceof DecimalType) {
      DecimalType dec = (DecimalType) type;
      relType = factory.createSqlType(SqlTypeName.DECIMAL, dec.getPrecision(), dec.getScale());
    } else if (type instanceof CharType) {
      CharType c = (CharType) type;
      relType = factory.createSqlType(SqlTypeName.CHAR, c.getLength());
    } else if (type instanceof VarcharType) {
      VarcharType v = (VarcharType) type;
      relType = factory.createSqlType(SqlTypeName.VARCHAR, v.getLength());
    } else if (type instanceof ArrayType) {
      ArrayType arrayType = (ArrayType) type;
      RelDataType elementType = convert(arrayType.getElementType(), factory);
      relType = factory.createArrayType(elementType, -1);
    } else if (type instanceof MapType) {
      MapType mapType = (MapType) type;
      RelDataType keyType = convert(mapType.getKeyType(), factory);
      RelDataType valueType = convert(mapType.getValueType(), factory);
      relType = factory.createMapType(keyType, valueType);
    } else if (type instanceof StructType) {
      StructType structType = (StructType) type;
      List<RelDataType> fieldTypes = new ArrayList<>();
      List<String> fieldNames = new ArrayList<>();
      for (StructField field : structType.getFields()) {
        fieldTypes.add(convert(field.getType(), factory));
        fieldNames.add(field.getName());
      }
      relType = factory.createStructType(fieldTypes, fieldNames);
    } else {
      // Fallback for unknown types
      relType = factory.createSqlType(SqlTypeName.ANY);
    }

    // Handle nullability
    if (type.isNullable() && !relType.isNullable()) {
      relType = factory.createTypeWithNullability(relType, true);
    } else if (!type.isNullable() && relType.isNullable()) {
      relType = factory.createTypeWithNullability(relType, false);
    }

    return relType;
  }

  /**
   * Converts a primitive Coral type to a Calcite RelDataType.
   */
  private static RelDataType convertPrimitive(PrimitiveType prim, RelDataTypeFactory factory) {
    switch (prim.getKind()) {
      case BOOLEAN:
        return factory.createSqlType(SqlTypeName.BOOLEAN);
      case TINYINT:
        return factory.createSqlType(SqlTypeName.TINYINT);
      case SMALLINT:
        return factory.createSqlType(SqlTypeName.SMALLINT);
      case INT:
        return factory.createSqlType(SqlTypeName.INTEGER);
      case BIGINT:
        return factory.createSqlType(SqlTypeName.BIGINT);
      case FLOAT:
        return factory.createSqlType(SqlTypeName.FLOAT);
      case DOUBLE:
        return factory.createSqlType(SqlTypeName.DOUBLE);
      case STRING:
        // Use VARCHAR with max length for STRING type
        return factory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);
      case DATE:
        return factory.createSqlType(SqlTypeName.DATE);
      case TIME:
        return factory.createSqlType(SqlTypeName.TIME);
      case BINARY:
        return factory.createSqlType(SqlTypeName.BINARY);
      case NULL:
        return factory.createSqlType(SqlTypeName.NULL);
      default:
        // Fallback for unsupported primitive types
        return factory.createSqlType(SqlTypeName.ANY);
    }
  }

  /**
   * Converts a struct Coral type to a Calcite RelDataType.
   */
  private static RelDataType convertStruct(StructType struct, RelDataTypeFactory factory) {
    List<String> names = new ArrayList<>();
    List<RelDataType> types = new ArrayList<>();

    for (StructField field : struct.getFields()) {
      names.add(field.getName());
      types.add(convert(field.getType(), factory));
    }

    return factory.createStructType(types, names);
  }
}
