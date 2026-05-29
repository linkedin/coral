/**
 * Copyright 2022-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.utils;

import org.apache.calcite.rel.type.RelDataType;


/**
 * @deprecated Use {@link RelDataTypeToTypeStringConverter} instead. This class will be removed in a future release.
 */
@Deprecated
public class RelDataTypeToHiveTypeStringConverter {
  private RelDataTypeToHiveTypeStringConverter() {
  }

  public RelDataTypeToHiveTypeStringConverter(boolean convertUnionTypes) {
    new RelDataTypeToTypeStringConverter(convertUnionTypes);
  }

  /**
   * @param relDataType a given RelDataType
   * @return a syntactically and semantically correct type string for relDataType
   */
  public static String convertRelDataType(RelDataType relDataType) {
    return RelDataTypeToTypeStringConverter.convertRelDataType(relDataType);
  }
}
