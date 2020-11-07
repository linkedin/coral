/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericRecord;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.SqlTypeName;


public class AvroJavaTypeFactory extends JavaTypeFactoryImpl {
  public static final AvroJavaTypeFactory AVRO_TYPE_FACTORY = new AvroJavaTypeFactory();

  @Override
  public Type getJavaClass(RelDataType type) {
    SqlTypeName typeName = type.getSqlTypeName();
    switch (typeName) {
      case ROW:
        return GenericRecord.class;
      case BINARY:
        return ByteBuffer.class;
      case MULTISET:
        return GenericArray.class;
      default:
        return super.getJavaClass(type);
    }
  }

  /**
   * Gets Java mutable type (primitive type if applicable) for a rel data type
   *
   * @param type Rel data type
   * @return Java mutable type
   */
  public Type getMutableJavaClass(RelDataType type) {
    final Type originalType = getJavaClass(type);
    final Primitive primitive = Primitive.ofBox(originalType);
    if (primitive != null) {
      return primitive.primitiveClass;
    }
    return originalType;
  }

  /**
   * Gets Java immutable type for a rel data type. If corresponding Java type is primitive, return the box type.
   * (like Integer instead of int).
   *
   * @param type Rel data type
   * @return Java immutable type
   */
  public Type getImmutableJavaClass(RelDataType type) {
    final Type originalType = getJavaClass(type);
    final Primitive primitive = Primitive.of(originalType);
    if (primitive != null) {
      return primitive.boxClass;
    }
    return originalType;
  }

}
