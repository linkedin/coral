/**
 * Copyright 2024-2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.serde2.typeinfo.*;

import com.linkedin.coral.common.types.*;


/**
 * Converts Hive TypeInfo objects to Coral data types.
 * This enables integration between Hive's type system and Coral's type system.
 */
public final class HiveToCoralTypeConverter {

  private HiveToCoralTypeConverter() {
    // Utility class - prevent instantiation
  }

  /**
   * Converts a Hive TypeInfo to a Coral data type.
   * @param typeInfo the Hive type to convert
   * @return the corresponding Coral data type
   */
  public static CoralDataType convert(TypeInfo typeInfo) {
    if (typeInfo == null) {
      throw new IllegalArgumentException("TypeInfo cannot be null");
    }

    switch (typeInfo.getCategory()) {
      case PRIMITIVE:
        return convertPrimitive((PrimitiveTypeInfo) typeInfo);
      case LIST:
        return convertList((ListTypeInfo) typeInfo);
      case MAP:
        return convertMap((MapTypeInfo) typeInfo);
      case STRUCT:
        return convertStruct((StructTypeInfo) typeInfo);
      case UNION:
        return convertUnion((UnionTypeInfo) typeInfo);
      default:
        throw new UnsupportedOperationException("Unsupported type category: " + typeInfo.getCategory());
    }
  }

  private static CoralDataType convertPrimitive(PrimitiveTypeInfo type) {
    boolean nullable = true; // Hive types are generally nullable

    switch (type.getPrimitiveCategory()) {
      case BOOLEAN:
        return PrimitiveType.of(CoralTypeKind.BOOLEAN, nullable);
      case BYTE:
        return PrimitiveType.of(CoralTypeKind.TINYINT, nullable);
      case SHORT:
        return PrimitiveType.of(CoralTypeKind.SMALLINT, nullable);
      case INT:
        return PrimitiveType.of(CoralTypeKind.INT, nullable);
      case LONG:
        return PrimitiveType.of(CoralTypeKind.BIGINT, nullable);
      case FLOAT:
        return PrimitiveType.of(CoralTypeKind.FLOAT, nullable);
      case DOUBLE:
        return PrimitiveType.of(CoralTypeKind.DOUBLE, nullable);
      case STRING:
        return PrimitiveType.of(CoralTypeKind.STRING, nullable);
      case DATE:
        return PrimitiveType.of(CoralTypeKind.DATE, nullable);
      case TIMESTAMP:
        // Hive TIMESTAMP has no explicit precision (matches TypeConverter behavior)
        // Use PRECISION_NOT_SPECIFIED (-1) to match Calcite's behavior
        return TimestampType.of(TimestampType.PRECISION_NOT_SPECIFIED, nullable);
      case BINARY:
        return PrimitiveType.of(CoralTypeKind.BINARY, nullable);
      case DECIMAL:
        DecimalTypeInfo decimalType = (DecimalTypeInfo) type;
        return DecimalType.of(decimalType.precision(), decimalType.scale(), nullable);
      case VARCHAR:
        VarcharTypeInfo varcharType = (VarcharTypeInfo) type;
        return VarcharType.of(varcharType.getLength(), nullable);
      case CHAR:
        CharTypeInfo charType = (CharTypeInfo) type;
        return CharType.of(charType.getLength(), nullable);
      case VOID:
        return PrimitiveType.of(CoralTypeKind.NULL, true);
      case UNKNOWN:
        return PrimitiveType.of(CoralTypeKind.STRING, true); // Map to nullable string as a fallback
      default:
        throw new UnsupportedOperationException("Unsupported primitive type: " + type.getPrimitiveCategory());
    }
  }

  private static CoralDataType convertList(ListTypeInfo listType) {
    CoralDataType elementType = convert(listType.getListElementTypeInfo());
    return ArrayType.of(elementType, true); // Lists are nullable in Hive
  }

  private static CoralDataType convertMap(MapTypeInfo mapType) {
    CoralDataType keyType = convert(mapType.getMapKeyTypeInfo());
    CoralDataType valueType = convert(mapType.getMapValueTypeInfo());
    return MapType.of(keyType, valueType, true); // Maps are nullable in Hive
  }

  private static CoralDataType convertStruct(StructTypeInfo structType) {
    List<String> fieldNames = structType.getAllStructFieldNames();
    List<TypeInfo> fieldTypeInfos = structType.getAllStructFieldTypeInfos();

    List<StructField> fields = new ArrayList<>();
    for (int i = 0; i < fieldTypeInfos.size(); i++) {
      CoralDataType fieldType = convert(fieldTypeInfos.get(i));
      fields.add(StructField.of(fieldNames.get(i), fieldType));
    }

    return StructType.of(fields, true); // Structs are nullable in Hive
  }

  private static CoralDataType convertUnion(UnionTypeInfo unionType) {
    // For UNION types, create a struct conforming to Trino's union representation
    // Schema: {tag, field0, field1, ..., fieldN}
    // See: https://github.com/trinodb/trino/pull/3483
    List<TypeInfo> memberTypes = unionType.getAllUnionObjectTypeInfos();

    // Create fields: "tag" field first (INTEGER), then "field0", "field1", etc.
    List<StructField> fields = new ArrayList<>();

    // Add "tag" field (INTEGER) to indicate which union member is active
    fields.add(StructField.of("tag", PrimitiveType.of(CoralTypeKind.INT, true)));

    // Add fields for each possible type in the union
    for (int i = 0; i < memberTypes.size(); i++) {
      CoralDataType fieldType = convert(memberTypes.get(i));
      fields.add(StructField.of("field" + i, fieldType));
    }

    return StructType.of(fields, true);
  }
}
