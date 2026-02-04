/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Type;
import org.apache.iceberg.types.Types;

import com.linkedin.coral.common.types.*;


/**
 * Converts Iceberg Schema and Types to Coral data types.
 * This is the first stage of the two-stage conversion: Iceberg → Coral → Calcite.
 *
 * This converter provides a unified type system abstraction by converting Iceberg types
 * to Coral's intermediate type representation, which can then be converted to Calcite
 * RelDataType using {@link com.linkedin.coral.common.types.CoralTypeToRelDataTypeConverter}.
 */
public class IcebergToCoralTypeConverter {

  private IcebergToCoralTypeConverter() {
    // Utility class
  }

  /**
   * Converts Iceberg Schema to Coral StructType.
   *
   * @param icebergSchema Iceberg table schema
   * @return StructType representing the Iceberg schema in Coral type system
   */
  public static StructType convert(Schema icebergSchema) {
    List<Types.NestedField> columns = icebergSchema.columns();
    List<StructField> fields = new ArrayList<>(columns.size());

    for (Types.NestedField field : columns) {
      // Convert field type using the main dispatcher
      CoralDataType fieldType = convert(field.type(), field.isOptional());
      fields.add(StructField.of(field.name(), fieldType));
    }

    // Table-level struct is nullable (consistent with Hive convention)
    return StructType.of(fields, true);
  }

  /**
   * Main dispatcher - converts Iceberg Type to Coral CoralDataType based on type category.
   *
   * @param icebergType Iceberg type
   * @param nullable Whether this type instance is nullable
   * @return CoralDataType representing the Iceberg type
   */
  public static CoralDataType convert(Type icebergType, boolean nullable) {
    Type.TypeID typeId = icebergType.typeId();

    switch (typeId) {
      case STRUCT:
        return convertStruct((Types.StructType) icebergType, nullable);
      case LIST:
        return convertList((Types.ListType) icebergType, nullable);
      case MAP:
        return convertMap((Types.MapType) icebergType, nullable);
      default:
        // Handle all primitive types
        return convertPrimitive(icebergType, nullable);
    }
  }

  /**
   * Converts Iceberg primitive types to Coral CoralDataType.
   * Handles all atomic types: BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, DATE, TIME, TIMESTAMP,
   * STRING, UUID, FIXED, BINARY, DECIMAL.
   *
   * @param icebergType Iceberg primitive type
   * @param nullable Whether this type instance is nullable
   * @return CoralDataType representing the primitive type
   */
  private static CoralDataType convertPrimitive(Type icebergType, boolean nullable) {
    Type.TypeID typeId = icebergType.typeId();

    switch (typeId) {
      case BOOLEAN:
        return PrimitiveType.of(CoralTypeKind.BOOLEAN, nullable);
      case INTEGER:
        return PrimitiveType.of(CoralTypeKind.INT, nullable);
      case LONG:
        return PrimitiveType.of(CoralTypeKind.BIGINT, nullable);
      case FLOAT:
        return PrimitiveType.of(CoralTypeKind.FLOAT, nullable);
      case DOUBLE:
        return PrimitiveType.of(CoralTypeKind.DOUBLE, nullable);
      case DATE:
        return PrimitiveType.of(CoralTypeKind.DATE, nullable);
      case TIME:
        return PrimitiveType.of(CoralTypeKind.TIME, nullable);
      case TIMESTAMP:
        // Iceberg has two timestamp variants: TimestampType.withZone() and TimestampType.withoutZone()
        // (see https://github.com/linkedin/iceberg/blob/ebf4776724f346310105a58a6966a69dba2200c1/api/src/main/java/org/apache/iceberg/types/Types.java#L49)
        // Both return typeId() == TypeID.TIMESTAMP, so they both reach this case statement.
        //
        // LIMITATION: Coral's TimestampType currently does NOT distinguish between with/without timezone.
        // We map both variants to the same TimestampType(precision=6) for now.
        // Future enhancement: Add timezone awareness to Coral's type system to preserve this distinction.
        //
        // Iceberg timestamps always have microsecond precision (6 fractional digits).
        return TimestampType.of(6, nullable);
      case STRING:
        // String in Iceberg maps to VARCHAR with max length
        return VarcharType.of(Integer.MAX_VALUE, nullable);
      case UUID:
        // Represent UUID as CHAR(36) to preserve UUID semantics
        return CharType.of(36, nullable);
      case FIXED:
        Types.FixedType fixedType = (Types.FixedType) icebergType;
        // Fixed-length binary - preserve length
        return BinaryType.of(fixedType.length(), nullable);
      case BINARY:
        // Variable-length binary
        return BinaryType.of(BinaryType.LENGTH_UNBOUNDED, nullable);
      case DECIMAL:
        Types.DecimalType decimalType = (Types.DecimalType) icebergType;
        return DecimalType.of(decimalType.precision(), decimalType.scale(), nullable);
      default:
        throw new UnsupportedOperationException(
            "Unsupported Iceberg primitive type: " + icebergType + " (TypeID: " + typeId + ")");
    }
  }

  /**
   * Converts Iceberg ListType to Coral ArrayType.
   *
   * @param listType Iceberg list type
   * @param nullable Whether the list itself is nullable
   * @return ArrayType representing the list/array
   */
  private static ArrayType convertList(Types.ListType listType, boolean nullable) {
    // Recursively convert element type with its nullability
    CoralDataType elementType = convert(listType.elementType(), listType.isElementOptional());
    return ArrayType.of(elementType, nullable);
  }

  /**
   * Converts Iceberg MapType to Coral MapType.
   *
   * @param mapType Iceberg map type
   * @param nullable Whether the map itself is nullable
   * @return MapType representing the map
   */
  private static MapType convertMap(Types.MapType mapType, boolean nullable) {
    // Recursively convert key and value types
    // Iceberg map keys are always required (not nullable)
    CoralDataType keyType = convert(mapType.keyType(), false);
    CoralDataType valueType = convert(mapType.valueType(), mapType.isValueOptional());

    return MapType.of(keyType, valueType, nullable);
  }

  /**
   * Converts Iceberg StructType to Coral StructType.
   *
   * @param structType Iceberg struct type
   * @param nullable Whether the struct itself is nullable
   * @return StructType representing the struct
   */
  private static StructType convertStruct(Types.StructType structType, boolean nullable) {
    List<Types.NestedField> fields = structType.fields();
    List<StructField> coralFields = new ArrayList<>(fields.size());

    for (Types.NestedField field : fields) {
      // Recursively convert field type using main dispatcher with field's nullability
      CoralDataType fieldType = convert(field.type(), field.isOptional());
      coralFields.add(StructField.of(field.name(), fieldType));
    }

    return StructType.of(coralFields, nullable);
  }
}
