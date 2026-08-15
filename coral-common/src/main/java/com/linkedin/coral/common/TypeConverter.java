/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.hadoop.hive.serde2.typeinfo.*;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class TypeConverter {

  private TypeConverter() {
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(TypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(TypeInfo typeInfo, RelDataTypeFactory relTypeFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(typeInfo, relTypeFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(PrimitiveTypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(PrimitiveTypeInfo type, RelDataTypeFactory dtFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(type, dtFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(ListTypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(ListTypeInfo lstType, RelDataTypeFactory dtFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(lstType, dtFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(MapTypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(MapTypeInfo mapType, RelDataTypeFactory dtFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(mapType, dtFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(StructTypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(StructTypeInfo structType, RelDataTypeFactory dtFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(structType, dtFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(UnionTypeInfo, RelDataTypeFactory)} instead. */
  @Deprecated
  public static RelDataType convert(UnionTypeInfo unionType, RelDataTypeFactory dtFactory) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(unionType, dtFactory);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convert(RelDataType)} instead. */
  @Deprecated
  public static TypeInfo convert(RelDataType rType) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convert(rType);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convertStructType(RelDataType)} instead. */
  @Deprecated
  public static TypeInfo convertStructType(RelDataType rType) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convertStructType(rType);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convertMapType(RelDataType)} instead. */
  @Deprecated
  public static TypeInfo convertMapType(RelDataType rType) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convertMapType(rType);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convertListType(RelDataType)} instead. */
  @Deprecated
  public static TypeInfo convertListType(RelDataType rType) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convertListType(rType);
  }

  /** @deprecated Use {@link com.linkedin.coral.catalog.hive.TypeConverter#convertPrimtiveType(RelDataType)} instead. */
  @Deprecated
  public static TypeInfo convertPrimtiveType(RelDataType rType) {
    return com.linkedin.coral.catalog.hive.TypeConverter.convertPrimtiveType(rType);
  }
}
