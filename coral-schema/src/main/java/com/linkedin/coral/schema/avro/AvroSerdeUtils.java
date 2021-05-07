/**
 * Copyright 2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.schema.avro;

import java.util.List;

import org.apache.avro.Schema;


/**
 * Utilities useful only to the Hive AvroSerde itself.
 * Please refer {@link org.apache.hadoop.hive.serde2.avro.AvroSerdeUtils} for original implementation
 */
final class AvroSerdeUtils {

  public static final String AVRO_SCHEMA_LITERAL = "avro.schema.literal";

  private AvroSerdeUtils() {

  }

  /**
   * Determine if an Avro schema is of type Union[T, NULL].  Avro supports nullable
   * types via a union of type T and null.  This is a very common use case.
   * As such, we want to silently convert it to just T and allow the value to be null.
   *
   * @return true if type represents Union[T, Null], false otherwise
   */
  public static boolean isNullableType(Schema schema) {
    return schema.getType().equals(Schema.Type.UNION) && schema.getTypes().size() == 2
        && (schema.getTypes().get(0).getType().equals(Schema.Type.NULL)
            || schema.getTypes().get(1).getType().equals(Schema.Type.NULL));
    // [null, null] not allowed, so this check is ok.
  }

  /**
   * In a nullable type, get the schema for the non-nullable type.  This method
   * does no checking that the provides Schema is nullable.
   */
  public static Schema getOtherTypeFromNullableType(Schema schema) {
    List<Schema> types = schema.getTypes();
    return types.get(0).getType().equals(Schema.Type.NULL) ? types.get(1) : types.get(0);
  }
}
