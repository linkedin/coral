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


/**
 * As Coral depends on the code of Calcite to generate RelDataType for each column,
 * RelDataType of a column of struct type is always generated with StructKind.FULLY_QUALIFIED
 * in {@link org.apache.calcite.rel.type.RelDataTypeFactoryImpl#createStructType(List, List)}.
 * With {@link org.apache.calcite.rel.type.StructKind}.FULLY_QUALIFIED,
 * the validation of Sql query always requires a fully qualified name of a field from a column
 * of struct type like tbl.structCol.field, otherwise an exception is thrown with the message
 * of "table not found". In order to make the translation of the query with non-fully-qualified
 * name for a field from a struct-type column, CoralJavaTypeFactoryImpl overrides
 * {@link org.apache.calcite.rel.type.RelDataTypeFactoryImpl#createStructType(List, List)} to
 * assign {@link org.apache.calcite.rel.type.StructKind}.PEEK_FIELDS_NO_EXPAND
 * to returned {@link org.apache.calcite.rel.type.RelDataType}
 */
public class CoralJavaTypeFactoryImpl extends JavaTypeFactoryImpl {
  public CoralJavaTypeFactoryImpl(RelDataTypeSystem typeSystem) {
    super(typeSystem);
  }

  @Override
  public RelDataType createStructType(final List<RelDataType> typeList, final List<String> fieldNameList) {
    return createStructType(StructKind.PEEK_FIELDS_NO_EXPAND, typeList, fieldNameList);
  }
}
