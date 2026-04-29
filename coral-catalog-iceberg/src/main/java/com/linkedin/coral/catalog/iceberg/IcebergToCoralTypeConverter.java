/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.catalog.iceberg;

import java.util.ArrayList;
import java.util.List;

import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Type;
import org.apache.iceberg.types.Types;

import com.linkedin.coral.common.types.*;


/**
 * Converts Iceberg Schema and Types to Coral data types.
 * This is the first stage of the two-stage conversion: Iceberg -> Coral -> Calcite.
 */
public class IcebergToCoralTypeConverter {

  private IcebergToCoralTypeConverter() {
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
      CoralDataType fieldType = convert(field.type(), field.isOptional());
      fields.add(StructField.of(field.name(), fieldType));
    }

    return StructType.of(fields, true);
  }

  /**
   * Converts Iceberg Type to Coral CoralDataType.
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
        return convertPrimitive(icebergType, nullable);
    }
  }

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
        return TimestampType.of(6, nullable);
      case STRING:
        return VarcharType.of(Integer.MAX_VALUE, nullable);
      case UUID:
        return CharType.of(36, nullable);
      case FIXED:
        Types.FixedType fixedType = (Types.FixedType) icebergType;
        return BinaryType.of(fixedType.length(), nullable);
      case BINARY:
        return BinaryType.of(BinaryType.LENGTH_UNBOUNDED, nullable);
      case DECIMAL:
        Types.DecimalType decimalType = (Types.DecimalType) icebergType;
        return DecimalType.of(decimalType.precision(), decimalType.scale(), nullable);
      default:
        throw new UnsupportedOperationException(
            "Unsupported Iceberg primitive type: " + icebergType + " (TypeID: " + typeId + ")");
    }
  }

  private static ArrayType convertList(Types.ListType listType, boolean nullable) {
    CoralDataType elementType = convert(listType.elementType(), listType.isElementOptional());
    return ArrayType.of(elementType, nullable);
  }

  private static MapType convertMap(Types.MapType mapType, boolean nullable) {
    CoralDataType keyType = convert(mapType.keyType(), false);
    CoralDataType valueType = convert(mapType.valueType(), mapType.isValueOptional());
    return MapType.of(keyType, valueType, nullable);
  }

  private static StructType convertStruct(Types.StructType structType, boolean nullable) {
    List<Types.NestedField> fields = structType.fields();
    List<StructField> coralFields = new ArrayList<>(fields.size());

    for (Types.NestedField field : fields) {
      CoralDataType fieldType = convert(field.type(), field.isOptional());
      coralFields.add(StructField.of(field.name(), fieldType));
    }

    return StructType.of(coralFields, nullable);
  }
}
