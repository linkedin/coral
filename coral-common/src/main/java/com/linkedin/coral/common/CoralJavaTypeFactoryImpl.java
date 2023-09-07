/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common;

import java.util.List;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.StructKind;


public class CoralJavaTypeFactoryImpl extends JavaTypeFactoryImpl {
  public CoralJavaTypeFactoryImpl(RelDataTypeSystem typeSystem) {
    super(typeSystem);
  }

  @Override
  public RelDataType createStructType(final List<RelDataType> typeList, final List<String> fieldNameList) {
    return createStructType(StructKind.PEEK_FIELDS_NO_EXPAND, typeList, fieldNameList);
  }
}
