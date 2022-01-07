/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.typeinfo.*;


/**
 * Copied from org.apache.hadoop.hive.ql.optimizer.calcite.translator.TypeConverter
 */

public class TypeConverter {

  private TypeConverter() {

  }

  public static RelDataType convert(TypeInfo typeInfo, RelDataTypeFactory relTypeFactory) {
    switch (typeInfo.getCategory()) {
      case PRIMITIVE:
        return convert((PrimitiveTypeInfo) typeInfo, relTypeFactory);
      case LIST:
        return convert((ListTypeInfo) typeInfo, relTypeFactory);
      case MAP:
        return convert((MapTypeInfo) typeInfo, relTypeFactory);
      case STRUCT:
        return convert((StructTypeInfo) typeInfo, relTypeFactory);
      case UNION:
        return convert((UnionTypeInfo) typeInfo, relTypeFactory);
      default:
        throw new RuntimeException("Unknown type category: " + typeInfo.getCategory());
    }
  }

  public static RelDataType convert(PrimitiveTypeInfo type, RelDataTypeFactory dtFactory) {
    RelDataType convertedType = null;

    switch (type.getPrimitiveCategory()) {
      case VOID:
        convertedType = dtFactory.createSqlType(SqlTypeName.NULL);
        break;
      case BOOLEAN:
        convertedType = dtFactory.createSqlType(SqlTypeName.BOOLEAN);
        break;
      case BYTE:
        convertedType = dtFactory.createSqlType(SqlTypeName.TINYINT);
        break;
      case SHORT:
        convertedType = dtFactory.createSqlType(SqlTypeName.SMALLINT);
        break;
      case INT:
        convertedType = dtFactory.createSqlType(SqlTypeName.INTEGER);
        break;
      case LONG:
        convertedType = dtFactory.createSqlType(SqlTypeName.BIGINT);
        break;
      case FLOAT:
        convertedType = dtFactory.createSqlType(SqlTypeName.FLOAT);
        break;
      case DOUBLE:
        convertedType = dtFactory.createSqlType(SqlTypeName.DOUBLE);
        break;
      case STRING:
        convertedType = dtFactory.createSqlType(SqlTypeName.VARCHAR, Integer.MAX_VALUE);
        break;
      case DATE:
        convertedType = dtFactory.createSqlType(SqlTypeName.DATE);
        break;
      case TIMESTAMP:
        convertedType = dtFactory.createSqlType(SqlTypeName.TIMESTAMP);
        break;
      case BINARY:
        convertedType = dtFactory.createSqlType(SqlTypeName.BINARY);
        break;
      case DECIMAL:
        DecimalTypeInfo dtInf = (DecimalTypeInfo) type;
        convertedType = dtFactory.createSqlType(SqlTypeName.DECIMAL, dtInf.precision(), dtInf.scale());
        break;
      case VARCHAR:
        convertedType = dtFactory.createSqlType(SqlTypeName.VARCHAR, ((BaseCharTypeInfo) type).getLength());
        break;
      case CHAR:
        convertedType = dtFactory.createSqlType(SqlTypeName.CHAR, ((BaseCharTypeInfo) type).getLength());
        break;
      case UNKNOWN:
        convertedType = dtFactory.createSqlType(SqlTypeName.OTHER);
        break;
      default:
        throw new RuntimeException("Unknown primitive type category: " + type.getPrimitiveCategory());
    }

    if (null == convertedType) {
      throw new RuntimeException("Unsupported Type : " + type.getTypeName());
    }

    return dtFactory.createTypeWithNullability(convertedType, true);
  }

  public static RelDataType convert(ListTypeInfo lstType, RelDataTypeFactory dtFactory) {
    RelDataType elemType = convert(lstType.getListElementTypeInfo(), dtFactory);
    RelDataType arrayType = dtFactory.createArrayType(elemType, -1);
    return dtFactory.createTypeWithNullability(arrayType, true);
  }

  public static RelDataType convert(MapTypeInfo mapType, RelDataTypeFactory dtFactory) {
    RelDataType keyType = convert(mapType.getMapKeyTypeInfo(), dtFactory);
    RelDataType valueType = convert(mapType.getMapValueTypeInfo(), dtFactory);
    RelDataType type = dtFactory.createMapType(keyType, valueType);
    return dtFactory.createTypeWithNullability(type, true);
  }

  public static RelDataType convert(StructTypeInfo structType, final RelDataTypeFactory dtFactory) {
    List<RelDataType> fTypes = new ArrayList<>(structType.getAllStructFieldTypeInfos().size());
    for (TypeInfo ti : structType.getAllStructFieldTypeInfos()) {
      fTypes.add(convert(ti, dtFactory));
    }
    RelDataType rowType = dtFactory.createStructType(fTypes, structType.getAllStructFieldNames());
    // TODO: Return nullable record type.
    // All types in Hive are effectively nullable since the data is injected from external source.
    // Calcite does not support nullable record type and since we don't create our own type factory
    // ... we've problem! The call below only makes the fields of the struct nullable which is
    // not the same as nullable struct.
    return dtFactory.createTypeWithNullability(rowType, true);
  }

  // Mimic the StructTypeInfo conversion to convert a UnionTypeInfo to the corresponding RelDataType
  // The schema of output Struct conforms to https://github.com/trinodb/trino/pull/3483
  // except we adopted "integer" for the type of "tag" field instead of "tinyint" in the Trino patch
  // for compatibility with other platforms that Iceberg currently doesn't support tinyint type.

  // Note: this is subject to change in the future pending on the discussion in
  // https://mail-archives.apache.org/mod_mbox/iceberg-dev/202112.mbox/browser
  public static RelDataType convert(UnionTypeInfo unionType, RelDataTypeFactory dtFactory) {
    List<RelDataType> fTypes = unionType.getAllUnionObjectTypeInfos().stream()
        .map(typeInfo -> convert(typeInfo, dtFactory)).collect(Collectors.toList());
    List<String> fNames = IntStream.range(0, unionType.getAllUnionObjectTypeInfos().size()).mapToObj(i -> "field" + i)
        .collect(Collectors.toList());
    fTypes.add(0, dtFactory.createSqlType(SqlTypeName.INTEGER));
    fNames.add(0, "tag");

    RelDataType rowType = dtFactory.createStructType(fTypes, fNames);
    return dtFactory.createTypeWithNullability(rowType, true);
  }

  public static TypeInfo convert(RelDataType rType) {
    if (rType.isStruct()) {
      return convertStructType(rType);
    } else if (rType.getComponentType() != null) {
      return convertListType(rType);
    } else if (rType.getKeyType() != null) {
      return convertMapType(rType);
    } else {
      return convertPrimtiveType(rType);
    }
  }

  public static TypeInfo convertStructType(RelDataType rType) {
    List<TypeInfo> fTypes = rType.getFieldList().stream().map(f -> convert(f.getType())).collect(Collectors.toList());
    List<String> fNames = rType.getFieldNames();
    return TypeInfoFactory.getStructTypeInfo(fNames, fTypes);
  }

  public static TypeInfo convertMapType(RelDataType rType) {
    return TypeInfoFactory.getMapTypeInfo(convert(rType.getKeyType()), convert(rType.getValueType()));
  }

  public static TypeInfo convertListType(RelDataType rType) {
    return TypeInfoFactory.getListTypeInfo(convert(rType.getComponentType()));
  }

  public static TypeInfo convertPrimtiveType(RelDataType rType) {
    switch (rType.getSqlTypeName()) {
      case BOOLEAN:
        return TypeInfoFactory.booleanTypeInfo;
      case TINYINT:
        return TypeInfoFactory.byteTypeInfo;
      case SMALLINT:
        return TypeInfoFactory.shortTypeInfo;
      case INTEGER:
        return TypeInfoFactory.intTypeInfo;
      case BIGINT:
        return TypeInfoFactory.longTypeInfo;
      case FLOAT:
        return TypeInfoFactory.floatTypeInfo;
      case DOUBLE:
        return TypeInfoFactory.doubleTypeInfo;
      case DATE:
        return TypeInfoFactory.dateTypeInfo;
      case TIMESTAMP:
        return TypeInfoFactory.timestampTypeInfo;
      case BINARY:
        return TypeInfoFactory.binaryTypeInfo;
      case DECIMAL:
        return TypeInfoFactory.getDecimalTypeInfo(rType.getPrecision(), rType.getScale());
      case VARCHAR:
        if (rType.getPrecision() == Integer.MAX_VALUE) {
          return TypeInfoFactory.getPrimitiveTypeInfo(serdeConstants.STRING_TYPE_NAME);
        } else {
          return TypeInfoFactory.getVarcharTypeInfo(rType.getPrecision());
        }
      case CHAR:
        if (rType.getPrecision() > HiveChar.MAX_CHAR_LENGTH) {
          return TypeInfoFactory.getVarcharTypeInfo(rType.getPrecision());
        } else {
          return TypeInfoFactory.getCharTypeInfo(rType.getPrecision());
        }
      case OTHER:
      default:
        return TypeInfoFactory.voidTypeInfo;
    }
  }
}
