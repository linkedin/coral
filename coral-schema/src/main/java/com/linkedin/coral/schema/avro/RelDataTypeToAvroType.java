package com.linkedin.coral.schema.avro;

import com.linkedin.coral.com.google.common.base.Preconditions;
import com.linkedin.coral.javax.annotation.Nonnull;
import org.apache.avro.Schema;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.BasicSqlType;


/**
 * This class provides RelDataType to avro data type mapping
 *
 * convertRelDataTypeToAvroType is the main API
 */
class RelDataTypeToAvroType {
  // private constructor for utility class
  private RelDataTypeToAvroType() {
  }

  /**
   * This method converts RelDataType to avro data type
   *
   * @param relDataType
   * @return Schema of Avro data type corresponding to input RelDataType
   */
  static Schema convertRelDataTypeToAvroType(@Nonnull RelDataType relDataType) {
    Preconditions.checkNotNull(relDataType);

    if (relDataType instanceof BasicSqlType) {
      return basicSqlTypeToAvroDataType((BasicSqlType) relDataType);
    }

    // TODO: support more RelDataType if necessary
    // For example: MultisetSqlType, ArraySqlType, RelRecordType, DynamicRecordType, MapSqlType

    // TODO: improve logging
    throw new UnsupportedOperationException("Unsupported RelDataType: " + relDataType.toString());
  }

  private static Schema basicSqlTypeToAvroDataType(BasicSqlType relType) {
    switch (relType.getSqlTypeName()) {
      case BOOLEAN:
        return Schema.create(Schema.Type.BOOLEAN);
      case TINYINT:
      case INTEGER:
        return Schema.create(Schema.Type.INT);
      case BIGINT:
        return Schema.create(Schema.Type.LONG);
      case FLOAT:
        return Schema.create(Schema.Type.FLOAT);
      case DOUBLE:
        return Schema.create(Schema.Type.DOUBLE);
      case VARCHAR:
      case CHAR:
        return Schema.create(Schema.Type.STRING);
      case BINARY:
        return Schema.create(Schema.Type.BYTES);
      case NULL:
        return Schema.create(Schema.Type.NULL);
      case ANY:
        return Schema.create(Schema.Type.BYTES);
      default:
        throw new UnsupportedOperationException(relType.getSqlTypeName() + " is not supported.");
    }
  }
}
