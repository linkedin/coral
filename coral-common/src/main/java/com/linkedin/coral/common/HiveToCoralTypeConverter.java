/**
 * Copyright 2024-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.linkedin.coral.common.types.CoralDataType;


/**
 * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveToCoralTypeConverter} instead.
 * This class is retained for backward compatibility and will be removed in a future release.
 */
@Deprecated
public final class HiveToCoralTypeConverter {

  private HiveToCoralTypeConverter() {
  }

  /**
   * Converts a Hive TypeInfo to a Coral data type.
   * @param typeInfo the Hive type to convert
   * @return the corresponding Coral data type
   * @deprecated Use {@link com.linkedin.coral.catalog.hive.HiveToCoralTypeConverter#convert(TypeInfo)} instead.
   */
  @Deprecated
  public static CoralDataType convert(TypeInfo typeInfo) {
    return com.linkedin.coral.catalog.hive.HiveToCoralTypeConverter.convert(typeInfo);
  }
}
