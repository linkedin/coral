/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.trino.rel2trino.transformers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;

import com.linkedin.coral.com.google.common.collect.ImmutableList;
import com.linkedin.coral.common.transformers.SqlCallTransformer;


/**
 * This class is an ad-hoc SqlCallTransformer which converts the map struct access operator "[]" defined
 * from Calcite in a SqlIdentifier into a UDF operator of "element_at",
 * e.g. from col["field"] to element_at(col, "field")
 */
public class MapStructAccessOperatorTransformer extends SqlCallTransformer {
  private static final String AS_OPERATOR_NAME = "AS";
  private static final Pattern MAP_STRUCT_ACCESS_PATTERN = Pattern.compile("\\\".+\\\"\\[\\\".+\\\"\\]");
  private static final String ELEMENT_AT = "element_at(%s, %s)";

  @Override
  protected boolean condition(SqlCall sqlCall) {
    if (AS_OPERATOR_NAME.equalsIgnoreCase(sqlCall.getOperator().getName())) {
      if (sqlCall.getOperandList().get(0) instanceof SqlIdentifier) {
        SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlCall.getOperandList().get(0);
        if (sqlIdentifier.names.size() == 2) {
          Matcher matcher = MAP_STRUCT_ACCESS_PATTERN.matcher(sqlIdentifier.names.get(0));
          return matcher.find();
        }
      }
    }
    return false;
  }

  @Override
  protected SqlCall transform(SqlCall sqlCall) {
    SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlCall.getOperandList().get(0);
    String[] names = sqlIdentifier.names.get(0).split("\\[");
    String newName = String.format(ELEMENT_AT, names[0], names[1].substring(0, names[1].length() - 1));
    sqlIdentifier.names = ImmutableList.of(newName, sqlIdentifier.names.get(1));
    return sqlCall;
  }
}
