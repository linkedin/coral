/**
 * Copyright 2017-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.iceberg.Schema;
import org.apache.iceberg.types.Type;

import com.linkedin.coral.common.types.CoralDataType;
import com.linkedin.coral.common.types.StructType;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergToCoralTypeConverter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public class IcebergToCoralTypeConverter {

  private IcebergToCoralTypeConverter() {
  }

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergToCoralTypeConverter#convert(Schema)} instead.
   */
  @Deprecated
  public static StructType convert(Schema icebergSchema) {
    return com.linkedin.coral.catalog.iceberg.IcebergToCoralTypeConverter.convert(icebergSchema);
  }

  /**
   * @deprecated Use {@link com.linkedin.coral.catalog.iceberg.IcebergToCoralTypeConverter#convert(Type, boolean)} instead.
   */
  @Deprecated
  public static CoralDataType convert(Type icebergType, boolean nullable) {
    return com.linkedin.coral.catalog.iceberg.IcebergToCoralTypeConverter.convert(icebergType, nullable);
  }
}
